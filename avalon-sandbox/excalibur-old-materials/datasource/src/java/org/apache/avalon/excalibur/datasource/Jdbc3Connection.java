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
import java.sql.Savepoint;
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
 * @version CVS $Revision: 1.9 $ $Date: 2001/12/11 09:53:28 $
 * @since 4.0
 */
public class Jdbc3Connection
    extends AbstractJdbcConnection
{
    public Jdbc3Connection( final Connection connection, final String keepAlive )
    {
        super( connection, keepAlive );
    }

    public final Statement createStatement()
        throws SQLException
    {
        return m_connection.createStatement();
    }

    public final PreparedStatement prepareStatement( final String sql )
        throws SQLException
    {
        return m_connection.prepareStatement( sql );
    }

    public final CallableStatement prepareCall( final String sql )
        throws SQLException
    {
        return m_connection.prepareCall( sql );
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
    }

    public final void rollback()
        throws SQLException
    {
        m_connection.rollback();
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
        return m_connection.createStatement(resultSetType, resultSetConcurrency);
    }

    public final PreparedStatement prepareStatement( final String sql,
                                               final int resultSetType,
                                               final int resultSetConcurrency )
        throws SQLException
    {
        return m_connection.prepareStatement( sql, resultSetType, resultSetConcurrency );
    }

    public final CallableStatement prepareCall( final String sql,
                                          final int resultSetType,
                                          final int resultSetConcurrency )
        throws SQLException
    {
        return m_connection.prepareCall( sql, resultSetType, resultSetConcurrency );
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

    public final void setHoldability(int holdability)
        throws SQLException
    {
        m_connection.setHoldability(holdability);
    }

    public final int getHoldability()
        throws SQLException
    {
        return m_connection.getHoldability();
    }

    public final Savepoint setSavepoint()
        throws SQLException
    {
        return m_connection.setSavepoint();
    }

    public final Savepoint setSavepoint(String savepoint)
        throws SQLException
    {
        return m_connection.setSavepoint(savepoint);
    }

    public final void rollback(Savepoint savepoint)
        throws SQLException
    {
        m_connection.rollback(savepoint);
    }

    public final void releaseSavepoint(Savepoint savepoint)
        throws SQLException
    {
        m_connection.releaseSavepoint(savepoint);
    }

    public final Statement createStatement(int resulSetType,
                                           int resultSetConcurrency,
                                           int resultSetHoldability)
        throws SQLException
    {
        return m_connection.createStatement(resulSetType,
                                            resultSetConcurrency,
                                            resultSetHoldability);
    }

    public final PreparedStatement prepareStatement(String sql,
                                        int resulSetType,
                                        int resultSetConcurrency,
                                        int resultSetHoldability)
        throws SQLException
    {
        return m_connection.prepareStatement(sql,
                                             resulSetType,
                                             resultSetConcurrency,
                                             resultSetHoldability);
    }

    public final CallableStatement prepareCall(String sql,
                                        int resulSetType,
                                        int resultSetConcurrency,
                                        int resultSetHoldability)
        throws SQLException
    {
        return m_connection.prepareCall(sql,
                                        resulSetType,
                                        resultSetConcurrency,
                                        resultSetHoldability);
    }

    public final PreparedStatement prepareStatement(String sql,
                                        int autoGeneratedKeys)
        throws SQLException
    {
        return m_connection.prepareStatement(sql,
                                             autoGeneratedKeys);
    }

    public final PreparedStatement prepareStatement(String sql,
                                        int[] columnIndexes)
        throws SQLException
    {
        return m_connection.prepareStatement(sql,
                                             columnIndexes);
    }

    public final PreparedStatement prepareStatement(String sql,
                                        String[] columnNames)
        throws SQLException
    {
       return m_connection.prepareStatement(sql,
                                            columnNames);
    }
}
