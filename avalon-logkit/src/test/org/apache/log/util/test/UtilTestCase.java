/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.util.test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import junit.framework.TestCase;
import org.apache.log.Hierarchy;
import org.apache.log.Logger;
import org.apache.log.Priority;
import org.apache.log.format.RawFormatter;
import org.apache.log.output.io.StreamTarget;
import org.apache.log.util.LoggerOutputStream;

/**
 * Test suite for utility features of Logger.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public final class UtilTestCase
    extends TestCase
{
    private final static String EOL = System.getProperty( "line.separator", "\n" );
    private final static RawFormatter FORMATTER = new RawFormatter();

    private final static String MSG = "No soup for you!";
    private final static String RMSG = MSG;
    private final static String METHOD_RESULT = UtilTestCase.class.getName() + ".";

    public UtilTestCase( final String name )
    {
        super( name );
    }

    private String getResult( final ByteArrayOutputStream output )
    {
        final String result = output.toString();
        output.reset();
        return result;
    }

    public void testStackIntrospector()
        throws Exception
    {
        /*
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final StreamTarget target = new StreamTarget( output, METHOD_FORMATTER );
        final Hierarchy hierarchy = new Hierarchy();
        hierarchy.setDefaultLogTarget( target );

        final Logger logger = hierarchy.getLoggerFor( "myLogger" );

        logger.debug( MSG );
        final String result = getResult( output );
        final String expected = METHOD_RESULT + "testStackIntrospector()";
        assert( "StackIntrospector", result.startsWith( expected ) );
        //result of StackIntrospector.getCallerMethod( Logger.class );
        */
    }

    public void testLoggerOutputStream()
        throws Exception
    {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final StreamTarget target = new StreamTarget( output, FORMATTER );

        final Hierarchy hierarchy = new Hierarchy();
        hierarchy.setDefaultLogTarget( target );

        final Logger logger = hierarchy.getLoggerFor( "myLogger" );
        final LoggerOutputStream outputStream = new LoggerOutputStream( logger, Priority.DEBUG );
        final PrintStream printer = new PrintStream( outputStream, true );

        printer.println( MSG );
        assertEquals( "LoggerOutputStream", RMSG + EOL, getResult( output ) );

        //unbuffered output
        printer.print( MSG );
        printer.flush();
        assertEquals( "LoggerOutputStream", RMSG, getResult( output ) );

        printer.close();
    }
}
