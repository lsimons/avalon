/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output.test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import org.apache.log.Hierarchy;
import org.apache.log.LogTarget;
import org.apache.log.Logger;
import org.apache.log.format.RawFormatter;
import org.apache.log.output.AbstractOutputTarget;
import org.apache.log.output.StreamTarget;
import org.apache.log.output.WriterTarget;
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

    private static RawFormatter FORMATTER = new RawFormatter();

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
        doTest( output, target );
    }

    public void testWriterTarget()
        throws Exception
    {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final OutputStreamWriter writer = new OutputStreamWriter( output );
        final WriterTarget target = new WriterTarget( writer, FORMATTER );
        doTest( output, target );
    }

    private void doTest( final ByteArrayOutputStream output,
                         final AbstractOutputTarget target )
    {
        final Hierarchy hierarchy = new Hierarchy();
        final Logger logger = hierarchy.getLoggerFor( "myCategory" );
        logger.setLogTargets( new LogTarget[] { target } );

        final String head = getResult( output );

        logger.debug( M1 );
        final String result1 = getResult( output );

        logger.debug( M2 );
        final String result2 = getResult( output );

        logger.debug( M3 );
        final String result3 = getResult( output );

        target.close();
        final String tail = getResult( output );

        assertEquality( "Targets Head output", HEAD, head );
        assertEquality( "Targets Tail output", TAIL, tail );
        assertEquality( "Targets R1 debug output", R1, result1 );
        assertEquality( "Targets R2 debug output", R2, result2 );
        assertEquality( "Targets R3 debug output", R3, result3 );

        logger.debug( M1 );
        final String noresult = getResult( output );
        //final String errorResult = getErrorResult( errorOutput );

        assertEquality( "Write after close()", "", noresult );
        //assertEquality( "Epecting error", "", errorResult );
    }
}
