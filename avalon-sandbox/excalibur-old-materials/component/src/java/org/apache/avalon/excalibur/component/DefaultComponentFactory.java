/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.component;

import java.util.ArrayList;
import java.util.Collection;
import org.apache.avalon.excalibur.collections.BucketMap;
import org.apache.avalon.excalibur.logger.LogKitManageable;
import org.apache.avalon.excalibur.pool.ObjectFactory;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Loggable;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.WrapperServiceManager;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.excalibur.container.legacy.ComponentProxyGenerator;
import org.apache.excalibur.instrument.InstrumentManageable;
import org.apache.excalibur.instrument.InstrumentManager;
import org.apache.excalibur.instrument.Instrumentable;

/**
 * Factory for Avalon components.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:paul@luminas.co.uk">Paul Russell</a>
 * @author <a href="mailto:ryan@silveregg.co.jp">Ryan Shaw</a>
 * @author <a href="mailto:leif@apache.org">Leif Mortenson</a>
 * @version CVS $Revision: 1.13 $ $Date: 2002/11/07 12:45:23 $
 * @since 4.0
 */
public class DefaultComponentFactory
    extends AbstractDualLogEnabled
    implements ObjectFactory, Disposable, ThreadSafe
{
    /** The class which this <code>ComponentFactory</code>
     * should create.
     */
    private Class m_componentClass;

    /** The Context for the component
     */
    private Context m_context;

    /** The component manager for this component.
     */
    private ComponentManager m_componentManager;

    /** The configuration for this component.
     */
    private Configuration m_configuration;

    /** The RoleManager for child ComponentSelectors
     */
    private RoleManager m_roles;

    /** The LogkitLoggerManager for child ComponentSelectors
     */
    private LogkitLoggerManager m_loggerManager;

    /** Components created by this factory, and their associated ComponentLocator
     *  proxies, if they are Composables.
     */
    private final BucketMap m_components = new BucketMap();

    /** Instrument Manager to register objects created by this factory with (May be null). */
    private InstrumentManager m_instrumentManager;

    /** Instrumentable Name assigned to objects created by this factory. */
    private String m_instrumentableName;

    private ComponentProxyGenerator m_proxyGenerator;
    private String m_role;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Construct a new component factory for the specified component.
     *
     * @param componentClass the class to instantiate (must have a default constructor).
     * @param configuration the <code>Configuration</code> object to pass to new instances.
     * @param componentManager the component manager to pass to <code>Composable</code>s.
     * @param context the <code>Context</code> to pass to <code>Contexutalizable</code>s.
     * @param roles the <code>RoleManager</code> to pass to <code>DefaultComponentSelector</code>s.
     *
     * @deprecated This constructor has been deprecated in favor of the version below which
     *             handles instrumentation.
     */
    public DefaultComponentFactory( final String role,
                                    final Class componentClass,
                                    final Configuration configuration,
                                    final ComponentManager componentManager,
                                    final Context context,
                                    final RoleManager roles,
                                    final LogkitLoggerManager loggerManager )
    {
        this( role,
              componentClass,
              configuration,
              componentManager,
              context,
              roles,
              loggerManager,
              null,
              "N/A" );
    }

    /**
     * Construct a new component factory for the specified component.
     *
     * @param componentClass the class to instantiate (must have a default constructor).
     * @param configuration the <code>Configuration</code> object to pass to new instances.
     * @param componentManager the component manager to pass to <code>Composable</code>s.
     * @param context the <code>Context</code> to pass to <code>Contexutalizable</code>s.
     * @param roles the <code>RoleManager</code> to pass to
     *              <code>DefaultComponentSelector</code>s.
     * @param instrumentManager the <code>InstrumentManager</code> to register the component
     *                          with if it is a Instrumentable (May be null).
     * @param instrumentableName The instrument name to assign the component if
     *                           it is Instrumentable.
     */
    public DefaultComponentFactory( final String role,
                                    final Class componentClass,
                                    final Configuration configuration,
                                    final ComponentManager componentManager,
                                    final Context context,
                                    final RoleManager roles,
                                    final LogkitLoggerManager loggerManager,
                                    final InstrumentManager instrumentManager,
                                    final String instrumentableName )

    {
        m_role = role;
        m_componentClass = componentClass;
        m_configuration = configuration;
        m_componentManager = componentManager;
        m_context = context;
        m_roles = roles;
        m_loggerManager = loggerManager;
        m_instrumentManager = instrumentManager;
        m_instrumentableName = instrumentableName;
        m_proxyGenerator = new ComponentProxyGenerator( m_componentClass.getClassLoader() );
    }

    /*---------------------------------------------------------------
     * ObjectFactory Methods
     *-------------------------------------------------------------*/
    public Object newInstance()
        throws Exception
    {
        final Object component = m_componentClass.newInstance();

        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "ComponentFactory creating new instance of " +
                               m_componentClass.getName() + "." );
        }

        if( component instanceof LogEnabled )
        {
            if( null == m_loggerManager || null == m_configuration )
            {
                ContainerUtil.enableLogging( component, getLogger() );
            }
            else
            {
                final String logger = m_configuration.getAttribute( "logger", null );
                if( null == logger )
                {
                    getLogger().debug( "no logger attribute available, using standard logger" );
                    ContainerUtil.enableLogging( component, getLogger() );
                }
                else
                {
                    getLogger().debug( "logger attribute is " + logger );
                    ContainerUtil.enableLogging( component, m_loggerManager.getLoggerForCategory( logger ) );
                }
            }
        }

        if( component instanceof Loggable )
        {
            if( null == m_loggerManager || null == m_configuration )
            {
                ( (Loggable)component ).setLogger( getLogkitLogger() );
            }
            else
            {
                final String logger = m_configuration.getAttribute( "logger", null );
                if( null == logger )
                {
                    getLogger().debug( "no logger attribute available, using standard logger" );
                    ( (Loggable)component ).setLogger( getLogkitLogger() );
                }
                else
                {
                    getLogger().debug( "logger attribute is " + logger );
                    ( (Loggable)component ).setLogger( m_loggerManager.getLogKitLoggerForCategory( logger ) );
                }
            }
        }

        // Set the name of the instrumentable before initialization.
        if( component instanceof Instrumentable )
        {
            Instrumentable instrumentable = (Instrumentable)component;
            instrumentable.setInstrumentableName( m_instrumentableName );
        }

        if( ( component instanceof InstrumentManageable ) && ( m_instrumentManager != null ) )
        {
            ( (InstrumentManageable)component ).setInstrumentManager( m_instrumentManager );
        }

        if( component instanceof Contextualizable )
        {
            ContainerUtil.contextualize( component, m_context );
        }

        Object proxy = null;

        if( component instanceof Composable )
        {
            // wrap the real CM with a proxy, see below for more info
            final ComponentManagerProxy manager =
                new ComponentManagerProxy( m_componentManager );
            ContainerUtil.compose( component, manager );
            proxy = manager;
        }

        if( component instanceof Serviceable )
        {
            // Wrap the real CM with a proxy, see below for more info
            final ServiceManagerProxy manager =
                new ServiceManagerProxy( m_componentManager );
            ContainerUtil.service( component, manager );
            proxy = manager;
        }

        if( component instanceof RoleManageable )
        {
            ( (RoleManageable)component ).setRoleManager( m_roles );
        }

        if( component instanceof LogKitManageable )
        {
            ( (LogKitManageable)component ).setLogKitManager( m_loggerManager.getLogKitManager() );
        }

        ContainerUtil.configure( component, m_configuration );

        if( component instanceof Parameterizable )
        {
            final Parameters parameters = Parameters.fromConfiguration( m_configuration );
            ContainerUtil.parameterize( component, parameters );
        }

        ContainerUtil.initialize( component );

        // Register the component as an instrumentable now that it has been initialized.
        if( component instanceof Instrumentable )
        {
            // Instrumentable Name is set above.
            if( m_instrumentManager != null )
            {
                m_instrumentManager.registerInstrumentable(
                    (Instrumentable)component, m_instrumentableName );
            }
        }
        ContainerUtil.start( component );

        m_components.put( component, proxy );

        // If the component is not an instance of Component then wrap it in a proxy.
        //  This makes it possible to use components which are not real Components
        //  with the ECM.
        Component returnableComponent;
        if( !( component instanceof Component ) )
        {
            returnableComponent = m_proxyGenerator.getProxy( m_role, component );
            m_components.put( returnableComponent, component );
        }
        else
        {
            returnableComponent = (Component)component;
        }

        return returnableComponent;
    }

    public final Class getCreatedClass()
    {
        return m_componentClass;
    }

    public final void decommission( final Object component )
        throws Exception
    {
        Object check = m_components.get( component );
        Object decommission;
        if( check instanceof ServiceManager || check instanceof ComponentManager || null == check )
        {
            decommission = component;
        }
        else
        {
            decommission = check;
            m_components.remove( component );
        }

        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "ComponentFactory decommissioning instance of " +
                               m_componentClass.getName() + "." );
        }

        ContainerUtil.stop( component );
        ContainerUtil.dispose( component );

        if( decommission instanceof Composable )
        {
            // ensure any components looked up by this Composable are properly
            // released, if they haven't been released already
            ( (ComponentManagerProxy)m_components.get( decommission ) ).releaseAll();
        }

        if( decommission instanceof Serviceable )
        {
            ( (ServiceManagerProxy)m_components.get( decommission ) ).releaseAll();
        }

        m_components.remove( decommission );
    }

    /*---------------------------------------------------------------
     * Disposable Methods
     *-------------------------------------------------------------*/
    public final void dispose()
    {
        Component[] components = new Component[ m_components.keySet().size() ];

        m_components.keySet().toArray( components );

        for( int i = 0; i < components.length; i++ )
        {
            try
            {
                decommission( components[ i ] );
            }
            catch( final Exception e )
            {
                if( getLogger().isWarnEnabled() )
                {
                    getLogger().warn( "Error decommissioning component: " +
                                      getCreatedClass().getName(), e );
                }
            }
        }
    }

    /*---------------------------------------------------------------
     * ThreadSafe Methods
     *-------------------------------------------------------------*/
    // No methods

    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Proxy <code>ComponentManager</code> class to maintain references to
     * components looked up within a <code>Composable</code> instance created
     * by this factory.
     *
     * This class acts a safety net to ensure that all components looked
     * up within a <code>Composable</code> instance created by this factory are
     * released when the instance itself is released.
     */
    private static class ComponentManagerProxy implements ComponentManager
    {
        private final ComponentManager m_realManager;
        private final Collection m_unreleased = new ArrayList();

        ComponentManagerProxy( ComponentManager manager )
        {
            m_realManager = manager;
        }

        public Component lookup( String role ) throws ComponentException
        {
            Component component = m_realManager.lookup( role );

            addUnreleased( component );

            return component;
        }

        public boolean hasComponent( String role )
        {
            return m_realManager.hasComponent( role );
        }

        public void release( Component component )
        {
            removeUnreleased( component );

            m_realManager.release( component );
        }

        private synchronized void addUnreleased( Component component )
        {
            m_unreleased.add( component );
        }

        private synchronized void removeUnreleased( Component component )
        {
            m_unreleased.remove( component );
        }

        /**
         * Releases all components that have been looked up through this
         * <code>ComponentManager</code>, that have not yet been released
         * via user code.
         */
        private void releaseAll()
        {
            Component[] unreleased;

            synchronized( this )
            {
                unreleased = new Component[ m_unreleased.size() ];
                m_unreleased.toArray( unreleased );
            }

            for( int i = 0; i < unreleased.length; i++ )
            {
                release( unreleased[ i ] );
            }
        }
    }

    /**
     * Proxy <code>ServiceManager</code> class to maintain references to
     * components looked up within a <code>Serviceable</code> instance created
     * by this factory.
     * <p>
     * Extends the WrapperServiceManager class to avoid duplicating
     * code and decrease the chance of making errors.
     *
     * This class acts a safety net to ensure that all components looked
     * up within a <code>Serviceable</code> instance created by this factory are
     * released when the instance itself is released.
     */
    private static class ServiceManagerProxy
        extends WrapperServiceManager
    {
        private final ComponentManager m_realManager;
        private final Collection m_unreleased = new ArrayList();

        ServiceManagerProxy( final ComponentManager manager )
        {
            super( manager );
            m_realManager = manager;
        }

        public Object lookup( final String role )
            throws ServiceException
        {
            final Object component = super.lookup( role );
            addUnreleased( component );
            return component;
        }

        public void release( final Object component )
        {
            removeUnreleased( component );
            super.release( component );
        }

        private synchronized void addUnreleased( final Object component )
        {
            m_unreleased.add( component );
        }

        private synchronized void removeUnreleased( final Object component )
        {
            m_unreleased.remove( component );
        }

        /**
         * Releases all components that have been looked up through this
         * <code>ServiceManager</code>, that have not yet been released
         * via user code.
         */
        private void releaseAll()
        {
            Object[] unreleased;

            synchronized( this )
            {
                unreleased = new Object[ m_unreleased.size() ];
                m_unreleased.toArray( unreleased );
            }

            for( int i = 0; i < unreleased.length; i++ )
            {
                release( unreleased[ i ] );
            }
        }
    }
}
