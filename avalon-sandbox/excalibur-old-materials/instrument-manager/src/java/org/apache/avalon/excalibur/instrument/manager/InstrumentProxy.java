/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.avalon.excalibur.instrument.manager.interfaces.CounterInstrumentListener;
import org.apache.avalon.excalibur.instrument.manager.interfaces.InstrumentDescriptor;
import org.apache.avalon.excalibur.instrument.manager.interfaces.InstrumentListener;
import org.apache.avalon.excalibur.instrument.manager.interfaces.InstrumentManagerClient;
import org.apache.avalon.excalibur.instrument.manager.interfaces.InstrumentSampleDescriptor;
import org.apache.avalon.excalibur.instrument.manager.interfaces.ValueInstrumentListener;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;

/**
 * Instrumentables which do not implement ThreadSafe may have multiple instances
 *  created by the ComponentManager.  Each of these Instruments will share
 *  a common key and are profiled as a group.  The InstrumentProxy is used
 *  make it easy for the InstrumentManager to control groups of Instruments
 *  as one.
 * <p>
 * The type of a Instrument can not be determined at configuration time.
 *  It is resolved when the Instrumentable actually registers the Instrument.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.6 $ $Date: 2002/04/28 17:05:41 $
 * @since 4.1
 */
public class InstrumentProxy
    extends AbstractLogEnabled
    implements org.apache.avalon.excalibur.instrument.InstrumentProxy, Configurable
{
    /** Configured flag. */
    private boolean m_configured;
    
    /** The name used to identify a Instrument. */
    private String m_name;
    
    /** The description of the Instrumente. */
    private String m_description;
    
    /** The Descriptor for the Instrument. */
    private InstrumentDescriptor m_descriptor;
    
    /** Type of the Instrument */
    private int m_type;
    
    /** Array of registered Counter/ValueInstrumentListeners. */
    private InstrumentListener[] m_listeners;
    
    /** Map of the maintained InstrumentSamples. */
    private HashMap m_samples = new HashMap();
    
    /** Optimized array of the InstrumentSamples. */
    private InstrumentSample[] m_sampleArray;
    
    /** Optimized array of the InstrumentSampleDescriptors. */
    private InstrumentSampleDescriptor[] m_sampleDescriptorArray;
    
    /** Child logger to use for logging of new values. */
    private Logger m_valueLogger;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new InstrumentProxy.
     *
     * @param name The name used to identify a Instrument.
     */
    InstrumentProxy( String name )
    {
        // Default description equals the name in case it is not set later.
        m_description = m_name = name;
        
        // Create the descriptor
        m_descriptor = new InstrumentDescriptorImpl( this );
    }
    
    /*---------------------------------------------------------------
     * LogEnabled Methods
     *-------------------------------------------------------------*/
    public void enableLogging( Logger logger )
    {
        super.enableLogging( logger );
        
        // Create a child logger for logging setValue and increment calls so
        //  that they can be filtered out.
        m_valueLogger = logger.getChildLogger( "values" );
    }
    
    /*---------------------------------------------------------------
     * Configurable Methods
     *-------------------------------------------------------------*/
    /**
     * Configures the Instrument.  Called from the InstrumentManager's
     *  configure method.  The class does not need to be configured to
     *  function correctly.
     *
     * @param configuration Instrument configuration element from the
     *                      InstrumentManager's configuration.
     *
     * @throws ConfigurationException If there are any configuration problems.
     */
    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        synchronized(this)
        {
            // The description is optional
            m_description = configuration.getAttribute( "description", m_name );
            
            if ( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Configuring Instrument: " + m_name + " as \"" +
                    m_description + "\"" );
            }
            
            m_configured = true;
            
            // Configure any Samples
            Configuration[] sampleConfs = configuration.getChildren( "sample" );
            for ( int i = 0; i < sampleConfs.length; i++ )
            {
                Configuration sampleConf = sampleConfs[i];
                
                int sampleType = InstrumentSampleFactory.resolveInstrumentSampleType(
                    sampleConf.getAttribute( "type" ) );
                long sampleInterval = sampleConf.getAttributeAsLong( "interval" );
                int sampleSize = sampleConf.getAttributeAsInteger( "size", 1 );
                
                // Build the sample name from its attributes.  This makes it
                //  possible to avoid forcing the user to maintain a name as well.
                String sampleName = 
                    generateSampleName( m_name, sampleType, sampleInterval, sampleSize );
                
                String sampleDescription = sampleConf.getAttribute( "description", sampleName );
                
                if ( getLogger().isDebugEnabled() )
                {
                    getLogger().debug( "Configuring InstrumentSample: " + sampleName + 
                        " as \"" + sampleDescription + "\"" );
                }
                
                InstrumentSample instrumentSample = InstrumentSampleFactory.getInstrumentSample(
                    sampleType, sampleName, sampleInterval, sampleSize, sampleDescription, 0 );
                instrumentSample.enableLogging( getLogger() );
                
                addInstrumentSample( instrumentSample );
            }
        }
    }
    
    /*---------------------------------------------------------------
     * InstrumentProxy Methods
     *-------------------------------------------------------------*/
    /**
     * Used by classes being profiles so that they can avoid unnecessary
     *  code when the data from a Instrument is not being used.
     *
     * @returns True if listeners are registered with the Instrument.
     */
    public boolean isActive() {
        return m_listeners != null;
    }
    
    /**
     * Increments the Instrument by a specified count.  This method should be
     *  optimized to be extremely light weight when there are no registered
     *  CounterInstrumentListeners.
     *
     * @param count A positive integer to increment the counter by.
     */
    public void increment( int count )
    {
        if ( m_type != InstrumentManagerClient.INSTRUMENT_TYPE_COUNTER )
        {
            // Type is not correct.
            throw new IllegalStateException(
                "The proxy is not configured to handle CounterInstruments." );
        }
        
        // Check the count
        if ( count <= 0 ) {
            throw new IllegalArgumentException( "Count must be a positive value." );
        }
        
        // Get a local reference to the listeners, so that synchronization can be avoided.
        InstrumentListener[] listeners = m_listeners;
        if ( listeners != null )
        {
            if ( m_valueLogger.isDebugEnabled() )
            {
                m_valueLogger.debug( "increment() called for Instrument, " + m_name );
            }
            
            long time = System.currentTimeMillis();
            for ( int i = 0; i < listeners.length; i++ )
            {
                CounterInstrumentListener listener =
                    (CounterInstrumentListener)listeners[i];
                listener.increment( getName(), count, time );
            }
        }
    }
    
    /**
     * Sets the current value of the Instrument.  This method is optimized
     *  to be extremely light weight when there are no registered
     *  ValueInstrumentListeners.
     *
     * @param value The new value for the Instrument.
     */
    public void setValue( int value )
    {
        if ( m_type != InstrumentManagerClient.INSTRUMENT_TYPE_VALUE )
        {
            // Type is not correct.
            throw new IllegalStateException(
                "The proxy is not configured to handle ValueInstruments." );
        }
        
        // Get a local reference to the listeners, so that synchronization can be avoided.
        InstrumentListener[] listeners = m_listeners;
        if ( listeners != null )
        {
            if ( m_valueLogger.isDebugEnabled() )
            {
                m_valueLogger.debug( "setValue( " + value + " ) called for Instrument, " + m_name );
            }
            
            long time = System.currentTimeMillis();
            for ( int i = 0; i < listeners.length; i++ )
            {
                ValueInstrumentListener listener =
                    (ValueInstrumentListener)listeners[i];
                listener.setValue( getName(), value, time );
            }
        }
    }
    
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Returns true if the Instrument was configured in the instrumentables
     *  section of the configuration.
     *
     * @return True if configured.
     */
    boolean isConfigured()
    {
        return m_configured;
    }
    
    /**
     * Gets the name for the Instrument.  The Instrument Name is used to
     *  uniquely identify the Instrument during the configuration of the
     *  Profiler and to gain access to a InstrumentDescriptor through a
     *  InstrumentManager.
     *
     * @return The name used to identify a Instrumentable.
     */
    String getName() 
    {
        return m_name;
    }
    
    /**
     * Sets the description for the Instrument.  This description will
     *  be set during the configuration of the profiler if a configuration
     *  exists for this Instrument.
     *
     * @param description The description of the Instrument.
     */
    void setDescription( String description )
    {
        m_description = description;
    }
    
    /**
     * Gets the description of the Instrument.
     *
     * @return The description of the Instrument.
     */
    String getDescription()
    {
        return m_description;
    }
    
    /**
     * Returns a Descriptor for the Instrument.
     *
     * @return A Descriptor for the Instrument.
     */
    InstrumentDescriptor getDescriptor()
    {
        return m_descriptor;
    }
    
    /**
     * Set the type of the Instrument.  Once set, the type can not be changed.
     *
     * @param type Type of the Instrument.
     */
    void setType( int type )
    {
        synchronized(this)
        {
            if ( m_type != InstrumentManagerClient.INSTRUMENT_TYPE_NONE )
            {
                throw new IllegalStateException( "Type already set." );
            }
            switch ( type )
            {
            case InstrumentManagerClient.INSTRUMENT_TYPE_COUNTER:
            case InstrumentManagerClient.INSTRUMENT_TYPE_VALUE:
                m_type = type;
                break;
            default:
                throw new IllegalStateException( type + " is not a valid type." );
            }
        }
    }
    
    /**
     * Returns the type of the Instrument.
     *
     * @return The type of the Instrument.
     */
    int getType()
    {
        return m_type;
    }
    
    /**
     * Adds a CounterInstrumentListener to the list of listeners which will
     *  receive updates of the value of the Instrument.
     *
     * @param listener CounterInstrumentListener which will start receiving
     *                 profile updates.
     *
     * @throws IllegalStateException If the Instrument's type is not
     *         InstrumentManager.INSTRUMENT_TYPE_COUNTER.
     */
    void addCounterInstrumentListener( CounterInstrumentListener listener )
    {
        if ( m_type != InstrumentManagerClient.INSTRUMENT_TYPE_COUNTER )
        {
            // Type is not correct.
            throw new IllegalStateException(
                "The proxy is not configured to handle CounterInstruments." );
        }
        
        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "A CounterInstrumentListener was added to Instrument, " +
                m_name + " : " + listener.getClass().getName() );
        }
        
        addInstrumentListener( listener );
    }
    
    /**
     * Removes a InstrumentListener from the list of listeners which will
     *  receive profile events.
     *
     * @param listener InstrumentListener which will stop receiving profile
     *                 events.
     *
     * @throws IllegalStateException If the Instrument's type is not
     *         InstrumentManager.INSTRUMENT_TYPE_COUNTER.
     */
    void removeCounterInstrumentListener( CounterInstrumentListener listener )
    {
        if ( m_type != InstrumentManagerClient.INSTRUMENT_TYPE_COUNTER )
        {
            // Type is not correct.
            throw new IllegalStateException(
                "The proxy is not configured to handle CounterInstruments." );
        }
        
        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "A CounterInstrumentListener was removed from Instrument, " + 
                m_name + " : " + listener.getClass().getName() );
        }
        
        removeInstrumentListener( listener );
    }
    
    /**
     * Adds a ValueInstrumentListener to the list of listeners which will
     *  receive updates of the value of the Instrument.
     *
     * @param listener ValueInstrumentListener which will start receiving
     *                 profile updates.
     *
     * @throws IllegalStateException If the Instrument's type is not
     *         InstrumentManager.INSTRUMENT_TYPE_VALUE.
     */
    void addValueInstrumentListener( ValueInstrumentListener listener )
    {
        if ( m_type != InstrumentManagerClient.INSTRUMENT_TYPE_VALUE )
        {
            // Type is not correct.
            throw new IllegalStateException(
                "The proxy is not configured to handle ValueInstruments." );
        }
        
        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "A ValueInstrumentListener was added to Instrument, " + m_name +
                " : " + listener.getClass().getName() );
        }
        
        addInstrumentListener( listener );
    }
    
    /**
     * Removes a InstrumentListener from the list of listeners which will
     *  receive profile events.
     *
     * @param listener InstrumentListener which will stop receiving profile
     *                 events.
     *
     * @throws IllegalStateException If the Instrument's type is not
     *         InstrumentManager.INSTRUMENT_TYPE_VALUE.
     */
    void removeValueInstrumentListener( ValueInstrumentListener listener )
    {
        if ( m_type != InstrumentManagerClient.INSTRUMENT_TYPE_VALUE )
        {
            // Type is not correct.
            throw new IllegalStateException(
                "The proxy is not configured to handle ValueInstruments." );
        }
        
        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "A ValueInstrumentListener was removed from Instrument, " + m_name +
                " : " + listener.getClass().getName() );
        }
        
        removeInstrumentListener( listener );
    }
    
    /**
     * Add a InstrumentSample to the Instrument.
     *
     * @param InstrumentSample InstrumentSample to be added.
     */
    public void addInstrumentSample( InstrumentSample InstrumentSample )
    {
        synchronized(this)
        {
            // If the type has not been set, set it.  If it has been set, make sure this sample has
            //  the same type.
            if ( m_type == InstrumentManagerClient.INSTRUMENT_TYPE_NONE )
            {
                setType( InstrumentSample.getInstrumentType() );
            }
            else if ( m_type != InstrumentSample.getInstrumentType() )
            {
                // The type is different.
                throw new IllegalStateException( "The sample '" + InstrumentSample.getName() + 
                    "' had its type set to " + getTypeName( m_type ) + 
                    " by another sample.  This sample has a type of " + 
                    getTypeName( InstrumentSample.getInstrumentType() ) + " and is not compatible." );
            }
            
            // Make sure that a sample with the same name has not already been set.
            String sampleName = InstrumentSample.getName();
            if ( m_samples.get( sampleName ) != null )
            {
                throw new IllegalStateException( "More than one sample with the same name, '" +
                    sampleName + "', can not be configured." );
            }
        
            // Sample is safe to add.
            m_samples.put( sampleName, InstrumentSample );
            
            // Clear the optimized arrays
            m_sampleArray = null;
            m_sampleDescriptorArray = null;
            
            // Add the sample as a listener for this Instrument.
            switch ( m_type )
            {
            case InstrumentManagerClient.INSTRUMENT_TYPE_COUNTER:
                addCounterInstrumentListener( (CounterInstrumentSample)InstrumentSample );
                break;
                
            case InstrumentManagerClient.INSTRUMENT_TYPE_VALUE:
                addValueInstrumentListener( (AbstractValueInstrumentSample)InstrumentSample );
                break;
                
            default:
                throw new IllegalStateException(
                    "Don't know how to deal with the type: " + m_type );
            }
        }
    }
    
    /**
     * Returns a InstrumentSample based on its name.
     *
     * @param InstrumentSampleName Name of the InstrumentSample being requested.
     *
     * @return The requested InstrumentSample or null if does not exist.
     */
    InstrumentSample getInstrumentSample( String InstrumentSampleName )
    {
        synchronized(this)
        {
            return (InstrumentSample)m_samples.get( InstrumentSampleName );
        }
    }
    
    /**
     * Returns an array of the InstrumentSamples in the Instrument.
     *
     * @return An array of the InstrumentSamples in the Instrument.
     */
    InstrumentSample[] getInstrumentSamples()
    {
        InstrumentSample[] samples = m_sampleArray;
        if ( samples == null )
        {
            samples = updateInstrumentSampleArray();
        }
        return samples;
    }
    
    /**
     * Returns a InstrumentSampleDescriptor based on its name.  If the requested
     *  sample is invalid in any way, then an expired Descriptor will be
     *  returned.
     *
     * @param sampleDescription Description to assign to the new Sample.
     * @param sampleInterval Sample interval to use in the new Sample.
     * @param sampleLease Requested lease time for the new Sample in
     *                    milliseconds.  The InstrumentManager may grant a
     *                    lease which is shorter or longer than the requested
     *                    period.
     * @param sampleType Type of sample to request.  Must be one of the
     *                   following:  InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_COUNTER,
     *                   InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MINIMUM,
     *                   InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MAXIMUM,
     *                   InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MEAN.
     *
     * @return A Descriptor of the requested InstrumentSample.
     *
     * @throws NoSuchInstrumentSampleException If the specified InstrumentSample
     *                                      does not exist.
     */
    InstrumentSampleDescriptor createInstrumentSample( String sampleDescription,
                                                       long sampleInterval,
                                                       int sampleSize,
                                                       long sampleLease,
                                                       int sampleType )
    {
        getLogger().info("Create new sample for " + m_name + ": interval=" + sampleInterval +
            ", size=" + sampleSize + ", lease=" + sampleLease + ", type=" +
            InstrumentSampleFactory.getInstrumentSampleTypeName( sampleType ) );
        
        // Validate the parameters
        long now = System.currentTimeMillis();
        
        // Generate a name for the new sample
        String sampleName = generateSampleName( m_name, sampleType, sampleInterval, sampleSize );
        
        synchronized( this )
        {
            // It is possible that the requested sample already exists.
            InstrumentSample instrumentSample = getInstrumentSample( sampleName );
            if ( instrumentSample != null )
            {
                // The requested sample already exists.
                instrumentSample.extendLease( sampleLease );
            }
            else
            {
                // The new sample needs to be created.
                instrumentSample = InstrumentSampleFactory.getInstrumentSample(
                    sampleType, sampleName, sampleInterval, sampleSize,
                    sampleDescription, sampleLease );
                instrumentSample.enableLogging( getLogger() );
                
                addInstrumentSample( instrumentSample );
            }
            
            return instrumentSample.getDescriptor();
        }
    }
    
    /**
     * Returns an array of Descriptors for the InstrumentSamples in the
     *  Instrument.
     *
     * @return An array of Descriptors for the InstrumentSamples in the
     *         Instrument.
     */
    InstrumentSampleDescriptor[] getInstrumentSampleDescriptors()
    {
        InstrumentSampleDescriptor[] descriptors = m_sampleDescriptorArray;
        if ( descriptors == null )
        {
            descriptors = updateInstrumentSampleDescriptorArray();
        }
        return descriptors;
    }
    
    /**
     * Common code to add a listener to the list of listeners which will
     *  receive updates of the value of the Instrument.
     *
     * @param listener InstrumentListener which will start receiving
     *                 profile updates.
     */
    private void addInstrumentListener( InstrumentListener listener )
    {
        synchronized(this)
        {
            // Store the listeners in an array.  This makes it possible to
            //  avoid synchronization while propagating events.  Never change
            //  the contents of the listener array once it has been set to the
            //  m_listeners field.
            InstrumentListener[] oldListeners = m_listeners;
            InstrumentListener[] newListeners;
            if ( oldListeners == null )
            {
                newListeners = new InstrumentListener[] { listener };
            }
            else
            {
                newListeners = new InstrumentListener[ oldListeners.length + 1 ];
                System.arraycopy( oldListeners, 0, newListeners, 0, oldListeners.length );
                newListeners[ oldListeners.length ] = listener;
            }
            
            // Update the m_listeners field.
            m_listeners = newListeners;
        }
    }
    
    /**
     * Common code to remove a listener from the list of listeners which will
     *  receive updates of the value of the Instrument.
     *
     * @param listener InstrumentListener which will stop receiving
     *                 profile updates.
     */
    private void removeInstrumentListener( InstrumentListener listener )
    {
        synchronized(this)
        {
            // Store the listeners in an array.  This makes it possible to
            //  avoid synchronization while propagating events.  Never change
            //  the contents of the listener array once it has been set to the
            //  m_listeners field.
            InstrumentListener[] oldListeners = m_listeners;
            InstrumentListener[] newListeners;
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
                    newListeners = new InstrumentListener[ oldListeners.length - 1 ];
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
     * Updates the cached array of InstrumentSamples taking synchronization into
     *  account.
     *
     * @return An array of the InstrumentSamples.
     */
    private InstrumentSample[] updateInstrumentSampleArray()
    {
        synchronized(this)
        {
            m_sampleArray = new InstrumentSample[ m_samples.size() ];
            m_samples.values().toArray( m_sampleArray );
            
            // Sort the array.  This is not a performance problem because this
            //  method is rarely called and doing it here saves cycles in the
            //  client.
            Arrays.sort( m_sampleArray, new Comparator()
                {
                    public int compare( Object o1, Object o2 )
                    {
                        return ((InstrumentSample)o1).getDescription().
                            compareTo( ((InstrumentSample)o2).getDescription() );
                    }
                    
                    public boolean equals( Object obj )
                    {
                        return false;
                    }
                } );
            
            return m_sampleArray;
        }
    }
    
    /**
     * Updates the cached array of InstrumentSampleDescriptors taking
     *  synchronization into account.
     *
     * @return An array of the InstrumentSampleDescriptors.
     */
    private InstrumentSampleDescriptor[] updateInstrumentSampleDescriptorArray()
    {
        synchronized(this)
        {
            if ( m_sampleArray == null )
            {
                updateInstrumentSampleArray();
            }
            
            m_sampleDescriptorArray =
                new InstrumentSampleDescriptor[ m_sampleArray.length ];
            for ( int i = 0; i < m_sampleArray.length; i++ )
            {
                m_sampleDescriptorArray[i] = m_sampleArray[i].getDescriptor();
            }
            
            return m_sampleDescriptorArray;
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
    Configuration saveState( boolean useCompactSamples )
    {
        DefaultConfiguration state = new DefaultConfiguration( "instrument", "-" );
        state.addAttribute( "name", m_name );
        
        InstrumentSample[] samples = getInstrumentSamples();
        for ( int i = 0; i < samples.length; i++ )
        {
            state.addChild( samples[i].saveState( useCompactSamples ) );
        }
        
        return state;
    }
    
    /**
     * Loads the state into the Instrument.
     *
     * @param state Configuration object to load state from.
     *
     * @throws ConfigurationException If there were any problems loading the
     *                                state.
     */
    void loadState( Configuration state ) throws ConfigurationException
    {
        synchronized( this )
        {
            Configuration[] InstrumentSampleConfs = state.getChildren( "sample" );
            for ( int i = 0; i < InstrumentSampleConfs.length; i++ )
            {
                Configuration InstrumentSampleConf = InstrumentSampleConfs[i];
                String name = InstrumentSampleConf.getAttribute( "name" );
                InstrumentSample sample = getInstrumentSample( name );
                if ( sample == null )
                {
                    getLogger().warn( "InstrumentSample entry ignored while loading state because the " +
                        "sample does not exist: " + name );
                }
                else
                {
                    sample.loadState( InstrumentSampleConf );
                }
            }
        }
    }
    
    /**
     * Returns the name of a Instrument type.
     *
     * @param type Type whose name is wanted.
     *
     * @return The name of a Instrument type.
     */
    public static String getTypeName( int type )
    {
        switch ( type )
        {
        case InstrumentManagerClient.INSTRUMENT_TYPE_NONE:
            return "none";
        case InstrumentManagerClient.INSTRUMENT_TYPE_COUNTER:
            return "counter";
        case InstrumentManagerClient.INSTRUMENT_TYPE_VALUE:
            return "value";
        default:
            throw new IllegalArgumentException( type + " is not a known Instrument type." );
        }
    }
    
    /**
     * Generates a sample name given its parameters.
     *
     * @param instrumentName Name of the instrument which owns the sample.
     */
    private String generateSampleName( String instrumentName,
                                       int sampleType,
                                       long sampleInterval,
                                       int sampleSize )
    {
        return instrumentName + "." +
            InstrumentSampleFactory.getInstrumentSampleTypeName( sampleType ) + "." + 
            sampleInterval + "." + sampleSize;
    }
}
