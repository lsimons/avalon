/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.cache.policy;

/**
 * FIFO(First In First Out) replacement policy.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public class FIFOPolicy
    extends ListBasedPolicy
{
    public FIFOPolicy()
    {
        super();
    }

    public void hit( final Object key )
    {
        //do nothing
    }
}
