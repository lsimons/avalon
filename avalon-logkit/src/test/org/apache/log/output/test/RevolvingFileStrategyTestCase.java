/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2002 The Apache Software Foundation. All rights
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
package org.apache.log.output.test;

import java.io.File;
import java.io.IOException;
import junit.framework.TestCase;
import org.apache.log.output.io.rotate.RevolvingFileStrategy;

/**
 * Test suite for the RevolvingFileStrategy.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public final class RevolvingFileStrategyTestCase
    extends TestCase
{
    private static final int OLD_AGE = 100000000;
    private static final int YOUNG_AGE = -100000000;

    private final File m_baseFile;
    private final long m_now = System.currentTimeMillis();

    public RevolvingFileStrategyTestCase( final String name )
        throws IOException
    {
        super( name );

        m_baseFile = ( new File( "build/testdata/log" ) ).getCanonicalFile();
        m_baseFile.getParentFile().mkdirs();
    }

    private void deleteFiles( final int maxRotation )
        throws IOException
    {
        for( int i = 0; i <= maxRotation; i++ )
        {
            final File file = new File( getFilename( i ) );
            if( file.exists() && !file.delete() )
            {
                throw new IOException( "Failed to delete file " + file );
            }
        }
    }

    private String getFilename( int i )
    {
        return m_baseFile.toString() + ".00000" + i;
    }

    private void createFile( final int rotation, final long age )
        throws IOException
    {
        final File file = new File( getFilename( rotation ) );
        if( !file.createNewFile() )
        {
            throw new IOException( "Failed to create file " + file );
        }
        final long time = m_now - age;
        file.setLastModified( time );
    }

    public void testNew()
        throws Exception
    {
        deleteFiles( 9 );

        final RevolvingFileStrategy strategy =
            new RevolvingFileStrategy( m_baseFile, 9 );

        assertEquals( "rotation", 0, strategy.getCurrentRotation() );
    }

    public void testRotationExisting()
        throws Exception
    {
        deleteFiles( 9 );
        createFile( 0, 0 );

        final RevolvingFileStrategy strategy =
            new RevolvingFileStrategy( m_baseFile, 9 );

        assertEquals( "rotation", 1, strategy.getCurrentRotation() );
    }

    public void testRotationExisting2()
        throws Exception
    {
        deleteFiles( 9 );
        createFile( 0, 0 );
        createFile( 1, 0 );
        createFile( 2, 0 );
        createFile( 3, 0 );
        createFile( 4, 0 );

        final RevolvingFileStrategy strategy =
            new RevolvingFileStrategy( m_baseFile, 9 );

        assertEquals( "rotation", 5, strategy.getCurrentRotation() );
    }

    public void testRotationExistingWithMissing()
        throws Exception
    {
        deleteFiles( 9 );
        createFile( 0, 0 );
        createFile( 4, 0 );

        final RevolvingFileStrategy strategy =
            new RevolvingFileStrategy( m_baseFile, 9 );

        assertEquals( "rotation", 5, strategy.getCurrentRotation() );
    }

    public void testRotationExistingWithOlderLower()
        throws Exception
    {
        deleteFiles( 9 );
        createFile( 0, OLD_AGE ); //Note this is oldest
        createFile( 4, 0 );

        final RevolvingFileStrategy strategy =
            new RevolvingFileStrategy( m_baseFile, 9 );

        assertEquals( "rotation", 5, strategy.getCurrentRotation() );
    }

    public void testRotationExistingWithOlderHigher()
        throws Exception
    {
        deleteFiles( 9 );
        createFile( 0, 0 );
        createFile( 4, OLD_AGE );

        final RevolvingFileStrategy strategy =
            new RevolvingFileStrategy( m_baseFile, 9 );

        assertEquals( "rotation", 5, strategy.getCurrentRotation() );
    }

    public void testFullRotation()
        throws Exception
    {
        deleteFiles( 9 );
        createFile( 0, 0 );
        createFile( 1, 0 );
        createFile( 2, 0 );
        createFile( 3, 0 );
        createFile( 4, 0 );
        createFile( 5, 0 );
        createFile( 6, 0 );
        createFile( 7, 0 );
        createFile( 8, 0 );
        createFile( 9, 0 );

        final RevolvingFileStrategy strategy =
            new RevolvingFileStrategy( m_baseFile, 9 );

        assertEquals( "rotation", 0, strategy.getCurrentRotation() );
    }

    public void testFullRotationWithOlder()
        throws Exception
    {
        deleteFiles( 9 );
        createFile( 0, 0 );
        createFile( 1, 0 );
        createFile( 2, 0 );
        createFile( 3, 0 );
        createFile( 4, 0 );
        createFile( 5, 0 );
        createFile( 6, 0 );
        createFile( 7, OLD_AGE );
        createFile( 8, 0 );
        createFile( 9, 0 );

        final RevolvingFileStrategy strategy =
            new RevolvingFileStrategy( m_baseFile, 9 );

        assertEquals( "rotation", 7, strategy.getCurrentRotation() );
    }
}
