/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.excalibur.datasource.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.excalibur.datasource.DataSourceComponent;
import org.apache.avalon.excalibur.datasource.JdbcDataSource;
import org.apache.log.Hierarchy;
import org.apache.log.Logger;
import org.apache.log.Priority;
import junit.framework.TestCase;

/**
 * Test the DataSource Component.  I don't know how to make this generic,
 * so I'll throw some bones out there, and hope someone can set this up
 * better.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public class DataSourceTestCase
    extends TestCase
{
    static final Configuration conf;
    static final String LOCATION = "Testlet Framework";
    static final Logger logger;

    public DataSourceTestCase(String name)
    {
        super(name);
    }

    static
    {
        DefaultConfiguration dc = new DefaultConfiguration( "", LOCATION );
        DefaultConfiguration pool = new DefaultConfiguration( "pool-controller", LOCATION );
        DefaultConfiguration dburl = new DefaultConfiguration( "dburl", LOCATION );
        DefaultConfiguration driver = new DefaultConfiguration( "driver", LOCATION );
        DefaultConfiguration user = new DefaultConfiguration( "user", LOCATION );
        DefaultConfiguration password = new DefaultConfiguration( "password", LOCATION );
        pool.addAttribute( "min", "5" );
        pool.addAttribute( "max", "10" );
        dc.addChild( pool );
        dburl.setValue( "jdbc:odbc:test" );
        dc.addChild( dburl );
        user.setValue( "test" );
        dc.addChild( user );
        password.setValue( "test" );
        dc.addChild( password );
        driver.setValue( "sun.jdbc.odbc.JdbcOdbcDriver" );
        dc.addChild(driver);
        conf = dc;

        logger = Hierarchy.getDefaultHierarchy().getLoggerFor( "test" );
        logger.setPriority( Priority.DEBUG );

        try
        {
            logger.setLogTargets( new org.apache.log.LogTarget[]
                                  { new org.apache.log.output.FileOutputLogTarget("test.log") } );
        }
        catch (Exception e)
        {
            // ignore
        }
    }

    public void testOverAllocation()
    {
        boolean result = false;
        JdbcDataSource ds = new JdbcDataSource();
        ds.setLogger( logger );

        try
        {
            ds.configure( conf );
        }
        catch( final ConfigurationException ce )
        {
            assertTrue( "Over Allocation Test: Could not configure", false );
        }

        try
        {
            for( int i = 0; i < 11; i++ )
            {
                ds.getConnection();
            }
        }
        catch( final SQLException se )
        {
            result = true;
            logger.info( "The test was successful" );
        }

        ds.dispose();

        assertTrue( "Over Allocation Test", result );
    }

    public void testNormalUse()
    {
        boolean result = true;
        JdbcDataSource ds = new JdbcDataSource();
        ds.setLogger( logger );

        try
        {
            ds.configure( conf );
        }
        catch( final ConfigurationException ce )
        {
            logger.error( ce.getMessage(), ce );
            assertTrue( "Over Allocation Test: could not configure", false );
        }

        Thread one = new Thread( new ConnectionThread( this, ds ) );
        Thread two = new Thread( new ConnectionThread( this, ds ) );
        Thread three = new Thread( new ConnectionThread( this, ds ) );
        Thread four = new Thread( new ConnectionThread( this, ds ) );
        Thread five = new Thread( new ConnectionThread( this, ds ) );
        Thread six = new Thread( new ConnectionThread( this, ds ) );
        Thread seven = new Thread( new ConnectionThread( this, ds ) );
        Thread eight = new Thread( new ConnectionThread( this, ds ) );
        Thread nine = new Thread( new ConnectionThread( this, ds ) );

        one.start();
        two.start();
        three.start();
        four.start();
        five.start();
        six.start();
        seven.start();
        eight.start();
        nine.start();

        while( one.isAlive() || two.isAlive() || three.isAlive() || four.isAlive() ||
               five.isAlive() || six.isAlive() || seven.isAlive() || eight.isAlive() ||
               nine.isAlive() )
        {
            try
            {
                Thread.sleep( 100 );
            }
            catch( final InterruptedException ie )
            {
                // Ignore
            }
        }

        logger.info( "If you saw no failure messages, then the test passed" );
        assertTrue( "Normal Use Test", result );
    }

    public void runDBTest( final DataSourceComponent datasource )
    {
        long end = System.currentTimeMillis() + 5000; // run for 5 seconds

        while( System.currentTimeMillis() < end )
        {
            try
            {
                Connection con = datasource.getConnection();
                long sleeptime = (long)(Math.random() * 100.0);
                Thread.sleep( sleeptime );
                con.close();
            }
            catch( final SQLException se )
            {
                logger.info( "Failed to get Connection, test failed" );
                assertTrue( "Normal Use Test", false );
            }
            catch( final InterruptedException ie )
            {
                // Ignore
            }
        }
    }

    class ConnectionThread
        implements Runnable
    {
        protected DataSourceComponent datasource;
        protected DataSourceTestCase testcase;

        ConnectionThread( final DataSourceTestCase testcase,
                          final DataSourceComponent datasource )
        {
            this.datasource = datasource;
            this.testcase = testcase;
        }

        public void run()
        {
            testcase.runDBTest( datasource );
        }
    }
}

