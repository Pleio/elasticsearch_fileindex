package nl.pleio.fileindexer.searchengine;

import java.util.HashMap;

abstract class SearchEngineClient implements SearchEngineClientInterface
{
    protected HashMap<String, String> connectionSettings;

    public SearchEngineClient(HashMap<String, String> connectionSettings) {
        this.connectionSettings = connectionSettings;
    }
}
