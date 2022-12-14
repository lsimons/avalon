/* 
 * Copyright 2002-2004 The Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.excalibur.instrument.manager;

import java.util.StringTokenizer;
import java.util.Calendar;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleSnapshot;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleUtils;

/**
 * An AbstractInstrumentSample contains all of the functionality common to all
 *  InstrumentSamples.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.5 $ $Date: 2004/02/28 11:47:25 $
 * @since 4.1
 */
abstract class AbstractInstrumentSample
    extends AbstractLogEnabled
    implements InstrumentSample
{
    /** Stores the time-zone offset for this JVM. */
    private static long m_zoneOffset;
    
    /** The InstrumentProxy which owns the InstrumentSample. */
    private InstrumentProxy m_instrumentProxy;

    /** Configured flag. */
    private boolean m_configured;

    /** The name of the new InstrumentSample. */
    private String m_name;

    /** The sample interval of the new InstrumentSample. */
    private long m_interval;

    /** The number of samples to store as history. */
    private int m_size;

    /** The description of the new InstrumentSample. */
    private String m_description;

    /** The Descriptor for the InstrumentSample. */
    private InstrumentSampleDescriptorLocal m_descriptor;

    /**
     * The maximum amount of time between updates before history will be
     * wiped clean.
     */
    private long m_maxAge;

    /** The UNIX time of the beginning of the sample. */
    protected long m_time;

    /** The time that the current lease expires. */
    private long m_leaseExpirationTime;

    /** True if the lease has expired. */
    private boolean m_expired;

    /** The Index into the history arrays. */
    private int m_historyIndex;

    /** The Old half of the history array. */
    private int[] m_historyOld;

    /** The New half of the history array. */
    private int[] m_historyNew;

    /** Array of registered InstrumentSampleListeners. */
    private InstrumentSampleListener[] m_listeners;

    /** State Version. */
    private int m_stateVersion;

    /*---------------------------------------------------------------
     * Static Initializer
     *-------------------------------------------------------------*/
    static
    {
        Calendar now = Calendar.getInstance();
        m_zoneOffset = now.get( Calendar.ZONE_OFFSET );
    }
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new AbstractInstrumentSample
     *
     * @param instrumentProxy The InstrumentProxy which owns the
     *                        InstrumentSample.
     * @param name The name of the new InstrumentSample.
     * @param interval The sample interval of the new InstrumentSample.
     * @param size The number of samples to store as history.  Assumes that
     *             size is at least 1.
     * @param description The description of the new InstrumentSample.
     * @param lease The length of the lease in milliseconds.
     */
    protected AbstractInstrumentSample( InstrumentProxy instrumentProxy,
                                        String name,
                                        long interval,
                                        int size,
                                        String description,
                                        long lease )
    {
        m_instrumentProxy = instrumentProxy;

        if( interval < 1 )
        {
            throw new IllegalArgumentException( "interval must be at least 1." );
        }
        if( size < 1 )
        {
            throw new IllegalArgumentException( "size must be at least 1." );
        }

        m_name = name;
        m_interval = interval;
        m_size = size;
        m_description = description;
        if( lease > 0 )
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
        m_descriptor = new InstrumentSampleDescriptorLocalImpl( this );
    }

    /*---------------------------------------------------------------
     * InstrumentSample Methods
     *-------------------------------------------------------------*/
    /**
     * Returns the InstrumentProxy which owns the InstrumentSample.
     *
     * @return The InstrumentProxy which owns the InstrumentSample.
     */
    public InstrumentProxy getInstrumentProxy()
    {
        return m_instrumentProxy;
    }

    /**
     * Returns true if the Instrument was configured in the instrumentables
     *  section of the configuration.
     *
     * @return True if configured.
     */
    public boolean isConfigured()
    {
        return m_configured;
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
    public InstrumentSampleDescriptorLocal getDescriptor()
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

        synchronized( this )
        {
            long now = System.currentTimeMillis();
            update = update( now );
            value = getValueInner();
            time = m_time;
        }

        if( update )
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

        synchronized( this )
        {
            long now = System.currentTimeMillis();
            update = update( now );
            value = getValueInner();
            time = m_time;
        }

        if( update )
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
     *  Ignored if the lease has already expired.
     *
     * @param lease The length of the lease in milliseconds.
     *
     * @return The new lease expiration time.  Returns 0 if the sample is
     *         permanent.
     */
    public long extendLease( long lease )
    {
        synchronized( this )
        {
            // Only extend the lease if it is not permanent.
            if( ( m_leaseExpirationTime > 0 ) && ( !m_expired ) )
            {
                long newLeaseExpirationTime = System.currentTimeMillis() + lease;
                if( newLeaseExpirationTime > m_leaseExpirationTime )
                {
                    m_leaseExpirationTime = newLeaseExpirationTime;
                    stateChanged();
                }
            }

            return m_leaseExpirationTime;
        }
    }

    /**
     * Tells the sample that its lease has expired.  No new references to
     *  the sample will be made available, but clients which already have
     *  access to the sample may continue to use it.
     */
    public void expire()
    {
        // Update to the time that we expire at.
        update( m_leaseExpirationTime );

        m_expired = true;
    }

    /**
     * Obtains a static snapshot of the InstrumentSample.
     *
     * @return A static snapshot of the InstrumentSample.
     */
    public final InstrumentSampleSnapshot getSnapshot()
    {
        synchronized( this )
        {
            long time = System.currentTimeMillis();
            update( time );

            return new InstrumentSampleSnapshot(
                m_name,
                m_interval,
                m_size,
                m_time,
                getHistorySnapshot(),
                m_stateVersion );
        }
    }

    /**
     * Returns the stateVersion of the sample.  The state version will be
     *  incremented each time any of the configuration of the sample is
     *  modified.
     * Clients can use this value to tell whether or not anything has
     *  changed without having to do an exhaustive comparison.
     *
     * @return The state version of the sample.
     */
    public int getStateVersion()
    {
        return m_stateVersion;
    }

    /**
     * Registers a InstrumentSampleListener with a InstrumentSample given a name.
     *
     * @param listener The listener which should start receiving updates from the
     *                 InstrumentSample.
     */
    public void addInstrumentSampleListener( InstrumentSampleListener listener )
    {
        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "A InstrumentSampleListener was added to sample, " + m_name + " : " +
                               listener.getClass().getName() );
        }

        synchronized( this )
        {
            // Store the listeners in an array.  This makes it possible to
            //  avoid synchronization while propagating events.  Never change
            //  the contents of the listener array once it has been set to the
            //  m_listeners field.
            InstrumentSampleListener[] oldListeners = m_listeners;
            InstrumentSampleListener[] newListeners;
            if( oldListeners == null )
            {
                newListeners = new InstrumentSampleListener[]{listener};
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
        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "A InstrumentSampleListener was removed from sample, " + m_name +
                               " : " + listener.getClass().getName() );
        }

        synchronized( this )
        {
            // Store the listeners in an array.  This makes it possible to
            //  avoid synchronization while propagating events.  Never change
            //  the contents of the listener array once it has been set to the
            //  m_listeners field.
            InstrumentSampleListener[] oldListeners = m_listeners;
            InstrumentSampleListener[] newListeners;
            if( oldListeners == null )
            {
                // Means that this method should not have been called, but
                //  don't throw an error.
                newListeners = null;
            }
            else if( oldListeners.length == 1 )
            {
                if( oldListeners[ 0 ] == listener )
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
                for( int i = 0; i < oldListeners.length; i++ )
                {
                    if( oldListeners[ i ] == listener )
                    {
                        pos = i;
                        break;
                    }
                }

                if( pos < 0 )
                {
                    // The listener was not in the list.
                    newListeners = oldListeners;
                }
                else
                {
                    newListeners = new InstrumentSampleListener[ oldListeners.length - 1 ];
                    if( pos > 0 )
                    {
                        // Copy the head of the array
                        System.arraycopy( oldListeners, 0, newListeners, 0, pos );
                    }
                    if( pos < oldListeners.length - 1 )
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
        if( listeners != null )
        {
            for( int i = 0; i < listeners.length; i++ )
            {
                listeners[ i ].setValue( getName(), value, time );
            }
        }
    }

    /**
     * Saves the current state into a Configuration.
     *
     * @return The state as a Configuration.  Returns null if the configuration
     *         would not contain any information.
     */
    public final Configuration saveState()
    {
        // If this sample is not configured and its lease time is 0, then it
        //  is an artifact of a previous state file, so it should not be saved.
        if( ( !isConfigured() ) && ( getLeaseExpirationTime() <= 0 ) )
        {
            return null;
        }

        synchronized( this )
        {
            DefaultConfiguration state = new DefaultConfiguration( "sample", "-" );
            state.setAttribute( "type",
                                InstrumentSampleUtils.getInstrumentSampleTypeName( getType() ) );
            state.setAttribute( "interval", Long.toString( m_interval ) );
            state.setAttribute( "size", Integer.toString( m_size ) );

            state.setAttribute( "time", Long.toString( m_time ) );
            if( getLeaseExpirationTime() > 0 )
            {
                state.setAttribute( "lease-expiration", Long.toString( getLeaseExpirationTime() ) );
                state.setAttribute( "description", m_description );
            }

            // Save the history samples so that the newest is first.
            DefaultConfiguration samples = new DefaultConfiguration( "history", "-" );
            int[] history = getHistorySnapshot();

            // Build up a string of the sample points.
            StringBuffer sb = new StringBuffer();
            // Store the first value outside the loop to simplify the loop.
            sb.append( history[ history.length - 1 ] );
            for( int i = history.length - 2; i >= 0; i-- )
            {
                sb.append( ',' );
                sb.append( history[ i ] );
            }
            samples.setValue( sb.toString() );
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

            // Load the lease expiration time
            m_leaseExpirationTime = state.getAttributeAsLong( "lease-expiration", 0 );

            // Set the history index.
            m_historyIndex = 0;

            // Read in the samples, don't trust that the number will be correct.
            //  First sample is the current value, following sames go back in
            //   time from newest to oldest.
            Configuration history = state.getChild( "history" );

            String compactSamples = history.getValue();

            // Sample values are stored in newest to oldest order.
            StringTokenizer st = new StringTokenizer( compactSamples, "," );
            int[] sampleValues = new int[ st.countTokens() ];

            for( int i = 0; i < sampleValues.length; i++ )
            {
                try
                {
                    sampleValues[ i ] = Integer.parseInt( st.nextToken() );
                }
                catch( NumberFormatException e )
                {
                    throw new ConfigurationException( "The compact sample data could not be " +
                                                      "loaded, because of a number format problem, for InstrumentSample: " +
                                                      m_name );
                }
            }

            // Get the current value
            int value;
            if( sampleValues.length > 0 )
            {
                value = sampleValues[ 0 ];

                for( int i = 0; i < m_size - 1; i++ )
                {
                    if( i < sampleValues.length - 1 )
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

            if( calculateSampleTime( System.currentTimeMillis() ) > savedTime )
            {
                // The sample period changed since the save.
                //  This will usually happen, but not always for long
                //  intervals.
                postSaveNeedsReset();
            }

            if( m_leaseExpirationTime > 0 )
            {
                // This is a sample that was leased in a previous JVM invocation
                //  and needs to be registered with the InstrumentManager
                getInstrumentProxy().getInstrumentableProxy().getInstrumentManager().
                    registerLeasedInstrumentSample( this );
            }
        }

        stateChanged();
    }

    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Sets the configured flag.
     */
    void setConfigured()
    {
        m_configured = true;
    }

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
    protected void saveState( DefaultConfiguration state )
    {
    }

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
        // We want this to round to the nearest interval.  For intervals
        //  over an hour, the current time zone needs to be taken into
        //  account so the interval will be alligned correctly.
        // The timezone offset is calculated once when the class is loaded.
        long offset = ( time + m_zoneOffset ) % m_interval;
        return time - offset;
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
        // If the lease has already expired, then do nothing
        if( m_expired )
        {
            return false;
        }

        // See if we are already up to date.
        if( time - m_time >= m_interval )
        {
            // Needs to move to a new sample.
            if( time - m_time >= m_maxAge )
            {
                // The history is too old, reset the sample.
                advanceToNextSample();
                init();
            }
            else
            {
                // Advance the history index.
                while( time - m_time >= m_interval )
                {
                    // Store the current value into the end of the history.
                    m_historyNew[ m_historyIndex ] = getValueInner();

                    // Advance to the next sample.
                    m_time += m_interval;
                    advanceToNextSample();
                    m_historyIndex++;

                    if( m_historyIndex >= m_size - 1 )
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
        }
        else
        {
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
        int[] history = new int[ m_size ];

        int sizem1 = m_size - 1;

        if( m_size > 1 )
        {
            // Copy samples from the old history first.
            if( m_historyIndex < sizem1 )
            {
                // Copy the last (size - 1 - historyIndex) samples from the old history.
                System.arraycopy( m_historyOld, m_historyIndex, history, 0, sizem1 - m_historyIndex );
            }

            if( m_historyIndex > 0 )
            {
                // Copy the first (historyIndex) samples from the new history.
                System.arraycopy( m_historyNew, 0, history, sizem1 - m_historyIndex, m_historyIndex );
            }
        }
        // Get the final sample from the current sample value.
        history[ m_size - 1 ] = getValueInner();

        return history;
    }

    /**
     * Called whenever the state of the sample is changed.
     */
    protected void stateChanged()
    {
        m_stateVersion++;

        // Propagate to the parent
        m_instrumentProxy.stateChanged();
    }

    /**
     * Returns a string representation of the sample.
     *
     * @return A string representation of the sample.
     */
    public String toString()
    {
        return "InstrumentSample[name=" + m_name + ", type=" +
            InstrumentSampleUtils.getInstrumentSampleTypeName( getType() ) + ", interval=" +
            m_interval + ", size=" + m_size + ", lease=" + m_leaseExpirationTime + "]";
    }
}
