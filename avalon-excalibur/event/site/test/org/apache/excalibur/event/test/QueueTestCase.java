/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.event.test;

import org.apache.avalon.excalibur.event.DefaultQueue;

/**
 * The default queue implementation is a variabl size queue.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public final class QueueTestCase extends AbstractQueueTestCase
{
    public QueueTestCase( String name )
    {
        super( name );
    }

    public void testDefaultQueue()
        throws Exception
    {
        this.performQueue( new DefaultQueue() );
    }
}