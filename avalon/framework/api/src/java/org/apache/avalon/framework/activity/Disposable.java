/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.activity;

/**
 * The Dispose interface should be implemented by classes that
 * need to dispose of internal resources prior to the destruction
 * of the implementing instance.
 *
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:pier@apache.org">Pierpaolo Fumagalli</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Disposable
{
    /**
     * The dispose operation will be invoked by a client on completion
     * of the useful life of instance.  This method is guaranteed to be called
     * after the stop() method if the Component supports the
     * <code>Stoppable</code> interface. This method is responsible for
     * releaseing/destroying any resources aquired by the Component
     * during it's lifecycle.
     */
    void dispose()
        throws Exception;
}
