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
 * be provided with parameters during its lifetime. This interface
 * will be called after Startable.start() and before
 * Startable.stop(). It is incompatible with the
 * Reconfigurable interface.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public interface Reparameterizable extends Parameterizable
{
    /**
     * Provide component with parameters.
     *
     * @param parameters the parameters
     * @exception ParameterException if parameters are invalid
     */
    void reparameterize( Parameters parameters )
        throws ParameterException;
}
