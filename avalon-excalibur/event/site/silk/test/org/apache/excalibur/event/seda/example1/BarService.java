/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.seda.example1;

/**
 * Bar Service interface for components processing bar messages
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public interface BarService
{
    String ROLE = BarService.class.getName();
    
    /**
     * This method processes a bar message element.
     * @since Sep 13, 2002
     * 
     * @param message
     *  A bar message element. to be processed.
     * @throws BarException
     *  If an exception occurrs
     */
    void process(BarMessage message) throws BarException;
}
