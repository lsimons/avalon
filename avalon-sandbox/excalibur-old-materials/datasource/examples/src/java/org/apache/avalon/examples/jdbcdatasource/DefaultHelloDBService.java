/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.examples.jdbcdatasource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.avalon.excalibur.datasource.DataSourceComponent;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.ComponentSelector;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 * This example application creates a conmponent which makes use of a JdbcDataSource to
 *  connect to a Hypersonic SQL database.  It then adds a row to a table that it creates
 *  displaying a list of all the rows in the table.
 *
 * Note, this code ignores exceptions to keep the code simple.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/10/03 03:36:06 $
 * @since 4.1
 */
public class DefaultHelloDBService
    extends AbstractLogEnabled
    implements HelloDBService, Composable, Configurable, Initializable, Disposable
{
    /** ComponentManager which created this component */
    protected ComponentManager  m_manager;

    private String              m_dataSourceName;
    private ComponentSelector   m_dbSelector;
    private DataSourceComponent m_dataSource;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /** Instantiate a DefaultHelloDBService */
    public DefaultHelloDBService()
    {
    }
    
    /*---------------------------------------------------------------
     * Private Methods
     *-------------------------------------------------------------*/
    /**
     * Allocates a connection for the caller.  The connection must be closed by the caller
     *  when no longer needed.
     *
     * @return an open DB connection.
     *
     * @throws SQLException if the connection can not be obtained for any reason.
     */
    private Connection getConnection()
        throws SQLException
    {
        return m_dataSource.getConnection();
    }
    
    /**
     * Initializes the database by creating the required table.  Normally
     *   this would not be needed.  But doing this with HSQLDB makes it easier
     *   to run the example.
     *
     * @throws SQLException  if there is a problem setting the database up.
     */
    private void initializeDatabase()
        throws SQLException
    {
        try
        {
            Connection conn = getConnection();
            try
            {
                PreparedStatement stmt = conn.prepareStatement(
                    "CREATE CACHED TABLE titles ( " +
                    "    title VARCHAR NOT NULL, " +
                    "    time TIMESTAMP NOT NULL " +
                    ")" );
                stmt.executeUpdate();
            }
            finally
            {
                // Return the connection to the pool by closing it.
                conn.close();
            }
        }
        catch ( SQLException e )
        {
            if ( e.getMessage().startsWith( "Table already exists" ) )
            {
                // Table is already there.  Must have run the example before.
            }
            else
            {
                throw e;
            }
        }
    }
    
    /*---------------------------------------------------------------
     * HelloDBService Methods
     *-------------------------------------------------------------*/
    /**
     * Adds a single row to the database.
     *
     * @param title  The row title
     */
    public void addRow( String title )
    {
        getLogger().debug( "DefaultHelloDBService.addRow(" + title + ")" );
        
        try
        {
            Connection conn = getConnection();
            try
            {
                PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO titles (title, time) VALUES (?, now())" );
                stmt.setString( 1, title );
                int result = stmt.executeUpdate();
                if ( result == 1 )
                {
                    System.out.println( "Added '" + title + "' to the database." );
                }
                else
                {
                    getLogger().error( "Unable to add title to the database.  database returned " +
                        result + " inserted." );
                }
            }
            finally
            {
                // Return the connection to the pool by closing it.
                conn.close();
            }
        }
        catch ( SQLException e )
        {
            getLogger().error( "Unable to add title to the database.", e );
        }
    }
    
    /**
     * Ask the component to delete all rows in the database.
     */
    public void deleteRows()
    {
        getLogger().debug( "DefaultHelloDBService.deleteRows()" );
        
        try
        {
            Connection conn = getConnection();
            try
            {
                PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM titles" );
                int result = stmt.executeUpdate();
                System.out.println( "Deleted " + result + " titles from the database." );
            }
            finally
            {
                // Return the connection to the pool by closing it.
                conn.close();
            }
        }
        catch ( SQLException e )
        {
            getLogger().error( "Unable to delete old titles from the database.", e );
        }
    }
    
    /**
     * Ask the component to log all of the rows in the database to the logger
     *  with the info log level.
     */
    public void logRows()
    {
        getLogger().debug( "DefaultHelloDBService.logRows()" );
        
        try
        {
            Connection conn = getConnection();
            try
            {
                PreparedStatement stmt = conn.prepareStatement(
                    "SELECT title, time FROM titles" );
                ResultSet rs = stmt.executeQuery();
                int count = 0;
                while ( rs.next() )
                {
                    String title = rs.getString( 1 );
                    Timestamp time = rs.getTimestamp( 2 );
                    
                    System.out.println( "    '" + title + "' saved at " + time );
                    count++;
                }
                
                if ( count == 0 )
                {
                    System.out.println( "The database does not contain any saved titles." );
                }
                else
                {
                    System.out.println( "The database contains " + count + " titles." );
                }
            }
            finally
            {
                // Return the connection to the pool by closing it.
                conn.close();
            }
        }
        catch ( SQLException e )
        {
            getLogger().error( "Unable to delete old titles from the database.", e );
        }
    }
    
    /*---------------------------------------------------------------
     * Composable Methods
     *-------------------------------------------------------------*/
    /**
     * Called by the Container to tell the component which ComponentManager
     *  is controlling it.
     *
     * @param manager which curently owns the component.
     */
    public void compose( ComponentManager manager )
    {
        getLogger().debug( "DefaultHelloDBService.compose()" );

        m_manager = manager;
    }
    
    /*---------------------------------------------------------------
     * Configurable Methods
     *-------------------------------------------------------------*/
    /**
     * Called by the Container to configure the component.
     *
     * @param configuration configuration info used to setup the component.
     *
     * @throws ConfigurationException if there are any problems with the configuration.
     */
    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        getLogger().debug( "DefaultHelloDBService.configure()" );

        // Obtain a reference to the configured DataSource
        m_dataSourceName = configuration.getChild( "dbpool" ).getValue();
    }
    
    /*---------------------------------------------------------------
     * Initializable Methods
     *-------------------------------------------------------------*/
    /**
     * Called by the Container to initialize the component.
     *
     * @throws Exception if there were any problems durring initialization.
     */
    public void initialize()
        throws Exception
    {
        getLogger().debug( "DefaultHelloDBService.initialize()" );

        // Get a reference to a data source
        m_dbSelector = (ComponentSelector)m_manager.lookup( DataSourceComponent.ROLE + "Selector" );
        m_dataSource = (DataSourceComponent)m_dbSelector.select( m_dataSourceName );
        
        // Initialize the database.
        initializeDatabase();
    }
    
    /*---------------------------------------------------------------
     * Disposable Methods
     *-------------------------------------------------------------*/
    /**
     * Called by the Container to dispose the component.
     */
    public void dispose()
    {
        getLogger().debug( "DefaultHelloDBService.dispose()" );

        // Free up the data source
        if ( m_dbSelector != null )
        {
            if ( m_dataSource != null )
            {
                m_dbSelector.release( m_dataSource );
                m_dataSource = null;
            }

            m_manager.release( m_dbSelector );
            m_dbSelector = null;
        }
    }
}

