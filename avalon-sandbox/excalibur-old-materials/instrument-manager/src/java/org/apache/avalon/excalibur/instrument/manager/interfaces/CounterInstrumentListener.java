/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument.manager.interfaces;

/**
 * Objects which implement the CounterInstrumentListener interface can
 *  register themselves with Instrument instances to receive updates when
 *  it is incremented.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/04/03 13:18:30 $
 * @since 4.1
 */
public interface CounterInstrumentListener
    extends InstrumentListener
{
    /**
     * Called by a CounterInstrument whenever its value is incremented.
     *
     * @param instrumentName The name of Instrument which was incremented.
     * @param time The time that the Instrument was incremented.
     */
    void increment( String instrumentName, long time );
}
