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

import org.apache.avalon.excalibur.concurrent.Mutex;
import org.apache.avalon.excalibur.collections.Buffer;
import org.apache.avalon.excalibur.collections.FixedSizeBuffer;
import org.apache.avalon.framework.activity.Disposable;

/**
 * This is an <code>Pool</code> that caches Poolable objects for reuse.
 * Please note that this pool offers no resource limiting whatsoever.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/08/08 00:57:25 $
 * @since 4.1
 */
public final class FixedSizePool
    implements Pool, Disposable
{
    private boolean m_disposed = false;
    private final Buffer m_buffer;
    private final ObjectFactory m_factory;
    private final long m_timeout;

    public FixedSizePool( ObjectFactory factory, int size )
        throws Exception
    {
        this( factory, size, 1000L );
    }

    public FixedSizePool( ObjectFactory factory, int size, long timeout )
        throws Exception
    {
        m_timeout = (timeout < 1) ? 1 : timeout;
        m_buffer = new FixedSizeBuffer( size );
        m_factory = factory;

        for( int i = 0; i < size; i++ )
        {
            m_buffer.add( newInstance() );
        }
    }

    public Object acquire()
    {
        if( m_disposed )
        {
            throw new IllegalStateException( "Cannot get an object from a disposed pool" );
        }

        Object object = null;

        long end = System.currentTimeMillis() + m_timeout;

        while ( null == object && System.currentTimeMillis() < end)
        {
            synchronized( m_buffer )
            {
                if ( m_buffer.isEmpty() )
                {
                    try
                    {
                        m_buffer.wait( m_timeout );
                    }
                    catch (Exception e) {}
                }

                object = m_buffer.remove();
            }
        }

        return object;
    }

    public void release( Object object )
    {
        if( m_disposed )
        {
            try
            {
                m_factory.dispose( object );
            }
            catch( Exception e )
            {
                // We should never get here, but ignore the exception if it happens
            }
        }
        else
        {
            synchronized( m_buffer )
            {
                m_buffer.add( object );
                m_buffer.notify();
            }
        }
    }

    public Object newInstance()
        throws Exception
    {
        return m_factory.newInstance();
    }

    public void dispose()
    {
        m_disposed = true;

        synchronized( m_buffer )
        {
            while( !m_buffer.isEmpty() )
            {
                try
                {
                    m_factory.dispose( m_buffer.remove() );
                }
                catch( Exception e )
                {
                    // We should never get here, but ignore the exception if it happens
                }
            }
        }
    }
}

