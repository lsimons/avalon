package org.apache.avalon.framework.parameters.test;

import java.util.Properties;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import junit.framework.TestCase;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;

/**
 * TestCase for Parameter.
 * FIXME: Write messages for each assertion.
 * Writing message in English is very difficult for me :-(.
 *
 * @author <a href="mailto:colus@isoft.co.kr">Eung-ju Park</a>
 */
public class ParameterTestCase
    extends TestCase
{
    private static final String EOL = "\n";

    public ParameterTestCase( final String name )
    {
        super( name );
    }

    public void testRemoveParameter()
    {
        final Parameters parameters = new Parameters();
        parameters.setParameter( "key1", "value1" );
        assertEquals( 1, parameters.getNames().length );
        parameters.setParameter( "key1", null );
        assertTrue( ! parameters.isParameter( "key1" ) );
        assertEquals( 0, parameters.getNames().length );
    }

    public void testIsParameter()
    {
        final Parameters parameters = new Parameters();
        parameters.setParameter( "key1", "value1" );
        assertTrue( parameters.isParameter( "key1" ) );
        assertTrue( ! parameters.isParameter( "key2" ) );
    }

    public void testGetParameter()
    {
        final Parameters parameters = new Parameters();
        parameters.setParameter( "key1", "value1" );

        try
        {
            assertEquals( "value1", parameters.getParameter( "key1" ) );
        }
        catch ( final ParameterException pe )
        {
            fail( pe.getMessage() );
        }

        try
        {
            parameters.getParameter( "key2" );
            fail( "Not inserted parameter 'key2' exists" );
        }
        catch( final ParameterException pe )
        {
            //OK
        }

        assertEquals( "value1", parameters.getParameter( "key1", "value1-1" ) );

        assertEquals( "value2", parameters.getParameter( "key2", "value2" ) );
    }

    public void testFromConfiguration()
    {
        final ByteArrayInputStream confInput = new ByteArrayInputStream( (
            "<?xml version=\"1.0\"?>" + EOL +
            "<test>" + EOL +
            "<parameter name=\"key1\" value=\"value1\"/>" + EOL +
            "<parameter name=\"key2\" value=\"value2\"/>" + EOL +
            "<parameter name=\"key3\" value=\"value3\"/>" + EOL +
            "</test>" ).getBytes() );

        try
        {
            final DefaultConfigurationBuilder builder =
                new DefaultConfigurationBuilder();
            final Configuration configuration = builder.build( confInput );

            final Parameters parameters =
                Parameters.fromConfiguration( configuration );

            assertEquals( "value1", parameters.getParameter( "key1" ) );
            assertEquals( "value2", parameters.getParameter( "key2" ) );
            assertEquals( "value3", parameters.getParameter( "key3" ) );
        }
        catch ( final ConfigurationException ce )
        {
            fail( "Converting failed: " + ce.getMessage() );
        }
        catch ( final Exception e )
        {
            fail( e.getMessage() );
        }
    }

    public void testFromProperties()
    {
        final Properties properties = new Properties();
        properties.put( "key1", "value1" );
        properties.put( "key2", "value2" );
        properties.put( "key3", "value3" );

        final Parameters parameters = Parameters.fromProperties( properties );

        try
        {
            assertEquals( "value1", parameters.getParameter( "key1" ) );
            assertEquals( "value2", parameters.getParameter( "key2" ) );
            assertEquals( "value3", parameters.getParameter( "key3" ) );
        }
        catch ( final ParameterException pe )
        {
            fail( pe.getMessage() );
        }
    }
}
