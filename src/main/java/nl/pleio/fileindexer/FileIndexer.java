package nl.pleio.fileindexer;

import nl.next2know.parsing.DocumentParser;
import nl.next2know.parsing.TikaDocumentParser;
import nl.pleio.fileindexer.queue.QueueException;
import nl.pleio.fileindexer.queue.QueueMessage;
import nl.pleio.fileindexer.queue.QueueReaderService;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import nl.pleio.fileindexer.config.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeoutException;

public class FileIndexer
{
    protected FileIndexerConfig config;
    protected QueueReaderService queueReader;

    public FileIndexer() {
    }

    public FileIndexer(String configPath) throws IOException {
        this.loadConfig(configPath);
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
     * @param configPath
     * @throws IOException
     */
    protected FileIndexerConfig loadConfig(String configPath) throws IOException
    {
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
     * Connects to RabbitMQ and sets up a channel.
     * @return QueueReaderService
     * @throws IOException
     * @throws TimeoutException
     */
    protected QueueReaderService connectToRabbit() throws IOException, TimeoutException {
        if (this.queueReader == null) {
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

    public void processMessagesInQueue() {

        try {
            this.connectToRabbit();

            System.out.println("[Processing messages in queue]");
            QueueMessage message;
            do {
                message = this.getQueueReader().getNextMessage();
                if (message != null) {
                    System.out.println(message.id + " : " + message.filePath);
                }
                Thread.sleep(2000);
            }
            while (message != null);

            System.out.println("[No more messages in queue]");

            Thread.sleep(this.config.pollinginterval * 1000);
        }
        catch (IOException e) {
            // @todo Log exception
            e.printStackTrace();
        }
        catch (TimeoutException e) {
            // @todo Log exception
            e.printStackTrace();
        }
        catch (QueueException e) {
            // @todo Log exception
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            // @todo Log exception
            e.printStackTrace();
        }
    }

    /**
     * Main entry point
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String configPath = "config.yml";
        //String configPath = "./src/main/resources/config.yml";
        FileIndexer indexer = new FileIndexer(configPath);

        while (true) {
            indexer.processMessagesInQueue();
        }
    }
}
