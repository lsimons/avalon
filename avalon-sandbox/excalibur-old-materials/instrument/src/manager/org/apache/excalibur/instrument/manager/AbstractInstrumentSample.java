/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleListener;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleSnapshot;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 * An AbstractInstrumentSample contains all of the functionality common to all
 *  InstrumentSamples.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/07/29 16:05:20 $
 * @since 4.1
 */
abstract class AbstractInstrumentSample
    extends AbstractLogEnabled
    implements InstrumentSample
{
    /** The name of the new InstrumentSample. */
    private String m_name;

    /** The sample interval of the new InstrumentSample. */
    private long m_interval;

    /** The number of samples to store as history. */
    private int m_size;

    /** The description of the new InstrumentSample. */
    private String m_description;

    /** The Descriptor for the InstrumentSample. */
    private InstrumentSampleDescriptor m_descriptor;

    /**
     * The maximum amount of time between updates before history will be
     * wiped clean.
     */
    private long m_maxAge;

    /** The UNIX time of the beginning of the sample. */
    protected long m_time;

    /** The time that the current lease expires. */
    private long m_leaseExpirationTime;

    /** The Index into the history arrays. */
    private int m_historyIndex;

    /** The Old half of the history array. */
    private int[] m_historyOld;

    /** The New half of the history array. */
    private int[] m_historyNew;

    /** Array of registered InstrumentSampleListeners. */
    private InstrumentSampleListener[] m_listeners;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new AbstractInstrumentSample
     *
     * @param name The name of the new InstrumentSample.
     * @param interval The sample interval of the new InstrumentSample.
     * @param size The number of samples to store as history.  Assumes that size is at least 1.
     * @param description The description of the new InstrumentSample.
     * @param lease The length of the lease in milliseconds.
     */
    protected AbstractInstrumentSample( String name,
                                        long interval,
                                        int size,
                                        String description,
                                        long lease )
    {
        if ( interval < 1 )
        {
            throw new IllegalArgumentException( "interval must be at least 1." );
        }
        if ( size < 1 )
        {
            throw new IllegalArgumentException( "size must be at least 1." );
        }

        m_name = name;
        m_interval = interval;
        m_size = size;
        m_description = description;
        if ( lease > 0 )
        {
            m_leaseExpirationTime = System.currentTimeMillis() + lease;
        }
        else
        {
            // Permanent lease.
            m_leaseExpirationTime = 0;
        }

        // Calculate the maxAge
        m_maxAge = m_size * m_interval;

        init();

        // Create the descriptor
        m_descriptor = new InstrumentSampleDescriptorImpl( this );
    }

    /*---------------------------------------------------------------
     * InstrumentSample Methods
     *-------------------------------------------------------------*/
    /**
     * Returns true if the Instrument was configured in the instrumentables
     *  section of the configuration.
     *
     * @return True if configured.
     */
    public boolean isConfigured()
    {
        //return m_configured;
        return true;
    }

    /**
     * Returns the name of the sample.
     *
     * @return The name of the sample.
     */
    public final String getName()
    {
        return m_name;
    }

    /**
     * Returns the sample interval.  The period of each sample in millisends.
     *
     * @return The sample interval.
     */
    public final long getInterval()
    {
        return m_interval;
    }

    /**
     * Returns the number of samples in the sample history.
     *
     * @return The size of the sample history.
     */
    public final int getSize()
    {
        return m_size;
    }

    /**
     * Returns the description of the sample.
     *
     * @return The description of the sample.
     */
    public final String getDescription()
    {
        return m_description;
    }

    /**
     * Returns a Descriptor for the InstrumentSample.
     *
     * @return A Descriptor for the InstrumentSample.
     */
    public InstrumentSampleDescriptor getDescriptor()
    {
        return m_descriptor;
    }

    /**
     * Obtain the value of the sample.  All samples are integers, so the profiled
     * objects must measure quantity (numbers of items), rate (items/period), time in
     * milliseconds, etc.
     *
     * @return The sample value.
     */
    public final int getValue()
    {
        boolean update;
        int value;
        long time;

        synchronized(this)
        {
            long now = System.currentTimeMillis();
            update = update( now );
            value = getValueInner();
            time = m_time;
        }

        if ( update )
        {
            updateListeners( value, time );
        }
        return value;
    }

    /**
     * Obtain the UNIX time of the beginning of the sample.
     *
     * @return The UNIX time of the beginning of the sample.
     */
    public final long getTime()
    {
        boolean update;
        int value;
        long time;

        synchronized(this)
        {
            long now = System.currentTimeMillis();
            update = update( now );
            value = getValueInner();
            time = m_time;
        }

        if ( update )
        {
            updateListeners( value, time );
        }
        return time;
    }

    /**
     * Returns the time that the current lease expires.  Permanent samples will
     *  return a value of 0.
     *
     * @return The time that the current lease expires.
     */
    public long getLeaseExpirationTime()
    {
        return m_leaseExpirationTime;
    }

    /**
     * Extends the lease to be lease milliseconds from the current time.
     *
     * @param lease The length of the lease in milliseconds.
     */
    public void extendLease( long lease )
    {
        synchronized(this)
        {
            // Only extend the lease if it is not permanent.
            if ( m_leaseExpirationTime > 0 )
            {
                long newLeaseExpirationTime = System.currentTimeMillis() + lease;
                m_leaseExpirationTime = Math.max( m_leaseExpirationTime, newLeaseExpirationTime );
            }
        }
    }

    /**
     * Obtains a static snapshot of the InstrumentSample.
     *
     * @return A static snapshot of the InstrumentSample.
     */
    public final InstrumentSampleSnapshot getSnapshot()
    {
        synchronized(this)
        {
            long time = System.currentTimeMillis();
            update( time );

            return new InstrumentSampleSnapshot(
                m_name,
                m_interval,
                m_size,
                m_time,
                getHistorySnapshot() );
        }
    }

    /**
     * Registers a InstrumentSampleListener with a InstrumentSample given a name.
     *
     * @param listener The listener which should start receiving updates from the
     *                 InstrumentSample.
     */
    public void addInstrumentSampleListener( InstrumentSampleListener listener )
    {
        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "A InstrumentSampleListener was added to sample, " + m_name + " : " +
                listener.getClass().getName() );
        }

        synchronized(this)
        {
            // Store the listeners in an array.  This makes it possible to
            //  avoid synchronization while propagating events.  Never change
            //  the contents of the listener array once it has been set to the
            //  m_listeners field.
            InstrumentSampleListener[] oldListeners = m_listeners;
            InstrumentSampleListener[] newListeners;
            if ( oldListeners == null )
            {
                newListeners = new InstrumentSampleListener[] { listener };
            }
            else
            {
                newListeners = new InstrumentSampleListener[ oldListeners.length + 1 ];
                System.arraycopy( oldListeners, 0, newListeners, 0, oldListeners.length );
                newListeners[ oldListeners.length ] = listener;
            }

            // Update the m_listeners field.
            m_listeners = newListeners;
        }
    }

    /**
     * Unregisters a InstrumentSampleListener from a InstrumentSample given a name.
     *
     * @param listener The listener which should stop receiving updates from the
     *                 InstrumentSample.
     */
    public void removeInstrumentSampleListener( InstrumentSampleListener listener )
    {
        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "A InstrumentSampleListener was removed from sample, " + m_name +
                " : " + listener.getClass().getName() );
        }

        synchronized(this)
        {
            // Store the listeners in an array.  This makes it possible to
            //  avoid synchronization while propagating events.  Never change
            //  the contents of the listener array once it has been set to the
            //  m_listeners field.
            InstrumentSampleListener[] oldListeners = m_listeners;
            InstrumentSampleListener[] newListeners;
            if ( oldListeners == null )
            {
                // Means that this method should not have been called, but
                //  don't throw an error.
                newListeners = null;
            }
            else if ( oldListeners.length == 1 )
            {
                if ( oldListeners[0] == listener )
                {
                    newListeners = null;
                }
                else
                {
                    // The listener was not in the list.
                    newListeners = oldListeners;
                }
            }
            else
            {
                // Look for the listener in the array.
                int pos = -1;
                for ( int i = 0; i < oldListeners.length; i++ )
                {
                    if ( oldListeners[i] == listener )
                    {
                        pos = i;
                        break;
                    }
                }

                if ( pos < 0 )
                {
                    // The listener was not in the list.
                    newListeners = oldListeners;
                }
                else
                {
                    newListeners = new InstrumentSampleListener[ oldListeners.length - 1 ];
                    if ( pos > 0 )
                    {
                        // Copy the head of the array
                        System.arraycopy( oldListeners, 0, newListeners, 0, pos );
                    }
                    if ( pos < oldListeners.length - 1 )
                    {
                        // Copy the tail of the array
                        System.arraycopy( oldListeners, pos + 1,
                            newListeners, pos, oldListeners.length - 1 - pos );
                    }
                }
            }

            // Update the m_listeners field.
            m_listeners = newListeners;
        }
    }

    /**
     * Notifies any listeners of a change.
     * <p>
     * Make sure that this is called OUTSIDE of a synchrinization block.
     *
     * @param value The new value.
     * @param time The time that the new value was set.
     */
    protected void updateListeners( int value, long time )
    {
        // Get a local reference to the listeners, so that synchronization can be avoided.
        InstrumentSampleListener[] listeners = m_listeners;
        if ( listeners != null )
        {
            for ( int i = 0; i < listeners.length; i++ )
            {
                listeners[i].setValue( getName(), value, time );
            }
        }
    }

    /**
     * Saves the current state into a Configuration.
     *
     * @param useCompactSamples Flag for whether or not InstrumentSample data
     *                          should be saved in compact format or not.
     *
     * @return The state as a Configuration.
     */
    public final Configuration saveState( boolean useCompactSamples )
    {
        synchronized(this)
        {
            DefaultConfiguration state = new DefaultConfiguration( "profile-sample", "-" );
            state.setAttribute( "name", m_name );
            state.setAttribute( "time", Long.toString( m_time ) );

            // Save the history samples so that the newest is first.
            DefaultConfiguration samples = new DefaultConfiguration( "history", "-" );
            int[] history = getHistorySnapshot();
            if ( useCompactSamples )
            {
                StringBuffer sb = new StringBuffer();

                // Store the first value outside the loop to simplify the loop.
                sb.append( history[ history.length - 1 ] );
                for ( int i = history.length - 2; i >= 0; i-- )
                {
                    sb.append( ',' );
                    sb.append( history[ i ] );
                }

                samples.setValue( sb.toString() );
            }
            else
            {
                for ( int i = history.length - 1; i >= 0; i-- )
                {
                    DefaultConfiguration sample = new DefaultConfiguration( "sample", "-" );
                    sample.setValue( Integer.toString( history[i] ) );
                    samples.addChild( sample );
                }
            }
            state.addChild( samples );

            saveState( state );

            return state;
        }
    }

    /**
     * Loads the state into the InstrumentSample.
     *
     * @param state Configuration object to load state from.
     *
     * @throws ConfigurationException If there were any problems loading the
     *                                state.
     */
    public final void loadState( Configuration state ) throws ConfigurationException
    {
        synchronized( this )
        {
            // Set the time
            long savedTime = m_time = state.getAttributeAsLong( "time" );

            // Set the history index.
            m_historyIndex = 0;

            // Read in the samples, don't trust that the number will be correct.
            //  First sample is the current value, following sames go back in
            //   time from newest to oldest.
            Configuration history = state.getChild( "history" );

            Configuration samples[] = history.getChildren( "sample" );
            int[] sampleValues;
            if ( samples.length == 0 )
            {
                // No sample children.  The data may be stored in compact form
                String compactSamples = history.getValue();

                // Sample values are stored in newest to oldest order.
                StringTokenizer st = new StringTokenizer( compactSamples, "," );
                sampleValues = new int[st.countTokens()];

                for ( int i = 0; i < sampleValues.length; i++ )
                {
                    try
                    {
                        sampleValues[i] = Integer.parseInt( st.nextToken() );
                    }
                    catch ( NumberFormatException e )
                    {
                        throw new ConfigurationException( "The compact sample data could not be " +
                            "loaded, because of a number format problem, for InstrumentSample: " +
                            m_name );
                    }
                }
            }
            else
            {
                // Sample data stored as individual elements
                sampleValues = new int[ samples.length ];

                // Sample values are stored in newest to oldest order.
                for ( int i = 0; i < samples.length; i++ )
                {
                    sampleValues[i] = samples[ i ].getValueAsInteger();
                }
            }

            // Get the current value
            int value;
            if ( sampleValues.length > 0 )
            {
                value = sampleValues[0];

                for ( int i = 0; i < m_size - 1; i++ )
                {
                    if ( i < sampleValues.length - 1 )
                    {
                        m_historyOld[ m_size - 2 - i ] = sampleValues[ i + 1 ];
                    }
                    else
                    {
                        m_historyOld[ m_size - 2 - i ] = 0;
                    }
                }
            }
            else
            {
                value = 0;
            }

            loadState( value, state );

            if ( calculateSampleTime( System.currentTimeMillis() ) > savedTime )
            {
                // The sample period changed since the save.
                //  This will usually happen, but not always for long
                //  intervals.
                postSaveNeedsReset();
            }
        }
    }

    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Initializes the sample
     */
    private void init()
    {
        // Calculate an interval time based on the current time by removing the modulous
        //  value of the current time. This will allign the intervals to the start of computer
        //  time.
        m_time = calculateSampleTime( System.currentTimeMillis() );

        // Create the arrays which will hold the history points.
        // History is build with m_value holding the current value and all previous values
        // spanning accross 2 arrays that switch places as time progresses.  This completely
        // removes the need to manage large lists or do array copies.
        // All history values are 0 initially.
        m_historyIndex = 0;
        m_historyOld = new int[ m_size - 1 ];
        m_historyNew = new int[ m_size - 1 ];
    }

    /**
     * Allow subclasses to add information into the saved state.
     *
     * @param state State configuration.
     */
    protected void saveState( DefaultConfiguration state ) {}

    /**
     * Used to load the state, called from AbstractInstrumentSample.loadState();
     * <p>
     * Should only be called when synchronized.
     *
     * @param value Current value loaded from the state.
     * @param state Configuration object to load state from.
     *
     * @throws ConfigurationException If there were any problems loading the
     *                                state.
     */
    protected abstract void loadState( int value, Configuration state )
        throws ConfigurationException;

    /**
     * Called after a state is loaded if the sample period is not the same
     *  as the last period saved.
     */
    protected abstract void postSaveNeedsReset();

    /**
     * Calculates the time of the sample which contains the specified time.
     *
     * @param time Time whose sample time is requested.
     */
    private long calculateSampleTime( long time )
    {
        return ( time / m_interval ) * m_interval;
    }

    /**
     * Gets the current value.  Does not update.
     * <p>
     * Should only be called when synchronized.
     *
     * @return The current value.
     */
    protected abstract int getValueInner();

    /**
     * The current sample has already been stored.  Reset the current sample
     *  and move on to the next.
     * <p>
     * Should only be called when synchronized.
     */
    protected abstract void advanceToNextSample();

    /**
     * Brings the InstrumentSample's time up to date so that a new value can be added.
     * <p>
     * Should only be called when synchronized.
     *
     * @param time The time to which the InstrumentSample should be brought up to date.
     *
     * @return True if listeners should be notified.
     */
    protected boolean update( long time )
    {
        //System.out.println("update(" + time + ")");
        // See if we are already up to date.
        if ( time - m_time >= m_interval )
        {
            // Needs to move to a new sample.
            if ( time - m_time >= m_maxAge )
            {
                // The history is too old, reset the sample.
                advanceToNextSample();
                init();
            }
            else
            {
                // Advance the history index.
                while ( time - m_time >= m_interval )
                {
                    // Store the current value into the end of the history.
                    m_historyNew[ m_historyIndex ] = getValueInner();

                    // Advance to the next sample.
                    m_time += m_interval;
                    advanceToNextSample();
                    m_historyIndex++;

                    if ( m_historyIndex >= m_size - 1 )
                    {
                        // Need to swap the history arrays
                        int[] tmp = m_historyOld;
                        m_historyOld = m_historyNew;
                        m_historyNew = tmp;

                        // Reset the history index
                        m_historyIndex = 0;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets a snapshot of the samples.
     * <p>
     * Should only be called after an update when synchronized.
     *
     * @return A snapshot of the samples in the InstrumentSample.
     */
    private int[] getHistorySnapshot()
    {
        // Create a new array to hold the snapshot of the history data.
        // This method is a little slow but normal collection of sample points is
        // extremely fast.
        int[] history = new int[m_size];

        int sizem1 = m_size - 1;

        if ( m_size > 1 )
        {
            // Copy samples from the old history first.
            if ( m_historyIndex < sizem1 )
            {
                // Copy the last (size - 1 - historyIndex) samples from the old history.
                System.arraycopy( m_historyOld, m_historyIndex, history, 0, sizem1 - m_historyIndex );
            }

            if ( m_historyIndex > 0 )
            {
                // Copy the first (historyIndex) samples from the new history.
                System.arraycopy( m_historyNew, 0, history, sizem1 - m_historyIndex, m_historyIndex );
            }
        }
        // Get the final sample from the current sample value.
        history[ m_size - 1] = getValueInner();

        return history;
    }
}
