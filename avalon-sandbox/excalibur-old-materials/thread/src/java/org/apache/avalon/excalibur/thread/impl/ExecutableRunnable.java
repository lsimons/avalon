/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.thread.impl;

import org.apache.avalon.framework.activity.Executable;

/**
 * Class to adapt a <code>Runnable</code> object in an <code>Executable</code> object.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
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
        m_runnable = runnable;

        ///Verify runnable is not null
        if( null == runnable )
        {
            throw new NullPointerException( "runnable property is null" );
        }
    }

    /**
     * Execute the underlying <code>Runnable</code> object.
     *
     * @exception Exception if an error occurs
     */
    public void execute()
        throws Exception
    {
        m_runnable.run();
    }
}
