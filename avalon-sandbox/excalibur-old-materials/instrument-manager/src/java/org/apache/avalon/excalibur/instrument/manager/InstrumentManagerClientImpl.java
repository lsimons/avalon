/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument.manager;

import org.apache.avalon.excalibur.instrument.manager.interfaces.InstrumentableDescriptor;
import org.apache.avalon.excalibur.instrument.manager.interfaces.InstrumentManagerClient;
import org.apache.avalon.excalibur.instrument.manager.interfaces.NoSuchInstrumentableException;

import org.apache.avalon.framework.configuration.Configuration;

/**
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.3 $ $Date: 2002/04/03 13:18:29 $
 * @since 4.1
 */
public class InstrumentManagerClientImpl
    implements InstrumentManagerClient
{
    private DefaultInstrumentManager m_manager;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public InstrumentManagerClientImpl( DefaultInstrumentManager manager )
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
        return m_manager.getInstrumentableDescriptor( instrumentableName );
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
        return m_manager.getInstrumentableDescriptors();
    }
    
    /**
     * Invokes garbage collection.
     */
    public void invokeGarbageCollection()
    {
        m_manager.invokeGarbageCollection();
    }
    
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
}

