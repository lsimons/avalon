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
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.LogKitLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.log.Hierarchy;
import org.apache.log.LogTarget;
import org.apache.log.Priority;

/**
 * LogKitLoggerManager implementation.  It populates the LoggerManager
 * from a configuration file.
 *
 * @author <a href="mailto:giacomo@apache.org">Giacomo Pati</a>
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.4 $ $Date: 2002/08/07 04:05:59 $
 * @since 4.0
 */
public class LogKitLoggerManager
    implements LoggerManager, Contextualizable, Configurable
{
    /** Map for name to logger mapping */
    final private Map m_loggers = new HashMap();

    /** The context object */
    private Context m_context;

    /** The hierarchy private to LogKitManager */
    private Hierarchy m_hierarchy;

    /** The root logger to configure */
    private String m_prefix;

    /** The default logger used for this system */
    final private Logger m_defaultLogger;
    
    /** The logger used to log output from the logger manager. */
    final private Logger m_logger;

    /**
     * Creates a new <code>DefaultLogKitManager</code>. It will use a new <code>Hierarchy</code>.
     */
    public LogKitLoggerManager()
    {
        this( new Hierarchy() );
    }

    /**
     * Creates a new <code>DefaultLogKitManager</code> with an existing <code>Hierarchy</code>.
     */
    public LogKitLoggerManager( final Hierarchy hierarchy )
    {
        this( null, hierarchy );
    }

    /**
     * Creates a new <code>DefaultLogKitManager</code> using
     * specified logger name as root logger.
     */
    public LogKitLoggerManager( final String prefix )
    {
        this( prefix, new Hierarchy() );
    }

    /**
     * Creates a new <code>DefaultLogKitManager</code> with an existing <code>Hierarchy</code> using
     * specified logger name as root logger.
     */
    public LogKitLoggerManager( final String prefix, final Hierarchy hierarchy )
    {
        this( prefix, hierarchy,
              new LogKitLogger( hierarchy.getLoggerFor( "" ) ) );
    }

    /**
     * Creates a new <code>DefaultLogKitManager</code> with an existing <code>Hierarchy</code> using
     * specified logger name as root logger.
     */
    public LogKitLoggerManager( final String prefix, final Hierarchy hierarchy,
        final Logger defaultLogger )
    {
        this( prefix, hierarchy, defaultLogger, defaultLogger );
    }

    /**
     * Creates a new <code>DefaultLogKitManager</code> with an existing <code>Hierarchy</code> using
     * specified logger name as root logger.
     */
    public LogKitLoggerManager( final String prefix, final Hierarchy hierarchy,
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
     * this LogKitManager does not have the match the default Logger will
     * be returned and a warning is issued.
     *
     * @param categoryName  The category name of a configured Logger.
     * @return the Logger.
     */
    public final Logger getLoggerForCategory( final String categoryName )
    {
        final String fullCategoryName = getFullCategoryName( m_prefix, categoryName );
        
        final Logger logger = (Logger)m_loggers.get( fullCategoryName );

        if( null != logger )
        {
            if( m_logger.isDebugEnabled() )
            {
                m_logger.debug( "Logger for category " + fullCategoryName + " returned" );
            }
            return logger;
        }

        if( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "Logger for category " + fullCategoryName + " not defined in "
                                    + "configuration. New Logger created and returned" );
        }

        return new LogKitLogger( m_hierarchy.getLoggerFor( fullCategoryName ) );
    }

    public final Logger getDefaultLogger()
    {
        return m_defaultLogger;
    }

    /**
     * Reads a context object that will be supplied to the log target factory manager.
     *
     * @param context The context object.
     * @throws ContextException if the context is malformed
     */
    public final void contextualize( final Context context )
        throws ContextException
    {
        m_context = context;
    }

    /**
     * Reads a configuration object and creates the category mapping.
     *
     * @param configuration  The configuration object.
     * @throws ConfigurationException if the configuration is malformed
     */
    public final void configure( final Configuration configuration )
        throws ConfigurationException
    {
        final Configuration factories = configuration.getChild( "factories" );
        final LogTargetFactoryManager targetFactoryManager = setupTargetFactoryManager( factories );

        final Configuration targets = configuration.getChild( "targets" );
        final LogTargetManager targetManager = setupTargetManager( targets, targetFactoryManager );

        final Configuration categories = configuration.getChild( "categories" );
        final Configuration[] category = categories.getChildren( "category" );
        setupLoggers( targetManager, m_prefix, category );
    }

    /**
     * Setup a LogTargetFactoryManager
     *
     * @param configuration  The configuration object.
     * @throws ConfigurationException if the configuration is malformed
     */
    private final LogTargetFactoryManager setupTargetFactoryManager( final Configuration configuration )
        throws ConfigurationException
    {
        final DefaultLogTargetFactoryManager targetFactoryManager = new DefaultLogTargetFactoryManager();
        if( targetFactoryManager instanceof LogEnabled )
        {
            targetFactoryManager.enableLogging( m_logger );
        }

        if( targetFactoryManager instanceof Contextualizable )
        {
            try
            {
                targetFactoryManager.contextualize( m_context );
            }
            catch( final ContextException ce )
            {
                throw new ConfigurationException( "cannot contextualize default factory manager", ce );
            }
        }

        targetFactoryManager.configure( configuration );

        return targetFactoryManager;
    }

    /**
     * Setup a LogTargetManager
     *
     * @param configuration  The configuration object.
     * @throws ConfigurationException if the configuration is malformed
     */
    private final LogTargetManager setupTargetManager( final Configuration configuration,
                                                       final LogTargetFactoryManager targetFactoryManager )
        throws ConfigurationException
    {
        final DefaultLogTargetManager targetManager = new DefaultLogTargetManager();

        if( targetManager instanceof LogEnabled )
        {
            targetManager.enableLogging( m_logger );
        }

        if( targetManager instanceof LogTargetFactoryManageable )
        {
            targetManager.setLogTargetFactoryManager( targetFactoryManager );
        }

        if( targetManager instanceof Configurable )
        {
            targetManager.configure( configuration );
        }

        return targetManager;
    }
    
    /**
     * Generates a full category name given a prefix and category.  Either may be
     *  null.
     *
     * @param prefix Prefix or parent category.
     * @param category Child category name.
     */
    private final String getFullCategoryName( String prefix, String category )
    {
        if( ( null == prefix ) || ( prefix.length() == 0 )  )
        {
            if ( category == null )
            {
                return "";
            }
            else
            {
                return category;
            }
        }
        else
        {
            if( ( null == category ) || ( category.length() == 0 ) )
            {
                return prefix;
            }
            else
            {
                return prefix + org.apache.log.Logger.CATEGORY_SEPARATOR + category;
            }
        }
    }

    /**
     * Setup Loggers
     *
     * @param categories []  The array object of configurations for categories.
     * @throws ConfigurationException if the configuration is malformed
     */
    private final void setupLoggers( final LogTargetManager targetManager,
                                     final String parentCategory,
                                     final Configuration[] categories )
        throws ConfigurationException
    {
        for( int i = 0; i < categories.length; i++ )
        {
            final String category = categories[ i ].getAttribute( "name" );
            final String loglevel = categories[ i ].getAttribute( "log-level" ).toUpperCase();

            final Configuration[] targets = categories[ i ].getChildren( "log-target" );
            final LogTarget[] logTargets = new LogTarget[ targets.length ];
            for( int j = 0; j < targets.length; j++ )
            {
                final String id = targets[ j ].getAttribute( "id-ref" );
                logTargets[ j ] = targetManager.getLogTarget( id );
            }

            if( "".equals( category ) && logTargets.length > 0 )
            {
                m_hierarchy.setDefaultPriority( Priority.getPriorityForName( loglevel ) );
                m_hierarchy.setDefaultLogTargets( logTargets );
            }

            final String fullCategory = getFullCategoryName( parentCategory, category );

            final org.apache.log.Logger logger = m_hierarchy.getLoggerFor( fullCategory );
            m_loggers.put( fullCategory, new LogKitLogger( logger ) );
            if( m_logger.isDebugEnabled() )
            {
                m_logger.debug( "added logger for category " + fullCategory );
            }
            logger.setPriority( Priority.getPriorityForName( loglevel ) );
            logger.setLogTargets( logTargets );

            final Configuration[] subCategories = categories[ i ].getChildren( "category" );
            if( null != subCategories )
            {
                setupLoggers( targetManager, fullCategory, subCategories );
            }
        }
    }
}
