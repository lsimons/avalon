/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.engine;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.excalibur.cli.CLArgsParser;
import org.apache.avalon.excalibur.cli.CLOption;
import org.apache.avalon.excalibur.cli.CLOptionDescriptor;
import org.apache.avalon.excalibur.cli.CLUtil;

/**
 * The class to load the kernel and start it running.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 */
public class Main
{
    /**
     * Main entry point.
     *
     * @param args[] the command line arguments
     */
    public void main( final String args[] )
    {
        final Main main = new Main();

        try
        { 
            final String command = "java " + getClass().getName() + " [options]";
            final CLISetup setup = new CLISetup( command );
            
            if( false == setup.parseCommandLineOptions( args ) )
            {
                return;
            }

            System.out.println();
            System.out.println( Constants.SOFTWARE + " " + Constants.VERSION );
            System.out.println();
            
            main.execute( setup.getParameters() ); 
        }
        catch( final Throwable throwable )
        {
            System.out.println( "There was an uncaught exception:" );
            System.out.println( "---------------------------------------------------------" );
            throwable.printStackTrace( System.out );
            System.out.println( "---------------------------------------------------------" );
            System.out.println( "The log file may contain further details of error." );
            System.out.println( "Please check the configuration files and restart phoenix." );
            System.out.println( "If the problem persists, contact the Avalon project.  See" );
            System.out.println( "http://jakarta.apache.org/avalon for more information." );
            System.exit( 1 );
        }

        System.exit( 0 );
    }

    /**
     * Actually create and execute the main component of embeddor.
     *
     * @exception Exception if an error occurs
     */
    private void execute( final Parameters parameters )
        throws Exception
    {
        final PhoenixEmbeddor embeddor = new PhoenixEmbeddor();
        //final SingleAppEmbeddor embeddor = new SingleAppEmbeddor();
        //parameters.setParameter( "application-location", "../apps/avalon-demo.sar" );

        if( embeddor instanceof Parameterizable )
        {
            ((Parameterizable)embeddor).parameterize( parameters );
        }

        embeddor.initialize();
        embeddor.start();

        try
        {
            embeddor.execute();
        }
        finally
        {
            //embeddor.stop();
            //embeddor.dispose();
        }
    }
}
