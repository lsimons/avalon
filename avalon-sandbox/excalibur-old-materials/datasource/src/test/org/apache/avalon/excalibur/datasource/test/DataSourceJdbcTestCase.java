/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.datasource.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import org.apache.avalon.excalibur.concurrent.ThreadBarrier;
import org.apache.avalon.excalibur.datasource.DataSourceComponent;
import org.apache.avalon.excalibur.testcase.CascadingAssertionFailedError;
import org.apache.avalon.excalibur.testcase.ExcaliburTestCase;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;

/**
 * Test the DataSource Component.  I don't know how to make this generic,
 * so I'll throw some bones out there, and hope someone can set this up
 * better.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public class DataSourceJdbcTestCase
    extends ExcaliburTestCase
{
    protected boolean isSuccessful;
    protected ThreadBarrier barrier;
    protected int connectionCount;

    public DataSourceJdbcTestCase( String name )
    {
        super( name );
    }

    public void testOverAllocation()
    {
        DataSourceComponent ds = null;
        this.isSuccessful = false;
        LinkedList connectionList = new LinkedList();

        try
        {
            ds = (DataSourceComponent)manager.lookup( DataSourceComponent.ROLE );

            for( int i = 0; i < 10; i++ )
            {
                connectionList.add( ds.getConnection() );
            }
            getLogger().info( "Testing overallocation of connections.  Should see a warning next." );
            connectionList.add( ds.getConnection() );
        }
        catch( SQLException se )
        {
            this.isSuccessful = true;
            getLogger().info( "The test was successful" );
        }
        catch( ComponentException ce )
        {
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "There was an error in the OverAllocation test", ce );
            }

            throw new CascadingAssertionFailedError( "There was an error in the OverAllocation test", ce );
        }
        finally
        {
            assertTrue( "The DataSourceComponent could not be retrieved.", null != ds );

            Iterator connections = connectionList.iterator();

            while( connections.hasNext() )
            {
                try
                {
                    ( (Connection)connections.next() ).close();
                }
                catch( SQLException se )
                {
                    // ignore
                }
            }

            connectionList.clear();

            manager.release( (Component)ds );
        }

        assertTrue( "Exception was not thrown when too many datasource components were retrieved.", this.isSuccessful );
    }

    public void testNormalUse()
    {
        DataSourceComponent ds = null;
        this.isSuccessful = true;

        try
        {
            ds = (DataSourceComponent)manager.lookup( DataSourceComponent.ROLE );

            this.connectionCount = 0;

            for( int i = 0; i < 10; i++ )
            {
                ( new Thread( new ConnectionThread( this, ds ) ) ).start();
            }

            this.barrier = new ThreadBarrier( 11 );
            try
            {
                this.barrier.barrierSynchronize();
            }
            catch( InterruptedException ie )
            {
                // Ignore
            }

            getLogger().info( "The normal use test passed with " + this.connectionCount + " requests and 10 concurrent threads running" );
        }
        catch( ComponentException ce )
        {
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "There was an error in the normal use test", ce );
            }

            throw new CascadingAssertionFailedError( "There was an error in the normal use test", ce );
        }
        finally
        {
            assertTrue( "The DataSourceComponent could not be retrieved.", null != ds );

            manager.release( (Component)ds );
        }

        assertTrue( "Normal use test failed", this.isSuccessful );
    }

    class ConnectionThread
        implements Runnable
    {
        protected DataSourceComponent datasource;
        protected DataSourceJdbcTestCase testcase;

        ConnectionThread( DataSourceJdbcTestCase testcase,
                          final DataSourceComponent datasource )
        {
            this.datasource = datasource;
            this.testcase = testcase;
        }

        public void run()
        {
            long end = System.currentTimeMillis() + 5000; // run for 5 seconds
            Random rnd = new Random();

            while( System.currentTimeMillis() < end && this.testcase.isSuccessful )
            {
                try
                {
                    Connection con = this.datasource.getConnection();
                    Thread.sleep( (long)rnd.nextInt( 100 ) ); // sleep for up to 100ms
                    con.close();
                    this.testcase.connectionCount++;
                }
                catch( final SQLException se )
                {
                    this.testcase.isSuccessful = false;
                    this.testcase.getLogger().info( "Failed to get Connection, test failed", se );
                }
                catch( final InterruptedException ie )
                {
                    // Ignore
                }
            }

            try
            {
                this.testcase.barrier.barrierSynchronize();
            }
            catch( final InterruptedException ie )
            {
                // Ignore
            }
        }
    }
}

