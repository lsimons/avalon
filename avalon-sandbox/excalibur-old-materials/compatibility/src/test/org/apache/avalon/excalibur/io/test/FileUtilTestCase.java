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
public final class FileUtilTestCase
    extends TestCase
{
    private final int FILE1_SIZE = 1;
    private final int FILE2_SIZE = 1024 * 4 + 1;

    private final File m_testDirectory;
    private final File m_testFile1;
    private final File m_testFile2;

    public FileUtilTestCase( final String name )
        throws IOException
    {
        super( name );

        m_testDirectory = ( new File( "test/io/" ) ).getAbsoluteFile();
        if( !m_testDirectory.exists() )
        {
            m_testDirectory.mkdirs();
        }

        m_testFile1 = new File( m_testDirectory, "file1-test.txt" );
        m_testFile2 = new File( m_testDirectory, "file2-test.txt" );

        createFile( m_testFile1, FILE1_SIZE );
        createFile( m_testFile2, FILE2_SIZE );
    }

    private void createFile( final File file, final long size )
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

    public static Test suite()
        throws IOException
    {
        final TestSuite suite = new TestSuite();
        suite.addTest( new FileUtilTestCase( "testCopyFile1" ) );
        suite.addTest( new FileUtilTestCase( "testCopyFile2" ) );
        suite.addTest( new FileUtilTestCase( "testForceDeleteAFile1" ) );
        suite.addTest( new FileUtilTestCase( "testForceDeleteAFile2" ) );
        suite.addTest( new FileUtilTestCase( "testCopyFile1ToDir" ) );
        suite.addTest( new FileUtilTestCase( "testCopyFile2ToDir" ) );
        suite.addTest( new FileUtilTestCase( "testForceDeleteDir" ) );
        suite.addTest( new FileUtilTestCase( "testResolveFileDotDot" ) );
        suite.addTest( new FileUtilTestCase( "testResolveFileDot" ) );
        suite.addTest( new FileUtilTestCase( "testNormalize" ) );
        suite.addTest( new FileUtilTestCase( "testCatPath" ) );
        return suite;
    }

    public void testCopyFile1()
        throws Exception
    {
        final File destination = new File( m_testDirectory, "copy1.txt" );
        FileUtil.copyFile( m_testFile1, destination );
        assertTrue( "Check Exist", destination.exists() );
        assertTrue( "Check Full copy", destination.length() == FILE1_SIZE );
    }

    public void testCopyFile2()
        throws Exception
    {
        final File destination = new File( m_testDirectory, "copy2.txt" );
        FileUtil.copyFile( m_testFile2, destination );
        assertTrue( "Check Exist", destination.exists() );
        assertTrue( "Check Full copy", destination.length() == FILE2_SIZE );
    }

    public void testForceDeleteAFile1()
        throws Exception
    {
        final File destination = new File( m_testDirectory, "copy1.txt" );
        destination.createNewFile();
        assertTrue( "Copy1.txt doesn't exist to delete", destination.exists() );
        FileUtil.forceDelete( destination );
        assertTrue( "Check No Exist", !destination.exists() );
    }

    public void testForceDeleteAFile2()
        throws Exception
    {
        final File destination = new File( m_testDirectory, "copy2.txt" );
        destination.createNewFile();
        assertTrue( "Copy2.txt doesn't exist to delete", destination.exists() );
        FileUtil.forceDelete( destination );
        assertTrue( "Check No Exist", !destination.exists() );
    }

    public void testCopyFile1ToDir()
        throws Exception
    {
        final File directory = new File( m_testDirectory, "subdir" );
        if( !directory.exists() ) directory.mkdirs();
        final File destination = new File( directory, "file1-test.txt" );
        FileUtil.copyFileToDirectory( m_testFile1, directory );
        assertTrue( "Check Exist", destination.exists() );
        assertTrue( "Check Full copy", destination.length() == FILE1_SIZE );
    }

    public void testCopyFile2ToDir()
        throws Exception
    {
        final File directory = new File( m_testDirectory, "subdir" );
        if( !directory.exists() ) directory.mkdirs();
        final File destination = new File( directory, "file2-test.txt" );
        FileUtil.copyFileToDirectory( m_testFile2, directory );
        assertTrue( "Check Exist", destination.exists() );
        assertTrue( "Check Full copy", destination.length() == FILE2_SIZE );
    }

    public void testForceDeleteDir()
        throws Exception
    {
        FileUtil.forceDelete( m_testDirectory.getParentFile() );
        assertTrue( "Check No Exist", !m_testDirectory.getParentFile().exists() );
    }

    public void testResolveFileDotDot()
        throws Exception
    {
        final File file = FileUtil.resolveFile( m_testDirectory, ".." );
        assertEquals( "Check .. operator", file, m_testDirectory.getParentFile() );
    }

    public void testResolveFileDot()
        throws Exception
    {
        final File file = FileUtil.resolveFile( m_testDirectory, "." );
        assertEquals( "Check . operator", file, m_testDirectory );
    }

    public void testNormalize()
        throws Exception
    {
        final String[] src =
            {
                "", "/", "///", "/foo", "/foo//", "/./", "/foo/./", "/foo/./bar",
                "/foo/../bar", "/foo/../bar/../baz", "/foo/bar/../../baz", "/././",
                "/foo/./../bar", "/foo/.././bar/", "//foo//./bar", "/../",
                "/foo/../../"
            };

        final String[] dest =
            {
                "", "/", "/", "/foo", "/foo/", "/", "/foo/", "/foo/bar", "/bar",
                "/baz", "/baz", "/", "/bar", "/bar/", "/foo/bar", null, null
            };

        assertEquals( "Oops, test writer goofed", src.length, dest.length );

        for( int i = 0; i < src.length; i++ )
        {
            assertEquals( "Check if '" + src[ i ] + "' normalized to '" + dest[ i ] + "'",
                          dest[ i ], FileUtil.normalize( src[ i ] ) );
        }
    }

    public void testCatPath()
    {
        assertEquals( "/a/b/d", FileUtil.catPath( "/a/b/c", "d" ) );
        assertEquals( "/a/d", FileUtil.catPath( "/a/b/c", "../d" ) );

        assertEquals( "a/b/d", FileUtil.catPath( "a/b/c", "d" ) );
        assertEquals( "a/d", FileUtil.catPath( "a/b/c", "../d" ) );

        assertEquals( "b", FileUtil.catPath( "a", "b" ) );
    }
}
