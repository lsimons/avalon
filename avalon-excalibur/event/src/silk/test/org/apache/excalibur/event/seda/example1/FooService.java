/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.seda.example1;

/**
 * Foo Service interface for components processing foo messages
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public interface FooService
{
    String ROLE = FooService.class.getName();

    /**
     * This method processes an array of foo 
     * message elements.
     * @since Sep 13, 2002
     * 
     * @param messages
     *  An array of foo message elements to be processed.
     * @throws FooException
     *  If an exception occurrs
     */
    void process(FooMessage[] messages) throws FooException;
    
    /**
     * Processes a bar exception thrown when processing bar
     * messages.
     * @since Sep 13, 2002
     * 
     * @param exception
     *  the bar exception thrown when processing bar messages
     * @throws FooException
     *  If an exception occurrs processing the exception
     */
    void handle(BarException exception) throws FooException;
}
