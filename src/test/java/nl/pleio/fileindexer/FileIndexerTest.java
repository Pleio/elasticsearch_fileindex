package nl.pleio.fileindexer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import nl.pleio.fileindexer.config.FileIndexerConfig;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by ruben on 3/4/16.
 */
public class FileIndexerTest
{
    @Test
    public void testLoadConfig() {
        String configPath = "src/test/_files/fileindexer/testconfig.yml";

        FileIndexer indexer = new FileIndexer();

        try {
            FileIndexerConfig result = indexer.loadConfig(configPath);

            String expectedVersion = "1.0";
            assertEquals(expectedVersion, result.version);

            String expectedRabbitHost = "localhost";
            assertEquals(expectedRabbitHost, result.rabbit.connection.host);

            String expectedRabbitQueue = "file_queue";
            assertEquals(expectedRabbitQueue, result.rabbit.queue);

            String expectedRabbitVhost = "pleio";
            assertEquals(expectedRabbitVhost, result.rabbit.connection.vhost);

            String expectedElasticHost = "localhost";
            assertEquals(expectedElasticHost, result.elasticsearch.host);

            String expectedElasticIndex = "pleio";
            assertEquals(expectedElasticIndex, result.elasticsearch.index);
        }
        catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
