/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.activity;

/**
 * This interface is used when you have a <code>Component</code> that
 * must be run for the time of it's existence.  Note, these methods
 * are not to be confused with the <code>java.lang.Thread</code>
 * methods.
 *
 * It provides a method through which components can be "started"
 * and "stopped" without requiring a thread. Useful for reactive or
 * passive objects.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public interface Startable
{
    /**
     * Starts the component.
     */
    void start()
        throws Exception;

    /**
     * Stops the component.
     */
    void stop()
        throws Exception;
}
