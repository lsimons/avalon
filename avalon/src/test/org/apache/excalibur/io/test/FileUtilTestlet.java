/* 
 * Copyright  The Apache Software Foundation. All rights reserved. 
 * 
 * This software is published under the terms of the Apache Software License 
 * version 1.1, a copy of which has been included with this distribution in 
 * the LICENSE file. 
 */ 
package org.apache.excalibur.io.test;

import java.io.*;
import org.apache.excalibur.io.FileUtil;
import org.apache.testlet.AbstractTestlet;
import org.apache.testlet.TestFailedException;  

/** 
 * This is used to test FileUtil for correctness. 
 * 
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a> 
 */ 
public final class FileUtilTestlet 
    extends AbstractTestlet 
{  
    protected final int      FILE1_SIZE  = 1;
    protected final int      FILE2_SIZE  = 1024 * 4 + 1;

    protected final File     m_testDirectory;
    protected final File     m_testFile1;
    protected final File     m_testFile2;

    public FileUtilTestlet()
        throws IOException
    {
        m_testDirectory = (new File( "test/io/" )).getAbsoluteFile();
        if( !m_testDirectory.exists() )
        {
            m_testDirectory.mkdirs();
        }

        m_testFile1 = new File( m_testDirectory, "file1-test.txt" );
        m_testFile2 = new File( m_testDirectory, "file2-test.txt" );
        
        createFile( m_testFile1, FILE1_SIZE );
        createFile( m_testFile2, FILE2_SIZE );
    }

    protected void createFile( final File file, final long size )
        throws IOException
    {
        final BufferedOutputStream output = 
            new BufferedOutputStream( new FileOutputStream( file ) );
        
        for( int i = 0; i < size; i++ )
        {
            output.write( (byte)'X' );
        }

        output.close();
    }

    public void testCopyFile1() 
        throws Exception 
    {
        final File destination = new File( m_testDirectory, "copy1.txt" );
        FileUtil.copyFile( m_testFile1, destination );
        assert( "Check Exist", destination.exists() );
        assert( "Check Full copy", destination.length() == FILE1_SIZE );
    }
    
    public void testCopyFile2() 
        throws Exception 
    {
        final File destination = new File( m_testDirectory, "copy2.txt" );
        FileUtil.copyFile( m_testFile2, destination );
        assert( "Check Exist", destination.exists() );
        assert( "Check Full copy", destination.length() == FILE2_SIZE );
    }
    
    public void testForceDeleteFile1() 
        throws Exception 
    {
        final File destination = new File( m_testDirectory, "copy1.txt" );
        FileUtil.forceDelete( destination );
        assert( "Check No Exist", !destination.exists() );
    }
    
    public void testForceDeleteFile2() 
        throws Exception 
    {
        final File destination = new File( m_testDirectory, "copy2.txt" );
        FileUtil.forceDelete( destination );
        assert( "Check No Exist", !destination.exists() );
    }
    
    public void testCopyFile1ToDir() 
        throws Exception 
    {
        final File directory = new File( m_testDirectory, "subdir" );
        if( !directory.exists() ) directory.mkdirs();
        final File destination = new File( directory, "file1-test.txt" );
        FileUtil.copyFileToDirectory( m_testFile1, directory );
        assert( "Check Exist", destination.exists() );
        assert( "Check Full copy", destination.length() == FILE1_SIZE );
    }
    
    public void testCopyFile2ToDir() 
        throws Exception 
    {
        final File directory = new File( m_testDirectory, "subdir" );
        if( !directory.exists() ) directory.mkdirs();
        final File destination = new File( directory, "file2-test.txt" );
        FileUtil.copyFileToDirectory( m_testFile2, directory );
        assert( "Check Exist", destination.exists() );
        assert( "Check Full copy", destination.length() == FILE2_SIZE );
    }  

    public void testForceDeleteDir() 
        throws Exception 
    {
        FileUtil.forceDelete( m_testDirectory.getParentFile() );
        assert( "Check No Exist", !m_testDirectory.getParentFile().exists() );
    }

    public void testResolveFileDotDot()
        throws Exception
    {
        final File file = FileUtil.resolveFile( m_testDirectory, ".." );
        assertEquality( "Check .. operator", file, m_testDirectory.getParentFile() );
    }
    
    public void testResolveFileDot()
        throws Exception
    {
        final File file = FileUtil.resolveFile( m_testDirectory, "." );
        assertEquality( "Check . operator", file, m_testDirectory );
    }
}
