/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument.manager.interfaces;

/**
 * Objects which implement the InstrumentSampleListener interface can register
 *  themselves with InstrumentSample instances to receive updates on their value.
 *
 * @author <a href="mailto:leif@silveregg.co.jp">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/03/28 04:06:19 $
 * @since 4.1
 */
public interface InstrumentSampleListener
{
    /**
     * Called by a InstrumentSample whenever its value is changed.
     *
     * @param InstrumentSampleName The name of the InstrumentSample whose value was changed.
     * @param value The new value.
     * @param time The time that the InstrumentSample value was changed.
     */
    void setValue( String InstrumentSampleName, int value, long time );
}
