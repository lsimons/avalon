/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.launcher;

import com.silveregg.wrapper.WrapperListener;
import com.silveregg.wrapper.WrapperManager;

/**
 * A frontend for Phoenix that starts it as a native service
 * using the Java Service Wrapper at http://wrapper.sourceforge.net
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class DaemonLauncher
    implements WrapperListener, Runnable
{
    private String[] m_args;

    public Integer start( final String[] args )
    {
        // This startup could take a while, so tell the wrapper to be patient.
        WrapperManager.signalStarting( 45000 );

        m_args = args;

        final Thread thread = new Thread( this );
        thread.start();

        // We are almost up now, so reset the wait time
        WrapperManager.signalStarting( 2000 );

        return null;
    }
    
    public void run()
    {
        try { Main.main( m_args ); }
        catch( final Exception e )
        {
            e.printStackTrace();
        }
    }

    public int stop( final int exitCode )
    {
        Main.shutdown();
        return exitCode;
    }

    public void controlEvent( final int event )
    {
        if( WrapperManager.isControlledByNativeWrapper() )
        {
            // This application ignores all incoming control events.
            //  It relies on the wrapper code to handle them.
        }
        else
        {
            WrapperManager.stop( 0 );
        }
    }

    public static void main( final String[] args )
    {
        WrapperManager.start( new DaemonLauncher(), args );
    }
}
