package avalon.tutorial;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

import avalon.tutorial.BirdSong;
import avalon.tutorial.BirdSongRunner;

public class BirdSongImpl extends AbstractLogEnabled implements BirdSong
{
	private int m_numberOfChilps;
	private String m_chilpMessage;
	private String m_chilpSeparator;

	private Thread m_runnerThread;

	public BirdSongImpl()
	{
	}

	public void parameterize( Parameters parameters ) throws ParameterException
	{
		getLogger().debug( "got parameters" );

		m_numberOfChilps = parameters.getParameterAsInteger( PARAM_NUMBER_OF_CHILPS );
		m_chilpMessage = parameters.getParameter( PARAM_CHILP_MESSAGE );
		m_chilpSeparator = parameters.getParameter( PARAM_CHILP_SEPARATOR );
	}
	public void start()
	{
		getLogger().debug( "starting" );

		Runnable runnable = new BirdSongRunner( this );

		m_runnerThread = new Thread( runnable );
		m_runnerThread.setDaemon( true );
		m_runnerThread.run();
	}
	public void stop()
	{
		getLogger().debug( "stopping" );

		m_runnerThread.notify();
	}

	public int getNumberOfChilps()
	{
		return m_numberOfChilps;
	}
	public String getChilpMessage()
	{
		return m_chilpMessage;
	}
	public String getChilpSeperator()
	{
		return m_chilpSeparator;
	}
}

