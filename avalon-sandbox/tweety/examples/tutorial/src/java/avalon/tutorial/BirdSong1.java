package avalon.tutorial;

public class BirdSong1
{
	private int m_numberOfChilps;
	private String m_chilpMessage;
	private String m_chilpSeparator;

	public BirdSong1( int numberOfChilps, String chilpMessage, String chilpSeparator )
	{
		m_numberOfChilps = numberOfChilps;
		m_chilpMessage = chilpMessage;
		m_chilpSeparator = chilpSeparator;
	}

	public void sing()
	{
		for( int i = 0; i != m_numberOfChilps; i++ )
		{
			if( i != 0 && i != (m_numberOfChilps-1) )
				System.out.print( m_chilpSeparator );

			System.out.print( m_chilpMessage );
		}
	}

	public static void main( String args[] )
	{
		BirdSong1 birdSong = new BirdSong1( new Integer( args[0] ).intValue(),
				args[1], args[2] );

		birdSong.sing();
	}
}

