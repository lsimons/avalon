/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument.component;

import java.util.Collection;

import org.apache.avalon.excalibur.instrument.Instrument;
import org.apache.avalon.excalibur.instrument.Instrumentable;
import org.apache.avalon.excalibur.instrument.InstrumentManageable;
import org.apache.avalon.excalibur.instrument.InstrumentManager;
import org.apache.avalon.excalibur.component.ComponentHandler;
import org.apache.avalon.excalibur.component.ExcaliburComponentSelector;
import org.apache.avalon.excalibur.component.RoleManager;
import org.apache.avalon.excalibur.logger.LogKitManager;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;

/**
 *
 * @author <a href="mailto:leif@silveregg.co.jp">Leif Mortenson</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/03/29 14:03:49 $
 * @since 4.1
 */
public class InstrumentComponentSelector
    extends ExcaliburComponentSelector
    implements InstrumentManageable, Instrumentable
{
    private InstrumentManager m_instrumentManager;

    /** Instrumentable Name assigned to this Instrumentable */
    private String m_instrumentableName;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new InstrumentComponentSelector.
     */
    public InstrumentComponentSelector()
    {
        super();
    }

    /**
     * Creates a new InstrumentComponentSelector which will use the specified
     *  class loader to load all of its components.
     */
    public InstrumentComponentSelector( ClassLoader classLoader )
    {
        super( classLoader );
    }

    /*---------------------------------------------------------------
     * ExcaliburComponentSelector Methods
     *-------------------------------------------------------------*/
    /**
     * Obtain a new ComponentHandler for the specified component.  This method
     *  allows classes which extend the ExcaliburComponentSelector to use their
     *  own ComponentHandlers.
     *
     * @param componentClass Class of the component for which the handle is
     *                       being requested.
     * @param configuration The configuration for this component.
     * @param componentManager The ComponentManager which will be managing
     *                         the Component.
     * @param context The current context object.
     * @param roleManager The current RoleManager.
     * @param logkitManager The current LogKitManager.
     *
     * @throws Exception If there were any problems obtaining a ComponentHandler
     */
    protected ComponentHandler getComponentHandler( final Class componentClass,
                                                    final Configuration configuration,
                                                    final ComponentManager componentManager,
                                                    final Context context,
                                                    final RoleManager roleManager,
                                                    final LogKitManager logkitManager )
        throws Exception
    {
        String instrumentableName =
            configuration.getAttribute( "instrumentable", configuration.getAttribute( "name" ) );
        
        return InstrumentComponentHandler.getComponentHandler( componentClass,
                                                               configuration,
                                                               componentManager,
                                                               context,
                                                               roleManager,
                                                               logkitManager,
                                                               m_instrumentManager,
                                                               instrumentableName );
    }

    /*---------------------------------------------------------------
     * InstrumentManageable Methods
     *-------------------------------------------------------------*/
    /**
     * Sets the InstrumentManager for child components.  Can be for special
     * purpose components, however it is used mostly internally.
     *
     * @param instrumentManager The InstrumentManager for the component to use.
     */
    public void setInstrumentManager( final InstrumentManager instrumentManager )
    {
        m_instrumentManager = instrumentManager;
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
        return Instrumentable.EMPTY_INSTRUMENT_ARRAY;
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
        // Get the values. This set is created for this call and thus thread safe.
        Collection values = getComponentHandlers().values();
        Instrumentable[] children = new Instrumentable[ values.size() ];
        values.toArray( children );
        
        return children;
    }
}

