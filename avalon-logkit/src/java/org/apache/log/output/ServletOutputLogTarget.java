package org.apache.log.output;

import javax.servlet.ServletContext;
import org.apache.log.LogEvent;

/**
 * Generic logging interface. Implementations are based on the strategy
 * pattern.
 * @author <a href="mailto:Tommy.Santoso@osa.de">Tommy Santoso</a>
 */
public class ServletOutputLogTarget
    extends DefaultOutputLogTarget //will extend AbstractOutputTarget in future
{
    ///The servlet context written to (may be null in which case it won't log at all)
    private ServletContext m_context;

    /**
     * Constructor.
     *
     * @param context ServletContext to use for logging.
     */
    public ServletOutputLogTarget( final ServletContext context )
    {
        m_context = context;
        open();
    }

    /**
     * Logs message to servlet context log file
     *
     * @param message message to log to servlet context log file.
     */
    protected void write( final String message )
    {
        final ServletContext context = m_context;
        if( null != context )
        {
            synchronized( context )
            {
                context.log( message );
            }
        }
    }

    /**
     * Shutdown target.
     * Attempting to write to target after close() will cause errors to be logged.
     */
    public synchronized void close()
    {
        super.close();

        m_context = null;
    }
}
