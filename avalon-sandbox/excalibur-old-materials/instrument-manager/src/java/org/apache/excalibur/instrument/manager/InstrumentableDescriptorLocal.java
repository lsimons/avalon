/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.manager;

import org.apache.excalibur.instrument.manager.interfaces.InstrumentableDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentableException;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentException;

/**
 * Describes a Instrumentable and acts as a Proxy to protect the original
 *  Instrumentable.  Methods defined by the Local interface should only
 *  be accessed from within the same JVM for performance reasons.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/08/14 14:58:21 $
 * @since 4.1
 */
public interface InstrumentableDescriptorLocal
    extends InstrumentableDescriptor
{
    /**
     * Returns a child InstrumentableDescriptorLocal based on its name or the
     *  name of any of its children.
     *
     * @param childInstrumentableName Name of the child Instrumentable being
     *                                requested.
     *
     * @return A descriptor of the requested child Instrumentable.
     *
     * @throws NoSuchInstrumentableException If the specified Instrumentable
     *                                       does not exist.
     */
    InstrumentableDescriptorLocal getChildInstrumentableDescriptorLocal(
        String childInstrumentableName ) throws NoSuchInstrumentableException;

    /**
     * Returns an array of Descriptors for the child Instrumentables registered
     *  by this Instrumentable.
     *
     * @return An array of Descriptors for the child Instrumentables registered
     *  by this Instrumentable.
     */
    InstrumentableDescriptorLocal[] getChildInstrumentableDescriptorLocals();
    
    /**
     * Returns a InstrumentDescriptorLocal based on its name.
     *
     * @param instrumentName Name of the Instrument being requested.
     *
     * @return A Descriptor of the requested Instrument.
     *
     * @throws NoSuchInstrumentException If the specified Instrument does
     *                                     not exist.
     */
    InstrumentDescriptorLocal getInstrumentDescriptorLocal( String instrumentName )
        throws NoSuchInstrumentException;

    /**
     * Returns an array of Descriptors for the Instruments registered by this
     *  Instrumentable.
     *
     * @return An array of Descriptors for the Instruments registered by this
     *  Instrumentable.
     */
    InstrumentDescriptorLocal[] getInstrumentDescriptorLocals();
}
