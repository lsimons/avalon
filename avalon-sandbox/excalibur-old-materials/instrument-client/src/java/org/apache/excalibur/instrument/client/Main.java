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
 * @version CVS $Revision: 1.4 $ $Date: 2002/11/05 02:59:04 $
 * @since 4.1
 */
public class Main
{
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    private static void showUsage()
    {
        System.out.println( "Usage:");
        System.out.println( "java -classpath {classpath} org.apache.excalibur.instrument.client.Main [-debug] [state file]" );
        System.out.println();
        System.out.println( "    -debug     - Enables debug output." );
        System.out.println( "    state file - Name of a state file to read at startup.  Defaults to: ../conf/default.desktop" );
        System.out.println();
    }
    
    
    /*---------------------------------------------------------------
     * Main Method
     *-------------------------------------------------------------*/
    /**
     * Main method used to lauch an InstrumentClient application.
     */
    public static void main( String args[] )
    {
        // Parse the command line.  Want to replace this with something more powerful later.
        boolean debug = false;
        String defaultStateFileName = "../conf/default.desktop";
        switch( args.length )
        {
        case 0:
            break;
            
        case 1:
            if ( args[0].equalsIgnoreCase( "-debug" ) )
            {
                debug = true;
            }
            else
            {
                defaultStateFileName = args[0];
            }
            break;
            
        case 2:
            if ( args[0].equalsIgnoreCase( "-debug" ) )
            {
                debug = true;
            }
            else
            {
                showUsage();
                System.exit( 1 );
            }
            defaultStateFileName = args[0];
            break;
            
        default:
            showUsage();
            System.exit( 1 );
        }
        
        File defaultStateFile = new File( defaultStateFileName );
        
        InstrumentClientFrame client = new InstrumentClientFrame( "Instrument Client" );
        int logLevel = ( debug ? ConsoleLogger.LEVEL_DEBUG : ConsoleLogger.LEVEL_INFO );
        client.enableLogging( new ConsoleLogger( logLevel ) );
        client.setDefaultStateFile( defaultStateFile );
        client.show();
    }
}

