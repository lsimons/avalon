/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.logger;

import org.apache.avalon.framework.logger.LogKitLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.log.Hierarchy;

/**
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 */
public class LogKitHierarchyLogger implements Logger
{
    private final Hierarchy m_hierarchy;

    private final org.apache.log.Logger m_root;

    public LogKitHierarchyLogger( Hierarchy hierarchy )
    {
        this.m_hierarchy = hierarchy;
        this.m_root = m_hierarchy.getLoggerFor( "" );
    }

    public void debug( String message )
    {
        this.m_root.debug( message );
    }

    public void debug( String message, Throwable throwable )
    {
        this.m_root.debug( message, throwable );
    }

    public void error( String message )
    {
        this.m_root.error( message );
    }

    public void error( String message, Throwable throwable )
    {
        this.m_root.error( message, throwable );
    }

    public void fatalError( String message )
    {
        this.m_root.fatalError( message );
    }

    public void fatalError( String message, Throwable throwable )
    {
        this.m_root.fatalError( message, throwable );
    }

    public Logger getChildLogger( String name )
    {
        return new LogKitLogger( this.m_hierarchy.getLoggerFor( name ) );
    }

    public void info( String message )
    {
        this.m_root.info( message );
    }

    public void info( String message, Throwable throwable )
    {
        this.m_root.info( message, throwable );
    }

    public boolean isDebugEnabled()
    {
        return this.m_root.isDebugEnabled();
    }

    public boolean isErrorEnabled()
    {
        return this.m_root.isErrorEnabled();
    }

    public boolean isFatalErrorEnabled()
    {
        return this.m_root.isFatalErrorEnabled();
    }

    public boolean isInfoEnabled()
    {
        return this.m_root.isInfoEnabled();
    }

    public boolean isWarnEnabled()
    {
        return this.m_root.isWarnEnabled();
    }

    public void warn( String message )
    {
        this.m_root.warn( message );
    }

    public void warn( String message, Throwable throwable )
    {
        this.m_root.warn( message, throwable );
    }
}
