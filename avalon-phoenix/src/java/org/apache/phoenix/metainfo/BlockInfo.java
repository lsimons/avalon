/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.metainfo;

import org.apache.avalon.camelot.Info;

/**
 * This descrbes information about the block that is used by administration tools and kernel.
 * 
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface BlockInfo 
    extends Info
{
    /**
     * Return meta information that is generallly only required by administration tools.
     *
     * It should be loaded on demand and not always present in memory.
     *
     * @return the BlockDescriptor
     */
    BlockDescriptor getBlockDescriptor();
    
    /**
     * This returns a list of Services that this block exports.
     *
     * @return an array of Services (can be null)
     */
    ServiceDescriptor[] getServices();

    /**
     * Return an array of Service dependencies that this Block depends upon.
     *
     * @return an array of Service dependencies (may be null) 
     */
    DependencyDescriptor[] getDependencies();

    /**
     * Retrieve a dependency with a particular role.
     *
     * @param role the role
     * @return the dependency or null if it does not exist
     */
    DependencyDescriptor getDependency( String role );
}
