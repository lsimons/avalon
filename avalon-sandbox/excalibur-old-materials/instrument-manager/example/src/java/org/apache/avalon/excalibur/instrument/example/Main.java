/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.apache.avalon.excalibur.component.DefaultRoleManager;
import org.apache.avalon.excalibur.instrument.component.InstrumentComponentManager;
import org.apache.avalon.excalibur.instrument.manager.DefaultInstrumentManager;
import org.apache.avalon.excalibur.instrument.manager.altrmi.InstrumentManagerAltrmiServer;
import org.apache.avalon.excalibur.logger.DefaultLogKitManager;
import org.apache.avalon.excalibur.concurrent.ThreadBarrier;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.LogKitLogger;

import org.apache.log.Hierarchy;
import org.apache.log.Logger;
import org.apache.log.Priority;

/**
 * This example application loads a component which publishes a series
 *  of Instruments.  An InstrumentManager is created to collect and
 *  manage the Instrument data.  And an Altrmi based InstrumentManagerInterface
 *  is registered.  A client may connect to InstrumentManager later.
 * <p>
 * Note, this code ignores exceptions to keep the code simple.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/04/03 13:18:29 $
 * @since 4.1
 */
public class Main
{
    private static InstrumentComponentManager           m_componentManager;
    private static DefaultInstrumentManager             m_instrumentManager;
    private static InstrumentManagerAltrmiServer        m_altrmiServer;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    private Main() {}

    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Creates and initializes the component manager using config files.
     */
    private static void createComponentManager()
        throws Exception
    {
        // Create a context to use.
        DefaultContext context = new DefaultContext();
        // Add any context variables here.
        context.makeReadOnly();

        // Create a ConfigurationBuilder to parse the config files.
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();

        // Load in the configuration files
        Configuration logKitConfig     = builder.build( "../conf/logkit.xml" );
        Configuration instrumentConfig = builder.build( "../conf/instrument.xml" );
        Configuration rolesConfig      = builder.build( "../conf/roles.xml" );
        Configuration componentsConfig = builder.build( "../conf/components.xml" );

        // Setup the LogKitManager
        DefaultLogKitManager logManager = new DefaultLogKitManager();
        Logger lmLogger = Hierarchy.getDefaultHierarchy().
            getLoggerFor( logKitConfig.getAttribute( "logger", "lm" ) );
        lmLogger.setPriority(
            Priority.getPriorityForName( logKitConfig.getAttribute( "log-level", "INFO" ) ) );
        logManager.setLogger( lmLogger );
        logManager.configure( logKitConfig );

        // Set up the Instrument Manager
        m_instrumentManager = new DefaultInstrumentManager( "instrument-manager-example" );
        m_instrumentManager.enableLogging( new LogKitLogger( logManager.getLogger( instrumentConfig.getAttribute( "logger", "pm" ) ) ) );
        m_instrumentManager.configure( instrumentConfig );
        m_instrumentManager.initialize();

        // Setup the RoleManager
        DefaultRoleManager roleManager = new DefaultRoleManager();
        roleManager.setLogger(
            logManager.getLogger( rolesConfig.getAttribute( "logger", "rm" ) ) );
        roleManager.configure( rolesConfig );

        // Set up the ComponentManager
        m_componentManager = new InstrumentComponentManager();
        m_componentManager.setLogger(
            logManager.getLogger( componentsConfig.getAttribute( "logger", "cm" ) ) );
        m_componentManager.setLogKitManager( logManager );
        m_componentManager.contextualize( context );
        m_componentManager.setInstrumentManager( m_instrumentManager ); // <-- Set the instrument manager.
        m_componentManager.setRoleManager( roleManager );
        m_componentManager.configure( componentsConfig );
        m_componentManager.initialize();

        // Set up the InstrumentManagerAltrmiServer so clients can connect.
        m_altrmiServer = new InstrumentManagerAltrmiServer( m_instrumentManager );
        
        /*
        m_instrumentFrame = new InstrumentFrame( m_instrumentManager, "Example Instrument" );
        m_instrumentFrame.setVisible( true );

        try
        {
            File desktopFile = new File( "../conf/instrument.desktop" );
            m_instrumentFrame.loadDesktopStateFromFile( desktopFile, false );
        }
        catch ( Exception e )
        {
            System.out.println( "Unable to load desktop file: " + e );
        }
        */
    }

    /*---------------------------------------------------------------
     * Main method
     *-------------------------------------------------------------*/
    /**
     * All of the guts of this example exist in the main method.
     */
    public static void main( String[] args )
        throws Exception
    {
        System.out.println( "Running the AltProfile Example Application" );

        // Create the ComponentManager
        createComponentManager();

        // Get a reference to the example component.
        ExampleInstrumentable instrumentable =
            (ExampleInstrumentable)m_componentManager.lookup( ExampleInstrumentable.ROLE );
        try
        {
            boolean quit = false;
            while ( !quit )
            {
                System.out.println( "Enter the number of times that exampleAction should be called, or 'q' to quit." );
                BufferedReader in = new BufferedReader( new InputStreamReader( System.in ) );
                System.out.print( " : " );
                String cntStr = in.readLine();

                // Can get a null if CTRL-C is hit.
                if ( ( cntStr == null ) || ( cntStr.equalsIgnoreCase( "q" ) ) )
                {
                    quit = true;
                }
                else if ( ( cntStr.equalsIgnoreCase( "gc" ) ) )
                {
                    System.gc();
                }
                else
                {
                    try
                    {
                        int concurrent = 100;
                        ThreadBarrier barrier = new ThreadBarrier( concurrent );
                        int cnt = Integer.parseInt( cntStr );
                        int average = Math.max(cnt / concurrent, 1);

                        while (cnt > 0)
                        {
                            Thread t = new Thread( new ActionRunner( instrumentable,
                                                          Math.min(average, cnt),
                                                          barrier) );
                            t.start();

                            if (cnt > 0)
                            {
                                cnt -= average;
                            }

                            if (cnt < 0)
                            {
                                cnt = 0;
                            }
                        }
                    }
                    catch ( NumberFormatException e ) {}
                }
            }
        }
        finally
        {
            // Release the component
            m_componentManager.release( instrumentable );
            instrumentable = null;
            
            // Dispose the InstrumentManagerAltrmiServer
            m_altrmiServer.dispose();
            m_altrmiServer = null;

            /*
            // Hide the frame
            m_instrumentFrame.setVisible( false );
            m_instrumentFrame.dispose();
            m_instrumentFrame = null;
            */

            // Dispose the ComponentManager
            m_componentManager.dispose();
            m_componentManager = null;

            // Dispose the InstrumentManager
            m_instrumentManager.dispose();
            m_instrumentManager = null;
        }

        System.out.println();
        System.out.println( "Exiting..." );
        System.exit(0);
    }

    private final static class ActionRunner implements Runnable
    {
        private final int m_numIterations;
        private final ExampleInstrumentable m_instrumentable;
        private final ThreadBarrier m_barrier;

        protected ActionRunner( ExampleInstrumentable instrumentable, int numIterations, ThreadBarrier barrier )
        {
            m_numIterations = numIterations;
            m_instrumentable = instrumentable;
            m_barrier = barrier;
        }

        public void run()
        {
            for ( int i = 0; i < m_numIterations; i++ )
            {
                m_instrumentable.doAction();
            }

            try
            {
                m_barrier.barrierSynchronize();
            }
            catch (Exception e) {}
        }
    }
}

