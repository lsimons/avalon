/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.manager;

import org.apache.excalibur.instrument.Instrumentable;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentManagerClient;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentableException;

import org.apache.avalon.framework.configuration.Configuration;

/**
 *  Methods defined by the Local interface should
 *  only be accessed from within the same JVM for performance reasons.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/08/14 14:58:21 $
 * @since 4.1
 */
public interface InstrumentManagerClientLocal
    extends InstrumentManagerClient
{
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
    InstrumentableDescriptorLocal getInstrumentableDescriptorLocal( String instrumentableName )
        throws NoSuchInstrumentableException;

    /**
     * Returns an array of Descriptors for the Instrumentables managed by this
     *  InstrumentManager.
     *
     * @return An array of Descriptors for the Instrumentables managed by this
     *  InstrumentManager.
     */
    InstrumentableDescriptorLocal[] getInstrumentableDescriptorLocals();
}

