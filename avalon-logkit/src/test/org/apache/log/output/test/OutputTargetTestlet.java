/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.apache.log.Hierarchy;
import org.apache.log.LogTarget;
import org.apache.log.Logger;
import org.apache.log.Priority;
import org.apache.log.format.ExtendedPatternFormatter;
import org.apache.log.format.PatternFormatter;
import org.apache.log.format.RawFormatter;
import org.apache.log.output.AbstractOutputTarget;
import org.apache.log.output.MemoryTarget;
import org.apache.log.output.io.FileTarget;
import org.apache.log.output.io.SafeFileTarget;
import org.apache.log.output.io.StreamTarget;
import org.apache.log.output.io.WriterTarget;
import org.apache.testlet.AbstractTestlet;

/**
 * Test suite for the formatters.
 * TODO: Incorporate testing for ContextStack and ContextMap
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class OutputTargetTestlet
    extends AbstractTestlet
{
    private static String M1 = "meep meep!";
    private static String M2 = "marina";
    private static String M3 = "spin and then fall down";

    private static String HEAD = "";
    private static String TAIL = "";

    private static String R1 = M1;
    private static String R2 = M2;
    private static String R3 = M3;

    private static String OUTPUT = HEAD + R1 + R2 + R3 + TAIL;

    private static RawFormatter FORMATTER = new RawFormatter();

    private final File     m_logFile;

    public OutputTargetTestlet()
        throws IOException
    {
        m_logFile = (new File( "test/log/logfile.txt" )).getCanonicalFile();
    }

    private String getResult( final ByteArrayOutputStream output )
    {
        final String result = output.toString();
        output.reset();
        return result;
    }

    public void testStreamTarget()
        throws Exception
    {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final StreamTarget target = new StreamTarget( output, FORMATTER );
        doStreamTest( output, target );
        /*
          final ExtendedPatternFormatter formatter =
          new ExtendedPatternFormatter( "%{method} from %{thread}\n" );
          final StreamTarget target2 = new StreamTarget( System.out, formatter );
          final Logger logger = getNewLogger( target2 );
          logger.debug( M1 );
        */
    }

    public void testWriterTarget()
        throws Exception
    {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final OutputStreamWriter writer = new OutputStreamWriter( output );
        final WriterTarget target = new WriterTarget( writer, FORMATTER );
        doStreamTest( output, target );
    }

    public void testFileTarget()
        throws Exception
    {
        final FileTarget target = new FileTarget( m_logFile, false, FORMATTER );

        final Logger logger = getNewLogger( target );
        logger.debug( M1 );
        logger.debug( M2 );
        logger.debug( M3 );
        target.close();

        final String data = getFileContents( m_logFile );
        assertEquality( "Targets file output", OUTPUT, data );
        assert( "Deleting logfile", m_logFile.delete() );

        logger.debug( M1 );
        assert( "Write after close()", !m_logFile.exists() );
    }

    public void testSafeFileTarget()
        throws Exception
    {
        final SafeFileTarget target = new SafeFileTarget( m_logFile, false, FORMATTER );

        final Logger logger = getNewLogger( target );
        logger.debug( M1 );

        final String data1 = getFileContents( m_logFile );
        assertEquality( "Targets file output", HEAD + R1, data1 );
        assert( "Deleting logfile", m_logFile.delete() );

        logger.debug( M2 );
        logger.debug( M3 );
        target.close();

        final String data2 = getFileContents( m_logFile );
        assertEquality( "Targets file output", R2 + R3 + TAIL, data2 );
        assert( "Deleting logfile", m_logFile.delete() );

        logger.debug( M1 );
        assert( "Write after close()", !m_logFile.exists() );
    }

    /**
     * Test that MemoryTarget triggers on buffer size hit.
     *
     * @exception Exception if an error occurs
     */
    public void testMemoryTarget1()
        throws Exception
    {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final StreamTarget other = new StreamTarget( output, FORMATTER );
        final MemoryTarget target = new MemoryTarget( other, 1, Priority.FATAL_ERROR );
        doStreamOutputTest( output, target );
    }

    /**
     * Test that MemoryTarget triggers on priority correctly.
     *
     * @exception Exception if an error occurs
     */
    public void testMemoryTarget2()
        throws Exception
    {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final StreamTarget other = new StreamTarget( output, FORMATTER );
        final MemoryTarget target = new MemoryTarget( other, 10, Priority.DEBUG );
        doStreamOutputTest( output, target );
    }

    /**
     * Test that MemoryTarget triggers on priority correctly.
     *
     * @exception Exception if an error occurs
     */
    public void testMemoryTarget3()
        throws Exception
    {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final StreamTarget other = new StreamTarget( output, FORMATTER );
        final MemoryTarget target = new MemoryTarget( other, 10, Priority.FATAL_ERROR );

        final Logger logger = getNewLogger( target );

        //Head output should not be pushed yet
        final String head = getResult( output );
        assertEquality( "Targets Head output", "", head );

        //Not pushed yet
        logger.debug( M1 );
        final String result1 = getResult( output );
        assertEquality( "Targets R1 debug output", "", result1 );

        target.push();
        final String resultPP = getResult( output );
        assertEquality( "Targets HEAD+R1 debug output", HEAD+R1, resultPP );

        logger.debug( M2 );
        final String result2 = getResult( output );

        logger.debug( M3 );
        final String result3 = getResult( output );

        //fatal error triggers a push
        logger.fatalError( M3 );
        final String result4 = getResult( output );

        assertEquality( "Targets R2 debug output", "", result2 );
        assertEquality( "Targets R3 debug output", "", result3 );
        assertEquality( "Targets R3 debug output", R2+R3+R3, result4 );
    }

    private Logger getNewLogger( final LogTarget target )
    {
        final Hierarchy hierarchy = new Hierarchy();
        final Logger logger = hierarchy.getLoggerFor( "myCategory" );
        logger.setLogTargets( new LogTarget[] { target } );
        return logger;
    }

    private String getFileContents( final File file )
        throws IOException
    {
        final FileInputStream input = new FileInputStream( file );
        final StringBuffer sb = new StringBuffer();

        int ch = input.read();
        while( -1 != ch )
        {
            sb.append( (char)ch );
            ch = input.read();
        }

        input.close();

        return sb.toString();
    }

    private Logger doStreamOutputTest( final ByteArrayOutputStream output,
                                       final LogTarget target )
    {
        final Logger logger = getNewLogger( target );

        final String head = getResult( output );

        logger.debug( M1 );
        final String result1 = getResult( output );

        logger.debug( M2 );
        final String result2 = getResult( output );

        logger.debug( M3 );
        final String result3 = getResult( output );

        assertEquality( "Targets Head output", HEAD, head );
        assertEquality( "Targets R1 debug output", R1, result1 );
        assertEquality( "Targets R2 debug output", R2, result2 );
        assertEquality( "Targets R3 debug output", R3, result3 );

        return logger;
    }

    private void doStreamTest( final ByteArrayOutputStream output,
                               final AbstractOutputTarget target )
    {
        final Logger logger = doStreamOutputTest( output, target );

        target.close();
        final String tail = getResult( output );
        assertEquality( "Targets Tail output", TAIL, tail );

        logger.debug( M1 );
        final String noresult = getResult( output );
        //final String errorResult = getErrorResult( errorOutput );

        assertEquality( "Write after close()", "", noresult );
        //assertEquality( "Epecting error", "", errorResult );
    }
}
