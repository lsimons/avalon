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

    protected final LogEngine                  m_engine;
    protected final Logger                     m_parent;
    protected final Category                   m_category;
    protected LogTarget[]                      m_logTargets;

    /**
     * Constructor.
     *
     * @deprecated This method should not be called directly but instead use a LogEngine or LogKit
     */
    public Logger( final Category category )
    {
        this( LogKit.getDefaultLogEngine(), category, null, null );
    }

    /**
     * Constructor.
     *
     * @deprecated This method should not be called directly but instead use a LogEngine or LogKit
     */
    public Logger( final Category category, final LogTarget[] logTargets )
    {
        this( LogKit.getDefaultLogEngine(), category, logTargets, null );
    }

    /**
     * Constructor.
     *
     * @deprecated This method should not be called directly but instead use a LogEngine or LogKit
     */
    public Logger( final Category category, final Logger parent )
    {
        this( LogKit.getDefaultLogEngine(), category, null, parent );
    }

    /**
     * Constructor.
     *
     * @deprecated This method should not be called directly but instead use a LogEngine or LogKit
     */
    public Logger( final Category category, 
                   final LogTarget[] logTargets, 
                   final Logger parent )
    {
        this( LogKit.getDefaultLogEngine(), category, logTargets, parent );
    }

    /**
     * Constructor taking a category.
     *
     * @param category the category
     */
    protected Logger( final LogEngine engine, final Category category )
    {
        this( engine, category, null, null );
    }

    /**
     * Constructor taking category and log targets
     *
     * @param category the category
     * @param logTargets the targets
     */
    protected Logger( final LogEngine engine, final Category category, final LogTarget[] logTargets )
    {
        this( engine, category, logTargets, null );
    }

    protected Logger( final LogEngine engine, final Category category, final Logger parent )
    {
        this( engine, category, null, parent );
    }

    protected Logger( final LogEngine engine, 
                      final Category category, 
                      final LogTarget[] logTargets, 
                      final Logger parent )
    {
        m_engine = engine;
        m_category = category;
        m_logTargets = logTargets;
        m_parent = parent;
    }

    /**
     * Log a debug priority entry.
     *
     * @param message the message
     * @param throwable the throwable
     */
    public final void debug( final String message, final Throwable throwable )
    {
        if( isDebugEnabled() )
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
        if( isDebugEnabled() )
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
        if( isErrorEnabled() )
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
        if( isErrorEnabled() )
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
        if( isFatalErrorEnabled() )
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
        if( isFatalErrorEnabled() )
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
        if( isInfoEnabled() )
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
            m_engine.getGlobalPriority().isLowerOrEqual( priority ) )
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
            m_engine.getGlobalPriority().isLowerOrEqual( priority ) )
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
        //cache a copy of targets for thread safety
        //It is now possible for another thread
        //to replace m_logTargets
        final LogTarget[] targets = m_logTargets;

        if( null == targets )
        {
            if( null != m_parent )
            {
                m_parent.output( entry );
            }
            else
            {
                m_engine.getDefaultLogTarget().processEntry( entry );
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
        if( isWarnEnabled() )
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
        if( isWarnEnabled() )
        {
            output( Priority.WARN, message, null );
        }
    }

    /**
     * Determine if messages of priority DEBUG will be logged.
     *
     * @return true if DEBUG messages will be logged
     */
    public final boolean isDebugEnabled()
    {
        return
            ( m_category.getPriority().isLowerOrEqual( Priority.DEBUG ) &&
              m_engine.getGlobalPriority().isLowerOrEqual( Priority.DEBUG ) );
    }

    /**
     * Determine if messages of priority INFO will be logged.
     *
     * @return true if INFO messages will be logged
     */
    public final boolean isInfoEnabled()
    {
        return
            ( m_category.getPriority().isLowerOrEqual( Priority.INFO ) &&
              m_engine.getGlobalPriority().isLowerOrEqual( Priority.INFO ) );
    }

    /**
     * Determine if messages of priority WARN will be logged.
     *
     * @return true if WARN messages will be logged
     */
    public final boolean isWarnEnabled()
    {
        return
            ( m_category.getPriority().isLowerOrEqual( Priority.WARN ) &&
              m_engine.getGlobalPriority().isLowerOrEqual( Priority.WARN ) );
    }

    /**
     * Determine if messages of priority ERROR will be logged.
     *
     * @return true if ERROR messages will be logged
     */
    public final boolean isErrorEnabled()
    {
        return
            ( m_category.getPriority().isLowerOrEqual( Priority.ERROR ) &&
              m_engine.getGlobalPriority().isLowerOrEqual( Priority.ERROR ) );
    }

    /**
     * Determine if messages of priority FATAL_ERROR will be logged.
     *
     * @return true if FATAL_ERROR messages will be logged
     */
    public final boolean isFatalErrorEnabled()
    {
        return
            ( m_category.getPriority().isLowerOrEqual( Priority.FATAL_ERROR ) &&
              m_engine.getGlobalPriority().isLowerOrEqual( Priority.FATAL_ERROR ) );
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

    /**
     * Retrieve a list of log targets associated with this Logger.
     *
     * @return an array LogTargets
     */
    public final LogTarget[] getLogTargets()
    {
        return m_logTargets;
    }

    /**
     * Add an individual log target to logtarget list.
     *
     * @param target target to be added
     */
    public synchronized final void addLogTarget( final LogTarget target )
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

    /**
     * Create a new child logger.
     * The category of child logger is [current-category].subcategory
     *
     * @param subcategory the subcategory of this logger
     * @return the new logger
     */
    public Logger getChildLogger( final String subcategory )
    {
        final String categoryName = 
            m_category.getName() + Category.SEPARATOR + subcategory;
        return m_engine.getLoggerFor( categoryName );
    }
}
