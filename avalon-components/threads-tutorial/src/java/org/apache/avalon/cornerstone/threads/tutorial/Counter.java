package org.apache.avalon.cornerstone.threads.tutorial;

import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;

/**
 * A demonstration runnable object that simply logs a countdown sequence.
 *
 * @author Stephen McConnell
 * @avalon.component name="counter" 
 */
public class Counter extends Thread implements LogEnabled
{
   /**
    * The supplied logging channel.
    */
    private Logger m_logger;

    private int m_count = 10;

    public void enableLogging( Logger logger )
    {
        m_logger = logger;
    }

    protected Logger getLogger()
    {
        return m_logger;
    }

    public void run()
    {
        while( m_count > 0 )
        {
            getLogger().info( "count: " + m_count );
            m_count--;
            try
            {
                sleep( 1000 );
            }
            catch( Throwable e )
            {
                getLogger().info( "I've been interrupted." );
                m_count = -1;
            }
        }
        getLogger().info( "Time to die." );
        m_logger = null;
    }
}

