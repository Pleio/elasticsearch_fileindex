package nl.next2know.parsing;

import java.io.InputStream;

/**
 * Interface describing a DocumentParser object's capabilities.
 * 
 * @author ruben
 *
 */
public interface DocumentParser
{
	/**
	 * @param input  The input stream the parser will read from
	 * 
	 * @return The parsed output
	 */
	public ParserResult parse(InputStream input) throws ParserException;

	/**
	 * @param input  The input stream the parser will read from
	 * @param parseContent  Determines if the parser should parse the body content
	 * @param parseMetaData  Determines if the parser should parse the document's metadata
	 * 
	 * @return The parsed output
	 */
	public ParserResult parse(InputStream input, boolean parseContent, boolean parseMetaData) throws ParserException;
}
