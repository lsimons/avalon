/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) @year@ The Apache Software Foundation. All rights reserved.

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

import java.io.File;
import org.apache.avalon.excalibur.logger.LoggerManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.event.Queue;
import org.apache.excalibur.instrument.InstrumentManager;
import org.apache.excalibur.mpool.PoolManager;

/**
 * Helper class to create a context for the ContextManager.
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.3 $ $Date: 2003/02/25 15:23:46 $
 */
public class ContextBuilder
    implements ContextManagerConstants
{
    private final FortressConfig m_config;

    /**
     * Creates a context builder and initializes it with default values.
     * The default values are:
     *
     * <ul>
     * <li>CONTAINER_CLASS = "org.apache.avalon.fortress.impl.DefaultContainer" </li>
     * <li>THREADS_CPU =  2</li>
     * <li>THREAD_TIMEOUT = 1000</li>
     * <li>CONTEXT_DIRECTORY = "../"</li>
     * <li>WORK_DIRECTORY = "/tmp"</li>
     * <li>LOG_CATEGORY = "fortress"</li>
     * <li>CONTEXT_CLASSLOADER = the thread context class loader</li>
     * </ul>
     */
    public ContextBuilder()
    {
        m_config = new FortressConfig();
    }

    /**
     * Creates a context builder and initializes it with default values.
     *
     * @param parent parent context with default values.
     */
    public ContextBuilder( final Context parent )
    {
        m_config = new FortressConfig( parent );
    }

    /**
     * Creates a default context.
     * @return a context
     */
    public static final Context createDefaultContext()
    {
        return FortressConfig.createDefaultConfig();
    }

    /**
     * Creates a default context.
     * @return a context
     */
    public static final Context createDefaultContext( ClassLoader classLoader )
    {
        return FortressConfig.createDefaultConfig( classLoader );
    }

    /**
     * Finalizes the context and returns it.
     * @return a context
     */
    public Context getContext()
    {
        return m_config.getContext();
    }

    public ContextBuilder setCommandQueue( Queue commandQueue )
    {
        m_config.setCommandQueue( commandQueue );
        return this;
    }

    public ContextBuilder setServiceManagerParent( ServiceManager serviceManager )
    {
        m_config.setServiceManagerParent( serviceManager );
        return this;
    }

    public ContextBuilder setServiceManager( ServiceManager serviceManager )
    {
        m_config.setServiceManager( serviceManager );
        return this;
    }

    public ContextBuilder setContainerClass( String containerClass )
        throws ClassNotFoundException
    {
        m_config.setContainerClass( containerClass );
        return this;
    }

    public ContextBuilder setContainerClass( Class containerClass )
    {
        m_config.setContainerClass( containerClass );
        return this;
    }

    public ContextBuilder setContainerConfiguration( Configuration config )
    {
        m_config.setContainerConfiguration( config );
        return this;
    }

    public ContextBuilder setContainerConfiguration( String location )
    {
        m_config.setContainerConfiguration( location );
        return this;
    }

    public ContextBuilder setAssemblyConfiguration( Configuration config )
    {
        m_config.setAssemblyConfiguration( config );
        return this;
    }

    public ContextBuilder setAssemblyConfiguration( String location )
    {
        m_config.setAssemblyConfiguration( location );
        return this;
    }

    public ContextBuilder setContextClassLoader( ClassLoader loader )
    {
        m_config.setContextClassLoader( loader );
        return this;
    }

    public ContextBuilder setContextDirectory( File file )
    {
        m_config.setContextDirectory( file );
        return this;
    }

    public ContextBuilder setContextDirectory( String directory )
    {
        m_config.setContextDirectory( directory );
        return this;
    }

    public ContextBuilder setLoggerCategory( String category )
    {
        m_config.setLoggerCategory( category );
        return this;
    }

    public ContextBuilder setLoggerManager( LoggerManager logManager )
    {
        m_config.setLoggerManager( logManager );
        return this;
    }

    public ContextBuilder setLoggerManagerConfiguration( Configuration config )
    {
        m_config.setLoggerManagerConfiguration( config );
        return this;
    }

    public ContextBuilder setLoggerManagerConfiguration( String location )
    {
        m_config.setLoggerManagerConfiguration( location );
        return this;
    }

    public ContextBuilder setInstrumentManager( InstrumentManager profiler )
    {
        m_config.setInstrumentManager( profiler );
        return this;
    }

    public ContextBuilder setInstrumentManagerConfiguration( Configuration config )
    {
        m_config.setInstrumentManagerConfiguration( config );
        return this;
    }

    public ContextBuilder setInstrumentManagerConfiguration( String location )
    {
        m_config.setInstrumentManagerConfiguration( location );
        return this;
    }

    public ContextBuilder setNumberOfThreadsPerCPU( int numberOfThreads )
    {
        m_config.setNumberOfThreadsPerCPU( numberOfThreads );
        return this;
    }

    public ContextBuilder setPoolManager( PoolManager poolManager )
    {
        m_config.setPoolManager( poolManager );
        return this;
    }

    public ContextBuilder setRoleManager( org.apache.avalon.fortress.RoleManager roleManager )
    {
        m_config.setRoleManager( roleManager );
        return this;
    }

    public ContextBuilder setRoleManagerConfiguration( Configuration config )
    {
        m_config.setRoleManagerConfiguration( config );
        return this;
    }

    public ContextBuilder setRoleManagerConfiguration( String location )
    {
        m_config.setRoleManagerConfiguration( location );
        return this;
    }

    public ContextBuilder setThreadTimeout( long timeout )
    {
        m_config.setThreadTimeout( timeout );
        return this;
    }

    public ContextBuilder setWorkDirectory( File file )
    {
        m_config.setWorkDirectory( file );
        return this;
    }

    public ContextBuilder setWorkDirectory( String directory )
    {
        m_config.setWorkDirectory( directory );
        return this;
    }
}
