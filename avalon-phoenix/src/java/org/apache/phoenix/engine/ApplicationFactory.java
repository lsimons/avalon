/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine;

import org.apache.avalon.atlantis.Application;

/**
 * This interface is used to create an Application.
 * The application created is usually specific to a particular kernel.
 *
 * Note this should eventually be moved to Atlantis.
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface ApplicationFactory
{
    /**
     * Create new Application.
     *
     * @return the new Application
     */
    Application createApplication();
}
