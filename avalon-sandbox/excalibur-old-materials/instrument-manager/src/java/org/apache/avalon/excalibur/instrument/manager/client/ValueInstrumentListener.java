/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument.manager.client;

/**
 * Objects which implement the InstrumentListener interface can register
 *  themselves with Instrument instances to receive updates on the
 *  Profile Point's value.
 *
 * @author <a href="mailto:leif@silveregg.co.jp">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/03/26 11:56:17 $
 * @since 4.1
 */
public interface ValueInstrumentListener
    extends InstrumentListener
{
    /**
     * Called by a ValueInstrument whenever its value is set.
     *
     * @param instrumentName The name of Instrument whose value was set.
     * @param value Value that was set.
     * @param time The time that the Instrument was incremented.
     *
     * ValueInstrument
     */
    void setValue( String instrumentName, int value, long time );
}
