/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log;

import java.util.Hashtable;
import org.apache.log.output.DefaultOutputLogTarget;

/**
 * This defines the basic interface to a log engine.
 * The log engine represents an independent hierarchy.
 *
 *  @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class LogEngine
{
    protected final Hashtable       m_loggers                = new Hashtable();
    protected final Hashtable       m_categories             = new Hashtable();
    protected final Hashtable       m_logTargets;
    protected Priority.Enum         m_priority               = Priority.DEBUG;
    protected LogTarget             m_defaultLogTarget;

    public LogEngine()
    {
        m_logTargets = new Hashtable();
        m_defaultLogTarget = new DefaultOutputLogTarget();
        m_logTargets.put( "default", m_defaultLogTarget );
    }

    /**
     * Add a named log target to global list.
     *
     * @param name the name of target
     * @param target the target
     */
    public synchronized void addLogTarget( final String name, final LogTarget target )
    {
        if( name.equals("default") ) m_defaultLogTarget = target;
        m_logTargets.put( name, target );
    }

    /**
     * Retrieve named log target if it exists.
     *
     * @param name the name of log target
     * @return the LogTarget
     */
    public LogTarget getLogTarget( final String name )
    {
        return (LogTarget)m_logTargets.get( name );
    }

    /**
     * Create or update named category and retrieve it.
     *
     * @param categoryName name of category
     * @param priority the priority of categroy
     * @return the catgeory
     */
    public synchronized Category createCategory( final String categoryName, 
                                                 final Priority.Enum priority )
    {       
        Category category = (Category)m_categories.get( categoryName );

        if( null == category )
        {
            category = new Category( categoryName );
            m_categories.put( categoryName, category );
        }

        category.setPriority( priority );

        return category;
    }

    /**
     * Create or update instance of logger.
     *
     * @param category the category that logger logs about
     * @return the Logger
     */
    public Logger createLogger( final Category category )
    {
        return createLogger( category, null );
    }

    /**
     * Create or update instance of logger.
     *
     * @param logTargets[] the list of log targets logger is to output to.
     * @param category the category that logger logs about
     * @return the Logger
     */
    public synchronized Logger createLogger( final Category category, final LogTarget logTargets[] )
    {
        final String categoryName = category.getName();
        Logger logger = (Logger)m_loggers.get( categoryName );

        if( null == logger )
        {
            final int index = categoryName.lastIndexOf( Category.SEPARATOR );

            Logger parent = null;

            if( -1 != index )
            {
                final String parentName = categoryName.substring( 0, index );
                parent = getLoggerFor( parentName );
            }

            logger = new Logger( this, category, logTargets, parent );
            m_loggers.put( categoryName, logger );
        }
        else
        {
            if( null != logTargets )
            {
                logger.setLogTargets( logTargets );
            }
        }

        return logger;
    }

    /**
     * Retrieve the default log target.
     *
     * @return the default LogTarget
     */
    public LogTarget getDefaultLogTarget()
    {
        return m_defaultLogTarget;
    }

    /**
     * Return VM global priority.
     *
     * @return the priority
     */
    public Priority.Enum getGlobalPriority()
    {
        return m_priority;
    }

    /**
     * Retrieve a logger for named category.
     *
     * @param category the context
     * @return the Logger
     */
    public Logger getLoggerFor( final String category )
    {
        synchronized( m_loggers )
        {
            Logger logger = (Logger)m_loggers.get( category );
            if( null == logger )
            {
                logger = createLogger( createCategory( category, Priority.DEBUG ) );
            }
            return logger;
        }
    }

    /**
     * Log an error message and exception to stderr.
     * TODO: replace this with an error handler
     */
    public void log( final String message, final Throwable t )
    {
        System.err.println( "Error: " + message );
        t.printStackTrace();
    }

    /**
     * Logs an error message to stderr.
     * TODO: replace this with an error handler
     */
    public void log( final String message )
    {
        System.err.println( "Error: " + message );
    }

    /**
     * Sets the default LogTarget for the default logger.
     */
    public void setDefaultLogTarget( final LogTarget defaultLogTarget )
    {
        addLogTarget( "default", defaultLogTarget );
    }

    /**
     * Set the global priority for this virtual machine.  Nothing below
     * this level will be logged when using this LogKit.
     */
    public void setGlobalPriority( final Priority.Enum priority )
    {
        m_priority = priority;
    }
}
