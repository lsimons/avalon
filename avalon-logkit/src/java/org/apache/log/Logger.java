/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log;

/**
 * The object interacted with by client objects to perform logging.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class Logger
{
    protected final static long                START_TIME = System.currentTimeMillis();

    protected final Logger                     m_parent;
    protected final Category                   m_category;
    protected LogTarget[]                      m_logTargets;

    /**
     * Constructor taking a category.
     *
     * @param category the category
     */
    public Logger( final Category category )
    {
        this( category, null, null );
    }

    /**
     * Constructor taking category and log targets
     *
     * @param category the category
     * @param logTargets the targets
     */
    public Logger( final Category category, final LogTarget[] logTargets )
    {
        this( category, logTargets, null );
    }

    public Logger( final Category category, final LogTarget[] logTargets, final Logger parent )
    {
        m_category = category;
        m_logTargets = logTargets;
        m_parent = parent;
    }

    public Logger( final Category category, final Logger parent )
    {
        this( category, null, parent );
    }

    /**
     * Log a debug priority entry.
     *
     * @param message the message
     * @param throwable the throwable
     */
    public final void debug( final String message, final Throwable throwable )
    {
        if( m_category.getPriority().isLowerOrEqual( Priority.DEBUG ) &&
            LogKit.getGlobalPriority().isLowerOrEqual( Priority.DEBUG ) )
        {
            output( Priority.DEBUG, message, throwable );
        }
    }

    /**
     * Log a debug priority entry.
     *
     * @param message the message
     */
    public final void debug( final String message )
    {
        if( m_category.getPriority().isLowerOrEqual( Priority.DEBUG ) &&
            LogKit.getGlobalPriority().isLowerOrEqual( Priority.DEBUG ) )
        {
            output( Priority.DEBUG, message, null );
        }
    }

    /**
     * Log a error priority entry.
     *
     * @param message the message
     * @param throwable the throwable
     */
    public final void error( final String message, final Throwable throwable )
    {
        if( m_category.getPriority().isLowerOrEqual( Priority.ERROR ) &&
            LogKit.getGlobalPriority().isLowerOrEqual( Priority.ERROR ) )
        {
            output( Priority.ERROR, message, throwable );
        }
    }

    /**
     * Log a error priority entry.
     *
     * @param message the message
     */
    public final void error( final String message )
    {
        if( m_category.getPriority().isLowerOrEqual( Priority.ERROR ) &&
            LogKit.getGlobalPriority().isLowerOrEqual( Priority.ERROR ) )
        {
            output( Priority.ERROR, message, null );
        }
    }

    /**
     * Log a fatalError priority entry.
     *
     * @param message the message
     * @param throwable the throwable
     */
    public final void fatalError( final String message, final Throwable throwable )
    {
        if( m_category.getPriority().isLowerOrEqual( Priority.FATAL_ERROR ) &&
            LogKit.getGlobalPriority().isLowerOrEqual( Priority.FATAL_ERROR ) )
        {
            output( Priority.FATAL_ERROR, message, throwable );
        }
    }

    /**
     * Log a fatalError priority entry.
     *
     * @param message the message
     */
    public final void fatalError( final String message )
    {
        if( m_category.getPriority().isLowerOrEqual( Priority.FATAL_ERROR ) &&
            LogKit.getGlobalPriority().isLowerOrEqual( Priority.FATAL_ERROR ) )
        {
            output( Priority.FATAL_ERROR, message, null );
        }
    }

    /**
     * Retrieve category associated with logger.
     *
     * @return the Category
     */
    public final Category getCategory()
    {
        return m_category;
    }


    /**
     * Log a info priority entry.
     *
     * @param message the message
     */
    public final void info( final String message )
    {
        if( m_category.getPriority().isLowerOrEqual( Priority.INFO ) &&
            LogKit.getGlobalPriority().isLowerOrEqual( Priority.INFO ) )
        {
            output( Priority.INFO, message, null );
        }
    }

    /**
     * Log a entry at specific priority with a certain message and throwable.
     *
     * @param message the message
     * @param priority the priority
     * @param throwable the throwable
     */
    public final void log( final Priority.Enum priority,
                           final String message,
                           final Throwable throwable )
    {
        if( m_category.getPriority().isLowerOrEqual( priority ) &&
            LogKit.getGlobalPriority().isLowerOrEqual( priority ) )
        {
            output( priority, message, throwable );
        }
    }

    /**
     * Log a entry at specific priority with a certain message.
     *
     * @param message the message
     * @param priority the priority
     */
    public final void log( final Priority.Enum priority, final String message )
    {
        if( m_category.getPriority().isLowerOrEqual( priority ) &&
            LogKit.getGlobalPriority().isLowerOrEqual( priority ) )
        {
            output( priority, message, null );
        }
    }

    /**
     * Internal method to do actual outputting.
     *
     * @param priority the priority
     * @param message the message
     * @param throwable the throwable
     */
    private final void output( final Priority.Enum priority,
                               final String message,
                               final Throwable throwable )
    {
        final LogEntry entry = new LogEntry();
        entry.setCategory( m_category );
        entry.setContextStack( LogKit.getCurrentContext() );

        if( null != message ) 
        {
            entry.setMessage( message );
        }
        else
        {
            entry.setMessage( "" );
        }

        entry.setThrowable( throwable );
        entry.setPriority( priority );

        //this next line can kill performance. It may be wise to
        //disable it sometimes and use a more granular approach
        entry.setTime( System.currentTimeMillis() - START_TIME );

        output( entry );
    }

    protected final void output( final LogEntry entry )
    {
        final LogTarget[] targets = m_logTargets;

        if( null == targets )
        {
            if( null != m_parent )
            {
                m_parent.output( entry );
            }
            else
            {
                LogKit.getDefaultLogTarget().processEntry( entry );
            }
        }
        else
        {
            for( int i = 0; i < targets.length; i++ )
            {
                //No need to clone as addition of a log-target 
                //will result in changin whole array                
                targets[ i ].processEntry( entry );
            }
        }
    }

    /**
     * Log a warn priority entry.
     *
     * @param message the message
     * @param throwable the throwable
     */
    public final void warn( final String message, final Throwable throwable )
    {
        if( m_category.getPriority().isLowerOrEqual( Priority.WARN ) &&
            LogKit.getGlobalPriority().isLowerOrEqual( Priority.WARN ) )
        {
            output( Priority.WARN, message, throwable );
        }
    }

    /**
     * Log a warn priority entry.
     *
     * @param message the message
     */
    public final void warn( final String message )
    {
        if( m_category.getPriority().isLowerOrEqual( Priority.WARN ) &&
            LogKit.getGlobalPriority().isLowerOrEqual( Priority.WARN ) )
        {
            output( Priority.WARN, message, null );
        }
    }

    /**
     * Set the log targets for this logger.
     *
     * @param logTargets the Log Targets
     */
    public void setLogTargets( final LogTarget[] logTargets )
    {
        m_logTargets = logTargets;
    }

    public final LogTarget[] getLogTargets()
    {
        return m_logTargets;
    }

    public final void addLogTarget( final LogTarget target )
    {
        if( null == m_logTargets ) m_logTargets = new LogTarget[] { target };
        else
        {
            final LogTarget[] targets = new LogTarget[ m_logTargets.length + 1 ];
            System.arraycopy( m_logTargets, 0, targets, 0, m_logTargets.length );
            targets[ m_logTargets.length ] = target;
            m_logTargets = targets;
        }
    }

    public Logger getChildLogger( final String subcategory )
    {
        final String categoryName = 
            m_category.getName() + Category.SEPARATOR + subcategory;
        return LogKit.getLoggerFor( categoryName );
    }
}
