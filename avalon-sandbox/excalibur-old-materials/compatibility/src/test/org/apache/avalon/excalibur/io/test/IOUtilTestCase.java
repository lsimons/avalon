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
    private void assertEqualContent( final byte[] b0, final File file )
        throws IOException
    {
        final FileInputStream is = new FileInputStream( file );
        byte[] b1 = new byte[ b0.length ];
        int numRead = is.read( b1 );
        assertTrue( "Different number of bytes", numRead == b0.length && is.available() == 0 );
        for( int i = 0;
             i < numRead;
             assertTrue( "Byte " + i + " differs (" + b0[ i ] + " != " + b1[ i ] + ")", b0[ i ] == b1[ i ] ), i++
            )
            ;
    }

    public void testInputStreamToOutputStream()
        throws Exception
    {
        final File destination = newFile( "copy1.txt" );
        final FileInputStream fin = new FileInputStream( m_testFile );
        final FileOutputStream fout = new FileOutputStream( destination );

        IOUtil.copy( fin, fout );
        assertTrue( "Not all bytes were read", fin.available() == 0 );
        fout.flush();

        checkFile( destination );
        checkWrite( fout );
        fout.close();
        fin.close();
        deleteFile( destination );
    }

    public void testInputStreamToWriter()
        throws Exception
    {
        final File destination = newFile( "copy2.txt" );
        final FileInputStream fin = new FileInputStream( m_testFile );
        final FileWriter fout = new FileWriter( destination );

        IOUtil.copy( fin, fout );

        assertTrue( "Not all bytes were read", fin.available() == 0 );
        fout.flush();

        checkFile( destination );
        checkWrite( fout );
        fout.close();
        fin.close();
        deleteFile( destination );
    }

    public void testInputStreamToString()
        throws Exception
    {
        final FileInputStream fin = new FileInputStream( m_testFile );
        final String out = IOUtil.toString( fin );
        assertNotNull( out );
        assertTrue( "Not all bytes were read", fin.available() == 0 );
        assertTrue( "Wrong output size: out.length()=" + out.length() +
                    "!=" + FILE_SIZE, out.length() == FILE_SIZE );
        fin.close();
    }

    public void testReaderToOutputStream()
        throws Exception
    {
        final File destination = newFile( "copy3.txt" );
        final FileReader fin = new FileReader( m_testFile );
        final FileOutputStream fout = new FileOutputStream( destination );
        IOUtil.copy( fin, fout );
        //Note: this method *does* flush. It is equivalent to:
        //  final OutputStreamWriter _out = new OutputStreamWriter(fout);
        //  IOUtil.copy( fin, _out, 4096 ); // copy( Reader, Writer, int );
        //  _out.flush();
        //  out = fout;

        // Note: rely on the method to flush
        checkFile( destination );
        checkWrite( fout );
        fout.close();
        fin.close();
        deleteFile( destination );
    }

    public void testReaderToWriter()
        throws Exception
    {
        final File destination = newFile( "copy4.txt" );
        final FileReader fin = new FileReader( m_testFile );
        final FileWriter fout = new FileWriter( destination );
        IOUtil.copy( fin, fout );

        fout.flush();
        checkFile( destination );
        checkWrite( fout );
        fout.close();
        fin.close();
        deleteFile( destination );
    }

    public void testReaderToString()
        throws Exception
    {
        final FileReader fin = new FileReader( m_testFile );
        final String out = IOUtil.toString( fin );
        assertNotNull( out );
        assertTrue( "Wrong output size: out.length()=" +
                    out.length() + "!=" + FILE_SIZE,
                    out.length() == FILE_SIZE );
        fin.close();
    }

    public void testStringToOutputStream()
        throws Exception
    {
        final File destination = newFile( "copy5.txt" );
        final FileReader fin = new FileReader( m_testFile );
        // Create our String. Rely on testReaderToString() to make sure this is valid.
        final String str = IOUtil.toString( fin );
        final FileOutputStream fout = new FileOutputStream( destination );
        IOUtil.copy( str, fout );
        //Note: this method *does* flush. It is equivalent to:
        //  final OutputStreamWriter _out = new OutputStreamWriter(fout);
        //  IOUtil.copy( str, _out, 4096 ); // copy( Reader, Writer, int );
        //  _out.flush();
        //  out = fout;
        // note: we don't flush here; this IOUtils method does it for us

        checkFile( destination );
        checkWrite( fout );
        fout.close();
        fin.close();
        deleteFile( destination );
    }

    public void testStringToWriter()
        throws Exception
    {
        final File destination = newFile( "copy6.txt" );
        FileReader fin = new FileReader( m_testFile );
        // Create our String. Rely on testReaderToString() to make sure this is valid.
        final String str = IOUtil.toString( fin );
        final FileWriter fout = new FileWriter( destination );
        IOUtil.copy( str, fout );
        fout.flush();

        checkFile( destination );
        checkWrite( fout );
        fout.close();
        fin.close();

        deleteFile( destination );
    }

    public void testInputStreamToByteArray()
        throws Exception
    {
        final FileInputStream fin = new FileInputStream( m_testFile );
        final byte[] out = IOUtil.toByteArray( fin );
        assertNotNull( out );
        assertTrue( "Not all bytes were read", fin.available() == 0 );
        assertTrue( "Wrong output size: out.length=" + out.length +
                    "!=" + FILE_SIZE, out.length == FILE_SIZE );
        assertEqualContent( out, m_testFile );
        fin.close();
    }

    public void testStringToByteArray()
        throws Exception
    {
        final FileReader fin = new FileReader( m_testFile );

        // Create our String. Rely on testReaderToString() to make sure this is valid.
        final String str = IOUtil.toString( fin );

        final byte[] out = IOUtil.toByteArray( str );
        assertEqualContent( str.getBytes(), out );
        fin.close();
    }

    public void testByteArrayToWriter()
        throws Exception
    {
        final File destination = newFile( "copy7.txt" );
        final FileWriter fout = new FileWriter( destination );
        final FileInputStream fin = new FileInputStream( m_testFile );

        // Create our byte[]. Rely on testInputStreamToByteArray() to make sure this is valid.
        final byte[] in = IOUtil.toByteArray( fin );
        IOUtil.copy( in, fout );
        fout.flush();
        checkFile( destination );
        checkWrite( fout );
        fout.close();
        fin.close();
        deleteFile( destination );
    }

    public void testByteArrayToString()
        throws Exception
    {
        final FileInputStream fin = new FileInputStream( m_testFile );
        final byte[] in = IOUtil.toByteArray( fin );
        // Create our byte[]. Rely on testInputStreamToByteArray() to make sure this is valid.
        String str = IOUtil.toString( in );
        assertEqualContent( in, str.getBytes() );
        fin.close();
    }

    public void testByteArrayToOutputStream()
        throws Exception
    {
        final File destination = newFile( "copy8.txt" );
        final FileOutputStream fout = new FileOutputStream( destination );
        final FileInputStream fin = new FileInputStream( m_testFile );

        // Create our byte[]. Rely on testInputStreamToByteArray() to make sure this is valid.
        final byte[] in = IOUtil.toByteArray( fin );

        IOUtil.copy( in, fout );

        fout.flush();

        checkFile( destination );
        checkWrite( fout );
        fout.close();
        fin.close();
        deleteFile( destination );
    }


    //////////////////////////////////////////////////////
    // xxxxxxxxx


    private File newFile( String filename )
        throws Exception
    {
        final File destination = new File( m_testDirectory, filename );
        assertTrue( filename + "Test output data file shouldn't previously exist",
                    !destination.exists() );

        return destination;
    }

    private void checkFile( final File file )
        throws Exception
    {
        assertTrue( "Check existence of output file", file.exists() );
        assertEqualContent( m_testFile, file );
    }

    private void checkWrite( final OutputStream output )
        throws Exception
    {
        try
        {
            new PrintStream( output ).write( 0 );
        }
        catch( final Throwable t )
        {
            throw new AssertionFailedError( "The copy() method closed the stream " +
                                            "when it shouldn't have. " + t.getMessage() );
        }
    }

    private void checkWrite( final Writer output )
        throws Exception
    {
        try
        {
            new PrintWriter( output ).write( 'a' );
        }
        catch( final Throwable t )
        {
            throw new AssertionFailedError( "The copy() method closed the stream " +
                                            "when it shouldn't have. " + t.getMessage() );
        }
    }

    private void deleteFile( final File file )
        throws Exception
    {
        assertTrue( "Wrong output size: file.length()=" +
                    file.length() + "!=" + FILE_SIZE + 1,
                    file.length() == FILE_SIZE + 1 );

        //assertTrue( "File would not delete", (file.delete() || ( !file.exists() )));
    }
}
