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
package org.apache.avalon.examples.jdbcdatasource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.avalon.excalibur.datasource.DataSourceComponent;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;

/**
 * This example application creates a conmponent which makes use of a JdbcDataSource to
 *  connect to a Hypersonic SQL database.  It then adds a row to a table that it creates
 *  displaying a list of all the rows in the table.
 *
 * Note, this code ignores exceptions to keep the code simple.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.7 $ $Date: 2003/03/22 12:46:26 $
 * @since 4.1
 */
public class DefaultHelloDBService
    extends AbstractLogEnabled
    implements HelloDBService, Serviceable, Configurable, Initializable, Disposable
{
    /** ComponentManager which created this component */
    private String m_dataSourceName;
    private ServiceSelector m_dbSelector;
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
        catch( SQLException e )
        {
            if( e.getMessage().startsWith( "Table already exists" ) )
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
                if( result == 1 )
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
        catch( SQLException e )
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
        catch( SQLException e )
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
                while( rs.next() )
                {
                    String title = rs.getString( 1 );
                    Timestamp time = rs.getTimestamp( 2 );

                    System.out.println( "    '" + title + "' saved at " + time );
                    count++;
                }

                if( count == 0 )
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
        catch( SQLException e )
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
     *
     * @avalon.service interface="org.apache.avalon.excalibur.datasource.DataSourceComponentSelector"
     */
    public void service( final ServiceManager manager )
        throws ServiceException
    {
        getLogger().debug( "DefaultHelloDBService.compose()" );
        m_dbSelector = (ServiceSelector)manager.lookup( DataSourceComponent.ROLE + "Selector" );
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
        if( m_dbSelector != null )
        {
            if( m_dataSource != null )
            {
                m_dbSelector.release( m_dataSource );
                m_dataSource = null;
            }
            m_dbSelector = null;
        }
    }
}

