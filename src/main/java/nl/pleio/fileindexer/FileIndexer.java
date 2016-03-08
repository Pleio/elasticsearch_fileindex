package nl.pleio.fileindexer;

import nl.next2know.parsing.DocumentParser;
import nl.next2know.parsing.TikaDocumentParser;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import nl.pleio.fileindexer.config.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileIndexer
{
    protected FileIndexerConfig config;

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
     * Create FileIndexer instance and run it.
     */
    public static void startDaemon() {

    }

    /**
     * Main entry point
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        FileIndexer indexer = new FileIndexer();
        String configPath = "config.yml";
        indexer.loadConfig(configPath);

        while (true) {
            try {
                Thread.sleep(1000);
                System.out.println("Still running...");
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }


//        DocumentParser docParser = new TikaDocumentParser();
//        docParser.parse()

    }
}
