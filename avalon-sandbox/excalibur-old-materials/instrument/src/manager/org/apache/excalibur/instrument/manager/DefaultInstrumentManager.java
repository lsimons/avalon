/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import org.apache.excalibur.instrument.AbstractInstrument;
import org.apache.excalibur.instrument.CounterInstrument;
import org.apache.excalibur.instrument.Instrument;
import org.apache.excalibur.instrument.Instrumentable;
import org.apache.excalibur.instrument.InstrumentManager;
import org.apache.excalibur.instrument.ValueInstrument;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentableDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentManagerClient;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentableException;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/07/29 16:05:20 $
 * @since 4.1
 */
public class DefaultInstrumentManager
    extends AbstractLogEnabled
    implements Configurable, Initializable, Disposable, InstrumentManager,
        Instrumentable, Runnable
{
    public static final String INSTRUMENT_TOTAL_MEMORY = "total-memory";
    public static final String INSTRUMENT_FREE_MEMORY = "free-memory";
    public static final String INSTRUMENT_MEMORY = "memory";
    public static final String INSTRUMENT_ACTIVE_THREAD_COUNT = "active-thread-count";

    /** The name used to identify this InstrumentManager. */
    private String m_name;

    /** The description of this InstrumentManager. */
    private String m_description;

    /** Configuration for the InstrumentManager */
    private Configuration m_configuration;

    /** State file. */
    private File m_stateFile;

    /** Save state interval. */
    private long m_stateInterval;

    /** Use a compact format when saving profile sample data. */
    private boolean m_stateCompactSamples;

    /** Last time that the state was saved. */
    private long m_lastStateSave;

    /** Semaphore for actions which must be synchronized */
    private Object m_semaphore = new Object();

    /** HashMap of all of the registered InstrumentableProxies by their keys. */
    private HashMap m_instrumentableProxies = new HashMap();

    /** Optimized array of the InstrumentableProxies. */
    private InstrumentableProxy[] m_instrumentableProxyArray;

    /** Optimized array of the InstrumentableDescriptors. */
    private InstrumentableDescriptor[] m_instrumentableDescriptorArray;

    /**
     * Thread used to keep the instruments published by the InstrumentManager
     *  up to date.
     */
    private Thread m_runner;

    /** Instrumentable Name assigned to this Instrumentable */
    private String m_instrumentableName = "instrument-manager";

    /** Instrument used to profile the total memory. */
    private ValueInstrument m_totalMemoryInstrument;

    /** Instrument used to profile the free memory. */
    private ValueInstrument m_freeMemoryInstrument;

    /** Instrument used to profile the in use memory. */
    private ValueInstrument m_memoryInstrument;

    /** Instrument used to profile the active thread count of the JVM. */
    private ValueInstrument m_activeThreadCountInstrument;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new DefaultInstrumentManager.
     *
     * @param name The name used to identify this InstrumentManager.  Should not
     *             contain any spaces or periods.
     */
    public DefaultInstrumentManager( String name )
    {
        m_name = name;
        // The description defaults to the name.
        m_description = name;

        // Initialize the Instrumentable elements.
        m_totalMemoryInstrument = new ValueInstrument( INSTRUMENT_TOTAL_MEMORY );
        m_freeMemoryInstrument = new ValueInstrument( INSTRUMENT_FREE_MEMORY );
        m_memoryInstrument = new ValueInstrument( INSTRUMENT_MEMORY );
        m_activeThreadCountInstrument = new ValueInstrument( INSTRUMENT_ACTIVE_THREAD_COUNT );
    }

    /*---------------------------------------------------------------
     * Configurable Methods
     *-------------------------------------------------------------*/
    /**
     * Initializes the configured instrumentables.
     *
     * @param configuration InstrumentManager configuration.
     *
     * @throws ConfigurationException If there are any configuration problems.
     */
    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        synchronized( m_semaphore )
        {
            m_configuration = configuration;

            // Configure the instrumentables.
            Configuration instrumentablesConf =
                configuration.getChild( "instrumentables" );
            Configuration[] instrumentableConfs =
                instrumentablesConf.getChildren( "instrumentable" );
            for( int i = 0; i < instrumentableConfs.length; i++ )
            {
                Configuration instrumentableConf = instrumentableConfs[ i ];
                String instrumentableName = instrumentableConf.getAttribute( "name" );

                InstrumentableProxy instrumentableProxy =
                    new InstrumentableProxy( instrumentableName );
                instrumentableProxy.enableLogging( getLogger() );
                instrumentableProxy.configure( instrumentableConf );
                m_instrumentableProxies.put( instrumentableName, instrumentableProxy );

                // Clear the optimized arrays
                m_instrumentableProxyArray = null;
                m_instrumentableDescriptorArray = null;
            }

            // Configure the state file.
            Configuration stateFileConf = configuration.getChild( "state-file" );
            m_stateInterval = stateFileConf.getAttributeAsLong( "interval", 60000 );
            m_stateCompactSamples =
                stateFileConf.getAttributeAsBoolean( "use-compact-samples", true );

            String stateFile = stateFileConf.getValue( null );
            if( stateFile != null )
            {
                m_stateFile = new File( stateFile );
                if( m_stateFile.exists() )
                {
                    /*
                    try
                    {
                        loadStateFromFile( m_stateFile );
                    }
                    catch( Exception e )
                    {
                        getLogger().error(
                            "Unable to load the instrument manager state.  The configuration " +
                            "may have been corruptped.  A backup may have been made in the same " +
                            "directory when it was saved.", e );
                    }
                    */
                }
            }
        }
    }

    /*---------------------------------------------------------------
     * Initializable Methods
     *-------------------------------------------------------------*/
    /**
     * Initializes the InstrumentManager.
     *
     * @throws Exception If there were any problems initializing the object.
     */
    public void initialize()
        throws Exception
    {
        // Register the InstrumentManager as an Instrumentable.
        registerInstrumentable( this, getInstrumentableName() );

        if( m_runner == null )
        {
            m_runner = new Thread( this, "InstrumentManagerRunner" );
            m_runner.start();
        }
    }

    /*---------------------------------------------------------------
     * Disposable Methods
     *-------------------------------------------------------------*/
    /**
     * Disposes the InstrumentManager.
     */
    public void dispose()
    {
        if( m_runner != null )
        {
            m_runner = null;
        }

        saveState();
    }

    /*---------------------------------------------------------------
     * InstrumentManager Methods
     *-------------------------------------------------------------*/
    /**
     * Instrumentable to be registered with the instrument manager.  Should be
     *  called whenever an Instrumentable is created.
     *
     * @param instrumentable Instrumentable to register with the InstrumentManager.
     * @param instrumentableName The name to use when registering the Instrumentable.
     *
     * @throws Exception If there were any problems registering the Instrumentable.
     */
    public void registerInstrumentable( Instrumentable instrumentable, String instrumentableName )
        throws Exception
    {
        getLogger().debug( "Registering Instrumentable: " + instrumentableName );

        synchronized( m_semaphore )
        {
            // If the instrumentable does not implement ThreadSafe, then it is possible that
            //  another one of its instance was already registered.  If so, then the
            //  Instruments will all be the same.  The new instances still need to be
            //  registered however.
            InstrumentableProxy instrumentableProxy =
                (InstrumentableProxy)m_instrumentableProxies.get( instrumentableName );
            if( instrumentableProxy == null )
            {
                // This is a Instrumentable that has not been seen before.
                instrumentableProxy = new InstrumentableProxy( instrumentableName );
                instrumentableProxy.enableLogging( getLogger() );
                // Do not call configure here because there is no configuration
                //  for discovered instrumentables.
                m_instrumentableProxies.put( instrumentableName, instrumentableProxy );

                // Clear the optimized arrays
                m_instrumentableProxyArray = null;
                m_instrumentableDescriptorArray = null;

                // Recursively register all the Instruments in this and any child Instrumentables.
                registerInstruments( instrumentable, instrumentableProxy, instrumentableName );
            }
            else
            {
                // Additional Instrumentable instance.  Possible that new Instruments could be found.
                registerInstruments( instrumentable, instrumentableProxy, instrumentableName );
            }
        }
    }


    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Returns the name used to identify this InstrumentManager.
     *
     * @return The name used to identify this InstrumentManager.
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Returns the description of this InstrumentManager.
     *
     * @return The description of this InstrumentManager.
     */
    public String getDescription()
    {
        return m_description;
    }

    /**
     * Returns an InstrumentableDescriptor based on its name or the name of any
     *  of its children.
     *
     * @param instrumentableName Name of the Instrumentable being requested.
     *
     * @return A Descriptor of the requested Instrumentable.
     *
     * @throws NoSuchInstrumentableException If the specified Instrumentable
     *                                       does not exist.
     */
    public InstrumentableDescriptor getInstrumentableDescriptor( String instrumentableName )
        throws NoSuchInstrumentableException
    {
        InstrumentableProxy proxy = getInstrumentableProxy( instrumentableName );
        if( proxy == null )
        {
            throw new NoSuchInstrumentableException(
                "No instrumentable can be found using name: " + instrumentableName );
        }

        return proxy.getDescriptor();
    }

    /**
     * Returns an array of Descriptors for the Instrumentables managed by this
     *  InstrumentManager.
     *
     * @return An array of Descriptors for the Instrumentables managed by this
     *  InstrumentManager.
     */
    public InstrumentableDescriptor[] getInstrumentableDescriptors()
    {
        InstrumentableDescriptor[] descriptors = m_instrumentableDescriptorArray;
        if( descriptors == null )
        {
            descriptors = updateInstrumentableDescriptorArray();
        }
        return descriptors;
    }

    /**
     * Invokes garbage collection.
     */
    public void invokeGarbageCollection()
    {
        System.gc();
    }



    /**
     * Loads the profiler state from the specified file.
     *
     * @param stateFile File to read the profiler's state from.
     *
     * @throws Exception if there are any problems loading the state.
     */
    /*
    public void loadInstrumentStateFromFile( File stateFile )
        throws Exception
    {
        long now = System.currentTimeMillis();
        getLogger().debug( "Loading profiler state from: " + stateFile.getAbsolutePath() );

        FileInputStream is = new FileInputStream( stateFile );
        try
        {
            loadInstrumentStateFromStream( is );
        }
        finally
        {
            is.close();
        }

        getLogger().debug( "Loading profiler state took " +
                           ( System.currentTimeMillis() - now ) + "ms." );
    }
    */

    /**
     * Loads the profiler state from the specified stream.
     *
     * @param is Stream to read the profiler's state from.
     *
     * @throws Exception if there are any problems loading the state.
     */
    /*
    public void loadInstrumentStateFromStream( InputStream is )
        throws Exception
    {
        // Ride on top of the Configuration classes to load the state.
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        Configuration stateConfig = builder.build( is );

        loadInstrumentStateFromConfiguration( stateConfig );
    }
    */

    /**
     * Loads the profiler state from the specified Configuration.
     *
     * @param state Configuration object to load the state from.
     *
     * @throws ConfigurationException If there were any problems loading the
     *                                state.
     */
    /*
    public void loadInstrumentStateFromConfiguration( Configuration state )
        throws ConfigurationException
    {
        Configuration[] instrumentableConfs = state.getChildren( "instrumentable" );
        for( int i = 0; i < instrumentableConfs.length; i++ )
        {
            Configuration instrumentableConf = instrumentableConfs[ i ];
            String name = instrumentableConf.getAttribute( "name" );
            InstrumentableProxy proxy = getInstrumentableProxy( name );
            if( proxy == null )
            {
                getLogger().warn( "Instrumentable entry ignored while loading state because the " +
                                  "instrumentable does not exist: " + name );
            }
            else
            {
                proxy.loadState( instrumentableConf );
            }
        }
    }
    */

    /**
     * Saves the Instrument's state to the specified file.
     *
     * @param stateFile File to write the profiler's state to.
     *
     * @throws Exception if there are any problems saving the state.
     */
    /*
    public void saveInstrumentStateToFile( File stateFile )
        throws Exception
    {
        long now = System.currentTimeMillis();
        getLogger().debug( "Saving profiler state to: " + stateFile.getAbsolutePath() );

        FileOutputStream os = new FileOutputStream( stateFile );
        try
        {
            saveInstrumentStateToStream( os );
        }
        finally
        {
            os.close();
        }

        getLogger().debug( "Saving profiler state took " +
                           ( System.currentTimeMillis() - now ) + "ms." );
    }
    */

    /**
     * Saves the Instrument's state to the specified output stream.
     *
     * @param os Stream to write the profiler's state to.
     *
     * @throws Exception if there are any problems saving the state.
     */
    /*
    public void saveInstrumentStateToStream( OutputStream os )
        throws Exception
    {
        Configuration stateConfig = saveInstrumentStateToConfiguration();

        // Ride on top of the Configuration classes to save the state.
        DefaultConfigurationSerializer serializer = new DefaultConfigurationSerializer();
        serializer.setIndent( true );
        serializer.serialize( os, stateConfig );
    }
    */

    /**
     * Returns the desktop state as a Configuration object.
     *
     * @return The desktop state as a Configuration object.
     */
    /*
    public Configuration saveInstrumentStateToConfiguration()
    {

        DefaultConfiguration state = new DefaultConfiguration( "profiler-state", "-" );

        InstrumentableProxy[] instrumentableProxies = m_instrumentableProxyArray;
        if( instrumentableProxies == null )
        {
            instrumentableProxies = updateInstrumentableProxyArray();
        }

        for( int i = 0; i < instrumentableProxies.length; i++ )
        {
            // Only save configured instrumentables as they are the only ones
            //  that will contain profile samples.
            if( instrumentableProxies[ i ].isConfigured() )
            {
                state.addChild( instrumentableProxies[ i ].saveState( m_stateCompactSamples ) );
            }
        }

        return state;
    }
    */


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
            m_totalMemoryInstrument,
            m_freeMemoryInstrument,
            m_memoryInstrument,
            m_activeThreadCountInstrument
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
        return Instrumentable.EMPTY_INSTRUMENTABLE_ARRAY;
    }

    /*---------------------------------------------------------------
     * Runnable Methods
     *-------------------------------------------------------------*/
    public void run()
    {
        while( m_runner != null )
        {
            try
            {
                Thread.sleep( 1000 );

                memoryInstruments();
                threadInstruments();

                // Handle the state file if it is set
                long now = System.currentTimeMillis();
                if( now - m_lastStateSave >= m_stateInterval )
                {
                    saveState();
                }
            }
            catch( Throwable t )
            {
                getLogger().error( "Encountered an unexpected error.", t );
            }
        }
    }

    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Saves the state to the current state file if configured.
     */
    private void saveState()
    {
        long now = System.currentTimeMillis();

        // Always set the time even if the save fails so that we don't thrash
        m_lastStateSave = now;

        if( m_stateFile == null )
        {
            return;
        }

        // Rename the old file in case there is a problem
        File renameFile = null;
        boolean success = false;
        if( m_stateFile.exists() )
        {
            renameFile = new File( m_stateFile.getAbsolutePath() + "." + now + ".backup" );
            m_stateFile.renameTo( renameFile );
        }

        try
        {
            /*
            try
            {
                saveInstrumentStateToFile( m_stateFile );
                success = true;
            }
            catch( Exception e )
            {
                getLogger().error( "Unable to save the profiler state.", e );
            }
            */
        }
        finally
        {
            // Clean up after the renamed file.
            if( renameFile != null )
            {
                if( success )
                {
                    renameFile.delete();
                }
                else
                {
                    m_stateFile.delete();
                    renameFile.renameTo( m_stateFile );
                }
            }
        }
    }

    /**
     * Returns a InstrumentableDescriptor based on its name or the name of any
     *  of its children.
     *
     * @param instrumentableName Name of the Instrumentable being requested.
     *
     * @return A Proxy of the requested Instrumentable or null if not found.
     */
    private InstrumentableProxy getInstrumentableProxy( String instrumentableName )
    {
        String name = instrumentableName;
        while( true )
        {
            InstrumentableProxy proxy = (InstrumentableProxy)m_instrumentableProxies.get( name );
            if( proxy != null )
            {
                return proxy;
            }

            // Assume this is a child name and try looking with the parent name.
            int pos = name.lastIndexOf( '.' );
            if( pos > 0 )
            {
                name = name.substring( 0, pos );
            }
            else
            {
                return null;
            }
        }
    }

    /**
     * Updates the Memory based Profile Points published by the InstrumentManager.
     */
    private void memoryInstruments()
    {
        // Avoid doing unneeded work if profile points are not being used.
        Runtime runtime = null;
        long totalMemory = -1;
        long freeMemory = -1;

        // Total Memory
        if( m_totalMemoryInstrument.isActive() )
        {
            runtime = Runtime.getRuntime();
            totalMemory = runtime.totalMemory();
            m_totalMemoryInstrument.setValue( (int)totalMemory );
        }

        // Free Memory
        if( m_freeMemoryInstrument.isActive() )
        {
            if( runtime == null )
            {
                runtime = Runtime.getRuntime();
            }
            freeMemory = runtime.freeMemory();
            m_freeMemoryInstrument.setValue( (int)freeMemory );
        }

        // In use Memory
        if( m_memoryInstrument.isActive() )
        {
            if( runtime == null )
            {
                runtime = Runtime.getRuntime();
            }
            if( totalMemory < 0 )
            {
                totalMemory = runtime.totalMemory();
            }
            if( freeMemory < 0 )
            {
                freeMemory = runtime.freeMemory();
            }
            m_memoryInstrument.setValue( (int)( totalMemory - freeMemory ) );
        }
    }

    /**
     * Updates the Thread based Profile Points published by the InstrumentManager.
     */
    private void threadInstruments()
    {
        if( m_activeThreadCountInstrument.isActive() )
        {
            // Get the top level thread group.
            ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
            ThreadGroup parent;
            while( ( parent = threadGroup.getParent() ) != null )
            {
                threadGroup = parent;
            }

            m_activeThreadCountInstrument.setValue( threadGroup.activeCount() );
        }
    }

    /**
     * Updates the cached array of InstrumentableProxies taking
     *  synchronization into account.
     *
     * @return An array of the InstrumentableProxies.
     */
    private InstrumentableProxy[] updateInstrumentableProxyArray()
    {
        synchronized( m_semaphore )
        {
            m_instrumentableProxyArray = new InstrumentableProxy[ m_instrumentableProxies.size() ];
            m_instrumentableProxies.values().toArray( m_instrumentableProxyArray );

            // Sort the array.  This is not a performance problem because this
            //  method is rarely called and doing it here saves cycles in the
            //  client.
            Arrays.sort( m_instrumentableProxyArray, new Comparator()
                {
                    public int compare( Object o1, Object o2 )
                    {
                        return ((InstrumentableProxy)o1).getDescription().
                            compareTo( ((InstrumentableProxy)o2).getDescription() );
                    }

                    public boolean equals( Object obj )
                    {
                        return false;
                    }
                } );

            return m_instrumentableProxyArray;
        }
    }

    /**
     * Updates the cached array of InstrumentableDescriptors taking
     *  synchronization into account.
     *
     * @return An array of the InstrumentableDescriptors.
     */
    private InstrumentableDescriptor[] updateInstrumentableDescriptorArray()
    {
        synchronized( m_semaphore )
        {
            if( m_instrumentableProxyArray == null )
            {
                updateInstrumentableProxyArray();
            }

            m_instrumentableDescriptorArray =
                new InstrumentableDescriptor[ m_instrumentableProxyArray.length ];
            for( int i = 0; i < m_instrumentableProxyArray.length; i++ )
            {
                m_instrumentableDescriptorArray[ i ] = m_instrumentableProxyArray[ i ].getDescriptor();
            }

            return m_instrumentableDescriptorArray;
        }
    }

    /**
     * Examines a instrumentable and Registers all of its Instruments.
     * <p>
     * Only called when m_semaphore is locked.
     */
    private void registerInstruments( Instrumentable instrumentable,
                                        InstrumentableProxy instrumentableProxy,
                                        String instrumentableName )
        throws Exception
    {
        // Loop over the Instruments published by this Instrumentable
        Instrument[] profilePoints = instrumentable.getInstruments();
        for( int i = 0; i < profilePoints.length; i++ )
        {
            Instrument profilePoint = profilePoints[ i ];
            String profilePointName =
                instrumentableName + "." + profilePoint.getInstrumentName();

            getLogger().debug( "Registering Instrument: " + profilePointName );

            // See if a proxy exists for the Instrument yet.
            InstrumentProxy proxy = instrumentableProxy.getInstrumentProxy( profilePointName );
            if( proxy == null )
            {
                proxy = new InstrumentProxy( profilePointName );
                proxy.enableLogging( getLogger() );

                // Set the type of the new InstrumentProxy depending on the
                //  class of the actual Instrument.
                if( profilePoint instanceof CounterInstrument )
                {
                    proxy.setType( InstrumentManagerClient.INSTRUMENT_TYPE_COUNTER );
                }
                else if( profilePoint instanceof ValueInstrument )
                {
                    proxy.setType( InstrumentManagerClient.INSTRUMENT_TYPE_VALUE );
                }
                else
                {
                    throw new ServiceException( profilePointName, "Encountered an unknown Instrument type for " +
                                                  "the Instrument with key, " + profilePointName + ": " +
                                                  profilePoint.getClass().getName() );
                }

                // Store a reference to the proxy in the Instrument.
                ( (AbstractInstrument)profilePoint ).setInstrumentProxy( proxy );

                instrumentableProxy.addInstrumentProxy( proxy );
            }
            else
            {
                // Register the existing proxy with the Instrument.  Make sure that the
                //  type didn't change on us.
                if( profilePoint instanceof CounterInstrument )
                {
                    switch( proxy.getType() )
                    {
                        case InstrumentManagerClient.INSTRUMENT_TYPE_COUNTER:
                            // Type is the same.
                            // Store a reference to the proxy in the Instrument.
                            ( (AbstractInstrument)profilePoint ).setInstrumentProxy( proxy );
                            break;

                        case InstrumentManagerClient.INSTRUMENT_TYPE_NONE:
                            // Not yet set.  Created in configuration.
                            proxy.setType( InstrumentManagerClient.INSTRUMENT_TYPE_COUNTER );

                            // Store a reference to the proxy in the Instrument.
                            ( (AbstractInstrument)profilePoint ).setInstrumentProxy( proxy );
                            break;

                        default:
                            throw new ServiceException( profilePointName,
                                "Instruments of more than one type are assigned to name: " +
                                profilePointName );
                    }
                }
                else if( profilePoint instanceof ValueInstrument )
                {
                    switch( proxy.getType() )
                    {
                        case InstrumentManagerClient.INSTRUMENT_TYPE_VALUE:
                            // Type is the same.
                            // Store a reference to the proxy in the Instrument.
                            ( (AbstractInstrument)profilePoint ).setInstrumentProxy( proxy );
                            break;

                        case InstrumentManagerClient.INSTRUMENT_TYPE_NONE:
                            // Not yet set.  Created in configuration.
                            proxy.setType( InstrumentManagerClient.INSTRUMENT_TYPE_VALUE );

                            // Store a reference to the proxy in the Instrument.
                            ( (AbstractInstrument)profilePoint ).setInstrumentProxy( proxy );
                            break;

                        default:
                            throw new ServiceException( profilePointName,
                                "Instruments of more than one type are assigned to name: " +
                                profilePointName );
                    }
                }
                else
                {
                    throw new ServiceException( profilePointName, "Encountered an unknown Instrument type for " +
                                                  "the Instrument with name, " + profilePointName + ": " +
                                                  profilePoint.getClass().getName() );
                }
            }
        }

        // Loop over the child Instrumentables and register their Instruments as well.
        Instrumentable[] childInstrumentables = instrumentable.getChildInstrumentables();
        for( int i = 0; i < childInstrumentables.length; i++ )
        {
            // Make sure that the child instrumentable name is set.
            if( childInstrumentables[ i ].getInstrumentableName() == null )
            {
                String msg = "The getInstrumentableName of a child Instrumentable returned null.  " +
                    "Instance of " + instrumentable.getClass().getName();
                getLogger().debug( msg );
                throw new ServiceException( instrumentable.getClass().getName(), msg );
            }

            String instrumentableChildName = instrumentableName + "." +
                childInstrumentables[ i ].getInstrumentableName();

            registerInstruments( childInstrumentables[ i ], instrumentableProxy, instrumentableChildName );
        }
    }
}

