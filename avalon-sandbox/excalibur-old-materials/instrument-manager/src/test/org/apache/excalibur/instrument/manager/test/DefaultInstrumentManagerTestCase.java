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
package org.apache.excalibur.instrument.manager.test;

import junit.framework.TestCase;

import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.excalibur.instrument.manager.DefaultInstrumentManager;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentableDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentException;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentSampleException;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentableException;

/**
 * Test of the DefaultInstrumentManager.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.5 $ $Date: 2003/03/22 12:46:44 $
 */
public class DefaultInstrumentManagerTestCase
    extends TestCase
{
    private DefaultInstrumentManager m_instrumentManager;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public DefaultInstrumentManagerTestCase( String name )
    {
        super( name );
    }
    
    /*---------------------------------------------------------------
     * TestCase Methods
     *-------------------------------------------------------------*/
    public void setUp()
        throws Exception
    {
        System.out.println( "setUp()" );
        
        super.setUp();
        
        DefaultConfiguration instrumentConfig = new DefaultConfiguration( "instrument" );
        
        m_instrumentManager = new DefaultInstrumentManager();
        m_instrumentManager.enableLogging( new ConsoleLogger( ConsoleLogger.LEVEL_DEBUG ) );
        m_instrumentManager.configure( instrumentConfig );
        m_instrumentManager.initialize();
    }
    
    public void tearDown()
        throws Exception
    {
        System.out.println( "tearDown()" );
        m_instrumentManager.dispose();
        m_instrumentManager = null;
        
        super.tearDown();
    }
    
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    private void assertInstrumentableExists( String name )
    {
        InstrumentableDescriptor descriptor =
            m_instrumentManager.locateInstrumentableDescriptor( name );
        assertEquals( "Looked up instrumentable name incorrect.", descriptor.getName(), name );
    }
    
    private void assertInstrumentableNotExists( String name )
    {
        try
        {
            InstrumentableDescriptor descriptor =
                m_instrumentManager.locateInstrumentableDescriptor( name );
            fail( "Found an instrumentable named " + name + " when it should not have existed." );
        }
        catch( NoSuchInstrumentableException e )
        {
            // Ok
        }
    }
    
    private void assertInstrumentExists( String name )
    {
        InstrumentDescriptor descriptor =
            m_instrumentManager.locateInstrumentDescriptor( name );
        assertEquals( "Looked up instrument name incorrect.", descriptor.getName(), name );
    }
    
    private void assertInstrumentNotExists( String name )
    {
        try
        {
            InstrumentDescriptor descriptor =
                m_instrumentManager.locateInstrumentDescriptor( name );
            fail( "Found an instrument named " + name + " when it should not have existed." );
        }
        catch( NoSuchInstrumentException e )
        {
            // Ok
        }
    }
    
    private void assertInstrumentSampleExists( String name )
    {
        InstrumentSampleDescriptor descriptor =
            m_instrumentManager.locateInstrumentSampleDescriptor( name );
        assertEquals( "Looked up instrument sample name incorrect.", descriptor.getName(), name );
    }
    
    private void assertInstrumentSampleNotExists( String name )
    {
        try
        {
            InstrumentSampleDescriptor descriptor =
                m_instrumentManager.locateInstrumentSampleDescriptor( name );
            fail( "Found an instrument sample named " + name + " when it should not have existed." );
        }
        catch( NoSuchInstrumentSampleException e )
        {
            // Ok
        }
    }
    
    /*---------------------------------------------------------------
     * Test Cases
     *-------------------------------------------------------------*/
    public void testCreateDestroy() throws Exception
    {
    }

    public void testLookupDefaultInstruments() throws Exception
    {
        // Look for elements which should always exist.
        assertInstrumentableExists( "instrument-manager" );
        assertInstrumentExists( "instrument-manager.total-memory" );
        assertInstrumentExists( "instrument-manager.free-memory" );
        assertInstrumentExists( "instrument-manager.memory" );
        assertInstrumentExists( "instrument-manager.active-thread-count" );
        
        // Look for elements which should not exist.
        assertInstrumentableNotExists( "instrument-manager.total-memory" );
        assertInstrumentableNotExists( "instrument-manager.foobar" );
        assertInstrumentableNotExists( "foobar" );
        assertInstrumentNotExists( "instrument-manager.foobar" );
    }
    
    public void testLookupSamples() throws Exception
    {
        
    }
}

