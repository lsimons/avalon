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
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 * Factory for Avalon components.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:paul@luminas.co.uk">Paul Russell</a>
 * @author <a href="mailto:ryan@silveregg.co.jp">Ryan Shaw</a>
 * @version CVS $Revision: 1.4 $ $Date: 2002/06/13 17:24:50 $
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
    private LogkitLoggerManager m_logkit;

    /** Components created by this factory, and their associated ComponentLocator
     *  proxies, if they are Composables.
     */
    private final BucketMap m_components = new BucketMap();

    /**
     * Construct a new component factory for the specified component.
     *
     * @param componentClass the class to instantiate (must have a default constructor).
     * @param configuration the <code>Configuration</code> object to pass to new instances.
     * @param componentManager the component manager to pass to <code>Composable</code>s.
     * @param context the <code>Context</code> to pass to <code>Contexutalizable</code>s.
     * @param roles the <code>RoleManager</code> to pass to <code>DefaultComponentSelector</code>s.
     */
    public DefaultComponentFactory( final Class componentClass,
                                    final Configuration configuration,
                                    final ComponentManager componentManager,
                                    final Context context,
                                    final RoleManager roles,
                                    final LogkitLoggerManager logkit )
    {
        m_componentClass = componentClass;
        m_configuration = configuration;
        m_componentManager = componentManager;
        m_context = context;
        m_roles = roles;
        m_logkit = logkit;
    }

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
            if( null == m_logkit || null == m_configuration )
            {
                ( (LogEnabled)component ).enableLogging( getLogger() );
            }
            else
            {
                final String logger = m_configuration.getAttribute( "logger", null );
                if( null == logger )
                {
                    getLogger().debug( "no logger attribute available, using standard logger" );
                    ( (LogEnabled)component ).enableLogging( getLogger() );
                }
                else
                {
                    getLogger().debug( "logger attribute is " + logger );
                    ( (LogEnabled)component ).enableLogging( m_logkit.getLoggerForCategory( logger ) );
                }
            }
        }

        if( component instanceof Loggable )
        {
            if( null == m_logkit || null == m_configuration )
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
                    ( (Loggable)component ).setLogger( m_logkit.getLogKitLoggerForCategory( logger ) );
                }
            }
        }

        // This was added to make it possible to implement a ProfilerComponentFactory without
        //  code duplication.  Once the issues there are worked out, this will most likely be
        //  removed as it is rather hackish.
        postLogger( component, m_configuration );

        if( component instanceof Contextualizable )
        {
            ( (Contextualizable)component ).contextualize( m_context );
        }

        ComponentManager proxy = null;

        if( component instanceof Composable )
        {
            // wrap the real CM with a proxy, see below for more info
            proxy = new ComponentManagerProxy( m_componentManager );
            ( (Composable)component ).compose( proxy );
        }

        if( component instanceof RoleManageable )
        {
            ( (RoleManageable)component ).setRoleManager( m_roles );
        }

        if( component instanceof LogKitManageable )
        {
            ( (LogKitManageable)component ).setLogKitManager( m_logkit.getLogKitManager() );
        }

        if( component instanceof Configurable )
        {
            ( (Configurable)component ).configure( m_configuration );
        }

        if( component instanceof Parameterizable )
        {
            ( (Parameterizable)component ).
                parameterize( Parameters.fromConfiguration( m_configuration ) );
        }

        if( component instanceof Initializable )
        {
            ( (Initializable)component ).initialize();
        }

        // This was added to make it possible to implement a ProfilerComponentFactory without
        //  code duplication.  Once the issues there are worked out, this will most likely be
        //  removed as it is rather hackish.
        postInitialize( component, m_configuration );

        if( component instanceof Startable )
        {
            ( (Startable)component ).start();
        }

        m_components.put( component, proxy );

        return component;
    }

    /**
     * Called after a new component is initialized, but before it is started.  This was added
     *  to make it possible to implement the ProfilerComponentFactory without too much duplicate
     *  code.  WARNING:  Do not take advantage of this method as it will most likely be removed.
     */
    protected void postLogger( Object component, Configuration configuration )
        throws Exception
    {
        // Do nothing in this version.
    }

    /**
     * Called after a new component is initialized, but before it is started.  This was added
     *  to make it possible to implement the ProfilerComponentFactory without too much duplicate
     *  code.  WARNING:  Do not take advantage of this method as it will most likely be removed.
     */
    protected void postInitialize( Object component, Configuration configuration )
        throws Exception
    {
        // Do nothing in this version.
    }

    public final Class getCreatedClass()
    {
        return m_componentClass;
    }

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

    public final void decommission( final Object component )
        throws Exception
    {
        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "ComponentFactory decommissioning instance of " +
                               m_componentClass.getName() + "." );
        }

        if( component instanceof Startable )
        {
            ( (Startable)component ).stop();
        }

        if( component instanceof Disposable )
        {
            ( (Disposable)component ).dispose();
        }

        if( component instanceof Composable )
        {
            // ensure any components looked up by this Composable are properly
            // released, if they haven't been released already
            ( (ComponentManagerProxy)m_components.get( component ) ).releaseAll();
        }

        m_components.remove( component );
    }

    /**
     * Proxy <code>ComponentLocator</code> class to maintain references to
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
         * <code>ComponentLocator</code>, that have not yet been released
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
}
