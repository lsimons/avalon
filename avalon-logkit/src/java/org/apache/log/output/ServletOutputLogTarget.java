package org.apache.log.output;

import javax.servlet.ServletContext;
import org.apache.log.LogEvent;

/**
 * Generic logging interface. Implementations are based on the strategy
 * pattern.
 * @author <a href="mailto:Tommy.Santoso@osa.de">Tommy Santoso</a>
 */
public class ServletOutputLogTarget
    extends DefaultOutputLogTarget
{
    private ServletContext m_context;

    /**
     * Constructor.
     *
     * @param context ServletContext to use for logging.
     */
    public ServletOutputLogTarget( final ServletContext context )
    {
        m_context = context;
    }

    /**
     * Logs message to servlet context log file
     *
     * @param message message to log to servlet context log file.
     */
    protected void output( final String message )
    {
        if( null != m_context )
        {
            //Do we really need to synchronize on this?????????????
            synchronized( m_context )
            {
                m_context.log( message );
            }
        }
    }
}
