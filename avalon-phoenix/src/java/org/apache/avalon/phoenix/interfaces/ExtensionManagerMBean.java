/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.interfaces;

/**
 * Management interface to ExtensionManager.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2001/12/02 04:12:51 $
 */
public interface ExtensionManagerMBean
{
    String ROLE = "org.apache.avalon.phoenix.interfaces.ExtensionManagerMBean";

    /**
     * Retrieve an array of paths where each
     * element in array represents a directory
     * in which the ExtensionManager will look 
     * for Extensions.
     *
     * @return the list of paths to search in
     */
    String[] getPaths();

    /**
     * Force the ExtensionManager to rescan the paths
     * to discover new Extensions that have been added
     * or remove old Extensions that have been removed.
     *
     */
    void rescanPath();

    //Extension[] getExtension();
}
