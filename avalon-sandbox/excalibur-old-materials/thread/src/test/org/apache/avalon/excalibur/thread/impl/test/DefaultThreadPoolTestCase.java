/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.thread.impl.test;

import java.util.HashMap;
import junit.framework.TestCase;
import org.apache.avalon.excalibur.thread.impl.DefaultThreadPool;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.excalibur.threadcontext.ThreadContext;
import org.apache.excalibur.threadcontext.impl.DefaultThreadContextPolicy;

/**
 * TestCase for DefaultThreadPool.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public class DefaultThreadPoolTestCase
    extends TestCase
{
    public DefaultThreadPoolTestCase( final String name )
    {
        super( name );
    }

    public void testWithThreadContext()
        throws Exception
    {
        final DefaultThreadContextPolicy policy = new DefaultThreadContextPolicy();
        final HashMap map = new HashMap( 1 );
        map.put( DefaultThreadContextPolicy.CLASSLOADER, getClass().getClassLoader() );
        final ThreadContext threadContext = new ThreadContext( policy, map );
        final DefaultThreadPool pool = new DefaultThreadPool( "default", 10, threadContext );
        pool.setDaemon( false );
        pool.enableLogging( new ConsoleLogger() );
        pool.execute( new DummyRunnable() );
    }

    public void testWithoutThreadContext()
        throws Exception
    {
        final ThreadContext threadContext = ThreadContext.getThreadContext();
        final DefaultThreadPool pool = new DefaultThreadPool( "default", 10, threadContext );
        pool.setDaemon( false );
        pool.enableLogging( new ConsoleLogger() );
        pool.execute( new DummyRunnable() );
    }

    private static class DummyRunnable
        implements Runnable
    {
        public void run()
        {
        }
    }
}
