/*
 * Copyright (c) The Apache Software Foundation.  All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */

package org.apache.avalon.excalibur.monitor.test;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentSelector;
import org.apache.avalon.framework.logger.AbstractLoggable;

import org.apache.avalon.excalibur.testcase.ExcaliburTestCase;
import org.apache.avalon.excalibur.testcase.CascadingAssertionFailedError;
import org.apache.avalon.excalibur.monitor.Monitor;
import org.apache.avalon.excalibur.monitor.ActiveMonitor;
import org.apache.avalon.excalibur.monitor.PassiveMonitor;
import org.apache.avalon.excalibur.monitor.FileResource;

import org.apache.log.Priority;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.Writer;


/**
 * Junit TestCase for all the monitors in Excalibur.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version $Id: MonitorTestCase.java,v 1.1 2001/09/04 20:33:47 bloritsch Exp $
 */
public class MonitorTestCase extends ExcaliburTestCase
{
    /**
     * The constructor for the MonitorTest
     */
    public MonitorTestCase( String name )
    {
        super( name );
    }

    public void setUp()
        throws Exception
    {
        m_logPriority = Priority.DEBUG;
        super.setUp();
    }

    public void testActiveMonitor()
        throws CascadingAssertionFailedError
    {
        ComponentSelector selector = null;
        Monitor activeMonitor = null;

        try
        {
            selector = (ComponentSelector) manager.lookup( Monitor.ROLE + "Selector" );
            activeMonitor = (Monitor) selector.select( "active" );

            internalTestProcedure( activeMonitor, true );
        }
        catch ( ComponentException ce )
        {
            if ( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "There was an error in the ActiveMonitor test", ce );
            }

            throw new CascadingAssertionFailedError( "There was an error in the ActiveMonitor test", ce );
        }
        finally
        {
            assertTrue(  "The monitor selector could not be retrieved.", null != selector );

            selector.release( (Component) activeMonitor );
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
            selector = (ComponentSelector) manager.lookup( Monitor.ROLE + "Selector" );
            passiveMonitor = (Monitor) selector.select( "passive" );

            internalTestProcedure( passiveMonitor, false );
        }
        catch ( ComponentException ce )
        {
            if ( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "There was an error in the PassiveMonitor test", ce );
            }

            throw new CascadingAssertionFailedError( "There was an error in the PassiveMonitor test", ce );
        }
        finally
        {
            assertTrue( "The monitor selector could not be retrieved.", null != selector );

            selector.release( (Component) passiveMonitor );
            manager.release( selector );
        }
    }

    private void internalTestProcedure( Monitor testMonitor, boolean active )
    {
        try
        {
            long sleepTo;
            File thirdWheel = new File( "test.txt" );

            thirdWheel.createNewFile();

            MonitorTestCaseListener listener = new MonitorTestCaseListener();
            listener.setLogger( getLogger() );

            FileResource resource = new FileResource( "test.txt" );
            resource.addPropertyChangeListener( listener );

            testMonitor.addResource( resource );

            thirdWheel.setLastModified( System.currentTimeMillis() );

            if( active )
            {
                FileWriter externalWriter = new FileWriter( thirdWheel );
                externalWriter.write( "External Writer modification" );
                externalWriter.flush();
                externalWriter.close();

                sleepTo = System.currentTimeMillis() + 1000L;

                while( System.currentTimeMillis() < sleepTo && ( ! listener.hasBeenModified() ) )
                {
                    try
                    {
                        Thread.sleep( 50 );  // sleep 50 millis per iteration
                    }
                    catch( final InterruptedException ie )
                    {
                        // ignore and keep waiting
                    }
                }

                assertTrue( "File not changed", listener.hasBeenModified() );
            }

            listener.reset();

            OutputStream out = resource.setResourceAsStream();
            out.write( "Test line 1\n".getBytes() );

            try
            {
                Thread.sleep( 50 ); // sleep 50 millis at a time
            }
            catch( final InterruptedException ie )
            {
                // ignore and keep waiting
            }

            out.flush();
            out.close();

            sleepTo = System.currentTimeMillis() + 1000L;

            while( System.currentTimeMillis() < sleepTo && ( ! listener.hasBeenModified() ) )
            {
                try
                {
                    Thread.sleep( 50 ); // sleep 50 millis at a time
                }
                catch( final InterruptedException ie )
                {
                    // ignore and keep waiting
                }
            }

            assertTrue( "File not changed", listener.hasBeenModified() );
            listener.reset();

            Writer write = resource.setResourceAsWriter();
            write.write( "Test line 2\n" );

            try
            {
                Thread.sleep( 50 ); // sleep 50 millis at a time
            }
            catch( final InterruptedException ie )
            {
                // ignore and keep waiting
            }

            write.flush();
            write.close();

            sleepTo = System.currentTimeMillis() + 1000L;

            while( System.currentTimeMillis() < sleepTo && ( ! listener.hasBeenModified() ) )
            {
                try
                {
                    Thread.sleep( 50 ); // sleep 50 millis at a time
                }
                catch( final InterruptedException ie )
                {
                    // ignore and keep waiting
                }
            }

            assertTrue( "File not changed", listener.hasBeenModified() );
            listener.reset();

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

    public static class MonitorTestCaseListener
        extends AbstractLoggable
        implements PropertyChangeListener
    {
        private volatile boolean m_hasChanged = false;

        public boolean hasBeenModified()
        {
            return m_hasChanged;
        }

        public void reset()
        {
            m_hasChanged = false;
        }

        public void propertyChange( final PropertyChangeEvent propertyChangeEvent )
        {
            m_hasChanged = true;

            if( getLogger().isInfoEnabled() )
            {
                getLogger().info( "NOTIFICATION LATENCY: " + (System.currentTimeMillis() -
                                   ((Long)propertyChangeEvent.getNewValue()).longValue()) +
                                   "ms");
                getLogger().info( "Received notification for " +
                                  ((FileResource) propertyChangeEvent.getSource()).getResourceKey());
                getLogger().info( propertyChangeEvent.getPropertyName() +
                                  "\n  IS::" + (Long)propertyChangeEvent.getNewValue() +
                                  "\n  WAS::" + (Long)propertyChangeEvent.getOldValue() +
                                  "\n  TIME SINCE LAST MOD::" +
                                  (((Long)propertyChangeEvent.getNewValue()).longValue() -
                                  ((Long)propertyChangeEvent.getOldValue()).longValue()) );
            }
        }
    }
}
