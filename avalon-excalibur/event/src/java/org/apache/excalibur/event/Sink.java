/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.event;

/**
 * A Sink implements the end of a finite-length event queue where QueueElements
 * are enqueued. These operations can throw a SinkException if the sink is
 * closed or becomes full, allowing event queues to support thresholding and
 * backpressure.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public interface Sink {

  /**
   * Enqueues the given element onto the queue.
   *
   * @param element  The <code>QueueElementIF</code> to enqueue
   * @exception SinkFullException Indicates that the sink is temporarily full.
   * @exception SinkClosedException Indicates that the sink is
   *   no longer being serviced.
   */
  public void enqueue(QueueElement element)
      throws Exception;

  /**
   * Given an array of elements, atomically enqueues all of the elements
   * in the array. This guarantees that no other thread can interleave its
   * own elements with those being inserted from this array. The
   * implementation must enqueue all of the elements or none of them;
   * if a SinkFullException or SinkClosedException is thrown, none of
   * the elements will have been enqueued.
   *
   * @param elements The element array to enqueue
   * @exception SinkFullException Indicates that the sink is temporarily full.
   * @exception SinkClosedException Indicates that the sink is
   *   no longer being serviced.
   *
   */
  public void enqueue(QueueElement[] elements)
      throws Exception;

  /**
   * Returns the number of elements waiting in this queue.
   */
  public int size();


  /**
   * Returns the length threshold of the sink. This is for informational
   * purposes only; an implementation may allow more (or fewer) new
   * entries to be enqueued than maxSize() - size(). This may be the
   * case, for example, if the sink implements some form of dynamic
   * thresholding, and does not always accurately report maxSize().
   *
   * @return -1 if the sink has no length threshold.
   */
  public int maxSize();

  /**
   * Returns true if this sink has reached its threshold; false otherwise.
   * Like maxSize(), this is also informational, and isFull() returning
   * false does not guarantee that future enqueue operations will succeed.
   * Clearly, isFull() returning true does not guarantee that they will
   * fail, since the queue may be serviced in the meantime.
   */
  public boolean isFull();

  /**
   * Returns the number of QueueElements it can currently accept.  This is
   * typically the difference between <code>size()</code> and
   * <code>maxSize()</code>.  It will return -1 if the queue is unbounded.
   */
  public int canAccept();

}