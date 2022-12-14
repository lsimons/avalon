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
 * @author Peter Donald
 * @version $Id: DirectoryTestCase.java,v 1.8 2003/12/05 15:15:15 leosimons Exp $
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
            deleteDir( dir );
        }
    }

    public void testDirectoryDelete()
        throws Exception
    {
        final File dir = createDir();
        final DirectoryResource resource =
            new DirectoryResource( dir.getCanonicalPath() );
        deleteDir( dir );
        try
        {
            resource.testModifiedAfter( System.currentTimeMillis() );
        }
        catch( final Exception e )
        {
            fail( "Received exception when dir deleted: " + e );
        }
    }

    private void deleteDir( final File dir )
    {
        final File[] files = dir.listFiles();
        for( int i = 0; i < files.length; i++ )
        {
            files[ i ].delete();
        }
        dir.delete();
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
        final int changeCount = listener.getChangeCount();
        resource.testModifiedAfter( System.currentTimeMillis() + 1 );
        testExpected( "Add", added, listener.getAdded() );
        testExpected( "Remove", removed, listener.getRemoved() );
        testExpected( "Modify", modified, listener.getModified() );

        assertEquals( "Changes detected. (Should be " + changeCount +
                      " as no changes occured between two tests)",
                      changeCount,
                      listener.getChangeCount() );
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
