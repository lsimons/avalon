/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework.activity;

/**
 * The Startable interface is used when components need to
 * be "running" to be active. It provides a method through
 * which components can be "started" and "stopped" without
 * requiring a thread.
 * Note that these methods should start the component but return
 * imediately.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public interface Startable
{
    /**
     * Starts the component.
     *
     * @throws Exception if Component can not be started
     */
    void start()
        throws Exception;

    /**
     * Stops the component.
     *
     * @throws Exception if the Component can not be Stopped.
     */
    void stop()
        throws Exception;
}
