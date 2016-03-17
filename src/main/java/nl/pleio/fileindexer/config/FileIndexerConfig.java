package nl.pleio.fileindexer.config;

public class FileIndexerConfig
{
	public String version;
	public RabbitMQConfig rabbit;
	public ElasticSearchConfig elasticsearch;
	public long pollinginterval;
	public String log;
}
