/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output.io.rotate;

import java.io.File;
import junit.framework.TestCase;
import org.apache.log.Hierarchy;
import org.apache.log.LogTarget;
import org.apache.log.Logger;
import org.apache.log.format.RawFormatter;
import org.apache.log.output.io.rotate.RotatingFileTarget;

/** 
 * 
 * @author <a href="mailto:bh22351@i-one.at">Bernhard Huber</a>
 */
public class TestRotatingFileOutputLogTarget 
    extends TestCase
{
    private RawFormatter m_formatter = new RawFormatter();

    public TestRotatingFileOutputLogTarget() 
    {
        super( "TestRotatingFileOutputLogTarget" );
    }

    public TestRotatingFileOutputLogTarget( final String name ) 
    {
        super( name );
    }

    /** test file rotation by size, using unique filenames
     */
    public void testSizeRotationUniqueFilename() 
        throws Exception
    {
        final File file = new File( "test/size-unique.log" );
        final FilenameStrategy filenameStrategy = new FilenameStrategyUniqueLogFile( file );
        final RotateStrategy rotateStrategy = new RotateStrategyBySize( 128 * 1024 );
        final Logger logger = getLogger( filenameStrategy, rotateStrategy );
        
        doTest( logger );
    }

    /** test file rotation by size, using revolving filenames
     */
    public void testSizeRotationRevolingFilename() 
        throws Exception
    {
        final File file = new File( "test/size-revolve.log" );
        final FilenameStrategy filenameStrategy = new FilenameStrategyRevolvingLogFile( file );
        final RotateStrategy rotateStrategy = new RotateStrategyBySize( 128 * 1024 );
        final Logger logger = getLogger( filenameStrategy, rotateStrategy );
        
        doTest( logger );
    }

    /** test file rotation by time, using unique filenames
     */
    public void testTimeRotationUniqueFilename() 
        throws Exception
    {
        final File file = new File( "test/time-unique.log" );
        final FilenameStrategy filenameStrategy = new FilenameStrategyUniqueLogFile( file );
        final RotateStrategy rotateStrategy = new RotateStrategyByTime( 3 * 1000 );
        final Logger logger = getLogger( filenameStrategy, rotateStrategy );
        
        doTest( logger );
    }

    /** test file rotation by time, using revolving filenames
     */
    public void testTimeRotationRevolvingFilename() 
        throws Exception
    {
        final File file = new File( "test/time-revolve.log" );
        final FilenameStrategy filenameStrategy = new FilenameStrategyRevolvingLogFile( file );
        final RotateStrategy rotateStrategy = new RotateStrategyByTime( 3 * 1000 );
        final Logger logger = getLogger( filenameStrategy, rotateStrategy );
        
        doTest( logger );
    }

    private void doTest( final Logger logger )
    {
        final long startTime = System.currentTimeMillis();
        final long diffTime = 10 * 1000;
        //final long diffTime = 1 * 1000;
        long endTime = startTime;

        int size = 0;
        for( int i = 0; (endTime - startTime) < diffTime; i++ )
        {
            size += generateMessages( logger, i, size, (endTime - startTime ) );
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
            "Message " + i +  ": total size " + totalSize + " diff time " + diffTime;
        logger.debug( message );
        logger.info( message );
        logger.warn( message );
        logger.error( message );
        logger.fatalError( message );

        return message.length();
    }

    private Logger getLogger( final FilenameStrategy filenameStrategy, 
                              final RotateStrategy rotateStrategy )
        throws Exception
    {
        final RotatingFileTarget target = 
            new RotatingFileTarget( m_formatter, rotateStrategy, filenameStrategy );
        final Hierarchy hierarchy = new Hierarchy();
        final Logger logger = hierarchy.getLoggerFor( "myCat" );

        logger.setLogTargets( new LogTarget[] { target } );

        return logger;
    }

    public static void main( final String args[] ) 
        throws Exception
    {
        TestRotatingFileOutputLogTarget trfolt = new TestRotatingFileOutputLogTarget();
        trfolt.testSizeRotationUniqueFilename();
        trfolt.testSizeRotationRevolingFilename();
        trfolt.testTimeRotationUniqueFilename();
        trfolt.testTimeRotationRevolvingFilename();
    }
}
