/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework.parameters;

/**
 * Components should implement this interface if they wish to
 * be provided with parameters during startup.
 * <p>
 * The Parameterizable interface is a light-weight alternative to the
 * {@link org.apache.avalon.framework.configuration.Configurable}
 * interface.  As such, the <code>Parameterizable</code> interface and
 * the <code>Configurable</code> interface are <strong>not</strong>
 * compatible.
 * </p><p>
 * This interface will be called after
 * <code>Composable.compose()</code> and before
 * <code>Initializable.initialize()</code>.
 * </p>


 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public interface Parameterizable
{
    /**
     * Provide component with parameters.
     *
     * @param parameters the parameters
     * @throws ParameterException if parameters are invalid
     */
    void parameterize( Parameters parameters )
        throws ParameterException;
}
