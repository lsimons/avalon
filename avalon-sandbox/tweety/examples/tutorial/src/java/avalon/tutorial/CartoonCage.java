package avalon.tutorial;

import java.util.ArrayList;
import java.util.Collections;

public class CartoonCage
{
	private String m_name;
	private String m_description;
	
	private ArrayList m_contents;
	
	public CartoonCage( String name, String description )
	{
		m_name = name;
		m_description = description;
		
		m_contents = new ArrayList();
	}
	
	public void setName( String name )
	{
		m_name = name;
	}
	public String getName()
	{
		return m_name;
	}
	public void setDescription( String description )
	{
		m_description = description;
	}
	public String getDescription()
	{
		return m_description;
	}
	
	public void addBird( CartoonBird bird )
	{
		m_contents.add( bird );
	}
	public void removeBird( CartoonBird bird )
	{
		int index = m_contents.indexOf(bird);
		if(index != -1)
			m_contents.remove( index );
	}
	public Iterator getBirds()
	{
		return (Collections.unmodifiableList(m_contents)).iterator();
	}
}

