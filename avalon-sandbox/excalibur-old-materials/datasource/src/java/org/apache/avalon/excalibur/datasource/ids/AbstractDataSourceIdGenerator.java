/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.datasource.ids;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.avalon.excalibur.datasource.DataSourceComponent;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.ComponentSelector;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/06/13 17:24:51 $
 * @since 4.1
 */
public abstract class AbstractDataSourceIdGenerator
    extends AbstractIdGenerator
    implements IdGenerator, Composable, Configurable, Initializable, Disposable, ThreadSafe
{
    protected static final int DBTYPE_STANDARD = 0;
    protected static final int DBTYPE_MYSQL = 1;

    /** ComponentLocator which created this component */
    protected ComponentManager m_manager;

    private String m_dataSourceName;
    private ComponentSelector m_dbSelector;
    protected DataSourceComponent m_dataSource;
    protected int m_dbType;

    /**
     * Number of allocated Ids remaining before another block must be allocated.
     */
    protected int m_allocated;
    protected long m_nextId;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public AbstractDataSourceIdGenerator()
    {
    }

    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Allocates a connection for the caller.  The connection must be closed by the caller
     *  when no longer needed.
     *
     * @return an open DB connection.
     *
     * @throws SQLException if the connection can not be obtained for any reason.
     */
    protected Connection getConnection()
        throws SQLException
    {
        return m_dataSource.getConnection();
    }

    /*---------------------------------------------------------------
     * Composable Methods
     *-------------------------------------------------------------*/
    /**
     * Called by the Container to tell the component which ComponentLocator
     *  is controlling it.
     *
     * @param ComponentLocator which curently owns the component.
     */
    public void compose( ComponentManager manager )
    {
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
        // Obtain the big-decimals flag.
        setUseBigDecimals( configuration.getAttributeAsBoolean( "big-decimals", false ) );

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
        // Get a reference to a data source
        m_dbSelector = (ComponentSelector)m_manager.lookup( DataSourceComponent.ROLE + "Selector" );
        m_dataSource = (DataSourceComponent)m_dbSelector.select( m_dataSourceName );

        // Resolve the type of database that is being used.
        try
        {
            Connection conn = getConnection();
            try
            {
                Statement statement = conn.createStatement();
                String className = statement.getClass().getName();
                if( className.indexOf( "mysql" ) > 0 )
                {
                    m_dbType = DBTYPE_MYSQL;
                }
                else
                {
                    m_dbType = DBTYPE_STANDARD;
                }
            }
            finally
            {
                conn.close();
            }
        }
        catch( SQLException e )
        {
            getLogger().error( "Unable to open connection to resolve database type.", e );
        }
    }

    /*---------------------------------------------------------------
     * Disposable Methods
     *-------------------------------------------------------------*/
    /**
     * Called by the Container to dispose the component.
     */
    public void dispose()
    {
        // Free up the data source
        if( m_dbSelector != null )
        {
            if( m_dataSource != null )
            {
                m_dbSelector.release( m_dataSource );

                m_dataSource = null;
            }

            m_manager.release( m_dbSelector );

            m_dbSelector = null;
        }
    }
}
