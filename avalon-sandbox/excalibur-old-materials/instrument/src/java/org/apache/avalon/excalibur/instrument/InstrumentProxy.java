/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument;

/**
 * Because some components using Instruments will be created in large numbers
 *  a way is needed to collect data from the instances of all instances of a
 *  component class without maintaining references to Instruments of each
 *  instance.  An Instrument Manager can do this by making use of Instrument
 *  Proxies.  Each Instrument is assigned a proxy when it is registered with
 *  the manager, then all communication is made through the proxy
 * The Instrument interface must by implemented by any object wishing to act
 *  as an instrument used by the instrument manager.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.3 $ $Date: 2002/04/03 13:48:49 $
 * @since 4.1
 */
public interface InstrumentProxy
{
    /**
     * Used by classes being profiles so that they can avoid unnecessary
     *  code when the data from a Instrument is not being used.
     *
     * @return True if listeners are registered with the Instrument.
     */
    boolean isActive();
    
    /**
     * Increments the Instrument.  This method should be optimized to be extremely
     *  light weight when there are no registered CounterInstrumentListeners.
     * <p>
     * This method may throw an IllegalStateException if the proxy is not meant
     *  to handle calls to increment.
     */
    void increment();
    
    /**
     * Sets the current value of the Instrument.  This method is optimized
     *  to be extremely light weight when there are no registered
     *  ValueInstrumentListeners.
     * <p>
     * This method may throw an IllegalStateException if the proxy is not meant
     *  to handle calls to setValue.
     *
     * @param value The new value for the Instrument.
     */
    void setValue( int value );
}