/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.excalibur.datasource;

import java.util.List;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.excalibur.concurrent.Lock;
import org.apache.avalon.excalibur.pool.HardResourceLimitingPool;
import org.apache.avalon.excalibur.pool.DefaultPoolController;
import org.apache.avalon.excalibur.pool.Poolable;

/**
 * The Pool implementation for JdbcConnections.  It uses a background
 * thread to manage the number of SQL Connections.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.2 $ $Date: 2001/08/07 10:57:07 $
 * @since 4.0
 */
public class JdbcConnectionPool
    extends HardResourceLimitingPool
    implements Runnable, Disposable, Initializable
{
    private Thread                 m_initThread;
    private final boolean          m_autoCommit;

    public JdbcConnectionPool( final JdbcConnectionFactory factory, final DefaultPoolController controller, final int min, final int max, final boolean autoCommit)
        throws Exception
    {
        super(factory, controller, max);
        m_min = min;

        this.m_autoCommit = autoCommit;
    }

    public void initialize()
    {
        m_initThread = new Thread( this );
        m_initThread.start();
    }

    protected final Poolable newPoolable() throws Exception
    {
        JdbcConnection conn = (JdbcConnection) super.newPoolable();
        conn.setPool(this);
        return conn;
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

        JdbcConnection obj = (JdbcConnection) super.get();

        if (obj.isClosed()) {
            getLogger().debug("JdbcConnection was closed, creating one to take its place");

            try {
                m_mutex.lock();
                if (m_active.contains(obj))
                {
                    m_active.remove(obj);
                }

                m_factory.decommission(obj);

                obj = (JdbcConnection) this.newPoolable();

                m_active.add(obj);
            }
            catch (Exception e)
            {
                getLogger().warn("Could not get an open connection", e);
                throw e;
            }
            finally
            {
                m_mutex.unlock();
            }
        }

        if (obj.getAutoCommit() != m_autoCommit) {
            obj.setAutoCommit(m_autoCommit);
        }

        return obj;
    }

    public void run()
    {
        try {
            this.grow(this.m_min);

            if (this.size() > 0) {
                m_initialized = true;
            }
        } catch (Exception e) {
            getLogger().debug("JdbcConnectionPool.run()", e);
        }
    }
}
