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

import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.excalibur.instrument.CounterInstrument;
import org.apache.excalibur.instrument.Instrument;
import org.apache.excalibur.instrument.Instrumentable;
import org.apache.excalibur.instrument.ValueInstrument;

/**
 * This example application creates a component which registers several
 *  Instruments for the example.
 *
 * Note, this code ignores exceptions to keep the code simple.
 *
 * @author <a href="mailto:leif@apache.org">Leif Mortenson</a>
 * @version CVS $Revision: 1.1.1.1 $ $Date: 2003/11/09 12:44:16 $
 * @since 4.1
 */
public class DefaultExampleInstrumentable
    extends AbstractLogEnabled
    implements ExampleInstrumentable, Startable, Runnable, Instrumentable
{
    public static final String INSTRUMENT_RANDOM_QUICK_NAME = "random-quick";
    public static final String INSTRUMENT_RANDOM_SLOW_NAME = "random-slow";
    public static final String INSTRUMENT_RANDOM_RANDOM_NAME = "random-random";
    public static final String INSTRUMENT_COUNTER_QUICK_NAME = "counter-quick";
    public static final String INSTRUMENT_COUNTER_SLOW_NAME = "counter-slow";
    public static final String INSTRUMENT_COUNTER_RANDOM_NAME = "counter-random";
    public static final String INSTRUMENT_DOACTION_NAME = "doaction-counter";

    /** Instrumentable Name assigned to this Instrumentable */
    private String m_instrumentableName;

    /** Instrument used to profile random values with lots of updates. */
    private ValueInstrument m_randomQuickInstrument;

    /** Instrument used to profile random values with few of updates. */
    private ValueInstrument m_randomSlowInstrument;

    /** Instrument used to profile random values with updates at a random rate. */
    private ValueInstrument m_randomRandomInstrument;

    /** Instrument used to profile random actions with lots of updates. */
    private CounterInstrument m_counterQuickInstrument;

    /** Instrument used to profile random actions with few of updates. */
    private CounterInstrument m_counterSlowInstrument;

    /** Instrument used to profile random actions with updates at a random rate. */
    private CounterInstrument m_counterRandomInstrument;

    /** Instrument used to count the number of times that doAction is called. */
    private CounterInstrument m_doActionInstrument;

    /** Thread which is used to send profile data to the random instruments. */
    private Thread m_runner;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public DefaultExampleInstrumentable()
    {
        // Initialize the Instrumentable elements.
        m_randomQuickInstrument = new ValueInstrument( INSTRUMENT_RANDOM_QUICK_NAME );
        m_randomSlowInstrument = new ValueInstrument( INSTRUMENT_RANDOM_SLOW_NAME );
        m_randomRandomInstrument = new ValueInstrument( INSTRUMENT_RANDOM_RANDOM_NAME );
        m_counterQuickInstrument = new CounterInstrument( INSTRUMENT_COUNTER_QUICK_NAME );
        m_counterSlowInstrument = new CounterInstrument( INSTRUMENT_COUNTER_SLOW_NAME );
        m_counterRandomInstrument = new CounterInstrument( INSTRUMENT_COUNTER_RANDOM_NAME );
        m_doActionInstrument = new CounterInstrument( INSTRUMENT_DOACTION_NAME );
    }

    /*---------------------------------------------------------------
     * ExampleInstrumentable Methods
     *-------------------------------------------------------------*/
    /**
     * Example action method.
     */
    public void doAction()
    {
        getLogger().debug( "ExampleInstrumentable.doAction() called." );

        // Notify the profiler.
        m_doActionInstrument.increment();
    }

    /*---------------------------------------------------------------
     * Startable Methods
     *-------------------------------------------------------------*/
    /**
     * Start the component.
     */
    public void start()
    {
        if( m_runner == null )
        {
            m_runner = new Thread( this, "ExampleInstrumentableRunner" );
            m_runner.start();
        }
    }

    /**
     * Stop the component.
     */
    public void stop()
    {
        if( m_runner != null )
        {
            m_runner.interrupt();
            m_runner = null;
        }
    }

    /*---------------------------------------------------------------
     * Runnable Methods
     *-------------------------------------------------------------*/
    /**
     * Runner thread which is responsible for sending data to the Profiler via
     *  the various random Profile Points.
     */
    public void run()
    {
        int counter = 0;
        while( m_runner != null )
        {
            // Add some delay to the loop.
            try
            {
                Thread.sleep( 100 );
            }
            catch( InterruptedException e )
            {
                if( m_runner == null )
                {
                    return;
                }
            }

            // Handle the quick Profile Points
            m_randomQuickInstrument.setValue( (int)( Math.random() * 100 ) );
            m_counterQuickInstrument.increment();

            // Handle the slow Profile Points
            counter++;
            if( counter >= 20 )
            {
                m_randomSlowInstrument.setValue( (int)( Math.random() * 100 ) );
                m_counterSlowInstrument.increment();
                counter = 0;
            }

            // Handle the random Profile Points.  Fire 10% of the time.
            if( 100 * Math.random() < 10 )
            {
                m_randomRandomInstrument.setValue( (int)( Math.random() * 100 ) );
                m_counterRandomInstrument.increment();
            }
        }
    }

    /*---------------------------------------------------------------
     * Instrumentable Methods
     *-------------------------------------------------------------*/
    /**
     * Sets the name for the Instrumentable.  The Instrumentable Name is used
     *  to uniquely identify the Instrumentable during the configuration of
     *  the InstrumentManager and to gain access to an InstrumentableDescriptor
     *  through the InstrumentManager.  The value should be a string which does
     *  not contain spaces or periods.
     * <p>
     * This value may be set by a parent Instrumentable, or by the
     *  InstrumentManager using the value of the 'instrumentable' attribute in
     *  the configuration of the component.
     *
     * @param name The name used to identify a Instrumentable.
     */
    public void setInstrumentableName( String name )
    {
        m_instrumentableName = name;
    }

    /**
     * Gets the name of the Instrumentable.
     *
     * @return The name used to identify a Instrumentable.
     */
    public String getInstrumentableName()
    {
        return m_instrumentableName;
    }

    /**
     * Obtain a reference to all the Instruments that the Instrumentable object
     *  wishes to expose.  All sampling is done directly through the
     *  Instruments as opposed to the Instrumentable interface.
     *
     * @return An array of the Instruments available for profiling.  Should
     *         never be null.  If there are no Instruments, then
     *         EMPTY_INSTRUMENT_ARRAY can be returned.  This should never be
     *         the case though unless there are child Instrumentables with
     *         Instruments.
     */
    public Instrument[] getInstruments()
    {
        return new Instrument[]
        {
            m_randomQuickInstrument,
            m_randomSlowInstrument,
            m_randomRandomInstrument,
            m_counterQuickInstrument,
            m_counterSlowInstrument,
            m_counterRandomInstrument,
            m_doActionInstrument
        };
    }

    /**
     * Any Object which implements Instrumentable can also make use of other
     *  Instrumentable child objects.  This method is used to tell the
     *  InstrumentManager about them.
     *
     * @return An array of child Instrumentables.  This method should never
     *         return null.  If there are no child Instrumentables, then
     *         EMPTY_INSTRUMENTABLE_ARRAY can be returned.
     */
    public Instrumentable[] getChildInstrumentables()
    {
        // This instrumentable does not have any children.
        return Instrumentable.EMPTY_INSTRUMENTABLE_ARRAY;
    }
}

