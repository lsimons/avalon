/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument;

/**
 * The Instrument interface must by implemented by any object wishing to act
 *  as an instrument used by the instrument manager.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/07/29 16:05:19 $
 * @since 4.1
 */
public interface Instrument
{
    /**
     * Gets the name for the Instrument.  When an Instrumentable publishes more
     *  than one Instrument, this name makes it possible to identify each
     *  Instrument.  The value should be a string which does not contain
     *  spaces or periods.
     *
     * @return The name of the Instrument.
     */
    String getInstrumentName();
}
