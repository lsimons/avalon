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
 * A InstrumentSample which stores the minimum value set during the sample
 *  period.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/07/29 16:05:20 $
 * @since 4.1
 */
class MinimumValueInstrumentSample
    extends AbstractValueInstrumentSample
{
    /** Last value set to the sample for use for sample periods where no value is set. */
    private int m_lastValue;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new MinimumValueInstrumentSample
     *
     * @param name The name of the new InstrumentSample.
     * @param interval The sample interval of the new InstrumentSample.
     * @param size The number of samples to store as history.  Assumes that size is at least 1.
     * @param description The description of the new InstrumentSample.
     * @param lease The length of the lease in milliseconds.
     */
    MinimumValueInstrumentSample( String name,
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
        // Reset the value count and set the value to the last known value.
        m_value = m_lastValue;
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

        state.setAttribute( "last-value", Integer.toString( m_lastValue ) );
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

        m_lastValue = state.getAttributeAsInteger( "last-value" );
    }

    /**
     * Called after a state is loaded if the sample period is not the same
     *  as the last period saved.
     */
    protected void postSaveNeedsReset()
    {
        super.postSaveNeedsReset();

        m_lastValue = 0;
    }

    /*---------------------------------------------------------------
     * AbstractValueInstrumentSample Methods
     *-------------------------------------------------------------*/
    /**
     * Sets the current value of the sample.  The value will be set as the
     *  sample value if it is the smallest value seen during the sample period.
     *
     * @param value New sample value.
     * @param time Time that the new sample arrives.
     */
    protected void setValueInner( int value, long time )
    {
        boolean update;
        int sampleValue;
        long sampleTime;

        synchronized(this)
        {
            update( time );

            // Always store the last value to use for samples where a value is not set.
            m_lastValue = value;

            if ( m_valueCount > 0 )
            {
                // Additional sample
                m_valueCount++;
                if ( value < m_value )
                {
                    m_value = value;
                    update = true;
                }
            }
            else
            {
                // First value of this sample.
                m_valueCount = 1;
                m_value = value;
            }

            sampleValue = m_value;
            sampleTime = m_time;
                update = true;
        }

        if ( update )
        {
            updateListeners( sampleValue, sampleTime );
        }
    }
}
