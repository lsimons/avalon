/*
    * Copyright (C) The Apache Software Foundation. All rights reserved.
    *
    * This software is published under the terms of the Apache Software License
    * version 1.1, a copy of which has been included with this distribution in
    * the LICENSE.txt file.
    */
package org.apache.excalibur.instrument.test;

import org.apache.excalibur.instrument.InstrumentProxy;

/**
 * Dummy InstrumentProxy used to test instruments.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/09/26 06:34:53 $
 */
public class TestInstrumentProxy
    implements InstrumentProxy
{
    private boolean m_active;
    private int m_value;
    
    /*---------------------------------------------------------------
     * InstrumentProxy Methods
     *-------------------------------------------------------------*/
    /**
     * Used by classes being profiles so that they can avoid unnecessary
     *  code when the data from a Instrument is not being used.
     *
     * @return True if listeners are registered with the Instrument.
     */
    public boolean isActive()
    {
        return m_active;
    }
    
    /**
     * Increments the Instrument by a specified count.  This method should be
     *  optimized to be extremely light weight when there are no registered
     *  CounterInstrumentListeners.
     * <p>
     * This method may throw an IllegalStateException if the proxy is not meant
     *  to handle calls to increment.
     *
     * @param count A positive integer to increment the counter by.
     */
    public void increment( int count )
    {
        m_value += count;
    }
    
    /**
     * Sets the current value of the Instrument.  This method is optimized
     *  to be extremely light weight when there are no registered
     *  ValueInstrumentListeners.
     * <p>
     * This method may throw an IllegalStateException if the proxy is not meant
     *  to handle calls to setValue.
     *
     * @param value The new value for the Instrument.
     */
    public void setValue( int value )
    {
        m_value = value;
    }
    
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Sets the activate flag on the proxy so that it will collect information.
     */
    public void activate()
    {
        m_active = true;
    }
    
    /**
     **/
    public int getValue()
    {
        return m_value;
    }
}