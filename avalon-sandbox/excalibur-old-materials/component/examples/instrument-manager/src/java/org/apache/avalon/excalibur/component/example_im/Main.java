/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.component.example_im;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import org.apache.avalon.excalibur.component.ExcaliburComponentManagerCreator;
import org.apache.avalon.excalibur.concurrent.ThreadBarrier;
import org.apache.avalon.framework.component.ComponentManager;

//import org.apache.avalon.framework.configuration.Configuration;
//import org.apache.avalon.framework.configuration.ConfigurationException;
//import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
//import org.apache.avalon.framework.context.DefaultContext;
//import org.apache.avalon.framework.logger.LogKitLogger;

//import org.apache.excalibur.instrument.manager.DefaultInstrumentManager;

//import org.apache.log.Hierarchy;
//import org.apache.log.Logger;
//import org.apache.log.Priority;

/**
 * This example application loads a component which publishes a series
 *  of Instruments.  An InstrumentManager is created to collect and
 *  manage the Instrument data.  And an Altrmi based InstrumentManagerInterface
 *  is registered.  A client may connect to InstrumentManager later.
 * <p>
 * Note, this code ignores exceptions to keep the code simple.
 *
 * @author <a href="mailto:leif@apache.org">Leif Mortenson</a>
 * @version CVS $Revision: 1.3 $ $Date: 2002/11/07 05:11:51 $
 * @since 4.1
 */
public class Main
{
    private static ExcaliburComponentManagerCreator m_componentManagerCreator;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    private Main()
    {
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
        System.out.println( "Running the InstrumentManager Example Application" );

        // Create the ComponentManager using the ExcaliburComponentManagerCreator
        //  utility class.  See the contents of that class if you wish to do the
        //  initialization yourself.
        m_componentManagerCreator = new ExcaliburComponentManagerCreator( null,
                                                                          new File( "../conf/logkit.xml" ), new File( "../conf/roles.xml" ),
                                                                          new File( "../conf/components.xml" ), new File( "../conf/instrument.xml" ) );

        // Get a reference to the component manager
        ComponentManager componentManager = m_componentManagerCreator.getComponentManager();

        // Get a reference to the example component.
        ExampleInstrumentable instrumentable =
            (ExampleInstrumentable)componentManager.lookup( ExampleInstrumentable.ROLE );
        try
        {
            boolean quit = false;
            while( !quit )
            {
                System.out.println( "Enter the number of times that exampleAction should be "
                                    + "called, or 'q' to quit." );
                BufferedReader in = new BufferedReader( new InputStreamReader( System.in ) );
                System.out.print( " : " );
                String cntStr = in.readLine();

                // Can get a null if CTRL-C is hit.
                if( ( cntStr == null ) || ( cntStr.equalsIgnoreCase( "q" ) ) )
                {
                    quit = true;
                }
                else if( ( cntStr.equalsIgnoreCase( "gc" ) ) )
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
                        int average = Math.max( cnt / concurrent, 1 );

                        while( cnt > 0 )
                        {
                            Thread t = new Thread( new ActionRunner( instrumentable,
                                                                     Math.min( average, cnt ),
                                                                     barrier ) );
                            t.start();

                            if( cnt > 0 )
                            {
                                cnt -= average;
                            }

                            if( cnt < 0 )
                            {
                                cnt = 0;
                            }
                        }
                    }
                    catch( NumberFormatException e )
                    {
                    }
                }
            }
        }
        finally
        {
            // Release the component
            componentManager.release( instrumentable );
            instrumentable = null;

            // Dispose of the ComponentManagerCreator.  It will dispose all
            //  of its own components, including the ComponentManager
            m_componentManagerCreator.dispose();
        }

        System.out.println();
        System.out.println( "Exiting..." );
        System.exit( 0 );
    }

    private static final class ActionRunner implements Runnable
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
            for( int i = 0; i < m_numIterations; i++ )
            {
                m_instrumentable.doAction();
            }

            try
            {
                m_barrier.barrierSynchronize();
            }
            catch( Exception e )
            {
            }
        }
    }
}

     }
    }
}

