/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.component;

import org.apache.avalon.component.Component;
import org.apache.avalon.component.ComponentManager;
import org.apache.avalon.component.Composable;
import org.apache.avalon.configuration.Configurable;
import org.apache.avalon.configuration.Configuration;
import org.apache.avalon.thread.ThreadSafe;
import org.apache.excalibur.pool.PoolController;

/**
 * This class holds a sitemap component which is not specially marked as having
 * a spezial behaviour or treatment.
 *
 * @author <a href="mailto:Giacomo.Pati@pwr.ch">Giacomo Pati</a>
 * @version CVS $Revision: 1.1 $ $Date: 2001/04/18 13:16:36 $
 */
public class DefaultComponentPoolController
    implements PoolController, ThreadSafe, Component
{
    /** Initial increase/decrease amount */
    public final static int  DEFAULT_AMOUNT      = 8;

    /** Current increase/decrease amount */
    protected int            m_amount            = DEFAULT_AMOUNT;

    /** The last direction to increase/decrease >0 means increase, <0 decrease */
    protected int            m_sizing_direction  = 0;

    /**
     * Called when a Pool reaches it's minimum.
     * Return the number of elements to increase minimum and maximum by.
     * @return the element increase
     */
    public int grow()
    {
        /*
          if (m_sizing_direction < 0 && m_amount > 1)
          m_amount /= 2;
          m_sizing_direction = 1;
        */
        return m_amount;
    }

    /**
     * Called when a pool reaches it's maximum.
     * Returns the number of elements to decrease mi and max by.
     * @return the element decrease
     */
    public int shrink()
    {
        /*
          if (m_sizing_direction > 0 && m_amount > 1)
          m_amount /= 2;
          m_sizing_direction = -1;
        */
        return m_amount;
    }
}
