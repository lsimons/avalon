/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.concurrent;

/**
 * A mutual exclusion {@link Semaphore}.
 *
 * @deprecated use EDU.oswego.cs.dl.util.concurrent.Mutex instead
 *
 * @version CVS $Revision: 1.3 $ $Date: 2003/03/22 12:31:43 $
 * @since 4.0
 */

public class Mutex
    extends Semaphore
{
    /** Initialize the Mutex */
    public Mutex()
    {
        super( 1 );
    }
}
