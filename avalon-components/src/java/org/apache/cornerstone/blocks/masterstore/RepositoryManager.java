/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.blocks.masterstore;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.avalon.AbstractLoggable;
import org.apache.avalon.Component;
import org.apache.avalon.ComponentManager;
import org.apache.avalon.Composer;
import org.apache.avalon.Context;
import org.apache.avalon.Contextualizable;
import org.apache.avalon.Initializable;
import org.apache.avalon.component.ComponentException;
import org.apache.avalon.configuration.Configurable;
import org.apache.avalon.configuration.Configuration;
import org.apache.avalon.configuration.ConfigurationException;
import org.apache.cornerstone.services.store.Repository;
import org.apache.cornerstone.services.store.Store;
import org.apache.phoenix.Block;

/**
 *
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 */
public class RepositoryManager
    extends AbstractLoggable
    implements Block, Store, Contextualizable, Composer, Configurable,
    org.apache.cornerstone.services.Store
{
    private static final String   REPOSITORY_NAME  = "Repository";
    private static long           id               = 0;

    protected HashMap             m_repositories        = new HashMap();
    protected HashMap             m_models              = new HashMap();
    protected HashMap             m_classes             = new HashMap();
    protected ComponentManager    m_componentManager;
    protected Context             m_context;

    public void contextualize( final Context context )
    {
        m_context = context;
    }

    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        m_componentManager = componentManager;
    }

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        final Configuration[] registeredClasses =
            configuration.getChild( "repositories" ).getChildren( "repository" );

        for( int i = 0; i < registeredClasses.length; i++ )
        {
            registerRepository( registeredClasses[ i ] );
        }
    }

    public void registerRepository( final Configuration repConf )
        throws ConfigurationException
    {
        final String className = repConf.getAttribute( "class" );
        getLogger().info( "Registering Repository " + className );

        final Configuration[] protocols =
            repConf.getChild( "protocols" ).getChildren( "protocol" );
        final Configuration[] types = repConf.getChild( "types" ).getChildren( "type" );
        final Configuration[] modelIterator =
            repConf.getChild( "models" ).getChildren( "model" );

        for( int i = 0; i < protocols.length; i++ )
        {
            final String protocol = protocols[ i ].getValue();

            for( int j = 0; j < types.length; j++ )
            {
                final String type = types[ j ].getValue();

                for( int k = 0; k < modelIterator.length; k++ )
                {
                    final String model = modelIterator[ k ].getValue();
                    m_classes.put( protocol + type + model, className );
                    getLogger().info( "   for " + protocol + "," + type + "," + model );
                }
            }
        }
    }

    public void release( Component component )
    {
    }

    public Component select( Object hint )
        throws ComponentException
    {
        Configuration repConf = null;
        try
        {
            repConf = (Configuration) hint;
        }
        catch( final ClassCastException cce )
        {
            throw new ComponentException( "Hint is of the wrong type. " +
                                          "Must be a Configuration", cce );
        }
        URL destination = null;
        try
        {
            destination = new URL( repConf.getAttribute("destinationURL") );
        }
        catch( final ConfigurationException ce )
        {
            throw new ComponentException( "Malformed configuration has no " +
                                          "destinationURL attribute", ce );
        }
        catch( final MalformedURLException mue )
        {
            throw new ComponentException( "destination is malformed. " +
                                          "Must be a valid URL", mue );
        }

        try
        {
            final String type = repConf.getAttribute( "type" );
            final String repID = destination + type;
            Repository reply = (Repository)m_repositories.get( repID );
            final String model = (String)repConf.getAttribute( "model" );

            if( null != reply )
            {
                if( m_models.get( repID ).equals( model ) )
                {
                    return reply;
                }
                else
                {
                    final String message = "There is already another repository with the " +
                        "same destination and type but with different model";
                    throw new ComponentException( message );
                }
            }
            else
            {
                final String protocol = destination.getProtocol();
                final String repClass = (String)m_classes.get( protocol + type + model );

                getLogger().debug( "Need instance of " + repClass + " to handle: " +
                                   protocol + type + model );

                try
                {
                    reply = (Repository)Class.forName( repClass ).newInstance();
                    setupLogger( reply, "repository" );

                    if( reply instanceof Contextualizable )
                    {
                        ((Contextualizable)reply).contextualize( m_context );
                    }

                    if( reply instanceof Composer )
                    {
                        ((Composer)reply).compose( m_componentManager );
                    }

                    if( reply instanceof Configurable )
                    {
                        ((Configurable)reply).configure( repConf );
                    }

                    if( reply instanceof Initializable )
                    {
                        ((Initializable)reply).init();
                    }

                    m_repositories.put( repID, reply );
                    m_models.put( repID, model );
                    getLogger().info( "New instance of " + repClass + " created for " +
                                      destination );
                    return reply;
                }
                catch( final Exception e )
                {
                    final String message = "Cannot find or init repository: " + e.getMessage();
                    getLogger().warn( message, e );

                    throw new ComponentException( message, e );
                }
            }
        }
        catch( final ConfigurationException ce )
        {
            throw new ComponentException( "Malformed configuration", ce );
        }
    }

    public static final String getName()
    {
        return REPOSITORY_NAME + id++;
    }
}
