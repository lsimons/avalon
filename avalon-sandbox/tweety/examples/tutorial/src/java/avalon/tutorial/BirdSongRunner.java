package avalon.tutorial;

import avalon.tutorial.BirdSongImpl;

public class BirdSongRunner implements Runnable
{
	BirdSongImpl m_bs;

	public BirdSongRunner( BirdSongImpl bs )
	{
		m_bs = bs;
	}

	public void run()
	{
		int max = m_bs.getNumberOfChilps();
		String msg = m_bs.getChilpMessage();
		String seperator = m_bs.getChilpSeperator();

		for( int i = 0; i != max; i++ )
		{
			if( i != 0 && i != (max-1) )
				System.out.print( seperator );

			System.out.print(msg );
		}
	}
}

