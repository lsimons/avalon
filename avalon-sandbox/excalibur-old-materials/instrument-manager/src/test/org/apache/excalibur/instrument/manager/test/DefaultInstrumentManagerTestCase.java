/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.manager.test;

import junit.framework.TestCase;

import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.logger.ConsoleLogger;

import org.apache.excalibur.instrument.manager.DefaultInstrumentManager;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentableDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentableException;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentException;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentSampleException;

/**
 * Test of the DefaultInstrumentManager.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/11/08 07:59:13 $
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

