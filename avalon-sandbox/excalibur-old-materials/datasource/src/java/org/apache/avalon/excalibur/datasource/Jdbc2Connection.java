/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
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
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.excalibur.pool.Pool;
import org.apache.avalon.framework.logger.Logger;

/**
 * The Connection object used in conjunction with the JdbcDataSource
 * object.
 *
 * TODO: Implement a configurable closed end Pool, where the Connection
 * acts like JDBC PooledConnections work.  That means we can limit the
 * total number of Connection objects that are created.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.6 $ $Date: 2001/10/31 21:40:59 $
 * @since 4.0
 */
public class JdbcConnection
    extends AbstractLoggable
    implements Connection, Recyclable, Disposable, Initializable
{
    protected Connection         m_connection;
    protected Pool               m_pool;
    protected PreparedStatement  m_test_statement;
    protected SQLException       m_test_exception;
    protected int                m_num_uses        = 15;

    /**
     * @deprecated Use the version with keepAlive specified
     */
    public JdbcConnection( final Connection connection, final boolean oradb )
    {
        this(connection, (oradb) ? "select 1 from dual" : "select 1");
    }

    public JdbcConnection( final Connection connection, final String keepAlive )
    {
        m_connection = connection;

        // subclasses can override initialize()
        this.initialize();

        if (null != keepAlive && "".equals(keepAlive.trim()))
        {
            try
            {
                m_test_statement = prepareStatement(keepAlive);
            }
            catch ( final SQLException se )
            {
                m_test_statement = null;
                m_test_exception = se;
            }
        }
        else
        {
            m_test_statement = null;
            m_test_exception = null;
        }
    }

    /**
     * Extend this for connection initialization--only needed for some drivers.
     */
    public void initialize()
    {
    }

    protected void setPool(Pool pool)
    {
        this.m_pool = pool;
    }

    public final void setLogger( final Logger log )
    {
        super.setLogger(log);

        if (m_test_statement == null && m_test_exception != null)
        {
            if (getLogger().isWarnEnabled())
            {
                getLogger().warn("Could not prepare test statement", m_test_exception);
            }
            m_test_exception = null;
        }
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

    public final void close()
        throws SQLException
    {
        clearWarnings();
        m_pool.put( this );
    }

    public final void dispose()
    {
        try { m_connection.close(); }
        catch( final SQLException se )
        {
            if (getLogger().isWarnEnabled())
            {
                getLogger().warn( "Could not close connection", se );
            }
        }
    }

    public final void recycle() {
        this.m_num_uses--;
        this.m_test_exception = null;
    }

    public final boolean isClosed()
        throws SQLException
    {
        if ( m_connection.isClosed())
        {
            return true;
        }

        if ( this.m_num_uses <= 0 )
        {
            this.dispose();
            return true;
        }

        if (m_test_statement != null)
        {
            try
            {
                m_test_statement.executeQuery();
            }
            catch (final SQLException se)
            {
                this.dispose();
                return true;
            }
        }

        return false;
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
}
