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

/**
 * This is an <code>Pool</code> that caches Poolable objects for reuse.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 14:44:01 $
 * @since 4.0
 */
public class SoftResourceLimitingPool
    extends DefaultPool
    implements Resizable
{
    /**
     * Create an SoftResourceLimitingPool.  The pool requires a factory.
     */
    public SoftResourceLimitingPool( final ObjectFactory factory )
        throws Exception
    {
        this( factory, AbstractPool.DEFAULT_POOL_SIZE / 2 );
    }

    /**
     * Create an SoftResourceLimitingPool.  The pool requires a factory,
     * and can optionally have a controller.
     */
    public SoftResourceLimitingPool( final ObjectFactory factory,
                                     final int min )
        throws Exception
    {
        this( factory, null, min, min * 2 );
    }

    /**
     * Create an SoftResourceLimitingPool.  The pool requires a factory,
     * and can optionally have a controller.
     */
    public SoftResourceLimitingPool( final ObjectFactory factory,
                                     final int min,
                                     final int max ) throws Exception
    {
        this( factory, null, min, max );
    }

    /**
     * Create an SoftResourceLimitingPool.  The pool requires a factory,
     * and can optionally have a controller.
     */
    public SoftResourceLimitingPool( final ObjectFactory factory,
                                     final PoolController controller,
                                     final int min,
                                     final int max )
        throws Exception
    {
        super( factory, controller, min, max );
    }

    public SoftResourceLimitingPool( final Class clazz, final int initial, final int maximum )
        throws NoSuchMethodException, Exception
    {
        this( new DefaultObjectFactory( clazz ), initial, maximum );
    }

    public SoftResourceLimitingPool( final Class clazz, final int initial )
        throws NoSuchMethodException, Exception
    {
        this( clazz, initial, initial );
    }

    public void initialize()
        throws Exception
    {
        this.grow( this.m_min );

        this.m_initialized = true;
    }

    public void grow( final int amount )
    {
        try
        {
            m_mutex.acquire();

            this.internalGrow( amount );
        }
        catch( final InterruptedException ie )
        {
            if( getLogger().isWarnEnabled() )
            {
                getLogger().warn( "Interrupted while waiting on lock", ie );
            }
        }
        catch( final Exception e )
        {
            if( getLogger().isWarnEnabled() )
            {
                getLogger().warn( "Could not grow the pool properly, an exception was caught", e );
            }
        }
        finally
        {
            m_mutex.release();
        }
    }

    public void shrink( final int amount )
    {
        try
        {
            m_mutex.acquire();

            this.internalShrink( amount );
        }
        catch( final InterruptedException ie )
        {
            if( getLogger().isWarnEnabled() )
            {
                getLogger().warn( "Interrupted while waiting on lock", ie );
            }
        }
        catch( final Exception e )
        {
            if( getLogger().isWarnEnabled() )
            {
                getLogger().warn( "Could not shrink the pool properly, an exception was caught", e );
            }
        }
        finally
        {
            m_mutex.release();
        }
    }
}
