/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.facilitys;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import org.apache.avalon.AbstractLoggable;
import org.apache.avalon.Context;
import org.apache.avalon.Contextualizable;
import org.apache.avalon.atlantis.Facility;
import org.apache.avalon.configuration.Configurable;
import org.apache.avalon.configuration.Configuration;
import org.apache.avalon.configuration.ConfigurationException;
import org.apache.avalon.util.log.AvalonLogFormatter;
import org.apache.log.Category;
import org.apache.log.LogKit;
import org.apache.log.LogTarget;
import org.apache.log.output.FileOutputLogTarget;

/**
 * Component responsible for managing logs.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultLogManager
    extends AbstractLoggable
    implements Facility, Contextualizable, Configurable
{
    protected String        m_baseName;
    protected File          m_baseDirectory;

    public void contextualize( final Context context )
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
        configureTargets( m_baseName, m_baseDirectory, targets );
        
        final Configuration[] categories = configuration.getChildren( "category" );
        configureCategories( m_baseName, categories );

        /*
          final String logPriority = configuration.getChild( "global-priority" ).getValue();
          final Priority.Enum priority = LogKit.getPriorityForName( logPriority );
          LogKit.setGlobalPriority( priority );
        */
    }
  
    protected void configureTargets( final String baseName, 
                                     final File baseDirectory, 
                                     final Configuration[] targets )
        throws ConfigurationException
    {
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
            
            LogKit.addLogTarget( name, logTarget );
        }
    }
    
    protected void configureCategories( final String baseName, final Configuration[] categories )
        throws ConfigurationException
    {
        for( int i = 0; i < categories.length; i++ )
        {
            final Configuration category = categories[ i ];
            String name = category.getAttribute( "name" );
            final String target = baseName + '.' + category.getAttribute( "target" );
            final String priority = category.getAttribute( "priority" );
            
            if( name.trim().equals( "" ) )
            {
                name = baseName;
            }
            else
            {
                name = baseName + '.' + name;
            }
            
            final Category logCategory = 
                LogKit.createCategory( name, LogKit.getPriorityForName( priority ) );
            final LogTarget logTarget = LogKit.getLogTarget( target );
            LogTarget logTargets[] = null;
            
            if( null != target ) logTargets = new LogTarget[] { logTarget };
            
            LogKit.createLogger( logCategory, logTargets );    
        }
    }
}
