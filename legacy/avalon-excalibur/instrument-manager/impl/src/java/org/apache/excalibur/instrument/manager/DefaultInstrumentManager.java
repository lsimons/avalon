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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.excalibur.instrument.AbstractInstrument;
import org.apache.excalibur.instrument.CounterInstrument;
import org.apache.excalibur.instrument.Instrument;
import org.apache.excalibur.instrument.InstrumentManager;
import org.apache.excalibur.instrument.Instrumentable;
import org.apache.excalibur.instrument.ValueInstrument;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentManagerClient;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentException;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentSampleException;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentableException;

/**
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.4 $ $Date: 2004/02/28 11:47:25 $
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
    
    /** List of configured connectors. */
    private ArrayList m_connectors = new ArrayList();

    /** State file. */
    private File m_stateFile;

    /** Save state interval. */
    private long m_stateInterval;

    /** Last time that the state was saved. */
    private long m_lastStateSave;

    /** Semaphore for actions which must be synchronized */
    private Object m_semaphore = new Object();

    /** HashMap of all of the registered InstrumentableProxies by their keys. */
    private HashMap m_instrumentableProxies = new HashMap();

    /** Optimized array of the InstrumentableProxies. */
    private InstrumentableProxy[] m_instrumentableProxyArray;

    /** Optimized array of the InstrumentableDescriptorLocals. */
    private InstrumentableDescriptorLocal[] m_instrumentableDescriptorArray;

    /** List of leased InstrumentSamples. */
    private ArrayList m_leasedInstrumentSamples = new ArrayList();
    
    /** Optimized array of the leased InstrumentSamples. */
    private InstrumentSample[] m_leasedInstrumentSampleArray;
    
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
    
    /** State Version. */
    private int m_stateVersion;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new DefaultInstrumentManager.
     *
     * @param name The name used to identify this InstrumentManager.  Should not
     *             contain any spaces or periods.
     *
     * @deprecated Name should be set in the instrument configuration file.
     */
    public DefaultInstrumentManager( String name )
    {
        this();
    }
    
    /**
     * Creates a new DefaultInstrumentManager.
     */
    public DefaultInstrumentManager()
    {
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

            // Look for a configured name and description
            m_name = configuration.getChild( "name" ).getValue( "instrument-manager" );
            m_description = configuration.getChild( "description" ).getValue( m_name );
            
            // Configure the instrumentables.
            Configuration instrumentablesConf = configuration.getChild( "instrumentables" );
            Configuration[] instrumentableConfs =
                instrumentablesConf.getChildren( "instrumentable" );
            for( int i = 0; i < instrumentableConfs.length; i++ )
            {
                Configuration instrumentableConf = instrumentableConfs[ i ];
                String instrumentableName = instrumentableConf.getAttribute( "name" );

                InstrumentableProxy instrumentableProxy = new InstrumentableProxy(
                    this, null, instrumentableName, instrumentableName );
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

            String stateFile = stateFileConf.getValue( null );
            if( stateFile != null )
            {
                m_stateFile = new File( stateFile );
                if( m_stateFile.exists() )
                {
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
                }
            }
            
            // Create a logger to use with the connectors
            Logger connLogger = getLogger().getChildLogger( "connector" );
            
            // Configure the connectors
            Configuration connectorsConf = configuration.getChild( "connectors" );
            Configuration[] connectorConfs =
                connectorsConf.getChildren( "connector" );
            for( int i = 0; i < connectorConfs.length; i++ )
            {
                Configuration connectorConf = connectorConfs[ i ];
                String className = connectorConf.getAttribute( "class" );
                // Handle aliases
                if ( className.equals( "http" ) )
                {
                    // Don't use InstrumentManagerAltrmiConnector.class.getName() because
                    //  the class is optional for the build.
                    className = "org.apache.excalibur.instrument.manager.http."
                        + "InstrumentManagerHTTPConnector";
                }
                else if ( className.equals( "altrmi" ) )
                {
                    // Don't use InstrumentManagerAltrmiConnector.class.getName() because
                    //  the class is optional for the build.
                    className = "org.apache.excalibur.instrument.manager.altrmi."
                        + "InstrumentManagerAltrmiConnector";
                }
                
                // Look for the connector class and create an instance.
                try
                {
                    Class clazz = Class.forName( className );
                    InstrumentManagerConnector connector =
                        (InstrumentManagerConnector)clazz.newInstance();
                    
                    // Initialize the new connector
                    connector.setInstrumentManager( this );
                    ContainerUtil.enableLogging( connector, connLogger );
                    ContainerUtil.configure( connector, connectorConf );
                    ContainerUtil.start( connector );
                    if ( connector instanceof Instrumentable )
                    {
                        Instrumentable inst = (Instrumentable)connector;
                        registerInstrumentable( inst,
                            m_instrumentableName + ".connector." + inst.getInstrumentableName() );
                    }
                    
                    m_connectors.add( connector );
                }
                catch ( Exception e )
                {
                    String msg = "Unable to create connector because: " + e;
                    
                    // Was the optional flag set?
                    if ( connectorConf.getAttributeAsBoolean( "optional", true ) )
                    {
                        getLogger().warn( msg );
                    }
                    else
                    {
                        throw new ConfigurationException( msg );
                    }
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
        
        // Shutdown the connectors
        for ( Iterator iter = m_connectors.iterator(); iter.hasNext(); )
        {
            InstrumentManagerConnector connector = (InstrumentManagerConnector)iter.next();
            try
            {
                ContainerUtil.stop( connector );
                ContainerUtil.dispose( connector );
            }
            catch ( Exception e )
            {
                getLogger().error( "Encountered an unexpected error shutting down a connector", e );
            }
        }

        saveState();
    }

    /*---------------------------------------------------------------
     * InstrumentManager Methods
     *-------------------------------------------------------------*/
    /**
     * Instrumentable to be registered with the instrument manager.  Should be
     *  called whenever an Instrumentable is created.  The '.' character is
     *  used to denote a child Instrumentable and can be used to register the
     *  instrumentable at a specific point in an instrumentable hierarchy.
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
            // If the specified instrumentable name contains '.' chars then we need to
            //  make sure we register the instrumentable at the correct location, creating
            //  any parent instrumentables as necessary.
            int pos = instrumentableName.indexOf( '.' );
            if ( pos >= 0 )
            {
                String parentName = instrumentableName.substring( 0, pos );
                String childName =
                    instrumentableName.substring( pos + 1 );
                InstrumentableProxy instrumentableProxy =
                    (InstrumentableProxy)m_instrumentableProxies.get( parentName );
                if( instrumentableProxy == null )
                {
                    // This is a Instrumentable that has not been seen before.
                    instrumentableProxy = new InstrumentableProxy(
                        this, null, parentName, parentName );
                    instrumentableProxy.enableLogging( getLogger() );
                    // Do not call configure here because there is no configuration
                    //  for discovered instrumentables.
                    m_instrumentableProxies.put( parentName, instrumentableProxy );
    
                    // Clear the optimized arrays
                    m_instrumentableProxyArray = null;
                    m_instrumentableDescriptorArray = null;
    
                    // Recursively register all the Instruments in this and any child Instrumentables.
                    registerDummyInstrumentableInner(
                        instrumentable, instrumentableProxy, parentName, childName );
                }
                else
                {
                    // Additional Instrumentable instance.  Possible that new Instruments could be found.
                    registerDummyInstrumentableInner(
                        instrumentable, instrumentableProxy, parentName, childName );
                }
            } else {
                // If the instrumentable does not implement ThreadSafe, then it is possible that
                //  another one of its instance was already registered.  If so, then the
                //  Instruments will all be the same.  The new instances still need to be
                //  registered however.
                InstrumentableProxy instrumentableProxy =
                    (InstrumentableProxy)m_instrumentableProxies.get( instrumentableName );
                if( instrumentableProxy == null )
                {
                    // This is a Instrumentable that has not been seen before.
                    instrumentableProxy = new InstrumentableProxy(
                        this, null, instrumentableName, instrumentableName );
                    instrumentableProxy.enableLogging( getLogger() );
                    // Do not call configure here because there is no configuration
                    //  for discovered instrumentables.
                    m_instrumentableProxies.put( instrumentableName, instrumentableProxy );
    
                    // Clear the optimized arrays
                    m_instrumentableProxyArray = null;
                    m_instrumentableDescriptorArray = null;
    
                    // Recursively register all the Instruments in this and any child Instrumentables.
                    registerInstrumentableInner(
                        instrumentable, instrumentableProxy, instrumentableName );
                }
                else
                {
                    // Additional Instrumentable instance.  Possible that new Instruments could be found.
                    registerInstrumentableInner(
                        instrumentable, instrumentableProxy, instrumentableName );
                }
            }
        }
        
        stateChanged();
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
     * Returns an InstrumentableDescriptorLocal based on its name or the name
     *  of any of its children.
     *
     * @param instrumentableName Name of the Instrumentable being requested.
     *
     * @return A Descriptor of the requested Instrumentable.
     *
     * @throws NoSuchInstrumentableException If the specified Instrumentable
     *                                       does not exist.
     */
    public InstrumentableDescriptorLocal getInstrumentableDescriptor( String instrumentableName )
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
    public InstrumentableDescriptorLocal[] getInstrumentableDescriptors()
    {
        InstrumentableDescriptorLocal[] descriptors = m_instrumentableDescriptorArray;
        if( descriptors == null )
        {
            descriptors = updateInstrumentableDescriptorArray();
        }
        return descriptors;
    }
    
    /**
     * Searches the entire instrument tree an instrumentable with the given
     *  name.
     *
     * @param instrumentableName Name of the Instrumentable being requested.
     *
     * @return A Descriptor of the requested Instrumentable.
     *
     * @throws NoSuchInstrumentableException If the specified Instrumentable does
     *                                       not exist.
     */
    public InstrumentableDescriptorLocal locateInstrumentableDescriptor( String instrumentableName )
        throws NoSuchInstrumentableException
    {
        InstrumentableProxy instrumentableProxy =
            locateDeepestInstrumentableProxy( instrumentableName );
        if ( instrumentableProxy != null )
        {
            if ( instrumentableProxy.getName().equals( instrumentableName ) )
            {
                // Found what we were looking for
                return instrumentableProxy.getDescriptor();
            }
        }
        
        // Unable to locate the requested Instrumentable
        throw new NoSuchInstrumentableException(
            "No instrumentable can be found with the name: " + instrumentableName );
    }
    
    /**
     * Searches the entire instrument tree an instrument with the given name.
     *
     * @param instrumentName Name of the Instrument being requested.
     *
     * @return A Descriptor of the requested Instrument.
     *
     * @throws NoSuchInstrumentException If the specified Instrument does
     *                                   not exist.
     */
    public InstrumentDescriptorLocal locateInstrumentDescriptor( String instrumentName )
        throws NoSuchInstrumentException
    {
        InstrumentableProxy instrumentableProxy =
            locateDeepestInstrumentableProxy( instrumentName );
        if ( instrumentableProxy != null )
        {
            // Now look for the specified instrument
            InstrumentProxy instrumentProxy =
                instrumentableProxy.getInstrumentProxy( instrumentName );
            if ( instrumentProxy != null )
            {
                if ( instrumentProxy.getName().equals( instrumentName ) )
                {
                    // Found what we were looking for
                    return instrumentProxy.getDescriptor();
                }
            }
        }
        
        // Unable to locate the requested Instrument
        throw new NoSuchInstrumentException(
            "No instrument can be found with the name: " + instrumentName );
    }

    /**
     * Searches the entire instrument tree an instrument sample with the given
     *  name.
     *
     * @param sampleName Name of the Instrument Sample being requested.
     *
     * @return A Descriptor of the requested Instrument Sample.
     *
     * @throws NoSuchInstrumentSampleException If the specified Instrument
     *                                         Sample does not exist.
     */
    public InstrumentSampleDescriptorLocal locateInstrumentSampleDescriptor( String sampleName )
        throws NoSuchInstrumentSampleException
    {
        InstrumentableProxy instrumentableProxy =
            locateDeepestInstrumentableProxy( sampleName );
        if ( instrumentableProxy != null )
        {
            // Now look for the specified instrument
            InstrumentProxy instrumentProxy =
                instrumentableProxy.getInstrumentProxy( sampleName );
            if ( instrumentProxy != null )
            {
                // Now look for the specified sample
                InstrumentSample sample = instrumentProxy.getInstrumentSample( sampleName );
                if ( sample != null )
                {
                    if ( sample.getName().equals( sampleName ) )
                    {
                        // Found what we were looking for
                        return sample.getDescriptor();
                    }
                }
            }
        }
        
        // Unable to locate the requested Instrument Sample
        throw new NoSuchInstrumentSampleException(
            "No instrument sample can be found with the name: " + sampleName );
    }
    
    /**
     * Returns the stateVersion of the instrument manager.  The state version
     *  will be incremented each time any of the configuration of the
     *  instrument manager or any of its children is modified.
     * Clients can use this value to tell whether or not anything has
     *  changed without having to do an exhaustive comparison.
     *
     * @return The state version of the instrument manager.
     */
    int getStateVersion()
    {
        return m_stateVersion;
    }
    
    /**
     * Invokes garbage collection.
     */
    public void invokeGarbageCollection()
    {
        System.gc();
    }
    
    

    /**
     * Loads the Instrument Manager state from the specified file.
     *
     * @param stateFile File to read the instrument manager's state from.
     *
     * @throws Exception if there are any problems loading the state.
     */
    public void loadStateFromFile( File stateFile )
        throws Exception
    {
        long now = System.currentTimeMillis();
        getLogger().debug( "Loading Instrument Manager state from: " +
            stateFile.getAbsolutePath() );

        FileInputStream is = new FileInputStream( stateFile );
        try
        {
            loadStateFromStream( is );
        }
        finally
        {
            is.close();
        }

        getLogger().debug( "Loading Instrument Manager state took " +
                           ( System.currentTimeMillis() - now ) + "ms." );
    }

    /**
     * Loads the Instrument Manager state from the specified stream.
     *
     * @param is Stream to read the instrument manager's state from.
     *
     * @throws Exception if there are any problems loading the state.
     */
    public void loadStateFromStream( InputStream is )
        throws Exception
    {
        // Ride on top of the Configuration classes to load the state.
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        Configuration stateConfig = builder.build( is );

        loadStateFromConfiguration( stateConfig );
    }

    /**
     * Loads the Instrument Manager state from the specified Configuration.
     *
     * @param state Configuration object to load the state from.
     *
     * @throws ConfigurationException If there were any problems loading the
     *                                state.
     */
    public void loadStateFromConfiguration( Configuration state )
        throws ConfigurationException
    {
        Configuration[] instrumentableConfs = state.getChildren( "instrumentable" );
        for( int i = 0; i < instrumentableConfs.length; i++ )
        {
            Configuration instrumentableConf = instrumentableConfs[ i ];
            String instrumentableName = instrumentableConf.getAttribute( "name" );
            InstrumentableProxy instrumentableProxy = getInstrumentableProxy( instrumentableName );
            if( instrumentableProxy == null )
            {
                // The Instrumentable was in the state file, but has not yet
                //  been registered.  It is possible that it will be registered
                //  at a later time.  For now it needs to be created.
                instrumentableProxy = new InstrumentableProxy(
                    this, null, instrumentableName, instrumentableName );
                instrumentableProxy.enableLogging( getLogger() );
                m_instrumentableProxies.put( instrumentableName, instrumentableProxy );

                // Clear the optimized arrays
                m_instrumentableProxyArray = null;
                m_instrumentableDescriptorArray = null;
            }
            
            instrumentableProxy.loadState( instrumentableConf );
        }
        
        stateChanged();
    }

    /**
     * Saves the Instrument Manager's state to the specified file.  Any
     *  existing file is backed up before the save takes place and replaced
     *  in the event of an error.
     *
     * @param stateFile File to write the Instrument Manager's state to.
     *
     * @throws Exception if there are any problems saving the state.
     */
    public void saveStateToFile( File stateFile )
        throws Exception
    {
        long now = System.currentTimeMillis();
        getLogger().debug( "Saving Instrument Manager state to: " + stateFile.getAbsolutePath() );

        // First save the state to an in memory stream to shorten the
        //  period of time needed to write the data to disk.  This makes it
        //  less likely that the files will be left in a corrupted state if
        //  the JVM dies at the wrong time.
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] data;
        try
        {
            saveStateToStream( os );
            data = os.toByteArray();
        }
        finally
        {
            os.close();
        }
        
        // If the specified file exists, then rename it before we start writing.
        //  This makes it possible to recover from some errors.
        File renameFile = null;
        boolean success = false;
        if( stateFile.exists() )
        {
            renameFile = new File( stateFile.getAbsolutePath() + "." + now + ".backup" );
            stateFile.renameTo( renameFile );
        }
        
        // Write the data to the new file.
        FileOutputStream fos = new FileOutputStream( stateFile );
        try
        {
            fos.write( data );
            success = true;
        }
        finally
        {
            fos.close();
            
            if ( !success )
            {
                // Make sure that part of the file does not exist.
                stateFile.delete();
            }
            
            // Handle the backup file.
            if ( renameFile != null )
            {
                if ( success )
                {
                    // No longer need the backup.
                    renameFile.delete();
                }
                else
                {
                    // Need to replace the backup.
                    renameFile.renameTo( stateFile );
                }
            }
        }
        
        getLogger().debug( "Saving Instrument Manager state took " +
                           ( System.currentTimeMillis() - now ) + "ms." );
    }

    /**
     * Saves the Instrument Manager's state to the specified output stream.
     *
     * @param os Stream to write the Instrument Manager's state to.
     *
     * @throws Exception if there are any problems saving the state.
     */
    public void saveStateToStream( OutputStream os )
        throws Exception
    {
        Configuration stateConfig = saveStateToConfiguration();

        // Ride on top of the Configuration classes to save the state.
        DefaultConfigurationSerializer serializer = new DefaultConfigurationSerializer();
        serializer.setIndent( true );
        serializer.serialize( os, stateConfig );
    }

    /**
     * Returns the Instrument Manager's state as a Configuration object.
     *
     * @return The Instrument Manager's state as a Configuration object.
     */
    public Configuration saveStateToConfiguration()
    {
        DefaultConfiguration state = new DefaultConfiguration( "instrument-manager-state", "-" );

        InstrumentableProxy[] instrumentableProxies = m_instrumentableProxyArray;
        if( instrumentableProxies == null )
        {
            instrumentableProxies = updateInstrumentableProxyArray();
        }

        for( int i = 0; i < instrumentableProxies.length; i++ )
        {
            Configuration childState = instrumentableProxies[ i ].saveState();
            if ( childState != null )
            {
                state.addChild( childState );
            }
        }

        return state;
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
                testInstrumentSampleLeases();

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
        
        try
        {
            saveStateToFile( m_stateFile );
        }
        catch ( Exception e )
        {
            getLogger().warn( "Unable to save the Instrument Manager state", e );
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
     * Given the name of an instrumentable proxy, locate the deepest child
     *  instrumentable given the name.  The name can be the name of an
     *  instrumentable or of any of its children.
     *
     * @param instrumentableName Fully qualified name of the instrumentable
     *                           being requested, or of any of its children.
     *
     * @return The requested instrumentable, or null if not found.
     */
    private InstrumentableProxy locateDeepestInstrumentableProxy( String instrumentableName )
    {
        InstrumentableProxy deepestProxy = null;
        // Start by obtaining a top level instrumentable
        InstrumentableProxy proxy = getInstrumentableProxy( instrumentableName );
        
        // Now attempt to locate a child instrumentable
        while ( proxy != null )
        {
            deepestProxy = proxy;
            
            proxy = deepestProxy.getChildInstrumentableProxy( instrumentableName );
        }
        
        return deepestProxy;
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
     * Handles the maintenance of all Instrument Samples which have been leased
     *  by a client.  Any Samples whose leases which have expired are cleaned
     *  up.
     */
    private void testInstrumentSampleLeases()
    {
        long now = System.currentTimeMillis();
        
        InstrumentSample[] samples;
        synchronized( m_leasedInstrumentSamples )
        {
            samples = m_leasedInstrumentSampleArray;
            if ( samples == null )
            {
                m_leasedInstrumentSampleArray =
                    new InstrumentSample[ m_leasedInstrumentSamples.size() ];
                m_leasedInstrumentSamples.toArray( m_leasedInstrumentSampleArray );
                samples = m_leasedInstrumentSampleArray;
            }
        }
        
        for ( int i = 0; i < samples.length; i++ )
        {
            InstrumentSample sample = samples[i];
            long expire = sample.getLeaseExpirationTime();
            if ( now >= expire )
            {
                // The sample lease has expired.
                InstrumentProxy instrument = sample.getInstrumentProxy();
                instrument.removeInstrumentSample( sample );
                sample.expire();
                
                m_leasedInstrumentSamples.remove( sample );
                m_leasedInstrumentSampleArray = null;
            }
        }
    }
    
    /**
     * Registers an InstrumentSample which has been leased so that the
     *  Instrument Manager can efficiently purge it when it has expired.
     *
     * @param instrumentSample Leased InstrumentSample to register.
     */
    void registerLeasedInstrumentSample( InstrumentSample instrumentSample )
    {
        synchronized( m_leasedInstrumentSamples )
        {
            // Make sure that the sample is really leased.
            if ( instrumentSample.getLeaseExpirationTime() <= 0 )
            {
                throw new IllegalStateException( "Got an InstrumentSample that was not leased." );
            }
            
            // Make sure that it is not already in the list.
            if ( m_leasedInstrumentSamples.indexOf( instrumentSample ) < 0 )
            {
                m_leasedInstrumentSamples.add( instrumentSample );
                m_leasedInstrumentSampleArray = null;
            }
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
     * Updates the cached array of InstrumentableDescriptorLocals taking
     *  synchronization into account.
     *
     * @return An array of the InstrumentableDescriptors.
     */
    private InstrumentableDescriptorLocal[] updateInstrumentableDescriptorArray()
    {
        synchronized( m_semaphore )
        {
            if( m_instrumentableProxyArray == null )
            {
                updateInstrumentableProxyArray();
            }

            m_instrumentableDescriptorArray =
                new InstrumentableDescriptorLocal[ m_instrumentableProxyArray.length ];
            for( int i = 0; i < m_instrumentableProxyArray.length; i++ )
            {
                m_instrumentableDescriptorArray[ i ] = m_instrumentableProxyArray[ i ].getDescriptor();
            }

            return m_instrumentableDescriptorArray;
        }
    }

    /**
     * Called as a place holder to handle the registration of instrumentables
     *  that do not really exist.  This makes it possible to register
     *  instrumentables at arbitrary locations in the instrumentable hierarchy.
     *
     * @param instrumentable The instrumentable that was registered below a dummy
     *                       parent.
     * @param instrumentableProxy The proxy assigned to the current placeholder
     *                            instrumentable.
     * @param instrumentableName The name of the current placeholder
     *                           instrumentable.
     * @param childName The name of the child instrumentable to register.  May
     *                  contain further '.' characters.
     */
    private void registerDummyInstrumentableInner( Instrumentable instrumentable,
                                                   InstrumentableProxy instrumentableProxy,
                                                   String instrumentableName,
                                                   String childName )
        throws Exception
    {
        // If the specified instrumentable name contains '.' chars then we need to
        //  make sure we register the instrumentable at the correct location, creating
        //  any parent instrumentables as necessary.
        int pos = childName.indexOf( '.' );
        if ( pos >= 0 )
        {
            String newParentName = childName.substring( 0, pos );
            String newChildName =
                childName.substring( pos + 1 );
            
            String fullChildName = instrumentableName + "." + newParentName;
            
            getLogger().debug( "Registering Child Instrumentable: " + fullChildName );
            
            // See if a proxy exists for the child Instrumentable yet.
            InstrumentableProxy proxy =
                instrumentableProxy.getChildInstrumentableProxy( fullChildName );
            if( proxy == null )
            {
                proxy = new InstrumentableProxy(
                    this, instrumentableProxy, fullChildName, newParentName );
                proxy.enableLogging( getLogger() );
                
                instrumentableProxy.addChildInstrumentableProxy( proxy );
            }
            
            // Recurse to the child
            registerDummyInstrumentableInner( instrumentable, proxy, fullChildName, newChildName );
        }
        else
        {
            // The child does not contain and '.' characters, so we are at the correct location.
            String fullChildName = instrumentableName + "." + childName;
            
            getLogger().debug( "Registering Child Instrumentable: " + fullChildName );
            
            // See if a proxy exists for the child Instrumentable yet.
            InstrumentableProxy proxy =
                instrumentableProxy.getChildInstrumentableProxy( fullChildName );
            if( proxy == null )
            {
                proxy = new InstrumentableProxy(
                    this, instrumentableProxy, fullChildName, childName );
                proxy.enableLogging( getLogger() );
                
                instrumentableProxy.addChildInstrumentableProxy( proxy );
            }
            
            // Recurse to the child
            registerInstrumentableInner( instrumentable, proxy, fullChildName );
        }
    }

    /**
     * Examines a instrumentable and Registers all of its child Instrumentables
     *  and Instruments.
     * <p>
     * Only called when m_semaphore is locked.
     */
    private void registerInstrumentableInner( Instrumentable instrumentable,
                                              InstrumentableProxy instrumentableProxy,
                                              String instrumentableName )
        throws Exception
    {
        // Mark the instrumentable proxy as registered.
        instrumentableProxy.setRegistered();

        // Loop over the Instruments published by this Instrumentable
        Instrument[] instruments = instrumentable.getInstruments();
        for( int i = 0; i < instruments.length; i++ )
        {
            Instrument instrument = instruments[ i ];
            String instrumentName = instrument.getInstrumentName();
            String fullInstrumentName = instrumentableName + "." + instrumentName;

            getLogger().debug( "Registering Instrument: " + fullInstrumentName );

            // See if a proxy exists for the Instrument yet.
            InstrumentProxy proxy = instrumentableProxy.getInstrumentProxy( fullInstrumentName );
            if( proxy == null )
            {
                proxy = new InstrumentProxy(
                    instrumentableProxy, fullInstrumentName, instrumentName );
                proxy.enableLogging( getLogger() );

                // Set the type of the new InstrumentProxy depending on the
                //  class of the actual Instrument.
                if( instrument instanceof CounterInstrument )
                {
                    proxy.setType( InstrumentManagerClient.INSTRUMENT_TYPE_COUNTER );
                }
                else if( instrument instanceof ValueInstrument )
                {
                    proxy.setType( InstrumentManagerClient.INSTRUMENT_TYPE_VALUE );
                }
                else
                {
                    throw new ServiceException( fullInstrumentName, "Encountered an unknown "
                        + "Instrument type for the Instrument with key, "
                        + fullInstrumentName + ": " + instrument.getClass().getName() );
                }

                // Mark the instrument proxy as registered.
                proxy.setRegistered();
                
                // Store a reference to the proxy in the Instrument.
                ( (AbstractInstrument)instrument ).setInstrumentProxy( proxy );

                instrumentableProxy.addInstrumentProxy( proxy );
            }
            else
            {
                // Register the existing proxy with the Instrument.  Make sure that the
                //  type didn't change on us.
                if( instrument instanceof CounterInstrument )
                {
                    switch( proxy.getType() )
                    {
                        case InstrumentManagerClient.INSTRUMENT_TYPE_COUNTER:
                            // Type is the same.
                            // Store a reference to the proxy in the Instrument.
                            ( (AbstractInstrument)instrument ).setInstrumentProxy( proxy );
                            break;

                        case InstrumentManagerClient.INSTRUMENT_TYPE_NONE:
                            // Not yet set.  Created in configuration.
                            proxy.setType( InstrumentManagerClient.INSTRUMENT_TYPE_COUNTER );

                            // Store a reference to the proxy in the Instrument.
                            ( (AbstractInstrument)instrument ).setInstrumentProxy( proxy );
                            break;

                        default:
                            throw new ServiceException( instrumentName,
                                "Instruments of more than one type are assigned to name: "
                                + instrumentName );
                    }
                }
                else if( instrument instanceof ValueInstrument )
                {
                    switch( proxy.getType() )
                    {
                        case InstrumentManagerClient.INSTRUMENT_TYPE_VALUE:
                            // Type is the same.
                            // Store a reference to the proxy in the Instrument.
                            ( (AbstractInstrument)instrument ).setInstrumentProxy( proxy );
                            break;

                        case InstrumentManagerClient.INSTRUMENT_TYPE_NONE:
                            // Not yet set.  Created in configuration.
                            proxy.setType( InstrumentManagerClient.INSTRUMENT_TYPE_VALUE );

                            // Store a reference to the proxy in the Instrument.
                            ( (AbstractInstrument)instrument ).setInstrumentProxy( proxy );
                            break;

                        default:
                            throw new ServiceException( instrumentName,
                                "Instruments of more than one type are assigned to name: "
                                + instrumentName );
                    }
                }
                else
                {
                    throw new ServiceException( instrumentName, "Encountered an unknown Instrument "
                        + "type for the Instrument with name, " + instrumentName + ": "
                        + instrument.getClass().getName() );
                }
                
                // Mark the instrument proxy as registered.
                proxy.setRegistered();
            }
        }

        // Loop over the child Instrumentables published by this Instrumentable
        Instrumentable[] children = instrumentable.getChildInstrumentables();
        for ( int i = 0; i < children.length; i++ )
        {
            Instrumentable child = children[i];
            
            // Make sure that the child instrumentable name is set.
            String childName = child.getInstrumentableName();
            if( childName == null )
            {
                String msg = "The getInstrumentableName() method of a child Instrumentable of " +
                    instrumentableName + " returned null.  Child class: " +
                    child.getClass().getName();
                getLogger().debug( msg );
                throw new ServiceException( instrumentable.getClass().getName(), msg );
            }
            
            String fullChildName = instrumentableName + "." + childName;
            
            getLogger().debug( "Registering Child Instrumentable: " + fullChildName );
            
            // See if a proxy exists for the child Instrumentable yet.
            InstrumentableProxy proxy =
                instrumentableProxy.getChildInstrumentableProxy( fullChildName );
            if( proxy == null )
            {
                proxy = new InstrumentableProxy(
                    this, instrumentableProxy, fullChildName, childName );
                proxy.enableLogging( getLogger() );
                
                instrumentableProxy.addChildInstrumentableProxy( proxy );
            }
            
            // Recurse to the child
            registerInstrumentableInner( child, proxy, fullChildName );
        }
    }
    
    /**
     * Called whenever the state of the instrument manager is changed.
     */
    protected void stateChanged()
    {
        m_stateVersion++;
    }
}

