/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument;

/**
 * CounterInstruments can be used to profile the number of times that
 *  something happens.  They are perfect for profiling things like the number
 *  of times a class instance is created or destroyed.  Or the number of
 *  times that a method is accessed.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/07/29 16:05:19 $
 * @since 4.1
 */
public class CounterInstrument
    extends AbstractInstrument
{
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new CounterInstrument.
     *
     * @param name The name of the Instrument.  The value should be a string
     *             which does not contain spaces or periods.
     */
    public CounterInstrument( String name )
    {
        super( name );
    }

    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Increments the Instrument.  This method is optimized to be extremely
     *  light weight when an InstrumentManager is not present and there are no
     *  registered CounterInstrumentListeners.
     */
    public void increment()
    {
        InstrumentProxy proxy = getInstrumentProxy();
        if( proxy != null )
        {
            proxy.increment( 1 );
        }
    }

    /**
     * Increments the Instrument by a specified count.  This method is
     *  optimized to be extremely light weight when an InstrumentManager is not
     *  present and there are no registered CounterInstrumentListeners.
     *
     * @param count A positive integer to increment the counter by.
     */
    public void increment( int count )
    {
        InstrumentProxy proxy = getInstrumentProxy();
        if( proxy != null )
        {
            proxy.increment( count );
        }
    }
}
