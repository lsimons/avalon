/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.client;

import java.io.File;

import org.apache.avalon.framework.logger.ConsoleLogger;

/**
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/08/22 16:50:38 $
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
        String defaultStateFileName;
        if ( args.length > 0 )
        {
            defaultStateFileName = args[0];
        }
        else
        {
            defaultStateFileName = "../conf/default.desktop";
        }
        File defaultStateFile = new File( defaultStateFileName );
        
        InstrumentClientFrame client = new InstrumentClientFrame( "Instrument Client" );
        client.enableLogging( new ConsoleLogger( ConsoleLogger.LEVEL_DEBUG ) );
        client.setDefaultStateFile( defaultStateFile );
        client.show();
    }
}

