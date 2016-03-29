package nl.pleio.fileindexer;

import nl.next2know.parsing.DocumentParser;
import nl.next2know.parsing.ParserException;
import nl.next2know.parsing.ParserResult;
import nl.next2know.parsing.TikaDocumentParser;
import nl.pleio.fileindexer.queue.QueueException;
import nl.pleio.fileindexer.queue.QueueMessage;
import nl.pleio.fileindexer.queue.QueueReaderService;

import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import nl.pleio.fileindexer.searchengine.*;
import nl.pleio.fileindexer.config.*;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class FileIndexer
{
    protected FileIndexerConfig config;
    protected QueueReaderService queueReader;
    protected SearchEngineClientInterface searchClient;
    protected Logger logger;

    public FileIndexer() {
    }

    public FileIndexer(String configPath) throws IOException {
        this.loadConfig(configPath);
        this.setupLog();
    }

    public QueueReaderService getQueueReader() {
        return queueReader;
    }

    public void setQueueReader(QueueReaderService queueReader) {
        this.queueReader = queueReader;
    }

    /**
     * Load the config YAML file and parse it into a FileIndexerConfig object.
     *
     * @param configPath Path to the main config file
     * @throws IOException
     */
    protected FileIndexerConfig loadConfig(String configPath) throws IOException {
        Constructor constructor = new Constructor(FileIndexerConfig.class);
        TypeDescription fileIndexerConfigDesc = new TypeDescription(FileIndexerConfig.class);
        fileIndexerConfigDesc.putMapPropertyType("rabbit", RabbitMQConfig.class, Object.class);
        constructor.addTypeDescription(fileIndexerConfigDesc);

        Yaml yamlReader = new Yaml(constructor);
        InputStream configStream = new FileInputStream(new File(configPath));

        this.config = (FileIndexerConfig)yamlReader.load(configStream);

        configStream.close();

        return this.config;
    }

    /**
     * Sets up Log4J to use the config file specified in our main config.yml
     */
    protected void setupLog() {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        File logConfig = new File(this.config.log);
        context.setConfigLocation(logConfig.toURI());
        this.logger = LogManager.getLogger(FileIndexer.class.getName());
    }

    /**
     * Connects to RabbitMQ and sets up a channel.
     * @return QueueReaderService
     * @throws IOException
     * @throws TimeoutException
     */
    protected QueueReaderService connectToRabbit() throws IOException, TimeoutException {
        if (this.queueReader == null || this.queueReader.getConnection() == null) {
            this.queueReader = new QueueReaderService();
            this.queueReader.connect(
                this.config.rabbit.connection.host,
                this.config.rabbit.connection.username,
                this.config.rabbit.connection.password,
                this.config.rabbit.connection.vhost,
                this.config.rabbit.queue
            );
        }

        return this.queueReader;
    }

    /**
     *
     */
    protected void disconnectFromRabbit() {
        try {
            this.queueReader.disconnect();
        }
        catch (IOException e) {
            this.logger.error(e.getMessage());
        }
        catch (TimeoutException e) {
            this.logger.error(e.getMessage());
        }

        this.queueReader = null;
    }

    protected SearchEngineClientInterface initSearchConnection() throws UnknownHostException {
        if (this.searchClient != null) {
            this.searchClient.disconnect();
        }

        HashMap<String, String> elasticSettings = new HashMap<String, String>();
        elasticSettings.put("host", this.config.elasticsearch.host);
        elasticSettings.put("port", Integer.toString(this.config.elasticsearch.port));
        elasticSettings.put("cluster", this.config.elasticsearch.cluster);
        elasticSettings.put("index", this.config.elasticsearch.index);
        this.searchClient = new ElasticSearchClient(elasticSettings);
        this.searchClient.connect();

        return this.searchClient;
    }

    /**
     * Process messages in the queue until there are no more messages left.
     * Return false on an abnormal event so the main method can abort the service.
     *
     * @return false on error, true on success
     */
    public boolean processMessagesInQueue() {

        try {
            this.connectToRabbit();
            this.logger.info("[Processing messages in queue]");

            DocumentParser documentParser = new TikaDocumentParser();

            QueueMessage message;

            this.initSearchConnection();

            HashMap<String,Object> updateData;
            do {
                message = this.getQueueReader().getNextMessage();
                if (message != null) {
                    this.logger.info(message.id + " : " + message.filePath);

                    try {
                        // Process the message
                        InputStream inputFile = new FileInputStream(message.filePath);

                        ParserResult result = documentParser.parse(inputFile);

                        updateData = new HashMap<String,Object>();
                        updateData.put("full_text", result.getBodyContent());
                        updateData.put("needs_file_parsing", Boolean.valueOf(false));
                        this.searchClient.updateDocument(message.id, "object", updateData);
                    }
                    catch (FileNotFoundException e) {
                        this.logger.error(e.getMessage());
                    }
                    catch (ParserException e) {
                        this.logger.error(e.getMessage());
                    }
                    catch (IOException e) {
                        this.logger.error(e.getMessage());
                    }
                    catch (ExecutionException e) {
                        this.logger.error(e.getMessage());
                    }
                }
            }
            while (message != null);

            this.logger.info("[No more messages in queue]");

            this.searchClient.disconnect();

            this.disconnectFromRabbit();

            Thread.sleep(this.config.pollinginterval * 1000);

            return true;
        }
        catch (IOException e) {
            this.logger.error(e.getMessage());
            return false;
        }
        catch (TimeoutException e) {
            this.logger.error(e.getMessage());
            return false;
        }
        catch (QueueException e) {
            this.logger.error(e.getMessage());
            return false;
        }
        catch (InterruptedException e) {
            this.logger.error(e.getMessage());
            return false;
        }
    }

    /**
     * Main entry point
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String configPath = "config.yml";

        if (args.length >= 2) {
            switch (args[0]) {
                case "-c":
                case "--config":
                    configPath = args[1];
                    break;
                default:
                    break;
            }
        }

        System.out.println("Using config path: " + configPath);

        FileIndexer indexer = new FileIndexer(configPath);

        boolean runningNormal = true;
        while (true && runningNormal) {
            runningNormal = indexer.processMessagesInQueue();
        }
    }
}
