/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.datasource;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Map;

/**
 * The Connection object used in conjunction with the JdbcDataSource
 * object.
 *
 * TODO: Implement a configurable closed end Pool, where the Connection
 * acts like JDBC PooledConnections work.  That means we can limit the
 * total number of Connection objects that are created.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.5 $ $Date: 2002/01/26 16:00:31 $
 * @since 4.0
 */
public class JdbcConnection
    extends AbstractJdbcConnection
{
    public JdbcConnection( final Connection connection, final String keepAlive )
    {
        super( connection, keepAlive );
    }

    public final Statement createStatement()
        throws SQLException
    {
        final Statement temp = m_connection.createStatement();
        m_lastUsed = System.currentTimeMillis();
        return temp;
    }

    public final PreparedStatement prepareStatement( final String sql )
        throws SQLException
    {
        final PreparedStatement temp = m_connection.prepareStatement( sql );
        m_lastUsed = System.currentTimeMillis();
        return temp;
    }

    public final CallableStatement prepareCall( final String sql )
        throws SQLException
    {
        final CallableStatement temp = m_connection.prepareCall( sql );
        m_lastUsed = System.currentTimeMillis();
        return temp;
    }

    public final String nativeSQL( final String sql )
        throws SQLException
    {
        return m_connection.nativeSQL( sql );
    }

    public final void setAutoCommit( final boolean autoCommit )
        throws SQLException
    {
        m_connection.setAutoCommit( autoCommit );
    }

    public final boolean getAutoCommit()
        throws SQLException
    {
        return m_connection.getAutoCommit();
    }

    public final void commit()
        throws SQLException
    {
        m_connection.commit();
        m_lastUsed = System.currentTimeMillis();
    }

    public final void rollback()
        throws SQLException
    {
        m_connection.rollback();
        m_lastUsed = System.currentTimeMillis();
    }

    public final DatabaseMetaData getMetaData()
        throws SQLException
    {
        return m_connection.getMetaData();
    }

    public final void setReadOnly( final boolean readOnly )
        throws SQLException
    {
        m_connection.setReadOnly( readOnly );
    }

    public final boolean isReadOnly()
        throws SQLException
    {
        return m_connection.isReadOnly();
    }

    public final void setCatalog( final String catalog )
        throws SQLException
    {
        m_connection.setCatalog( catalog );
    }

    public final String getCatalog()
        throws SQLException
    {
        return m_connection.getCatalog();
    }

    public final void setTransactionIsolation( final int level )
        throws SQLException
    {
        m_connection.setTransactionIsolation(level);
    }

    public final int getTransactionIsolation()
        throws SQLException
    {
        return m_connection.getTransactionIsolation();
    }

    public final SQLWarning getWarnings()
        throws SQLException
    {
        return m_connection.getWarnings();
    }

    public final void clearWarnings()
        throws SQLException
    {
        m_connection.clearWarnings();
    }

    public final Statement createStatement( final int resultSetType,
                                            final int resultSetConcurrency )
        throws SQLException
    {
        final Statement temp = m_connection.createStatement(
                resultSetType, resultSetConcurrency
        );

        m_lastUsed = System.currentTimeMillis();
        return temp;
    }

    public final PreparedStatement prepareStatement( final String sql,
                                               final int resultSetType,
                                               final int resultSetConcurrency )
        throws SQLException
    {
        final PreparedStatement temp = m_connection.prepareStatement(
                sql, resultSetType, resultSetConcurrency
        );

        m_lastUsed = System.currentTimeMillis();
        return temp;
    }

    public final CallableStatement prepareCall( final String sql,
                                          final int resultSetType,
                                          final int resultSetConcurrency )
        throws SQLException
    {
        final CallableStatement temp = m_connection.prepareCall(
            sql, resultSetType, resultSetConcurrency
        );

        m_lastUsed = System.currentTimeMillis();
        return temp;
    }

    public final Map getTypeMap()
        throws SQLException
    {
        return m_connection.getTypeMap();
    }

    public final void setTypeMap( final Map map )
        throws SQLException
    {
        m_connection.setTypeMap( map );
    }

@JDBC3_START@
    public final void setHoldability(int holdability)
        throws SQLException
    {
        throw new SQLException("This is not a Jdbc 3.0 Compliant Connection");
    }

    public final int getHoldability()
        throws SQLException
    {
        throw new SQLException("This is not a Jdbc 3.0 Compliant Connection");
    }

    public final java.sql.Savepoint setSavepoint()
        throws SQLException
    {
        throw new SQLException("This is not a Jdbc 3.0 Compliant Connection");
    }

    public final java.sql.Savepoint setSavepoint(String savepoint)
        throws SQLException
    {
        throw new SQLException("This is not a Jdbc 3.0 Compliant Connection");
    }

    public final void rollback(java.sql.Savepoint savepoint)
        throws SQLException
    {
        throw new SQLException("This is not a Jdbc 3.0 Compliant Connection");
    }

    public final void releaseSavepoint(java.sql.Savepoint savepoint)
        throws SQLException
    {
        throw new SQLException("This is not a Jdbc 3.0 Compliant Connection");
    }

    public final Statement createStatement(int resulSetType,
                                           int resultSetConcurrency,
                                           int resultSetHoldability)
        throws SQLException
    {
        throw new SQLException("This is not a Jdbc 3.0 Compliant Connection");
    }

    public final PreparedStatement prepareStatement(String sql,
                                        int resulSetType,
                                        int resultSetConcurrency,
                                        int resultSetHoldability)
        throws SQLException
    {
        throw new SQLException("This is not a Jdbc 3.0 Compliant Connection");
    }

    public final CallableStatement prepareCall(String sql,
                                        int resulSetType,
                                        int resultSetConcurrency,
                                        int resultSetHoldability)
        throws SQLException
    {
        throw new SQLException("This is not a Jdbc 3.0 Compliant Connection");
    }

    public final PreparedStatement prepareStatement(String sql,
                                        int autoGeneratedKeys)
        throws SQLException
    {
        throw new SQLException("This is not a Jdbc 3.0 Compliant Connection");
    }

    public final PreparedStatement prepareStatement(String sql,
                                        int[] columnIndexes)
        throws SQLException
    {
        throw new SQLException("This is not a Jdbc 3.0 Compliant Connection");
    }

    public final PreparedStatement prepareStatement(String sql,
                                        String[] columnNames)
        throws SQLException
    {
        throw new SQLException("This is not a Jdbc 3.0 Compliant Connection");
    }
@JDBC3_END@
}

