/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument.client;

/**
 *
 * @author <a href="mailto:leif@silveregg.co.jp">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/03/26 11:32:24 $
 * @since 4.1
 */
public class Main
{
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    
    /*---------------------------------------------------------------
     * Main Method
     *-------------------------------------------------------------*/
    /**
     * Main method used to lauch an InstrumentClient application.
     */
    public static void main( String args[] )
    {
        InstrumentClientFrame client = new InstrumentClientFrame( "Instrument Client" );
        client.show();
    }
}

