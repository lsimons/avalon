/*
 * Copyright (c) The Apache Software Foundation.  All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.avalon.excalibur.monitor.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import junit.framework.TestCase;
import org.apache.avalon.excalibur.monitor.DirectoryResource;
import org.apache.avalon.framework.logger.ConsoleLogger;

/**
 * Junit TestCase for the directory resource.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Id: DirectoryTestCase.java,v 1.1 2002/09/08 02:30:48 donaldp Exp $
 */
public class DirectoryTestCase
    extends TestCase
{
    public DirectoryTestCase( String name )
    {
        super( name );
    }

    public void testDirectoryEvents()
        throws Exception
    {
        final File dir = createDir();

        try
        {
            final Set added1 = new HashSet();
            added1.add( "file1.txt" );
            added1.add( "file2.txt" );
            added1.add( "file3.txt" );
            testChanges( added1,
                         Collections.EMPTY_SET,
                         Collections.EMPTY_SET,
                         dir );

            final Set mods2 = new HashSet();
            mods2.add( "file2.txt" );
            final Set added2 = new HashSet();
            added2.add( "file4.txt" );
            testChanges( added2,
                         Collections.EMPTY_SET,
                         mods2,
                         dir );

            final Set dels = new HashSet();
            dels.add( "file2.txt" );
            testChanges( Collections.EMPTY_SET,
                         dels,
                         Collections.EMPTY_SET,
                         dir );
        }
        finally
        {
            final File[] files = dir.listFiles();
            for( int i = 0; i < files.length; i++ )
            {
                files[ i ].delete();
            }
        }
    }

    private File createDir()
    {
        final File dir = new File( "testDir" );
        dir.mkdir();
        dir.setLastModified( System.currentTimeMillis() );
        return dir;
    }

    private void testChanges( final Set added,
                              final Set removed,
                              final Set modified,
                              final File dir )
        throws Exception
    {
        final DirectoryResource resource =
            new DirectoryResource( dir.getCanonicalPath() );

        final DirectoryTestCaseListener listener = new DirectoryTestCaseListener();
        listener.enableLogging( new ConsoleLogger() );
        resource.addPropertyChangeListener( listener );

        final Iterator adds = added.iterator();
        while( adds.hasNext() )
        {
            final String add = (String)adds.next();
            touchFile( dir, add );
        }

        final Iterator mods = modified.iterator();
        while( mods.hasNext() )
        {
            final String mod = (String)mods.next();
            touchFile( dir, mod );
        }

        final Iterator rems = removed.iterator();
        while( rems.hasNext() )
        {
            final String rem = (String)rems.next();
            deleteFile( dir, rem );
        }

        longDelay();

        resource.testModifiedAfter( System.currentTimeMillis() );
        testExpected( "Add", added, listener.getAdded() );
        testExpected( "Remove", removed, listener.getRemoved() );
        testExpected( "Modify", modified, listener.getModified() );

        listener.reset();
    }

    private void testExpected( final String name,
                               final Set expected,
                               final Set actual )
    {
        assertEquals( name + " results count(" +
                      expected + " vs (actual) " +
                      actual,
                      expected.size(),
                      actual.size() );
        final Iterator iterator = actual.iterator();
        while( iterator.hasNext() )
        {
            final File file = (File)iterator.next();
            if( !expected.contains( file.getName() ) )
            {
                fail( "Missing " + file.getName() +
                      " from expected set " + expected );
            }
        }
    }

    private void touchFile( final File dir,
                            final String filename )
        throws IOException
    {
        final File file = new File( dir, filename );
        file.createNewFile();
        final FileWriter writer = new FileWriter( file );
        writer.write( "Meep!" );
        writer.flush();
        writer.close();
        file.setLastModified( System.currentTimeMillis() );
    }

    private void deleteFile( final File dir,
                             final String filename )
    {
        final File file = new File( dir, filename );
        if( !file.delete() )
        {
            fail( "Failed to delete file " + file );
        }
    }

    /**
     * Some filesystems are not sensitive enough so you need
     * to delay for a long enough period of time (ie 1 second).
     */
    private void longDelay()
    {
        delay( 1000 );
    }

    private void delay( final int time )
    {
        try
        {
            Thread.sleep( time ); // sleep 10 millis at a time
        }
        catch( final InterruptedException ie )
        {
            // ignore and keep waiting
        }
    }
}
