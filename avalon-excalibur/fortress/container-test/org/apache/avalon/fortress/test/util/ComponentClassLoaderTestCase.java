/*
* Copyright (C) The Apache Software Foundation. All rights reserved.
*
* This software is published under the terms of the Apache Software License
* version 1.1, a copy of which has been included with this distribution in
* the LICENSE.txt file.
*/

package org.apache.avalon.fortress.test.util;

import junit.framework.TestCase;

/**
* A testcase for the @link{ComponentClassLoader}.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public class ComponentClassLoaderTestCase extends TestCase
{
    private java.util.Set m_blocks;
    private java.util.Set m_services;
    private java.util.Set m_types;

    public ComponentClassLoaderTestCase( String name )
    {
        super( name );
        m_blocks = new java.util.HashSet();
        m_blocks.add("org.apache.avalon.test.EphemeralComponent");
        m_blocks.add("org.apache.avalon.test.AnotherTest");

        m_types = new java.util.HashSet();
        m_types.add("org.apache.avalon.test.YetAnotherTest");
        m_types.add("org.apache.avalon.test.HooHa");

        m_services = new java.util.HashSet();
        m_services.add("org.apache.avalon.test.EphemeralComponent");
        m_services.add("org.apache.avalon.test.Nothing");
    }

    public void testEmptyManifest()
    {
        java.net.URL jar = getClass().getResource( "empty.jar" );
        assertTrue( jar != null );

        org.apache.excalibur.fortress.util.classloader.ComponentClassLoader loader = new org.apache.excalibur.fortress.util.classloader.ComponentClassLoader( new java.net.URL[] { jar } );

        assertEquals( loader.getServiceEntries().length, 0 );
        assertEquals( loader.getBlockEntries().length, 0 );
        assertEquals( loader.getTypeEntries().length, 0 );
    }

    public void testEmptyManifestJarScanner()
    {
        java.net.URL jar = this.getClass().getResource( "empty.jar" );
        assertTrue( jar != null );

        org.apache.excalibur.fortress.util.classloader.JarEntries entries = org.apache.excalibur.fortress.util.classloader.JarScanner.scan( jar );

        assertEquals( entries.getServiceEntries().size(), 0 );
        assertEquals( entries.getBlockEntries().size(), 0 );
        assertEquals( entries.getTypeEntries().size(), 0 );
    }

    public void testBlocksManifest()
    {
        java.net.URL jar = this.getClass().getResource( "full.jar" );
        assertTrue( jar != null );
        org.apache.excalibur.fortress.util.classloader.ComponentClassLoader loader = new org.apache.excalibur.fortress.util.classloader.ComponentClassLoader( new java.net.URL[] { jar } );

        String[] blocks = loader.getBlockEntries();
        assertEquals( m_blocks.size(), blocks.length );

        for ( int i = 0; i < blocks.length; i++ )
        {
            assertTrue( m_blocks.contains( blocks[i] ) );
        }
    }

    public void testBlocksManifestJarScanner()
    {
        java.net.URL jar = this.getClass().getResource( "full.jar" );
        assertTrue( jar != null );
        org.apache.excalibur.fortress.util.classloader.JarEntries entries = org.apache.excalibur.fortress.util.classloader.JarScanner.scan( jar );

        java.util.Set blocks = entries.getBlockEntries();
        assertEquals( m_blocks.size(), blocks.size() );

        java.util.Iterator it = blocks.iterator();
        while( it.hasNext() )
        {
            assertTrue( m_blocks.contains( it.next() ) );
        }
    }

    public void testServicesManifest()
    {
        java.net.URL jar = this.getClass().getResource( "full.jar" );
        assertTrue( jar != null );
        org.apache.excalibur.fortress.util.classloader.ComponentClassLoader loader = new org.apache.excalibur.fortress.util.classloader.ComponentClassLoader( new java.net.URL[] { jar } );

        String[] services = loader.getServiceEntries();
        assertEquals( m_services.size(), services.length );

        for ( int i = 0; i < services.length; i++ )
        {
            assertTrue( m_services.contains( services[i] ) );
        }
    }

    public void testServicesManifestJarScanner()
    {
        java.net.URL jar = this.getClass().getResource( "full.jar" );
        assertTrue( jar != null );
        org.apache.excalibur.fortress.util.classloader.JarEntries entries = org.apache.excalibur.fortress.util.classloader.JarScanner.scan( jar );

        java.util.Set services = entries.getServiceEntries();
        assertEquals( m_services.size(), services.size() );

        java.util.Iterator it = services.iterator();
        while( it.hasNext() )
        {
            assertTrue( m_services.contains( it.next() ) );
        }
    }

    public void testTypesManifest()
    {
        java.net.URL jar = this.getClass().getResource( "full.jar" );
        assertTrue( jar != null );
        org.apache.excalibur.fortress.util.classloader.ComponentClassLoader loader = new org.apache.excalibur.fortress.util.classloader.ComponentClassLoader( new java.net.URL[] { jar } );

        String[] types = loader.getTypeEntries();
        assertEquals( m_types.size(), types.length );

        for ( int i = 0; i < types.length; i++ )
        {
            assertTrue( m_types.contains( types[i] ) );
        }
    }

    public void testTypesManifestJarScanner()
    {
        java.net.URL jar = this.getClass().getResource( "full.jar" );
        assertTrue( jar != null );
        org.apache.excalibur.fortress.util.classloader.JarEntries entries = org.apache.excalibur.fortress.util.classloader.JarScanner.scan( jar );

        java.util.Set types = entries.getTypeEntries();
        assertEquals( m_types.size(), types.size() );

        java.util.Iterator it = types.iterator();
        while( it.hasNext() )
        {
            assertTrue( m_types.contains( it.next() ) );
        }
    }
}
