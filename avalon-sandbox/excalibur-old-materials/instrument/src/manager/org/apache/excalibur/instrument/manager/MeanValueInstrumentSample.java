/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.manager;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;

/**
 * A InstrumentSample which stores the mean value set during the sample
 *  period.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/07/29 16:05:20 $
 * @since 4.1
 */
class MeanValueInstrumentSample
    extends AbstractValueInstrumentSample
{
    /** Total of all values seen during the sample period. */
    private long m_valueTotal;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new MeanValueInstrumentSample
     *
     * @param name The name of the new InstrumentSample.
     * @param interval The sample interval of the new InstrumentSample.
     * @param size The number of samples to store as history.  Assumes that size is at least 1.
     * @param description The description of the new InstrumentSample.
     * @param lease The length of the lease in milliseconds.
     */
    MeanValueInstrumentSample( String name,
                               long interval,
                               int size,
                               String description,
                               long lease )
    {
        super( name, interval, size, description, lease );
    }

    /*---------------------------------------------------------------
     * AbstractInstrumentSample Methods
     *-------------------------------------------------------------*/
    /**
     * The current sample has already been stored.  Reset the current sample
     *  and move on to the next.
     * <p>
     * Should only be called when synchronized.
     */
    protected void advanceToNextSample()
    {
        // Leave the value as is so that it will propagate to the next sample
        //  if needed.  But reset the value count so that new values will not
        //  be affected by the old.
        m_valueCount = 0;
    }

    /**
     * Allow subclasses to add information into the saved state.
     *
     * @param state State configuration.
     */
    protected void saveState( DefaultConfiguration state )
    {
        super.saveState( state );

        state.setAttribute( "value-total", Long.toString( m_valueTotal ) );
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
        super.loadState( value, state );

        m_valueTotal = state.getAttributeAsLong( "value-total" );
    }

    /**
     * Called after a state is loaded if the sample period is not the same
     *  as the last period saved.
     */
    protected void postSaveNeedsReset()
    {
        super.postSaveNeedsReset();

        m_valueTotal = 0;
    }

    /*---------------------------------------------------------------
     * AbstractValueInstrumentSample Methods
     *-------------------------------------------------------------*/
    /**
     * Sets the current value of the sample.  The value will be set as the
     *  mean of the new value and other values seen during the sample period.
     *
     * @param value New sample value.
     * @param time Time that the new sample arrives.
     */
    protected void setValueInner( int value, long time )
    {
        int sampleValue;
        long sampleTime;

        synchronized(this)
        {
            update( time );

            if ( m_valueCount > 0 )
            {
                // Additional sample
                m_valueCount++;
                m_valueTotal += value;
                m_value = (int)(m_valueTotal / m_valueCount);
            }
            else
            {
                // First value of this sample.
                m_valueCount = 1;
                m_valueTotal = m_value = value;
            }
            sampleValue = m_value;
            sampleTime = m_time;
        }

        updateListeners( sampleValue, sampleTime );
    }
}
