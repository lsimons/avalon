/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument.component;

import org.apache.avalon.excalibur.instrument.InstrumentManageable;
import org.apache.avalon.excalibur.instrument.InstrumentManager;
import org.apache.avalon.excalibur.component.ComponentHandler;
import org.apache.avalon.excalibur.component.ExcaliburComponentManager;
import org.apache.avalon.excalibur.component.RoleManager;
import org.apache.avalon.excalibur.logger.LogKitManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;

/**
 *
 * @author <a href="mailto:leif@silveregg.co.jp">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/03/26 11:56:16 $
 * @since 4.1
 */
public class InstrumentComponentManager
    extends ExcaliburComponentManager
    implements InstrumentManageable
{
    private InstrumentManager m_instrumentManager;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new InstrumentComponentManager.
     */
    public InstrumentComponentManager()
    {
        super();
    }

    /**
     * Creates a new InstrumentComponentManager which will use the specified
     *  class loader to load all of its components.
     */
    public InstrumentComponentManager( ClassLoader classLoader )
    {
        super( classLoader );
    }

    /*---------------------------------------------------------------
     * ExcaliburComponentManager Methods
     *-------------------------------------------------------------*/
    /**
     * Obtain a new ComponentHandler for the specified component.  This method
     *  allows classes which extend the ExcaliburComponentManager to use their
     *  own ComponentHandlers.
     *
     * @param componentClass Class of the component for which the handle is
     *                       being requested.
     * @param configuration The configuration for this component.
     * @param context The current context object.
     * @param roleManager The current RoleManager.
     * @param logkitManager The current LogKitManager.
     *
     * @throws Exception If there were any problems obtaining a ComponentHandler
     */
    protected ComponentHandler getComponentHandler( final Class componentClass,
                                                    final Configuration configuration,
                                                    final Context context,
                                                    final RoleManager roleManager,
                                                    final LogKitManager logkitManager )
        throws Exception
    {
        if( m_instrumentManager == null )
        {
            throw new IllegalStateException( "The InstrumentManager was not yet set." );
        }

        return InstrumentComponentHandler.getComponentHandler( componentClass,
                                                               configuration,
                                                               this,
                                                               context,
                                                               roleManager,
                                                               logkitManager,
                                                               m_instrumentManager );
    }

    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Sets the InstrumentManager for child components.  Can be for special
     * purpose components, however it is used mostly internally.
     *
     * @param instrumentManager The InstrumentManager for the component to use.
     */
    public void setInstrumentManager( InstrumentManager instrumentManager )
    {
        m_instrumentManager = instrumentManager;
    }
}

