/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument;

/**
 * Objects implementing Instrumentable can create Instruments with integer
 *  values using a ValueInstrument.  ValueInstruments are perfect for
 *  profiling things like system memory, or the size of a pool or cache.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.4 $ $Date: 2002/05/13 12:28:36 $
 * @since 4.1
 */
public class ValueInstrument
    extends AbstractInstrument
{
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new ValueInstrument.
     *
     * @param name The name of the Instrument.  The value should be a string
     *             which does not contain spaces or periods.
     */
    public ValueInstrument( String name )
    {
        super( name );
    }

    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Sets the current value of the Instrument.  This method is optimized
     *  to be extremely light weight when an InstrumentManager is not present
     *  and there are no registered ValueInstrumentListeners.
     *
     * @param value The new value for the Instrument.
     */
    public void setValue( int value )
    {
        InstrumentProxy proxy = getInstrumentProxy();
        if( proxy != null )
        {
            proxy.setValue( value );
        }
    }
}
