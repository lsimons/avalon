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

import org.apache.excalibur.instrument.manager.interfaces.InstrumentableDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentSampleException;

/**
 * Describes a Instrument and acts as a Proxy to protect the original
 *  Instrument.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.4 $ $Date: 2003/09/08 09:00:44 $
 * @since 4.1
 */
public class InstrumentDescriptorLocalImpl
    implements InstrumentDescriptorLocal
{
    /** InstrumentProxy being described. */
    private InstrumentProxy m_instrumentProxy;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new InstrumentDescriptor.
     *
     * @param instrumentProxy InstrumentProxy being described.
     */
    InstrumentDescriptorLocalImpl( InstrumentProxy instrumentProxy )
    {
        m_instrumentProxy = instrumentProxy;
    }
    
    /*---------------------------------------------------------------
     * InstrumentDescriptor Methods
     *-------------------------------------------------------------*/
    /**
     * Returns true if the Instrument was configured in the instrumentables
     *  section of the configuration.
     *
     * @return True if configured.
     */
    public boolean isConfigured()
    {
        return m_instrumentProxy.isConfigured();
    }

    /**
     * Returns true if the Instrument was registered with the Instrument
     *  Manager.
     *
     * @return True if registered.
     */
    public boolean isRegistered()
    {
        return m_instrumentProxy.isRegistered();
    }
    
    /**
     * Gets the name for the Instrument.  The Instrument Name is used to
     *  uniquely identify the Instrument during the configuration of the
     *  Profiler.  The value should be a string which does not contain spaces
     *  or periods.
     *
     * @return The name used to identify a Instrument.
     */
    public String getName() 
    {
        return m_instrumentProxy.getName();
    }
    
    /**
     * Gets the description of the Instrument.
     *
     * @return The description of the Instrument.
     */
    public String getDescription()
    {
        return m_instrumentProxy.getDescription();
    }
    
    /**
     * Returns the type of the Instrument.  Possible values include
     *  InstrumentManagerClient.INSTRUMENT_TYPE_COUNTER,
     *  InstrumentManagerClient.INSTRUMENT_TYPE_VALUE or
     *  InstrumentManagerClient.INSTRUMENT_TYPE_NONE, if the type was never set.
     *
     * @return The type of the Instrument.
     */
    public int getType()
    {
        return m_instrumentProxy.getType();
    }
    
    /**
     * Returns a reference to the descriptor of the Instrumentable of the
     *  instrument.
     *
     * @return A reference to the descriptor of the Instrumentable of the
     *  instrument.
     */
    public InstrumentableDescriptor getInstrumentableDescriptor()
    {
        return getInstrumentableDescriptorLocal();
    }
    
    /**
     * Returns a InstrumentSampleDescriptor based on its name.
     *
     * @param instrumentSampleName Name of the InstrumentSample being requested.
     *
     * @return A Descriptor of the requested InstrumentSample.
     *
     * @throws NoSuchInstrumentSampleException If the specified InstrumentSample
     *                                      does not exist.
     */
    public InstrumentSampleDescriptor getInstrumentSampleDescriptor( String instrumentSampleName )
        throws NoSuchInstrumentSampleException
    {
        return getInstrumentSampleDescriptorLocal( instrumentSampleName );
    }
    
    /**
     * Returns a InstrumentSampleDescriptor based on its name.  If the requested
     *  sample is invalid in any way, then an expired Descriptor will be
     *  returned.
     *
     * @param sampleDescription Description to assign to the new Sample.
     * @param sampleInterval Sample interval to use in the new Sample.
     * @param sampleLease Requested lease time for the new Sample in
     *                    milliseconds.  The InstrumentManager may grant a
     *                    lease which is shorter or longer than the requested
     *                    period.
     * @param sampleType Type of sample to request.  Must be one of the
     *                   following:  InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_COUNTER,
     *                   InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MINIMUM,
     *                   InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MAXIMUM,
     *                   InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MEAN.
     *
     * @return A Descriptor of the requested InstrumentSample.
     *
     * @throws NoSuchInstrumentSampleException If the specified InstrumentSample
     *                                      does not exist.
     */
    public InstrumentSampleDescriptor createInstrumentSample( String sampleDescription,
                                                              long sampleInterval,
                                                              int sampleSize,
                                                              long sampleLease,
                                                              int sampleType )
    {
        return createInstrumentSampleLocal(
            sampleDescription, sampleInterval, sampleSize, sampleLease, sampleType );
    }
    
    /**
     * Returns an array of Descriptors for the InstrumentSamples configured for this
     *  Instrument.
     *
     * @return An array of Descriptors for the InstrumentSamples configured for this
     *  Instrument.
     */
    public InstrumentSampleDescriptor[] getInstrumentSampleDescriptors()
    {
        return getInstrumentSampleDescriptorLocals();
    }
    
    
    /**
     * Returns the stateVersion of the instrument.  The state version will be
     *  incremented each time any of the configuration of the instrument or
     *  any of its children is modified.
     * Clients can use this value to tell whether or not anything has
     *  changed without having to do an exhaustive comparison.
     *
     * @return The state version of the instrument.
     */
    public int getStateVersion()
    {
        return m_instrumentProxy.getStateVersion();
    }
    
    /*---------------------------------------------------------------
     * InstrumentDescriptorLocal Methods
     *-------------------------------------------------------------*/
    /**
     * Returns a reference to the descriptor of the Instrumentable of the
     *  instrument.
     *
     * @return A reference to the descriptor of the Instrumentable of the
     *  instrument.
     */
    public InstrumentableDescriptorLocal getInstrumentableDescriptorLocal()
    {
        return m_instrumentProxy.getInstrumentableProxy().getDescriptor();
    }
    
    /**
     * Adds a CounterInstrumentListener to the list of listeners which will
     *  receive updates of the value of the Instrument.
     *
     * @param listener CounterInstrumentListener which will start receiving
     *                 profile updates.
     *
     * @throws IllegalStateException If the Instrument's type is not
     *         InstrumentManager.PROFILE_POINT_TYPE_COUNTER.
     */
    public void addCounterInstrumentListener( CounterInstrumentListener listener )
    {
        m_instrumentProxy.addCounterInstrumentListener( listener );
    }
    
    /**
     * Removes a InstrumentListener from the list of listeners which will
     *  receive profile events.
     *
     * @param listener InstrumentListener which will stop receiving profile
     *                 events.
     *
     * @throws IllegalStateException If the Instrument's type is not
     *         InstrumentManager.PROFILE_POINT_TYPE_COUNTER.
     */
    public void removeCounterInstrumentListener( CounterInstrumentListener listener )
    {
        m_instrumentProxy.removeCounterInstrumentListener( listener );
    }
    
    /**
     * Adds a ValueInstrumentListener to the list of listeners which will
     *  receive updates of the value of the Instrument.
     *
     * @param listener ValueInstrumentListener which will start receiving
     *                 profile updates.
     *
     * @throws IllegalStateException If the Instrument's type is not
     *         InstrumentManager.PROFILE_POINT_TYPE_VALUE.
     */
    public void addValueInstrumentListener( ValueInstrumentListener listener )
    {
        m_instrumentProxy.addValueInstrumentListener( listener );
    }
        
    /**
     * Removes a InstrumentListener from the list of listeners which will
     *  receive profile events.
     *
     * @param listener InstrumentListener which will stop receiving profile
     *                 events.
     *
     * @throws IllegalStateException If the Instrument's type is not
     *         InstrumentManager.PROFILE_POINT_TYPE_VALUE.
     */
    public void removeValueInstrumentListener( ValueInstrumentListener listener )
    {
        m_instrumentProxy.removeValueInstrumentListener( listener );
    }
    
    /**
     * Returns a InstrumentSampleDescriptorLocal based on its name.
     *
     * @param instrumentSampleName Name of the InstrumentSample being requested.
     *
     * @return A Descriptor of the requested InstrumentSample.
     *
     * @throws NoSuchInstrumentSampleException If the specified InstrumentSample
     *                                      does not exist.
     */
    public InstrumentSampleDescriptorLocal getInstrumentSampleDescriptorLocal(
                                                    String instrumentSampleName )
        throws NoSuchInstrumentSampleException
    {
        InstrumentSample instrumentSample =
            m_instrumentProxy.getInstrumentSample( instrumentSampleName );
        if ( instrumentSample == null )
        {
            throw new NoSuchInstrumentSampleException(
                "No instrument sample can be found using name: " + instrumentSampleName );
        }
        
        return instrumentSample.getDescriptor();
    }
    
    /**
     * Returns a InstrumentSampleDescriptorLocal based on its name.  If the requested
     *  sample is invalid in any way, then an expired Descriptor will be
     *  returned.
     *
     * @param sampleDescription Description to assign to the new Sample.
     * @param sampleInterval Sample interval to use in the new Sample.
     * @param sampleLease Requested lease time for the new Sample in
     *                    milliseconds.  The InstrumentManager may grant a
     *                    lease which is shorter or longer than the requested
     *                    period.
     * @param sampleType Type of sample to request.  Must be one of the
     *                   following:  InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_COUNTER,
     *                   InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MINIMUM,
     *                   InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MAXIMUM,
     *                   InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MEAN.
     *
     * @return A Descriptor of the requested InstrumentSample.
     *
     * @throws NoSuchInstrumentSampleException If the specified InstrumentSample
     *                                      does not exist.
     */
    public InstrumentSampleDescriptorLocal createInstrumentSampleLocal( String sampleDescription,
                                                                        long sampleInterval,
                                                                        int sampleSize,
                                                                        long sampleLease,
                                                                        int sampleType )
    {
        InstrumentSample sample = m_instrumentProxy.createInstrumentSample(
            sampleDescription, sampleInterval, sampleSize, sampleLease, sampleType );
        return sample.getDescriptor();
    }
    
    /**
     * Returns an array of Descriptors for the InstrumentSamples configured for this
     *  Instrument.
     *
     * @return An array of Descriptors for the InstrumentSamples configured for this
     *  Instrument.
     */
    public InstrumentSampleDescriptorLocal[] getInstrumentSampleDescriptorLocals()
    {
        return m_instrumentProxy.getInstrumentSampleDescriptors();
    }
}
