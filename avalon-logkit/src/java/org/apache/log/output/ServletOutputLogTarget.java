package org.apache.log.output;

import javax.servlet.ServletContext;
import org.apache.log.output.DefaultOutputLogTarget;

/**
 * Generic logging interface. Implementations are based on the strategy
 * pattern.
 * @author <a href="mailto:Tommy.Santoso@osa.de">Tommy Santoso</a>
 */
public class ServletOutputLogTarget
    extends DefaultOutputLogTarget
{
    private ServletContext context = null;

    /**
     * Constructor.
     *
     * @param context ServletContext to use for logging.
     */
    public ServletOutputLogTarget( final ServletContext context )
    {
        this.context = context;
    }

    /**
     * Logs message to servlet context log file
     *
     * @param message message to log to servlet context log file.
     */
    protected void output( final String message )
    {
        if( null != context )
        {
            synchronized( this )
            {
                context.log( message );
            }
        }
    }
}
