/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework;

/**
 * Interface which all cascadign throwables should implement.
 * Allows recording of nested exceptions.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version 1.0
 */
public interface CascadingThrowable
{
    /**
     * Returns the root cause of this <code>Throwable</code>.
     *
     * @return a <code>Throwable</code>
     */
    Throwable getCause();
}
