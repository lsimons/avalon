/*
    * Copyright (C) The Apache Software Foundation. All rights reserved.
    *
    * This software is published under the terms of the Apache Software License
    * version 1.1, a copy of which has been included with this distribution in
    * the LICENSE.txt file.
    */
package org.apache.excalibur.instrument.test;

import org.apache.excalibur.instrument.AbstractInstrumentable;
import org.apache.excalibur.instrument.Instrument;
import org.apache.excalibur.instrument.Instrumentable;

/**
 * Test Implementation of an AbstractInstrumentable.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/09/26 06:34:53 $
 */
public class AbstractInstrumentableImpl
    extends AbstractInstrumentable
{
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public AbstractInstrumentableImpl( String name )
    {
        setInstrumentableName( name );
    }
    
    /*---------------------------------------------------------------
     * AbstractInstrumentable Methods
     *-------------------------------------------------------------*/
    /**
     * Adds an Instrument to the list of Instruments published by the component.
     *  This method may not be called after the Instrumentable has been
     *  registered with the InstrumentManager.
     *
     * @param instrument Instrument to publish.
     */
    public void addInstrument( Instrument instrument )
    {
        // Make this method public for testing.
        super.addInstrument( instrument );
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
    public void addChildInstrumentable( Instrumentable child )
    {
        // Make this method public for testing.
        super.addChildInstrumentable( child );
    }
}