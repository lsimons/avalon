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
package org.apache.avalon.excalibur.component.example_im;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.apache.avalon.excalibur.component.ExcaliburComponentManagerCreator;
import org.apache.avalon.framework.service.ServiceManager;

import EDU.oswego.cs.dl.util.concurrent.CyclicBarrier;


/**
 * This example application loads a component which publishes a series
 *  of Instruments.  An InstrumentManager is created to collect and
 *  manage the Instrument data.  And an Altrmi based InstrumentManagerInterface
 *  is registered.  A client may connect to InstrumentManager later.
 * <p>
 * Note, this code ignores exceptions to keep the code simple.
 *
 * @author <a href="mailto:leif@apache.org">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 12:45:26 $
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

        // Get a reference to the service manager
        ServiceManager serviceManager = m_componentManagerCreator.getServiceManager();

        // Get a reference to the example component.
        ExampleInstrumentable instrumentable =
            (ExampleInstrumentable)serviceManager.lookup( ExampleInstrumentable.ROLE );
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
                        CyclicBarrier barrier = new CyclicBarrier( concurrent );
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
            serviceManager.release( instrumentable );
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
        private final CyclicBarrier m_barrier;

        protected ActionRunner( ExampleInstrumentable instrumentable, int numIterations, CyclicBarrier barrier )
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
                m_barrier.barrier();
            }
            catch( Exception e )
            {
            }
        }
    }
}

