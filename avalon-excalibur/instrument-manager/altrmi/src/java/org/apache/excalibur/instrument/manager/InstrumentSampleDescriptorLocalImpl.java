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

import org.apache.excalibur.instrument.manager.interfaces.InstrumentDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleSnapshot;

/**
 * Describes an InstrumentSample and acts as a Proxy to protect the original
 *  InstrumentSample object.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 16:36:32 $
 * @since 4.1
 */
public class InstrumentSampleDescriptorLocalImpl
    implements InstrumentSampleDescriptorLocal
{
    /** The InstrumentSample. */
    private InstrumentSample m_instrumentSample;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new InstrumentSampleDescriptor.
     *
     * @param InstrumentSample InstrumentSample being described.
     */
    InstrumentSampleDescriptorLocalImpl( InstrumentSample InstrumentSample )
    {
        m_instrumentSample = InstrumentSample;
    }
    
    /*---------------------------------------------------------------
     * Methods InstrumentSampleDescriptor
     *-------------------------------------------------------------*/
    /**
     * Returns true if the InstrumentSample was configured in the instrumentables
     *  section of the configuration.
     *
     * @return True if configured.
     */
    public boolean isConfigured()
    {
        return m_instrumentSample.isConfigured();
    }
    
    /**
     * Returns the name of the sample.
     *
     * @return The name of the sample.
     */
    public String getName()
    {
        return m_instrumentSample.getName();
    }
    
    /**
     * Returns the sample interval.  The period of each sample in millisends.
     *
     * @return The sample interval.
     */
    public long getInterval()
    {
        return m_instrumentSample.getInterval();
    }
    
    /**
     * Returns the number of samples in the sample history.
     *
     * @return The size of the sample history.
     */
    public int getSize()
    {
        return m_instrumentSample.getSize();
    }
    
    /**
     * Returns the description of the sample.
     *
     * @return The description of the sample.
     */
    public String getDescription()
    {
        return m_instrumentSample.getDescription();
    }
    
    /**
     * Returns the type of the Instrument Sample.  Possible values include
     *  InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_COUNTER,
     *  InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MAXIMUM,
     *  InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MEAN, or
     *  InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MINIMUM.
     *
     * @return The type of the Instrument Sample.
     */
    public int getType()
    {
        return m_instrumentSample.getType();
    }
    
    /**
     * Obtain the value of the sample.  All samples are integers, so the profiled
     * objects must measure quantity (numbers of items), rate (items/period), time in
     * milliseconds, etc.
     *
     * @return The sample value.
     */
    public int getValue()
    {
        return m_instrumentSample.getValue();
    }
    
    /**
     * Obtain the UNIX time of the beginning of the sample.
     *
     * @return The UNIX time of the beginning of the sample.
     */
    public long getTime()
    {
        return m_instrumentSample.getTime();
    }
    
    /**
     * Returns the Type of the Instrument which can use the sample.  This
     *  should be the same for all instances of a class.
     * <p>
     * Should be one of the following: InstrumentManager.PROFILE_POINT_TYPE_COUNTER
     *  or InstrumentManager.PROFILE_POINT_TYPE_VALUE
     *
     * @return The Type of the Instrument which can use the sample.
     */
    public int getInstrumentType()
    {
        return m_instrumentSample.getInstrumentType();
    }
    
    /**
     * Returns a reference to the descriptor of the Instrument of the sample.
     *
     * @return A reference to the descriptor of the Instrument of the sample.
     */
    public InstrumentDescriptor getInstrumentDescriptor()
    {
        return getInstrumentDescriptorLocal();
    }
    
    /**
     * Returns the time that the current lease expires.  Permanent samples will
     *  return a value of 0.
     *
     * @return The time that the current lease expires.
     */
    public long getLeaseExpirationTime()
    {
        return m_instrumentSample.getLeaseExpirationTime();
    }
    
    /**
     * Extends the lease to be lease milliseconds from the current time.
     *
     * @param lease The length of the lease in milliseconds.
     *
     * @return The new lease expiration time.  Returns 0 if the sample is
     *         permanent.
     */
    public long extendLease( long lease )
    {
        return m_instrumentSample.extendLease( lease );
    }
    
    /**
     * Obtains a static snapshot of the InstrumentSample.
     *
     * @return A static snapshot of the InstrumentSample.
     */
    public InstrumentSampleSnapshot getSnapshot()
    {
        return m_instrumentSample.getSnapshot();
    }
    
    /**
     * Returns the stateVersion of the sample.  The state version will be
     *  incremented each time any of the configuration of the sample is
     *  modified.
     * Clients can use this value to tell whether or not anything has
     *  changed without having to do an exhaustive comparison.
     *
     * @return The state version of the sample.
     */
    public int getStateVersion()
    {
        return m_instrumentSample.getStateVersion();
    }
    
    /*---------------------------------------------------------------
     * Methods InstrumentSampleDescriptorLocal
     *-------------------------------------------------------------*/
    /**
     * Returns a reference to the descriptor of the Instrument of the sample.
     *
     * @return A reference to the descriptor of the Instrument of the sample.
     */
    public InstrumentDescriptorLocal getInstrumentDescriptorLocal()
    {
        return m_instrumentSample.getInstrumentProxy().getDescriptor();
    }
    
    /**
     * Registers a InstrumentSampleListener with a InstrumentSample given a name.
     *
     * @param listener The listener which should start receiving updates from the
     *                 InstrumentSample.
     */
    public void addInstrumentSampleListener( InstrumentSampleListener listener )
    {
        m_instrumentSample.addInstrumentSampleListener( listener );
    }
    
    /**
     * Unregisters a InstrumentSampleListener from a InstrumentSample given a name.
     *
     * @param listener The listener which should stop receiving updates from the
     *                 InstrumentSample.
     */
    public void removeInstrumentSampleListener( InstrumentSampleListener listener )
    {
        m_instrumentSample.removeInstrumentSampleListener( listener );
    }
}

