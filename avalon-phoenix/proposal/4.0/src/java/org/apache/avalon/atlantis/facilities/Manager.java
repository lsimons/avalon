/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.atlantis.facilities;

import javax.management.MBeanServer;

import org.apache.framework.lifecycle.Executable;
import org.apache.framework.context.Context;
import org.apache.framework.context.Contextualizable;
import org.apache.framework.context.ContextException;

/**
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 */
public interface Manager extends Facility, Executable, Contextualizable
    // and thus Component
{
    /**
     * At a minimum, the <code>Context</code> for Manager
     * should contain:<br />
     * <ul>
     * <li>"javax.management.MBeanServer" containing a reference to an MBeanServer;</li>
     * <li>"org.apache.framework.atlantis.core.Embeddor" containing a reference to an Embeddor;</li>
     * <li>"org.apache.framework.atlantis.core.Kernel" containing a reference to a Kernel;</li>
     * <li>"org.apache.avalon.camelot.Deployer" containing a reference to a Deployer;</li>
     * </ul>
     */
    public void contextualize( Context context ) throws ContextException;
}