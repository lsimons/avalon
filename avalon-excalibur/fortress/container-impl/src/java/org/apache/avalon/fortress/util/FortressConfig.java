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

import org.apache.avalon.excalibur.logger.LoggerManager;
import org.apache.avalon.fortress.impl.DefaultContainer;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.command.ThreadManager;
import org.apache.excalibur.instrument.InstrumentManager;
import org.apache.excalibur.mpool.PoolManager;

import java.io.File;
import java.net.URL;

/**
 * Helper class to create a m_context for the ContextManager.
 * @version CVS $Revision: 1.20 $ $Date: 2003/06/12 18:44:46 $
 */
public final class FortressConfig
{
    private final DefaultContext m_context;

    /**
     * Creates a m_context builder and initializes it with default values.
     * The default values are:
     *
     * <ul>
     * <li>CONTAINER_CLASS = "org.apache.avalon.fortress.impl.DefaultContainer" </li>
     * <li>THREADS_CPU =  2</li>
     * <li>THREAD_TIMEOUT = 1000</li>
     * <li>CONTEXT_DIRECTORY = "../"</li>
     * <li>WORK_DIRECTORY = "/tmp"</li>
     * <li>LOG_CATEGORY = "fortress"</li>
     * <li>CONTEXT_CLASSLOADER = the thread m_context class loader</li>
     * </ul>
     */
    public FortressConfig()
    {
        this( createDefaultConfig() );
    }

    /**
     * Creates a m_context builder and initializes it with default values.
     *
     * @param parent parent m_context with default values.
     */
    public FortressConfig( final Context parent )
    {
        m_context = new OverridableContext( parent );
    }

    /**
     * Creates a default m_context.
     */
    public static final Context createDefaultConfig()
    {
        return createDefaultConfig( Thread.currentThread().getContextClassLoader() );
    }

    /**
     * Creates a default m_context.
     */
    public static final Context createDefaultConfig( final ClassLoader classLoader )
    {
        final DefaultContext defaultContext = new DefaultContext();

        try
        {
            defaultContext.put( ContextManagerConstants.CONTAINER_CLASS,
                DefaultContainer.class );
            defaultContext.put( ContextManagerConstants.COMMAND_FAILURE_HANDLER_CLASS,
                FortressCommandFailureHandler.class );
        }
        catch ( Exception e )
        {
            // ignore
        }

        final File contextDir = new File( System.getProperty( "user.dir" ) );
        final File workDir = new File( System.getProperty( "java.io.tmpdir" ) );

        defaultContext.put( ContextManagerConstants.THREADS_CPU, new Integer( 2 ) );
        defaultContext.put( ContextManagerConstants.THREAD_TIMEOUT, new Long( 1000 ) );
        defaultContext.put( ContextManagerConstants.CONTEXT_DIRECTORY, contextDir );
        defaultContext.put( ContextManagerConstants.WORK_DIRECTORY, workDir );
        defaultContext.put( ContextManagerConstants.LOG_CATEGORY, "fortress" );
        defaultContext.put( ClassLoader.class.getName(), classLoader );
        defaultContext.put( ContextManagerConstants.CONFIGURATION_URI, "conf/system.xconf" );
        defaultContext.put( ContextManagerConstants.LOGGER_MANAGER_CONFIGURATION_URI, "conf/logkit.xconf" );

        defaultContext.makeReadOnly();

        return defaultContext;
    }

    /**
     * Finalizes the m_context and returns it.
     */
    public Context getContext()
    {
        m_context.makeReadOnly();
        return m_context;
    }

    public void setCommandSink( final Sink commandSink )
    {
        m_context.put( Sink.ROLE, commandSink );
    }

    public void setServiceManager( final ServiceManager componentManager )
    {
        m_context.put( ContextManagerConstants.SERVICE_MANAGER, componentManager );
    }

    public void setLifecycleExtensionManager( final LifecycleExtensionManager extensionManager )
    {
        m_context.put( LifecycleExtensionManager.ROLE, extensionManager );
    }

    public void setContainerClass( final String containerClass )
        throws ClassNotFoundException
    {
        ClassLoader classLoader;
        try
        {
            classLoader = (ClassLoader) m_context.get( ClassLoader.class.getName() );
        }
        catch ( ContextException ce )
        {
            classLoader = Thread.currentThread().getContextClassLoader();
        }

        setContainerClass( classLoader.loadClass( containerClass ) );
    }

    public void setContainerClass( final Class containerClass )
    {
        m_context.put( ContextManagerConstants.CONTAINER_CLASS, containerClass );
    }
    
    /**
     * Sets a class whose instance will be used to override the default
     *  CommandFailureHandler used by the container.  This makes it possible
     *  for applications to decide how they wish to handle failures.
     *
     * @param commandFailureHandlerClass Name of the CommandFailureHandler class to use.
     */
    public void setCommandFailureHandlerClass( final String commandFailureHandlerClass )
        throws ClassNotFoundException
    {
        ClassLoader classLoader;
        try
        {
            classLoader = (ClassLoader) m_context.get( ClassLoader.class.getName() );
        }
        catch ( ContextException ce )
        {
            classLoader = Thread.currentThread().getContextClassLoader();
        }

        setCommandFailureHandlerClass( classLoader.loadClass( commandFailureHandlerClass ) );
    }

    /**
     * Sets a class whose instance will be used to override the default
     *  CommandFailureHandler used by the container.  This makes it possible
     *  for applications to decide how they wish to handle failures.
     *
     * @param commandFailureHandlerClass The CommandFailureHandler class to use.
     */
    public void setCommandFailureHandlerClass( final Class commandFailureHandlerClass )
    {
        m_context.put(
            ContextManagerConstants.COMMAND_FAILURE_HANDLER_CLASS, commandFailureHandlerClass );
    }

    public void setContainerConfiguration( final Configuration config )
    {
        m_context.put( ContextManagerConstants.CONFIGURATION, config );
        m_context.put( ContextManagerConstants.CONFIGURATION_URI, null );
    }

    public void setContainerConfiguration( final String location )
    {
        m_context.put( ContextManagerConstants.CONFIGURATION_URI, location );
    }

    public void setContextClassLoader( final ClassLoader loader )
    {
        m_context.put( ClassLoader.class.getName(), loader );
    }

    public void setContextDirectory( final File file )
    {
        m_context.put( ContextManagerConstants.CONTEXT_DIRECTORY, file );
    }

    public void setContextDirectory( final String directory )
    {
        m_context.put( ContextManagerConstants.CONTEXT_DIRECTORY, new File( directory ) );
    }

    public void setContextRootURL( final URL url )
    {
        m_context.put( ContextManagerConstants.CONTEXT_DIRECTORY, url );
    }

    public void setLoggerCategory( final String category )
    {
        m_context.put( ContextManagerConstants.LOG_CATEGORY, category );
    }

    public void setLoggerManager( final LoggerManager logManager )
    {
        m_context.put( LoggerManager.ROLE, logManager );
        m_context.put( ContextManagerConstants.LOGGER_MANAGER_CONFIGURATION, null );
        m_context.put( ContextManagerConstants.LOGGER_MANAGER_CONFIGURATION_URI, null );
    }

    public void setLoggerManagerConfiguration( final Configuration config )
    {
        m_context.put( ContextManagerConstants.LOGGER_MANAGER_CONFIGURATION, config );
        m_context.put( ContextManagerConstants.LOGGER_MANAGER_CONFIGURATION_URI, null );
    }

    public void setLoggerManagerConfiguration( final String location )
    {
        m_context.put( ContextManagerConstants.LOGGER_MANAGER_CONFIGURATION_URI, location );
    }

    public void setInstrumentManager( final InstrumentManager profiler )
    {
        m_context.put( InstrumentManager.ROLE, profiler );
        m_context.put( ContextManagerConstants.INSTRUMENT_MANAGER_CONFIGURATION, null );
        m_context.put( ContextManagerConstants.INSTRUMENT_MANAGER_CONFIGURATION_URI, null );
    }

    public void setInstrumentManagerConfiguration( final Configuration config )
    {
        m_context.put( ContextManagerConstants.INSTRUMENT_MANAGER_CONFIGURATION, config );
        m_context.put( ContextManagerConstants.INSTRUMENT_MANAGER_CONFIGURATION_URI, null );
    }

    public void setInstrumentManagerConfiguration( final String location )
    {
        m_context.put( ContextManagerConstants.INSTRUMENT_MANAGER_CONFIGURATION_URI, location );
    }

    public void setNumberOfThreadsPerCPU( final int numberOfThreads )
    {
        m_context.put( ContextManagerConstants.THREADS_CPU, new Integer( numberOfThreads ) );
    }

    public void setPoolManager( final PoolManager poolManager )
    {
        m_context.put( PoolManager.ROLE, poolManager );
    }

    public void setRoleManager( final org.apache.avalon.fortress.RoleManager roleManager )
    {
        m_context.put( org.apache.avalon.fortress.RoleManager.ROLE, roleManager );
    }

    public void setRoleManagerConfiguration( final Configuration config )
    {
        m_context.put( ContextManagerConstants.ROLE_MANAGER_CONFIGURATION, config );
        m_context.put( ContextManagerConstants.ROLE_MANAGER_CONFIGURATION_URI, null );
    }

    public void setRoleManagerConfiguration( final String location )
    {
        m_context.put( ContextManagerConstants.ROLE_MANAGER_CONFIGURATION_URI, location );
    }

    public void setThreadTimeout( final long timeout )
    {
        m_context.put( ContextManagerConstants.THREAD_TIMEOUT, new Long( timeout ) );
    }

    public void setWorkDirectory( final File file )
    {
        m_context.put( ContextManagerConstants.WORK_DIRECTORY, file );
    }

    public void setWorkDirectory( final String directory )
    {
        setWorkDirectory( new File( directory ) );
    }

    public void setThreadManager( final ThreadManager threadManager )
    {
        m_context.put( ThreadManager.ROLE, threadManager );
    }
}
