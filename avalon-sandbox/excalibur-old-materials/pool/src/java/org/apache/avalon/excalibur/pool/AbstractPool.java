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
package org.apache.avalon.excalibur.pool;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.Buffer; 
import org.apache.commons.collections.UnboundedFifoBuffer; 
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 * This is an <code>Pool</code> that caches Poolable objects for reuse.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.7 $ $Date: 2003/02/20 17:09:24 $
 * @since 4.0
 */
public abstract class AbstractPool
    extends AbstractLogEnabled
    implements Pool, ThreadSafe
{
    public static final int DEFAULT_POOL_SIZE = 8;
    protected final ObjectFactory m_factory;
    protected List m_active = new ArrayList();
    protected Buffer m_ready = new UnboundedFifoBuffer();
    protected Mutex m_mutex = new Mutex();
    protected boolean m_initialized = false;
    protected int m_min;

    /**
     * Create an AbstractPool.  The pool requires a factory, and can
     * optionally have a controller.
     */
    public AbstractPool( final ObjectFactory factory ) throws Exception
    {
        m_factory = factory;

        if( !( this instanceof Initializable ) )
        {
            initialize();
        }
    }

    protected void initialize()
        throws Exception
    {
        lock();

        for( int i = 0; i < AbstractPool.DEFAULT_POOL_SIZE; i++ )
        {
            this.m_ready.add( this.newPoolable() );
        }

        m_initialized = true;

        unlock();
    }

    protected final void lock()
        throws InterruptedException
    {
        m_mutex.acquire();
    }

    protected final void unlock()
        throws InterruptedException
    {
        m_mutex.release();
    }

    /**
     * This is the method to override when you need to enforce creational
     * policies.
     */
    protected Poolable newPoolable() throws Exception
    {
        Object obj = m_factory.newInstance();
        return (Poolable)obj;
    }

    /**
     * This is the method to override when you need to enforce destructional
     * policies.
     */
    protected void removePoolable( Poolable poolable )
    {
        try
        {
            m_factory.decommission( poolable );
        }
        catch( Exception e )
        {
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Error decommissioning object", e );
            }
        }
    }

    public final int size()
    {
        synchronized( this )
        {
            // this is actually not 100% correct as the pool should always
            // reflect the current size (i.e. m_ready.size()) and not the
            // total size.
            return this.m_active.size() + this.m_ready.size();
        }
    }

    public abstract Poolable get() throws Exception;

    public abstract void put( Poolable object );

    protected void internalGrow( final int amount )
        throws Exception
    {
        for( int i = 0; i < amount; i++ )
        {
            try
            {
                m_ready.add( newPoolable() );
            }
            catch( final Exception e )
            {
                if( null != getLogger() && getLogger().isDebugEnabled() )
                {
                    Class createdClass = m_factory.getCreatedClass();
                    if( createdClass == null )
                    {
                        getLogger().debug( "factory created class was null so a new "
                                           + "instance could not be created.", e );
                    }
                    else
                    {
                        getLogger().debug( createdClass.getName() +
                                           ": could not be instantiated.", e );
                    }
                }
            }
        }
    }

    protected void internalShrink( final int amount )
        throws Exception
    {
        for( int i = 0; i < amount; i++ )
        {
            if( m_ready.size() > m_min )
            {
                try
                {
                    this.removePoolable( (Poolable)m_ready.remove() );
                }
                catch( final Exception e )
                {
                    if( null != getLogger() && getLogger().isDebugEnabled() )
                    {
                        getLogger().debug( m_factory.getCreatedClass().getName() +
                                           ": improperly decommissioned.", e );
                    }
                }
            }
        }
    }
}
