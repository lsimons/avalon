/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.manager.interfaces;

/**
 * Thrown when a Instrument can not be found.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/07/29 16:05:21 $
 * @since 4.1
 */
public class NoSuchInstrumentException
    extends RuntimeException
{
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Construct a new NoSuchInstrumentException instance.
     *
     * @param message The detail message for this exception.
     */
    public NoSuchInstrumentException( String message )
    {
        super( message );
    }
}
