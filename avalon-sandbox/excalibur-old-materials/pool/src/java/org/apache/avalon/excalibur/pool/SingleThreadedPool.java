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

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.SingleThreaded;

/**
 * This is an <code>Pool</code> that caches Poolable objects for reuse.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version CVS $Revision: 1.2 $ $Date: 2003/02/20 17:09:24 $
 * @since 4.0
 */
public class SingleThreadedPool
    extends AbstractLogEnabled
    implements Pool, Initializable, SingleThreaded, Resizable, Disposable
{
    protected boolean m_initialized;
    protected int m_count;
    protected Poolable[] m_pool;
    protected ObjectFactory m_factory;
    protected PoolController m_controller;
    protected int m_maximum;
    protected int m_initial;

    public SingleThreadedPool( final Class clazz,
                               final int initial,
                               final int maximum ) throws Exception
    {
        this( new DefaultObjectFactory( clazz ), initial, maximum );
    }

    public SingleThreadedPool( final ObjectFactory factory,
                               final int initial,
                               final int maximum ) throws Exception
    {
        this( factory, null, initial, maximum );
    }

    public SingleThreadedPool( final ObjectFactory factory,
                               final PoolController controller,
                               final int initial,
                               final int maximum ) throws Exception
    {
        m_count = 0;
        m_factory = factory;
        m_controller = controller;
        m_maximum = maximum;
        m_initial = initial;
    }

    public void initialize()
        throws Exception
    {
        m_initialized = true;

        grow( m_maximum );
        fill( m_initial );
    }

    /**
     * Retrieve an object from pool.
     *
     * @return an object from Pool
     */
    public Poolable get() throws Exception
    {
        // To make this class backwards compatible, it has to auto initialize if necessary
        if( !m_initialized )
        {
            initialize();
        }

        if( null == m_pool && null != m_controller )
        {
            final int increase = m_controller.grow();
            if( increase > 0 )
            {
                grow( increase );
            }
        }

        if( 0 > m_count )
        {
            m_count = -1;
            return (Poolable)m_factory.newInstance();
        }
        else if( 0 == m_count )
        {
            m_count--;
            return m_pool[ 0 ];
        }

        final Poolable poolable = m_pool[ m_count ];
        m_pool[ m_count ] = null;
        m_count--;
        return poolable;
    }

    /**
     * Place an object in pool.
     *
     * @param poolable the object to be placed in pool
     */
    public void put( final Poolable poolable )
    {
        if( poolable instanceof Recyclable )
        {
            ( (Recyclable)poolable ).recycle();
        }

        if( m_pool.length == ( m_count + 1 ) && null != m_controller )
        {
            final int decrease = m_controller.shrink();
            if( decrease > 0 )
            {
                shrink( decrease );
            }
        }

        if( m_pool.length > m_count + 1 )
        {
            m_count++;
            m_pool[ m_count ] = poolable;
        }
        else
        {
            try
            {
                m_factory.decommission( poolable );
            }
            catch( Exception e )
            {
                // To be backwards compatible, we have to support the logger having not been set.
                if( ( getLogger() != null ) && ( getLogger().isDebugEnabled() ) )
                {
                    getLogger().debug( "Error decommissioning object", e );
                }
            }
        }
    }

    /**
     * Return the total number of slots in Pool
     *
     * @return the total number of slots
     */
    public final int getCapacity()
    {
        return m_pool.length;
    }

    /**
     * Get the number of used slots in Pool
     *
     * @return the number of used slots
     * @deprecated use the official size() method instead
     */
    public final int getSize()
    {
        return m_count;
    }

    /**
     * Get the number of used slots in Pool
     *
     * @return the number of used slots
     */
    public final int size()
    {
        return getSize();
    }

    /**
     * This fills the pool to the size specified in parameter.
     */
    public final void fill( final int fillSize ) throws Exception
    {
        final int size = Math.min( m_pool.length, fillSize );

        for( int i = m_count; i < size; i++ )
        {
            m_pool[ i ] = (Poolable)m_factory.newInstance();
        }

        m_count = size - 1;
    }

    /**
     * This fills the pool by the size specified in parameter.
     */
    public final void grow( final int increase )
    {
        if( null == m_pool )
        {
            m_pool = new Poolable[ increase ];
            return;
        }

        final Poolable[] poolables = new Poolable[ increase + m_pool.length ];
        System.arraycopy( m_pool, 0, poolables, 0, m_pool.length );
        m_pool = poolables;
    }

    /**
     * This shrinks the pool by parameter size.
     */
    public final void shrink( final int decrease )
    {
        final Poolable[] poolables = new Poolable[ m_pool.length - decrease ];
        System.arraycopy( m_pool, 0, poolables, 0, poolables.length );
        m_pool = poolables;
    }

    /**
     * Dispose the pool and decommission any Poolables.
     */
    public void dispose()
    {
        while( m_count > 0 )
        {
            int i = m_count - 1;
            try
            {
                m_factory.decommission( m_pool[ i ] );
            }
            catch( Exception e )
            {
                // To be backwards compatible, we have to support the logger having not been set.
                if( ( getLogger() != null ) && ( getLogger().isDebugEnabled() ) )
                {
                    getLogger().debug( "Error decommissioning object", e );
                }
            }
            m_pool[ i ] = null;
            m_count--;
        }
    }
}
