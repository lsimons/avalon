/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.event;

/**
 * A Source implements the side of an event queue where QueueElements are
 * dequeued operations only.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public interface Message extends QueueElement
{
    /**
     * Get the attachment associated with this Message.  If there is no
     * attachment, this method will return null.
     */
    Object getAttachment();

    /**
     * Attach an Object to the message.
     */
    void attach( Object attachment );

    /**
     * Clear the attachment.
     */
    void clear();
}