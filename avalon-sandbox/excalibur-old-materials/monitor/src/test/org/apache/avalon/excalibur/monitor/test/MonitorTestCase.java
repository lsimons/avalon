/*
 * Copyright (c) The Apache Software Foundation.  All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.avalon.excalibur.monitor.test;

import java.io.File;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.Writer;
import org.apache.avalon.excalibur.monitor.FileResource;
import org.apache.avalon.excalibur.monitor.Monitor;
import org.apache.avalon.excalibur.testcase.CascadingAssertionFailedError;
import org.apache.avalon.excalibur.testcase.ExcaliburTestCase;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentSelector;

/**
 * Junit TestCase for all the monitors in Excalibur.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Id: MonitorTestCase.java,v 1.12 2002/09/07 12:14:02 donaldp Exp $
 */
public class MonitorTestCase
    extends ExcaliburTestCase
{
    /**
     * The constructor for the MonitorTest
     */
    public MonitorTestCase( String name )
    {
        super( name );
    }

    public void testActiveMonitor()
        throws CascadingAssertionFailedError
    {
        ComponentSelector selector = null;
        Monitor activeMonitor = null;

        try
        {
            selector = (ComponentSelector)manager.lookup( Monitor.ROLE + "Selector" );
            activeMonitor = (Monitor)selector.select( "active" );
            getLogger().info( "Aquired Active monitor" );
            internalTestProcedure( activeMonitor, true );
        }
        catch( final ComponentException ce )
        {
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "There was an error in the ActiveMonitor test", ce );
            }

            throw new CascadingAssertionFailedError( "There was an error in the ActiveMonitor test", ce );
        }
        finally
        {
            assertTrue( "The monitor selector could not be retrieved.", null != selector );
            selector.release( (Component)activeMonitor );
            manager.release( selector );
        }
    }

    public void testPassiveMonitor()
        throws CascadingAssertionFailedError
    {
        ComponentSelector selector = null;
        Monitor passiveMonitor = null;

        try
        {
            selector = (ComponentSelector)manager.lookup( Monitor.ROLE + "Selector" );
            passiveMonitor = (Monitor)selector.select( "passive" );
            getLogger().info( "Aquired Passive monitor" );
            internalTestProcedure( passiveMonitor, false );
        }
        catch( ComponentException ce )
        {
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "There was an error in the PassiveMonitor test", ce );
            }

            throw new CascadingAssertionFailedError( "There was an error in the PassiveMonitor test", ce );
        }
        finally
        {
            assertTrue( "The monitor selector could not be retrieved.", null != selector );

            selector.release( (Component)passiveMonitor );
            manager.release( selector );
        }
    }

    private void internalTestProcedure( final Monitor testMonitor,
                                        final boolean active )
    {
        try
        {
            final File thirdWheel = new File( "test.txt" );
            thirdWheel.createNewFile();
            thirdWheel.setLastModified( System.currentTimeMillis() );
            final MonitorTestCaseListener listener = new MonitorTestCaseListener();
            listener.enableLogging( getLogEnabledLogger() );

            final FileResource resource = new FileResource( "test.txt" );
            resource.addPropertyChangeListener( listener );

            testMonitor.addResource( resource );
            longDelay();

            if( active )
            {
                final FileWriter externalWriter = new FileWriter( thirdWheel );
                externalWriter.write( "External Writer modification" );
                externalWriter.flush();
                externalWriter.close();

                getLogger().info( "Checking for modification on active monitor" );
                checkForModification( listener );
            }

            final OutputStream out = resource.setResourceAsStream();
            out.write( "Test line 1\n".getBytes() );
            delay();
            out.flush();
            out.close();

            checkForModification( listener );

            final Writer write = resource.setResourceAsWriter();
            write.write( "Test line 2\n" );
            delay();
            write.flush();
            write.close();

            checkForModification( listener );

            resource.removePropertyChangeListener( listener );
            testMonitor.removeResource( resource );
            thirdWheel.delete();
        }
        catch( final Exception e )
        {
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Error running the test", e );
            }

            throw new CascadingAssertionFailedError( "Error running the test", e );
        }
    }

    private void delay()
    {
        delay( 10 );
    }

    /**
     * Some filesystems are not sensitive enough so you need
     * to delay for a long enough period of time (ie 1 second).
     */
    private void longDelay()
    {
        delay( 1000 );
    }

    private void delay( final int time )
    {
        try
        {
            Thread.sleep( time ); // sleep 10 millis at a time
        }
        catch( final InterruptedException ie )
        {
            // ignore and keep waiting
        }
    }

    private void checkForModification( final MonitorTestCaseListener listener )
    {
        final long sleepTo = System.currentTimeMillis() + 1000L;
        while( System.currentTimeMillis() < sleepTo &&
            (!listener.hasBeenModified()) )
        {
            delay();
        }
        assertTrue( "File not changed", listener.hasBeenModified() );
        listener.reset();
    }

}
