/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument.component;

import org.apache.avalon.excalibur.component.DefaultComponentFactory;
import org.apache.avalon.excalibur.instrument.CounterInstrument;
import org.apache.avalon.excalibur.instrument.Instrument;
import org.apache.avalon.excalibur.instrument.ValueInstrument;
import org.apache.avalon.excalibur.logger.LogKitManager;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.log.Logger;

/**
 * The ThreadSafeComponentHandler to make sure components are initialized
 * and destroyed correctly.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:ryan@silveregg.co.jp">Ryan Shaw</a>
 * @author <a href="mailto:leif@silveregg.co.jp">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/03/29 14:03:49 $
 * @since 4.0
 */
public class ThreadSafeComponentHandler
    extends InstrumentComponentHandler
{
    private Component m_instance;
    private final DefaultComponentFactory m_factory;
    private boolean m_initialized = false;
    private boolean m_disposed = false;

    /** Instrument used to profile the number of outstanding references. */
    private ValueInstrument m_referencesInstrument;
    
    /** Instrument used to profile the number of gets. */
    private CounterInstrument m_getsInstrument;
    
    /** Instrument used to profile the number of puts. */
    private CounterInstrument m_putsInstrument;

    /**
     * Create a ThreadSafeComponentHandler which manages a pool of Components
     *  created by the specified factory object.
     *
     * @param factory The factory object which is responsible for creating the components
     *                managed by the ComponentHandler.
     * @param config The configuration to use to configure the pool.
     */
    public ThreadSafeComponentHandler( final DefaultComponentFactory factory,
                                       final Configuration config )
        throws Exception
    {
        m_factory = factory;
        
        // Initialize the Instrumentable elements.
        m_referencesInstrument = new ValueInstrument( "references" );
        m_getsInstrument = new CounterInstrument( "gets" );
        m_putsInstrument = new CounterInstrument( "puts" );
    }

    /**
     * Create a ComponentHandler that takes care of hiding the details of
     * whether a Component is ThreadSafe, Poolable, or SingleThreaded.
     * It falls back to SingleThreaded if not specified.
     */
    /* Don't need this?
    protected ThreadSafeComponentHandler( final Component component )
        throws Exception
    {
        m_instance = component;
        m_factory = null;
    }
    */

    public void setLogger( Logger log )
    {
        if( this.m_factory != null )
        {
            m_factory.setLogger( log );
        }

        super.setLogger( log );
    }

    /**
     * Initialize the ComponentHandler.
     */
    public void initialize()
        throws Exception
    {
        if( m_initialized )
        {
            return;
        }

        if( m_instance == null )
        {
            m_instance = (Component)this.m_factory.newInstance();
        }

        if( getLogger().isDebugEnabled() )
        {
            if( this.m_factory != null )
            {
                getLogger().debug( "ComponentHandler initialized for: " + this.m_factory.getCreatedClass().getName() );
            }
            else
            {
                getLogger().debug( "ComponentHandler initialized for: " + this.m_instance.getClass().getName() );
            }
        }

        m_initialized = true;
    }

    /**
     * Get a reference of the desired Component
     */
    protected final Component doGet()
        throws Exception
    {
        if( !m_initialized )
        {
            throw new IllegalStateException( "You cannot get a component from an uninitialized holder." );
        }

        if( m_disposed )
        {
            throw new IllegalStateException( "You cannot get a component from a disposed holder" );
        }

        // Notify the instrument manager
        m_getsInstrument.increment();
        // Reference count will be incremented after this returns
        m_referencesInstrument.setValue( getReferences() + 1 );
        
        return m_instance;
    }

    /**
     * Return a reference of the desired Component
     */
    protected void doPut( final Component component )
    {
        if( !m_initialized )
        {
            throw new IllegalStateException( "You cannot put a component in an uninitialized holder." );
        }
        
        // Notify the instrument manager
        m_putsInstrument.increment();
        // References decremented before this call.
        m_referencesInstrument.setValue( getReferences() );
    }

    /**
     * Dispose of the ComponentHandler and any associated Pools and Factories.
     */
    public void dispose()
    {
        try
        {
            if( null != m_factory )
            {
                m_factory.decommission( m_instance );
            }
            else
            {
                if( m_instance instanceof Startable )
                {
                    ( (Startable)m_instance ).stop();
                }

                if( m_instance instanceof Disposable )
                {
                    ( (Disposable)m_instance ).dispose();
                }
            }

            m_instance = null;
        }
        catch( final Exception e )
        {
            if( getLogger().isWarnEnabled() )
            {
                getLogger().warn( "Error decommissioning component: " +
                                  m_factory.getCreatedClass().getName(), e );
            }
        }

        m_disposed = true;
    }
    
    /*---------------------------------------------------------------
     * Instrumentable Methods
     *-------------------------------------------------------------*/
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
            m_referencesInstrument,
            m_getsInstrument,
            m_putsInstrument
        };
    }
}
