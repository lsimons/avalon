/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.launcher;

import java.util.Hashtable;
import java.util.Observer;
import java.util.Observable;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonController;

/**
 * A frontend for Phoenix that starts it as a native service
 * using the Jakarta commons-daemon.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 * @version $Revision: 1.2 $ $Date: 2002/02/28 17:11:16 $
 */
public class CommonsDaemon
    implements Daemon, Observer
{
    private DaemonContext m_context;
    private DaemonController m_controller;
    private String[] m_args;
    private boolean m_debugEnabled = false;

    public void init( final DaemonContext daemonContext )
        throws Exception
    {
        m_context = daemonContext;
        m_controller = m_context.getController();
        m_args = m_context.getArguments();
        for ( int i = 0; i < m_args.length; i++ )
        {
            if ( "-d".equals( m_args[ i ] ) || "--debug-init".equals( m_args[ i ] ) )
            {
                m_debugEnabled = true;
            }
        }
    }

    public void start()
        throws Exception
    {
        final Hashtable data = new Hashtable();
        data.put( Observer.class.getName(), this );

        Main.startup( m_context.getArguments(), data, false );
    }

    public void stop()
        throws Exception
    {
        Main.shutdown();
    }

    public void destroy()
    {
    }

    public void update( final Observable observable, final Object arg )
    {
        final String command = ( null != arg ) ? arg.toString() : "";
        if( command.equals( "restart" ) )
        {
            if ( m_debugEnabled )
            {
                log( "restart requested." );
            }

            m_controller.reload();

            if ( m_debugEnabled )
            {
                log( "restart completed." );
            }
        }
        else if ( command.equals( "shutdown" ) )
        {
            if ( m_debugEnabled )
            {
                log( "shutdown requested." );
            }

            m_controller.shutdown();

            if ( m_debugEnabled )
            {
                log( "shutdown completed." );
            }
        }
        else
        {
            throw new IllegalArgumentException( "Unknown action " + command );
        }
    }

    private void log( final String message )
    {
        System.out.print( "CommonsDaemon: " );
        System.out.println( message );
        System.out.flush();
    }
}