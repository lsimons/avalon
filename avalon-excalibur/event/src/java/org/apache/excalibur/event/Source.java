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
package org.apache.excalibur.event;

/**
 * A Source implements the side of an event queue where QueueElements are
 * dequeued operations only.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public interface Source
{
    /**
     * Sets the timeout on a blocking Source.  Values above <code>1</code>
     * will force all <code>dequeue</code> operations to block for up to that
     * number of milliseconds waiting for new elements.  Values below
     * <code>1</code> will turn off blocking for Source.  This is intentional
     * because a Source should never block indefinitely.
     *
     * @param  Number of milliseconds to block
     */
    void setTimeout( long millis );

    /**
     * Dequeues the next element, or <code>null</code> if there is
     * nothing left on the queue or in case of a timeout while
     * attempting to obtain the mutex
     *
     * @return the next queue element on the queue
     */
    Object dequeue();

    /**
     * Dequeues all available elements. Returns a zero-sized array in
     * case of a timeout while attempting to obtain the mutex or if
     * there is nothing left on the queue.
     *
     * @return all pending queue elements on the queue
     */
    Object[] dequeueAll();

    /**
     * Dequeues at most <code>num</code> available elements. Returns a
     * zero-sized array in case of a timeout while attempting to
     * obtain the mutex or if there is nothing left on the queue.
     *
     * @return At most <code>num</code> <code>QueueElement</code>s from the
     *         queue
     */
    Object[] dequeue( int num );

    /**
     * Returns the number of elements waiting in this queue.
     */
    int size();

}
