/* ====================================================================
* The Apache Software License, Version 1.1
*
* Copyright (c) 1997-2003 The Apache Software Foundation. All rights
* reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions
* are met:
*
* 1. Redistributions of source code must retain the above copyright
*    notice, this list of conditions and the following disclaimer.
*
* 2. Redistributions in binary form must reproduce the above copyright
*    notice, this list of conditions and the following disclaimer in
*    the documentation and/or other materials provided with the
*    distribution.
*
* 3. The end-user documentation included with the redistribution,
*    if any, must include the following acknowledgment:
*    "This product includes software developed by the
*    Apache Software Foundation (http://www.apache.org/)."
*    Alternately, this acknowledgment may appear in the software
*    itself, if and wherever such third-party acknowledgments
*    normally appear.
*
* 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
*    must not be used to endorse or promote products derived from this
*    software without prior written permission. For written
*    permission, please contact apache@apache.org.
*
* 5. Products derived from this software may not be called "Apache",
*    nor may "Apache" appear in their name, without prior written
*    permission of the Apache Software Foundation.
*
* THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
* WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
* OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
* ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
* SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
* LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
* USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
* OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
* SUCH DAMAGE.
* ====================================================================
*
* This software consists of voluntary contributions made by many
* individuals on behalf of the Apache Software Foundation. For more
* information on the Apache Software Foundation, please see
* <http://www.apache.org/>.
*/
package org.apache.avalon.framework.test;

import org.apache.avalon.framework.ValuedEnum;

import junit.framework.TestCase;

import java.util.Map;
import java.util.HashMap;

/**
 * 
 *
 * @author <a href="lsimons at jicarilla dot org">Leo Simons</a>
 * @version $Id: ValuedEnumTestCase.java,v 1.1 2004/01/11 22:44:45 leosimons Exp $
 */
public class ValuedEnumTestCase extends TestCase
{
    private final static class Color extends ValuedEnum
    {
        public static final Color RED = new Color( "Red", 0 );
        public static final Color RED_NEGATIVE = new Color( "Red", -1 );
        public static final Color GREEN = new Color( "Green", 1 );
        public static final Color BLUE = new Color( "Blue", 2 );

        public Color( final String color, final int value )
        {
            super( color, value );
        }

        public Color( final String color, final int value, Map stuff )
        {
            super( color, value, stuff );
        }
    }

    private final static class OtherColor extends ValuedEnum
    {
        public static final OtherColor RED = new OtherColor( "Red", 0 );
        public static final OtherColor RED_NEGATIVE = new OtherColor( "Red", -1 );
        public static final OtherColor GREEN = new OtherColor( "Green", 1 );
        public static final OtherColor BLUE = new OtherColor( "Blue", 2 );

        public OtherColor( final String color, final int value )
        {
            super( color, value );
        }

        public OtherColor( final String color, final int value, Map stuff )
        {
            super( color, value, stuff );
        }
    }

    public ValuedEnumTestCase( final String name )
    {
        super( name );
    }

    public void testConstructor()
    {
        assertNotNull( new Color( "blah", 0, null ) );

        Map entries = new HashMap();

        Color c = new Color( "blah", 0, entries );

        assertTrue( entries.containsKey("blah") );
        assertTrue( entries.containsValue(c) );

        OtherColor c2 = new OtherColor( "blah", 0, entries );
        assertTrue( entries.containsKey("blah") );
        assertFalse( entries.containsValue(c) );
        assertTrue( entries.containsValue(c2) );
    }

    public void testEquals()
    {
        assertTrue( Color.RED.equals( Color.RED ) );
        assertTrue( Color.GREEN.equals( Color.GREEN ) );
        assertTrue( Color.BLUE.equals( Color.BLUE ) );

        assertTrue( !OtherColor.RED.equals( Color.RED ) );
        assertTrue( !OtherColor.GREEN.equals( Color.GREEN ) );
        assertTrue( !OtherColor.BLUE.equals( Color.BLUE ) );

        assertTrue( !Color.RED.equals( OtherColor.RED ) );
        assertTrue( !Color.GREEN.equals( OtherColor.GREEN ) );
        assertTrue( !Color.BLUE.equals( OtherColor.BLUE ) );

        assertTrue( !Color.RED.equals( Color.GREEN ) );
        assertTrue( !Color.GREEN.equals( Color.BLUE ) );
        assertTrue( !Color.BLUE.equals( Color.RED ) );

        assertTrue( !Color.BLUE.equals( null ) );

        assertTrue( new Color(null,0).equals( new Color( null,0 ) ) );
        assertFalse( new Color(null,0).equals( new Color( "hi",0 ) ) );
        assertFalse( new Color("hi",0).equals( new Color( null,0 ) ) );

        // todo: is this _really_ desired?
        assertTrue( Color.RED.equals( Color.RED_NEGATIVE ) );
        assertTrue( Color.RED_NEGATIVE.equals( Color.RED ) );
        assertTrue( OtherColor.RED.equals( OtherColor.RED_NEGATIVE ) );
        assertTrue( OtherColor.RED_NEGATIVE.equals( OtherColor.RED ) );
    }

    public void testHashCode()
    {
        assertTrue( Color.RED.hashCode() ==  Color.RED.hashCode() );
        assertTrue( Color.GREEN.hashCode() ==  Color.GREEN.hashCode() );
        assertTrue( Color.BLUE.hashCode() ==  Color.BLUE.hashCode() );

        assertTrue( OtherColor.RED.hashCode() !=  Color.RED.hashCode() );
        assertTrue( OtherColor.GREEN.hashCode() !=  Color.GREEN.hashCode() );
        assertTrue( OtherColor.BLUE.hashCode() !=  Color.BLUE.hashCode() );

        assertTrue( Color.RED.hashCode() !=  OtherColor.RED.hashCode() );
        assertTrue( Color.GREEN.hashCode() !=  OtherColor.GREEN.hashCode() );
        assertTrue( Color.BLUE.hashCode() !=  OtherColor.BLUE.hashCode() );

        assertTrue( Color.RED.hashCode() !=  Color.GREEN.hashCode() );
        assertTrue( Color.GREEN.hashCode() !=  Color.BLUE.hashCode() );
        assertTrue( Color.BLUE.hashCode() !=  Color.RED.hashCode() );

        // todo: is this _really_ desired?
        assertTrue( Color.RED.hashCode() ==Color.RED_NEGATIVE.hashCode() );
        assertTrue( Color.RED_NEGATIVE.hashCode() ==Color.RED.hashCode() );
        assertTrue( OtherColor.RED.hashCode() ==OtherColor.RED_NEGATIVE.hashCode() );
        assertTrue( OtherColor.RED_NEGATIVE.hashCode() ==OtherColor.RED.hashCode() );
    }

    public void testGet()
    {
        assertEquals( "Red", Color.RED.getName() );
        assertNull( (new Color(null,0)).getName() );
    }

    public void testToString()
    {
        assertTrue( Color.RED.toString().indexOf( "Red") != -1 );
        assertTrue( Color.RED.toString().indexOf( Color.class.getName() ) != -1 );

        Color c = new Color(null,0);
        assertTrue( c.toString().indexOf( "null") != -1 );

    }
}
