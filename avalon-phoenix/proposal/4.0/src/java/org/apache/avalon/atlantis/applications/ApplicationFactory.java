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
 * Convenience class for the creation of Applications.
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public interface ApplicationFactory
{
    /**
     * Provides a new Application.
     */
    public Application getApplication();
    /**
     * Provides a new Application on which contextualize() has
     * already been called (using the supplied context).
     */
    public Application getApplication( Context context );
    /**
     * Provides a new Application on which contextualize() and
     * compose() have already been called (using the supplied
     * context and componentManager).
     */
    public Application getApplication( Context context,
        ComponentManager componentManager );
    /**
     * Provides a new Application on which contextualize(),
     * compose() and configure() have already been called
     * (using the supplied context, componentManager, and
     * configuration).
     */
    public Application getApplication( Context context,
        ComponentManager componentManager,
        Configuration configuration );
}