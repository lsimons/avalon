/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument;

/**
 * The Instrumentable interface is to mark objects that can be sampled by an
 *  InstrumentManager.  The getInstruments method may or may not be called
 *  depending on whether or not the ComponentManager used to create the
 *  Component supports Instrumentables.  In most cases, an instrumentable
 *  object should always create its internal Instruments and make use of them
 *  as if instrument data were being collected.  The Instruments are optimized
 *  so as not to reduce performance when they are not being used.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/04/03 13:48:49 $
 * @since 4.1
 */
public interface Instrumentable
{
    /**
     * Empty Instrument array for use in hierarchical Instrumentable systems.
     */
    Instrument[] EMPTY_INSTRUMENT_ARRAY = new Instrument[] {};
    
    /**
     * Empty Instrumentable array for use in hierarchical Instrumentable
     *  systems.
     */
    Instrumentable[] EMPTY_INSTRUMENTABLE_ARRAY = new Instrumentable[] {};
    
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
    void setInstrumentableName( String name );
    
    /**
     * Gets the name of the Instrumentable.
     *
     * @return The name used to identify a Instrumentable.
     */
    String getInstrumentableName();

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
    Instrument[] getInstruments();

    /**
     * Any Object which implements Instrumentable can also make use of other
     *  Instrumentable child objects.  This method is used to tell the
     *  InstrumentManager about them.
     *
     * @return An array of child Instrumentables.  This method should never
     *         return null.  If there are no child Instrumentables, then
     *         EMPTY_INSTRUMENTABLE_ARRAY can be returned.
     */
    Instrumentable[] getChildInstrumentables();
}

