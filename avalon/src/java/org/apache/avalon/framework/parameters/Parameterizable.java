/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.parameters;

/**
 * Components should implement this interface if they wish to 
 * be provided with parameters during startup. This interface
 * will be called after Composable.compose() and before 
 * Initializable.initialize(). It is incompatible with the 
 * Configurable interface.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Parameterizable
{
    /**
     * Provide component with parameters.
     *
     * @param parameters the parameters
     * @exception ParameterException if parameters are invalid
     */
    void parameterize( Parameters parameters )
        throws ParameterException;
}
