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

    public DataSourceTestCase()
    {
        this("Excalibur DataSource Test Case");
    }

    public DataSourceTestCase(String name)
    {
        super(name);
    }

    static
    {
        DefaultConfiguration dc = new DefaultConfiguration( "", LOCATION );
        DefaultConfiguration pool = new DefaultConfiguration( "pool-controller", LOCATION );
        DefaultConfiguration dburl = new DefaultConfiguration( "dburl", LOCATION );
        DefaultConfiguration user = new DefaultConfiguration( "user", LOCATION );
        DefaultConfiguration password = new DefaultConfiguration( "password", LOCATION );
        pool.addAttribute( "min", "5" );
        pool.addAttribute( "max", "10" );
        dc.addChild( pool );
        dburl.appendValueData( "jdbc:odbc://test" );
        dc.addChild( dburl );
        user.appendValueData( "test" );
        dc.addChild( user );
        password.appendValueData( "test" );
        dc.addChild( password );
        conf = dc;

        logger = Hierarchy.getDefaultHierarchy().getLoggerFor( "test" );
        logger.setPriority( Priority.INFO );

        try
        {
            Class.forName( "Your Driver Class Here" );
        }
        catch( final Exception e )
        {
            logger.error( e.getMessage(), e );
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
            assertTrue( "Over Allocation Test", false );
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
            assertTrue( "Over Allocation Test", false );
        }

        Thread one = new Thread( new ConnectionThread( this, ds ) );
        Thread two = new Thread( new ConnectionThread( this, ds ) );

        one.start();
        two.start();

        while( one.isAlive() || two.isAlive() )
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
        protected DataSourceTestCase testlet;

        ConnectionThread( final DataSourceTestCase testlet,
                          final DataSourceComponent datasource )
        {
            this.datasource = datasource;
            this.testlet = testlet;
        }

        public void run()
        {
            testlet.runDBTest( datasource );
        }
    }
}

