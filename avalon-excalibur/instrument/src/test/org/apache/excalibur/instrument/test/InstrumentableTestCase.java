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

import org.apache.excalibur.instrument.CounterInstrument;
import org.apache.excalibur.instrument.Instrument;
import org.apache.excalibur.instrument.Instrumentable;
import org.apache.excalibur.instrument.ValueInstrument;

/**
 * Test of the AbstractInstrumentable instrument.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 15:59:13 $
 */
public class InstrumentableTestCase
    extends TestCase
{
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public InstrumentableTestCase( String name )
    {
        super( name );
    }
    
    /*---------------------------------------------------------------
     * TestCase Methods
     *-------------------------------------------------------------*/
    
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    private void generalTest( Instrument[] instruments, Instrumentable[] children )
        throws Exception
    {
        AbstractInstrumentableImpl impl = new AbstractInstrumentableImpl( "base" );
        
        // Set the name
        impl.setInstrumentableName( "test" );
        
        // Add the instruments
        for ( int i = 0; i < instruments.length; i++ )
        {
            impl.addInstrument( instruments[i] );
        }
        
        // Add the child instrumentables
        for ( int i = 0; i < children.length; i++ )
        {
            impl.addChildInstrumentable( children[i] );
        }
        
        // Verify the name
        assertEquals( "Instrumentable name incorrect.", impl.getInstrumentableName(), "test" );
        
        
        // Verify the instruments
        Instrument[] implInstruments = impl.getInstruments();
        assertEquals( "The number of instruments is not correct.",
            implInstruments.length, instruments.length );
        for ( int i = 0; i < instruments.length; i++ )
        {
            assertEquals( "Instrument[i] is not correct.", implInstruments[i], instruments[i] );
        }
        
        // Make sure that instruments can no longer be added
        try
        {
            impl.addInstrument( new CounterInstrument( "bad" ) );
            fail( "Should not have been able to add more instruments" );
        }
        catch ( IllegalStateException e )
        {
            // Ok
        }
        
        
        // Verify the child instrumentables
        Instrumentable[] implChildren = impl.getChildInstrumentables();
        assertEquals( "The number of child instrumentables is not correct.",
            implChildren.length, children.length );
        for ( int i = 0; i < children.length; i++ )
        {
            assertEquals( "Child[i] is not correct.", implChildren[i], children[i] );
        }
        
        // Make sure that child instrumentables can no longer be added
        try
        {
            impl.addChildInstrumentable( new AbstractInstrumentableImpl( "bad" ) );
            fail( "Should not have been able to add more child instrumentables" );
        }
        catch ( IllegalStateException e )
        {
            // Ok
        }
    }
    
    /*---------------------------------------------------------------
     * Test Cases
     *-------------------------------------------------------------*/
    public void testEmpty() throws Exception
    {
        Instrument[] instruments = new Instrument[] {};
        Instrumentable[] children = new Instrumentable[] {};
        
        generalTest( instruments, children );
    }
    
    public void test1Instrument() throws Exception
    {
        Instrument[] instruments = new Instrument[]
            {
                new CounterInstrument( "c1" )
            };
        Instrumentable[] children = new Instrumentable[] {};
        
        generalTest( instruments, children );
    }
    
    public void testNInstrument() throws Exception
    {
        Instrument[] instruments = new Instrument[]
            {
                new CounterInstrument( "c1" ),
                new ValueInstrument( "v1" ),
                new CounterInstrument( "c2" ),
                new ValueInstrument( "v2" ),
                new CounterInstrument( "c3" ),
                new ValueInstrument( "v3" ),
                new CounterInstrument( "c4" ),
                new ValueInstrument( "v4" )
            };
        Instrumentable[] children = new Instrumentable[] {};
        
        generalTest( instruments, children );
    }
    
    public void test1ChildInstrumentable() throws Exception
    {
        Instrument[] instruments = new Instrument[] {};
        Instrumentable[] children = new Instrumentable[]
            {
                new AbstractInstrumentableImpl( "i1" )
            };
        
        generalTest( instruments, children );
    }
    
    public void testNChildInstrumentable() throws Exception
    {
        Instrument[] instruments = new Instrument[] {};
        Instrumentable[] children = new Instrumentable[]
            {
                new AbstractInstrumentableImpl( "i1" ),
                new AbstractInstrumentableImpl( "i2" ),
                new AbstractInstrumentableImpl( "i3" ),
                new AbstractInstrumentableImpl( "i4" ),
                new AbstractInstrumentableImpl( "i5" ),
                new AbstractInstrumentableImpl( "i6" )
            };
        
        generalTest( instruments, children );
    }
}

