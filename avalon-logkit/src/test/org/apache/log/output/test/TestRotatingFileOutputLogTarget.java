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
import org.apache.log.Hierarchy;
import org.apache.log.LogTarget;
import org.apache.log.Logger;
import org.apache.log.format.RawFormatter;
import org.apache.log.output.io.rotate.FileStrategy;
import org.apache.log.output.io.rotate.RevolvingFileStrategy;
import org.apache.log.output.io.rotate.RotateStrategy;
import org.apache.log.output.io.rotate.RotateStrategyBySize;
import org.apache.log.output.io.rotate.RotateStrategyByTime;
import org.apache.log.output.io.rotate.RotatingFileTarget;
import org.apache.log.output.io.rotate.UniqueFileStrategy;

/**
 *
 * @author <a href="mailto:bh22351@i-one.at">Bernhard Huber</a>
 */
public class TestRotatingFileOutputLogTarget
{
    private RawFormatter m_formatter = new RawFormatter();

    /** test file rotation by size, using unique filenames
     */
    public void testSizeUnique()
        throws Exception
    {
        final File file = new File( "test/size-unique.log" );
        final FileStrategy fileStrategy = new UniqueFileStrategy( file );
        final RotateStrategy rotateStrategy = new RotateStrategyBySize( 128 * 1024 );
        final Logger logger = getLogger( fileStrategy, rotateStrategy );

        doTest( logger );
    }

    /** test file rotation by size, using revolving filenames
     */
    public void testSizeRevoling()
        throws Exception
    {
        final File file = new File( "test/size-revolve.log" );
        final FileStrategy fileStrategy = new RevolvingFileStrategy( file, 20 );
        final RotateStrategy rotateStrategy = new RotateStrategyBySize( 128 * 1024 );
        final Logger logger = getLogger( fileStrategy, rotateStrategy );

        doTest( logger );
    }

    /** test file rotation by time, using unique filenames
     */
    public void testTimeUnique()
        throws Exception
    {
        final File file = new File( "test/time-unique.log" );
        final FileStrategy fileStrategy = new UniqueFileStrategy( file );
        final RotateStrategy rotateStrategy = new RotateStrategyByTime( 3 * 1000 );
        final Logger logger = getLogger( fileStrategy, rotateStrategy );

        doTest( logger );
    }

    /** test file rotation by time, using revolving filenames
     */
    public void testTimeRevolving()
        throws Exception
    {
        final File file = new File( "test/time-revolve.log" );
        final FileStrategy fileStrategy = new RevolvingFileStrategy( file, 5 );
        final RotateStrategy rotateStrategy = new RotateStrategyByTime( 3 * 1000 );
        final Logger logger = getLogger( fileStrategy, rotateStrategy );

        doTest( logger );
    }

    private void doTest( final Logger logger )
    {
        final long startTime = System.currentTimeMillis();
        final long diffTime = 10 * 1000;
        long endTime = startTime;

        int size = 0;
        for( int i = 0; ( endTime - startTime ) < diffTime; i++ )
        {
            size += generateMessages( logger, i, size, ( endTime - startTime ) );
            endTime = System.currentTimeMillis();
        }
    }

    /** just generate some logger messages
     */
    private int generateMessages( final Logger logger,
                                  final int i,
                                  final long totalSize,
                                  final long diffTime )
    {
        final String message =
            "Message " + i + ": total size " + totalSize + " diff time " + diffTime;
        logger.debug( message );
        logger.info( message );
        logger.warn( message );
        logger.error( message );
        logger.fatalError( message );

        return message.length();
    }

    private Logger getLogger( final FileStrategy fileStrategy,
                              final RotateStrategy rotateStrategy )
        throws Exception
    {
        final RotatingFileTarget target =
            new RotatingFileTarget( m_formatter, rotateStrategy, fileStrategy );
        final Hierarchy hierarchy = new Hierarchy();
        final Logger logger = hierarchy.getLoggerFor( "myCat" );

        logger.setLogTargets( new LogTarget[]{target} );

        return logger;
    }

    public static void main( final String args[] )
        throws Exception
    {
        TestRotatingFileOutputLogTarget trfolt = new TestRotatingFileOutputLogTarget();
        trfolt.testSizeUnique();
        trfolt.testSizeRevoling();
        trfolt.testTimeUnique();
        trfolt.testTimeRevolving();
    }
}
