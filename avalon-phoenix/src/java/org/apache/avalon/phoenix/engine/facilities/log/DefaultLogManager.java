/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.engine.facilities.log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.apache.avalon.framework.atlantis.Facility;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.log.LogKit;
import org.apache.log.LogTarget;
import org.apache.log.Logger;
import org.apache.log.Priority;
import org.apache.log.output.FileOutputLogTarget;
import org.apache.avalon.phoenix.engine.facilities.LogManager;

/**
 * Component responsible for managing logs.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultLogManager
    extends AbstractLoggable
    implements LogManager, Contextualizable, Configurable
{
    private String        m_baseName;
    private File          m_baseDirectory;

    public void contextualize( final Context context )
        throws ContextException
    {
        m_baseName = (String)context.get( "name" );
        if( null == m_baseName ) m_baseName = "<base>";

        m_baseDirectory = (File)context.get( "directory" );
        if( null == m_baseDirectory ) m_baseDirectory = new File( "." );
    }

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        final Configuration[] targets = configuration.getChildren( "log-target" );
        final HashMap targetSet = configureTargets( m_baseName, m_baseDirectory, targets );

        final Configuration[] categories = configuration.getChildren( "category" );
        configureCategories( m_baseName, categories, targetSet );

        /*
          final String logPriority = configuration.getChild( "global-priority" ).getValue();
          final Priority.Enum priority = LogKit.getPriorityForName( logPriority );
          LogKit.setGlobalPriority( priority );
        */
    }

    /**
     * Get logger with category for application.
     * Note that this name may not be the absolute category.
     *
     * @param category the logger category
     * @return the Logger
     */
    public Logger getLogger( final String category )
    {
        String name = null;

        if( category.trim().equals( "" ) )
        {
            name = m_baseName;
        }
        else
        {
            name = m_baseName + '.' + category;
        }

        return LogKit.getLoggerFor( name );
    }

    private HashMap configureTargets( final String baseName,
                                      final File baseDirectory,
                                      final Configuration[] targets )
        throws ConfigurationException
    {
        final HashMap targetSet = new HashMap();

        for( int i = 0; i < targets.length; i++ )
        {
            final Configuration target = targets[ i ];
            final String name = baseName + '.' + target.getAttribute( "name" );
            String location = target.getAttribute( "location" ).trim();
            final String format = target.getAttribute( "format", null );

            if( '/' == location.charAt( 0 ) )
            {
                location = location.substring( 1 );
            }

            final File file = new File( baseDirectory, location );

            final FileOutputLogTarget logTarget = new FileOutputLogTarget();
            final AvalonLogFormatter formatter = new AvalonLogFormatter();
            formatter.setFormat( "%{time} [%7.7{priority}] <<%{category}>> " +
                                 "(%{context}): %{message}\\n%{throwable}" );
            logTarget.setFormatter( formatter );

            try { logTarget.setFilename( file.getAbsolutePath() ); }
            catch( final IOException ioe )
            {
                throw new ConfigurationException( "Error initializing log files", ioe );
            }

            if( null != format )
            {
                logTarget.setFormat( format );
            }

            targetSet.put( name, logTarget );
        }

        return targetSet;
    }

    private void configureCategories( final String baseName, 
                                      final Configuration[] categories,
                                      final HashMap targets )
        throws ConfigurationException
    {
        for( int i = 0; i < categories.length; i++ )
        {
            final Configuration category = categories[ i ];
            final String name = category.getAttribute( "name" );
            final String target = baseName + '.' + category.getAttribute( "target" );
            final String priorityName = category.getAttribute( "priority" );

            final Logger logger = getLogger( name );

            final LogTarget logTarget = (LogTarget)targets.get( target );
            if( null != target ) 
            {
                logger.setLogTargets( new LogTarget[] { logTarget } );
            }

            final Priority priority = Priority.getPriorityForName( priorityName );
            logger.setPriority( priority );

        }
    }
}
