/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 
 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:
 
 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.
 
 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.
 
 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.
 
 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"  
    must not be used to endorse or promote products derived from this  software 
    without  prior written permission. For written permission, please contact 
    apache@apache.org.
 
 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.
 
 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the 
 Apache Software Foundation, please see <http://www.apache.org/>.
 
*/
package org.apache.avalon.excalibur.datasource;

import java.sql.Connection;

import org.apache.avalon.excalibur.pool.DefaultPoolController;
import org.apache.avalon.excalibur.pool.HardResourceLimitingPool;
import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;

/**
 * The Pool implementation for JdbcConnections.  It uses a background
 * thread to manage the number of SQL Connections.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 14:13:58 $
 * @since 4.0
 */
public class JdbcConnectionPool
    extends HardResourceLimitingPool
    implements Runnable, Disposable, Initializable
{
    private Exception m_cause = null;
    private Thread m_initThread;
    private final boolean m_autoCommit;
    private boolean m_noConnections;
    private long m_wait = -1;
    private Object m_spinLock = new Object();

    public JdbcConnectionPool( final JdbcConnectionFactory factory, 
                               final DefaultPoolController controller, 
                               final int min, 
                               final int max, 
                               final boolean autoCommit )
        throws Exception
    {
        super( factory, controller, max );
        m_min = min;
        m_initialized = false;
        m_autoCommit = autoCommit;
    }

    /**
     * Set the timeout in milliseconds for blocking when waiting for a
     * new connection.  It defaults to -1.  Any number below 1 means that there
     * is no blocking, and the Pool fails hard.  Any number above 0 means we
     * will wait for that length of time before failing.
     */
    public void setTimeout( long timeout )
    {
        if( this.m_initialized )
        {
            throw new IllegalStateException( "You cannot change the timeout after the pool is initialized" );
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
        PoolSettable conn = null;

        if( m_wait < 1 )
        {
            conn = (PoolSettable)super.newPoolable();
        }
        else
        {
            long curMillis = System.currentTimeMillis();
            long endTime = curMillis + m_wait;
            while( ( null == conn ) && ( curMillis < endTime ) )
            {
                try
                {
                    unlock();
                    curMillis = System.currentTimeMillis();

                    synchronized(m_spinLock)
                    {
                        m_spinLock.wait( endTime - curMillis );
                    }
                }
                finally
                {
                    lock();
                }

                try
                {
                    conn = (PoolSettable)super.newPoolable();
                }
                finally
                {
                    // Do nothing except keep waiting
                }
            }
        }

        if( null == conn )
        {
            throw new NoAvailableConnectionException( "All available connections are in use" );
        }

        conn.setPool( this );
        return conn;
    }

    public Poolable get()
        throws Exception
    {
        if( !m_initialized )
        {
            if( m_noConnections )
            {
                if (m_cause != null) throw m_cause;
                
                throw new IllegalStateException( "There are no connections in the pool, check your settings." );
            }
            else if( m_initThread == null )
            {
                throw new IllegalStateException( "You cannot get a Connection before the pool is initialized." );
            }
            else
            {
                m_initThread.join();
            }
        }

        PoolSettable obj = (PoolSettable)super.get();

        if( obj.isClosed() )
        {
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "JdbcConnection was closed, creating one to take its place" );
            }

            try
            {
                lock();
                if( m_active.contains( obj ) )
                {
                    m_active.remove( obj );
                }

                this.removePoolable( obj );

                obj = (PoolSettable)this.newPoolable();

                m_active.add( obj );
            }
            catch( Exception e )
            {
                if( getLogger().isWarnEnabled() )
                {
                    getLogger().warn( "Could not get an open connection", e );
                }
                throw e;
            }
            finally
            {
                unlock();
            }
        }

        if( ((Connection)obj).getAutoCommit() != m_autoCommit )
        {
            ((Connection)obj).setAutoCommit( m_autoCommit );
        }

        return obj;
    }

    public void put( Poolable obj )
    {
        super.put( obj );
        synchronized(m_spinLock)
        {
            m_spinLock.notifyAll();
        }
    }

    public void run()
    {
        try
        {
            this.grow( this.m_min );

            if( this.size() > 0 )
            {
                m_initialized = true;
            }
            else
            {
                this.m_noConnections = true;

                if( getLogger().isFatalErrorEnabled() )
                {
                    getLogger().fatalError( "Excalibur could not create any connections.  " +
                                            "Examine your settings to make sure they are correct.  " +
                                            "Make sure you can connect with the same settings on your machine." );
                }
            }
        }
        catch( Exception e )
        {
            m_cause = e;
            if( getLogger().isWarnEnabled() )
            {
                getLogger().warn( "Caught an exception during initialization", e );
            }
        }
    }
}
