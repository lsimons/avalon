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
import org.apache.avalon.phoenix.Constants;
import org.apache.avalon.phoenix.components.embeddor.DefaultEmbeddor;
import org.apache.avalon.phoenix.interfaces.Embeddor;

/**
 * The class to load the kernel and start it running.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 */
public final class CLIMain
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( CLIMain.class );

    ///The embeddor attached to frontend
    private Embeddor      m_embeddor;

    ///The code to return to system using exit code
    private int           m_exitCode;

    private ShutdownHook  m_hook;

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
            handleException( throwable );
        }

        System.exit( m_exitCode );
    }

    /**
     * Actually create and execute the main component of embeddor.
     *
     * @exception Exception if an error occurs
     */
    private void execute( final Parameters parameters )
        throws Exception
    {
        if( false == startup( parameters ) )
        {
            return;
        }

        final boolean disableHook = parameters.getParameterAsBoolean( "disable-hook", false );
        if( false == disableHook )
        {
            m_hook = new ShutdownHook( this );
            Runtime.getRuntime().addShutdownHook( m_hook );
        }

        try
        {
            m_embeddor.execute();
        }
        catch( final Throwable throwable )
        {
            handleException( throwable );
        }
        finally
        {
            if( null != m_hook )
            {
                Runtime.getRuntime().removeShutdownHook( m_hook );
            }
            shutdown();
        }
    }

    /**
     * Startup the embeddor.
     */
    protected synchronized boolean startup( final Parameters parameters )
    {
        try
        {
            m_embeddor = new DefaultEmbeddor();
            //m_embeddor = new SingleAppEmbeddor();
            //parameters.setParameter( "application-location", "../apps/avalon-demo.sar" );
            
            if( m_embeddor instanceof Parameterizable )
            {
                ((Parameterizable)m_embeddor).parameterize( parameters );
            }
                
            m_embeddor.initialize();
        }
        catch( final Throwable throwable )
        {
            handleException( throwable );
            return false;
        }

        return true;
    }

    /**
     * Shut the embeddor down.
     */
    protected synchronized void forceShutdown()
    {
        final String message = REZ.getString( "main.abnormal-exit.notice" );
        System.out.println( message );
        System.out.flush();
        shutdown();
    }

    /**
     * Shut the embeddor down.
     */
    private synchronized void shutdown()
    {
        if( null != m_embeddor )
        {
            try
            {
                m_embeddor.dispose();
            }
            catch( final Throwable throwable )
            {
                handleException( throwable );
            }
            finally
            {
                m_embeddor = null;
            }
        }
    }

    /**
     * Print out exception and details to standard out.
     *
     * @param throwable the exception that caused failure
     */
    private void handleException( final Throwable throwable )
    {
        System.out.println( REZ.getString( "main.exception.header" ) );
        System.out.println( "---------------------------------------------------------" );
        System.out.println( "--- Message ---" );
        System.out.println( throwable.getMessage() );                
        System.out.println( "--- Stack Trace ---" );
        throwable.printStackTrace( System.out );
        System.out.println( "---------------------------------------------------------" );
        System.out.println( REZ.getString( "main.exception.footer" ) );

        m_exitCode = 1;
    }
}

final class ShutdownHook
    extends Thread
{
    private CLIMain   m_main;

    protected ShutdownHook( CLIMain main )
    {
        m_main = main;
    }

    /**
     * Run the shutdown hook.
     */
    public void run()
    {
        m_main.forceShutdown();
    }
}
