/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.component.servlet;

import org.apache.avalon.excalibur.logger.LoggerManager;
import org.apache.avalon.framework.logger.Logger;

/**
 * Reference Proxy to a LoggerManager
 *
 * @author <a href="mailto:leif@apache.org">Leif Mortenson</a>
 * @version CVS $Revision: 1.3 $ $Date: 2002/11/07 05:11:35 $
 * @since 4.2
 */
final class LoggerManagerReferenceProxy
    extends AbstractReferenceProxy
    implements LoggerManager
{
    private LoggerManager m_loggerManager;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Create a new proxy.
     *
     * @param componentManager LoggerManager being proxied.
     * @param latch Latch wich will be notified when this proxy is finalized.
     * @param name Name of the proxy.
     */
    LoggerManagerReferenceProxy( LoggerManager loggerManager,
                                 AbstractReferenceProxyLatch latch,
                                 String name )
    {
        super( latch, name );
        m_loggerManager = loggerManager;
    }

    /*---------------------------------------------------------------
     * LoggerManager Methods
     *-------------------------------------------------------------*/
    /**
     * Return the Logger for the specified category.
     */
    public Logger getLoggerForCategory( String categoryName )
    {
        return m_loggerManager.getLoggerForCategory( categoryName );
    }

    /**
     * Return the default Logger.  This is basically the same
     * as getting the Logger for the "" category.
     */
    public Logger getDefaultLogger()
    {
        return m_loggerManager.getDefaultLogger();
    }
}
