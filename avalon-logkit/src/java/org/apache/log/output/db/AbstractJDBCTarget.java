/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output.db;

import java.sql.SQLException;
import java.sql.Connection;
import javax.sql.DataSource;
import org.apache.log.Hierarchy;
import org.apache.log.LogEvent;
import org.apache.log.LogTarget;

/**
 * Abstract JDBC target.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public abstract class AbstractJDBCTarget
    implements LogTarget
{
    ///Flag indicating that log session is finished (aka target has been closed)
    private boolean        m_isOpen;

    ///Datasource to extract connections from
    private DataSource     m_dataSource;

    ///Database connection
    private Connection     m_connection;

    protected AbstractJDBCTarget( final DataSource dataSource )
    {
        m_dataSource = dataSource;
    }

    /**
     * Check if database connection is open.
     *
     * @return true if open, false otherwise
     */
    protected boolean isOpen()
    {
        return m_isOpen;
    }

    /**
     * Process a log event, via formatting and outputting it.
     *
     * @param event the log event
     */
    public void processEvent( final LogEvent event )
    {
        if( !isOpen() )
        {
            error( "Writing event to closed stream.", null );
            return;
        }

        try
        {
            checkConnection();

            if( isOpen() )
            {
                output( event );
            }
        }
        catch( final Throwable throwable )
        {
            error( "Unknown error writing event.", throwable );
        }
    }

    /**
     * Output a log event to DB.
     * This must be implemented by subclasses.
     *
     * @param event the log event.
     */
    protected abstract void output( LogEvent event );

    /**
     * Startup log session.
     *
     */
    protected void open()
    {
        if( !isOpen() )
        {
            m_isOpen = true;
            openConnection();
        }
    }

    /**
     * Open connection to underlying database.
     *
     */
    protected synchronized void openConnection()
    {
        try
        {
            m_connection = m_dataSource.getConnection();
        }
        catch( final Throwable throwable )
        {
            error( "Unable to open connection", throwable );
        }
    }

    /**
     * Utility method for subclasses to access connection.
     *
     * @return the Connection
     */
    protected final Connection getConnection()
    {
        return m_connection;
    }

    /**
     * Utility method to check connection and bring it back up if necessary.
     */
    protected final void checkConnection()
    {
        if( isStale() )
        {
            closeConnection();
            openConnection();
        }
    }

    /**
     * Detect if connection is stale and should be reopened.
     *
     * @return true if connection is stale, false otherwise
     */
    protected boolean isStale()
    {
        if( null == m_connection ) return true;

        try
        {
            if( m_connection.isClosed() ) return true;
        }
        catch( final SQLException se )
        {
            return true;
        }

        return false;
    }
    
    /**
     * Shutdown target.
     * Attempting to write to target after close() will cause errors to be logged.
     *
     */
    public void close()
    {
        if( isOpen() )
        {
            closeConnection();
            m_isOpen = false;
        }
    }

    /**
     * Close connection to underlying database.
     *
     */
    protected synchronized void closeConnection()
    {
        if( null != m_connection )
        {
            try
            { 
                m_connection.close(); 
            }
            catch( final SQLException se )
            {
                error( "Error shutting down JDBC connection", se );
            }

            m_connection = null;
        }
    }

    /**
     * Helper method to write error messages to error handler.
     *
     * @param message the error message
     * @param throwable the exception if any
     */
    protected final void error( final String message, final Throwable throwable )
    {
        Hierarchy.getDefaultHierarchy().log( message, throwable );
        //TODO:
        //Can no longer route to global error handler - somehow need to pass down error
        //handler from engine...
    }
}
