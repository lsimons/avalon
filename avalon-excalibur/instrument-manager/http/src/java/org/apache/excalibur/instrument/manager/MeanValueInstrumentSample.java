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
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentManagerClient;

/**
 * A InstrumentSample which stores the mean value set during the sample
 *  period.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 16:36:32 $
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
     * @param instrumentProxy The InstrumentProxy which owns the
     *                        InstrumentSample.
     * @param name The name of the new InstrumentSample.
     * @param interval The sample interval of the new InstrumentSample.
     * @param size The number of samples to store as history.  Assumes that size is at least 1.
     * @param description The description of the new InstrumentSample.
     * @param lease The length of the lease in milliseconds.
     */
    MeanValueInstrumentSample( InstrumentProxy instrumentProxy,
                               String name,
                               long interval,
                               int size,
                               String description,
                               long lease )
    {
        super( instrumentProxy, name, interval, size, description, lease );
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
        return InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MEAN;
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
