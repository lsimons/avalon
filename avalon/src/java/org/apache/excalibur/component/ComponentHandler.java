/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.component;

import org.apache.avalon.logger.AbstractLoggable;
import org.apache.avalon.thread.ThreadSafe;
import org.apache.avalon.thread.SingleThreaded;
import org.apache.avalon.Initializable;
import org.apache.avalon.Disposable;
import org.apache.avalon.component.Component;
import org.apache.avalon.configuration.Configuration;
import org.apache.avalon.component.ComponentManager;
import org.apache.avalon.context.Context;
import org.apache.excalibur.pool.Poolable;

/**
 * The DefaultComponentHandler to make sure components are initialized
 * and destroyed correctly.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.1 $ $Date: 2001/04/20 20:48:33 $
 */
public abstract class ComponentHandler extends AbstractLoggable
                                       implements Initializable, Disposable {

    public static ComponentHandler getComponentHandler(
                             final Class componentClass,
                             final Configuration config,
                             final ComponentManager manager,
                             final Context context,
                             final RoleManager roles )
    throws Exception
    {
        int numInterfaces = 0;

        if (SingleThreaded.class.isAssignableFrom(componentClass))
        {
            numInterfaces++;
        }

        if (ThreadSafe.class.isAssignableFrom(componentClass))
        {
            numInterfaces++;
        }

        if (Poolable.class.isAssignableFrom(componentClass))
        {
            numInterfaces++;
        }

        if (numInterfaces > 1)
        {
            throw new Exception("[CONFLICT] lifestyle interfaces: " + componentClass.getName());
        }

        if (Poolable.class.isAssignableFrom(componentClass))
        {
            return new PoolableComponentHandler(componentClass,
                                                config,
                                                manager,
                                                context,
                                                roles);
        }
        else if (ThreadSafe.class.isAssignableFrom(componentClass))
        {
            return new ThreadSafeComponentHandler(componentClass,
                                                  config,
                                                  manager,
                                                  context,
                                                  roles);
        }
        else // This is a SingleThreaded component
        {
            return new DefaultComponentHandler(componentClass,
                                               config,
                                               manager,
                                               context,
                                               roles);
        }
    }

    public static ComponentHandler getComponentHandler(
                             final Component componentInstance )
    throws Exception
    {
        int numInterfaces = 0;

        if (SingleThreaded.class.isAssignableFrom(componentInstance.getClass()))
        {
            numInterfaces++;
        }

        if (ThreadSafe.class.isAssignableFrom(componentInstance.getClass()))
        {
            numInterfaces++;
        }

        if (Poolable.class.isAssignableFrom(componentInstance.getClass()))
        {
            numInterfaces++;
        }

        if (numInterfaces > 1)
        {
            throw new Exception("[CONFLICT] lifestyle interfaces: " + componentInstance.getClass().getName());
        }

        return new ThreadSafeComponentHandler(componentInstance);
    }

    public abstract Component get() throws Exception;

    public abstract void put(Component component) throws Exception;
}