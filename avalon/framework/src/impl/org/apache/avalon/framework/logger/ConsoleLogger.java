/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework.logger;


/**
 * Logger sending everything to the standard output streams.
 * This is mainly for the cases when you have a utility that
 * does not have a logger to supply.
 *
 * @author <a href="mailto:leo.sutic@inspireinfrastructure.com">Leo Sutic</a>
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public final class ConsoleLogger implements Logger
{
    public final static int LEVEL_DEBUG = 0;
    public final static int LEVEL_INFO = 1;
    public final static int LEVEL_WARN = 2;
    public final static int LEVEL_ERROR = 3;
    public final static int LEVEL_FATAL = 4;
    public final static int LEVEL_DISABLED = 5;

    private final int m_logLevel;

    /**
     * Creates a new ConsoleLogger with the priority set to DEBUG.
     */
    public ConsoleLogger()
    {
        this( LEVEL_DEBUG );
    }

    /**
     * Creates a new ConsoleLogger.
     */
    public ConsoleLogger( final int logLevel )
    {
        m_logLevel = logLevel;
    }

    public void debug( final String message )
    {
        debug( message, null );
    }

    public void debug( final String message, final Throwable throwable )
    {
        if ( m_logLevel <= LEVEL_DEBUG )
        {
            System.out.print( "[DEBUG] " );
            System.out.println( message );

            if ( null != throwable )
            {
                throwable.printStackTrace( System.out );
            }
        }
    }

    public boolean isDebugEnabled()
    {
        return m_logLevel <= LEVEL_DEBUG;
    }

    public void info( final String message )
    {
        info( message, null );
    }

    public void info( final String message, final Throwable throwable )
    {
        if ( m_logLevel <= LEVEL_INFO )
        {
            System.out.print( "[INFO] " );
            System.out.println( message );

            if ( null != throwable )
            {
                throwable.printStackTrace( System.out );
            }
        }
    }

    public boolean isInfoEnabled()
    {
        return m_logLevel <= LEVEL_INFO;
    }

    public void warn( final String message )
    {
        warn( message, null );
    }

    public void warn(final String message, final Throwable throwable)
    {
        if ( m_logLevel <= LEVEL_WARN )
        {
            System.out.print( "[WARNING] " );
            System.out.println( message );

            if ( null != throwable )
            {
                throwable.printStackTrace( System.out );
            }
        }
    }

    public boolean isWarnEnabled()
    {
        return m_logLevel <= LEVEL_WARN;
    }

    public void error( final String message )
    {
        error( message, null );
    }

    public void error( final String message, final Throwable throwable )
    {
        if ( m_logLevel <= LEVEL_ERROR )
        {
            System.out.print( "[ERROR] " );
            System.out.println( message );

            if ( null != throwable )
            {
                throwable.printStackTrace( System.out );
            }
        }
    }

    public boolean isErrorEnabled()
    {
        return m_logLevel <= LEVEL_ERROR;
    }

    public void fatalError( final String message )
    {
        fatalError( message, null );
    }

    public void fatalError( final String message, final Throwable throwable )
    {
        if ( m_logLevel <= LEVEL_FATAL )
        {
            System.out.print( "[FATAL ERROR] " );
            System.out.println( message );

            if ( null != throwable )
            {
                throwable.printStackTrace( System.out );
            }
        }
    }

    public boolean isFatalErrorEnabled()
    {
        return m_logLevel <= LEVEL_FATAL;
    }

    public Logger getChildLogger( final String name )
    {
        return this;
    }
}
