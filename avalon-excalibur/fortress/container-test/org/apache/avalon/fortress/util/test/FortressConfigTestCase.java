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
package org.apache.avalon.fortress.util.test;

import junit.framework.TestCase;
import org.apache.avalon.excalibur.logger.DefaultLoggerManager;
import org.apache.avalon.excalibur.logger.LoggerManager;
import org.apache.avalon.fortress.ContainerManagerConstants;
import org.apache.avalon.fortress.RoleManager;
import org.apache.avalon.fortress.impl.extensions.test.TestInstrumentManager;
import org.apache.avalon.fortress.impl.role.FortressRoleManager;
import org.apache.avalon.fortress.util.ContextManagerConstants;
import org.apache.avalon.fortress.util.FortressConfig;
import org.apache.avalon.fortress.util.LifecycleExtensionManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.service.DefaultServiceManager;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.impl.DefaultQueue;
import org.apache.excalibur.instrument.InstrumentManager;
import org.apache.excalibur.mpool.DefaultPoolManager;
import org.apache.excalibur.mpool.PoolManager;

import java.io.File;

/**
 * FortressConfigTestCase does XYZ
 *
 * @author <a href="bloritsch.at.apache.org">Berin Loritsch</a>
 * @version CVS $ Revision: 1.1 $
 */
public class FortressConfigTestCase extends TestCase
{
    private FortressConfig m_config;

    public FortressConfigTestCase( String name )
    {
        super( name );
    }

    public void setUp() throws Exception
    {
        m_config = new FortressConfig( FortressConfig.createDefaultConfig() );
        m_config.setCommandSink( new DefaultQueue() );
        m_config.setContextClassLoader( FortressConfigTestCase.class.getClassLoader() );
        m_config.setInstrumentManager( new TestInstrumentManager() );
        m_config.setLifecycleExtensionManager( new LifecycleExtensionManager() );
        m_config.setLoggerCategory( "test" );
        m_config.setLoggerManager( new DefaultLoggerManager() );
        m_config.setNumberOfThreadsPerCPU( 10 );
        m_config.setPoolManager( new DefaultPoolManager() );
        m_config.setRoleManager( new FortressRoleManager() );
        m_config.setServiceManager( new DefaultServiceManager() );
        m_config.setThreadTimeout( 50 );
    }

    public void testFortressConfigUsingURI() throws Exception
    {
        m_config.setContainerClass( FullLifecycleComponent.class.getName() );
        m_config.setContainerConfiguration( "resource://config.xml" );
        m_config.setContextDirectory( "/" );
        m_config.setWorkDirectory( "/" );
        m_config.setInstrumentManagerConfiguration( "resource://config.xml" );
        m_config.setLoggerManagerConfiguration( "resource://config.xml" );
        m_config.setRoleManagerConfiguration( "resource://config.xml" );

        checkContext( m_config.getContext(), true );
    }

    public void testFortressConfigUsingObject() throws Exception
    {
        m_config.setContainerClass( FullLifecycleComponent.class );
        m_config.setContainerConfiguration( new DefaultConfiguration( "test" ) );
        m_config.setContextDirectory( new File( "/" ) );
        m_config.setWorkDirectory( new File( "/" ) );
        m_config.setInstrumentManagerConfiguration( new DefaultConfiguration( "test" ) );
        m_config.setLoggerManagerConfiguration( new DefaultConfiguration( "test" ) );
        m_config.setRoleManagerConfiguration( new DefaultConfiguration( "test" ) );

        checkContext( m_config.getContext(), false );
    }

    private void checkContext( Context context, boolean useURI ) throws Exception
    {
        assertNotNull( context.get( Sink.ROLE ) );
        assertInstanceof( context.get( Sink.ROLE ), Sink.class );

        assertNotNull( context.get( ContainerManagerConstants.CONTAINER_CLASS ) );
        assertInstanceof( context.get( ContainerManagerConstants.CONTAINER_CLASS ), Class.class );
        assertEquals( FullLifecycleComponent.class, context.get( ContainerManagerConstants.CONTAINER_CLASS ) );

        assertNotNull( context.get( ClassLoader.class.getName() ) );
        assertInstanceof( context.get( ClassLoader.class.getName() ), ClassLoader.class );

        assertNotNull( context.get( ContextManagerConstants.CONTEXT_DIRECTORY ) );
        assertInstanceof( context.get( ContextManagerConstants.CONTEXT_DIRECTORY ), File.class );
        assertEquals( new File( "/" ), context.get( ContextManagerConstants.CONTEXT_DIRECTORY ) );

        assertNotNull( context.get( ContextManagerConstants.WORK_DIRECTORY ) );
        assertInstanceof( context.get( ContextManagerConstants.WORK_DIRECTORY ), File.class );
        assertEquals( new File( "/" ), context.get( ContextManagerConstants.WORK_DIRECTORY ) );

        assertNotNull( context.get( InstrumentManager.ROLE ) );
        assertInstanceof( context.get( InstrumentManager.ROLE ), InstrumentManager.class );

        assertNotNull( context.get( LifecycleExtensionManager.ROLE ) );
        assertInstanceof( context.get( LifecycleExtensionManager.ROLE ), LifecycleExtensionManager.class );

        assertNotNull( context.get( ContextManagerConstants.LOG_CATEGORY ) );
        assertInstanceof( context.get( ContextManagerConstants.LOG_CATEGORY ), String.class );
        assertEquals( "test", context.get( ContextManagerConstants.LOG_CATEGORY ) );

        assertNotNull( context.get( LoggerManager.ROLE ) );
        assertInstanceof( context.get( LoggerManager.ROLE ), LoggerManager.class );

        assertNotNull( context.get( ContextManagerConstants.THREADS_CPU ) );
        assertInstanceof( context.get( ContextManagerConstants.THREADS_CPU ), Integer.class );
        assertEquals( new Integer( 10 ), context.get( ContextManagerConstants.THREADS_CPU ) );

        assertNotNull( context.get( PoolManager.ROLE ) );
        assertInstanceof( context.get( PoolManager.ROLE ), PoolManager.class );

        assertNotNull( context.get( RoleManager.ROLE ) );
        assertInstanceof( context.get( RoleManager.ROLE ), RoleManager.class );

        assertNotNull( context.get( ContextManagerConstants.SERVICE_MANAGER ) );
        assertInstanceof( context.get( ContextManagerConstants.SERVICE_MANAGER ), ServiceManager.class );


        assertNotNull( context.get( ContextManagerConstants.THREAD_TIMEOUT ) );
        assertInstanceof( context.get( ContextManagerConstants.THREAD_TIMEOUT ), Long.class );
        assertEquals( new Long( 50 ), context.get( ContextManagerConstants.THREAD_TIMEOUT ) );

        if ( useURI )
        {
            assertNotNull( context.get( ContextManagerConstants.CONFIGURATION_URI ) );
            assertInstanceof( context.get( ContextManagerConstants.CONFIGURATION_URI ), String.class );
            assertEquals( "resource://config.xml", context.get( ContextManagerConstants.CONFIGURATION_URI ) );

            assertNotNull( context.get( ContextManagerConstants.INSTRUMENT_MANAGER_CONFIGURATION_URI ) );
            assertInstanceof( context.get( ContextManagerConstants.INSTRUMENT_MANAGER_CONFIGURATION_URI ), String.class );
            assertEquals( "resource://config.xml", context.get( ContextManagerConstants.INSTRUMENT_MANAGER_CONFIGURATION_URI ) );

            assertNotNull( context.get( ContextManagerConstants.LOGGER_MANAGER_CONFIGURATION_URI ) );
            assertInstanceof( context.get( ContextManagerConstants.LOGGER_MANAGER_CONFIGURATION_URI ), String.class );
            assertEquals( "resource://config.xml", context.get( ContextManagerConstants.LOGGER_MANAGER_CONFIGURATION_URI ) );

            assertNotNull( context.get( ContextManagerConstants.ROLE_MANAGER_CONFIGURATION_URI ) );
            assertInstanceof( context.get( ContextManagerConstants.ROLE_MANAGER_CONFIGURATION_URI ), String.class );
            assertEquals( "resource://config.xml", context.get( ContextManagerConstants.ROLE_MANAGER_CONFIGURATION_URI ) );
        }
        else
        {
            assertNotNull( context.get( ContextManagerConstants.CONFIGURATION ) );
            assertInstanceof( context.get( ContextManagerConstants.CONFIGURATION ), Configuration.class );
            assertEquals( "test", ( (Configuration) context.get( ContextManagerConstants.CONFIGURATION ) ).getName() );

            assertNotNull( context.get( ContextManagerConstants.INSTRUMENT_MANAGER_CONFIGURATION ) );
            assertInstanceof( context.get( ContextManagerConstants.INSTRUMENT_MANAGER_CONFIGURATION ), Configuration.class );
            assertEquals( "test", ( (Configuration) context.get( ContextManagerConstants.INSTRUMENT_MANAGER_CONFIGURATION ) ).getName() );

            assertNotNull( context.get( ContextManagerConstants.LOGGER_MANAGER_CONFIGURATION ) );
            assertInstanceof( context.get( ContextManagerConstants.LOGGER_MANAGER_CONFIGURATION ), Configuration.class );
            assertEquals( "test", ( (Configuration) context.get( ContextManagerConstants.LOGGER_MANAGER_CONFIGURATION ) ).getName() );

            assertNotNull( context.get( ContextManagerConstants.ROLE_MANAGER_CONFIGURATION ) );
            assertInstanceof( context.get( ContextManagerConstants.ROLE_MANAGER_CONFIGURATION ), Configuration.class );
            assertEquals( "test", ( (Configuration) context.get( ContextManagerConstants.ROLE_MANAGER_CONFIGURATION ) ).getName() );
        }
    }

    protected void assertInstanceof( Object obj, Class klass )
    {
        assertTrue( obj.getClass().getName() + " is not an instance of " + klass.getName(),
            klass.isAssignableFrom( obj.getClass() ) );
    }
}
