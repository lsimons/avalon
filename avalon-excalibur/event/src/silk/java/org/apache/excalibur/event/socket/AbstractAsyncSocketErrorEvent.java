/* 
 * Copyright (c) 2000 by Matt Welsh and The Regents of the University of 
 * California. All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose, without fee, and without written agreement is
 * hereby granted, provided that the above copyright notice and the following
 * two paragraphs appear in all copies of this software.
 * 
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
 * OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE UNIVERSITY OF
 * CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
 * ON AN "AS IS" BASIS, AND THE UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO
 * PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 */

/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.socket;

/**
 * This is the base class for all error events passed 
 * up by the Socket library.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public abstract class AbstractAsyncSocketErrorEvent
{ 

    /** The error message for the socket */
    private final String m_message;

    //------------------------- AbstractAsyncSocketErrorEvent constructors
    /**
     * Creates an error event with the specified message.
     * @since May 21, 2002
     * 
     * @param message
     *  The message describing the error.
     */
    protected AbstractAsyncSocketErrorEvent(String message)
    {
        m_message = message;
    }
    
    //------------------------- AbstractAsyncSocketErrorEvent abstract methods
    /**
     * Returns the Socket that caused the error.
     * @since Sep 26, 2002
     * 
     * @return AbstractSocketBase
     *  The Socket that caused the error.
     */
    public abstract AsyncSocketBase getSocket();

    //------------------------- AbstractAsyncSocketErrorEvent specific implementation
    /**
     * Returns the error message of this event.
     * @since May 21, 2002
     * 
     * @return String
     *  The error message of this event.
     */
    public final String getMessage()
    {
        return m_message;
    }

}