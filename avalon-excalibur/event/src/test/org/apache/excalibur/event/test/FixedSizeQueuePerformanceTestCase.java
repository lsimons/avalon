/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.event.test;

import org.apache.avalon.excalibur.event.FixedSizeQueue;

/**
 * The default queue implementation is a variabl size queue.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public final class FixedSizeQueuePerformanceTestCase extends AbstractQueueTestCase
{
    public FixedSizeQueuePerformanceTestCase( String name )
    {
        super( name );
    }

    public void testMillionIterationOneElement()
        throws Exception
    {
        this.performMillionIterationOneElement( new FixedSizeQueue( 32 ) );
    }

    public void testMillionIterationTenElements()
        throws Exception
    {
        this.performMillionIterationTenElements( new FixedSizeQueue( 32 ) );
    }
}