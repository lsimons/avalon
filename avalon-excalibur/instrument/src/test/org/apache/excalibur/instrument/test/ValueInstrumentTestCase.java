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
package org.apache.excalibur.instrument.test;

import junit.framework.TestCase;

import org.apache.excalibur.instrument.ValueInstrument;

/**
 * Test of the ValueInstrument instrument.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 15:59:13 $
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

