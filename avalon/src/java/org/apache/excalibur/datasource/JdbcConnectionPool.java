/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.avalon.activity.Disposable;
import org.apache.avalon.activity.Initializable;
import org.apache.avalon.logger.AbstractLoggable;
import org.apache.excalibur.concurrent.Lock;
import org.apache.excalibur.pool.Pool;
import org.apache.excalibur.pool.Poolable;
import org.apache.excalibur.pool.Recyclable;

/**
 * The Pool implementation for JdbcConnections.  It uses a background
 * thread to manage the number of SQL Connections.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.4 $ $Date: 2001/04/25 14:24:42 $
 */
public class JdbcConnectionPool
    extends AbstractLoggable
    implements Pool, Runnable, Disposable, Initializable
{
    private final String           m_dburl;
    private final String           m_username;
    private final String           m_password;
    private final int              m_max;
    private final boolean          m_autoCommit;
    private List                   m_active        = new ArrayList();
    private List                   m_ready         = new ArrayList();
    private boolean                m_initialized   = false;
    private boolean                m_disposed      = false;
    private Lock                   m_mutex         = new Lock();
    private Thread                 m_initThread;

    public JdbcConnectionPool( final String url,
                               final String username,
                               final String password,
                               final int max,
                               final boolean autoCommit )
    {
        m_dburl = url;
        m_username = username;
        m_password = password;

        if( max < 1 )
        {
            getLogger().warn( "Maximum number of connections specified must be at " +
                              "least 1 and must be greater than the minumum number " +
                              "of connections" );
            m_max = 1;
        }
        else
        {
            m_max = max;
        }

        m_autoCommit = autoCommit;
    }

    public void initialize()
    {
        m_initThread = new Thread( this );
        m_initThread.start();
    }

    private JdbcConnection createJdbcConnection()
        throws SQLException
    {
        JdbcConnection connection = null;

        if( null == m_username )
        {
            connection = new JdbcConnection( DriverManager.getConnection( m_dburl ), this );
            connection.setLogger(getLogger());
        }
        else
        {
            connection =
                new JdbcConnection( DriverManager.getConnection( m_dburl,
                                                                 m_username,
                                                                 m_password ),
                                    this);
            connection.setLogger(getLogger());
        }

        getLogger().debug( "JdbcConnection object created" );
        return connection;
    }

    private void recycle( final Recyclable obj )
    {
        getLogger().debug( "JdbcConnection object recycled" );
        obj.recycle();
    }

    public Poolable get()
        throws Exception
    {
        if (! m_initialized)
        {
            if (m_initThread == null)
            {
                throw new IllegalStateException("You cannot get a Connection before the pool is initialized");
            }
            else
            {
                m_initThread.join();
            }
        }

        if (m_disposed)
        {
            throw new IllegalStateException("You cannot get a Connection after the pool is disposed");
        }

        Poolable obj = null;

        try {
            this.m_mutex.lock();
            final int size;

            if( 0 == m_ready.size() )
            {
                if (m_active.size() < m_max)
                {
                    obj = this.createJdbcConnection();

                    m_active.add(obj);
                }
                else
                {
                    throw new SQLException("There are no more Connections available");
                }
            }
            else
            {
                obj = (Poolable)m_ready.remove( 0 );
                if (((Connection)obj).isClosed())
                {
                    ((JdbcConnection)obj).recycle();
                    obj = this.createJdbcConnection();
                }

                m_active.add( obj );
            }
        } catch (Exception e) {
            getLogger().debug("JdbcConnectionPool.get()", e);
        } finally {
            this.m_mutex.unlock();
        }

        if (((Connection)obj).getAutoCommit() != m_autoCommit) {
            ((Connection)obj).setAutoCommit(m_autoCommit);
        }

        getLogger().debug( "JdbcConnection '" + m_dburl +
                           "' has been requested from pool." );

        return obj;
    }

    public void put( final Poolable obj )
    {
        if (! m_initialized)
        {
            throw new IllegalStateException("You cannot return an object to an uninitialized pool");
        }

        try {
            this.m_mutex.lock();
            m_active.remove( obj );

            if(! m_disposed)
            {
                JdbcConnection connection = (JdbcConnection) obj;

                if (connection.isClosed()) {
                    getLogger().warn("Connection was closed by server, attempting to create a new one in its stead.");
                    connection.recycle();
                    connection = this.createJdbcConnection();
                }

                m_ready.add( connection );
            } else {
                recycle((Recyclable) obj);
            }
        } catch (Exception e) {
            getLogger().warn("Error returning connection to pool", e);
        } finally {
            this.m_mutex.unlock();
        }

        getLogger().debug( "JdbcConnection '" + m_dburl + "' has been returned to the pool." );
    }

    public void run()
    {
        try {
            this.m_mutex.lock();
            for (int i = 0; i < m_max; i++)
            {
                try {
                    m_ready.add( createJdbcConnection() );
                } catch (SQLException se) {
                    getLogger().error( "Could not create connection to database", se );
                }
            }

            if (m_ready.size() > 0) {
                m_initialized = true;
            }
        } catch (Exception e) {
            getLogger().debug("JdbcConnectionPool.run()", e);
        } finally {
            this.m_mutex.unlock();
        }
    }

    public void dispose()
    {
        try {
            this.m_mutex.lock();
            m_disposed = true;

            while( ! m_ready.isEmpty() )
            {
                recycle( (Recyclable)m_ready.remove( 0 ) );
            }
        } catch (Exception e) {
            getLogger().debug("JdbcConnectionPool.dispose()", e);
        } finally {
            this.m_mutex.unlock();
        }
    }
}
