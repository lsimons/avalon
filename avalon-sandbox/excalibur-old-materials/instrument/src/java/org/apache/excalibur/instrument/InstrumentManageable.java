/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument;

/**
 * Components which implement the InstrumentManageable Interface will have
 *  their InstrumentManager set by the component manager during their
 *  initialization phase.  Components which create child components needs to
 *  implement this interface in order for Instruments to be supported within
 *  those child components.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/09/15 10:11:58 $
 * @since 4.0
 */
public interface InstrumentManageable
{
    /**
     * Sets the InstrumentManager for child components.  Can be for special
     * purpose components, however it is used mostly internally.
     *
     * @param instrumentManager The InstrumentManager for the component to use.
     */
    void setInstrumentManager( InstrumentManager instrumentManager );
}
