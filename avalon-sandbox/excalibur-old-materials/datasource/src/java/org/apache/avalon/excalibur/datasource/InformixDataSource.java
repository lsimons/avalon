/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.excalibur.datasource;

import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.excalibur.datasource.DataSourceComponent;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;

import com.informix.jdbcx.IfxConnectionPoolDataSource;
import com.informix.jdbcx.IfxDataSource;

/**
 * The Informix implementation for DataSources in Cocoon.  This uses the
 * <code>com.informix.jdbcx.IfxConnectionPoolDataSource</code> object.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.1 $ $Date: 2001/08/07 20:20:46 $
 * @since 4.0
 */
public class InformixDataSource
    extends AbstractLoggable
    implements DataSourceComponent
{
    private IfxDataSource m_dataSource;
    private boolean m_autocommit;
    private static boolean INIT_FACTORY = false;

    /**
     * Set up the system property for the context factory if it hasn't been
     * done already.  This is not done in a static initializer due to the
     * existence of the J2eeDataSource.
     */
    public InformixDataSource()
    {
        if ( ! InformixDataSource.INIT_FACTORY )
        {
            System.setProperty( Context.INITIAL_CONTEXT_FACTORY,
                 "org.apache.avalon.cadastre.memory.MemoryInitialContextFactory" );
        }
    }

    /**
     * Return an Informix Connection object
     */
    public Connection getConnection() throws SQLException
    {
        Connection conn = m_dataSource.getConnection();

        if ( conn.getAutoCommit() != m_autocommit )
        {
            conn.setAutoCommit(m_autocommit);
        }

        return conn;
    }

    /**
     * Set up the Informix driver for direct use.  It doesn't like the JdbcDataSource
     * Component, so we gave it it's own.  Do not use this datasource if you are in
     * a J2EE setting.
     */
    public void configure(Configuration conf) throws ConfigurationException
    {
        Configuration poolController = conf.getChild("pool-controller");
        String dbname = conf.getChild("dbname").getValue("ifx");
        IfxConnectionPoolDataSource pooledDataSource = new IfxConnectionPoolDataSource();
        m_autocommit = conf.getChild("autocommit").getValueAsBoolean(true);

        pooledDataSource.setIfxCPMInitPoolSize(poolController.getAttributeAsInteger("init", 5));
        pooledDataSource.setIfxCPMMinPoolSize(poolController.getAttributeAsInteger("min", 5));
        pooledDataSource.setIfxCPMMaxPoolSize(poolController.getAttributeAsInteger("max", 10));
        pooledDataSource.setIfxCPMServiceInterval(100);
        pooledDataSource.setServerName(conf.getChild("servername").getValue());
        pooledDataSource.setDatabaseName(conf.getChild("dbname").getValue());
        pooledDataSource.setIfxIFXHOST(conf.getChild("host").getValue());
        pooledDataSource.setPortNumber(conf.getChild("host").getAttributeAsInteger("port"));
        pooledDataSource.setUser(conf.getChild("user").getValue());
        pooledDataSource.setPassword(conf.getChild("password").getValue());

        try
        {
            Context context = new InitialContext();

            context.bind(dbname + "pool", pooledDataSource);

            m_dataSource = new IfxDataSource();
            m_dataSource.setDataSourceName(dbname + "pool");
            m_dataSource.setServerName(conf.getChild("servername").getValue());
            m_dataSource.setDatabaseName(conf.getChild("dbname").getValue());
            m_dataSource.setIfxIFXHOST(conf.getChild("host").getValue());
            m_dataSource.setPortNumber(conf.getChild("host").getAttributeAsInteger("port"));
            m_dataSource.setUser(conf.getChild("user").getValue());
            m_dataSource.setPassword(conf.getChild("password").getValue());

            context.bind(dbname, m_dataSource);
        }
        catch (Exception e)
        {
            if (getLogger().isErrorEnabled())
            {
                getLogger().error("There was an error trying to bind the connection pool", e);
            }
            throw new ConfigurationException("There was an error trying to bind the connection pool", e);
        }
    }
}