/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.component;

import org.apache.avalon.excalibur.logger.LogKitManager;
import org.apache.avalon.excalibur.logger.LoggerManager;
import org.apache.avalon.framework.logger.LogKitLogger;
import org.apache.avalon.framework.logger.Logger;

/**
 *
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.1.2.1 $ $Date: 2002/05/18 05:55:04 $
 */
public class LogkitLoggerManager
    implements LoggerManager
{
    private LoggerManager m_loggerManager;
    private LogKitManager m_logKitManager;

    public LogkitLoggerManager( final LoggerManager loggerManager,
                                final LogKitManager logKitManager )
    {
        m_loggerManager = loggerManager;
        m_logKitManager = logKitManager;
    }

    public org.apache.log.Logger
        getLogKitLoggerForCategory( final String categoryName )
    {
        if( null != m_logKitManager )
        {
            return m_logKitManager.getLogger( categoryName );
        }
        else
        {
            final Logger logger =
                m_loggerManager.getLoggerForCategory( categoryName );
            return LogKit2LoggerTarget.createLogger( logger );
        }
    }

    public Logger getLoggerForCategory( String categoryName )
    {
        if( null != m_logKitManager )
        {
            final org.apache.log.Logger logger =
                m_logKitManager.getLogger( categoryName );
            return new LogKitLogger( logger );
        }
        else
        {
            return m_loggerManager.getLoggerForCategory( categoryName );
        }
    }

    public Logger getDefaultLogger()
    {
        return getLoggerForCategory( "" );
    }

    LogKitManager getLogKitManager()
    {
        if( null == m_logKitManager )
        {
            m_logKitManager = new Logger2LogKitManager( m_loggerManager );
        }
        return m_logKitManager;
    }
}
