package org.apache.log.output;

import javax.servlet.ServletContext;
import org.apache.log.output.DefaultOutputLogTarget;
import org.apache.log.Priority;
import org.apache.log.LogEntry;

/**
 * Generic logging interface. Implementations are based on the strategy
 * pattern.
 * @author <a href="mailto:Tommy.Santoso@osa.de">Tommy Santoso</a>
 */
public class ServletOutputLogTarget
    extends DefaultOutputLogTarget
{
    private ServletContext context = null;
    private Priority.Enum minimum = null;

    /**
     * Constructor.
     *
     * @param context ServletContext to use for logging.
     */
    public ServletOutputLogTarget( final ServletContext context )
    {
        this(context, Priority.ERROR);
    }

    /**
     * Constructor.
     *
     * @param context ServletContext to use for logging.
     */
    public ServletOutputLogTarget( final ServletContext context, Priority.Enum priority )
    {
        this.context = context;
        this.minimum = priority;
    }

    /**
     * Process a log entry, via formatting and outputting it.
     *
     * @param entry the log entry
     */
    public void processEntry( final LogEntry entry )
    {
        if (this.minimum.isLowerOrEqual(entry.getPriority())) {
            super.processEntry(entry);
        }
    }

    /**
     * Logs message to servlet context log file
     *
     * @param message message to log to servlet context log file.
     */
    protected void output( final String message )
    {
        if( null != this.context )
        {
            synchronized( this.context )
            {
                context.log( message );
            }
        }
    }
}
