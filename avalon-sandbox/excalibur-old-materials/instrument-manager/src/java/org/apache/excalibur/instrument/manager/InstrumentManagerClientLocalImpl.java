/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.manager;

import org.apache.excalibur.instrument.manager.interfaces.InstrumentableDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentableException;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentException;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentSampleException;

import org.apache.avalon.framework.configuration.Configuration;

/**
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.3 $ $Date: 2002/11/08 07:59:13 $
 * @since 4.1
 */
public class InstrumentManagerClientLocalImpl
    implements InstrumentManagerClientLocal
{
    private DefaultInstrumentManager m_manager;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public InstrumentManagerClientLocalImpl( DefaultInstrumentManager manager )
    {
        m_manager = manager;
    }
    
    /*---------------------------------------------------------------
     * InstrumentManagerClient Methods
     *-------------------------------------------------------------*/
    /**
     * Returns the name used to identify this InstrumentManager.
     *
     * @return The name used to identify this InstrumentManager.
     */
    public String getName()
    {
        return m_manager.getName();
    }
    
    /**
     * Returns the description of this InstrumentManager.
     *
     * @return The description of this InstrumentManager.
     */
    public String getDescription()
    {
        return m_manager.getDescription();
    }
    
    /**
     * Returns a InstrumentableDescriptor based on its name or the name of any
     *  of its children.
     *
     * @param instrumentableName Name of the Instrumentable being requested.
     *
     * @return A Descriptor of the requested Instrumentable.
     *
     * @throws NoSuchInstrumentableException If the specified Instrumentable does
     *                                   not exist.
     */
    public InstrumentableDescriptor getInstrumentableDescriptor( String instrumentableName )
        throws NoSuchInstrumentableException
    {
        return getInstrumentableDescriptorLocal( instrumentableName );
    }

    /**
     * Returns an array of Descriptors for the Instrumentables managed by this
     *  InstrumentManager.
     *
     * @return An array of Descriptors for the Instrumentables managed by this
     *  InstrumentManager.
     */
    public InstrumentableDescriptor[] getInstrumentableDescriptors()
    {
        return getInstrumentableDescriptorLocals();
    }
    
    /**
     * Searches the entire instrument tree an instrumentable with the given
     *  name.
     *
     * @param instrumentableName Name of the Instrumentable being requested.
     *
     * @return A Descriptor of the requested Instrumentable.
     *
     * @throws NoSuchInstrumentableException If the specified Instrumentable does
     *                                       not exist.
     */
    public InstrumentableDescriptor locateInstrumentableDescriptor( String instrumentableName )
        throws NoSuchInstrumentableException
    {
        return locateInstrumentableDescriptorLocal( instrumentableName );
    }
    
    /**
     * Searches the entire instrument tree an instrument with the given name.
     *
     * @param instrumentName Name of the Instrument being requested.
     *
     * @return A Descriptor of the requested Instrument.
     *
     * @throws NoSuchInstrumentException If the specified Instrument does
     *                                   not exist.
     */
    public InstrumentDescriptor locateInstrumentDescriptor( String instrumentName )
        throws NoSuchInstrumentException
    {
        return locateInstrumentDescriptorLocal( instrumentName );
    }

    /**
     * Searches the entire instrument tree an instrument sample with the given
     *  name.
     *
     * @param sampleName Name of the Instrument Sample being requested.
     *
     * @return A Descriptor of the requested Instrument Sample.
     *
     * @throws NoSuchInstrumentSampleException If the specified Instrument
     *                                         Sample does not exist.
     */
    public InstrumentSampleDescriptor locateInstrumentSampleDescriptor( String sampleName )
        throws NoSuchInstrumentSampleException
    {
        return locateInstrumentSampleDescriptorLocal( sampleName );
    }
    
    /**
     * Returns the stateVersion of the instrument manager.  The state version
     *  will be incremented each time any of the configuration of the
     *  instrument manager or any of its children is modified.
     * Clients can use this value to tell whether or not anything has
     *  changed without having to do an exhaustive comparison.
     *
     * @return The state version of the instrument manager.
     */
    public int getStateVersion()
    {
        return m_manager.getStateVersion();
    }
    
    /**
     * Invokes garbage collection.
     */
    public void invokeGarbageCollection()
    {
        m_manager.invokeGarbageCollection();
    }
    
    /*---------------------------------------------------------------
     * InstrumentManagerClientLocal Methods
     *-------------------------------------------------------------*/
    /**
     * Returns a InstrumentableDescriptorLocal based on its name or the name
     *  of any of its children.
     *
     * @param instrumentableName Name of the Instrumentable being requested.
     *
     * @return A Descriptor of the requested Instrumentable.
     *
     * @throws NoSuchInstrumentableException If the specified Instrumentable does
     *                                   not exist.
     */
    public InstrumentableDescriptorLocal getInstrumentableDescriptorLocal(
                                                    String instrumentableName )
        throws NoSuchInstrumentableException
    {
        return m_manager.getInstrumentableDescriptor( instrumentableName );
    }

    /**
     * Returns an array of Descriptors for the Instrumentables managed by this
     *  InstrumentManager.
     *
     * @return An array of Descriptors for the Instrumentables managed by this
     *  InstrumentManager.
     */
    public InstrumentableDescriptorLocal[] getInstrumentableDescriptorLocals()
    {
        return m_manager.getInstrumentableDescriptors();
    }
    
    /**
     * Searches the entire instrument tree an instrumentable with the given
     *  name.
     *
     * @param instrumentableName Name of the Instrumentable being requested.
     *
     * @return A Descriptor of the requested Instrumentable.
     *
     * @throws NoSuchInstrumentableException If the specified Instrumentable does
     *                                       not exist.
     */
    public InstrumentableDescriptorLocal locateInstrumentableDescriptorLocal(
                                                String instrumentableName )
        throws NoSuchInstrumentableException
    {
        return m_manager.locateInstrumentableDescriptor( instrumentableName );
    }
    
    /**
     * Searches the entire instrument tree an instrument with the given name.
     *
     * @param instrumentName Name of the Instrument being requested.
     *
     * @return A Descriptor of the requested Instrument.
     *
     * @throws NoSuchInstrumentException If the specified Instrument does
     *                                   not exist.
     */
    public InstrumentDescriptorLocal locateInstrumentDescriptorLocal( String instrumentName )
        throws NoSuchInstrumentException
    {
        return m_manager.locateInstrumentDescriptor( instrumentName );
    }

    /**
     * Searches the entire instrument tree an instrument sample with the given
     *  name.
     *
     * @param sampleName Name of the Instrument Sample being requested.
     *
     * @return A Descriptor of the requested Instrument Sample.
     *
     * @throws NoSuchInstrumentSampleException If the specified Instrument
     *                                         Sample does not exist.
     */
    public InstrumentSampleDescriptorLocal locateInstrumentSampleDescriptorLocal(
                                                String sampleName )
        throws NoSuchInstrumentSampleException
    {
        return m_manager.locateInstrumentSampleDescriptor( sampleName );
    }
}

