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
package org.apache.avalon.fortress.util;

import org.apache.avalon.excalibur.logger.LogKitLoggerManager;
import org.apache.avalon.excalibur.logger.LoggerManager;
import org.apache.avalon.fortress.MetaInfoManager;
import org.apache.avalon.fortress.RoleManager;
import org.apache.avalon.fortress.impl.role.ConfigurableRoleManager;
import org.apache.avalon.fortress.impl.role.FortressRoleManager;
import org.apache.avalon.fortress.impl.role.Role2MetaInfoManager;
import org.apache.avalon.fortress.impl.role.ServiceMetaManager;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.LogKitLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.DefaultServiceManager;
import org.apache.avalon.framework.service.DefaultServiceSelector;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.event.Queue;
import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.command.CommandManager;
import org.apache.excalibur.event.command.TPCThreadManager;
import org.apache.excalibur.event.command.ThreadManager;
import org.apache.excalibur.instrument.InstrumentManager;
import org.apache.excalibur.instrument.manager.DefaultInstrumentManager;
import org.apache.excalibur.mpool.DefaultPoolManager;
import org.apache.excalibur.mpool.PoolManager;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.impl.ResourceSourceFactory;
import org.apache.excalibur.source.impl.SourceResolverImpl;
import org.apache.excalibur.source.impl.URLSourceFactory;
import org.apache.log.Hierarchy;
import org.apache.log.Priority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 * <p>The ContextManager is used to manage the values in a Container's
 * Context.  The ContainerManager uses this helper class to create the
 * initial context for the Container.  Its purpose is to add the default
 * values, and give convenient methods to override those defaults.  Once you
 * get an instance of the Context, it is made read-only and returned.  No
 * further operations will be possible on it.</p>
 *
 * <p>You can get two different contexts from the ContextManager: the child
 * context and the impl manager context. The former contains all
 * managers, such as the pool manager etc. necessary for a child impl to
 * create additional child containers. The impl manager context contains
 * all of the child context, but also initialization parameters for the
 * impl, such as a Configuration object, a ComponentLocator, etc., that
 * the impl wants, but does not want to pass on to its children.</p>
 *
 * <p>You would typically use the impl manager context to initialize
 * the impl manager, and let it pass the child context on to the
 * impl.</p>
 *
 * <p>The ContextManager will sometimes create new components, such as a
 * component manager, a pool manager, etc. It will manage these components
 * and dispose of them properly when it itself is disposed .</p>
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.26 $ $Date: 2003/05/15 13:50:36 $
 * @since 4.1
 */
public final class ContextManager
    implements ContextManagerConstants, Initializable, Disposable
{
    private static final Configuration EMPTY_CONFIG;

    static
    {
        DefaultConfiguration config =
            new DefaultConfiguration( "", "", "", "" );
        config.makeReadOnly();
        EMPTY_CONFIG = config;
    }

    /**
     * The root context.
     */
    private final Context m_rootContext;

    /**
     * The context of the new impl. This context has the rootContext
     * as its parent. Put everything here that you want the new impl
     * to have in its own context.
     */
    private final DefaultContext m_childContext;

    /**
     * Container manager's context. This context has the child context
     * as parent. Put things here that you want the impl manager
     * to see, but do not wish to expose to the impl.
     */
    private final DefaultContext m_containerManagerContext;

    /**
     * New context passed in, maybe from a FortressConfig.
     * This context may not have objects, but rather URI's or
     * other pointers. The ContextManager inspects it for
     * elements it can use to create component managers etc.
     */
    private Logger m_logger;
    private final Logger m_primordialLogger;
    private ServiceManager m_manager;

    /**
     * The components that are "owned" by this context and should
     * be disposed by it. Any manager that is created as a result
     * of it not being in the rootContext, or having been created
     * by the ContextManager should go in here.
     */
    private final ArrayList ownedComponents = new ArrayList();

    /**
     * The ConfigurationBuilder is instantiated lazilly in getConfiguration
     * to avoid LinkageErrors in some environments.
     */
    private DefaultConfigurationBuilder configBuilder;

    /**
     * Create a new ContextManager.
     *
     * @param rootContext the default values.
     * @param logger      logger to use when creating new components.
     */
    public ContextManager( final Context rootContext, final Logger logger )
    {
        m_rootContext = rootContext;
        m_childContext = new OverridableContext( m_rootContext );
        m_containerManagerContext = new OverridableContext( m_childContext );
        m_logger = logger;

        // The primordial logger is used for all output up until the point where
        //  the logger manager has been initialized.  However it is set into
        //  two objects used to load the configuration resource files within
        //  the ContextManager.  Any problems loading these files will result in
        //  warning or error messages.  However in most cases, the debug
        //  information should not be displayed, so turn it off by default.
        //  Unfortunately, there is not a very good place to make this settable.
        m_primordialLogger = new ConsoleLogger( ConsoleLogger.LEVEL_INFO );
    }

    /**
     * Method to assume ownership of one of the managers the
     * <code>ContextManager</code> created.  Ownership means that the
     * <code>ContextManager</code> is responsible for destroying the
     * manager when the <code>ContextManager</code> is destroyed.
     *
     * @param object  The object being claimed
     *
     * @throws IllegalArgumentException if the object is null.
     */
    private void assumeOwnership( final Object object )
    {
        if ( object == null )
        {
            throw new NullPointerException( "object: Can not assume ownership of a null!" );
        }
        ownedComponents.add( object );
    }

    /**
     * Initialize the <code>ContextManager</code>.  This will cause the
     * <code>ContextManager</code> to create any managers it needs.
     *
     * @throws Exception if there is a problem at any point in the
     *         initialization.
     */
    public void initialize() throws Exception
    {
        initializeServiceManager();
        initializeLoggerManager();
        initializeRoleManager();
        initializeMetaInfoManager();
        initializeCommandQueue();
        initializePoolManager();
        initializeContext();
        initializeInstrumentManager();
        initializeConfiguration();

        m_childContext.makeReadOnly();
        m_containerManagerContext.makeReadOnly();
    }

    /**
     * Load the configuration file that the Container will need to operate.
     */
    protected void initializeConfiguration()
    {
        try
        {
            copyEntry(CONFIGURATION);
            return;
        }
        catch ( ContextException ce )
        {
            final Configuration containerConfig = getConfiguration( CONFIGURATION, CONFIGURATION_URI );

            if ( containerConfig == null )
            {
                getLogger().debug( "Could not initialize the Configuration", ce );
                // Guess there is none.
                return;
            }
            else
            {
                m_containerManagerContext.put( CONFIGURATION, containerConfig );
            }
        }
    }

    /**
     * Initialize the context that will be passed into the impl.
     *
     * @throws Exception if any of the parameters cannot be copied properly.
     */
    protected void initializeContext() throws Exception
    {
        copyEntry( CONTAINER_CLASS );

        try
        {
            copyEntry( PARAMETERS );
        }
        catch ( ContextException ce )
        {
            getLogger().debug( "Could not copy Context parameters.  This may be Ok depending on "
                + "other configured context values." );
        }
    }

    /**
     * Copies the specified entry from the <code>rootContext</code> to the
     * <code>containerManagerContext</code>.
     *
     * @throws ContextException if the parameter does not exist
     */
    protected void copyEntry( final String key ) throws ContextException
    {
        m_containerManagerContext.put( key, m_rootContext.get( key ) );
        m_childContext.put( key, null );
    }

    /**
     * Disposes all items that this ContextManager has created.
     */
    public void dispose()
    {
        Collections.sort( ownedComponents, new DestroyOrderComparator() );
        // Dispose owned components
        final Iterator ownedComponentsIter = ownedComponents.iterator();
        while ( ownedComponentsIter.hasNext() )
        {
            final Object o = ownedComponentsIter.next();

            try
            {
                if ( getLogger().isDebugEnabled() ) getLogger().debug( "Shutting down: " + o );
                ContainerUtil.shutdown( o );
                if ( getLogger().isDebugEnabled() ) getLogger().debug( "Done." );
            }
            catch ( Exception e )
            {
                getLogger().warn( "Unable to dispose of owned component "
                    + o.getClass().getName(), e );
            }

            ownedComponentsIter.remove();
        }
    }

    /**
     * Convenience method to obtain a value, or defer to a default if it does
     * not exist.
     *
     * @param context       The context object we intend to get a value from.
     * @param key           The key we want to use to get the value.
     * @param defaultValue  The default value we return if the key does not
     *                      exist.
     */
    protected Object get( final Context context, final String key, final Object defaultValue )
    {
        try
        {
            return context.get( key );
        }
        catch ( ContextException ce )
        {
            return defaultValue;
        }
    }

    /**
     * Set up the CommandQueue to enable asynchronous management.
     *
     * @throws Exception if the <code>CommandQueue</code> could not be
     *         created.
     */
    protected void initializeCommandQueue() throws Exception
    {
        try
        {
            m_rootContext.get( Queue.ROLE );
            return;
        }
        catch ( ContextException ce )
        {
        }

        // No CommandQueue specified, create a default one
        m_childContext.put( Queue.ROLE, createCommandSink() );
    }

    /**
     * Helper method for creating a default CommandQueue
     *
     * @return a default <code>CommandQueue</code>
     * @throws Exception if an error occurs
     */
    private Sink createCommandSink() throws Exception
    {
        final CommandManager cm = new CommandManager();
        final ThreadManager tm = new TPCThreadManager();

        assumeOwnership( cm );
        assumeOwnership( tm );

        // Get the context Logger Manager
        final LoggerManager loggerManager = (LoggerManager) m_childContext.get( LoggerManager.ROLE );

        // Get the logger for the thread manager
        final Logger tmLogger = loggerManager.getLoggerForCategory( "system.threadmgr" );

        ContainerUtil.enableLogging( tm, tmLogger );
        ContainerUtil.parameterize( tm, buildCommandQueueConfig() );
        ContainerUtil.initialize( tm );

        tm.register( cm );

        return cm.getCommandSink();
    }

    /**
     * Helper method for creating ThreadManager configuration.
     *
     * @return ThreadManager configuration as a <code>Parameters</code>
     *         instance
     */
    private Parameters buildCommandQueueConfig()
    {
        final Parameters p = new Parameters();
        Integer threadsPerProcessor;
        Long threadBlockTimeout;

        try
        {
            final Integer processors = (Integer) m_rootContext.get( "processors" );
            p.setParameter( "processors", processors.toString() );
        }
        catch ( ContextException e )
        {
        }

        try
        {
            threadsPerProcessor = (Integer) m_rootContext.get( THREADS_CPU );
        }
        catch ( ContextException e )
        {
            threadsPerProcessor = new Integer( 2 );
        }

        p.setParameter( "threads-per-processor", threadsPerProcessor.toString() );

        try
        {
            threadBlockTimeout = (Long) m_rootContext.get( THREAD_TIMEOUT );
        }
        catch ( ContextException e )
        {
            threadBlockTimeout = new Long( 1000 );
        }

        p.setParameter( "block-timeout", threadBlockTimeout.toString() );

        return p;
    }

    /**
     * Set up the Pool Manager for the managed pool.
     *
     * @throws Exception if there is an error.
     */
    protected void initializePoolManager() throws Exception
    {
        try
        {
            m_rootContext.get( PoolManager.ROLE );
            return;
        }
        catch ( ContextException ce )
        {
        }

        final PoolManager pm = new DefaultPoolManager( (Sink) m_childContext.get( Queue.ROLE ) );
        assumeOwnership( pm );
        m_childContext.put( PoolManager.ROLE, pm );
    }

    /**
     * Set up the RoleManager for the Container.
     *
     * @throws Exception if there is an error.
     */
    protected void initializeRoleManager() throws Exception
    {
        try
        {
            m_rootContext.get( RoleManager.ROLE );
            return;
        }
        catch ( ContextException ce )
        {
            getLogger().debug( "Could not copy context entry: " + RoleManager.ROLE
                + ".  This may be Ok depending on other configured context values." );
        }

        Configuration roleConfig =
            getConfiguration( ROLE_MANAGER_CONFIGURATION, ROLE_MANAGER_CONFIGURATION_URI );

        if ( roleConfig == null )
        {
            getLogger().debug( "Could not initialize the RoleManager, no configuration" );
            // No RoleManager available anywhere.
            roleConfig = EMPTY_CONFIG;
        }

        // Get the context Logger Manager
        final LoggerManager loggerManager = (LoggerManager) m_childContext.get( LoggerManager.ROLE );

        // Create a logger for the role manager
        final Logger rmLogger = loggerManager.getLoggerForCategory(
            roleConfig.getAttribute( "logger", "system.roles" ) );

        // Lookup the context class loader
        final ClassLoader classLoader = (ClassLoader) m_rootContext.get( ClassLoader.class.getName() );

        // Create a parent role manager with all the default roles
        final FortressRoleManager frm = new FortressRoleManager( null, classLoader );
        frm.enableLogging( rmLogger.getChildLogger( "defaults" ) );
        frm.initialize();

        // Create a role manager with the configured roles
        final ConfigurableRoleManager rm = new ConfigurableRoleManager(frm);
        rm.enableLogging( rmLogger );
        rm.configure( roleConfig );

        assumeOwnership( rm );
        m_childContext.put( RoleManager.ROLE, rm );
    }

    /**
     * Set up the RoleManager for the Container.
     *
     * @throws Exception if there is an error.
     */
    protected void initializeMetaInfoManager() throws Exception
    {
        try
        {
            m_rootContext.get( MetaInfoManager.ROLE );
            return;
        }
        catch ( ContextException ce )
        {
            getLogger().debug( "Could not copy context entry: " + MetaInfoManager.ROLE
                + ".  This may be Ok depending on other configured context values." );
        }

        // Get the context Logger Manager
        final LoggerManager loggerManager = (LoggerManager) m_childContext.get( LoggerManager.ROLE );

        // Create a logger for the role manager
        final Logger rmLogger = loggerManager.getLoggerForCategory( "system.meta" );

        // Lookup the context class loader
        final ClassLoader classLoader = (ClassLoader) m_rootContext.get( ClassLoader.class.getName() );

        // Create a parent role manager with all the default roles
        final ServiceMetaManager smm = new ServiceMetaManager( new Role2MetaInfoManager( (RoleManager) m_childContext.get( RoleManager.ROLE ) ), classLoader );
        smm.enableLogging( rmLogger.getChildLogger( "defaults" ) );
        smm.initialize();

        assumeOwnership( smm );
        m_childContext.put( org.apache.avalon.fortress.MetaInfoManager.ROLE, smm );
    }

    /**
     * Get a reference to the initial ComponentLocator used by the
     * ContainerManager to hold the Components used for parsing the config
     * files and setting up the environment.
     *
     * @throws Exception when there is an error.
     */
    protected void initializeServiceManager() throws Exception
    {
        final ServiceManager parent = (ServiceManager) get( m_rootContext, SERVICE_MANAGER, null );
        final DefaultServiceManager manager = new DefaultServiceManager( parent );
        final DefaultServiceSelector selector = new DefaultServiceSelector();
        final URLSourceFactory file = new URLSourceFactory();
        file.enableLogging( getLogger() );
        selector.put( "*", file );
        final ResourceSourceFactory resource = new ResourceSourceFactory();
        resource.enableLogging( getLogger() );
        selector.put( "resource", resource );

        manager.put( ResourceSourceFactory.ROLE + "Selector", selector );

        final SourceResolverImpl resolver = new SourceResolverImpl();
        ContainerUtil.enableLogging( resolver, getLogger() );
        ContainerUtil.contextualize( resolver, m_childContext );
        ContainerUtil.service( resolver, manager );
        ContainerUtil.parameterize( resolver, new Parameters() );

        manager.put( SourceResolver.ROLE, resolver );

        manager.makeReadOnly();

        assumeOwnership( manager );

        m_manager = manager;

        m_childContext.put( ContextManagerConstants.SERVICE_MANAGER, m_manager );
    }

    /**
     * Get a configuration based on a config and URI key.
     *
     * @param configKey  Get the <code>Configuration</code> object directly
     *                   from the context.
     * @param uriKey     Get the uri from the context.
     */
    protected Configuration getConfiguration( final String configKey, final String uriKey )
    {
        try
        {
            return (Configuration) m_rootContext.get( configKey );
        }
        catch ( ContextException ce )
        {
            getLogger().debug( "A preloaded Configuration was not found for key: " + configKey
                + "  This may be Ok depending on other configured context values." );
        }

        final String configUri;
        try
        {
            configUri = (String) m_rootContext.get( uriKey );
        }
        catch ( ContextException ce )
        {
            getLogger().debug( "A configuration URI was not specified either: " + uriKey
                + "  One or the other is required." );
            return null;
        }

        SourceResolver resolver = null;
        Source src = null;
        try
        {
            resolver = (SourceResolver) m_manager.lookup( SourceResolver.ROLE );
            src = resolver.resolveURI( configUri );
            if ( configBuilder == null )
            {
                configBuilder = new DefaultConfigurationBuilder();
            }

            return configBuilder.build( src.getInputStream() );
        }
        catch ( Exception e )
        {
            getLogger().warn( "Could not read configuration file: " + configUri, e );

            return null;
        }
        finally
        {
            if ( null != resolver )
            {
                resolver.release( src );
            }

            m_manager.release( resolver );
        }
    }

    /**
     * Finalizes and returns the context.
     *
     * @return a <code>Context</code>
     */
    public Context getContainerManagerContext()
    {
        return m_containerManagerContext;
    }

    /**
     * Finalizes and returns the context.
     *
     * @return a <code>Context</code>
     */
    public Context getChildContext()
    {
        return m_childContext;
    }

    /**
     * Get a reference the Logger.
     *
     * @return a <code>Logger</code>
     */
    protected Logger getLogger()
    {
        if ( m_logger == null )
        {
            return m_primordialLogger;
        }
        else
        {
            return m_logger;
        }
    }

    /**
     * Will set up a LogKitLoggerManager if none is supplied.  This can be
     * overridden if you don't want a LogKitLoggerManager.
     *
     * <p>The postcondition is that
     * <code>childContext.get( LoggerManager.ROLE )</code> should
     * return a valid logger manager.</p>
     *
     * @throws Exception if it cannot instantiate the LoggerManager
     */
    protected void initializeLoggerManager() throws Exception
    {
        try
        {
            // Try copying an already existing logger manager from the override context.

            m_childContext.put( LoggerManager.ROLE, m_rootContext.get( LoggerManager.ROLE ) );
        }
        catch ( ContextException ce )
        {
            // Should we set one up?
            // Try to get a configuration for it...
            Configuration loggerManagerConfig =
                getConfiguration( LOGGER_MANAGER_CONFIGURATION, LOGGER_MANAGER_CONFIGURATION_URI );
            if ( loggerManagerConfig == null )
            {
                // Create an empty configuration so that
                // a default logger can be created.
                loggerManagerConfig = EMPTY_CONFIG;
            }

            // Resolve a name for the logger, taking the logPrefix into account
            final String lmDefaultLoggerName = (String) m_rootContext.get( ContextManagerConstants.LOG_CATEGORY );
            final String lmLoggerName = loggerManagerConfig.getAttribute( "logger", lmDefaultLoggerName + ".system.logkit" );

            // Create the default logger for the Logger Manager.
            final org.apache.log.Logger lmDefaultLogger =
                Hierarchy.getDefaultHierarchy().getLoggerFor( lmDefaultLoggerName );
            // The default logger is not used until after the logger conf has been loaded
            //  so it is possible to configure the priority there.
            lmDefaultLogger.setPriority( Priority.DEBUG );

            // Create the logger for use internally by the Logger Manager.
            final org.apache.log.Logger lmLogger =
                Hierarchy.getDefaultHierarchy().getLoggerFor( lmLoggerName );
            lmLogger.setPriority( Priority.getPriorityForName(
                loggerManagerConfig.getAttribute( "log-level", "DEBUG" ) ) );

            // Setup the Logger Manager
            final LoggerManager logManager = new LogKitLoggerManager(
                lmDefaultLoggerName, Hierarchy.getDefaultHierarchy(),
                new LogKitLogger( lmDefaultLogger ), new LogKitLogger( lmLogger ) );
            ContainerUtil.contextualize( logManager, m_rootContext );
            ContainerUtil.configure( logManager, loggerManagerConfig );

            assumeOwnership( logManager );

            m_childContext.put( LoggerManager.ROLE, logManager );
        }

        // Since we now have a LoggerManager, we can update the this.logger field
        // if it is null and start logging to the "right" logger.

        if ( m_logger == null )
        {
            getLogger().debug( "Switching to default Logger provided by LoggerManager." );

            final LoggerManager loggerManager = (LoggerManager) m_childContext.get( LoggerManager.ROLE );
            m_logger = loggerManager.getDefaultLogger();
        }
    }

    /**
     * Will set up a LogKitLoggerManager if none is supplied.  This can be
     * overridden if you don't want a LogKitLoggerManager.
     *
     * <p>The postcondition is that
     * <code>childContext.get( LoggerManager.ROLE )</code> should
     * return a valid logger manager.</p>
     *
     * @throws Exception if it cannot instantiate the LoggerManager
     */
    protected void initializeInstrumentManager() throws Exception
    {
        try
        {
            // Try copying an already existing instrument manager from the override context.

            m_rootContext.get( InstrumentManager.ROLE );
        }
        catch ( ContextException ce )
        {
            // Should we set one up?
            // Try to get a configuration for it...
            Configuration instrumentConfig = getConfiguration( INSTRUMENT_MANAGER_CONFIGURATION, INSTRUMENT_MANAGER_CONFIGURATION_URI );
            if ( instrumentConfig == null )
            {
                // No config.
                instrumentConfig = EMPTY_CONFIG;
            }

            // Get the context Logger Manager
            final LoggerManager loggerManager = (LoggerManager) m_childContext.get( LoggerManager.ROLE );

            // Get the logger for the instrument manager
            final Logger imLogger = loggerManager.getLoggerForCategory(
                instrumentConfig.getAttribute( "logger", "system.instrument" ) );

            // Set up the Instrument Manager
            final DefaultInstrumentManager instrumentManager = new DefaultInstrumentManager();
            instrumentManager.enableLogging( imLogger );
            instrumentManager.configure( instrumentConfig );
            instrumentManager.initialize();

            assumeOwnership( instrumentManager );

            m_childContext.put( InstrumentManager.ROLE, instrumentManager );
        }
    }

    private final class DestroyOrderComparator implements Comparator
    {
        public boolean equals( final Object other )
        {
            return other instanceof DestroyOrderComparator;
        }

        public int hashCode()
        {
            return DestroyOrderComparator.class.hashCode();
        }

        public int compare( final Object a, final Object b )
        {
            final int typeA = typeOf( a );
            final int typeB = typeOf( b );

            if ( typeA < typeB ) return -1;
            if ( typeA > typeB ) return 1;
            return 0;
        }

        private int typeOf( final Object obj )
        {
            int retVal = 1; // Doesn't matter the type

            if ( obj instanceof CommandManager ) retVal = 0;
            if ( obj instanceof ThreadManager ) retVal = 2;

            return retVal;
        }
    }
}

