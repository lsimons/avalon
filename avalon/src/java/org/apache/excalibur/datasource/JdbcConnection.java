/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.datasource;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Map;
import org.apache.avalon.AbstractLoggable;
import org.apache.avalon.Recyclable;
import org.apache.excalibur.pool.Pool;

/**
 * The Connection object used in conjunction with the JdbcDataSource
 * object.
 *
 * TODO: Implement a configurable closed end Pool, where the Connection
 * acts like JDBC PooledConnections work.  That means we can limit the
 * total number of Connection objects that are created.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.1 $ $Date: 2001/04/17 03:07:46 $
 */
public class JdbcConnection
    extends AbstractLoggable
    implements Connection, Recyclable
{
    private Connection         m_connection;
    private Pool               m_pool;

    public JdbcConnection( final Connection connection, final Pool pool )
    {
        m_connection = connection;
        m_pool = pool;
    }

    public Statement createStatement()
        throws SQLException
    {
        return m_connection.createStatement();
    }

    public PreparedStatement prepareStatement( final String sql )
        throws SQLException
    {
        return m_connection.prepareStatement( sql );
    }

    public CallableStatement prepareCall( final String sql )
        throws SQLException
    {
        return m_connection.prepareCall( sql );
    }

    public String nativeSQL( final String sql )
        throws SQLException
    {
        return m_connection.nativeSQL( sql );
    }

    public void setAutoCommit( final boolean autoCommit )
        throws SQLException
    {
        m_connection.setAutoCommit( autoCommit );
    }

    public boolean getAutoCommit()
        throws SQLException
    {
        return m_connection.getAutoCommit();
    }

    public void commit()
        throws SQLException
    {
        m_connection.commit();
    }

    public void rollback()
        throws SQLException
    {
        m_connection.rollback();
    }

    public void close()
        throws SQLException
    {
        clearWarnings();
        m_pool.put( this );
    }

    public void recycle()
    {
        try { m_connection.close(); }
        catch( final SQLException se )
        {
            getLogger().warn( "Could not close connection", se );
        }
    }

    public boolean isClosed()
        throws SQLException
    {
        return m_connection.isClosed();
    }

    public DatabaseMetaData getMetaData()
        throws SQLException
    {
        return m_connection.getMetaData();
    }

    public void setReadOnly( final boolean readOnly )
        throws SQLException
    {
        m_connection.setReadOnly( readOnly );
    }

    public boolean isReadOnly()
        throws SQLException
    {
        return m_connection.isReadOnly();
    }

    public void setCatalog( final String catalog )
        throws SQLException
    {
        m_connection.setCatalog( catalog );
    }

    public String getCatalog()
        throws SQLException
    {
        return m_connection.getCatalog();
    }

    public void setTransactionIsolation( final int level )
        throws SQLException
    {
        m_connection.setTransactionIsolation(level);
    }

    public int getTransactionIsolation()
        throws SQLException
    {
        return m_connection.getTransactionIsolation();
    }

    public SQLWarning getWarnings()
        throws SQLException
    {
        return m_connection.getWarnings();
    }

    public void clearWarnings()
        throws SQLException
    {
        m_connection.clearWarnings();
    }

    public Statement createStatement( final int resultSetType,
                                      final int resultSetConcurrency )
        throws SQLException
    {
        return m_connection.createStatement(resultSetType, resultSetConcurrency);
    }

    public PreparedStatement prepareStatement( final String sql,
                                               final int resultSetType,
                                               final int resultSetConcurrency )
        throws SQLException
    {
        return m_connection.prepareStatement( sql, resultSetType, resultSetConcurrency );
    }

    public CallableStatement prepareCall( final String sql,
                                          final int resultSetType,
                                          final int resultSetConcurrency )
        throws SQLException
    {
        return m_connection.prepareCall( sql, resultSetType, resultSetConcurrency );
    }

    public Map getTypeMap()
        throws SQLException
    {
        return m_connection.getTypeMap();
    }

    public void setTypeMap( final Map map )
        throws SQLException
    {
        m_connection.setTypeMap( map );
    }
}
