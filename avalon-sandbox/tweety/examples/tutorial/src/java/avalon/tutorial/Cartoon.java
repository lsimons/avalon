package avalon.tutorial;

import java.util.ArrayList;

public class Cartoon
{
	private String m_name;
	
	private ArrayList m_birds;
	private ArrayList m_cats;
	private ArrayList m_cages;
	
	public CartoonBird( String name )
	{
		m_name = name;
		
		m_birds = new ArrayList();
		m_cats = new ArrayList();
		m_cages = new ArrayList();
	}
	
	public static void main( String[] args )
	{
		Cartoon c = new Cartoon( "AvalonBirdie vs StupidCat" );
		CartoonBird ab = new CartoonBird( "AvalonBirdie", "I thawt I saw a Stupid Cat!" );
		CartoonBird ob = new CartoonBird( "OtherBirdie", "I like the mountains, I like the rolling hills. I like the ..." );
		CartoonCat sc = new CartoonBird( "StupidCat", "EJBs to the victory!" );
		
		CartoonCage gc = new CartoonCage( "GoldenCage", "A shiney golden cage" );
		
		c.addBird( ab );
		c.addBird( ob );
		c.addCat( sc );
		c.addCage( gc );
		
	}
	
	public static void printIntro( Object o )
	{
		if( o instanceof Cartoon )
		{
			Cartoon c = (Cartoon)o;
			System.out.println( "Starting cartoon :" + c.getName() );
			return;
		}
		if( o instanceof CartoonBird )
		{
			CartoonBird c = (CartoonBird)b;
			System.out.println( "   Starring the really cool bird :" + c.getName() );
			return;
		}
		if( o instanceof CartoonCat )
		{
			CartoonCat c = (CartoonBird)c;
			System.out.println( "   Starring the really mean cat :" + c.getName() );
			return;
		}
		
		// got ourselves something else
		System.out.println( "   Containing the really interesting object :" + c.getName() );
	}
	
	public void setName( String name )
	{
		m_name = name;
	}
	public String getName()
	{
		return m_name;
	}
	
	public void addBird( CartoonBird bird )
	{
		m_birds.add( bird );
	}
	public void removeBird( CartoonBird bird )
	{
		int index = m_birds.indexOf(bird);
		if(index != -1)
			m_birds.remove( index );
	}
	public Iterator getBirds()
	{
		return (Collections.unmodifiableList(m_birds)).iterator();
	}

	public void addCat( CartoonCat cat )
	{
		m_cats.add( cat );
	}
	public void removeCat( CartoonCat cat )
	{
		int index = m_cats.indexOf(cat);
		if(index != -1)
			m_cats.remove( index );
	}
	public Iterator getCats()
	{
		return (Collections.unmodifiableList(m_cats)).iterator();
	}


	public void addCage( CartoonCage cage )
	{
		m_cages.add( cage );
	}
	public void removeCage( CartoonCage cage )
	{
		int index = m_cages.indexOf(cage);
		if(index != -1)
			m_cages.remove( index );
	}
	public Iterator getCages()
	{
		return (Collections.unmodifiableList(m_cages)).iterator();
	}
}

