/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.seda;

import org.apache.avalon.framework.CascadingRuntimeException;

/**
 * This exception is thrown if a sink as requested by a call to
 * {@link org.apache.excalibur.event.seda.event.seda.SinkMap#getSink(String)} 
 * does not exist.
 *
 * @see Stage
 * 
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class NoSuchSinkException extends CascadingRuntimeException
{

    //---------------------- NoSuchSinkException constructors
    /**
     * @see CascadingException#CascadingException(String)
     */
    public NoSuchSinkException(String message)
    {
        super(message, null);
    }

    /**
     * @see CascadingException#CascadingException(String, Throwable)
     */
    public NoSuchSinkException(String message, Throwable throwable)
    {
        super(message, throwable);
    }

}