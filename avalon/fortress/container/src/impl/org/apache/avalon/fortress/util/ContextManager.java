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
import org.apache.avalon.excalibur.logger.Log4JConfLoggerManager;
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
import org.apache.avalon.framework.service.ServiceException;
import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.Queue;
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
 * <p>You can get two different contexts from the ContextManager:
 * the container context (m_childContext)
 * and the container manager context (m_containerManagerContext)</p>
 *
 * <p>You can get two different contexts from the ContextManager: the child
 * context and the impl manager context. The former contains all
 * managers, such as the pool manager etc. necessary for a child impl to
 * create additional child containers. The impl manager context contains
 * all of the child context, but also initialization parameters for the
 * impl, such as a Configuration object, a ComponentLocator, etc., that
 * the impl wants, but does not want to pass on to its children.</p>
 *
 * <p>The container manager context is used to provide the container manager
 * with all the data needed to initialize the container.</p>
 * <p>The container context is passed directly to the container.</p>
 *
 * <p>The ContextManager will sometimes create new components, such as
 * a service manager, a pool manager, etc. It will manage these components
 * and dispose of them properly when it itself is disposed .</p>
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.43 $ $Date: 2003/06/11 10:48:15 $
 * @since 4.1
 */
public class ContextManager
        implements ContextManagerConstants, Initializable, Disposable
{
    protected static final Configuration EMPTY_CONFIG;

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
    protected final Context m_rootContext;

    /**
     * The context of the new impl. This context has the rootContext
     * as its parent. Put everything here that you want the new impl
     * to have in its own context.
     */
    protected final DefaultContext m_childContext;

    /**
     * Container manager's context. This context has the child context
     * as parent. Put things here that you want the impl manager
     * to see, but do not wish to expose to the impl.
     */
    protected final DefaultContext m_containerManagerContext;

    protected Logger m_logger;
    protected final Logger m_primordialLogger;

    /**
     *  Source resolver used to read-in the configurations.
     *  and provided as a default source resolver if the
     *  user has not supplied a ServiceManager.
     */
    protected SourceResolver m_defaultSourceResolver;

    /**
     * The logger manager in use.
     * Either supplied via rootContext, or created locally.
     */
    protected LoggerManager m_loggerManager;

    /**
     * The Sink in use.
     * Either supplied via rootContext or created locally.
     */
    protected Sink m_sink;

    /**
     * The MetaInfoManager to be used by the container.
     * Either supplied via rootContext or created locally.
     */
    protected MetaInfoManager m_metaInfoManager;

    /**
     * The PoolManager to be used by the container.
     * Either supplied via rootContext or created locally.
     */
    protected PoolManager m_poolManager;

    /**
     * The InstrumentManager to be used by the container.
     * Either supplied via rootContext or created locally.
     */
    protected InstrumentManager m_instrumentManager;
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
        if ( m_logger == null )
        {
            m_primordialLogger = new ConsoleLogger( ConsoleLogger.LEVEL_INFO );
        }
        else
        {
            m_primordialLogger = null;
        }
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
    protected void assumeOwnership( final Object object )
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
        initializeDefaultSourceResolver();
        initializeLoggerManager();
        initializeMetaInfoManager();
        initializeCommandSink();
        initializePoolManager();
        initializeContext();
        initializeInstrumentManager();
        initializeConfiguration();
        initializeServiceManager();

        m_childContext.makeReadOnly();
        m_containerManagerContext.makeReadOnly();

        m_defaultSourceResolver = null;
    }

    /**
     * Load the configuration file that the Container will need to operate.
     */
    protected void initializeConfiguration()
    {
        try
        {
            copyEntry( CONFIGURATION );
            return;
        }
        catch ( ContextException ce )
        {
            final Configuration containerConfig =
                    getConfiguration( CONFIGURATION, CONFIGURATION_URI );

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

        // hide from the container implementation what it does not need
        m_childContext.put( CONFIGURATION, null );
        m_childContext.put( CONFIGURATION_URI, null );
        m_childContext.put( RoleManager.ROLE, null );
        m_childContext.put( ROLE_MANAGER_CONFIGURATION, null );
        m_childContext.put( ROLE_MANAGER_CONFIGURATION_URI, null );
        m_childContext.put( LoggerManager.ROLE, null );
        m_childContext.put( LOGGER_MANAGER_CONFIGURATION, null );
        m_childContext.put( LOGGER_MANAGER_CONFIGURATION_URI, null );
        m_childContext.put( InstrumentManager.ROLE, null );
        m_childContext.put( INSTRUMENT_MANAGER_CONFIGURATION, null );
        m_childContext.put( INSTRUMENT_MANAGER_CONFIGURATION_URI, null );
        m_childContext.put( Sink.ROLE, null );
        m_childContext.put( MetaInfoManager.ROLE, null );
        m_childContext.put( PoolManager.ROLE, null );
        m_childContext.put( LifecycleExtensionManager.ROLE, null );
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
     * Checks if a specified entry in <code>context</code>
     * has been supplied by the invoker.
     *
     * @param context  The context to check
     * @param key      The key name to check
     */
    protected boolean entryPresent( Context context, final String key )
    {
        boolean isPresent = false;

        try
        {
            context.get( key );
            isPresent = true;
        }
        catch ( ContextException ce )
        {
            // It is not present, so the value remains false
        }

        return isPresent;
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
     * Set up the CommandSink to enable asynchronous management.
     *
     * @throws Exception if the <code>CommandSink</code> could not be
     *         created.
     */
    protected void initializeCommandSink() throws Exception
    {
        try
        {
            m_sink = (Sink) m_rootContext.get( Sink.ROLE );
        }
        catch ( ContextException ce )
        {
            // No CommandSink specified, create a default one
            m_sink = createCommandSink();
        }
    }

    /**
     * Helper method for creating a default CommandSink
     *
     * @return a default <code>CommandSink</code>
     * @throws Exception if an error occurs
     */
    private Sink createCommandSink() throws Exception
    {
        final CommandManager cm = new CommandManager();
        final ThreadManager tm = new TPCThreadManager();

        assumeOwnership( cm );
        assumeOwnership( tm );

        // Get the context Logger Manager
        final Logger tmLogger = m_loggerManager.getLoggerForCategory( "system.threadmgr" );

        ContainerUtil.enableLogging( tm, tmLogger );
        ContainerUtil.parameterize( tm, buildCommandSinkConfig() );
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
    private Parameters buildCommandSinkConfig()
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
            m_poolManager = (PoolManager) m_rootContext.get( PoolManager.ROLE );
        }
        catch ( ContextException ce )
        {
            final PoolManager pm = new DefaultPoolManager( m_sink );
            assumeOwnership( pm );
            m_poolManager = pm;
        }
    }

    /**
     * Set up a RoleManager for the Container if configuration for
     * it has been supplied.
     *
     * @throws Exception if there is an error.
     */
    protected RoleManager obtainRoleManager() throws Exception
    {
        /* we don't want an error message from getConfiguration, so
         * check if there is job to do first
         */
        if ( entryPresent( m_rootContext, RoleManager.ROLE ) )
        {
            return (RoleManager) m_rootContext.get( RoleManager.ROLE );
        }

        if ( !entryPresent( m_rootContext, ROLE_MANAGER_CONFIGURATION ) &&
                !entryPresent( m_rootContext, ROLE_MANAGER_CONFIGURATION_URI ) )
        {
            return null;
        }

        Configuration roleConfig =
                getConfiguration( ROLE_MANAGER_CONFIGURATION, ROLE_MANAGER_CONFIGURATION_URI );

        if ( roleConfig == null )
        {
            // Something went wrong, but the error has already been reported
            return null;
        }

        // Lookup the context class loader
        final ClassLoader classLoader =
                (ClassLoader) m_rootContext.get( ClassLoader.class.getName() );

        // Create a logger for the role manager
        final Logger rmLogger = m_loggerManager.getLoggerForCategory(
                roleConfig.getAttribute( "logger", "system.roles" ) );

        // Create a parent role manager with all the default roles
        final FortressRoleManager frm = new FortressRoleManager( null, classLoader );
        frm.enableLogging( rmLogger.getChildLogger( "defaults" ) );
        frm.initialize();

        // Create a role manager with the configured roles
        final ConfigurableRoleManager rm = new ConfigurableRoleManager( frm );
        rm.enableLogging( rmLogger );
        rm.configure( roleConfig );

        assumeOwnership( rm );
        return rm;
    }

    protected void initializeMetaInfoManager() throws Exception
    {
        boolean mmSupplied = false;

        try
        {
            m_metaInfoManager = (MetaInfoManager) m_rootContext.get( MetaInfoManager.ROLE );
            mmSupplied = true;
        }
        catch ( ContextException ce )
        {
            // okay, we will create one
        }

        RoleManager roleManager = obtainRoleManager();
        final boolean rmSupplied = roleManager != null;

        if ( mmSupplied )
        {
            if ( rmSupplied )
            {
                getLogger().warn( "MetaInfoManager found, ignoring RoleManager" );
            }

            // Otherwise everything is ok, and we continue on (i.e. return)
        }
        else
        {
            final ClassLoader classLoader =
                    (ClassLoader) m_rootContext.get( ClassLoader.class.getName() );

            if ( !rmSupplied )
            {
                final FortressRoleManager newRoleManager =
                        new FortressRoleManager( null, classLoader );
                newRoleManager.enableLogging(
                        m_loggerManager.getLoggerForCategory( "system.roles.defaults" ) );
                newRoleManager.initialize();

                roleManager = newRoleManager;
            }

            final ServiceMetaManager metaManager =
                    new ServiceMetaManager( new Role2MetaInfoManager( roleManager ), classLoader );

            metaManager.enableLogging( m_loggerManager.getLoggerForCategory( "system.meta" ) );
            metaManager.initialize();
            assumeOwnership( metaManager );
            m_metaInfoManager = metaManager;
        }
    }

    /**
     * Initialize the default source resolver
     *
     * @throws Exception when there is an error.
     */
    protected void initializeDefaultSourceResolver() throws Exception
    {
        final DefaultServiceManager manager = new DefaultServiceManager();
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

        m_defaultSourceResolver = resolver;
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
        final DefaultServiceManager manager = new EAServiceManager( parent, getLogger().getChildLogger("compat") );

        /**
         * We assume that if there is a parent ServiceManager provided,
         * there is a SourceResolver mounted there. And if there
         * is none, then it is the true caller's intetion.
         * However it is hard to imagine how that could be usefull
         * except for testing purposes.
         */

        if ( parent == null )
        {
            manager.put( SourceResolver.ROLE, m_defaultSourceResolver );
        }

        Object lem = get( m_rootContext, LifecycleExtensionManager.ROLE, null);

        /**
         * Role manager won't be passed here as it is now only
         * an utility for reading ECM role files.
         */

        manager.put( LoggerManager.ROLE, m_loggerManager );
        manager.put( Sink.ROLE, m_sink );
        manager.put( MetaInfoManager.ROLE, m_metaInfoManager );
        manager.put( PoolManager.ROLE, m_poolManager );
        manager.put( InstrumentManager.ROLE, m_instrumentManager );

        if ( lem != null )
        {
            manager.put( LifecycleExtensionManager.ROLE, lem );
        }

        manager.makeReadOnly();

        m_containerManagerContext.put( SERVICE_MANAGER, manager );
        m_childContext.put( SERVICE_MANAGER, manager );
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
            getLogger().debug( "A configuration URI was not specified: " + uriKey );
            return null;
        }

        Source src = null;
        try
        {
            src = m_defaultSourceResolver.resolveURI( configUri );
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
            m_defaultSourceResolver.release( src );
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
            m_loggerManager = (LoggerManager) m_rootContext.get( LoggerManager.ROLE );
        }
        catch ( ContextException ce )
        {
            // Should we set one up?
            // Try to get a configuration for it...
            Configuration loggerManagerConfig =
                    getConfiguration( LOGGER_MANAGER_CONFIGURATION,
                            LOGGER_MANAGER_CONFIGURATION_URI );

            boolean log4j = false;

            if ( loggerManagerConfig == null )
            {
                // Create an empty configuration so that
                // a default logger can be created.
                loggerManagerConfig = EMPTY_CONFIG;
            }
            else
            {
                /*
                 * We rely on namespace handing being turned off in DefaultConfiguration
                 * builder here. TODO: add code that tests
                 * root element for name "configuration" and for the correct Log4J
                 * configuration namespace (not currently known to me - Anton Tagunov)
                 * to survive if a namespace-enabled configuration has been passed to us.
                 */
                final String version = loggerManagerConfig.getAttribute( "version", null );
                if ( "log4j".equals( version ) )
                {
                    log4j = true;
                }
                else if ( "log4j:configuration".equals( loggerManagerConfig.getName() ) )
                {
                    log4j = true;
                }
            }

            final String lmDefaultLoggerName =
                    (String) get( m_rootContext, ContextManagerConstants.LOG_CATEGORY, "fotress" );
            final String lmLoggerName = loggerManagerConfig.getAttribute( "logger",
                    log4j ? "system.log4j" : "system.logkit" );

            if ( log4j )
            {
                /**
                 * Working around a weird compilation problem: with JDK 1.4.1-b21
                 * on Win2K couldn't get the following statement to compile:
                 *
                 * m_loggerManager = 
                 *         new Log4JConfLoggerManager( lmDefaultLoggerName, lmLoggerName );
                 *
                 * javac kept complaining:
                 *
                 * file org\apache\log4j\spi\LoggerRepository.class not found
                 *           new Log4JConfLoggerManager( lmDefaultLoggerName, lmLoggerName );
                 *
                 * ... ContextManager.java:xxx: cannot access org.apache.log4j.spi.LoggerRepository
                 * file org\apache\log4j\spi\LoggerRepository.class not found
                 * m_loggerManager = new Log4JConfLoggerManager( lmDefaultLoggerName, lmLoggerName );
                 *                 ^
                 *
                 * - Anton Tagunov
                 */
                m_loggerManager = Log4JConfLoggerManager.newInstance( 
                        lmDefaultLoggerName, lmLoggerName );
            }
            else // LogKitLoggerManager
            {
                // Setup the Logger Manager
                m_loggerManager = new LogKitLoggerManager( 
                        lmDefaultLoggerName, lmLoggerName );
            }

            ContainerUtil.enableLogging( m_loggerManager, getLogger() );
            ContainerUtil.contextualize( m_loggerManager, m_rootContext );
            ContainerUtil.configure( m_loggerManager, loggerManagerConfig );
            ContainerUtil.start( m_loggerManager );
            assumeOwnership( m_loggerManager );
        }

        // Since we now have a LoggerManager, we can update the this.logger field
        // if it is null and start logging to the "right" logger.

        if ( m_logger == null )
        {
            getLogger().debug( "Switching to default Logger provided by LoggerManager." );

            m_logger = m_loggerManager.getDefaultLogger();
        }

        // pass our own logger to the ContainerManager
        m_containerManagerContext.put( LOGGER, m_logger );
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
            m_instrumentManager = (InstrumentManager) m_rootContext.get( InstrumentManager.ROLE );
        }
        catch ( ContextException ce )
        {
            // Should we set one up?
            // Try to get a configuration for it...
            Configuration instrumentConfig = getConfiguration( INSTRUMENT_MANAGER_CONFIGURATION,
                    INSTRUMENT_MANAGER_CONFIGURATION_URI );
            if ( instrumentConfig == null )
            {
                // No config.
                instrumentConfig = EMPTY_CONFIG;
            }

            // Get the logger for the instrument manager
            final Logger imLogger = m_loggerManager.getLoggerForCategory(
                    instrumentConfig.getAttribute( "logger", "system.instrument" ) );

            // Set up the Instrument Manager
            final DefaultInstrumentManager instrumentManager = new DefaultInstrumentManager();
            instrumentManager.enableLogging( imLogger );
            instrumentManager.configure( instrumentConfig );
            instrumentManager.initialize();

            assumeOwnership( instrumentManager );

            m_instrumentManager = instrumentManager;
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
            if ( obj instanceof LoggerManager ) retVal = 3;

            return retVal;
        }
    }

    /**
     * This is a compatibility thing for early adopters of Fortress so that they will use the
     * correct ROLE for the CommandSink.
     */
    private static final class EAServiceManager extends DefaultServiceManager
    {
        private final Logger m_ealogger;

        public EAServiceManager( ServiceManager parent, Logger logger )
        {
            super( parent );
            m_ealogger = logger;
        }

        public Object lookup(String role) throws ServiceException
        {
            if ( Queue.ROLE.equals( role ) )
            {
                m_ealogger.info("Using deprecated role (Queue.ROLE) for the Command Sink.  Use \"Sink.ROLE\" instead.");
                return lookup(Sink.ROLE);
            }

            return super.lookup( role );
        }

        public boolean hasService(String role)
        {
            if (Queue.ROLE.equals( role ) ) return hasService(Sink.ROLE);

            return super.hasService( role );
        }
    }
}

