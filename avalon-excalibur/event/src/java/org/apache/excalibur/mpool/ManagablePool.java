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

/**
 * This is the interface for Pools that are not a fixed size.  This interface
 * exposes enough explicit state so that an external asynchronous Controller
 * can do it's job.  A secondary purpose of this interface is to supply a
 * simple authentication mechanism so that the Pool only responds to method
 * invocations by the legitimate controller.
 *
 * <p>
 *   The key is a randomly generated number greater than one assigned by the
 *   PoolManager and given to the Pool and the PoolController.  The mechanism
 *   to generate the number is up to the PoolManager's policy.  Keep in mind
 *   that should the key be made publicly available, the Pool is susceptible
 *   to a replay attack.  Therefore, it is suggested that the key be created
 *   at the same time the Pool is created.
 * </p>
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/08/13 08:15:20 $
 * @since 4.1
 */
public interface ManagablePool extends Pool
{
    /**
     * Grow by the specified amount.  The pool should trust the Controller
     * for the Grow size.
     *
     * @param  amount  an integer amount to increase the pool size by.
     * @param  key     an integer number supplied by the PoolManager to
     *                 validate that the method is called legitimately
     *
     * @throws IllegalAccessException if the key does not match the
     *                                controller's key.
     */
    void grow( int amount, long key )
        throws IllegalAccessException;

    /**
     * Shrink the pool by the specified amount.  The pool should trust the
     * Controller, but be smart enough not to achieve a negative pool size.
     * In other words, you should clip the shrink amount so that the pool
     * does not go below 0.
     *
     * @param  amount  an integer amount to decrease the pool size by.
     * @param  key     an integer number supplied by the PoolManager to
     *                 validate that the method is called legitimately
     *
     * @throws IllegalAccessException if the key does not match the
     *                                controller's key.
     */
    void shrink( int amount, long key )
        throws IllegalAccessException;

    /**
     * Determine the pool's current size.  The size is defined as the number
     * of Poolable objects in reserve.
     *
     * @param  key     an integer number supplied by the PoolManager to
     *                 validate that the method is called legitimately
     *
     * @return  size of pool's reserve.
     *
     * @throws IllegalAccessException if the key does not match the
     *                                controller's key.
     */
    int size( long key )
        throws IllegalAccessException;
}
