package org.apache.log.util.test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.apache.log.Hierarchy;
import org.apache.log.LogTarget;
import org.apache.log.Logger;
import org.apache.log.Priority;
import org.apache.log.format.PatternFormatter;
import org.apache.log.format.RawFormatter;
import org.apache.log.output.StreamTarget;
import org.apache.log.util.OutputStreamLogger;
import org.apache.testlet.AbstractTestlet;

/**
 * Test suite for utility features of Logger.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class UtilTestlet
    extends AbstractTestlet
{
    private final static String EOL = System.getProperty( "line.separator", "\n" );
    private final static RawFormatter FORMATTER = new RawFormatter();

    private final static String MSG = "No soup for you!";
    private final static String RMSG = MSG;
    private final static String METHOD_RESULT = UtilTestlet.class.getName() + ".";
    
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

    public void testOutputStreamLogger()
        throws Exception
    {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final StreamTarget target = new StreamTarget( output, FORMATTER );

        final Hierarchy hierarchy = new Hierarchy();
        hierarchy.setDefaultLogTarget( target );

        final Logger logger = hierarchy.getLoggerFor( "myLogger" );
        final OutputStreamLogger outputStream = new OutputStreamLogger( logger, Priority.DEBUG );
        final PrintStream printer = new PrintStream( outputStream, true );

        printer.println( MSG );
        assertEquality( "OutputStreamLogger", RMSG + EOL, getResult( output ) );

        //unbuffered output
        printer.print( MSG );
        printer.flush();
        assertEquality( "OutputStreamLogger", RMSG, getResult( output ) );

        printer.close();
    }
}
