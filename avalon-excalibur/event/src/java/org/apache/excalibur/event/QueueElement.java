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
     *       long FILE_OPEN = TypeUtil.makeId(AsyncFileOpen.getClass().getName());
     *       long FILE_CLOSE = TypeUtil.makeId(AsyncFileClose.getClass().getName());
     *   }
     * </pre>
     *
     * <p>
     *   <strong>Important:</strong> The ID has to be unique for each class.
     *   That means that if the fully qualified class name is different, then
     *   the id's need to be different.  It also means that you must not use
     *   multiple id's for the same class.
     * </p>
     */
    long getType();
}