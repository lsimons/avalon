/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument.manager;

import org.apache.avalon.excalibur.instrument.manager.interfaces.InstrumentDescriptor;
import org.apache.avalon.excalibur.instrument.manager.interfaces.InstrumentableDescriptor;
import org.apache.avalon.excalibur.instrument.manager.interfaces.NoSuchInstrumentException;

/**
 * Describes a Instrumentable and acts as a Proxy to protect the original
 *  Instrumentable.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.3 $ $Date: 2002/04/03 13:18:29 $
 * @since 4.1
 */
public class InstrumentableDescriptorImpl
    implements InstrumentableDescriptor
{
    /** InstrumentableProxy being described. */
    private InstrumentableProxy m_instrumentableProxy;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new InstrumentableDescriptorImpl.
     *
     * @param instrumentableProxy InstrumentableProxy being described.
     */
    InstrumentableDescriptorImpl( InstrumentableProxy instrumentableProxy )
    {
        m_instrumentableProxy = instrumentableProxy;
    }

    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Returns true if the Instrumentable was configured in the instrumentables
     *  section of the configuration.
     *
     * @return True if configured.
     */
    public boolean isConfigured()
    {
        return m_instrumentableProxy.isConfigured();
    }

    /**
     * Gets the name for the Instrumentable.  The Instrumentable Name is used to
     *  uniquely identify the Instrumentable during the configuration of the
     *  Profiler and to gain access to a InstrumentableDescriptor through a
     *  ProfilerManager.
     *
     * @return The name used to identify a Instrumentable.
     */
    public String getName()
    {
        return m_instrumentableProxy.getName();
    }

    /**
     * Gets the description of the Instrumentable.
     *
     * @return The description of the Instrumentable.
     */
    public String getDescription()
    {
        return m_instrumentableProxy.getDescription();
    }

    /**
     * Returns a InstrumentDescriptor based on its name.
     *
     * @param instrumentName Name of the Instrument being requested.
     *
     * @return A Descriptor of the requested Instrument.
     *
     * @throws NoSuchInstrumentException If the specified Instrument does
     *                                     not exist.
     */
    public InstrumentDescriptor getInstrumentDescriptor( String instrumentName )
        throws NoSuchInstrumentException
    {
        InstrumentProxy instrumentProxy =
            m_instrumentableProxy.getInstrumentProxy( instrumentName );
        if( instrumentProxy == null )
        {
            throw new NoSuchInstrumentException(
                "No instrument can be found using name: " + instrumentName );
        }

        return instrumentProxy.getDescriptor();
    }

    /**
     * Returns an array of Descriptors for the Instruments registered by this
     *  Instrumentable.
     *
     * @return An array of Descriptors for the Instruments registered by this
     *  Instrumentable.
     */
    public InstrumentDescriptor[] getInstrumentDescriptors()
    {
        return m_instrumentableProxy.getInstrumentDescriptors();
    }
}
