/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument.component;

import org.apache.avalon.excalibur.instrument.Instrument;
import org.apache.avalon.excalibur.instrument.Instrumentable;
import org.apache.avalon.excalibur.instrument.InstrumentManager;
import org.apache.avalon.excalibur.component.ComponentHandler;
import org.apache.avalon.excalibur.component.DefaultComponentFactory;
//import org.apache.avalon.excalibur.component.DefaultComponentHandler;
//import org.apache.avalon.excalibur.component.PoolableComponentHandler;
import org.apache.avalon.excalibur.component.RoleManager;
//import org.apache.avalon.excalibur.component.ThreadSafeComponentHandler;
import org.apache.avalon.excalibur.logger.LogKitManager;
import org.apache.avalon.excalibur.pool.Poolable;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.thread.SingleThreaded;
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.4 $ $Date: 2002/04/03 13:18:29 $
 * @since 4.1
 */
public abstract class InstrumentComponentHandler
    extends ComponentHandler
    implements Instrumentable
{
    /** Instrumentable Name assigned to this Instrumentable */
    private String m_instrumentableName = "component-manager";
    
    /*---------------------------------------------------------------
     * Static Methods
     *-------------------------------------------------------------*/
    /**
     *
     *
     * @param componentClass Class of the component for which the handle is
     *                       being requested.
     * @param configuration The configuration for this component.
     * @param componentManager The ComponentManager which will be managing
     *                         the Component.
     * @param context The current context object.
     * @param roleManager The current RoleManager.
     * @param logkitManager The current LogKitManager.
     * @param instrumentManager The current InstrumentManager.
     * @param instrumentableName The name of the handler.
     *
     * @throws Exception If there were any problems obtaining a ComponentHandler
     */
    public static ComponentHandler getComponentHandler(
        final Class componentClass,
        final Configuration config,
        final ComponentManager componentManager,
        final Context context,
        final RoleManager roleManager,
        final LogKitManager logkitManager,
        final InstrumentManager instrumentManager,
        final String instrumentableName )
        throws Exception
    {
        int numInterfaces = 0;

        if( SingleThreaded.class.isAssignableFrom( componentClass ) )
        {
            numInterfaces++;
        }

        if( ThreadSafe.class.isAssignableFrom( componentClass ) )
        {
            numInterfaces++;
        }

        if( Poolable.class.isAssignableFrom( componentClass ) )
        {
            numInterfaces++;
        }

        if( numInterfaces > 1 )
        {
            throw new Exception( "[CONFLICT] lifestyle interfaces: " + componentClass.getName() );
        }

        // Create the factory to use to create the instances of the Component.
        DefaultComponentFactory factory =
            new InstrumentDefaultComponentFactory( componentClass,
                                                   config,
                                                   componentManager,
                                                   context,
                                                   roleManager,
                                                   logkitManager,
                                                   instrumentManager,
                                                   instrumentableName );

        InstrumentComponentHandler handler;
        if( Poolable.class.isAssignableFrom( componentClass ) )
        {
            handler = new PoolableComponentHandler( factory, config );
        }
        else if( ThreadSafe.class.isAssignableFrom( componentClass ) )
        {
            handler = new ThreadSafeComponentHandler( factory, config );
        }
        else // This is a SingleThreaded component
        {
            handler = new DefaultComponentHandler( factory, config );
        }
        
        // Register the new handler with the instrumentManager if it exists.
        handler.setInstrumentableName( instrumentableName );
        
        return handler;
    }


    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new InstrumentComponentHandler.
     */
    public InstrumentComponentHandler()
    {
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

    /*
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
    public abstract Instrument[] getInstruments();

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
        return Instrumentable.EMPTY_INSTRUMENTABLE_ARRAY;
    }

    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
}

