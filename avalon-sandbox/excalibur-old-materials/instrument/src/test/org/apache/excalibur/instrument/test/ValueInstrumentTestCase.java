/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.test;

import junit.framework.TestCase;

import org.apache.excalibur.instrument.ValueInstrument;

/**
 * Test of the ValueInstrument instrument.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/09/26 06:34:53 $
 */
public class ValueInstrumentTestCase
    extends TestCase
{
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public ValueInstrumentTestCase( String name )
    {
        super( name );
    }
    
    /*---------------------------------------------------------------
     * TestCase Methods
     *-------------------------------------------------------------*/
    
    /*---------------------------------------------------------------
     * Test Cases
     *-------------------------------------------------------------*/
    public void testSimpleValueDisconnected() throws Exception
    {
        ValueInstrument vi = new ValueInstrument( "testInstrument" );
        
        assertEquals( "A disconnected instrument should not be active.", vi.isActive(), false );
        
        vi.setValue( 0 );
        vi.setValue( -1 );
        vi.setValue( 1 );
    }
    
    public void testSimpleValueConnectedInactive() throws Exception
    {
        ValueInstrument vi = new ValueInstrument( "testInstrument" );
        TestInstrumentProxy proxy = new TestInstrumentProxy();
        vi.setInstrumentProxy( proxy );
        
        assertEquals( "The instrument should not be active.", vi.isActive(), false );
        
        vi.setValue( 0 );
        assertEquals( "The expected value was incorrect.", proxy.getValue(), 0 );
        
        vi.setValue( -1 );
        assertEquals( "The expected value was incorrect.", proxy.getValue(), -1 );
        
        vi.setValue( 1 );
        assertEquals( "The expected value was incorrect.", proxy.getValue(), 1 );
    }
    
    public void testLargeValueConnectedInactive() throws Exception
    {
        ValueInstrument vi = new ValueInstrument( "testInstrument" );
        TestInstrumentProxy proxy = new TestInstrumentProxy();
        vi.setInstrumentProxy( proxy );
        
        assertEquals( "The instrument should not be active.", vi.isActive(), false );
        
        vi.setValue( 1313123123 );
        assertEquals( "The expected value was incorrect.", proxy.getValue(), 1313123123 );
        
        vi.setValue( -325353253 );
        assertEquals( "The expected value was incorrect.", proxy.getValue(), -325353253 );
    }
    
    public void testSimpleValueConnectedActive() throws Exception
    {
        ValueInstrument vi = new ValueInstrument( "testInstrument" );
        TestInstrumentProxy proxy = new TestInstrumentProxy();
        vi.setInstrumentProxy( proxy );
        proxy.activate();
        
        assertEquals( "The instrument should br active.", vi.isActive(), true );
        
        vi.setValue( 0 );
        assertEquals( "The expected value was incorrect.", proxy.getValue(), 0 );
        
        vi.setValue( -1 );
        assertEquals( "The expected value was incorrect.", proxy.getValue(), -1 );
        
        vi.setValue( 1 );
        assertEquals( "The expected value was incorrect.", proxy.getValue(), 1 );
    }
    
    public void testLargeValueConnectedActive() throws Exception
    {
        ValueInstrument vi = new ValueInstrument( "testInstrument" );
        TestInstrumentProxy proxy = new TestInstrumentProxy();
        vi.setInstrumentProxy( proxy );
        proxy.activate();
        
        assertEquals( "The instrument should br active.", vi.isActive(), true );
        
        vi.setValue( 1313123123 );
        assertEquals( "The expected value was incorrect.", proxy.getValue(), 1313123123 );
        
        vi.setValue( -325353253 );
        assertEquals( "The expected value was incorrect.", proxy.getValue(), -325353253 );
    }
}

