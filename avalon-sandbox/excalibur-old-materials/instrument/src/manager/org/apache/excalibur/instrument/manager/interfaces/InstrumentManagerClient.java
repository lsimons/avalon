/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.manager.interfaces;

import org.apache.excalibur.instrument.Instrumentable;

import org.apache.avalon.framework.configuration.Configuration;

/**
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/08/03 15:00:38 $
 * @since 4.1
 */
public interface InstrumentManagerClient
{
    /** Type which specifies that the type of a Instrument has not yet been determined. */
    int INSTRUMENT_TYPE_NONE = 0;
    
    /** Type which identifies CounterInstruments. */
    int INSTRUMENT_TYPE_COUNTER = 1;
    
    /** Type which identifies ValueInstruments. */
    int INSTRUMENT_TYPE_VALUE   = 2;
    
    /** Type which identifies CounterInstrumentSamples. */
    int INSTRUMENT_SAMPLE_TYPE_COUNTER = 101;
    
    /** Type which identifies MinimumInstrumentSamples. */
    int INSTRUMENT_SAMPLE_TYPE_MINIMUM = 102;
    
    /** Type which identifies MaximumInstrumentSamples. */
    int INSTRUMENT_SAMPLE_TYPE_MAXIMUM = 103;
    
    /** Type which identifies MeanInstrumentSamples. */
    int INSTRUMENT_SAMPLE_TYPE_MEAN = 104;
    
    /**
     * Returns the name used to identify this InstrumentManager.
     *
     * @return The name used to identify this InstrumentManager.
     */
    String getName();
    
    /**
     * Returns the description of this InstrumentManager.
     *
     * @return The description of this InstrumentManager.
     */
    String getDescription();
    
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
    InstrumentableDescriptor getInstrumentableDescriptor( String instrumentableName )
        throws NoSuchInstrumentableException;

    /**
     * Returns an array of Descriptors for the Instrumentables managed by this
     *  InstrumentManager.
     *
     * @return An array of Descriptors for the Instrumentables managed by this
     *  InstrumentManager.
     */
    InstrumentableDescriptor[] getInstrumentableDescriptors();
    
    /**
     * Invokes garbage collection.
     */
    void invokeGarbageCollection();
}

