/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.event;

/**
 * A Source implements the end of a finite-length event queue where QueueElements
 * are enqueued. These operations can throw a SourceException if the sink is
 * closed or becomes full, allowing event queues to support thresholding and
 * backpressure.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public interface EventDroppingSource extends Source {

  /**
   * Enqueues the given element onto the queue.
   *
   * This is lossy in that this method drops the element if the element 
   * could not be enqueued, rather than throwing a SourceFullException or 
   * SourceClosedException. This is meant as a convenience interface for 
   * "low priority" enqueue events which can be safely dropped. 
   *
   * @param element  The <code>QueueElementIF</code> to enqueue
   * @return true if the element was enqueued, false otherwise. 
   * 
   */
  public boolean enqueue(QueueElement element);

  /**
   * Enqueues the given element onto the queue.
   *
   * This is lossy in that this method drops the element if the element 
   * could not be enqueued, rather than throwing a SourceFullException or 
   * SourceClosedException. This is meant as a convenience interface for 
   * "low priority" enqueue events which can be safely dropped. 
   *
   * @param element  The <code>QueueElementIF</code> to enqueue
   * @return true if the element was enqueued, false otherwise. 
   * 
   */
  public boolean enqueue(QueueElement[] elements);
}
