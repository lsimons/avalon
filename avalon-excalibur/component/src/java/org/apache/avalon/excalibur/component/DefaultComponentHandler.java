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

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.Context;

/**
 * The DefaultComponentHandler to make sure components are initialized
 * and destroyed correctly.
 *
 * @deprecated ECM is no longer supported
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:ryan@silveregg.co.jp">Ryan Shaw</a>
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 12:45:26 $
 * @since 4.0
 */
public class DefaultComponentHandler
    extends ComponentHandler
{
    /** The instance of the ComponentFactory that creates and disposes of the Component */
    private final DefaultComponentFactory m_factory;

    /** State management boolean stating whether the Handler is initialized or not */
    private boolean m_initialized;

    /** State management boolean stating whether the Handler is disposed or not */
    private boolean m_disposed;

    /**
     * Create a ComponentHandler that takes care of hiding the details of
     * whether a Component is ThreadSafe, Poolable, or SingleThreaded.
     * It falls back to SingleThreaded if not specified.
     *
     * @param componentClass Class of the component of the handler being
     *                       created.
     * @param config The configuration for the component.
     * @param manager The ComponentManager which will be managing the
     *                Component.
     * @param context The current context.
     * @param roles The current RoleManager.
     * @param logkit The current LogKitLoggerManager.
     *
     * @throws Exception If there are any problems creating the handler.
     */
    protected DefaultComponentHandler( final String role,
                                       final Class componentClass,
                                       final Configuration config,
                                       final ComponentManager manager,
                                       final Context context,
                                       final RoleManager roles,
                                       final LogkitLoggerManager logkit )
        throws Exception
    {
        this(
            new DefaultComponentFactory(role, componentClass, config, manager, context, roles, logkit ),
            config );
    }

    /**
     * Create a DefaultComponentHandler which manages a pool of Components
     *  created by the specified factory object.
     *
     * @param factory The factory object which is responsible for creating the components
     *                managed by the ComponentHandler.
     * @param config The configuration to use to configure the pool.
     *
     * @throws Exception If there are any problems creating the handler.
     */
    public DefaultComponentHandler( final DefaultComponentFactory factory,
                                    final Configuration config )
        throws Exception
    {
        m_factory = factory;
    }

    /**
     * Initialize the ComponentHandler.
     */
    public void initialize()
    {
        if( m_initialized )
        {
            return;
        }
        m_factory.setLogger( getLogkitLogger() );
        m_factory.enableLogging( getLogger() );

        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "ComponentHandler initialized for: "
                + m_factory.getCreatedClass().getName() );
        }
        m_initialized = true;
    }

    /**
     * Get a reference of the desired Component
     *
     * @return A component instance.
     *
     * @throws Exception If there are any problems encountered acquiring a
     *                   component instance.
     */
    protected Component doGet()
        throws Exception
    {
        if( !m_initialized )
        {
            throw new IllegalStateException(
                "You cannot get a component from an uninitialized holder." );
        }

        if( m_disposed )
        {
            throw new IllegalStateException( "You cannot get a component from a disposed holder" );
        }

        return (Component)m_factory.newInstance();
    }

    /**
     * Return a reference of the desired Component
     *
     * @param component Component to be be put/released back to the handler.
     */
    protected void doPut( final Component component )
    {
        if( !m_initialized )
        {
            throw new IllegalStateException(
                "You cannot put a component in an uninitialized holder." );
        }

        try
        {
            m_factory.decommission( component );
        }
        catch( final Exception e )
        {
            if( getLogger().isWarnEnabled() )
            {
                getLogger().warn( "Error decommissioning component: "
                    + m_factory.getCreatedClass().getName(), e );
            }
        }
    }

    /**
     * Dispose of the ComponentHandler and any associated Pools and Factories.
     */
    public void dispose()
    {
        try
        {
            ContainerUtil.dispose( m_factory );
        }
        catch( final Exception e )
        {
            if( getLogger().isWarnEnabled() )
            {
                getLogger().warn( "Error decommissioning component: "
                    + m_factory.getCreatedClass().getName(), e );
            }
        }

        m_disposed = true;
    }
}