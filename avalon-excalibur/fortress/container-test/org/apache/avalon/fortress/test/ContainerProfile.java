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
package org.apache.avalon.fortress.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.avalon.excalibur.component.ExcaliburComponentManager;
import org.apache.avalon.excalibur.logger.DefaultLogKitManager;
import org.apache.avalon.excalibur.monitor.Monitor;
import org.apache.avalon.excalibur.testcase.CascadingAssertionFailedError;
import org.apache.avalon.excalibur.testcase.LatchedThreadGroup;
import org.apache.avalon.fortress.ContainerManager;
import org.apache.avalon.fortress.impl.DefaultContainer;
import org.apache.avalon.fortress.impl.DefaultContainerManager;
import org.apache.avalon.fortress.util.FortressConfig;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.NullLogger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.WrapperServiceManager;
import org.apache.excalibur.xml.dom.DOMParser;

/**
 * Used as a basis for the PoolComparisonProfile Tests
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version $Id: ContainerProfile.java,v 1.5 2003/03/22 12:31:53 leosimons Exp $
 */
public final class ContainerProfile
    extends TestCase
{
    /**
     * The TEST_SIZE defines the overall size of the tests.  Decreasing this will
     *  decrease the time the test takes to run, but also decrease its efficiency.
     */
    protected static final int TEST_SIZE = 5000;
    protected static final int THREADS = 20;

    protected static Throwable m_throwable = null;
    protected static int m_getCount = 0;

    protected Logger m_logger;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public ContainerProfile( String name )
    {
        super( name );

        // Set to debug to see more useful information.
        m_logger = new ConsoleLogger( ConsoleLogger.LEVEL_INFO );
    }

    /*---------------------------------------------------------------
     * ECM vs. ContainerManager StartTimes
     *-------------------------------------------------------------*/
    /**
     * Compare the ECM and ContainerManager start times.
     */
    public void testCompare_ECM_ContainerManager_UseageTime()
        throws Exception
    {
        resetMemory(); // Start clean

        long ecmStart = System.currentTimeMillis();
        org.apache.log.Logger logKitLogger =
            org.apache.log.Hierarchy.getDefaultHierarchy().getLoggerFor( "test" );
        logKitLogger.setPriority( org.apache.log.Priority.INFO );
        ExcaliburComponentManager manager = new ExcaliburComponentManager();
        Context context = new DefaultContext();
        manager.setLogger( logKitLogger );
        manager.contextualize( context );
        DefaultLogKitManager logmanager = new DefaultLogKitManager();
        logmanager.setLogger( logKitLogger );
        logmanager.contextualize( context );
        logmanager.configure( getLogKitConfig() );
        manager.setLogKitManager( logmanager );
        manager.configure( getContainerConfig() );
        manager.initialize();
        long ecmStop = System.currentTimeMillis();
        long ecmDuration = ecmStop - ecmStart;

        resetMemory(); // Start clean

        long cmStart = System.currentTimeMillis();
        final FortressConfig config = new FortressConfig();
        config.setContextDirectory( "./" );
        config.setWorkDirectory( "./tmp" );
        config.setContainerClass( DefaultContainer.class );
        config.setContainerConfiguration( "resource://org/apache/avalon/fortress/test/ContainerProfile.xconf" );
        config.setLoggerManagerConfiguration( "resource://org/apache/avalon/fortress/test/ContainerProfile.xlog" );
        config.setRoleManagerConfiguration( "resource://org/apache/avalon/fortress/test/ContainerProfile.roles" );


        final ContainerManager cm = new DefaultContainerManager( config.getContext(), new NullLogger() );
        ContainerUtil.initialize( cm );
        DefaultContainer container = (DefaultContainer)cm.getContainer();
        assertNotNull( container );
        long cmStop = System.currentTimeMillis();
        long cmDuration = cmStop - cmStart;

        // Show a summary
        if( m_logger.isInfoEnabled() )
        {
            m_logger.info( "Test Case: ECM_ContainerManager_StartTime" );
            m_logger.info( "     ECM time = " + ecmDuration + "ms." );
            m_logger.info( "     ContainerManager time = " + cmDuration + "ms." );

            double mult;
            mult = ( cmDuration > 0 ? ( ecmDuration * 100 / cmDuration ) / 100.0 : Float.POSITIVE_INFINITY );
            m_logger.info( "  => ContainerManager is " + mult + " X as fast as ExcaliburComponentManager on init." );
            mult = ( ecmDuration > 0 ? ( cmDuration * 100 / ecmDuration ) / 100.0 : Float.POSITIVE_INFINITY );
            m_logger.info( "  => ExcaliburComponentManager is " + mult + " X as fast as ContainerManager on init." );
        }

        resetMemory();

        lookupTest( "Test Case: ECM_ContainerManager_UseageTime", container.getServiceManager(), new WrapperServiceManager( manager ) );

        resetMemory();

        ecmStart = System.currentTimeMillis();
        ContainerUtil.dispose( manager );
        ecmStop = System.currentTimeMillis();
        ecmDuration = ecmStop - ecmStart;

        resetMemory();

        cmStart = System.currentTimeMillis();
        ContainerUtil.dispose( cm );
        cmStop = System.currentTimeMillis();
        cmDuration = cmStop - cmStart;

        // Show a summary
        if( m_logger.isInfoEnabled() )
        {
            m_logger.info( "Test Case: ECM_ContainerManager_KillTime" );
            m_logger.info( "     ECM time = " + ecmDuration + "ms." );
            m_logger.info( "     ContainerManager time = " + cmDuration + "ms." );

            double mult;
            mult = ( cmDuration > 0 ? ( ecmDuration * 100 / cmDuration ) / 100.0 : Float.POSITIVE_INFINITY );
            m_logger.info( "  => ContainerManager is " + mult + " X as fast as ExcaliburComponentManager on dispose." );
            mult = ( ecmDuration > 0 ? ( cmDuration * 100 / ecmDuration ) / 100.0 : Float.POSITIVE_INFINITY );
            m_logger.info( "  => ExcaliburComponentManager is " + mult + " X as fast as ContainerManager on dispose." );
        }
    }

    /*---------------------------------------------------------------
     * Utility Methods
     *-------------------------------------------------------------*/
    protected void resetMemory()
    {
        System.gc();
        System.gc();

        // Let the system settle down.
        try
        {
            Thread.sleep( 50 );
        }
        catch( InterruptedException e )
        {
        }
        Runtime runtime = Runtime.getRuntime();
        m_logger.debug( "Memory: " + ( runtime.totalMemory() - runtime.freeMemory() ) );
    }

    /**
     * Get the LogKitManager Config file
     */
    protected Configuration getLogKitConfig()
        throws Exception
    {
        final String resourceName = this.getClass().getName().replace( '.', '/' ) + ".xlog";
        java.net.URL resource = this.getClass().getClassLoader().getResource( resourceName );
        return loadConfig( resource );
    }

    /**
     * Get the Container Config file
     */
    protected Configuration getContainerConfig()
        throws Exception
    {
        final String resourceName = this.getClass().getName().replace( '.', '/' ) + ".xconf";
        java.net.URL resource = this.getClass().getClassLoader().getResource( resourceName );
        return loadConfig( resource );
    }

    /**
     * Load Config
     */
    protected Configuration loadConfig( java.net.URL path )
        throws Exception
    {
        final DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        return builder.build( path.openStream() );
    }

    /**
     * Get the short class name
     */
    protected String getShortClassName( Object o )
    {
        String name = o.getClass().getName();
        int pos = name.lastIndexOf( '.' );
        if( pos > 0 )
        {
            name = name.substring( pos + 1 );
        }
        return name;
    }

    /**
     * The guts of the various test cases.  Will dispose the pools
     */
    protected void lookupTest( String name, ServiceManager cmA, ServiceManager cmB )
        throws Exception
    {
        m_logger.info( "Test Case: " + name );

        // Get the short class names
        final String cmAName = getShortClassName( cmA );
        final String cmBName = getShortClassName( cmB );

        // Start clean
        resetMemory();


        // Get the time for ecm
        final long cmADuration = getLookupRunTime( cmA );
        m_logger.info( "     " + cmAName + " time = " + cmADuration + "ms. to use " + TEST_SIZE + " calls on 3 components." );
        resetMemory();


        // Get the time for manager
        final long cmBDuration = getLookupRunTime( cmB );
        m_logger.info( "     " + cmBName + " time = " + cmBDuration + "ms. to use " + TEST_SIZE + " calls on 3 components." );
        resetMemory();

        // Show a summary
        if( m_logger.isInfoEnabled() )
        {
            double mult;
            mult = ( cmADuration > 0 ? ( cmBDuration * 100 / cmADuration ) / 100.0 : Float.POSITIVE_INFINITY );
            m_logger.info( "  => " + cmAName + " is " + mult + " X as fast as " + cmBName + "." );

            mult = ( cmBDuration > 0 ? ( cmADuration * 100 / cmBDuration ) / 100.0 : Float.POSITIVE_INFINITY );
            m_logger.info( "  => " + cmBName + " is " + mult + " X as fast as " + cmAName + "." );
        }
    }

    protected long getLookupRunTime( ServiceManager manager )
    {
        // Create the runnable
        org.apache.avalon.fortress.test.ContainerProfile.LookupRunner runnable = new org.apache.avalon.fortress.test.ContainerProfile.LookupRunner( manager, m_logger );

        LatchedThreadGroup group = new LatchedThreadGroup( runnable, THREADS );
        group.enableLogging( m_logger );

        long duration;
        try
        {
            duration = group.go();
        }
        catch( Throwable t )
        {
            // Throwable could have been thrown by one of the tests.
            if( m_throwable == null )
            {
                m_throwable = t;
            }
            duration = 0;
        }

        if( m_throwable != null )
        {
            throw new CascadingAssertionFailedError( "Exception in test thread.", m_throwable );
        }

        assertTrue( "m_getCount == 0 (" + m_getCount + ")", m_getCount == 0 );

        return duration;
    }

    private static class LookupRunner implements Runnable
    {
        private Logger m_logger;
        private ServiceManager m_manager;
        private int m_getCount = 0;
        private Throwable m_throwable = null;

        public LookupRunner( ServiceManager manager, Logger logger )
        {
            m_manager = manager;
            m_logger = logger;
        }

        public int getCount()
        {
            return m_getCount;
        }

        public Throwable getThrowable()
        {
            return m_throwable;
        }

        public void run()
        {
            // Perform this threads part of the test.
            final int loops = ( TEST_SIZE / THREADS );
            for( int i = 0; i < loops; i++ )
            {
                DOMParser parser = null;

                try
                {
                    parser = (DOMParser)m_manager.lookup( DOMParser.ROLE );

                    // Make the loops hold the components longer than they are released, but only slightly.
                    Thread.yield();
                }
                catch( Throwable t )
                {
                    m_logger.error( "Unexpected error after " + m_getCount +
                                    " iterations retrieved for DOMParser", t );

                    if( m_throwable == null )
                    {
                        m_throwable = t;
                    }
                    return;
                }
                finally
                {
                    if( null != parser )
                    {
                        m_manager.release( parser );
                    }
                }
                /*
                DataSourceComponent datasource = null;

                try
                {
                    datasource = (DataSourceComponent) m_manager.lookup(DataSourceComponent.ROLE);

                    // Make the loops hold the components longer than they are released, but only slightly.
                    Thread.yield();
                }
                catch (Throwable t)
                {
                    m_logger.error( "Unexpected error after " + m_getCount +
                        " iterations retrieved for DataSourceComponent", t );

                    if (m_throwable == null) {
                        m_throwable = t;
                    }
                    return;
                }
                finally
                {
                    if ( null != datasource )
                    {
                        m_manager.release( datasource );
                    }
                }
                */
                Monitor monitor = null;

                try
                {
                    monitor = (Monitor)m_manager.lookup( Monitor.ROLE );

                    // Make the loops hold the components longer than they are released, but only slightly.
                    Thread.yield();
                }
                catch( Throwable t )
                {
                    m_logger.error( "Unexpected error after " + m_getCount +
                                    " iterations retrieved for DataSourceComponent", t );

                    if( m_throwable == null )
                    {
                        m_throwable = t;
                    }
                    return;
                }
                finally
                {
                    if( null != monitor )
                    {
                        m_manager.release( monitor );
                    }
                }
            }
        }
    }

    public static final void main( String[] args )
    {
        TestRunner.run( ContainerProfile.class );
    }
}

