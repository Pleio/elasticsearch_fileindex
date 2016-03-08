package nl.next2know.parsing;

public class ParserException extends Exception
{
	public ParserException(String message)
	{
		super(message);
	}
	
	public ParserException(String message, Exception innerException)
	{
		super(message, innerException);
	}
}
