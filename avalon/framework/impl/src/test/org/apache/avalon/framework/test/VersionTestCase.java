/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) 2002,2003 The Apache Software Foundation. All rights reserved.
 
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
 
 4. The names "Jakarta", "Apache Avalon", "Avalon Excalibur", "Avalon
    Framework" and "Apache Software Foundation"  must not be used to endorse
    or promote products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.
 
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
 on  behalf of the Apache Software  Foundation and was  originally created by
 Stefano Mazzocchi  <stefano@apache.org>. For more  information on the Apache 
 Software Foundation, please see <http://www.apache.org/>.
 
*/
package org.apache.avalon.framework.test;

import junit.framework.TestCase;
import org.apache.avalon.framework.Version;

/**
 * TestCase for Version.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public class VersionTestCase
    extends TestCase
{
    public VersionTestCase( final String name )
    {
        super( name );
    }

    public void testValidVersionString()
    {
        final Version v1 = Version.getVersion( "1" );
        assertTrue( new Version( 1, 0, 0 ).equals( v1 ) );

        final Version v2 = Version.getVersion( "0.3" );
        assertTrue( new Version( 0, 3, 0 ).equals( v2 ) );

        final Version v3 = Version.getVersion( "78.10.03" );
        assertTrue( new Version( 78, 10, 3 ).equals( v3 ) );
    }

    public void testInvalidVersionString()
    {
        try
        {
            assertEquals( -1, Version.getVersion( "" ).getMajor() );
        }
        catch ( final IllegalArgumentException iae )
        {
            fail( "Empty string is legal version string" );
        }

        try
        {
            Version.getVersion( "1.F" );
            Version.getVersion( "1.0-dev" );
            fail( "Version string do contains only '.' and number" );
        }
        catch ( final NumberFormatException nfe )
        {
            //OK
        }
    }

    public void testComplies()
    {
        final Version v0 = new Version( -1, 0 , 0 );
        final Version v1 = new Version( 1, 3 , 6 );
        final Version v2 = new Version( 1, 3 , 7 );
        final Version v3 = new Version( 1, 4 , 0 );
        final Version v4 = new Version( 2, 0 , 1 );

        assertTrue(   v0.complies( v1 ) );
        assertTrue(   v0.complies( v4 ) );
        
        assertTrue(   v1.complies( v1 ) );
        assertTrue( ! v1.complies( v2 ) );
        assertTrue(   v2.complies( v1 ) );
        assertTrue( ! v1.complies( v3 ) );
        assertTrue(   v3.complies( v1 ) );
        assertTrue( ! v1.complies( v4 ) );
        assertTrue( ! v4.complies( v1 ) );
    }
    
    public void testHashCode()
    {
        final Version v1 = new Version( 5, 1, 0 );
        final Version v2 = new Version( 1, 0, 3 );
        final Version v3 = new Version( 1, 0, 3 );
        
        assertEquals( calculateHash(v1), v1.hashCode() );
        assertEquals( calculateHash(v2), v2.hashCode() );
        
        assertTrue( v1.hashCode() != v2.hashCode() );
        assertTrue( ! v1.equals(v2) );
        
        assertEquals( v2.hashCode(), v3.hashCode() );
        assertEquals( v2, v3 );
    }
    
    public void testComparable()
    {
        final Version v1 = new Version( 1, 0, 0 );
        final Version v2 = new Version( 2, 0, 0 );
        final Version v3 = new Version( 2, 1, 0 );
        final Version v4 = new Version( 2, 1, 1 );
        final Version v5 = new Version( 1, 0, 0 );
        
        assertEquals( 0, v1.compareTo(v5) );
        assertEquals( 0, v5.compareTo(v1) );
        
        assertEquals( -1, v1.compareTo(v2) );
        assertEquals( 1, v2.compareTo(v1) );
        
        assertEquals( -1, v2.compareTo(v3) );
        assertEquals( 1, v3.compareTo(v2) );
        
        assertEquals( -1, v3.compareTo(v4) );
        assertEquals( 1, v4.compareTo(v3) );
    }

    private int calculateHash(final Version v) {
        int hash = v.getMajor();
        hash >>>= 17;
        hash += v.getMinor();
        hash >>>= 17;
        hash += v.getMicro();
        return hash;
    }
}
