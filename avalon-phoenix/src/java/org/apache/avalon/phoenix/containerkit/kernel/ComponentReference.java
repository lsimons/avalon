/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.containerkit.kernel;

import org.apache.avalon.framework.info.ComponentInfo;

/**
 * This interface defines a resource returned from the
 * {@link ComponentDirectory}.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003/01/18 16:43:43 $
 */
public interface ComponentReference
{
    /**
     * Return the underlying object representing the Component.
     *
     * @return the underlying object representing the Component
     */
    Object getComponent();

    /**
     * @todo Determine if this is reallly needed. It is only
     * returned as  a result of Directory lookup, hence should
     * know the type.
     * @todo Determine if this could be replace by getInfo()
     */
    ComponentInfo getInfo();

    /**
     * Invalidate the component reference. After the reference has been
     * invalidated, any calls made on the Compnent will have undefined
     * results.
     */
    void invalidate();
}
