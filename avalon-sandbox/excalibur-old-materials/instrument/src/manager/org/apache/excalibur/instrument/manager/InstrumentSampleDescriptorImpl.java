/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.manager;

import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleListener;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleSnapshot;

/**
 * Describes an InstrumentSample and acts as a Proxy to protect the original
 *  InstrumentSample object.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/07/29 16:05:20 $
 * @since 4.1
 */
public class InstrumentSampleDescriptorImpl
    implements InstrumentSampleDescriptor
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
    InstrumentSampleDescriptorImpl( InstrumentSample InstrumentSample )
    {
        m_instrumentSample = InstrumentSample;
    }

    /*---------------------------------------------------------------
     * Methods
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
     * Obtains a static snapshot of the InstrumentSample.
     *
     * @return A static snapshot of the InstrumentSample.
     */
    public InstrumentSampleSnapshot getSnapshot()
    {
        return m_instrumentSample.getSnapshot();
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

