/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.thread.impl;

import org.apache.excalibur.thread.Executable;

/**
 * Class to adapt a {@link org.apache.avalon.framework.activity.Executable} object in
 * an {@link Executable} object.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
final class ExecutableExecuteable
    implements Executable
{
    ///The runnable instance being wrapped
    private org.apache.avalon.framework.activity.Executable m_executable;

    /**
     * Create adapter using specified executable.
     *
     * @param executable the executable to adapt to
     */
    protected ExecutableExecuteable( final org.apache.avalon.framework.activity.Executable executable )
    {
        if( null == executable )
        {
            throw new NullPointerException( "executable" );
        }
        m_executable = executable;
    }

    /**
     * Execute the underlying
     * {@link org.apache.avalon.framework.activity.Executable} object.
     *
     * @throws Exception if an error occurs
     */
    public void execute()
        throws Exception
    {
        m_executable.execute();
    }
}
