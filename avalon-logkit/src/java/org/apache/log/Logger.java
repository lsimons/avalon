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
    public final static char    CATEGORY_SEPARATOR   = '.';

    private final static long   START_TIME           = System.currentTimeMillis();

    private final Hierarchy     m_hierarchy;
    private final Logger        m_parent;
    private final String        m_category;

    private Logger[]            m_children;

    private LogTarget[]         m_logTargets;
    private boolean             m_logTargetsForceSet;
    private Priority            m_priority;
    private boolean             m_priorityForceSet;

    protected Logger( final Hierarchy hierarchy, 
                      final String category, 
                      final LogTarget[] logTargets, 
                      final Logger parent )
    {
        m_hierarchy = hierarchy;
        m_category = category;
        m_logTargets = logTargets;
        m_parent = parent;

        if( null == m_logTargets )
        {
            unsetLogTargets();
        }

        unsetPriority();
    }

    /**
     * Log a debug priority event.
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
     * Log a debug priority event.
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
     * Log a error priority event.
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
     * Log a error priority event.
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
     * Log a fatalError priority event.
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
     * Log a fatalError priority event.
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

    public final Priority getPriority()
    {
        return m_priority;
    }

    /**
     * Retrieve category associated with logger.
     *
     * @return the Category
     */
    public final String getCategory()
    {
        return m_category;
    }

    /**
     * Log a info priority event.
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
     * Log a event at specific priority with a certain message and throwable.
     *
     * @param message the message
     * @param priority the priority
     * @param throwable the throwable
     */
    public final void log( final Priority priority,
                           final String message,
                           final Throwable throwable )
    {
        if( getPriority().isLowerOrEqual( priority ) )
        {
            output( priority, message, throwable );
        }
    }

    /**
     * Log a event at specific priority with a certain message.
     *
     * @param message the message
     * @param priority the priority
     */
    public final void log( final Priority priority, final String message )
    {
        log( priority, message, null );
    }

    /**
     * Internal method to do actual outputting.
     *
     * @param priority the priority
     * @param message the message
     * @param throwable the throwable
     */
    private final void output( final Priority priority,
                               final String message,
                               final Throwable throwable )
    {
        final LogEvent event = new LogEvent();
        event.setCategory( m_category );
        event.setContextStack( ContextStack.getCurrentContext() );

        if( null != message ) 
        {
            event.setMessage( message );
        }
        else
        {
            event.setMessage( "" );
        }

        event.setThrowable( throwable );
        event.setPriority( priority );

        //this next line can kill performance. It may be wise to
        //disable it sometimes and use a more granular approach
        event.setTime( System.currentTimeMillis() - START_TIME );

        output( event );
    }

    private final void output( final LogEvent event )
    {
        //cache a copy of targets for thread safety
        //It is now possible for another thread
        //to replace m_logTargets
        final LogTarget[] targets = m_logTargets;

        if( null == targets )
        {
            m_hierarchy.log( "LogTarget is null for category '" + m_category + "'" );
        }
        else
        {
            for( int i = 0; i < targets.length; i++ )
            {
                //No need to clone as addition of a log-target 
                //will result in changin whole array                
                targets[ i ].processEvent( event );
            }
        }
    }

    /**
     * Log a warn priority event.
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
     * Log a warn priority event.
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
        return getPriority().isLowerOrEqual( Priority.DEBUG );
    }

    /**
     * Determine if messages of priority INFO will be logged.
     *
     * @return true if INFO messages will be logged
     */
    public final boolean isInfoEnabled()
    {
        return getPriority().isLowerOrEqual( Priority.INFO );
    }

    /**
     * Determine if messages of priority WARN will be logged.
     *
     * @return true if WARN messages will be logged
     */
    public final boolean isWarnEnabled()
    {
        return getPriority().isLowerOrEqual( Priority.WARN );
    }

    /**
     * Determine if messages of priority ERROR will be logged.
     *
     * @return true if ERROR messages will be logged
     */
    public final boolean isErrorEnabled()
    {
        return getPriority().isLowerOrEqual( Priority.ERROR );
    }

    /**
     * Determine if messages of priority FATAL_ERROR will be logged.
     *
     * @return true if FATAL_ERROR messages will be logged
     */
    public final boolean isFatalErrorEnabled()
    {
        return getPriority().isLowerOrEqual( Priority.FATAL_ERROR );
    }

    /**
     * Set the priority for this logger.
     *
     * @param priority the priority
     */
    public synchronized void setPriority( final Priority priority )
    {
        m_priority = priority;
        m_priorityForceSet = true;
        resetChildPrioritys( false );
    }

    /**
     * Unset the priority of Logger.
     * (Thus it will use it's parent's priority or DEBUG if no parent.
     */
    public synchronized void unsetPriority()
    {
        unsetPriority( false );
    }

    /**
     * Unset the priority of Logger.
     * (Thus it will use it's parent's priority or DEBUG if no parent.
     * If recursive is true unset priorities of all child loggers.
     *
     * @param recursive true to unset priority of all child loggers
     */
    public synchronized void unsetPriority( final boolean recursive )
    {
        if( null != m_parent ) m_priority = m_parent.getPriority();
        else m_priority = Priority.DEBUG;

        m_priorityForceSet = false;
        resetChildPrioritys( recursive );
    }

    /**
     * Get all the child Loggers of current logger.
     *
     * @return the child loggers
     */
    public synchronized Logger[] getChildren()
    {
        if( null == m_children ) return new Logger[ 0 ];

        final Logger[] children = new Logger[ m_children.length ];       

        for( int i = 0; i < children.length; i++ )
        {
            children[ i ] = m_children[ i ];
        }

        return children;
    }

    /**
     * Update priority of children if any.
     */
    private synchronized void resetChildPrioritys( final boolean recursive )
    {
        if( null == m_children ) return;

        final Logger[] children = m_children;

        for( int i = 0; i < children.length; i++ )
        {
            children[ i ].resetPriority( recursive );
        }
    }

    /**
     * Update priority of this Logger.
     * If this loggers priority was manually set then ignore
     * otherwise get parents priority and update all children's priority.
     *
     */
    private synchronized void resetPriority( final boolean recursive )
    {
        if( recursive )
        {
            m_priorityForceSet = false;
        }
        else if( m_priorityForceSet ) 
        {
            return;
        }

        m_priority = m_parent.getPriority();
        resetChildPrioritys( recursive );
    }

    /**
     * Get a copy of log targets for this logger.
     *
     * @return the child loggers
     */
    public synchronized LogTarget[] getLogTargets()
    {
        if( null == m_logTargets ) 
        {
            if( null == m_parent ) return new LogTarget[ 0 ];
            else return m_parent.getLogTargets();
        }
        else
        {
            final LogTarget[] logTargets = new LogTarget[ m_logTargets.length ];       

            for( int i = 0; i < logTargets.length; i++ )
            {
                logTargets[ i ] = m_logTargets[ i ];
            }
            
            return logTargets;
        }
    }

    /**
     * Update logTargets of children if any.
     */
    private synchronized void resetChildLogTargets( final boolean recursive )
    {
        if( null == m_children ) return;

        for( int i = 0; i < m_children.length; i++ )
        {
            m_children[ i ].resetLogTargets( recursive );
        }
    }

    /**
     * Update logTarget of this Logger.
     * If this loggers logTarget was manually set then ignore
     * otherwise get parents logTarget and update all children's logTarget.
     *
     */
    private synchronized void resetLogTargets( final boolean recursive )
    {
        if( recursive )
        {
            m_logTargetsForceSet = false;
        }
        else if( m_logTargetsForceSet ) 
        {
            return;
        }

        m_logTargets = m_parent.getLogTargets();
        resetChildLogTargets( recursive );
    }

    /**
     * Set the log targets for this logger.
     *
     * @param logTargets the Log Targets
     */
    public synchronized void setLogTargets( final LogTarget[] logTargets )
    {
        m_logTargets = logTargets;
        m_logTargetsForceSet = true;
        resetChildLogTargets( false );
    }

    public synchronized void unsetLogTargets()
    {
        unsetLogTargets( false );
    }

    public synchronized void unsetLogTargets( final boolean recursive )
    {
        if( null != m_parent ) m_logTargets = m_parent.getLogTargets();
        else m_logTargets = null;

        m_logTargetsForceSet = false;
        resetChildLogTargets( recursive );
    }

    /**
     * Create a new child logger.
     * The category of child logger is [current-category].subcategory
     *
     * @param subCategory the subcategory of this logger
     * @return the new logger
     * @exception IllegalArgumentException if subCategory has an empty element name
     */
    public synchronized Logger getChildLogger( final String subCategory )
        throws IllegalArgumentException
    {
        final int end = subCategory.indexOf( CATEGORY_SEPARATOR );

        String nextCategory = null;
        String remainder = null;
        
        if( -1 == end ) nextCategory = subCategory;
        else
        {
            if( end == 0 )
            {
                throw new IllegalArgumentException( "Logger categories MUST not have empty elements" );
            }
            
            nextCategory = subCategory.substring( 0, end );
            remainder = subCategory.substring( end + 1 );
        }

        //Get FQN for category 
        String category = null;
        if( m_category.equals( "" ) ) category = nextCategory;
        else
        {
            category = m_category + CATEGORY_SEPARATOR + nextCategory;
        }

        //Check existing children to see if they
        //contain next Logger for step in category 
        if( null != m_children )
        {
            for( int i = 0; i < m_children.length; i++ )
            {
                if( m_children[ i ].getCategory().equals( category ) )
                {
                    if( null == remainder ) return m_children[ i ];
                    else return m_children[ i ].getChildLogger( remainder );
                }
            }
        }

        //Create new logger
        final Logger child = new Logger( m_hierarchy, category, null, this );

        //Add new logger to child list
        if( null == m_children ) 
        {
            m_children = new Logger[] { child };
        }
        else
        {
            final Logger[] children = new Logger[ m_children.length + 1 ];
            System.arraycopy( m_children, 0, children, 0, m_children.length );
            children[ m_children.length ] = child;
            m_children = children;
        }

        if( null == remainder ) return child;
        else return child.getChildLogger( remainder );
    }
}
