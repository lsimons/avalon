/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.containerkit.kernel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.info.ComponentInfo;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.phoenix.containerkit.factory.ComponentBundle;
import org.apache.avalon.phoenix.containerkit.factory.ComponentFactory;
import org.apache.avalon.phoenix.containerkit.kernel.processor.DependencyMap;
import org.apache.avalon.phoenix.containerkit.lifecycle.LifecycleHelper;
import org.apache.avalon.phoenix.containerkit.lifecycle.ResourceProvider;
import org.apache.avalon.phoenix.containerkit.metadata.ComponentMetaData;
import org.apache.avalon.phoenix.containerkit.registry.ComponentProfile;

/**
 * The <code>AbstractServiceKernel</code> defines an application scope through
 * the aggregation of a set of container entries and the exposure of operations
 * supporting the collective startup and shutdown of registered entries.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.1 $ $Date: 2003/01/18 16:43:43 $
 */
public abstract class AbstractServiceKernel
    extends AbstractLogEnabled
    implements Initializable, Disposable
{
    private final static Resources REZ =
        ResourceManager.getPackageResources( AbstractServiceKernel.class );
    /**
     * The factory to use when creating components.
     */
    private ComponentFactory m_factory;
    /**
     * The resource provider to use to provide resources
     * for all the components.
     */
    private ResourceProvider m_resourceProvider;
    /**
     * The component that is responsible for running components
     * through their lifecycle stages.
     */
    private LifecycleHelper m_lifecycleHelper;
    /**
     * The map of all entrys present in application.
     */
    private final ComponentStore m_store = new ComponentStore();
    /**
     * The {@link org.apache.avalon.phoenix.containerkit.kernel.processor.DependencyMap} via which dependency graph is
     * produced.
     */
    private final DependencyMap m_dependencyMap = new DependencyMap();
    /**
     * The resource provider to use to provide resources
     * for all the components.
     */
    private Map m_entrySet = new HashMap();

    //private PhaseProcessor m_processor;

    /**
     * Initialization of the kernel.  The implementation will request
     * resource provider preparation via the {@link #prepareResourceProvider}
     * method that must be implemeted by a kernel derived from this abstract
     * class.
     * @throws Exception if an error occurs during kernel initialization
     */
    public void initialize()
        throws Exception
    {
        m_factory = prepareFactory();
        m_resourceProvider = prepareResourceProvider();
        m_lifecycleHelper = prepareLifecycleHelper();
        //m_processor = new PhaseProcessor();
        //setupLogger( m_processor, "processor" );
    }

    /**
     * Dispose the kernel and de-allocate any resources.
     * This method will just shutdown and null out the
     * {@link ResourceProvider} and {@link LifecycleHelper}
     * objects.
     */
    public void dispose()
    {
        try
        {
            ContainerUtil.shutdown( m_resourceProvider );
        }
        catch( final Exception e )
        {
            final String message =
                REZ.getString( "provider-shutdown.error" );
            getLogger().warn( message, e );
        }
        try
        {
            ContainerUtil.shutdown( m_lifecycleHelper );
        }
        catch( Exception e )
        {
            final String message =
                REZ.getString( "lifecycle-shutdown.error" );
            getLogger().warn( message, e );
        }
    }

    protected abstract ComponentFactory prepareFactory();

    protected abstract ResourceProvider prepareResourceProvider();

    protected LifecycleHelper prepareLifecycleHelper()
    {
        final LifecycleHelper lifecycleHelper = new LifecycleHelper();
        setupLogger( lifecycleHelper, "lifecycle" );
        return lifecycleHelper;
    }

    /**
     * Request startup of all components.
     * @exception Exception if there is an error during startup
     */
    protected final void startupAllComponents()
        throws Exception
    {
        final ComponentProfile[] components = m_dependencyMap.getStartupGraph( m_store );
        processComponents( true, components );
    }

    /**
     * Request shutdown of all components.
     * @exception Exception if there is an error during shutdown
     */
    protected final void shutdownAllComponents()
        throws Exception
    {
        final ComponentProfile[] components = m_dependencyMap.getShutdownGraph( m_store );
        processComponents( false, components );
    }

    /**
     * Request startup of a named component.
     * @param name the name of the component to startup
     * @exception Exception if there is an error during component startup
     */
    protected final void startupComponent( final String name )
        throws Exception
    {
        final ComponentProfile entry = m_store.getComponent( name );
        final ComponentProfile[] components =
            m_dependencyMap.getProviderGraph( entry, m_store );
        processComponents( true, components );
    }

    /**
     * Request shutdown of a named component.
     * @param name the name of the component to shutdown
     * @exception Exception if there is an error during component shutdown
     */
    protected final void shutdownComponent( final String name )
        throws Exception
    {
        final ComponentProfile entry = m_store.getComponent( name );
        final ComponentProfile[] components =
            m_dependencyMap.getConsumerGraph( entry, m_store );
        processComponents( false, components );
    }

    /**
     * Return the {@link ComponentFactory} associated with kernel.
     *
     * @return the {@link ComponentFactory} associated with kernel.
     */
    protected final ComponentFactory getFactory()
    {
        return m_factory;
    }

    /**
     * Add a Component to the container.
     * This Must be called before any components are started
     * or else an exception is raised.
     *
     * @param component the component
     */
    protected final void addComponent( final ComponentMetaData component )
        throws Exception
    {
        final ComponentBundle bundle = m_factory.createBundle( component.getImplementationKey() );
        final ComponentInfo info = bundle.getComponentInfo();
        final ComponentProfile profile = new ComponentProfile( info, component );
        final ComponentEntry entry = new ComponentEntry( profile );
        m_entrySet.put( component.getName(), entry );
        m_store.addComponent( profile );
    }

    public final Object getComponent( final String name )
    {
        final ComponentEntry entry = (ComponentEntry)m_entrySet.get( name );
        if( null != entry )
        {
            return entry.getObject();
        }
        else
        {
            return null;
        }
    }

    /**
     * Process a whole assembly through a lifecycle phase
     * (ie startup or shutdown). The components should be processed
     * in order specified by the dependency graph.
     *
     * @param startup true if application startup phase, false if shutdown phase
     * @throws Exception if there is error processing any of the components
     *         through the phases
     */
    private void processComponents( final boolean startup,
                                    final ComponentProfile[] components )
        throws Exception
    {
        processComponentsNotice( components, startup );

        Exception exception = null;
        for( int i = 0; i < components.length; i++ )
        {
            try
            {
                processComponent( components[ i ], startup );
            }
            catch( final Exception e )
            {
                //during startup we should fail immediately
                //while during shutdown of component you need to shutdown
                //all components regardless of whether some of them don't
                //shutdown cleanly
                if( startup )
                {
                    throw e;
                }
                else
                {
                    exception = e;
                }
            }
        }

        if( null != exception )
        {
            throw exception;
        }
    }

    private void processComponent( final ComponentProfile component,
                                   final boolean startup )
        throws Exception
    {
        final String name = component.getMetaData().getName();
        final ComponentEntry entry =
            (ComponentEntry)m_entrySet.get( name );
        processComponent( entry, startup );
    }

    /**
     * Process a component through a lifecycle phase
     * (ie startup or shutdown). If it is startup phase then
     * it is expected that all the providers for the components
     * dependencies have been process. If it is the shutdown phase
     * it is expected that all of the consumers of services provided
     * by this component have already been shutdown.
     *
     * @param component the component
     * @param startup true if application startup phase, false if shutdown phase
     * @throws Exception if there is error processing any of the components
     *         through the phases
     */
    private void processComponent( final ComponentEntry component,
                                   final boolean startup )
        throws Exception
    {
        final String name = component.getProfile().getMetaData().getName();

        if( startup == component.isActive() )
        {
            //If component is already started and we said start
            //or the component is already stopped and we said stop
            //then skip it.
            return;
        }

        processComponentNotice( startup, name, false );

        try
        {
            if( startup )
            {
                final Object object =
                    m_lifecycleHelper.startup( name,
                                               component,
                                               m_resourceProvider );
                component.setObject( object );
            }
            else
            {
                final Object object = component.getObject();
                component.setObject( null );
                m_lifecycleHelper.shutdown( name, object );
            }
        }
        catch( final Exception e )
        {
            final String message =
                REZ.getString( "app.error.run-phase",
                               startup ? "0":"1",
                               name,
                               e.getMessage() );
            getLogger().error( message, e );
            throw e;
        }

        processComponentNotice( startup, name, true );
    }

    /**
     * Log message describing the number of components
     * the phase in and the order in which they will be
     * processed
     *
     * @param order the order the components will be processed in
     * @param startup true if application startup phase, false if shutdown phase
     */
    private void processComponentsNotice( final ComponentProfile[] order,
                                          final boolean startup )
    {
        if( getLogger().isInfoEnabled() )
        {
            final Integer count = new Integer( order.length );

            final List pathList = new ArrayList();
            for( int i = 0; i < order.length; i++ )
            {
                ComponentProfile componentEntry = order[ i ];
                pathList.add( componentEntry.getMetaData().getName() );
            }

            final String message =
                REZ.getString( "components-processing",
                               count,
                               startup ? "0":"1",
                               pathList );
            getLogger().info( message );
        }
    }

    /**
     * Log processing of component.
     *
     * @param name the name of component processing
     * @param startup true if application startup phase, false if shutdown phase
     */
    private void processComponentNotice( final boolean startup,
                                         final String name,
                                         final boolean completed )
    {
        if( getLogger().isDebugEnabled() )
        {
            final String key =
                completed ? "processed-component": "process-component";
            final String message =
                REZ.getString( key,
                               startup ? "0":"1",
                               name );
            getLogger().debug( message );
        }
    }
}
