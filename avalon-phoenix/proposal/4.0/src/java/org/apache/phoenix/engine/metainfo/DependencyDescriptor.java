/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.metainfo;

/**
 * This interface describes a dependency of Block.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface DependencyDescriptor
    extends Descriptor
{
    /**
     * Return name of dependency.
     *
     * The name is what is used by block implementor to aquire dependency in ComponentManager.
     *
     * @return the name of the dependency
     */
    String getRole();

    /**
     * Return Service dependency provides.
     *
     * @return the service dependency provides
     */
    ServiceDescriptor getService();
}
