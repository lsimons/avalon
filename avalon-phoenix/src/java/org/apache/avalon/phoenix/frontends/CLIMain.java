/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.frontends;

import org.apache.avalon.excalibur.cli.CLArgsParser;
import org.apache.avalon.excalibur.cli.CLOption;
import org.apache.avalon.excalibur.cli.CLOptionDescriptor;
import org.apache.avalon.excalibur.cli.CLUtil;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.phoenix.components.embeddor.DefaultEmbeddor;
import org.apache.avalon.phoenix.Constants;

/**
 * The class to load the kernel and start it running.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 */
public class CLIMain
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( CLIMain.class );

    /**
     * Main entry point.
     *
     * @param args[] the command line arguments
     */
    public void main( final String args[] )
    {
        final CLIMain main = new CLIMain();

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
            System.out.println( REZ.getString( "main.exception.header" ) );
            System.out.println( "---------------------------------------------------------" );
            throwable.printStackTrace( System.out );
            System.out.println( "---------------------------------------------------------" );
            System.out.println( REZ.getString( "main.exception.footer" ) );
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
        final DefaultEmbeddor embeddor = new DefaultEmbeddor();
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
        catch( final Throwable throwable )
        {
            System.out.println( REZ.getString( "main.exception.header" ) );
            System.out.println( "---------------------------------------------------------" );
            throwable.printStackTrace( System.out );
            System.out.println( "---------------------------------------------------------" );
            System.out.println( REZ.getString( "main.exception.footer" ) );
        }
        finally
        {
            embeddor.stop();
            embeddor.dispose();
        }
    }
}
