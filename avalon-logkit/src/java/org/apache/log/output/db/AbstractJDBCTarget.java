/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output.db;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.log.LogEvent;
import org.apache.log.output.AbstractTarget;

/**
 * Abstract JDBC target.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public abstract class AbstractJDBCTarget
    extends AbstractTarget
{
    ///Datasource to extract connections from
    private DataSource     m_dataSource;

    ///Database connection
    private Connection     m_connection;

    protected AbstractJDBCTarget( final DataSource dataSource )
    {
        m_dataSource = dataSource;
    }

    /**
     * Process a log event, via formatting and outputting it.
     *
     * @param event the log event
     */
    protected synchronized void doProcessEvent( final LogEvent event )
        throws Exception
    {
        checkConnection();

        if( isOpen() )
        {
            output( event );
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
    protected synchronized void open()
    {
        if( !isOpen() )
        {
            super.open();
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
            getErrorHandler().error( "Unable to open connection", throwable, null );
        }
    }

    /**
     * Utility method for subclasses to access connection.
     *
     * @return the Connection
     */
    protected final synchronized Connection getConnection()
    {
        return m_connection;
    }

    /**
     * Utility method to check connection and bring it back up if necessary.
     */
    protected final synchronized void checkConnection()
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
    protected synchronized boolean isStale()
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
    public synchronized void close()
    {
        if( isOpen() )
        {
            closeConnection();
            super.close();
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
                getErrorHandler().error( "Error shutting down JDBC connection", se, null );
            }

            m_connection = null;
        }
    }
}
