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
package org.apache.excalibur.event;

/**
 * A Source implements the side of an event queue where QueueElements are
 * dequeued operations only.
 *
 * <p>
 *   The interface design is heavily influenced by
 *   <a href="mailto:mdw@cs.berkeley.edu">Matt Welsh</a>'s SandStorm server,
 *   his demonstration of the SEDA architecture.  We have deviated where we
 *   felt the design differences where better.
 * </p>
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public interface Queue extends Source, Sink
{
    String ROLE = Queue.class.getName();

    /**
     * Set the enqueue predicate for this sink. This mechanism
     * allows user to define a method that will 'screen'
     * QueueElementIF's during the enqueue procedure to either
     * accept or reject them. The enqueue predicate runs in the
     * context of the caller of {@link #enqueue(Object)},
     * which means it must be simple and fast. This can be used
     * to implement many interesting m_sink-thresholding policies,
     * such as simple count threshold, credit-based mechanisms,
     * and more.
     * @since Feb 10, 2003
     *
     * @param enqueuePredicate
     *  the enqueue predicate for this sink
     */
    public void setEnqueuePredicate(EnqueuePredicate enqueuePredicate);

    /**
     * Return the enqueue predicate for this sink.
     * @since Feb 10, 2003
     *
     * @return {@link EnqueuePredicate}
     *  the enqueue predicate for this sink.
     */
    public EnqueuePredicate getEnqueuePredicate();

    /**
     * Set the dequeue executable for this sink. This mechanism
     * allows users to define a methods that will be executed
     * before or after dequeuing elements from a source
     * @since Feb 10, 2003
     *
     * @param executable
     *  The dequeue executable for this sink.
     */
    public void setDequeueInterceptor(DequeueInterceptor executable);

    /**
     * Return the dequeue executable for this sink.
     * @since Feb 10, 2003
     *
     * @return {@link DequeueInterceptor}
     *  The dequeue executable for this sink.
     */
    public DequeueInterceptor getDequeueInterceptor();
}
