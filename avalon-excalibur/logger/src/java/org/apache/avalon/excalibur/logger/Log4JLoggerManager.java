/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.logger;

import java.util.HashMap;
import java.util.Map;
import org.apache.avalon.framework.logger.Log4JLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.log4j.Category;
import org.apache.log4j.Hierarchy;

/**
 * Log4JLoggerManager implementation.  This is the interface used to get instances of
 * a Logger for your system.  This manager does not set up the categories--it
 * leaves that as an excercise for Log4J's construction.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/08/07 04:05:59 $
 * @since 4.1
 */
public class Log4JLoggerManager
    implements LoggerManager
{
    /** Map for name to logger mapping */
    final private Map m_loggers = new HashMap();

    /** The root logger to configure */
    private String m_prefix;

    /** The hierarchy private to Log4JManager */
    private Hierarchy m_hierarchy;

    /** The default logger used for this system */
    final private Logger m_defaultLogger;
    
    /** The logger used to log output from the logger manager. */
    final private Logger m_logger;

    /**
     * Creates a new <code>DefaultLog4JManager</code>. It will use a new <code>Hierarchy</code>.
     */
    public Log4JLoggerManager()
    {
        this( Category.getDefaultHierarchy() );
    }

    /**
     * Creates a new <code>DefaultLog4JManager</code> with an existing <code>Hierarchy</code>.
     */
    public Log4JLoggerManager( final Hierarchy hierarchy )
    {
        this( null, hierarchy );
    }

    /**
     * Creates a new <code>DefaultLog4JManager</code> using
     * specified logger name as root logger.
     */
    public Log4JLoggerManager( final String prefix )
    {
        this( prefix, Category.getDefaultHierarchy() );
    }

    /**
     * Creates a new <code>DefaultLog4JManager</code> with an existing <code>Hierarchy</code> using
     * specified logger name as root logger.
     */
    public Log4JLoggerManager( final String prefix, final Hierarchy hierarchy )
    {
        this( prefix, hierarchy,
              new Log4JLogger( hierarchy.getInstance( "" ) ) );
    }

    /**
     * Creates a new <code>DefaultLog4JManager</code> with an existing <code>Hierarchy</code> using
     * specified logger name as root logger.
     */
    public Log4JLoggerManager( final String prefix, final Hierarchy hierarchy,
        final Logger defaultLogger )
    {
        this( prefix, hierarchy, defaultLogger, defaultLogger );
    }

    /**
     * Creates a new <code>DefaultLog4JManager</code> with an existing <code>Hierarchy</code> using
     * specified logger name as root logger.
     */
    public Log4JLoggerManager( final String prefix, final Hierarchy hierarchy,
        final Logger defaultLogger, final Logger logger )
    {
        m_prefix = prefix;
        m_hierarchy = hierarchy;
        m_defaultLogger = defaultLogger;
        m_logger = logger;
    }

    /**
     * Retrieves a Logger from a category name. Usually
     * the category name refers to a configuration attribute name.  If
     * this Log4JManager does not have the match the default Logger will
     * be returned and a warning is issued.
     *
     * @param categoryName  The category name of a configured Logger.
     * @return the Logger.
     */
    public final Logger getLoggerForCategory( final String categoryName )
    {
        Logger logger = (Logger)m_loggers.get( categoryName );

        if( null != logger )
        {
            if( m_logger.isDebugEnabled() )
            {
                m_logger.debug( "Logger for category " + categoryName + " returned" );
            }
            return logger;
        }

        if( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "Logger for category " + categoryName
                                   + " not defined in configuration. New Logger created and returned" );
        }

        logger = new Log4JLogger( m_hierarchy.getInstance( categoryName ) );
        m_loggers.put( categoryName, logger );
        return logger;
    }

    public final Logger getDefaultLogger()
    {
        return m_defaultLogger;
    }
}
