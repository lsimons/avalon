/* 
 * Copyright 2002-2004 The Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.excalibur.instrument.manager;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentManagerClient;

/**
 * A InstrumentSample which stores the number of times that increment has been
 *  called during the sample period.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.4 $ $Date: 2004/02/28 11:47:25 $
 * @since 4.1
 */
class CounterInstrumentSample
    extends AbstractInstrumentSample
    implements CounterInstrumentListener
{
    /** The count. */
    protected int m_count;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new CounterInstrumentSample
     *
     * @param instrumentProxy The InstrumentProxy which owns the
     *                        InstrumentSample.
     * @param name The name of the new InstrumentSample.
     * @param interval The sample interval of the new InstrumentSample.
     * @param size The number of samples to store as history.  Assumes that size is at least 1.
     * @param description The description of the new InstrumentSample.
     * @param lease The length of the lease in milliseconds.
     */
    CounterInstrumentSample( InstrumentProxy instrumentProxy,
                             String name,
                             long interval,
                             int size,
                             String description,
                             long lease )
    {
        super( instrumentProxy, name, interval, size, description, lease );
        
        // Set the current value to 0 initially.
        m_count = 0;
    }
    
    /*---------------------------------------------------------------
     * InstrumentSample Methods
     *-------------------------------------------------------------*/
    /**
     * Returns the type of the Instrument Sample.
     *
     * @return The type of the Instrument Sample.
     */
    public int getType()
    {
        return InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_COUNTER;
    }
    
    /**
     * Returns the Type of the Instrument which can use the sample.  This
     *  should be the same for all instances of a class.
     * <p>
     * This InstrumentSample returns InstrumentManager.PROFILE_POINT_TYPE_COUNTER
     *
     * @return The Type of the Instrument which can use the sample.
     */
    public final int getInstrumentType()
    {
        return InstrumentManagerClient.INSTRUMENT_TYPE_COUNTER;
    }
    
    /**
     * Obtain the value of the sample.  All samples are integers, so the profiled
     * objects must measure quantity (numbers of items), rate (items/period), time in
     * milliseconds, etc.
     *
     * @return The sample value.
     */
    public int getValueInner()
    {
        return m_count;
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
        // Counts do not propagate, so always reset the count to 0.
        m_count = 0;
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
        m_count = value;
    }
    
    /**
     * Called after a state is loaded if the sample period is not the same
     *  as the last period saved.
     */
    protected void postSaveNeedsReset()
    {
        m_count = 0;
    }
    
    /*---------------------------------------------------------------
     * CounterInstrumentListener Methods
     *-------------------------------------------------------------*/
    /**
     * Called by a CounterInstrument whenever its value is incremented.
     *
     * @param instrumentName The name of Instrument which was incremented.
     * @param count A positive integer to increment the counter by.
     * @param time The time that the Instrument was incremented.
     */
    public void increment( String instrumentName, int count, long time )
    {
        //System.out.println("CounterInstrumentSample.increment(" + instrumentName + ", " + count + ", " + time + ") : " + getName() );
        increment( count, time );
    }
    
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Increments the count.
     *
     * @param time Time that the count is incremented.
     * @param count A positive integer to increment the counter by.
     */
    private void increment( int count, long time )
    {
        int sampleValue;
        long sampleTime;
        
        synchronized(this)
        {
            update( time );
            
            m_count += count;
            
            sampleValue = m_count;
            sampleTime = m_time;
        }
        
        updateListeners( sampleValue, sampleTime );
    }
}
