/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument.component;

import org.apache.avalon.excalibur.instrument.InstrumentManager;
import org.apache.avalon.excalibur.component.ComponentHandler;
import org.apache.avalon.excalibur.component.DefaultComponentFactory;
import org.apache.avalon.excalibur.component.DefaultComponentHandler;
import org.apache.avalon.excalibur.component.PoolableComponentHandler;
import org.apache.avalon.excalibur.component.RoleManager;
import org.apache.avalon.excalibur.component.ThreadSafeComponentHandler;
import org.apache.avalon.excalibur.logger.LogKitManager;
import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.thread.SingleThreaded;
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 *
 * @author <a href="mailto:leif@silveregg.co.jp">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/03/26 11:56:16 $
 * @since 4.1
 */
public abstract class InstrumentComponentHandler
    extends ComponentHandler
{
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
        final InstrumentManager instrumentManager )
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
                                                   instrumentManager );

        if( Poolable.class.isAssignableFrom( componentClass ) )
        {
            return new PoolableComponentHandler( factory, config );
        }
        else if( ThreadSafe.class.isAssignableFrom( componentClass ) )
        {
            return new ThreadSafeComponentHandler( factory, config );
        }
        else // This is a SingleThreaded component
        {
            return new DefaultComponentHandler( factory, config );
        }
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
     * ComponentHandler Methods
     *-------------------------------------------------------------*/

    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
}

