/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument.manager;

import java.util.HashMap;

import org.apache.avalon.excalibur.instrument.manager.interfaces.InstrumentDescriptor;
import org.apache.avalon.excalibur.instrument.manager.interfaces.InstrumentableDescriptor;
import org.apache.avalon.excalibur.instrument.manager.interfaces.NoSuchInstrumentException;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 * A InstrumentableProxy makes it easy for the InstrumentManager to manage
 *  Instrumentables and their Instruments.
 * <p>
 * Not Synchronized.
 *
 * @author <a href="mailto:leif@silveregg.co.jp">Leif Mortenson</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/03/28 04:06:18 $
 * @since 4.1
 */
class InstrumentableProxy
    extends AbstractLogEnabled
    implements Configurable
{
    /** Configured flag. */
    private boolean m_configured;

    /** The name used to identify a Instrumentable. */
    private String m_name;

    /** The description of the Instrumentable. */
    private String m_description;

    /** The Descriptor for the Instrumentable. */
    private InstrumentableDescriptorImpl m_descriptor;

    /** Map of the InstrumentProxies owned by this InstrumentableProxy. */
    private HashMap m_instrumentProxies = new HashMap();

    /** Optimized array of the InstrumentProxies. */
    private InstrumentProxy[] m_instrumentProxyArray;

    /** Optimized array of the InstrumentDescriptors. */
    private InstrumentDescriptor[] m_instrumentDescriptorArray;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new InstrumentableProxy.
     *
     * @param name The name used to identify a Instrumentable.
     */
    InstrumentableProxy( String name )
    {
        // Default description equals the name in case it is not set later.
        m_description = m_name = name;

        // Create the descriptor
        m_descriptor = new InstrumentableDescriptorImpl( this );
    }

    /*---------------------------------------------------------------
     * Configurable Methods
     *-------------------------------------------------------------*/
    /**
     * Configures the Instrumentable.  Called from the ProfilerManager's
     *  configure method.  The class does not need to be configured to
     *  function correctly.
     *
     * @param configuration Instrumentable configuration element from the
     *                      ProfilerManager's configuration.
     *
     * @throws ConfigurationException If there are any configuration problems.
     */
    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        synchronized( this )
        {
            // The description is optional
            m_description = configuration.getAttribute( "description", m_name );

            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Configuring Instrumentable: " + m_name + " as \"" +
                                   m_description + "\"" );
            }

            m_configured = true;
            
            // Configure any Instruments
            Configuration[] instrumentConfs = configuration.getChildren( "instrument" );
            for( int i = 0; i < instrumentConfs.length; i++ )
            {
                Configuration instrumentConf = instrumentConfs[ i ];
                String instrumentName = m_name + "." + instrumentConf.getAttribute( "name" );

                InstrumentProxy instrumentProxy = new InstrumentProxy( instrumentName );
                instrumentProxy.enableLogging( getLogger() );
                instrumentProxy.configure( instrumentConf );
                m_instrumentProxies.put( instrumentName, instrumentProxy );

                // Clear the optimized arrays
                m_instrumentProxyArray = null;
                m_instrumentDescriptorArray = null;
            }
        }
    }

    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Returns true if the instrumentable was configured in the instrumentables
     *  section of the configuration.
     *
     * @return True if configured.
     */
    boolean isConfigured()
    {
        return m_configured;
    }

    /**
     * Gets the name for the Instrumentable.  The Instrumentable Name is used to
     *  uniquely identify the Instrumentable during the configuration of the
     *  Profiler and to gain access to a InstrumentableDescriptor through a
     *  ProfilerManager.
     *
     * @return The name used to identify a Instrumentable.
     */
    String getName()
    {
        return m_name;
    }

    /**
     * Sets the description for the instrumentable object.  This description will
     *  be set during the configuration of the profiler if a configuration
     *  exists for this Instrumentable.
     *
     * @param description The description of the Instrumentable.
     */
    void setDescription( String description )
    {
        m_description = description;
    }

    /**
     * Gets the description of the Instrumentable.
     *
     * @return The description of the Instrumentable.
     */
    String getDescription()
    {
        return m_description;
    }

    /**
     * Returns a Descriptor for the Instrumentable.
     *
     * @return A Descriptor for the Instrumentable.
     */
    InstrumentableDescriptor getDescriptor()
    {
        return m_descriptor;
    }

    /**
     * Adds a InstrumentProxy to the Instrumentable.  This method will be
     *  called during the configuration phase of the Profiler if an element
     *  defining the Instrument exists, or if the Instrument registers
     *  itself with the ProfilerManager as it is running.
     * <p>
     * This method should never be called for Instruments which have already
     *  been added.
     *
     * @param instrumentProxy InstrumentProxy to be added.
     */
    void addInstrumentProxy( InstrumentProxy instrumentProxy )
    {
        synchronized( this )
        {
            m_instrumentProxies.put( instrumentProxy.getName(), instrumentProxy );

            // Clear the optimized arrays
            m_instrumentProxyArray = null;
            m_instrumentDescriptorArray = null;
        }
    }

    /**
     * Returns a InstrumentProxy based on its name or the name of any
     *  of its children.
     *
     * @param instrumentName Name of the Instrument being requested.
     *
     * @return The requested InstrumentProxy or null if does not exist.
     */
    InstrumentProxy getInstrumentProxy( String instrumentName )
    {
        synchronized( this )
        {
            String name = instrumentName;
            while( true )
            {
                InstrumentProxy proxy = (InstrumentProxy)m_instrumentProxies.get( name );
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
    }

    /**
     * Returns an array of Proxies to the Instruments in the Instrumentable.
     *
     * @return An array of Proxies to the Instruments in the Instrumentable.
     */
    InstrumentProxy[] getInstrumentProxies()
    {
        InstrumentProxy[] proxies = m_instrumentProxyArray;
        if( proxies == null )
        {
            proxies = updateInstrumentProxyArray();
        }
        return proxies;
    }

    /**
     * Returns an array of Descriptors for the Instruments in the Instrumentable.
     *
     * @return An array of Descriptors for the Instruments in the Instrumentable.
     */
    InstrumentDescriptor[] getInstrumentDescriptors()
    {
        InstrumentDescriptor[] descriptors = m_instrumentDescriptorArray;
        if( descriptors == null )
        {
            descriptors = updateInstrumentDescriptorArray();
        }
        return descriptors;
    }

    /**
     * Updates the cached array of InstrumentProxies taking
     *  synchronization into account.
     *
     * @return An array of the InstrumentProxies.
     */
    private InstrumentProxy[] updateInstrumentProxyArray()
    {
        synchronized( this )
        {
            m_instrumentProxyArray = new InstrumentProxy[ m_instrumentProxies.size() ];
            m_instrumentProxies.values().toArray( m_instrumentProxyArray );

            return m_instrumentProxyArray;
        }
    }

    /**
     * Updates the cached array of InstrumentDescriptors taking
     *  synchronization into account.
     *
     * @return An array of the InstrumentDescriptors.
     */
    private InstrumentDescriptor[] updateInstrumentDescriptorArray()
    {
        synchronized( this )
        {
            if( m_instrumentProxyArray == null )
            {
                updateInstrumentProxyArray();
            }

            m_instrumentDescriptorArray =
                new InstrumentDescriptor[ m_instrumentProxyArray.length ];
            for( int i = 0; i < m_instrumentProxyArray.length; i++ )
            {
                m_instrumentDescriptorArray[ i ] = m_instrumentProxyArray[ i ].getDescriptor();
            }

            return m_instrumentDescriptorArray;
        }
    }

    /**
     * Saves the current state into a Configuration.
     *
     * @param useCompactSamples Flag for whether or not ProfileSample data
     *                          should be saved in compact format or not.
     *
     * @return The state as a Configuration.
     */
    /*
    Configuration saveState( boolean useCompactSamples )
    {
        DefaultConfiguration state = new DefaultConfiguration( "instrumentable", "-" );
        state.addAttribute( "name", m_name );

        InstrumentProxy[] proxies = getInstrumentProxies();
        for( int i = 0; i < proxies.length; i++ )
        {
            // Only save configured instrumentables as they are the only ones
            //  that will contain profile samples.
            if( proxies[ i ].isConfigured() )
            {
                state.addChild( proxies[ i ].saveState( useCompactSamples ) );
            }
        }

        return state;
    }
    */

    /**
     * Loads the state into the Instrumentable.
     *
     * @param state Configuration object to load state from.
     *
     * @throws ConfigurationException If there were any problems loading the
     *                                state.
     */
    /*
    void loadState( Configuration state ) throws ConfigurationException
    {
        synchronized( this )
        {
            Configuration[] instrumentConfs = state.getChildren( "profile-point" );
            for( int i = 0; i < instrumentConfs.length; i++ )
            {
                Configuration instrumentConf = instrumentConfs[ i ];
                String name = instrumentConf.getAttribute( "name" );
                InstrumentProxy proxy = getInstrumentProxy( name );
                if( proxy == null )
                {
                    getLogger().warn( "Instrument entry ignored while loading state because the " +
                                      "profile point does not exist: " + name );
                }
                else
                {
                    proxy.loadState( instrumentConf );
                }
            }
        }
    }
    */
}
