/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.atlantis;

/**
 * Null SystemManager.
 *
 * @author <a href="mailto:colus@isoft.co.kr">Eung-ju Park</a>
 */
public class NoopSystemManager
    extends AbstractSystemManager
{
    public void initialize()
        throws Exception
    {
    }

    public void start()
    {
    }

    public void stop()
    {
    }

    public void dispose()
    {
    }

    protected Object export( final String name,
                             final Object object,
                             final Class[] interfaces )
        throws ManagerException
    {
        return object;
    }

    protected void unexport( final String name,
                             final Object exportedObject )
        throws ManagerException
    {
    }

    protected void verifyInterface( final Class clazz )
        throws ManagerException
    {
    }
}
