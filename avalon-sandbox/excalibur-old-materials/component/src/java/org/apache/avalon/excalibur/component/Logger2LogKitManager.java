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
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.LogKit2AvalonLoggerAdapter;
import org.apache.log.Hierarchy;
import org.apache.log.LogTarget;

/**
 * An adapter between LogkitManager and LoggerManager.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.4 $ $Date: 2002/11/10 00:58:04 $
 */
class Logger2LogKitManager
    implements LogKitManager
{
    private final Hierarchy m_hierarchy = new Hierarchy();
    private final LoggerManager m_loggerManager;

    public Logger2LogKitManager( final LoggerManager loggerManager )
    {
        m_loggerManager = loggerManager;
        final LogKit2AvalonLoggerAdapter target =
            new LogKit2AvalonLoggerAdapter( loggerManager.getDefaultLogger() );
        m_hierarchy.setDefaultLogTarget( target );
    }

    public org.apache.log.Logger getLogger( final String categoryName )
    {
        final Logger logger =
            m_loggerManager.getLoggerForCategory( categoryName );
        final org.apache.log.Logger logkitLogger =
            getHierarchy().getLoggerFor( categoryName );
        final LogKit2AvalonLoggerAdapter target =
            new LogKit2AvalonLoggerAdapter( logger );
        logkitLogger.setLogTargets( new LogTarget[ ] { target } );
        return logkitLogger;
    }

    public Hierarchy getHierarchy()
    {
        return m_hierarchy;
    }
}
