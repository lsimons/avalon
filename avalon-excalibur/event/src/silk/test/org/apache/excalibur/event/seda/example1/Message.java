/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.seda.example1;

/**
 * A Message to be send around.
 * 
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public abstract class Message
{
    /** The event's message String */
    public final String m_message;

    //------------------------ Message constructors
    /**
     * Constructor for Message that takes a String message.
     * @since Sep 12, 2002
     * 
     * @param message
     *  The message for this message
     */
    public Message(String message)
    {
        super();
        m_message = message;
    }

    //------------------------ overridden methods in Object
    /**
     * @see Object#toString()
     */
    public String toString()
    {
        return m_message;
    }
}
