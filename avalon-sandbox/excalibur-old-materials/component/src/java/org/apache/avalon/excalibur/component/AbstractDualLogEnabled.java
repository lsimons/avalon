/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.component;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.LogKitLogger;
import org.apache.avalon.framework.logger.Loggable;

/**
 * A base class for all objects that need to support LogEnabled/Loggable
 * for backwards compatability.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.1.2.1 $ $Date: 2002/05/18 05:31:08 $
 */
class AbstractDualLogEnabled
    extends AbstractLogEnabled
    implements Loggable
{
    private org.apache.log.Logger m_logkitLogger;

    public void setLogger( org.apache.log.Logger logger )
    {
        m_logkitLogger = logger;
        enableLogging( new LogKitLogger( logger ) );
    }

    protected final org.apache.log.Logger getLogkitLogger()
    {
        if( null == m_logkitLogger )
        {
            m_logkitLogger = LogKit2LoggerTarget.createLogger( getLogger() );
        }
        return m_logkitLogger;
    }
}
