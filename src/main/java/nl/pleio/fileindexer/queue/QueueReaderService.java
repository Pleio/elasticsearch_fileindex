package nl.pleio.fileindexer.queue;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.*;
import com.google.gson.*;

public class QueueReaderService
{
    private final int DEFAULT_DELIVERY_TIMEOUT = 3000;

    /**
     * The RabbitMQ connection object
     */
    protected Connection connection;

    /**
     * The RabbitMQ channel
     */
    protected Channel channel;

    /**
     * The RabbitMQ consumer object
     */
    protected QueueingConsumer consumer;

    /**
     * The Gson object to help us process JSON packets
     */
    protected Gson gson;

    /**
     * The name of the queue we are working with
     */
    protected String queueName;

    /**
     * The number of messages in the queue
     */
    protected int messageCount;

    /**
     *
     */
    protected AMQP.Queue.DeclareOk dok;

    /**
     * The time in miliseconds to wait for the next delivery.
     * If no new messages arrive within this timespan we bail out.
     */
    protected int deliveryTimeout = DEFAULT_DELIVERY_TIMEOUT;

    /**
     * Default constructor
     */
    public QueueReaderService()
    {
    }

    /**
     * @return
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * @param connection
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * @return
     */
    public int getDeliveryTimeout()
    {
        return this.deliveryTimeout;
    }

    /**
     * @param deliveryTimeout
     * @return
     */
    public void setDeliveryTimeout(int deliveryTimeout)
    {
        this.deliveryTimeout = deliveryTimeout;
    }

    /**
     * @return
     */
    public AMQP.Queue.DeclareOk getDok()
    {
        return dok;
    }

    /**
     * @param dok
     */
    public void setDok(AMQP.Queue.DeclareOk dok)
    {
        this.dok = dok;
    }

    /**
     * @return
     */
    public int getMessageCount() throws IOException
    {
        return this.messageCount;
    }

    /**
     * @param messageCount
     */
    public void setMessageCount(int messageCount)
    {
        this.messageCount = messageCount;
    }

    /**
     * Connect to RabbitMQ
     *
     * @param hostname
     * @param username
     * @param password
     * @param vhost
     * @param queueName
     *
     * @throws IOException
     */
    public void connect(String hostname, String username, String password, String vhost, String queueName) throws java.io.IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(hostname);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost(vhost);
        this.connection = factory.newConnection();
        this.channel = connection.createChannel();
        this.channel.basicQos(1); // Get messages one by one

        this.queueName = queueName;
        AMQP.Queue.DeclareOk dok = this.channel.queueDeclare(queueName, true, false, false, null);
        this.setDok(dok);
        this.setMessageCount(dok.getMessageCount());

        this.consumer = new QueueingConsumer(channel);
    }

    /**
     * Disconnect from RabbitMQ
     *
     * @throws IOException
     */
    public void disconnect() throws IOException, TimeoutException {
        if (this.channel != null)
        {
            this.channel.close();
        }

        if (this.connection != null)
        {
            this.connection.close();
        }
    }

    /**
     *
     * @return The QueueMessage object containing the message
     * @throws QueueException
     */
    public QueueMessage getNextMessage() throws QueueException
    {
        if (this.connection == null)
        {
            throw new QueueException("You need to connect to RabbitMQ first by calling the connect() method of this class.");
        }

        try
        {
            boolean autoAck = false;
            this.channel.basicConsume(this.queueName, autoAck, this.consumer);

            QueueingConsumer.Delivery delivery = this.consumer.nextDelivery(this.getDeliveryTimeout());

            if (delivery == null)
            {
                return null;
            }

            String message = new String(delivery.getBody());

            // Send ACK to Rabbit
            this.channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

            if (this.gson == null)
            {
                this.gson = new GsonBuilder().registerTypeAdapter(QueueMessage.class, new QueueMessageDeserializer()).create();
            }

            QueueMessage queueMessage = this.gson.fromJson(message, QueueMessage.class);

            return queueMessage;
        }
        catch (IOException | ShutdownSignalException | ConsumerCancelledException | InterruptedException e)
        {
            throw new QueueException("Error communicating with RabbitMQ.", e);
        }
    }

    public static class QueueMessageDeserializer implements JsonDeserializer<QueueMessage> {

        @Override
        public QueueMessage deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
                throws JsonParseException {

            final QueueMessage queueMessage = new QueueMessage();
            final JsonObject rootObject = json.getAsJsonObject();

            // Object id
            queueMessage.id = rootObject.get("id").getAsString();

            // File path
            queueMessage.filePath = rootObject.get("file_path").getAsString();

            return queueMessage;
        }
    }
}
