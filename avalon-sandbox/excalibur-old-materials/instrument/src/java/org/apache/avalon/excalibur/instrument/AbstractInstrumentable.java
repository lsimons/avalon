/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument;

import java.util.ArrayList;

/**
 * Utility class to ease the construction of components that can be instrumented.
 * <p>
 * Subclasses should call <code>addInstrument</code> or
 *  <code>addChildInstrumentable</code> as part of the component's
 *  initialization.
 *
 * @author <a href="mailto:ryan.shaw@stanfordalumni.org">Ryan Shaw</a>
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 */
public abstract class AbstractInstrumentable
    implements Instrumentable
{
    /** Name of the instrumentable. */
    private String m_instrumentableName; 
    
    /** Stores the instruments during initialization. */
    private ArrayList m_instrumentList;
    
    /** Stores the child instrumentables during initialization. */
    private ArrayList m_childList;
    
    /** Flag which is to used to keep track of when the Instrumentable has been registered. */
    private boolean m_registered;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new AbstractInstrumentable.
     */
    protected AbstractInstrumentable()
    {
        m_registered = false;
        m_instrumentList = new ArrayList();
        m_childList = new ArrayList();
    }
    
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Adds an Instrument to the list of Instruments published by the component.
     *  This method may not be called after the Instrumentable has been
     *  registered with the InstrumentManager.
     *
     * @param instrument Instrument to publish.
     */
    protected void addInstrument( Instrument instrument )
    {
        if ( m_registered )
        {
            throw new IllegalStateException( "Instruments can not be added after the " +
                "Instrumentable is registered with the InstrumentManager." );
        }
        m_instrumentList.add( instrument );
    }
    
    /**
     * Adds a child Instrumentable to the list of child Instrumentables
     *  published by the component.  This method may not be called after the
     *  Instrumentable has been registered with the InstrumentManager.
     * <p>
     * Note that Child Instrumentables must be named by the caller using the
     *  setInstrumentableName method.
     *
     * @param child Child Instrumentable to publish.
     */
    protected void addChildInstrumentable( Instrumentable child )
    {
        if ( m_registered )
        {
            throw new IllegalStateException( "Child Instrumentables can not be added after the " +
                "Instrumentable is registered with the InstrumentManager." );
        }
        m_childList.add( child );
    }
    
    /*---------------------------------------------------------------
     * Instrumentable Methods
     *-------------------------------------------------------------*/
    /**
     * Gets the name of the Instrumentable.
     *
     * @return The name used to identify a Instrumentable.
     */
    public final String getInstrumentableName() 
    {
        return m_instrumentableName;
    }

    /**
     * Sets the name for the Instrumentable.  The Instrumentable Name is used
     *  to uniquely identify the Instrumentable during the configuration of
     *  the InstrumentManager and to gain access to an InstrumentableDescriptor
     *  through the InstrumentManager.  The value should be a string which does
     *  not contain spaces or periods.
     * <p>
     * This value may be set by a parent Instrumentable, or by the
     *  InstrumentManager using the value of the 'instrumentable' attribute in
     *  the configuration of the component.
     *
     * @param name The name used to identify a Instrumentable.
     */
    public final void setInstrumentableName(String name) 
    {
        m_instrumentableName = name;
    }
    
    /**
     * Any Object which implements Instrumentable can also make use of other
     *  Instrumentable child objects.  This method is used to tell the
     *  InstrumentManager about them.
     *
     * @return An array of child Instrumentables.  This method should never
     *         return null.  If there are no child Instrumentables, then
     *         EMPTY_INSTRUMENTABLE_ARRAY can be returned.
     */
    public final Instrumentable[] getChildInstrumentables() 
    {
        m_registered = true;
        if ( m_childList.size() == 0 )
        {
            return Instrumentable.EMPTY_INSTRUMENTABLE_ARRAY;
        }
        else
        {
            Instrumentable[] children = new Instrumentable[ m_childList.size() ];
            m_childList.toArray( children );
            return children;
        }
    }
    
    /**
     * Obtain a reference to all the Instruments that the Instrumentable object
     *  wishes to expose.  All sampling is done directly through the
     *  Instruments as opposed to the Instrumentable interface.
     *
     * @return An array of the Instruments available for profiling.  Should
     *         never be null.  If there are no Instruments, then
     *         EMPTY_INSTRUMENT_ARRAY can be returned.  This should never be
     *         the case though unless there are child Instrumentables with
     *         Instruments.
     */
    public final Instrument[] getInstruments() 
    {
        m_registered = true;
        if ( m_instrumentList.size() == 0 )
        {
            return Instrumentable.EMPTY_INSTRUMENT_ARRAY;
        }
        else
        {
            Instrument[] instruments = new Instrument[ m_instrumentList.size() ];
            m_instrumentList.toArray( instruments );
            return instruments;
        }
    }
}
