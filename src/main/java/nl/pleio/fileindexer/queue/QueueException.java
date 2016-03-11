package nl.pleio.fileindexer.queue;

public class QueueException extends Exception
{
    public QueueException(String message)
    {
        super(message);
    }

    public QueueException(String message, Exception innerException)
    {
        super(message, innerException);
    }
}
