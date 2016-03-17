package nl.pleio.fileindexer.searchengine;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Implementation of SearchEngineClientInterface for Elastic Search 1.x.
 */
public class ElasticSearchClient extends SearchEngineClient
{
    protected Client elasticClient;
    protected String host;
    protected int port;
    protected String cluster;
    protected String index;

    /**
     * @param connectionSettings - Needs the following settings:
     *                           "host" - the hostname of the ElasticSearch server
     *                           "port" - the port number to connect with
     *                           "cluster" - [optional] name of the cluster, default "elasticsearch"
     *                           "index" - [optional] default index to use in future calls
     */
    public ElasticSearchClient(HashMap<String, String> connectionSettings) {
        super(connectionSettings);

        if (!connectionSettings.containsKey("host") || !connectionSettings.containsKey("port")) {
            throw new IllegalArgumentException("ElasticSearchClient connectionSettings require at least a 'host' and 'port' setting");
        }

        // Parse settings in connectionSettings
        for (String key : connectionSettings.keySet()) {
           switch (key.toLowerCase()) {
               case "host":
                   this.host = connectionSettings.get(key);
                   break;

               case "port":
                   this.port = Integer.parseInt(connectionSettings.get(key));
                   break;

               case "cluster":
                   this.cluster = connectionSettings.get(key);
                   break;

               case "index":
                   this.index = connectionSettings.get(key);
                   break;

               default:
                   // Ignore
                   break;
           }
        }

        if (this.cluster == null) {
            this.cluster = "elasticsearch"; // default to 'elasticsearch'
        }
    }

    /**
     * Connect to Elastic Search host.
     */
    public void connect() {
        this.disconnect();

        Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", this.cluster).build();
        this.elasticClient = new TransportClient(settings).addTransportAddresses(
                new InetSocketTransportAddress(this.host, this.port)
        );
    }

    /**
     * Disconnect from Elastic Search host.
     */
    public void disconnect() {
        if (this.elasticClient != null) {
            this.elasticClient.close();
        }
    }

    /**
     * Find a document by id and type.
     *
     * @param documentId
     * @param type
     * @return
     */
    public String findDocument(String documentId, String type) {
        GetResponse response = this.elasticClient.prepareGet(this.index, type, documentId)
                .execute()
                .actionGet();

        return response.getSourceAsString();
    }

    /**
     * Update the document with the data in the updateData hashmap. The hashmap should use the fieldname for key
     * and the new field content for the value.
     *
     * @param documentId
     * @param updateData
     */
    public void updateDocument(String documentId, String type, HashMap<String, Object> updateData) throws IOException, ExecutionException, InterruptedException {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(this.index);
        updateRequest.type(type);
        updateRequest.id(documentId);

        XContentBuilder builder = jsonBuilder().startObject();
        for (String fieldName : updateData.keySet()) {
            builder.field(fieldName, updateData.get(fieldName));
        }
        builder.endObject();

        updateRequest.doc(builder);
        this.elasticClient.update(updateRequest).get();
    }

    /**
     * Update the specified document field.
     * Example usage: client.updateField("888", "object", "full_text", "This is my extracted full text data");
     *
     * @param documentId
     * @param type
     * @param fieldName
     * @param fieldData
     */
    public void updateField(String documentId, String type, String fieldName, Object fieldData) throws IOException, ExecutionException, InterruptedException {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(this.index);
        updateRequest.type(type);
        updateRequest.id(documentId);

        updateRequest.doc(jsonBuilder()
                .startObject()
                    .field(fieldName, fieldData)
                .endObject());
        this.elasticClient.update(updateRequest).get();
    }
}
