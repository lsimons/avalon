/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument;

/**
 * The AbstractInstrument class can be used by an class wishing to implement
 *  the Instruement interface.
 *
 * @author <a href="mailto:leif@silveregg.co.jp">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/03/26 11:17:21 $
 * @since 4.1
 */
public abstract class AbstractInstrument
    implements Instrument
{
    /** The name of the Instrument. */
    private String m_name;
    
    /** Proxy object used to communicate with the InstrumentManager. */
    protected InstrumentProxy m_proxy;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new AbstractInstrument.
     *
     * @param name The name of the Instrument.  The value should be a string
     *             which does not contain spaces or periods.
     */
    protected AbstractInstrument( String name )
    {
        m_name = name;
    }
    
    /*---------------------------------------------------------------
     * Instrument Methods
     *-------------------------------------------------------------*/
    /**
     * Gets the name for the Instrument.  When an Instrumentable publishes more
     *  than one Instrument, this name makes it possible to identify each
     *  Instrument.  The value should be a string which does not contain
     *  spaces or periods.
     *
     * @return The name of the Instrument.
     */
    public String getInstrumentName()
    {
        return m_name;
    }
    
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * When the InstrumentManager is present, an InstrumentProxy will be set
     *  to enable the Instrument to communicate with the InstrumentManager.
     *  Once the InstrumentProxy is set, it should never be changed or set
     *  back to null.  This restriction removes the need for synchronization
     *  within the Instrument classes.  Which in turn makes them more
     *  efficient.
     *
     * @param proxy Proxy object used to communicate with the
     *              InstrumentManager.
     */
    public void setInstrumentProxy( InstrumentProxy proxy )
    {
        if ( m_proxy != null )
        {
            throw new IllegalStateException(
                "Once an InstrumentProxy has been set, it can not be changed." );
        }
        m_proxy = proxy;
    }
    
    /**
     * Used by classes being profiled so that they can avoid unnecessary
     *  code when the data from an Instrument is not being used.
     *
     * @returns True if an InstrumentProxy has been set and is active.
     */
    public boolean isActive()
    {
        return ( m_proxy != null ) && ( m_proxy.isActive() );
    }
}
