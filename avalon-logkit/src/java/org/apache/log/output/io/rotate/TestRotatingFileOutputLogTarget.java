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
import org.apache.log.format.PatternFormatter;
import org.apache.log.output.io.rotate.RotatingFileOutputLogTarget;

/** 
 * 
 * @author <a href="mailto:bh22351@i-one.at">Bernhard Huber</a>
 */
public class TestRotatingFileOutputLogTarget extends TestCase {
    Logger logger;
    RotatingFileOutputLogTarget target;
    PatternFormatter formatter;

    public TestRotatingFileOutputLogTarget() {
        super( "TestRotatingFileOutputLogTarget" );
    }
    public TestRotatingFileOutputLogTarget( String name ) {
        super( name );
    }
    protected void setUp() throws Exception {
        final String pattern = "%7.7{priority} %5.5{time}   [%8.8{category}] " +
            "(%{context}): %{message}\\n%{throwable}";
        formatter = new PatternFormatter( pattern );
        logger = Hierarchy.getDefaultHierarchy().getLoggerFor("TestRotatingFileOutputLogTarget");
    }

    protected void tearDown() throws Exception {
        logger.unsetLogTargets();
    }

    /** test file rotation by size, using unique filenames
     */
    public void testSizeRotationUniqueFilename() throws Exception {
        final File file = new File( "testSizeRotationUniqueFilename.log" );
        target = new RotatingFileOutputLogTarget( file, formatter );
        logger.setLogTargets( new LogTarget[] { target } );

        target.setRotateStrategyBySizeKB( 128 ); // rotate every N KB

        long start_time = System.currentTimeMillis();
        long diff_time = 10 * 1000; // generate messages for 10 secs
        long end_time = start_time;

        long total_max_size = 1024 * (1024 + 512); // generate messages of 1.5 MB
        long total_size = 0;
        for (int i = 0; total_size < total_max_size; i++ ) {
            total_size += generateMessages( logger, i, total_size, (end_time - start_time ) );
            end_time = System.currentTimeMillis();
        }
    }

    /** test file rotation by size, using revolving filenames
     */
    public void testSizeRotationRevolingFilename() throws Exception {
        final File file = new File( "testSizeRotationRevolingFilename.log" );
        target = new RotatingFileOutputLogTarget( file, formatter );
        logger.setLogTargets( new LogTarget[] { target } );

        target.setRotateStrategyBySizeKB( 128 ); // rotate every N KB
        target.setFilenameStrategyRevolvingLogFile();

        long start_time = System.currentTimeMillis();
        long diff_time = 10 * 1000; // generate messages for 10 secs
        long end_time = start_time;

        long total_max_size = 1024 * (1024 + 512); // generate messages of 1.5 MB
        long total_size = 0;
        for (int i = 0; total_size < total_max_size; i++ ) {
            total_size += generateMessages( logger, i, total_size, (end_time - start_time ) );
            end_time = System.currentTimeMillis();
        }
    }

    /** test file rotation by time, using unique filenames
     */
    public void testTimeRotationUniqueFilename() throws Exception {
        final File file = new File( "testTimeRotationUniqueFilename.log" );
        target = new RotatingFileOutputLogTarget( file, formatter );
        logger.setLogTargets( new LogTarget[] { target } );

        target.setRotateStrategyByTimeSeconds( 3 );

        long start_time = System.currentTimeMillis();
        long diff_time = 10 * 1000;
        long end_time = start_time;

        long total_max_size = 1024 * ( 512 + 1024); // generate at least 1.5 MB log messages
        long total_size = 0;
        for (int i = 0; (end_time - start_time) < diff_time; i++ ) {
            total_size += generateMessages( logger, i, total_size, (end_time - start_time ) );
            end_time = System.currentTimeMillis();
        }
    }

    /** test file rotation by time, using revolving filenames
     */
    public void testTimeRotationRevolvingFilename() throws Exception {
        final File file = new File( "testTimeRotationRevolingFilename.log" );
        target = new RotatingFileOutputLogTarget( file, formatter );
        logger.setLogTargets( new LogTarget[] { target } );

        target.setRotateStrategyByTimeSeconds( 3 );
        target.setFilenameStrategyRevolvingLogFile();

        long start_time = System.currentTimeMillis();
        long diff_time = 10 * 1000;
        long end_time = start_time;

        long total_max_size = 1024 * ( 512 + 1024); // generate at least 1.5 MB log messages
        long total_size = 0;
        for (int i = 0; (end_time - start_time) < diff_time; i++ ) {
            total_size += generateMessages( logger, i, total_size, (end_time - start_time ) );
            end_time = System.currentTimeMillis();
        }
    }

    /** just generate some logger messages
     */
    long generateMessages( Logger logger, int i, long total_size, long diff_time )
    {
        String message = " TestRotatingFileOutputLogTarget - " +
            String.valueOf( i ) +  " : " +
            " total size " + String.valueOf( total_size ) +
            " diff time " + String.valueOf( diff_time );
        logger.debug( message );
        logger.info( message );
        logger.warn( message );
        logger.error( message );
        logger.fatalError( message );

        return message.length() * 5;
    }

    public static void main( String args[] ) throws Exception {
        TestRotatingFileOutputLogTarget trfolt = new TestRotatingFileOutputLogTarget();
        trfolt.setUp();
        trfolt.testSizeRotationUniqueFilename();
        trfolt.testSizeRotationRevolingFilename();
        trfolt.testTimeRotationUniqueFilename();
        trfolt.testTimeRotationRevolvingFilename();
        trfolt.tearDown();
    }

}
