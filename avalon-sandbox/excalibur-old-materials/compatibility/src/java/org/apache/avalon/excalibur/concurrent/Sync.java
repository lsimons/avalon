/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.concurrent;

/**
 * The interface to synchronization objects.
 *
 * @deprecated use EDU.oswego.cs.dl.util.concurrent.Sync instead
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version CVS $Revision: 1.3 $ $Date: 2003/03/22 12:31:43 $
 * @since 4.0
 */
public interface Sync
{
    /**
     * Aquire access to resource.
     * This method will block until resource aquired.
     *
     * @throws InterruptedException if an error occurs
     */
    void acquire()
        throws InterruptedException;

    /**
     * Aquire access to resource.
     * This method will block for a maximum of msec.
     *
     * @param msec the duration to wait for lock to be released
     * @return true if lock aquired, false on timeout
     * @throws InterruptedException if an error occurs
     */
    boolean attempt( long msec )
        throws InterruptedException;

    /**
     * Release lock.
     */
    void release();
}
