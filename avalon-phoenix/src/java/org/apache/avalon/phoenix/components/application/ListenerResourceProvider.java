/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.application;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.phoenix.interfaces.ApplicationContext;
import org.apache.avalon.phoenix.metadata.BlockListenerMetaData;
import org.apache.excalibur.containerkit.lifecycle.ResourceProvider;

/**
 * The accessor used to access resources for a particular
 * Block or Listener.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2002/08/06 11:57:39 $
 */
class ListenerResourceProvider
    extends AbstractLogEnabled
    implements ResourceProvider
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( ListenerResourceProvider.class );

    /**
     * Context in which Blocks/Listeners operate.
     */
    private final ApplicationContext m_context;

    public ListenerResourceProvider( final ApplicationContext context )
    {
        if( null == context )
        {
            throw new NullPointerException( "context" );
        }

        m_context = context;
    }

    /**
     * Create Block for specified entry.
     *
     * @param entry the entry
     * @return a new object
     * @throws Exception
     */
    public Object createObject( final Object entry )
        throws Exception
    {
        final BlockListenerMetaData metaData = getMetaData( entry );
        final ClassLoader classLoader = m_context.getClassLoader();
        final Class clazz =
            classLoader.loadClass( metaData.getClassname() );
        return clazz.newInstance();
    }

    /**
     * Retrieve Logger for specified listener.
     *
     * @param entry the entry representing listener
     * @return the new Logger object
     * @throws Exception if an error occurs
     */
    public Logger createLogger( final Object entry )
        throws Exception
    {
        final BlockListenerMetaData metaData = getMetaData( entry );
        final String name = metaData.getName();
        return m_context.getLogger( name );
    }

    public Context createContext( Object entry )
        throws Exception
    {
        throw new UnsupportedOperationException();
    }

    public ComponentManager createComponentManager( final Object entry )
        throws Exception
    {
        throw new UnsupportedOperationException();
    }

    public ServiceManager createServiceManager( final Object entry )
        throws Exception
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Retrieve a configuration for specified component.
     * If the configuration is missing then a exception
     * is raised with an appropraite error message.
     *
     * @param entry the entry
     * @return the Configuration object
     * @throws ConfigurationException if an error occurs
     */
    public Configuration createConfiguration( final Object entry )
        throws Exception
    {
        final BlockListenerMetaData metaData = getMetaData( entry );
        final String name = metaData.getName();
        try
        {
            return m_context.getConfiguration( name );
        }
        catch( final ConfigurationException ce )
        {
            //Note that this shouldn't ever happen once we
            //create a Config validator
            final String message =
                REZ.getString( "missing-listener-configuration",
                               name );
            throw new ConfigurationException( message, ce );
        }
    }

    public Parameters createParameters( final Object entry )
        throws Exception
    {
        final Configuration configuration = createConfiguration( entry );
        final Parameters parameters =
            Parameters.fromConfiguration( configuration );
        parameters.makeReadOnly();
        return parameters;
    }

    /**
     * Get meta data for entry.
     *
     * @param entry the entry
     * @return the metadata
     */
    private BlockListenerMetaData getMetaData( final Object entry )
    {
        return (BlockListenerMetaData)entry;
    }
}
