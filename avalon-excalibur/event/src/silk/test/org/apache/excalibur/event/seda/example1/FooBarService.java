/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.seda.example1;

/**
 * Foo Bar Service interface for components reporting foo bar messages
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public interface FooBarService
{
    String ROLE = FooBarService.class.getName();
    
    /**
     * This method processes a foobar message element.
     * @since Sep 13, 2002
     * 
     * @param message
     *  A foobar message element to be reported.
     * @throws BarException
     *  If an exception occurrs
     */
    void report(FooBarMessage message) throws FooBarException;
}
