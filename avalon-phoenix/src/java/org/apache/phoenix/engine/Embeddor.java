/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine;

import org.apache.avalon.Disposable;
import org.apache.avalon.Initializable;
import org.apache.avalon.configuration.ConfigurationException;
import org.apache.avalon.configuration.Parameters;

/**
 * This is the object that is interacted with to create, manage and
 * dispose of the kernel and related resources.
 *
 * Note that this will eventually be moved to atlantis package.
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 * @author <a href="donaldp@apache.org">Peter Donald</a>
 */
public interface Embeddor
    extends Initializable, Disposable
{
    /**
     * Kset parameters for this component.
     * This must be called before initialize()
     *
     * @param parameters the Parameters for embeddor
     * @exception ConfigurationException if an error occurs
     */
    void setParameters( Parameters parameters )
        throws ConfigurationException;

    /**
     * After the Embeddor is initialized, this method is called to actually
     * do the work. It will return when the embeddor is ready to be disposed.
     *
     * @exception Exception if an error occurs
     */
    void execute()
        throws Exception;
}
