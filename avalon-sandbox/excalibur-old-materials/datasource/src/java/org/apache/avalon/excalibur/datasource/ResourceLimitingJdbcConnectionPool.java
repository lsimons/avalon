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

import java.sql.SQLException;

import org.apache.avalon.excalibur.pool.ObjectFactory;
import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.excalibur.pool.ValidatedResourceLimitingPool;

/**
 * A ResourceLimiting JdbcConnectionPool which allows for fine configuration of
 *  how the pool scales to loads.
 *
 * The pool supports; weak and strong pool size limits, optional blocking gets
 *  when connections are not available, and automatic trimming of unused
 *  connections.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.3 $ $Date: 2003/02/27 15:20:55 $
 * @since 4.1
 */
public class ResourceLimitingJdbcConnectionPool
    extends ValidatedResourceLimitingPool
{
    private boolean m_autoCommit;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new ResourceLimitingJdbcConnectionPool
     *
     * @param factory The ObjectFactory which will be used to create new connections as needed
     *  by the pool.
     * @param max Maximum number of connections which can be stored in the pool, 0 implies
     *  no limit.
     * @param maxStrict true if the pool should never allow more than max connections to be
     *  created.  Will cause an exception to be thrown if more than max connections are
     *  requested and blocking is false.
     * @param blocking true if the pool should cause a thread calling get() to block when
     *  connections are not currently available in the pool.
     * @param blockTimeout The maximum amount of time, in milliseconds, that a call to get() will
     *  block before an exception is thrown.  A value of 0 implies an indefinate wait.
     * @param trimInterval The minimum interval with which old unused connections will be removed
     *  from the pool.  A value of 0 will cause the pool to never trim old connections.
     * @param autoCommit true if connections created by this pool should have autoCommit enabled.
     */
    public ResourceLimitingJdbcConnectionPool( final ObjectFactory factory,
                                               int max,
                                               boolean maxStrict,
                                               boolean blocking,
                                               long blockTimeout,
                                               long trimInterval,
                                               boolean autoCommit )
    {

        super( factory, max, maxStrict, blocking, blockTimeout, trimInterval );

        m_autoCommit = autoCommit;
    }

    /*---------------------------------------------------------------
     * ValidatedResourceLimitingPool Methods
     *-------------------------------------------------------------*/
    /**
     * Create a new poolable instance by by calling the newInstance method
     *  on the pool's ObjectFactory.
     * This is the method to override when you need to enforce creational
     *  policies.
     * This method is only called by threads that have m_semaphore locked.
     */
    protected Poolable newPoolable() throws Exception
    {
        AbstractJdbcConnection conn = (AbstractJdbcConnection)super.newPoolable();

        // Store a reference to this pool in the connection
        conn.setPool( this );

        // Set the auto commit flag for new connections.
        conn.setAutoCommit( m_autoCommit );

        return conn;
    }

    /**
     * Validates the poolable before it is provided to the caller of get on this pool.
     *  This implementation of the validation method always returns true indicating
     *  that the Poolable is valid.
     * The pool is not locked by the current thread when this method is called.
     *
     * @param poolable The Poolable to be validated
     * @return true if the Poolable is valid, false if it should be removed from the pool.
     */
    protected boolean validatePoolable( Poolable poolable )
    {
        AbstractJdbcConnection conn = (AbstractJdbcConnection)poolable;
        try
        {
            // Calling isClosed() may take time if the connection has not been
            //  used for a while.  Is this a problem because the m_semaphore
            //  is currently locked?  I am thinking no because isClosed() will
            //  return immediately when connections are being used frequently.
            if( conn.isClosed() )
            {
                getLogger().debug( "JdbcConnection was closed." );
                return false;
            }
        }
        catch( SQLException e )
        {
            getLogger().debug(
                "Failed to check whether JdbcConnection was closed. " + e.getMessage() );
        }

        return true;
    }
}

