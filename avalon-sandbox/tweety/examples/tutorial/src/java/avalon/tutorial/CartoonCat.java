package avalon.tutorial;

public class CartoonCat
{
	private String m_name;
	private String m_quote;
	
	public CartoonCat( String name, String quote )
	{
		m_name = name;
		m_quote = quote;
	}
	
	public void setName( String name )
	{
		m_name = name;
	}
	public String getName()
	{
		return m_name;
	}
	public void setQuote( String quote )
	{
		m_quote = quote;
	}
	public String getQuote()
	{
		return m_quote;
	}
}

