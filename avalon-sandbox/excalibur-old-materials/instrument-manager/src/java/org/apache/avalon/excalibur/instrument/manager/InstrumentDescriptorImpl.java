/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument.manager;

import org.apache.avalon.excalibur.instrument.manager.interfaces.CounterInstrumentListener;
import org.apache.avalon.excalibur.instrument.manager.interfaces.InstrumentDescriptor;
import org.apache.avalon.excalibur.instrument.manager.interfaces.InstrumentSampleDescriptor;
import org.apache.avalon.excalibur.instrument.manager.interfaces.NoSuchInstrumentSampleException;
import org.apache.avalon.excalibur.instrument.manager.interfaces.ValueInstrumentListener;

/**
 * Describes a Instrument and acts as a Proxy to protect the original
 *  Instrument.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.3 $ $Date: 2002/04/03 13:18:29 $
 * @since 4.1
 */
public class InstrumentDescriptorImpl
    implements InstrumentDescriptor
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
    InstrumentDescriptorImpl( InstrumentProxy instrumentProxy )
    {
        m_instrumentProxy = instrumentProxy;
    }
    
    /*---------------------------------------------------------------
     * Methods
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
     * Returns the type of the Instrument.
     *
     * @return The type of the Instrument.
     */
    public int getType()
    {
        return m_instrumentProxy.getType();
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
     * Returns a InstrumentSampleDescriptor based on its name.
     *
     * @param InstrumentSampleName Name of the InstrumentSample being requested.
     *
     * @return A Descriptor of the requested InstrumentSample.
     *
     * @throws NoSuchInstrumentSampleException If the specified InstrumentSample
     *                                      does not exist.
     */
    public InstrumentSampleDescriptor getInstrumentSampleDescriptor( String InstrumentSampleName )
        throws NoSuchInstrumentSampleException
    {
        InstrumentSample InstrumentSample =
            m_instrumentProxy.getInstrumentSample( InstrumentSampleName );
        if ( InstrumentSample == null )
        {
            throw new NoSuchInstrumentSampleException(
                "No instrument sample can be found using name: " + InstrumentSampleName );
        }
        
        return InstrumentSample.getDescriptor();
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
        return m_instrumentProxy.getInstrumentSampleDescriptors();
    }
}
