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
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.DefaultComponentManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.LogKitLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.DefaultServiceManager;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.phoenix.interfaces.Application;
import org.apache.avalon.phoenix.interfaces.ApplicationContext;
import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metadata.DependencyMetaData;
import org.apache.excalibur.containerkit.ResourceAccessor;

/**
 * The accessor used to access resources for a particular
 * Block or Listener.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.7 $ $Date: 2002/06/04 06:58:11 $
 */
class BlockAccessor
    extends AbstractLogEnabled
    implements ResourceAccessor
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( BlockAccessor.class );

    /**
     * Context in which Blocks/Listeners operate.
     */
    private final ApplicationContext m_context;

    /**
     * The Application which this phase is associated with.
     * Required to build a ComponentManager.
     */
    private final Application m_application;

    public BlockAccessor( final ApplicationContext context,
                          final Application application )
    {
        if( null == context )
        {
            throw new NullPointerException( "context" );
        }

        if( null == application )
        {
            throw new NullPointerException( "application" );
        }

        m_context = context;
        m_application = application;
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
        final BlockMetaData metaData = getMetaDataFor( entry );
        final ClassLoader classLoader = m_context.getClassLoader();
        String classname = metaData.getBlockInfo().getBlockDescriptor().getClassname();
        final Class clazz = classLoader.loadClass( classname );
        return clazz.newInstance();
    }

    /**
     * Retrieve Logger for specified block.
     *
     * @param entry the entry representing block
     * @return the new Logger object
     * @throws Exception if an error occurs
     */
    public Logger createLogger( final Object entry )
        throws Exception
    {
        final BlockMetaData metaData = getMetaDataFor( entry );
        final String name = metaData.getName();
        return new LogKitLogger( m_context.getLogger( name ) );
    }

    /**
     * Create a BlockContext object for Block.
     *
     * @param entry the entry representing block
     * @return the created BlockContext
     */
    public Context createContext( final Object entry )
        throws Exception
    {
        final BlockMetaData metaData = getMetaDataFor( entry );
        return new DefaultBlockContext( metaData.getName(),
                                        m_context );
    }

    /**
     * Create a <code>ComponentManager</code> object for a
     * specific <code>Block</code>. This requires that for
     * each dependency a reference to providing <code>Block</code>
     * is aaqiured from the Application and placing it in
     * <code>ComponentManager</code> under the correct name.
     *
     * @param entry the entry representing block
     * @return the created ComponentManager
     */
    public ComponentManager createComponentManager( final Object entry )
        throws Exception
    {
        final BlockMetaData metaData = getMetaDataFor( entry );
        final DefaultComponentManager componentManager = new DefaultComponentManager();
        final DependencyMetaData[] roles = metaData.getDependencies();

        for( int i = 0; i < roles.length; i++ )
        {
            final DependencyMetaData role = roles[ i ];
            final Object dependency = m_application.getBlock( role.getName() );
            if( dependency instanceof Component )
            {
                componentManager.put( role.getRole(), (Component)dependency );
            }
            else
            {
                final String message =
                    REZ.getString( "lifecycle.nota-component.error",
                                   metaData.getName(),
                                   role.getRole(),
                                   role.getName() );
                throw new Exception( message );
            }
        }

        return componentManager;
    }

    /**
     * Create a <code>ServiceManager</code> object for a
     * specific <code>Block</code>. This requires that for
     * each dependency a reference to providing <code>Block</code>
     * is aaqiured from the Application and placing it in
     * <code>ServiceManager</code> under the correct name.
     *
     * @param entry the entry representing block
     * @return the created ServiceManager
     */
    public ServiceManager createServiceManager( final Object entry )
        throws Exception
    {
        final BlockMetaData metaData = getMetaDataFor( entry );
        final DefaultServiceManager manager = new DefaultServiceManager();
        final DependencyMetaData[] roles = metaData.getDependencies();

        for( int i = 0; i < roles.length; i++ )
        {
            final DependencyMetaData role = roles[ i ];
            final Object dependency = m_application.getBlock( role.getName() );
            manager.put( role.getRole(), dependency );
        }

        return manager;
    }

    public Configuration createConfiguration( final Object entry )
        throws Exception
    {
        final BlockMetaData metaData = getMetaDataFor( entry );
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
                REZ.getString( "missing-block-configuration",
                               name );
            throw new ConfigurationException( message, ce );
        }
    }

    public Parameters createParameters( final Object entry )
        throws Exception
    {
        final Configuration configuration =
            createConfiguration( entry );
        final Parameters parameters =
            Parameters.fromConfiguration( configuration );
        parameters.makeReadOnly();
        return parameters;
    }

    /**
     * Retrieve metadata for entry.
     *
     * @param entry the entry
     * @return the MetaData for entry
     */
    private BlockMetaData getMetaDataFor( final Object entry )
    {
        return ( (BlockEntry)entry ).getMetaData();
    }
}
