/*
* Copyright (C) The Apache Software Foundation. All rights reserved.
*
* This software is published under the terms of the Apache Software License
* version 1.1, a copy of which has been included with this distribution in
* the LICENSE.txt file.
*/

package org.apache.avalon.fortress.test;

import junit.framework.TestCase;

import org.apache.avalon.fortress.ContainerManager;
import org.apache.avalon.fortress.impl.DefaultContainer;
import org.apache.avalon.fortress.impl.DefaultContainerManager;
import org.apache.avalon.fortress.util.FortressConfig;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.service.ServiceManager;

/**
 * A testcase for the different handlers.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003/02/25 16:28:37 $
 */
public class HandlersTestCase extends TestCase
{
    private Exception m_exception;

    public HandlersTestCase( final String name )
    {
        super( name );
    }

    public void testThreadsafe()
        throws Exception
    {
        final ServiceManager serviceManager = getServiceManager();
        final String key = org.apache.avalon.fortress.test.data.Role1.ROLE;
        final org.apache.avalon.fortress.test.data.BaseRole object1 = (org.apache.avalon.fortress.test.data.BaseRole)serviceManager.lookup( key );
        final org.apache.avalon.fortress.test.data.BaseRole object2 = (org.apache.avalon.fortress.test.data.BaseRole)serviceManager.lookup( key );

        assertEquals( "Threadsafe object IDs (1 vs 2)", object1.getID(), object2.getID() );

        final Thread thread = new Thread()
        {
            public void run()
            {
                try
                {
                    final org.apache.avalon.fortress.test.data.BaseRole object3 = (org.apache.avalon.fortress.test.data.BaseRole)serviceManager.lookup( key );
                    final org.apache.avalon.fortress.test.data.BaseRole object4 = (org.apache.avalon.fortress.test.data.BaseRole)serviceManager.lookup( key );

                    assertEquals( "Threadsafe object IDs (1 vs 3)", object1.getID(), object3.getID() );
                    assertEquals( "Threadsafe object IDs (2 vs 4)", object2.getID(), object4.getID() );
                    assertEquals( "Threadsafe object IDs (3 vs 4)", object3.getID(), object4.getID() );
                }
                catch( final Exception e )
                {
                    m_exception = e;
                }
            }
        };
        thread.start();
        thread.join();

        checkException();
    }

    public void testPerThread()
        throws Exception
    {
        final String key = org.apache.avalon.fortress.test.data.Role3.ROLE;
        final String type = "PerThread";

        final ServiceManager serviceManager = getServiceManager();
        final org.apache.avalon.fortress.test.data.BaseRole object1 = (org.apache.avalon.fortress.test.data.BaseRole)serviceManager.lookup( key );
        final org.apache.avalon.fortress.test.data.BaseRole object2 = (org.apache.avalon.fortress.test.data.BaseRole)serviceManager.lookup( key );

        assertEquals( type + " object IDs (1 vs 2)", object1.getID(), object2.getID() );

        final Thread thread = new Thread()
        {
            public void run()
            {
                try
                {
                    final org.apache.avalon.fortress.test.data.BaseRole object3 = (org.apache.avalon.fortress.test.data.BaseRole)serviceManager.lookup( key );
                    final org.apache.avalon.fortress.test.data.BaseRole object4 = (org.apache.avalon.fortress.test.data.BaseRole)serviceManager.lookup( key );

                    assertTrue( type + " object IDs (1 vs 3)", object1.getID() != object3.getID() );
                    assertTrue( type + " object IDs (2 vs 4)", object2.getID() != object4.getID() );
                    assertEquals( type + " object IDs (3 vs 4)", object3.getID(), object4.getID() );
                }
                catch( final Exception e )
                {
                    m_exception = e;
                }
            }
        };
        thread.start();
        thread.join();

        checkException();
    }

    public void testFactory()
        throws Exception
    {
        final String key = org.apache.avalon.fortress.test.data.Role4.ROLE;
        final String type = "Factory";

        final ServiceManager serviceManager = getServiceManager();
        final org.apache.avalon.fortress.test.data.BaseRole object1 = (org.apache.avalon.fortress.test.data.BaseRole)serviceManager.lookup( key );
        final org.apache.avalon.fortress.test.data.BaseRole object2 = (org.apache.avalon.fortress.test.data.BaseRole)serviceManager.lookup( key );

        assertTrue( type + " object IDs (1 vs 2)", object1.getID() != object2.getID() );

        final Thread thread = new Thread()
        {
            public void run()
            {
                try
                {
                    final org.apache.avalon.fortress.test.data.BaseRole object3 = (org.apache.avalon.fortress.test.data.BaseRole)serviceManager.lookup( key );
                    final org.apache.avalon.fortress.test.data.BaseRole object4 = (org.apache.avalon.fortress.test.data.BaseRole)serviceManager.lookup( key );

                    assertTrue( type + " object IDs (1 vs 3)", object1.getID() != object3.getID() );
                    assertTrue( type + " object IDs (2 vs 4)", object2.getID() != object4.getID() );
                    assertTrue( type + " object IDs (3 vs 4)", object3.getID() != object4.getID() );
                }
                catch( final Exception e )
                {
                    m_exception = e;
                }
            }
        };
        thread.start();
        thread.join();

        checkException();
    }

    private void checkException() throws Exception
    {
        if( null != m_exception )
        {
            final Exception exception = m_exception;
            m_exception = null;
            throw exception;
        }
    }

    public void testPoolable()
        throws Exception
    {
        final ServiceManager serviceManager = getServiceManager();
        final String key = org.apache.avalon.fortress.test.data.Role2.ROLE;
        final org.apache.avalon.fortress.test.data.BaseRole object1 = (org.apache.avalon.fortress.test.data.BaseRole)serviceManager.lookup( key );
        final org.apache.avalon.fortress.test.data.BaseRole object2 = (org.apache.avalon.fortress.test.data.BaseRole)serviceManager.lookup( key );
        final org.apache.avalon.fortress.test.data.BaseRole object3 = (org.apache.avalon.fortress.test.data.BaseRole)serviceManager.lookup( key );


        serviceManager.release( object1 );
        serviceManager.release( object2 );
        serviceManager.release( object3 );
    }

    private ServiceManager getServiceManager() throws Exception
    {
        final FortressConfig config = new FortressConfig();
        config.setContextDirectory( "./" );
        config.setWorkDirectory( "./" );
        final String BASE = "resource://org/apache/avalon/fortress/test/data/";
        config.setContainerConfiguration( BASE + "test1.xconf" );
        config.setLoggerManagerConfiguration( BASE + "test1.xlog" );
        config.setRoleManagerConfiguration( BASE + "test1.roles" );

        final ContainerManager cm = new DefaultContainerManager( config.getContext() );
        ContainerUtil.initialize( cm );

        final DefaultContainer container = (DefaultContainer)cm.getContainer();
        final ServiceManager serviceManager = container.getServiceManager();
        return serviceManager;
    }
}
