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

import org.apache.avalon.excalibur.logger.LoggerManager;
import org.apache.avalon.fortress.InitializationException;
import org.apache.avalon.fortress.MetaInfoManager;
import org.apache.avalon.fortress.RoleManager;
import org.apache.avalon.fortress.util.ContextManager;
import org.apache.avalon.fortress.util.LifecycleExtensionManager;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.Loggable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.DefaultServiceManager;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.event.Queue;
import org.apache.excalibur.instrument.InstrumentManager;
import org.apache.excalibur.mpool.PoolManager;

/**
 * This is the default implementation of the
 * {@link org.apache.avalon.fortress.ContainerManager} interface.
 * See that interface for a description.
 *
 * @author <a href="mailto:dev@avalon.apache.org">The Avalon Team</a>
 * @version CVS $Revision: 1.18 $ $Date: 2003/05/22 14:56:07 $
 */
public class DefaultContainerManager
    implements Initializable, Disposable, org.apache.avalon.fortress.ContainerManager, org.apache.avalon.fortress.ContainerManagerConstants
{
    private final ContextManager m_contextManager;
    private final Logger m_logger;
    private Object m_containerInstance;

    public DefaultContainerManager( final ContextManager contextManager )
    {
        this( contextManager, null );
    }

    public DefaultContainerManager( final ContextManager contextManager,
                                    final Logger logger )
    {
        m_contextManager = contextManager;
        m_logger = ( logger == null ?
            createLoggerFromContext( m_contextManager.getContainerManagerContext() ) : logger );
    }

    public DefaultContainerManager( final Context initParameters )
    {
        this( initParameters, null );
    }

    public DefaultContainerManager( final Context initParameters,
                                    final Logger logger )
    {
        this( getInitializedContextManager( initParameters, logger ), logger );
    }

    /**
     * Creates and initializes a contextManager given an initialization context.
     *  This is necessary so that these operations can complete before the
     *  super constructor has been executed.
     */
    private static ContextManager getInitializedContextManager( final Context initParameters,
                                                                Logger logger )
    {
        // The context manager will use an internal coonsole logger if logger is null.
        final ContextManager contextManager = new ContextManager( initParameters, logger );
        try
        {
            contextManager.initialize();
            return contextManager;
        }
        catch ( Exception e )
        {
            if ( logger == null )
            {
                logger = new ConsoleLogger( ConsoleLogger.LEVEL_INFO );
            }
            logger.fatalError( "Unable to initialize the contextManager.", e );

            // What should happen now.  There needs to be a failure mode here.
            // As is, this will result in an NPE, but it can't really be allowed
            // to continue without having been initialized.
            return null;
        }
    }

    protected Logger createLoggerFromContext( final Context initParameters )
    {
        try
        {
            final LoggerManager loggerManager = (LoggerManager) initParameters.get( LoggerManager.ROLE );
            return loggerManager.getDefaultLogger();
        }
        catch ( ContextException ce )
        {
            final Logger consoleLogger = new ConsoleLogger();
            consoleLogger.error( "ContainerManager could not obtain logger manager from context "
                + "(this should not happen). Using console instead." );
            return consoleLogger;
        }
    }

    /**
     * Initialize the ContainerManager
     */
    public void initialize() throws Exception
    {
        initializeContainer();
    }

    protected void initializeContainer() throws InitializationException
    {
        if ( null == m_containerInstance )
        {
            createContainer();
        }
    }

    private void createContainer()
        throws InitializationException
    {
        final Context managerContext =
            m_contextManager.getContainerManagerContext();
        final Object instance;
        try
        {
            final Class clazz = (Class) managerContext.get( CONTAINER_CLASS );
            instance = clazz.newInstance();
        }
        catch ( Exception e )
        {
            final String message =
                "Cannot set up impl. Unable to create impl class";
            throw new InitializationException( message, e );
        }

        if ( instance instanceof Loggable )
        {
            throw new InitializationException( "Loggable containers are not supported" );
        }

        if ( instance instanceof Composable )
        {
            throw new InitializationException( "Composable containers are not supported" );
        }

        try
        {
            ContainerUtil.enableLogging( instance, m_logger );
            ContainerUtil.contextualize( instance, managerContext );

            final ServiceManager serviceManager =
                createServiceManager( managerContext );

            ContainerUtil.service( instance, serviceManager );

            final Configuration config =
                (Configuration) getContextEntry( managerContext, CONFIGURATION );
            ContainerUtil.configure( instance, config );

            final Parameters parameters =
                (Parameters) getContextEntry( managerContext, PARAMETERS );
            ContainerUtil.parameterize( instance, parameters );

            ContainerUtil.initialize( instance );
            ContainerUtil.start( instance );

            m_containerInstance = instance;
        }
        catch ( Exception e )
        {
            final String message =
                "Cannot set up Container. Startup lifecycle failure";
            throw new InitializationException( message, e );
        }
    }

    private ServiceManager createServiceManager( final Context managerContext )
    {
        final ServiceManager smanager =
            (ServiceManager) getContextEntry( managerContext, SERVICE_MANAGER );
        final DefaultServiceManager serviceManager = new DefaultServiceManager( smanager );

        addService( Queue.ROLE, managerContext, serviceManager );
        addService( LoggerManager.ROLE, managerContext, serviceManager );
        addService( PoolManager.ROLE, managerContext, serviceManager );
        addService( InstrumentManager.ROLE, managerContext, serviceManager );
        addService( MetaInfoManager.ROLE, managerContext, serviceManager );
        addService( RoleManager.ROLE, managerContext, serviceManager );
        addService( LifecycleExtensionManager.ROLE, managerContext, serviceManager );
        serviceManager.makeReadOnly();

        return serviceManager;
    }

    private void addService( final String role,
                             final Context context,
                             final DefaultServiceManager serviceManager )
    {
        try
        {
            final Object object = context.get( role );
            serviceManager.put( role, object );
        }
        catch ( ContextException e )
        {
        }
    }

    /**
     * Retrieve an entry from context if it exists, else return null.
     *
     * @param context the context
     * @param key the key
     * @return the entry
     */
    private Object getContextEntry( final Context context, final String key )
    {
        try
        {
            return context.get( key );
        }
        catch ( ContextException e )
        {
            return null;
        }
    }

    protected void disposeContainer()
    {
        if ( null != m_containerInstance )
        {
            try
            {
                ContainerUtil.stop( m_containerInstance );
            }
            catch ( Exception e )
            {
                if ( getLogger().isWarnEnabled() )
                {
                    getLogger().warn( "Caught an exception when stopping the Container, "
                        + "continuing with shutdown", e );
                }
            }

            ContainerUtil.dispose( m_containerInstance );
            m_containerInstance = null;
        }
    }

    /**
     * Dispose of the ContainerManager and managed Container
     */
    public void dispose()
    {
        disposeContainer();
        m_contextManager.dispose();
    }

    /**
     * Get a reference to your Container.  Typically, you would cast this to
     * whatever interface you will use to interact with it.
     */
    public Object getContainer()
    {
        return m_containerInstance;
    }

    /**
     * Allows to get the logger and associated hierarchy for logging.
     * @return Logger
     */
    public final Logger getLogger()
    {
        // (mschier) was protected.
        // Made public to get to the logger at the impl setup level.
        return m_logger;
    }
}
