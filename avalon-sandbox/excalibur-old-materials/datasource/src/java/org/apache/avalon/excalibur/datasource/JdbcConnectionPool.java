/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.datasource;

import java.util.HashSet;
import java.util.Iterator;
import org.apache.avalon.excalibur.concurrent.Lock;
import org.apache.avalon.excalibur.pool.DefaultPoolController;
import org.apache.avalon.excalibur.pool.HardResourceLimitingPool;
import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;

/**
 * The Pool implementation for JdbcConnections.  It uses a background
 * thread to manage the number of SQL Connections.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.14 $ $Date: 2001/12/21 16:58:06 $
 * @since 4.0
 */
public class JdbcConnectionPool
    extends HardResourceLimitingPool
    implements Runnable, Disposable, Initializable
{
    private Thread                 m_initThread;
    private final boolean          m_autoCommit;
    private boolean                m_noConnections = false;
    private long                   m_wait = -1;
    private HashSet                m_waitingThreads = new HashSet();

    public JdbcConnectionPool( final JdbcConnectionFactory factory, final DefaultPoolController controller, final int min, final int max, final boolean autoCommit)
        throws Exception
    {
        super(factory, controller, max);
        m_min = min;

        this.m_autoCommit = autoCommit;
    }

    /**
     * Set the timeout in milliseconds for blocking when waiting for a
     * new connection.  It defaults to -1.  Any number below 1 means that there
     * is no blocking, and the Pool fails hard.  Any number above 0 means we
     * will wait for that length of time before failing.
     */
    public void setTimeout( long timeout )
    {
        if (this.m_initialized)
        {
            throw new IllegalStateException("You cannot change the timeout after the pool is initialized");
        }

        m_wait = timeout;
    }

    public void initialize()
    {
        m_initThread = new Thread( this );
        m_initThread.start();
    }

    protected final Poolable newPoolable() throws Exception
    {
        JdbcConnection conn = null;

        if ( m_wait < 1 )
        {
            conn = (JdbcConnection) super.newPoolable();
        }
        else
        {
            long curMillis = System.currentTimeMillis();
            long endTime = curMillis + m_wait;
            while ( ( null == conn ) && ( curMillis < endTime ) )
            {
                Object thread = Thread.currentThread();
                m_waitingThreads.add(thread);

                try
                {
                    curMillis = System.currentTimeMillis();
                    m_mutex.release();

                    thread.wait( endTime - curMillis );
                }
                finally
                {
                    m_mutex.acquire();
                }

                try
                {
                    conn = (JdbcConnection) super.newPoolable();
                }
                finally
                {
                    // Do nothing except keep waiting
                }
            }
        }

        if (null == conn )
        {
            throw new NoAvailableConnectionException("All available connections are in use");
        }

        conn.setPool(this);
        return conn;
    }

    public Poolable get()
        throws Exception
    {
        if (! m_initialized)
        {
            if (m_noConnections)
            {
                throw new IllegalStateException("There are no connections in the pool, check your settings.");
            }
            else if (m_initThread == null)
            {
                throw new IllegalStateException("You cannot get a Connection before the pool is initialized.");
            }
            else
            {
                m_initThread.join();
            }
        }

        JdbcConnection obj = (JdbcConnection) super.get();

        if (obj.isClosed())
        {
            if (getLogger().isDebugEnabled())
            {
                getLogger().debug("JdbcConnection was closed, creating one to take its place");
            }

            try {
                m_mutex.acquire();
                if (m_active.contains(obj))
                {
                    m_active.remove(obj);
                }

                this.removePoolable(obj);

                obj = (JdbcConnection) this.newPoolable();

                m_active.add(obj);
            }
            catch (Exception e)
            {
                if (getLogger().isWarnEnabled())
                {
                    getLogger().warn("Could not get an open connection", e);
                }
                throw e;
            }
            finally
            {
                m_mutex.release();
            }
        }

        if (obj.getAutoCommit() != m_autoCommit) {
            obj.setAutoCommit(m_autoCommit);
        }

        return obj;
    }

    public void put( Poolable obj )
    {
        super.put( obj );
        Iterator i = m_waitingThreads.iterator();
        while (i.hasNext())
        {
            Object thread = i.next();
            thread.notify();
            i.remove();
        }
    }


    public void run()
    {
        try {
            this.grow(this.m_min);

            if (this.size() > 0) {
                m_initialized = true;
            } else {
                this.m_noConnections = true;

                if (getLogger().isFatalErrorEnabled())
                {
                    getLogger().fatalError("Excalibur could not create any connections.  " +
                                           "Examine your settings to make sure they are correct.  " +
                                           "Make sure you can connect with the same settings on your machine.");
                }
            }
        } catch (Exception e) {
            if (getLogger().isDebugEnabled())
            {
                getLogger().debug("Caught an exception during initialization", e);
            }
        }
    }
}
