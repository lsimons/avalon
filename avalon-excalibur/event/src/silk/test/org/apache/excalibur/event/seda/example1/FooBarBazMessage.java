/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.seda.example1;

/**
 * A Foo Bar Baz message to be send around.
 * 
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class FooBarBazMessage extends FooBarMessage
{
    //------------------------ FooBarMessage constructors
    /**
     * @see Message#Message(String)
     */
    public FooBarBazMessage(String message)
    {
        super("'s derived FooBarBaz message " + message);
    }
}
