/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.manager;

import org.apache.excalibur.instrument.manager.interfaces.InstrumentManagerClient;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;

/**
 * An AbstractValueInstrumentSample contains all of the functionality common
 *  to all InstrumentSamples which represent a fixed value.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/08/14 14:58:21 $
 * @since 4.1
 */
abstract class AbstractValueInstrumentSample
    extends AbstractInstrumentSample
    implements ValueInstrumentListener
{
    /** The sample value. */
    protected int m_value;
    
    /** The number of times that the value has been changed in this sample period. */
    protected int m_valueCount;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new AbstractValueInstrumentSample
     *
     * @param instrumentProxy The InstrumentProxy which owns the
     *                        InstrumentSample.
     * @param name The name of the new InstrumentSample.
     * @param interval The sample interval of the new InstrumentSample.
     * @param size The number of samples to store as history.  Assumes that size is at least 1.
     * @param description The description of the new InstrumentSample.
     * @param lease The length of the lease in milliseconds.
     */
    protected AbstractValueInstrumentSample( InstrumentProxy instrumentProxy,
                                             String name,
                                             long interval,
                                             int size,
                                             String description,
                                             long lease )
    {
        super( instrumentProxy, name, interval, size, description, lease );
        
        // Set the current value to 0 initially.
        m_value = 0;
    }
    
    /*---------------------------------------------------------------
     * InstrumentSample Methods
     *-------------------------------------------------------------*/
    /**
     * Returns the Type of the Instrument which can use the sample.  This
     *  should be the same for all instances of a class.
     * <p>
     * This InstrumentSample returns InstrumentManager.PROFILE_POINT_TYPE_VALUE
     *
     * @return The Type of the Instrument which can use the sample.
     */
    public final int getInstrumentType()
    {
        return InstrumentManagerClient.INSTRUMENT_TYPE_VALUE;
    }
    
    /**
     * Obtain the value of the sample.  All samples are integers, so the profiled
     * objects must measure quantity (numbers of items), rate (items/period), time in
     * milliseconds, etc.
     * <p>
     * Should only be called when synchronized.
     *
     * @return The sample value.
     */
    public int getValueInner()
    {
        return m_value;
    }

    /*---------------------------------------------------------------
     * AbstractInstrumentSample Methods
     *-------------------------------------------------------------*/
    /**
     * Allow subclasses to add information into the saved state.
     *
     * @param state State configuration.
     */
    protected void saveState( DefaultConfiguration state )
    {
        super.saveState( state );
        
        state.setAttribute( "value-count", Integer.toString( m_valueCount ) );
    }
    
    /**
     * Used to load the state, called from AbstractInstrumentSample.loadState();
     * <p>
     * Should only be called when synchronized.
     *
     * @param value Current value loaded from the state.
     * @param state Configuration object to load state from.
     *
     * @throws ConfigurationException If there were any problems loading the
     *                                state.
     */
    protected void loadState( int value, Configuration state )
        throws ConfigurationException
    {
        m_value = value;
        m_valueCount = state.getAttributeAsInteger( "value-count" );
    }
    
    /**
     * Called after a state is loaded if the sample period is not the same
     *  as the last period saved.
     */
    protected void postSaveNeedsReset()
    {
        m_value = 0;
        m_valueCount = 0;
    }
    
    /*---------------------------------------------------------------
     * ValueInstrumentListener Methods
     *-------------------------------------------------------------*/
    /**
     * Called by a ValueInstrument whenever its value is set.
     *
     * @param instrumentName The key of Instrument whose value was set.
     * @param value Value that was set.
     * @param time The time that the Instrument was incremented.
     *
     * ValueInstrument
     */
    public void setValue( String instrumentName, int value, long time )
    {
        //System.out.println("AbstractValueInstrumentSample.setValue(" + instrumentName + ", "
        //    + value + ", " + time + ") : " + getName());
        setValueInner( value, time );
    }
    
    
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Sets the current value of the sample.
     *
     * @param value New sample value.
     * @param time Time that the new sample arrives.
     */
    protected abstract void setValueInner( int value, long time );
}
