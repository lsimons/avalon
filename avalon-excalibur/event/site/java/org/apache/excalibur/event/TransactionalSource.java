/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.event;

/**
 * A Source implements the end of a finite-length event queue where
 * QueueElements are enqueued. These operations can throw a SourceException
 * if the sink is closed or becomes full, allowing event queues to support
 * thresholding and backpressure.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public interface TransactionalSource extends Source {

  /**
   * Support for transactional enqueue.
   *
   * <p>This method allows a client to provisionally enqueue a number 
   * of elements onto the queue, and then later commit the enqueue (with
   * a <pre>commitEnqueue</code> call), or abort (with an 
   * <code>abortEnqueue</code> call). This mechanism can be used to 
   * perform "split-phase" enqueues, where a client first enqueues a 
   * set of elements on the queue and then performs some work to "fill in"
   * those elements before performing a commit. This can also be used
   * to perform multi-queue transactional enqueue operations, with an
   * "all-or-nothing" strategy for enqueueing events on multiple queues.</p>
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
   * @return A <code>PreparedEnqueue</code that may be used to commit or abort
   *         the provisional enqueue
   * @exception SourceFullException Indicates that the sink is temporarily full
   *            and that the requested elements could not be provisionally
   *            enqueued.
   * @exception SourceClosedException Indicates that the sink is 
   *            no longer being serviced.
   *
   * @see PreparedEnqueue
   */
  public PreparedEnqueue prepareEnqueue(QueueElementIF[] elements)
      throws SourceException;

}
