/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.atlantis.applications;

import org.apache.framework.context.Context;
import org.apache.framework.component.ComponentManager;
import org.apache.framework.configuration.Configuration;

/**
 * Convenience class for the creation of ServerApplications.
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public interface ServerApplicationFactory
{
    /**
     * Provides a new Application.
     */
    public ServerApplication getApplication();
    /**
     * Provides a new Application on which contextualize() has
     * already been called (using the supplied context).
     */
    public ServerApplication getApplication( Context context );
    /**
     * Provides a new Application on which contextualize() and
     * compose() have already been called (using the supplied
     * context and componentManager).
     */
    public ServerApplication getApplication( Context context,
        ComponentManager componentManager );
    /**
     * Provides a new Application on which contextualize(),
     * compose() and configure() have already been called
     * (using the supplied context, componentManager, and
     * configuration).
     */
    public ServerApplication getApplication( Context context,
        ComponentManager componentManager,
        Configuration configuration );
}