/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.pool;


/**
 * This is a <code>PoolController</code> that controls how many
 * instances of a <code>Poolable</code> are created at one time.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public class DefaultPoolController implements PoolController {
    private final int m_amount;

    public DefaultPoolController()
    {
        this(AbstractPool.DEFAULT_POOL_SIZE);
    }

    public DefaultPoolController(final int amount)
    {
        m_amount = (amount > 0) ? amount : 1;
    }

    public int grow()
    {
        return m_amount;
    }

    public int shrink()
    {
        return m_amount;
    }
}