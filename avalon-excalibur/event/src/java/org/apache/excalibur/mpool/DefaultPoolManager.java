/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) @year@ The Apache Software Foundation. All rights reserved.

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
package org.apache.excalibur.mpool;

import java.util.Iterator;
import java.util.Random;
import org.apache.avalon.excalibur.collections.BucketMap;
import org.apache.excalibur.event.Queue;
import org.apache.excalibur.event.command.RepeatedCommand;

/**
 * This interface is for a PoolManager that creates pools that are managed
 * asynchronously.  The contract is that the controller type is specified in
 * the constructor.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/08/13 08:15:20 $
 * @since 4.1
 */
public class DefaultPoolManager implements PoolManager
{
    private final long m_managerKey;
    private final Random m_keyGenerator;
    private final BucketMap m_keyMap = new BucketMap();
    private final BucketMap m_factoryMap = new BucketMap();

    public DefaultPoolManager()
    {
        this( null );
    }

    public DefaultPoolManager( final Queue commandQueue )
    {
        m_keyGenerator = new Random();
        m_managerKey = m_keyGenerator.nextLong();

        if( null != commandQueue )
        {
            try
            {
                commandQueue.enqueue( new PoolManagerCommand( m_keyMap ) );
            }
            catch( Exception e )
            {
                // ignore silently for now
            }
        }
    }

    /**
     * Return a managed pool that has a controller.
     */
    public Pool getManagedPool( ObjectFactory factory, int initialEntries )
        throws Exception
    {
        ManagablePool pool = (ManagablePool)m_factoryMap.get( factory );

        if( null == pool )
        {
            final long poolKey = getKey();
            pool = new VariableSizePool( factory, initialEntries, poolKey );
            m_keyMap.put( pool, new Long( poolKey ) );
            m_factoryMap.put( factory, pool );
        }

        return pool;
    }

    /**
     * Return a new key for the pool and controller.
     */
    private final long getKey()
    {
        return m_keyGenerator.nextLong();
    }

    private static final class PoolManagerCommand implements RepeatedCommand
    {
        private final BucketMap m_map;
        private final int m_min = 4;
        private final int m_max = 256;
        private final int m_grow = 4;

        protected PoolManagerCommand( BucketMap map )
        {
            m_map = map;
        }

        public long getDelayInterval()
        {
            return 10 * 1000L;
        }

        public long getRepeatInterval()
        {
            return 10 * 1000L;
        }

        public int getNumberOfRepeats()
        {
            return 0;
        }

        public void execute()
            throws Exception
        {
            Iterator i = m_map.keySet().iterator();

            while( i.hasNext() )
            {
                ManagablePool pool = (ManagablePool)i.next();
                long key = ( (Long)m_map.get( pool ) ).longValue();
                int size = pool.size( key );

                if( size < m_min )
                {
                    pool.grow( m_grow, key );
                }

                if( size > m_max )
                {
                    pool.shrink( m_grow, key );
                }
            }
        }
    }
}
