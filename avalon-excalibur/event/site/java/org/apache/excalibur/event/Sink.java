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
 * A Sink implements the end of a finite-length event queue where
 * elements are enqueued. These operations can throw a
 * <code>SinkException</code> if the sink is closed or becomes full, allowing
 * event queues to support thresholding and backpressure.
 *
 * <p>
 *   The interface design is heavily influenced by
 *   <a href="mailto:mdw@cs.berkeley.edu">Matt Welsh</a>'s SandStorm server,
 *   his demonstration of the SEDA architecture.  We have deviated where we
 *   felt the design differences where better.
 * </p>
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public interface Sink
{
    /**
     * Enqueues the given element onto the queue.
     *
     * @param element  The elements to enqueue
     * @throws SinkFullException Indicates that the sink is temporarily full.
     * @throws SinkClosedException Indicates that the sink is
     *         no longer being serviced.
     */
    void enqueue( Object element )
        throws SinkException;

    /**
     * Given an array of elements, atomically enqueues all of the elements
     * in the array. This guarantees that no other thread can interleave its
     * own elements with those being inserted from this array. The
     * implementation must enqueue all of the elements or none of them;
     * if a SinkFullException or SinkClosedException is thrown, none of
     * the elements will have been enqueued.
     *
     * @param elements The element array to enqueue
     * @throws SinkFullException Indicates that the sink is temporarily full.
     * @throws SinkClosedException Indicates that the sink is
     *   no longer being serviced.
     *
     */
    void enqueue( Object[] elements )
        throws SinkException;

    /**
     * Tries to enqueue an event, but instead of throwing exceptions, it
     * returns a boolean value of whether the attempt was successful.
     *
     * @param element The element to attempt to enqueue
     * @return <code>true</code> if successful, <code>false</code> if not.
     */
    boolean tryEnqueue( Object element );

    /**
     * Support for transactional enqueue.
     *
     * <p>This method allows a client to provisionally enqueue a number
     * of elements onto the queue, and then later commit the enqueue (with
     * a <code>commitEnqueue</code> call), or abort (with an
     * <code>abortEnqueue</code> call). This mechanism can be used to
     * perform "split-phase" enqueues, where a client first enqueues a
     * set of elements on the queue and then performs some work to "fill in"
     * those elements before performing a commit. This can also be used
     * to perform multi-queue transactional enqueue operations, with an
     * "all-or-nothing" strategy for enqueueing events on multiple queues.
     * </p>
     *
     * <p>This method would generally be used in the following manner:</p>
     * <pre>
     *   PreparedEnqueue enqueue = sink.prepareEnqueue(someElements);
     *   if (canCommit) {
     *     enqueue.commit();
     *   } else {
     *     enqueue.abort();
     *   }
     * </pre>
     *
     * <p> Note that this method does <strong>not</strong> protect against
     * "dangling prepares" -- that is, a prepare without an associated
     * commit or abort operation. This method should be used with care.
     * In particular, be sure that all code paths (such as exceptions)
     * after a prepare include either a commit or an abort.</p>
     *
     * @param elements The element array to provisionally enqueue
     * @return A <code>PreparedEnqueue</code> that may be used to commit or
     *         abort the provisional enqueue
     * @throws SinkFullException Indicates that the sink is
     *            temporarily full and that the requested elements could not
     *            be provisionally enqueued.
     * @throws SinkClosedException Indicates that the sink is
     *            no longer being serviced.
     *
     * @see PreparedEnqueue
     */
    PreparedEnqueue prepareEnqueue( Object[] elements )
        throws SinkException;

    /**
     * Returns the length threshold of the sink. This is for informational
     * purposes only; an implementation may allow more (or fewer) new
     * entries to be enqueued than maxSize() - size(). This may be the
     * case, for example, if the sink implements some form of dynamic
     * thresholding, and does not always accurately report maxSize().
     *
     * @return -1 if the sink has no length threshold.
     */
    int maxSize();

    /**
     * Returns true if this sink has reached its threshold; false otherwise.
     * Like maxSize(), this is also informational, and isFull() returning
     * false does not guarantee that future enqueue operations will succeed.
     * Clearly, isFull() returning true does not guarantee that they will
     * fail, since the queue may be serviced in the meantime.
     *
     * @return true if the Sink is full
     */
    boolean isFull();

    /**
     * Returns the number of elements it can currently accept.  This is
     * typically the difference between <code>size()</code> and
     * <code>maxSize()</code>.  It will return -1 if the sink is unbounded.
     *
     * @return the number of elements the Sink can accept
     */
    int canAccept();
}
