package nl.next2know.parsing;

import java.util.HashMap;

/**
 * Class to hold the result of a DocumentParser.
 * 
 * @author ruben
 */
public class ParserResult
{
	protected String bodyContent;
	protected HashMap<String, String> metaData;
	
	public ParserResult()
	{
		this.metaData = new HashMap<String, String>();
	}
	
	/**
	 * @return  The content of the parsed document.
	 */
	public String getBodyContent()
	{
		return this.bodyContent;
	}
	
	/**
	 * @param bodyContent
	 */
	public void setBodyContent(String bodyContent)
	{
		this.bodyContent = bodyContent;
	}
	
	/**
	 * @return  The metadata of the parsed document.
	 */
	public HashMap<String, String> getMetaData()
	{
		return this.metaData;
	}
	
	/**
	 * @param metaData  The metadata of the parsed document.
	 */
	public void setMetaData(HashMap<String, String> metaData)
	{
		this.metaData = metaData;
	}

	/**
	 * Gets the metadata entry with the specified key or null when the key does not exist.
	 * 
	 * @param key
	 * @return
	 */
	public String getMetaValue(String key)
	{
		return this.metaData.get(key);
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void setMetaValue(String key, String value)
	{
		this.metaData.put(key, value);
	}
}
