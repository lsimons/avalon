/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.client;

import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleUtils;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;

/**
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/08/22 16:50:38 $
 * @since 4.1
 */
class MaintainedSampleLease
{
    private String m_instrumentName;
    private String m_sampleName;
    private int    m_type;
    private long   m_interval;
    private int    m_size;
    private long   m_leaseDuration;
    private String m_description;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    MaintainedSampleLease( String instrumentName,
                           int    type,
                           long   interval,
                           int    size,
                           long   leaseDuration,
                           String description )
    {
        m_instrumentName = instrumentName;
        m_type           = type;
        m_interval       = interval;
        m_size           = size;
        m_leaseDuration  = leaseDuration;
        m_description    = description;
        
        m_sampleName = InstrumentSampleUtils.generateFullInstrumentSampleName(
            m_instrumentName, m_type, m_interval, m_size );
    }
    
    MaintainedSampleLease( Configuration stateConfig ) throws ConfigurationException
    {
        m_instrumentName = stateConfig.getAttribute         ( "instrument-name" );
        m_type           = stateConfig.getAttributeAsInteger( "type" );
        m_interval       = stateConfig.getAttributeAsLong   ( "interval" );
        m_size           = stateConfig.getAttributeAsInteger( "size" );
        m_leaseDuration  = stateConfig.getAttributeAsLong   ( "lease-duration" );
        m_description    = stateConfig.getAttribute         ( "description" );
        
        m_sampleName = InstrumentSampleUtils.generateFullInstrumentSampleName(
            m_instrumentName, m_type, m_interval, m_size );
    }

    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Saves the current state into a Configuration.
     *
     * @return The state as a Configuration.
     */
    public final Configuration saveState()
    {
        DefaultConfiguration stateConfig = new DefaultConfiguration( "maintained-sample", "-" );
        
        stateConfig.setAttribute( "instrument-name", m_instrumentName );
        stateConfig.setAttribute( "type",
            InstrumentSampleUtils.getInstrumentSampleTypeName( m_type ) );
        stateConfig.setAttribute( "interval",        Long.toString( m_interval ) );
        stateConfig.setAttribute( "size",            Integer.toString( m_size ) );
        stateConfig.setAttribute( "lease-duration",  Long.toString( m_leaseDuration ) );
        stateConfig.setAttribute( "description",     m_description );
        
        return stateConfig;
    }
    
    String getInstrumentName()
    {
        return m_instrumentName;
    }
    
    String getSampleName()
    {
        return m_sampleName;
    }
    
    int getType()
    {
        return m_type;
    }
    
    long getInterval()
    {
        return m_interval;
    }
    
    int getSize()
    {
        return m_size;
    }
    
    long getLeaseDuration()
    {
        return m_leaseDuration;
    }
    
    String getDescription()
    {
        return m_description;
    }
}

