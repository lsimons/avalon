/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.seda.example1;

import org.apache.avalon.framework.CascadingException;

/** 
 * Thrown by the foo stage in case of an error.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class FooException extends CascadingException
{
    //------------------------- FooException constructors
    /**
     * @see CascadingException#CascadingException(String)
     */
    public FooException(String message)
    {
        super(message);
    }

    /**
     * @see CascadingException#CascadingException(String, Throwable)
     */
    public FooException(String message, Throwable throwable)
    {
        super(message, throwable);
    }

}
