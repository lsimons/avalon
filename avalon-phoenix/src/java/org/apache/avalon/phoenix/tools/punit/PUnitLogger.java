/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.punit;

import org.apache.avalon.framework.logger.Logger;

import java.util.ArrayList;

/**
 * PunitLogger
 *
 * @author Paul Hammant
 */
public class PUnitLogger
    implements Logger
{
    private ArrayList m_messages = new ArrayList();

    /**
     * Get a logged entry.
     * @param startsWith This term
     * @return The full term
     */
    public String get( String startsWith )
    {
        final int size = m_messages.size();
        for( int i = 0; i < size; i++ )
        {
            final String message = (String)m_messages.get( i );
            if( message.startsWith( startsWith ) )
            {
                return message;
            }
        }
        return null;
    }

    /**
     * Contains a logged entry
     *
     * @param message The term
     * @return true or not.
     */
    public boolean contains( final String message )
    {
        return get( message ) != null;
    }

    /**
     * Debug an entry as per Loggable
     * @param message the term
     */
    public void debug( final String message )
    {
        m_messages.add( "D:" + message );
    }

    /**
     * Debug an entry as per Loggable
     *
     * @param message the term
     * @param throwable An exception
     */
    public void debug( final String message,
                       final Throwable throwable )
    {
        m_messages.add( "D:" + message + ":" + throwable != null ? throwable.getMessage() : "" );
    }

    public boolean isDebugEnabled()
    {
        return true;
    }

    /**
     * Info an entry as per Loggable
     * @param message the term
     */
    public void info( final String message )
    {
        m_messages.add( "I:" + message );
    }

    /**
     * Info an entry as per Loggable
     * @param message the term
     * @param throwable An exception
     */
    public void info( final String message, final Throwable throwable )
    {
        m_messages.add( "I:" + message + ":" + throwable != null ? throwable.getMessage() : "" );
    }

    /**
     * Is Info Enabled
     * @return
     */
    public boolean isInfoEnabled()
    {
        return true;
    }

    /**
     * Warn an entry as per Loggable
     *
     * @param message the term
     */
    public void warn( final String message )
    {
        m_messages.add( "W:" + message );
    }

    /**
     * Warn an entry as per Loggable
     * @param message the term
     * @param throwable An exception
     */
    public void warn( final String message, final Throwable throwable )
    {
        m_messages.add( "W:" + message + ":" + throwable != null ? throwable.getMessage() : "" );
    }

    /**
     * Is Warn Enabled
     * @return
     */
    public boolean isWarnEnabled()
    {
        return false;
    }

    /**
     * Error an entry as per Loggable
     *
     * @param message the term
     */
    public void error( final String message )
    {
        m_messages.add( "E:" + message );
    }

    /**
     * Error an entry as per Loggable
     * @param message the term
     * @param throwable An exception
     */
    public void error( final String message, final Throwable throwable )
    {
        m_messages.add( "E:" + message + ":" + throwable != null ? throwable.getMessage() : "" );
    }

    /**
     * Is Error Enabled
     * @return
     */
    public boolean isErrorEnabled()
    {
        return true;
    }

    /**
     * Log a fatal error as per Loggable
     * @param message the term
     */
    public void fatalError( final String message )
    {
        m_messages.add( "F:" + message );
    }

    /**
     * Log a fatal error entry as per Loggable
     * @param message the term
     * @param throwable An exception
     */
    public void fatalError( final String message, final Throwable throwable )
    {
        m_messages.add( "F:" + message + ":" + throwable != null ? throwable.getMessage() : "" );
    }

    /**
     * Is Fatal Error Enabled
     * @return
     */
    public boolean isFatalErrorEnabled()
    {
        return true;
    }

    /**
     * Gtet the child logger
     *
     * @param name The hint to use (ignored)
     * @return The child logger.
     */
    public Logger getChildLogger( final String name )
    {
        return this;
    }
}
