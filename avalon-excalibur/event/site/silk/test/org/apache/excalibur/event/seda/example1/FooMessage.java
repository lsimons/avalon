/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.seda.example1;

/**
 * A foo message to be send around.
 * 
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class FooMessage extends Message
{
    boolean m_timed = false;
    
    //------------------------ FooMessage constructors
    /**
     * @see Message#Message(String)
     */
    public FooMessage()
    {
        super("Timed");
        m_timed = true;
    }

    /**
     * @see Message#Message(String)
     */
    public FooMessage(String message)
    {
        super("Foo " + message);
    }
    
    //------------------------ FooMessage specific implementation
    /**
     * Returns whether this message is timed.
     * @since Sep 12, 2002
     * 
     * @return boolean
     *  whether this message is timed.
     */
    public boolean isTimed()
    {
        return m_timed;
    }

}
