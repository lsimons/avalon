/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output.db;

import java.sql.DriverManager;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Connection;
import javax.sql.DataSource;

/**
 * A basic datasource that doesn't do any pooling but just wraps 
 * around default mechanisms.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultDataSource
    implements DataSource
{
    private final String   m_username;
    private final String   m_password;
    private final String   m_url;

    private PrintWriter    m_logWriter;
    private int            m_loginTimeout;

    public DefaultDataSource( final String url, 
                              final String username, 
                              final String password )
    {
        m_url = url;
        m_username = username;
        m_password = password;

        m_logWriter = new PrintWriter( System.err, true );
    }

    /**
     * Attempt to establish a database connection.
     *
     * @return the Connection
     */
    public Connection getConnection()
        throws SQLException
    {
        return getConnection( m_username, m_password );
    }

    /**
     * Attempt to establish a database connection.
     *
     * @return the Connection
     */
    public Connection getConnection( final String username, final String password )
        throws SQLException
    {
        return DriverManager.getConnection( m_url, username, password );
    }

    /**
     * Gets the maximum time in seconds that this data source can wait while 
      * attempting to connect to a database.
      *
      * @return the login time
      */
     public int getLoginTimeout()
        throws SQLException
     {
         return m_loginTimeout;
     }

    /**
     * Get the log writer for this data source.
     *
     * @return the LogWriter
     */
    public PrintWriter getLogWriter()
        throws SQLException
    {
        return m_logWriter;
    }
    
    /**
     * Sets the maximum time in seconds that this data source will wait 
     * while attempting to connect to a database.
     *
     * @param loginTimeout the loging timeout in seconds
     */
    public void setLoginTimeout( final int loginTimeout )
        throws SQLException
    {
        m_loginTimeout = loginTimeout;
    }

    public void setLogWriter( final PrintWriter logWriter )
        throws SQLException
    {
        m_logWriter = logWriter;
    }
}
