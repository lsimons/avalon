/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.component;

import org.apache.avalon.activity.Startable;
import org.apache.avalon.activity.Disposable;
import org.apache.avalon.activity.Initializable;
import org.apache.avalon.component.ComponentManager;
import org.apache.avalon.component.Composable;
import org.apache.avalon.configuration.Configurable;
import org.apache.avalon.configuration.Configuration;
import org.apache.avalon.context.Context;
import org.apache.avalon.context.Contextualizable;
import org.apache.avalon.logger.AbstractLoggable;
import org.apache.avalon.logger.Loggable;
import org.apache.avalon.thread.ThreadSafe;
import org.apache.excalibur.pool.ObjectFactory;
import org.apache.excalibur.pool.Pool;
import org.apache.excalibur.pool.Poolable;

/**
 * Factory for Avalon components.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:paul@luminas.co.uk">Paul Russell</a>
 * @version CVS $Revision: 1.3 $ $Date: 2001/04/25 17:34:28 $
 */
public class DefaultComponentFactory
    extends AbstractLoggable
    implements ObjectFactory, ThreadSafe
{
    /** The class which this <code>ComponentFactory</code>
     * should create.
     */
    private Class                   m_componentClass;

    /** The Context for the component
     */
    private Context                 m_context;

    /** The component manager for this component.
     */
    private ComponentManager        m_componentManager;

    /** The configuration for this component.
     */
    private Configuration           m_configuration;

    /** The RoleManager for child ComponentSelectors
     */
    private RoleManager             m_roles;

    /** Construct a new component factory for the specified component.
     * @param componentClass the class to instantiate (must have a default constructor).
     * @param configuration the <code>Configuration</code> object to pass to new instances.
     * @param componentManager the component manager to pass to <code>Composable</code>s.
     */
    public DefaultComponentFactory( final Class componentClass,
                                    final Configuration configuration,
                                    final ComponentManager componentManager,
                                    final Context context,
                                    final RoleManager roles )
    {
        m_componentClass = componentClass;
        m_configuration = configuration;
        m_componentManager = componentManager;
        m_context = context;
        m_roles = roles;
    }

    public Object newInstance()
        throws Exception
    {
        final Object component = m_componentClass.newInstance();

        getLogger().debug( "ComponentFactory creating new instance of " +
                           m_componentClass.getName() + "." );

        if( component instanceof Loggable )
        {
            ((Loggable)component).setLogger( getLogger() );
        }

        if( component instanceof Contextualizable )
        {
            ((Contextualizable)component).contextualize( m_context );
        }

        if( component instanceof Composable )
        {
            ((Composable)component).compose( m_componentManager );
        }

        if ( component instanceof DefaultComponentSelector )
        {
            ((DefaultComponentSelector)component).setRoleManager( m_roles );
        }

        if( component instanceof Configurable )
        {
            ((Configurable)component).configure( m_configuration );
        }

        if( component instanceof Initializable )
        {
            ((Initializable)component).initialize();
        }

        if( component instanceof Startable )
        {
            ((Startable)component).start();
        }

        return component;
    }

    public final Class getCreatedClass()
    {
        return m_componentClass;
    }

    public final void decommission( final Object component )
        throws Exception
    {
        getLogger().debug( "ComponentFactory decommissioning instance of " +
                           m_componentClass.getName() + "." );

        if( component instanceof Startable )
        {
            ((Startable)component).stop();
        }

        if( component instanceof Disposable )
        {
            ((Disposable)component).dispose();
        }
    }
}
