/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.engine.facilities;

import org.apache.avalon.framework.atlantis.Facility;
import org.apache.avalon.framework.atlantis.ManagerException;
import org.apache.avalon.phoenix.Block;

/**
 * This facility is responsible for managing a particular application.
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface ApplicationManager
    extends Facility
{
    String ROLE = "org.apache.avalon.phoenix.engine.facilities.ApplicationManager";

    /**
     * Register a block for management.
     * The block is exported through some management scheme
     * (typically JMX) and the management is restricted
     * to the interfaces passed in as a parameter to method.
     *
     * @param name the name to register block under
     * @param block the block
     * @param interfaces the interfaces to register the component under
     * @exception ManagerException if an error occurs. An error could occur if the block doesn't
     *            implement the interfaces, the interfaces parameter contain non-instance
     *            classes, the name is already registered etc.
     * @exception IllegalArgumentException if block or interfaces is null
     */
    void register( String name, Block block, Class[] interfaces )
        throws ManagerException, IllegalArgumentException;

    /**
     * Unregister named block.
     *
     * @param name the name of block to unregister
     * @exception ManagerException if an error occurs such as when no such block registered.
     */
    void unregister( String name )
        throws ManagerException;
}
