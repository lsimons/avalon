/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.classloader;

import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.excalibur.policy.verifier.PolicyVerifier;

/**
 * A simple adapter for verifier to support logging to
 * Phoenixs subsystem.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2002/10/02 11:25:55 $
 */
class SarPolicyVerifier
    extends PolicyVerifier
    implements LogEnabled
{
    private Logger m_logger;

    public void enableLogging( final Logger logger )
    {
        m_logger = logger;
    }

    protected void info( final String message )
    {
        m_logger.info( message );
    }
}
