/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.excalibur.logger.LoggerManager;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.AvalonFormatter;
import org.apache.avalon.framework.logger.LogKitLogger;
import org.apache.log.Hierarchy;
import org.apache.log.LogTarget;
import org.apache.log.Logger;
import org.apache.log.Priority;
import org.apache.log.output.io.FileTarget;

/**
 * A <code>LogKitManager</code> that supports the old &lt;logs version="1.0"/&gt;
 * style logging configuration.
 *
 * @author <a href="mailto:colus@isoft.co.kr">Eung-ju Park</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class SimpleLogKitManager
    extends AbstractLogEnabled
    implements LoggerManager, Contextualizable, Configurable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( SimpleLogKitManager.class );

    private static final String DEFAULT_FORMAT =
        "%7.7{priority} %23.23{time:yyyy-MM-dd' 'HH:mm:ss.SSS} [%8.8{category}] (%{context}): "
        + "%{message}\n%{throwable}";

    ///Base directory of applications working directory
    private File m_baseDirectory;

    ///Hierarchy of Application logging

    private final Hierarchy m_hierarchy = new Hierarchy();
    private final Logger m_logkitLogger = m_hierarchy.getLoggerFor( "" );
    private org.apache.avalon.framework.logger.Logger m_logger =
        new LogKitLogger( m_logkitLogger );

    public void contextualize( final Context context )
        throws ContextException
    {
        m_baseDirectory = (File)context.get( "app.home" );
    }

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        final Configuration[] targets = configuration.getChildren( "log-target" );
        final HashMap targetSet = configureTargets( targets );
        final Configuration[] categories = configuration.getChildren( "category" );
        configureCategories( categories, targetSet );
    }

    public org.apache.avalon.framework.logger.Logger
        getLoggerForCategory( final String categoryName )
    {
        return m_logger.getChildLogger( categoryName );
    }

    public org.apache.avalon.framework.logger.Logger getDefaultLogger()
    {
        return m_logger;
    }

    /**
     * Configure a set of logtargets based on config data.
     *
     * @param targets the target configuration data
     * @return a Map of target-name to target
     * @throws ConfigurationException if an error occurs
     */
    private HashMap configureTargets( final Configuration[] targets )
        throws ConfigurationException
    {
        final HashMap targetSet = new HashMap();

        for( int i = 0; i < targets.length; i++ )
        {
            final Configuration target = targets[ i ];
            final String name = target.getAttribute( "name" );
            String location = target.getAttribute( "location" ).trim();
            final String format = target.getAttribute( "format", DEFAULT_FORMAT );

            if( '/' == location.charAt( 0 ) )
            {
                location = location.substring( 1 );
            }

            final AvalonFormatter formatter = new AvalonFormatter( format );

            //Specify output location for logging
            final File file = new File( m_baseDirectory, location );

            //Setup logtarget
            FileTarget logTarget = null;
            try
            {
                logTarget = new FileTarget( file.getAbsoluteFile(), false, formatter );
            }
            catch( final IOException ioe )
            {
                final String message =
                    REZ.getString( "target.nocreate", name, file, ioe.getMessage() );
                throw new ConfigurationException( message, ioe );
            }

            targetSet.put( name, logTarget );
        }

        return targetSet;
    }

    /**
     * Configure Logging categories.
     *
     * @param categories configuration data for categories
     * @param targets a hashmap containing the already existing taregt
     * @throws ConfigurationException if an error occurs
     */
    private void configureCategories( final Configuration[] categories, final HashMap targets )
        throws ConfigurationException
    {
        for( int i = 0; i < categories.length; i++ )
        {
            final Configuration category = categories[ i ];
            final String name = category.getAttribute( "name", "" );
            final String target = category.getAttribute( "target" );
            final String priorityName = category.getAttribute( "priority" );

            final Logger logger =
                m_logkitLogger.getChildLogger( name );

            final LogTarget logTarget = (LogTarget)targets.get( target );
            if( null == target )
            {
                final String message = REZ.getString( "unknown-target", target, name );
                throw new ConfigurationException( message );
            }

            final Priority priority = Priority.getPriorityForName( priorityName );
            if( !priority.getName().equals( priorityName ) )
            {
                final String message = REZ.getString( "unknown-priority", priorityName, name );
                throw new ConfigurationException( message );
            }

            if( getLogger().isDebugEnabled() )
            {
                final String message =
                    REZ.getString( "category-create", name, target, priorityName );
                getLogger().debug( message );
            }

            if( name.equals( "" ) )
            {
                m_hierarchy.setDefaultPriority( priority );
                m_hierarchy.setDefaultLogTarget( logTarget );
            }
            else
            {
                logger.setPriority( priority );
                logger.setLogTargets( new LogTarget[]{logTarget} );
            }
        }
    }
}
