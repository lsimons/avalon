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
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;

/**
 * A frontend for Phoenix that starts it as a native service
 * using the Java Service Wrapper at http://wrapper.sourceforge.net
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 */
public class DaemonLauncher
    implements WrapperListener, Observer
{
    /**
     * In order to avoid calling the Wrapper stop method recursively, we need
     *  to keep track of whether or not the Wrapper already knows we are
     *  stopping.  Necessary because of the way the shutdown process in
     *  Phoenix works.   Ideally, we would unregister this Observer with
     *  CLIMain but we can't do that for security reasons.
     */
    private boolean m_ignoreUpdates = false;
    
    public Integer start( final String[] args )
    {
        Integer exitCodeInteger = null;

        // This startup could take a while, so tell the wrapper to be patient.
        WrapperManager.signalStarting( 45000 );

        final Hashtable data = new Hashtable();
        data.put( Observer.class.getName(), this );

        if( WrapperManager.isDebugEnabled() )
        {
            System.out.println( "DaemonLauncher: Starting up Phoenix" );
        }

        try
        {
            int exitCode = Main.startup( args, data, false );
            if( exitCode != 0 )
            {
                exitCodeInteger = new Integer( exitCode );
            }

            if( WrapperManager.isDebugEnabled() )
            {
                System.out.println( "DaemonLauncher: Phoenix startup completed" );
            }
        }
        catch( final Exception e )
        {
            e.printStackTrace();
            exitCodeInteger = new Integer( 1 );
        }

        // We are almost up now, so reset the wait time
        WrapperManager.signalStarting( 2000 );

        return exitCodeInteger;
    }

    public int stop( final int exitCode )
    {
        // To avoid recursive calls, start ignoring updates.
        m_ignoreUpdates = true;
        
        Main.shutdown();
        return exitCode;
    }

    public void controlEvent( final int event )
    {
        if( WrapperManager.isControlledByNativeWrapper() )
        {
            if( WrapperManager.isDebugEnabled() )
            {
                System.out.println( "DaemonLauncher: controlEvent(" + event + ") - Ignored." );
            }

            // This application ignores all incoming control events.
            //  It relies on the wrapper code to handle them.
        }
        else
        {
            if( WrapperManager.isDebugEnabled() )
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
     * We use an Observer rather than operating on some more meaningful
     * event system as Observer and friends can be loaded from system
     * ClassLoader and thus the Embeddor does not have to share a common
     * classloader ancestor with invoker
     */
    public void update( final Observable observable, final Object arg )
    {
        if ( m_ignoreUpdates )
        {
            // Ignore this update
            if( WrapperManager.isDebugEnabled() )
            {
                System.out.println( "DaemonLauncher: " + arg +
                    " request ignored because stop already called." );
                System.out.flush();
            }
        }
        else
        {
            Thread.dumpStack();
            final String command = ( null != arg ) ? arg.toString() : "";
            if( command.equals( "restart" ) )
            {
                if( WrapperManager.isDebugEnabled() )
                {
                    System.out.println( "DaemonLauncher: restart requested." );
                    System.out.flush();
                }
    
                WrapperManager.restart();
    
                if( WrapperManager.isDebugEnabled() )
                {
                    //Should never get here???
                    System.out.println( "DaemonLauncher: restart completed." );
                    System.out.flush();
                }
            }
            else if( command.equals( "shutdown" ) )
            {
                if( WrapperManager.isDebugEnabled() )
                {
                    System.out.println( "DaemonLauncher: shutdown requested." );
                    System.out.flush();
                }
    
                WrapperManager.stop( 0 );
    
                if( WrapperManager.isDebugEnabled() )
                {
                    //Should never get here???
                    System.out.println( "DaemonLauncher: shutdown completed." );
                    System.out.flush();
                }
            }
            else
            {
                throw new IllegalArgumentException( "Unknown action " + command );
            }
        }
    }

    public static void main( final String[] args )
    {
        WrapperManager.start( new DaemonLauncher(), args );
    }
}
