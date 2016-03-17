package nl.pleio.fileindexer.searchengine;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by ruben on 3/14/16.
 * Interface for the File Indexer's search engine client to allow for easier porting to different types of
 * search engines.
 */
public interface SearchEngineClientInterface
{
    void connect();

    void disconnect();

    String findDocument(String documentId, String type);

    void updateDocument(String documentId, String type, HashMap<String, Object> updateData) throws IOException, ExecutionException, InterruptedException;

    void updateField(String documentId, String type, String fieldName, Object fieldData) throws IOException, ExecutionException, InterruptedException;
}