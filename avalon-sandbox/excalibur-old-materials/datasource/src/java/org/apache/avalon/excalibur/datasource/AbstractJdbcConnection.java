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
import java.util.LinkedList;
import java.util.List;

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
 * @version CVS $Revision: 1.23 $ $Date: 2003/02/25 16:28:37 $
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
     * Contains Statements created on the original jdbc connection
     * between a {@link JdbcDataSource#getConnection} and {@link
     * Connection#close}. The statements are registered using
     * {@link #registerAllocatedStatement} and deallocated in
     * {@link #close}. LinkedList was chosen because access
     * to elements is sequential through Iterator and the number
     * of elements is not known in advance. Synchronization is
     * done on the Link instance itself.
     */
    final private List m_allocatedStatements = new LinkedList();

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
                // test statement is allocated directly from the
                // underlying connection, it is special and should not
                // be closed during recycling session
                m_testStatement = m_connection.prepareStatement( keepAlive );
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
            clearAllocatedStatements();
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
        try
        {
            clearAllocatedStatements();
            clearWarnings();
            m_pool.put( this );
        }
        catch ( SQLException se )
        {
            // gets rid of connections that throw SQLException during
            // clean up
            getLogger().error( "Connection could not be recycled", se );
            this.dispose();
        }
    }

    /**
     * Closes statements that were registered and removes all
     * statements from the list of allocated ones.  If any statement
     * fails to properly close, the rest of the statements is ignored.
     * But the registration list if cleared in any case.
     * <p>
     * Holds m_allocatedStatements locked the whole time. This should
     * not be a problem because connections are inherently single
     * threaded objects and any attempt to use them from a different
     * thread while it is being closed is a violation of the contract.
     *
     * @throws SQLException of the first Statement.close()
     */
    protected void clearAllocatedStatements() throws SQLException
    {
        synchronized( m_allocatedStatements )
        {
            try
            {
                final Iterator iterator = m_allocatedStatements.iterator();
                while( iterator.hasNext() )
                {
                    Statement stmt = (Statement) iterator.next();
                    stmt.close();
                }
            }
            finally
            {
                m_allocatedStatements.clear();
            }
        }
    }

    /**
     * Adds the statement to the list of this connection.  Used by
     * subclasses to ensure release of statements when connection is
     * logically terminated and returned to the pool.
     */
    protected void registerAllocatedStatement( Statement stmt )
    {
        synchronized( m_allocatedStatements )
        {
            m_allocatedStatements.add( stmt );
        }
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
