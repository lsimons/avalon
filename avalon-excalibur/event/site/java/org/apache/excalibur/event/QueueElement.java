/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.event;

/**
 * A Source implements the side of an event queue where QueueElements are
 * dequeued operations only.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public interface QueueElement
{
    /**
     * Get the type id of the QueueElement.  This allows simplified processing
     * without alot of explicit casting.  In order to manage the types allowed
     * for a subsystem, each subsystem should declare an interface that lists
     * all the types available with constant names.  Another rule of thumb is
     * to map all the signals into a type of address space.  For instance, the
     * code snippet below will help:
     *
     * <pre>
     *   public interface AsyncFileTypes
     *   {
     *       long ADDRESS = $af100000;
     *       long FILE_OPEN = ADDRESS + 1;
     *       long FILE_CLOSE = FILE_OPEN + 1;
     *   }
     * </pre>
     */
    long getType();

    /**
     * Get a handle to an attached object.
     */
    Object getAttachment();

    /**
     * Attach an object to the element.
     */
    void attach( Object attachment );
}