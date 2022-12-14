/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 
 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:
 
 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.
 
 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.
 
 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.
 
 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"  
    must not be used to endorse or promote products derived from this  software 
    without  prior written permission. For written permission, please contact 
    apache@apache.org.
 
 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.
 
 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the 
 Apache Software Foundation, please see <http://www.apache.org/>.
 
*/
package org.apache.excalibur.instrument.client;

import java.io.File;

import org.apache.avalon.framework.logger.ConsoleLogger;

/**
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.8 $ $Date: 2003/03/22 12:46:38 $
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
            defaultStateFileName = args[1];
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

