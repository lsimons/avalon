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
import org.apache.avalon.excalibur.instrument.Instrumentable;
import org.apache.avalon.excalibur.instrument.ValueInstrument;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.configuration.Configuration;

/**
 * The PoolableComponentHandler to make sure that poolable components are initialized
 * destroyed and pooled correctly.
 * <p>
 * Components which implement Poolable may be configured to be pooled using the following
 *  example configuration.  This example assumes that the user component class MyComp
 *  implements Poolable.
 * <p>
 * Configuration Example:
 * <pre>
 *   &lt;my-comp pool-max="8" pool-max-strict="false" pool-blocking="true" pool-timeout="0"
 *            pool-trim-interval="0"/&gt;
 * </pre>
 * <p>
 * Roles Example:
 * <pre>
 *   &lt;role name="com.mypkg.MyComponent"
 *         shorthand="my-comp"
 *         default-class="com.mypkg.DefaultMyComponent"/&gt;
 * </pre>
 * <p>
 * Configuration Attributes:
 * <ul>
 * <li>The <code>pool-max</code> attribute is used to set the maximum number of components which
 *  will be pooled.  See the <code>pool-max-scrict</code> and <code>pool-blocking</code>
 *  attributes.  (Defaults to "8")</li>
 *
 * <li>The <code>pool-max-scrict</code> attribute is used to configure whether the Component
 *  Manager should allow more than <code>pool-max</code> Poolables to be looked up at the same
 *  time.  Setting this to true will throw an exception if the <code>pool-blocking</code> attribute
 *  is false.  A value of false will allow additional instances of the Component to be created
 *  to serve requests, but the additional instances will not be pooled.  (Defaults to "false")
 *
 * <li>The <code>pool-blocking</code> attributes is used to configure whether the Component Manager
 *  should block or throw an Exception when more than <code>pool-max</code> Poolables are looked
 *  up at the same time.  Setting this to true will cause requests to block until another thread
 *  releases a Poolable back to the Component Manager.  False will cause an exception to be thrown.
 *  This attribute is ignored if <code>pool-max-scrict</code> is false.  (Defaults to "true")</li>
 *
 * <li>The <code>pool-timeout</code> attribute is used to specify the maximum amount of time in
 *  milliseconds that a lookup will block for if Poolables are unavailable.  If the timeout expires
 *  before another thread releases a Poolable back to the Component Managaer then an Exception
 *  will be thrown.  A value of "0" specifies that the block will never timeout.
 *  (Defaults to "0")</li>
 *
 * <li>The <code>pool-trim-interval</code> attribute is used to
 * specify, in milliseconds, how long idle Poolables will be
 * maintained in the pool before being closed.  For a complete
 * explanation on how this works, see {@link
 * org.apache.avalon.excalibur.pool.ResourceLimitingPool#trim()}
 * (Defaults to "0", trimming disabled)</li>
 *
 * <li>The <code>pool-min</code> and <code>pool-grow</code> attributes
 * were deprecated as the underlying Pool ({@link
 * org.apache.avalon.excalibur.pool.ResourceLimitingPool}) does not
 * make use of them.  Configurations which still use these attributes
 * will continue to function however, a minimum pool size is no longer
 * applicable.
 *
 * </ul>
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.3 $ $Date: 2002/04/10 05:39:37 $
 * @since 4.0
 */
public class PoolableComponentHandler
    extends org.apache.avalon.excalibur.component.PoolableComponentHandler
    implements Instrumentable
{
    /** Instrumentable Name assigned to this Instrumentable */
    private String m_instrumentableName;

    /** Instrument used to profile the number of outstanding references. */
    private ValueInstrument m_referencesInstrument;
    
    /** Instrument used to profile the number of gets. */
    private CounterInstrument m_getsInstrument;
    
    /** Instrument used to profile the number of puts. */
    private CounterInstrument m_putsInstrument;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Create a PoolableComponentHandler which manages a pool of Components
     *  created by the specified factory object.
     *
     * @param factory The factory object which is responsible for creating the components
     *                managed by the ComponentHandler.
     * @param config The configuration to use to configure the pool.
     *
     * @throws Exception if the handler could not be created.
     */
    public PoolableComponentHandler( final DefaultComponentFactory factory,
                                     final Configuration config )
        throws Exception
    {
        super( factory, config );
        
        // Initialize the Instrumentable elements.
        m_referencesInstrument = new ValueInstrument( "references" );
        m_getsInstrument = new CounterInstrument( "gets" );
        m_putsInstrument = new CounterInstrument( "puts" );
    }

    /*---------------------------------------------------------------
     * ComponentHandler Methods
     *-------------------------------------------------------------*/
    /**
     * Get a reference of the desired Component.
     *
     * @return A component from the handler.
     *
     * @throws Exception If there was any problems getting a component.
     */
    protected Component doGet()
        throws Exception
    {
        Component component = super.doGet();

        // Notify the instrument manager
        m_getsInstrument.increment();
        // Reference count will be incremented after this returns
        m_referencesInstrument.setValue( getReferences() + 1 );
        
        return component;
    }

    /**
     * Return a reference of the desired Component.
     *
     * @param component Component to put back into the handler.
     */
    protected void doPut( final Component component )
    {
        // Notify the instrument manager
        m_putsInstrument.increment();
        // References decremented before this call.
        m_referencesInstrument.setValue( getReferences() );
        
        super.doPut( component );
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
            m_referencesInstrument,
            m_getsInstrument,
            m_putsInstrument
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
        // Child Instrumenatables are registered as they are found.
        return new Instrumentable[]
        {
            getPool()
        };
    }
}
