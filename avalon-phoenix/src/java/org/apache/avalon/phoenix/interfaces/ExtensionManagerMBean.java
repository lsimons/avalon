/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.interfaces;

import java.io.File;

/**
 * Management interface to ExtensionManager.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.4 $ $Date: 2002/07/14 01:36:30 $
 */
public interface ExtensionManagerMBean
{
    String ROLE = ExtensionManagerMBean.class.getName();

    /**
     * Retrieve an array of paths where each
     * element in array represents a directory
     * in which the ExtensionManager will look
     * for Extensions.
     *
     * @return the list of paths to search in
     */
    File[] getPaths();

    /**
     * Force the ExtensionManager to rescan the paths
     * to discover new Extensions that have been added
     * or remove old Extensions that have been removed.
     *
     */
    void rescanPath();

    //Extension[] getExtension();
}
