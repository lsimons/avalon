/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 
 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:
 
 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.
 
 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.
 
 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.
 
 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"  
    must not be used to endorse or promote products derived from this  software 
    without  prior written permission. For written permission, please contact 
    apache@apache.org.
 
 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.
 
 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the 
 Apache Software Foundation, please see <http://www.apache.org/>.
 
*/
package org.apache.excalibur.instrument.manager;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentManagerClient;

/**
 * A InstrumentSample which stores the number of times that increment has been
 *  called during the sample period.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.3 $ $Date: 2003/02/25 16:28:16 $
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
