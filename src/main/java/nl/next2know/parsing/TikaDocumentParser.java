package nl.next2know.parsing;

import java.io.IOException;  
import java.io.InputStream; 

import org.apache.tika.exception.TikaException;  
import org.apache.tika.metadata.Metadata;  
import org.apache.tika.parser.AutoDetectParser;  
import org.apache.tika.parser.ParseContext;  
import org.apache.tika.parser.Parser;  
import org.apache.tika.sax.BodyContentHandler;  
import org.xml.sax.ContentHandler;  
import org.xml.sax.SAXException;  

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class TikaDocumentParser implements DocumentParser
{
	/**
	 *
	 * @param input  The input stream the parser will read from
	 *
	 * @return
	 * @throws ParserException
     */
	public ParserResult parse(InputStream input) throws ParserException
	{
		ParserResult result = new ParserResult();
		
		Parser parser = new AutoDetectParser();
        ContentHandler contentHandler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();
        
        try
        {
        	parser.parse(input, contentHandler, metadata, new ParseContext());
        	
        	// Handle the parsed metadata and assign it to the ParserResult object
        	for (String key : metadata.names())
        	{
        		String value = metadata.get(key);
        		result.setMetaValue(key, value);
        	}
        	
        	// Assign the body text content.
        	result.setBodyContent(contentHandler.toString());
        }
        catch (TikaException e)
        {
        	throw new ParserException(e.getMessage(), e);
        }
        catch (IOException e)
        {
        	throw new ParserException(e.getMessage(), e);
        }
        catch (SAXException e)
        {
        	throw new ParserException(e.getMessage(), e);
        }
        
        return result;
	}

	/**
	 *
	 * @param input  The input stream the parser will read from
	 * @param parseContent  Determines if the parser should parse the body content
	 * @param parseMetaData  Determines if the parser should parse the document's metadata
     *
     * @return
     */
	public ParserResult parse(InputStream input, boolean parseContent, boolean parseMetaData)
	{
		throw new NotImplementedException();
	}
}
