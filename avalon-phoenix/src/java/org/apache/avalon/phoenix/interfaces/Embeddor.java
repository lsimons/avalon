/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.interfaces;

import org.apache.avalon.framework.activity.Executable;

/**
 * This is the object that is interacted with to create, manage and
 * dispose of the kernel and related resources.
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 * @author <a href="peter at apache.org">Peter Donald</a>
 */
public interface Embeddor
    extends Executable
{
    String ROLE = Embeddor.class.getName();

    /**
     * Request the Embeddor shutsdown.
     */
    void shutdown();
}
