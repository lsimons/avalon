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
package org.apache.avalon.fortress.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.avalon.excalibur.logger.LoggerManager;
import org.apache.avalon.fortress.Container;
import org.apache.avalon.fortress.RoleEntry;
import org.apache.avalon.fortress.RoleManager;
import org.apache.avalon.fortress.impl.extensions.InstrumentableCreator;
import org.apache.avalon.fortress.impl.handler.ComponentFactory;
import org.apache.avalon.fortress.impl.handler.ComponentHandler;
import org.apache.avalon.fortress.impl.handler.LEAwareComponentHandler;
import org.apache.avalon.fortress.impl.handler.PrepareHandlerCommand;
import org.apache.avalon.fortress.impl.handler.ProxyObjectFactory;
import org.apache.avalon.fortress.impl.lookup.FortressServiceManager;
import org.apache.avalon.fortress.impl.lookup.FortressServiceSelector;
import org.apache.avalon.fortress.impl.role.FortressRoleManager;
import org.apache.avalon.fortress.util.CompositeException;
import org.apache.avalon.fortress.util.LifecycleExtensionManager;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.DefaultServiceManager;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.commons.collections.BoundedFifoBuffer;
import org.apache.commons.collections.StaticBucketMap;
import org.apache.excalibur.event.Queue;
import org.apache.excalibur.instrument.InstrumentManager;
import org.apache.excalibur.instrument.Instrumentable;
import org.apache.excalibur.mpool.ObjectFactory;
import org.apache.excalibur.mpool.PoolManager;

/**
 * This abstract implementation provides basic functionality for building
 * an implementation of the {@link Container} interface.
 * It exposes a protected getServiceManager() method so that the
 * Container's Manager can expose that to the instantiating class.
 *
 * @author <a href="mailto:dev@avalon.apache.org">The Avalon Team</a>
 * @version CVS $Revision: 1.15 $ $Date: 2003/03/22 11:29:09 $
 */
public abstract class AbstractContainer
    extends AbstractLogEnabled
    implements Contextualizable, Serviceable, Initializable, Disposable, Container
{
    /** The hint map's entry to get the default component type */
    public static final String DEFAULT_ENTRY = "*";
    /** The component map's entry to get a ServiceSelector */
    public static final String SELECTOR_ENTRY = "$";

    /** contains the impl's context passed in through contextualize() */
    protected Context m_context;
    /** contains the ServiceManager the impl will use, based on the one passed in through service() */
    protected ServiceManager m_serviceManager;
    /** contains the impl's LoggerManager, which is extracted from m_serviceManager */
    protected LoggerManager m_loggerManager;
    /** contains the impl's PoolManager, which is extracted from m_serviceManager */
    protected PoolManager m_poolManager;
    /** contains the impl's Queue, which is extracted from m_serviceManager */
    protected Queue m_commandQueue;
    /** contains the impl's root ClassLoader, which is extracted from m_serviceManager */
    protected ClassLoader m_classLoader;
    /** contains the impl's RoleManager, which is extracted from m_serviceManager */
    protected RoleManager m_roleManager;
    /** contains the impl's InstrumentManager, which is extracted from m_serviceManager */
    protected InstrumentManager m_instrumentManager;
    /** contains the impl's LifecycleExtensionManager, which is extracted from m_serviceManager */
    protected LifecycleExtensionManager m_extManager;
    /**
     * Contains entries mapping roles to hint maps, where the hint map contains
     * mappings from hints to ComponentHandlers.
     */
    protected StaticBucketMap m_mapper = new StaticBucketMap();
    /** Contains an entry for each ComponentHandler */
    protected List m_components = new ArrayList( 10 );

    /**
     * Pull the manager items from the context so we can use them to set up
     * the system.
     * @param context the impl context
     * @exception ContextException if a contexaulization error occurs
     * @avalon.context type="ClassLoader" optional="true"
     */
    public void contextualize( final Context context )
        throws ContextException
    {
        m_context = context;
        try
        {
            m_classLoader = (ClassLoader)context.get( ClassLoader.class.getName() );
        }
        catch( ContextException ce )
        {
            m_classLoader = Thread.currentThread().getContextClassLoader();
        }
    }

    /**
     * Root ServiceManager.  The Container may choose to have it's
     * ServiceManager delegate to the root manager, or it may choose to be
     * entirely self contained.
     *
     * @param serviceManager the service manager to apply to the impl
     * @exception ServiceException is a servicing related error occurs
     *
     * @avalon.dependency type="LoggerManager"
     * @avalon.dependency type="PoolManager"
     * @avalon.dependency type="InstrumentManager"
     * @avalon.dependency type="LifecycleExtensionManager" optional="true"
     * @avalon.dependency type="RoleManager" optional="true"
     * @avalon.dependency type="Queue" optional="true"
     */
    public void service( final ServiceManager serviceManager )
        throws ServiceException
    {
        // get non-optional services

        m_loggerManager = (LoggerManager)serviceManager.lookup( LoggerManager.ROLE );
        m_poolManager = (PoolManager)serviceManager.lookup( PoolManager.ROLE );
        m_instrumentManager = (InstrumentManager)serviceManager.lookup( InstrumentManager.ROLE );

        // get optional services, or a default if the service isn't provided

        if( serviceManager.hasService( LifecycleExtensionManager.ROLE ) )
        {
            m_extManager =
                (LifecycleExtensionManager)serviceManager.lookup( LifecycleExtensionManager.ROLE );
        }
        else
        {
            m_extManager = new LifecycleExtensionManager();
            m_extManager.enableLogging( getLogger() );

            if( getLogger().isDebugEnabled() )
            {
                final String message =
                    "No Container.LIFECYCLE_EXTENSION_MANAGER is given, " +
                    "installing default lifecycle extension manager with " +
                    "1 extensions";
                getLogger().debug( message );
            }
        }

        /* Add all the standard extensions if they have not already been
         * done.
         */
        boolean isInstrumentEnabled = false;
        Iterator it = m_extManager.creatorExtensionsIterator();
        while( it.hasNext() )
        {
            if( it.next() instanceof InstrumentableCreator )
            {
                isInstrumentEnabled = true;
            }
        }

        if( !isInstrumentEnabled )
        {
            m_extManager.addCreatorExtension( new InstrumentableCreator( m_instrumentManager ) );
        }

        if( serviceManager.hasService( Queue.ROLE ) )
        {
            m_commandQueue = (Queue)serviceManager.lookup( Queue.ROLE );
        }
        else
        {
            final String message =
                "No Container.COMMAND_QUEUE is given, all " +
                "management will be performed synchronously";
            getLogger().warn( message );
        }

        if( serviceManager.hasService( RoleManager.ROLE ) )
        {
            m_roleManager = (RoleManager)serviceManager.lookup( RoleManager.ROLE );
        }
        else
        {
            try
            {
                m_roleManager = createDefaultRoleManager();
            }
            catch( final Exception e )
            {
                final String message = "Unable to create default role manager";
                throw new ServiceException( RoleManager.ROLE, message, e );
            }
        }

        // set up our ServiceManager
        m_serviceManager = new FortressServiceManager( this, serviceManager );
    }

    /**
     * Create a default RoleManager that can be used to addRole system.
     *
     * @return the default role manager
     * @throws Exception if unable to create role manager
     */
    private FortressRoleManager createDefaultRoleManager()
        throws Exception
    {
        final FortressRoleManager roleManager =
            new FortressRoleManager( null, m_classLoader );
        ContainerUtil.enableLogging( roleManager, getLogger().getChildLogger( "roles" ) );
        ContainerUtil.initialize( roleManager );
        return roleManager;
    }

    /**
     * Add a Component into the impl. This sets the component up for management
     * by the impl by creating an appropriate {@link ComponentHandler}.
     *
     * @param metaData the information needed to construct a ComponentHandler for the component
     * @throws IllegalArgumentException if the classname defined by the meta data
     *   argument is undefined within the scope of the role manager
     * @throws Exception if unable to create a Handler for the component
     */
    protected void addComponent( final ComponentHandlerMetaData metaData )
        throws IllegalArgumentException, Exception
    {
        // figure out Role
        final String classname = metaData.getClassname();
        final RoleEntry roleEntry = m_roleManager.getRoleForClassname( classname );
        if( null == roleEntry )
        {
            final String message = "No role defined for " + classname;
            throw new IllegalArgumentException( message );
        }

        if( DEFAULT_ENTRY.equals( metaData.getName() ) ||
            SELECTOR_ENTRY.equals( metaData.getName() ) )
        {
            throw new IllegalArgumentException( "Using a reserved id name" + metaData.getName() );
        }

        // create a handler for the combo of Role+MetaData
        final ComponentHandler handler =
            getComponentHandler( roleEntry, metaData );

        final String role = roleEntry.getRole();

        // put the role into our role mapper. If the role doesn't exist
        // yet, just stuff it in as DEFAULT_ENTRY. If it does, we create a
        // ServiceSelector and put that in as SELECTOR_ENTRY.
        if( null != role && null != classname && null != handler )
        {
            Map hintMap = (StaticBucketMap)m_mapper.get( role );

            // Initialize the hintMap if it doesn't exist yet.
            if( null == hintMap )
            {
                hintMap = new StaticBucketMap();
                hintMap.put( DEFAULT_ENTRY, handler );
                m_mapper.put( role, hintMap );
            }

            hintMap.put( metaData.getName(), handler );

            if( ( !hintMap.containsKey( SELECTOR_ENTRY ) ) && ( hintMap.size() > 1 ) )
            {
                hintMap.put( SELECTOR_ENTRY,
                             new FortressServiceSelector( this, role ) );
            }

            if( metaData.getConfiguration().getAttributeAsBoolean( "default", false ) )
            {
                hintMap.put( DEFAULT_ENTRY, handler );
            }
        }
    }

    /**
     * Get a ComponentHandler with the default constructor for the component class passed in.
     *
     * @param roleEntry the description of the Role this handler will be for
     * @param metaData the information needed to construct a ComponentHandler for the component
     * @return the component handler
     * @throws Exception if unable to provide a componenthandler
     */
    private ComponentHandler getComponentHandler( final RoleEntry roleEntry,
                                                  final ComponentHandlerMetaData metaData )
        throws Exception
    {
        // get info from params
        ComponentHandler handler = null;
        final String classname = roleEntry.getComponentClass().getName();
        final Configuration configuration = metaData.getConfiguration();

        try
        {
            final ObjectFactory factory =
                createObjectFactory( classname, configuration );

            // create the appropriate handler instance
            final ComponentHandler targetHandler =
                (ComponentHandler)roleEntry.getHandlerClass().newInstance();

            // do the handler lifecycle
            ContainerUtil.contextualize( targetHandler, m_context );
            final DefaultServiceManager serviceManager =
                new DefaultServiceManager( getServiceManager() );
            serviceManager.put( ObjectFactory.ROLE, factory );
            serviceManager.makeReadOnly();

            ContainerUtil.service( targetHandler, serviceManager );
            ContainerUtil.configure( targetHandler, configuration );
            ContainerUtil.initialize( targetHandler );

            if( targetHandler instanceof Instrumentable )
            {
                final Instrumentable instrumentable = (Instrumentable)targetHandler;
                final String name = instrumentable.getInstrumentableName();
                m_instrumentManager.registerInstrumentable( instrumentable, name );
            }

            // no other lifecycle stages supported for ComponentHandler;
            // ComponentHandler is not a "true" avalon component

            handler =
                new LEAwareComponentHandler( targetHandler, m_extManager, m_context );
        }
        catch( final Exception e )
        {
            // if anything went wrong, the component cannot be worked with
            // and it cannot be added into the impl, so don't provide
            // a handler
            if( getLogger().isDebugEnabled() )
            {
                final String message =
                    "Could not create the handler for the '" +
                    classname + "' component.";
                getLogger().debug( message, e );
            }
            throw e;
        }

        if( getLogger().isDebugEnabled() )
        {
            final String message =
                "Component " + classname +
                " uses handler " + roleEntry.getHandlerClass().getName();
            getLogger().debug( message );
        }

        // we're still here, so everything went smooth. Register the handler
        // and return it
        final ComponentHandlerEntry entry =
            new ComponentHandlerEntry( handler, metaData );
        m_components.add( entry );

        return handler;
    }

    /**
     * Create an objectFactory for specified Object configuration.
     *
     * @param classname the classname of object
     * @param configuration the objests configuration
     * @return the ObjectFactory
     * @throws ClassNotFoundException if the specified class does not exist
     */
    protected ObjectFactory createObjectFactory( final String classname,
                                                 final Configuration configuration )
        throws ClassNotFoundException
    {
        final Class clazz = m_classLoader.loadClass( classname );
        final ComponentFactory componentFactory =
            new ComponentFactory( clazz, configuration,
                                  m_serviceManager, m_context,
                                  m_loggerManager, m_extManager );
        return new ProxyObjectFactory( componentFactory );
    }

    /**
     * This is the method that the ContainerComponentManager and Selector use
     * to gain access to the ComponentHandlers and ComponentSelectors.  The
     * actual access of the ComponentHandler is delegated to the Container.
     *
     * @param  role  The role we intend to access a Component for.
     * @param  hint  The hint that we use as a qualifier
     *         (note: if null, the default implementation is returned).
     *
     * @return Object  a reference to the ComponentHandler or
     *                 ComponentSelector for the role/hint combo.
     */
    public Object get( final String role, final Object hint )
        throws ServiceException
    {
        final Map hintMap = (StaticBucketMap)m_mapper.get( role );
        Object value;

        if( null == hintMap )
        {
            final String key = getRoleKey( role, hint );
            final String message = "Component does not exist";
            throw new ServiceException( key, message );
        }

        if( null == hint )
        {
            // no hint -> try selector
            value = hintMap.get( SELECTOR_ENTRY );

            if( null == value )
            {
                // no selector -> use default
                value = hintMap.get( DEFAULT_ENTRY );
            }

            return value;
        }

        // got a hint -> use it
        value = hintMap.get( hint );

        if( null == value )
        {
            final String key = getRoleKey( role, hint );
            final String message = "Component does not exist";
            throw new ServiceException( key, message );
        }

        return value;
    }

    /**
     * Get the composite role name based on the specified role and hint.
     * The default implementation puts a "/" on the end of the rolename
     * and then adds the string representation of the hint.
     * This is used <i>for informational display purposes only</i>.
     *
     * @param role
     * @param hint
     * @return
     */
    protected static String getRoleKey( final String role, final Object hint )
    {
        return role + "/" + hint;
    }

    /**
     * This is the method that the ContainerComponentManager and Selector use
     * to gain access to the ComponentHandlers and ComponentSelectors.  The
     * actual access of the ComponentHandler is delegated to the Container.
     *
     * @param  role  The role we intend to access a Component for.
     * @param  hint  The hint that we use as a qualifier
     *         (note: if null, the default implementation is returned).
     *
     * @return true  if a reference to the role exists.
     */
    public boolean has( final String role, final Object hint )
    {
        final Map hintMap = (StaticBucketMap)m_mapper.get( role );
        boolean hasComponent = false;

        if( null != hintMap )
        {
            hasComponent = true;
        }

        if( hasComponent )
        {
            if( null == hint )
            {
                // no hint -> try selector
                hasComponent = hintMap.containsKey( SELECTOR_ENTRY );

                if( !hasComponent )
                {
                    // no hint -> try DEFAULT_ENTRY
                    hasComponent = hintMap.containsKey( DEFAULT_ENTRY );
                }
            }
            else
            {
                // hint -> find it
                hasComponent = hintMap.containsKey( hint );
            }
        }

        return hasComponent;
    }

    /**
     * Initializes the impl and all the components it hosts so that they are ready to be used.
     * Unless components ask for lazy activation, this is where they are activated.
     *
     * @throws CompositeException if one or more components could not be initialized.
     *                   The system <i>is</i> running properly so if the missing components are
     *                   not vital to operation, it should be possible to recover gracefully
     */
    public void initialize()
        throws CompositeException, Exception
    {
        // go over all components
        final Iterator i = m_components.iterator();
        final BoundedFifoBuffer buffer = new BoundedFifoBuffer( Math.max( m_components.size(), 1 ) );

        ComponentHandlerEntry entry;
        while( i.hasNext() )
        {
            entry = (ComponentHandlerEntry)i.next();
            try
            {
                final ComponentHandler handler = entry.getHandler();
                // if the component is not lazy, prepare it now,
                // otherwise, don't do anything yet
                if( !entry.getMetaData().isLazyActivation() )
                {
                    // if we're doing queueing, enqueue, otherwise tell
                    // the handler to prepare itself and its components.
                    if( null != m_commandQueue )
                    {
                        final PrepareHandlerCommand element =
                            new PrepareHandlerCommand( handler, getLogger() );
                        m_commandQueue.enqueue( element );
                    }
                    else
                    {
                        handler.prepareHandler();
                    }
                }
                else
                {
                    if( getLogger().isDebugEnabled() )
                    {
                        final String message = "ComponentHandler (" + handler +
                            ") has specified request time initialization policy, " +
                            "initialization deferred till first use";
                        getLogger().debug( message );
                    }
                }
            }
            catch( final Exception e )
            {
                final String cName = entry.getMetaData().getName();

                if( getLogger().isWarnEnabled() )
                {
                    final String message = "Could not initialize component " + cName;
                    getLogger().warn( message, e );
                }
                buffer.add( e );
            }
        }

        // if we were unable to activate one or more components,
        // throw an exception
        if( buffer.size() > 0 )
        {
            throw new CompositeException( (Exception[])buffer.toArray( new Exception[ 0 ] ),
                                          "unable to instantiate one or more components" );
        }
    }

    /**
     * Disposes of all components and frees resources that they consume.
     */
    public void dispose()
    {
        final Iterator i = m_components.iterator();
        while( i.hasNext() )
        {
            final ComponentHandlerEntry entry = (ComponentHandlerEntry)i.next();
            final ComponentHandler handler = entry.getHandler();

            if( getLogger().isDebugEnabled() ) getLogger().debug( "Shutting down: " + handler );
            ContainerUtil.dispose( handler );
            if( getLogger().isDebugEnabled() ) getLogger().debug( "Done." );
        }
    }

    /**
     * Exposes to subclasses the service manager which this impl
     * uses to manage its child components.
     * The returned ServiceManager <i>is</i> aware of the services passed
     * in to <i>this</i> impl, and services that were passed in through
     * service() are hence available to subclasses.
     *
     * @return the service manager that contains the child components.
     */
    protected ServiceManager getServiceManager()
    {
        return m_serviceManager;
    }
}