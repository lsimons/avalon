/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.manager.interfaces;

import java.io.Serializable;

/**
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/09/06 02:10:13 $
 * @since 4.1
 */
public class InstrumentSampleSnapshot
    implements Serializable
{
    static final long serialVersionUID = -3284372358291073513L;
    
    /** The name used to reference the InstrumentSample. */
    private String m_InstrumentSampleName;
    
    /** The interval between each sample. */
    private long m_interval;
    
    /** The number of samples in the InstrumentSample. */
    private int m_size;
    
    /** The time that the last sample starts. */
    private long m_time;
    
    /** The samples as an array of integers. */
    private int[] m_samples;
    
    /** State Version. */
    private int m_stateVersion;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * @param InstrumentSampleName The name used to reference the InstrumentSample.
     * @param interval The interval between each sample.
     * @param size The number of samples in the InstrumentSample.
     * @param time The time that the last sample starts.
     * @param samples The samples as an array of integers.
     * @param stateVersion The current state version of the sample. 
     */
    public InstrumentSampleSnapshot( String InstrumentSampleName,
                           long interval,
                           int size,
                           long time,
                           int[] samples,
                           int stateVersion )
    {
        m_InstrumentSampleName = InstrumentSampleName;
        m_interval = interval;
        m_size = size;
        m_time = time;
        m_samples = samples;
        m_stateVersion = stateVersion;
    }
    
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Returns the name used to reference the InstrumentSample.
     *
     * @return The name used to reference the InstrumentSample.
     */
    public String getInstrumentSampleName()
    {
        return m_InstrumentSampleName;
    }
    
    /**
     * Returns the interval, in milliseconds, between each sample.
     *
     * @return The interval between each sample.
     */
    public long getInterval()
    {
        return m_interval;
    }
    
    /**
     * Returns the number of samples in the InstrumentSample.
     *
     * @return The number of samples in the InstrumentSample.
     */
    public int getSize()
    {
        return m_size;
    }
    
    /**
     * Returns the time that the last sample starts.
     *
     * @return The time that the last sample starts.
     */
    public long getTime()
    {
        return m_time;
    }
    
    /**
     * Returns the samples as an array of integers.  The sample at index 0
     *  will be the oldest.  The end of the array is the newest.
     *
     * @return The samples as an array of integers.
     */
    public int[] getSamples()
    {
        return m_samples;
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
        return m_stateVersion;
    }
}

