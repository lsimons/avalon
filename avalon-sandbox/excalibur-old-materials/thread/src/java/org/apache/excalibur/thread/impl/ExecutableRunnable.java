/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.thread.impl;

import org.apache.excalibur.thread.Executable;

/**
 * Class to adapt a {@link Runnable} object in
 * an {@link Executable} object.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
final class ExecutableRunnable
    implements Executable
{
    ///The runnable instance being wrapped
    private Runnable m_runnable;

    /**
     * Create adapter using specified runnable.
     *
     * @param runnable the runnable to adapt to
     */
    protected ExecutableRunnable( final Runnable runnable )
    {
        if( null == runnable )
        {
            throw new NullPointerException( "runnable" );
        }
        m_runnable = runnable;
    }

    /**
     * Execute the underlying {@link Runnable} object.
     *
     * @throws Exception if an error occurs
     */
    public void execute()
        throws Exception
    {
        m_runnable.run();
    }
}
