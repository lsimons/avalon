/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.context;

/**
 * This interface is used to indicate objects that need to be
 * resolved in some particular context.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Resolvable
{
    /**
     * Resolve a object to a value.
     *
     * @param context the contextwith respect which to resolve
     * @return the resolved object
     */
    Object resolve( Context context )
        throws ContextException;
}
