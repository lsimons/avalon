/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.launcher;

import com.silveregg.wrapper.WrapperListener;
import com.silveregg.wrapper.WrapperManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

/**
 * A frontend for Phoenix that starts it as a native service
 * using the Java Service Wrapper at http://wrapper.sourceforge.net
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class DaemonLauncher
    implements WrapperListener, ActionListener
{
    public Integer start( final String[] args )
    {
        // This startup could take a while, so tell the wrapper to be patient.
        WrapperManager.signalStarting( 45000 );

        final Hashtable data = new Hashtable();
        data.put( ActionListener.class.getName(), this );

        if ( WrapperManager.isDebugEnabled() )
        {
            System.out.println( "DaemonLauncher: Starting up Phoenix" );
        }

        try { Main.startup( args, data ); }
        catch( final Exception e )
        {
            e.printStackTrace();
        }

        if ( WrapperManager.isDebugEnabled() )
        {
            System.out.println( "DaemonLauncher: Phoenix startup completed" );
        }

        // We are almost up now, so reset the wait time
        WrapperManager.signalStarting( 2000 );

        return null;
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
            if ( WrapperManager.isDebugEnabled() )
            {
                System.out.println( "DaemonLauncher: controlEvent(" + event + ") - Ignored." );
            }
            
            // This application ignores all incoming control events.
            //  It relies on the wrapper code to handle them.
        }
        else
        {
            if ( WrapperManager.isDebugEnabled() )
            {
                System.out.println( "DaemonLauncher: controlEvent(" + event + ") - Stopping." );
            }
            
            // Not being run under a wrapper, so this isn't an NT service and should always exit.
            //  Handle the event here.
            WrapperManager.stop( 0 );
            // Will not get here.
        }
    }

    /**
     * We use an ActionListener rather than operating on some more meaningful
     * event system as ActionListener and friends can be loaded from system
     * ClassLoader and thus the Embeddor does not have to share a common
     * classloader ancestor with invoker
     */
    public void actionPerformed( final ActionEvent action )
    {
        final String command = action.getActionCommand();
        if( command.equals( "restart" ) )
        {
            if ( WrapperManager.isDebugEnabled() )
            {
                System.out.println( "DaemonLauncher: restart requested." );
                System.out.flush();
            }

            WrapperManager.restart();

            if ( WrapperManager.isDebugEnabled() )
            {
                //Should never get here???
                System.out.println( "DaemonLauncher: restart completed." );
                System.out.flush();
            }
        }
        else
        {
            throw new IllegalArgumentException( "Unknown action " + command );
        }
    }

    public static void main( final String[] args )
    {
        WrapperManager.start( new DaemonLauncher(), args );
    }
}
