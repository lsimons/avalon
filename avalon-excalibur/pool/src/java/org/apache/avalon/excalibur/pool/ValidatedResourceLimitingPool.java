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
 * A ResourceLimitingPool which validates reused poolables before they are
 *  returned with a call get().
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 14:44:01 $
 * @since 4.1
 */
public class ValidatedResourceLimitingPool
    extends ResourceLimitingPool
{
    /*---------------------------------------------------------------
     * Private Fields
     *-------------------------------------------------------------*/
    /**
     * Used for communication between the get() and newPoolable() methods, only valid
     *  within a single synchronized block.
     */
    private boolean m_needsValidation;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new ValidatedResourceLimitingPool
     *
     * @param factory The ObjectFactory which will be used to create new Poolables as needed by
     *  the pool.
     * @param max Maximum number of Poolables which can be stored in the pool, 0 implies no limit.
     * @param maxStrict true if the pool should never allow more than max Poolable to be created.
     *  Will cause an exception to be thrown if more than max Poolables are requested and blocking
     *  is false.
     * @param blocking true if the pool should cause a thread calling get() to block when Poolables
     *  are not currently available on the pool.
     * @param blockTimeout The maximum amount of time, in milliseconds, that a call to get() will
     *  block before an exception is thrown.  A value of 0 implies an indefinate wait.
     * @param trimInterval The minimum interval with which old unused poolables will be removed
     *  from the pool.  A value of 0 will cause the pool to never trim poolables.
     */
    public ValidatedResourceLimitingPool( final ObjectFactory factory,
                                          int max,
                                          boolean maxStrict,
                                          boolean blocking,
                                          long blockTimeout,
                                          long trimInterval )
    {

        super( factory, max, maxStrict, blocking, blockTimeout, trimInterval );
    }

    /*---------------------------------------------------------------
     * Pool Methods
     *-------------------------------------------------------------*/
    /**
     * Gets a Poolable from the pool.  If there is room in the pool, a new Poolable will be
     *  created.  Depending on the parameters to the constructor, the method may block or throw
     *  an exception if a Poolable is not available on the pool.
     *
     * @return Always returns a Poolable.  Contract requires that put must always be called with
     *  the Poolable returned.
     * @throws Exception An exception may be thrown as described above or if there is an exception
     *  thrown by the ObjectFactory's newInstance() method.
     */
    public Poolable get() throws Exception
    {
        Poolable poolable;
        boolean needsValidation;

        // If an obtained Poolable is invalid, then we will want to obtain another one requiring
        //  that we loop.
        do
        {
            synchronized( m_semaphore )
            {
                // Set the needs validation flag to false.  The super.get() method will call the
                //  newPoolable() method causing the flag to be set to false if called.
                m_needsValidation = true;

                poolable = super.get();

                // Store the validation flag in a local variable so that we can continue to use it
                //  after we release the semaphore.
                needsValidation = m_needsValidation;
            }

            // If necessay, validate the poolable now that this thread owns it.
            if( needsValidation )
            {
                // Call the validation method for the obtained poolable.
                if( !validatePoolable( poolable ) )
                {
                    // The poolable is no longer valid.  We need to resynchronize to remove the bad
                    //  poolable and prepare to get another one.
                    synchronized( m_semaphore )
                    {
                        if( getLogger().isDebugEnabled() )
                        {
                            getLogger().debug( "Removing a " + poolable.getClass().getName()
                                               + " from the pool because it failed validation." );
                        }

                        permanentlyRemovePoolable( poolable );
                        poolable = null;
                    }
                }
            }
        } while( poolable == null );

        return poolable;
    }

    /*---------------------------------------------------------------
     * ResourceLimitingPool Methods
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
        // Set the validation flag to false.  See the exclamation in the get() method.
        m_needsValidation = false;

        return super.newPoolable();
    }

    /*---------------------------------------------------------------
     * Public Methods
     *-------------------------------------------------------------*/
    /**
     * If the poolable implements Validatable, then its validate() method will be called to give
     *  the poolable a chance to validate itself.
     * Different functionality can be achieved by overriding this method.
     * This method is only called by threads that have m_semaphore locked.
     *
     * @param poolable The Poolable to be validated
     * @return true if the Poolable is valid, false if it should be removed from the pool.
     */
    protected boolean validatePoolable( Poolable poolable ) throws Exception
    {
        if( poolable instanceof Validatable )
        {
            return ( (Validatable)poolable ).validate();
        }
        else
        {
            return true;
        }
    }
}

