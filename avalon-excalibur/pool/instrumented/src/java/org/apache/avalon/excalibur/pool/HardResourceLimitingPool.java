/* 
 * Copyright 2002-2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avalon.excalibur.pool;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 * This is a implementation of  <code>Pool</code> that is thread safe.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version CVS $Revision: 1.3 $ $Date: 2004/02/21 15:38:46 $
 * @since 4.0
 */
public class HardResourceLimitingPool
    extends SoftResourceLimitingPool
    implements ThreadSafe, Initializable
{
    public HardResourceLimitingPool( final ObjectFactory factory, final PoolController controller )
        throws Exception
    {
        this( factory, controller, DEFAULT_POOL_SIZE, DEFAULT_POOL_SIZE );
    }

    public HardResourceLimitingPool( final ObjectFactory factory, final PoolController controller, int max )
        throws Exception
    {
        this( factory, controller, controller.grow(), max );
    }

    public HardResourceLimitingPool( final ObjectFactory factory, final PoolController controller, int initial, int max )
        throws Exception
    {
        super( factory, controller, initial, max );
    }

    public HardResourceLimitingPool( final ObjectFactory factory )
        throws Exception
    {
        this( factory, null );
    }

    public HardResourceLimitingPool( final ObjectFactory factory,
                                     final int initial,
                                     final int maximum )
        throws Exception
    {
        this( factory, null, initial, maximum );
    }

    public HardResourceLimitingPool( final ObjectFactory factory, final int initial )
        throws Exception
    {
        this( factory, initial, initial );
    }

    public HardResourceLimitingPool( final Class clazz, final int initial, final int maximum )
        throws NoSuchMethodException, Exception
    {
        this( new DefaultObjectFactory( clazz ), initial, maximum );
    }

    public HardResourceLimitingPool( final Class clazz, final int initial )
        throws NoSuchMethodException, Exception
    {
        this( clazz, initial, initial );
    }

    public void initialize()
    {
        try
        {
            super.initialize();
        }
        catch( final Exception e )
        {
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Caught init exception", e );
            }
        }
    }

    protected Poolable newPoolable() throws Exception
    {
        if( this.size() < m_max )
        {
            return super.newPoolable();
        }

        throw new InstantiationException( "Ran out of resources to instantiate" );
    }

    protected void internalGrow( int amount )
        throws Exception
    {
        super.internalGrow( ( ( this.size() + amount ) < m_max ) ? amount : m_max - this.size() );
    }
}
