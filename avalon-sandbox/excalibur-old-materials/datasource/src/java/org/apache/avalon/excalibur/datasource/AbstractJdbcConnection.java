/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.datasource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;
import org.apache.avalon.excalibur.pool.Pool;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;

/**
 * The Connection object used in conjunction with the JdbcDataSource
 * object.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.21 $ $Date: 2002/11/08 03:24:27 $
 * @since 4.1
 */
public abstract class AbstractJdbcConnection
    extends AbstractLogEnabled
    implements Connection, Recyclable, Disposable, Initializable
{
    protected Connection m_connection;
    protected Pool m_pool;
    protected PreparedStatement m_testStatement;
    protected SQLException m_testException;
    protected long m_lastUsed = System.currentTimeMillis();

    /**
     * @deprecated Use the version with keepAlive specified
     */
    public AbstractJdbcConnection( final Connection connection, final boolean oradb )
    {
        this( connection, ( oradb ) ? "select 1 from dual" : "select 1" );
    }

    /**
     * @param connection a driver specific JDBC connection to be wrapped.
     * @param keepAlive a query which will be used to check the statis of the connection after it
     *                  has been idle.  A null value will cause the keep alive feature to
     *                  be disabled.
     */
    public AbstractJdbcConnection( final Connection connection, final String keepAlive )
    {
        m_connection = connection;

        // subclasses can override initialize()
        this.initialize();

        if( null == keepAlive || "".equals( keepAlive.trim() ) )
        {
            m_testStatement = null;
            m_testException = null;
        }
        else
        {
            try
            {
                m_testStatement = prepareStatement( keepAlive );
            }
            catch( final SQLException se )
            {
                m_testStatement = null;
                m_testException = se;
            }
        }
    }

    public void initialize()
    {
    }

    public void enableLogging( final Logger log )
    {
        super.enableLogging( log );

        if( m_testStatement == null && m_testException != null )
        {
            if( getLogger().isWarnEnabled() )
            {
                getLogger().warn( "Could not prepare test statement, connection recycled on basis of time.", m_testException );
            }
            m_testException = null;
        }
    }

    protected void setPool( Pool pool )
    {
        m_pool = pool;
    }

    public void recycle()
    {
        //m_lastUsed = System.currentTimeMillis(); // not accurate
        m_testException = null;
        try
        {
            m_connection.clearWarnings();
        }
        catch( SQLException se )
        {
            // ignore
        }
    }

    public boolean isClosed()
        throws SQLException
    {
        if( m_connection.isClosed() )
        {
            return true;
        }

        long age = System.currentTimeMillis() - m_lastUsed;
        if( age > 1000 * 60 * 60 ) // over an hour?
        {
            this.dispose();
            return true;
        }

        if( m_testStatement != null && age > ( 5 * 1000 ) ) // over 5 seconds ago
        {
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Pinging database after " + age + "ms of inactivity." );
            }

            try
            {
                ResultSet rs = m_testStatement.executeQuery();
                rs.close();
            }
            catch( final SQLException se )
            {
                getLogger().debug( "Ping of connection failed.", se );
                this.dispose();
                return true;
            }
        }

        return false;
    }

    public void close()
        throws SQLException
    {
        clearWarnings();
        m_pool.put( this );
    }

    public void dispose()
    {
        try
        {
            m_connection.close();
        }
        catch( final SQLException se )
        {
            if( getLogger().isWarnEnabled() )
            {
                getLogger().warn( "Could not close connection", se );
            }
        }
    }

/*    @JDBC3_START@
    public abstract void setHoldability( int holdability )
        throws SQLException;

    public abstract int getHoldability()
        throws SQLException;

    public abstract java.sql.Savepoint setSavepoint()
        throws SQLException;

    public abstract java.sql.Savepoint setSavepoint( String savepoint )
        throws SQLException;

    public abstract void rollback( java.sql.Savepoint savepoint )
        throws SQLException;

    public abstract void releaseSavepoint( java.sql.Savepoint savepoint )
        throws SQLException;

    public abstract Statement createStatement( int resulSetType,
                                               int resultSetConcurrency,
                                               int resultSetHoldability )
        throws SQLException;

    public abstract PreparedStatement prepareStatement( String sql,
                                                        int resulSetType,
                                                        int resultSetConcurrency,
                                                        int resultSetHoldability )
        throws SQLException;

    public abstract java.sql.CallableStatement prepareCall( String sql,
                                                   int resulSetType,
                                                   int resultSetConcurrency,
                                                   int resultSetHoldability )
        throws SQLException;

    public abstract PreparedStatement prepareStatement( String sql,
                                                        int autoGeneratedKeys )
        throws SQLException;

    public abstract PreparedStatement prepareStatement( String sql,
                                                        int[] columnIndexes )
        throws SQLException;

    public abstract PreparedStatement prepareStatement( String sql,
                                                        String[] columnNames )
        throws SQLException;
    @JDBC3_END@ */
}
