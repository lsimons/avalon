/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
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
        assertEquals( new Version( 1, 0, 0 ), v1 );

        final Version v2 = Version.getVersion( "0.3" );
        assertEquals( new Version( 0, 3, 0 ), v1 );

        final Version v3 = Version.getVersion( "78.10.03" );
        assertEquals( new Version( 78, 10, 3 ), v1 );
    }

    public void testInvalidVersionString()
    {
        try
        {
            Version.getVersion( "" );
            fail( "Empty string is illegal version string" );
        }
        catch ( final IllegalArgumentException iae )
        {
            //OK
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
        final Version v1 = new Version( 1, 3 , 6 );
        final Version v2 = new Version( 1, 3 , 7 );
        final Version v3 = new Version( 1, 4 , 0 );
        final Version v4 = new Version( 2, 0 , 1 );
        
        assertTrue(   v1.complies( v1 ) );
        assertTrue( ! v1.complies( v2 ) );
        assertTrue(   v2.complies( v1 ) );
        assertTrue( ! v1.complies( v3 ) );
        assertTrue(   v3.complies( v1 ) );
        assertTrue( ! v1.complies( v4 ) );
        assertTrue( ! v4.complies( v1 ) );
    }
}
