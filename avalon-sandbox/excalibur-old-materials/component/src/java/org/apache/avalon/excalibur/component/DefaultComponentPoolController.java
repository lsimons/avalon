/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.component;

import org.apache.avalon.excalibur.pool.PoolController;
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 * This is the <code>PoolController</code> for the Avalon Excalibur
 * Component Management Framework.
 *
 * @author <a href="mailto:Giacomo.Pati@pwr.ch">Giacomo Pati</a>
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/04 05:09:02 $
 * @since 4.0
 *
 * @deprecated DefaultComponentPool is no longer used by the PoolableComponentHandler.
 */
public class DefaultComponentPoolController
    implements PoolController, ThreadSafe
{
    /** Default increase/decrease amount */
    public static final int DEFAULT_AMOUNT = 8;

    /** Used increase/decrease amount */
    protected final int m_amount;

    /**
     * The default constructor.  It initializes the used increase/
     * decrease amount to the default.
     */
    public DefaultComponentPoolController()
    {
        m_amount = DefaultComponentPoolController.DEFAULT_AMOUNT;
    }

    /**
     * The alternate constructor.  It initializes the used increase/
     * decrease amount to the specified number only if it is greater
     * than 0.  Otherwise it uses the default amount.
     *
     * @param amount   The amount to grow and shrink a pool by.
     */
    public DefaultComponentPoolController( final int amount )
    {
        if( amount > 0 )
        {
            m_amount = amount;
        }
        else
        {
            m_amount = DefaultComponentPoolController.DEFAULT_AMOUNT;
        }
    }

    /**
     * Called when a Pool reaches it's minimum.
     * Return the number of elements to increase pool by.
     *
     * @return the element increase
     */
    public int grow()
    {
        return m_amount;
    }

    /**
     * Called when a pool reaches it's maximum.
     * Returns the number of elements to decrease pool by.
     *
     * @return the element decrease
     */
    public int shrink()
    {
        return m_amount;
    }
}
