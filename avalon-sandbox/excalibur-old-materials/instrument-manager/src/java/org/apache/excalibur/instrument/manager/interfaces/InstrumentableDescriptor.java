/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.manager.interfaces;

/**
 * Describes a Instrumentable and acts as a Proxy to protect the original
 *  Instrumentable.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/09/06 02:10:13 $
 * @since 4.1
 */
public interface InstrumentableDescriptor
{
    /**
     * Returns true if the Instrumentable was configured in the instrumentables
     *  section of the configuration.
     *
     * @return True if configured.
     */
    boolean isConfigured();

    /**
     * Returns true if the Instrumentable was registered with the Instrument
     *  Manager.
     *
     * @return True if registered.
     */
    boolean isRegistered();

    /**
     * Gets the name for the Instrumentable.  The Instrumentable Name is used to
     *  uniquely identify the Instrumentable during the configuration of the
     *  Profiler and to gain access to a InstrumentableDescriptor through a
     *  ProfilerManager.
     *
     * @return The name used to identify a Instrumentable.
     */
    String getName();

    /**
     * Gets the description of the Instrumentable.
     *
     * @return The description of the Instrumentable.
     */
    String getDescription();

    /**
     * Returns a child InstrumentableDescriptor based on its name or the name
     *  of any of its children.
     *
     * @param childInstrumentableName Name of the child Instrumentable being
     *                                requested.
     *
     * @return A descriptor of the requested child Instrumentable.
     *
     * @throws NoSuchInstrumentableException If the specified Instrumentable
     *                                       does not exist.
     */
    InstrumentableDescriptor getChildInstrumentableDescriptor( String childInstrumentableName )
        throws NoSuchInstrumentableException;

    /**
     * Returns an array of Descriptors for the child Instrumentables registered
     *  by this Instrumentable.
     *
     * @return An array of Descriptors for the child Instrumentables registered
     *  by this Instrumentable.
     */
    InstrumentableDescriptor[] getChildInstrumentableDescriptors();
        
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
    InstrumentDescriptor getInstrumentDescriptor( String instrumentName )
        throws NoSuchInstrumentException;

    /**
     * Returns an array of Descriptors for the Instruments registered by this
     *  Instrumentable.
     *
     * @return An array of Descriptors for the Instruments registered by this
     *  Instrumentable.
     */
    InstrumentDescriptor[] getInstrumentDescriptors();
    
    /**
     * Returns the stateVersion of the instrumentable.  The state version
     *  will be incremented each time any of the configuration of the
     *  instrumentable or any of its children is modified.
     * Clients can use this value to tell whether or not anything has
     *  changed without having to do an exhaustive comparison.
     *
     * @return The state version of the instrumentable.
     */
    int getStateVersion();
}
