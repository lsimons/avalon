/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.test;

import junit.framework.TestCase;

import org.apache.excalibur.instrument.CounterInstrument;

/**
 * Test of the CounterInstrument instrument.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/09/26 06:34:53 $
 */
public class CounterInstrumentTestCase
    extends TestCase
{
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public CounterInstrumentTestCase( String name )
    {
        super( name );
    }
    
    /*---------------------------------------------------------------
     * TestCase Methods
     *-------------------------------------------------------------*/
    
    /*---------------------------------------------------------------
     * Test Cases
     *-------------------------------------------------------------*/
    public void testSimpleIncrementDisconnected() throws Exception
    {
        CounterInstrument ci = new CounterInstrument( "testInstrument" );
        assertEquals( "A disconnected instrument should not be active.", ci.isActive(), false );
        
        ci.increment( 1 );
    }
    
    public void testCount1IncrementDisconnected() throws Exception
    {
        CounterInstrument ci = new CounterInstrument( "testInstrument" );
        assertEquals( "A disconnected instrument should not be active.", ci.isActive(), false );
        
        ci.increment( 1 );
    }
    
    public void testCount0IncrementDisconnected() throws Exception
    {
        CounterInstrument ci = new CounterInstrument( "testInstrument" );
        try
        {
            ci.increment( 0 );
            fail( "calling increment with a count of 0 should fail." );
        }
        catch ( IllegalArgumentException e )
        {
            // Ok
        }
    }
    
    public void testCountNegIncrementDisconnected() throws Exception
    {
        CounterInstrument ci = new CounterInstrument( "testInstrument" );
        try
        {
            ci.increment( -1 );
            fail( "calling increment with a negative count should fail." );
        }
        catch ( IllegalArgumentException e )
        {
            // Ok
        }
    }
    
    public void testSimpleIncrementConnectedInactive() throws Exception
    {
        CounterInstrument ci = new CounterInstrument( "testInstrument" );
        TestInstrumentProxy proxy = new TestInstrumentProxy();
        ci.setInstrumentProxy( proxy );
        
        assertEquals( "The instrument should not be active.", ci.isActive(), false );
        
        ci.increment();
        
        assertEquals( "The expected count was incorrect.", proxy.getValue(), 1 );
        
        ci.increment();
        assertEquals( "The expected count was incorrect.", proxy.getValue(), 2 );
    }
    
    public void testCount1IncrementConnectedInactive() throws Exception
    {
        CounterInstrument ci = new CounterInstrument( "testInstrument" );
        TestInstrumentProxy proxy = new TestInstrumentProxy();
        ci.setInstrumentProxy( proxy );
        
        assertEquals( "The instrument should not be active.", ci.isActive(), false );
        
        ci.increment( 1 );
        
        assertEquals( "The expected count was incorrect.", proxy.getValue(), 1 );
        
        ci.increment( 2 );
        assertEquals( "The expected count was incorrect.", proxy.getValue(), 3 );
    }
    
    public void testSimpleIncrementConnectedActive() throws Exception
    {
        CounterInstrument ci = new CounterInstrument( "testInstrument" );
        TestInstrumentProxy proxy = new TestInstrumentProxy();
        ci.setInstrumentProxy( proxy );
        proxy.activate();
        
        assertEquals( "The instrument should br active.", ci.isActive(), true );
        
        ci.increment();
        
        assertEquals( "The expected count was incorrect.", proxy.getValue(), 1 );
    }
    
    public void testCount1IncrementConnectedActive() throws Exception
    {
        CounterInstrument ci = new CounterInstrument( "testInstrument" );
        TestInstrumentProxy proxy = new TestInstrumentProxy();
        ci.setInstrumentProxy( proxy );
        proxy.activate();

        assertEquals( "The instrument should br active.", ci.isActive(), true );
        
        ci.increment( 1 );
        
        assertEquals( "The expected count was incorrect.", proxy.getValue(), 1 );
    }
}

