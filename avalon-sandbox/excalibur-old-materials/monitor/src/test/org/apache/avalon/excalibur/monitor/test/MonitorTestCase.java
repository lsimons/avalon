/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 
 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:
 
 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.
 
 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.
 
 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.
 
 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"  
    must not be used to endorse or promote products derived from this  software 
    without  prior written permission. For written permission, please contact 
    apache@apache.org.
 
 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.
 
 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the 
 Apache Software Foundation, please see <http://www.apache.org/>.
 
*/

package org.apache.avalon.excalibur.monitor.test;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
 * @version $Id: MonitorTestCase.java,v 1.18 2003/03/28 14:51:56 bloritsch Exp $
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
            final Mock thirdWheel = new Mock( "test.txt" );
            thirdWheel.touch();
            final MonitorTestCaseListener listener = new MonitorTestCaseListener();
            listener.enableLogging( getLogEnabledLogger() );

            final MockResource resource = new MockResource( thirdWheel );
            resource.addPropertyChangeListener( listener );

            testMonitor.addResource( resource );
            longDelay();

            if( active )
            {
                final Writer externalWriter = new OutputStreamWriter( new MockOutputStream(thirdWheel) );
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
            ( !listener.hasBeenModified() ) )
        {
            delay();
        }
        assertTrue( "File not changed", listener.hasBeenModified() );
        listener.reset();
    }

}
