/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument.component;

import org.apache.avalon.excalibur.instrument.Instrumentable;
import org.apache.avalon.excalibur.instrument.InstrumentManageable;
import org.apache.avalon.excalibur.instrument.InstrumentManager;
import org.apache.avalon.excalibur.component.DefaultComponentFactory;
import org.apache.avalon.excalibur.component.RoleManager;
import org.apache.avalon.excalibur.logger.LogKitManager;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;

/**
 * Factory for Avalon Instrumentable components.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.4 $ $Date: 2002/04/03 13:18:29 $
 * @since 4.0
 */
public class InstrumentDefaultComponentFactory
    extends DefaultComponentFactory
{
    private InstrumentManager m_instrumentManager;
    
    /** Instrumentable Name assigned to objects created by this factory. */
    private String m_instrumentableName;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Construct a new component factory for the specified component.
     *
     * @param componentClass the class to instantiate (must have a default constructor).
     * @param configuration the <code>Configuration</code> object to pass to new instances.
     * @param componentManager the component manager to pass to <code>Composable</code>s.
     * @param context the <code>Context</code> to pass to <code>Contexutalizable</code>s.
     * @param roles the <code>RoleManager</code> to pass to 
     *              <code>DefaultComponentSelector</code>s.
     * @param instrumentManager the <code>InstrumentManager</code> to register the component
     *                          with if it is a Instrumentable.
     * @param instrumentableName The name of the handler.
     */
    public InstrumentDefaultComponentFactory( final Class componentClass,
                                              final Configuration configuration,
                                              final ComponentManager componentManager,
                                              final Context context,
                                              final RoleManager roles,
                                              final LogKitManager logkit,
                                              final InstrumentManager instrumentManager,
                                              final String instrumentableName )

    {
        super( componentClass, configuration, componentManager, context, roles, logkit );

        m_instrumentManager = instrumentManager;
        m_instrumentableName = instrumentableName;
    }


    /*---------------------------------------------------------------
     * DefaultComponentFactory Methods
     *-------------------------------------------------------------*/
    /**
     * Called after a new component is initialized, but before it is started.  This was added
     *  to make it possible to implement the InstrumentComponentFactory without too much duplicate
     *  code.  WARNING:  Do not take advantage of this method as it will most likely be removed.
     */
    protected void postLogger( Object component, Configuration configuration )
        throws Exception
    {
        if( component instanceof InstrumentManageable )
        {
            ( (InstrumentManageable)component ).setInstrumentManager( m_instrumentManager );
        }
    }

    /**
     * Called after a new component is initialized, but before it is started.  This was added
     *  to make it possible to implement the InstrumentComponentFactory without too much duplicate
     *  code.  WARNING:  Do not take advantage of this method as it will most likely be removed.
     */
    protected void postInitialize( Object component, Configuration configuration )
        throws Exception
    {
        if( component instanceof Instrumentable )
        {
            getLogger().debug( "Doing instrument setup for: " + component );
            
            m_instrumentManager.registerInstrumentable( (Instrumentable)component, m_instrumentableName );
        }
    }

    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
}
