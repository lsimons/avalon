/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"
    must not be used to endorse or promote products derived from this  software
    without  prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/
package org.apache.avalon.excalibur.component;

import org.apache.avalon.excalibur.logger.LogKitManageable;
import org.apache.avalon.excalibur.pool.ObjectFactory;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Loggable;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.WrapperServiceManager;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.commons.collections.StaticBucketMap;
import org.apache.excalibur.instrument.InstrumentManageable;
import org.apache.excalibur.instrument.InstrumentManager;
import org.apache.excalibur.instrument.Instrumentable;

/**
 * Factory for Avalon components.
 *
 * @deprecated ECM is no longer supported
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:paul@luminas.co.uk">Paul Russell</a>
 * @author <a href="mailto:ryan@silveregg.co.jp">Ryan Shaw</a>
 * @author <a href="mailto:leif@apache.org">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 12:45:26 $
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

    /** The service manager for this component
     */
    private WrapperServiceManager m_serviceManager;
    
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
     *  proxies, if they are Composables.  These must be seperate maps in case
     *  a component falls into more than one category, which they often do.
     */
    private final StaticBucketMap m_componentProxies = new StaticBucketMap();

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
        m_serviceManager = new WrapperServiceManager( m_componentManager );
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

        else if( component instanceof Loggable )
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

        if( component instanceof Composable )
        {
            ContainerUtil.compose( component, m_componentManager );
        }

        if( component instanceof Serviceable )
        {
            ContainerUtil.service( component, m_serviceManager );

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

        // If the component is not an instance of Component then wrap it in a proxy.
        //  This makes it possible to use components which are not real Components
        //  with the ECM.  We need to remember to unwrap this when the component is
        //  decommissioned.
    //
    // note that ComponentHandler depends on this specific
    // component instanceof Component check to be made
        Component returnableComponent;
        if( !( component instanceof Component ) )
        {
            returnableComponent = m_proxyGenerator.getCompatibleProxy( component );
            m_componentProxies.put( returnableComponent, component );
        }
        else
        {
            returnableComponent = (Component)component;
        }

        return returnableComponent;
    }

    public Class getCreatedClass()
    {
        return m_componentClass;
    }

    public void decommission( final Object component )
        throws Exception
    {
        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "ComponentFactory decommissioning instance of " +
                               m_componentClass.getName() + "." );
        }

        // See if we need to unwrap this component.  It may have been wrapped in a proxy
        //  by the ProxyGenerator.
        Object decommissionComponent = m_componentProxies.remove( component );
        if ( null == decommissionComponent )
        {
            // It was not wrapped.
            decommissionComponent = component;
        }

        ContainerUtil.stop( decommissionComponent );
        ContainerUtil.dispose( decommissionComponent );

        /*if ( decommissionComponent instanceof Composable )
        {
            // A proxy will have been created.  Ensure that components created by it
            //  are also released.
            ((ComponentManagerProxy)m_composableProxies.remove( decommissionComponent )).
                releaseAll();
        }

        if ( decommissionComponent instanceof Serviceable )
        {
            // A proxy will have been created.  Ensure that components created by it
            //  are also released.
            ((ServiceManagerProxy)m_serviceableProxies.remove( decommissionComponent )).
                releaseAll();
        }*/
    }

    /*---------------------------------------------------------------
     * Disposable Methods
     *-------------------------------------------------------------*/
    public void dispose()
    {
    }

}
