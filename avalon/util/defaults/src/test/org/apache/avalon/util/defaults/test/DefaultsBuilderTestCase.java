
package org.apache.avalon.util.defaults.test;

import java.util.Properties;
import java.io.File;
import java.io.IOException;

import junit.framework.TestCase ;

import org.apache.avalon.util.defaults.DefaultsBuilder;

/**
 * DefaultsBuilderTestCase
 * 
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.1 $
 */
public class DefaultsBuilderTestCase extends TestCase
{
    private static final String KEY = "test";

    private DefaultsBuilder m_builder;

    protected void setUp() throws Exception
    {
        File base = new File( System.getProperty( "basedir" ) );
        File dir = new File( base, "target/test-classes" );
        System.out.println( "dir: " + dir );

        m_builder = new DefaultsBuilder( KEY, dir );
    }

    public void testHomeDirectory() throws Exception
    {
        System.out.println( "inst: " + m_builder.getHomeDirectory() );
    }

    public void testHomeProperties() throws Exception
    {
        System.out.println( "home: " + m_builder.getHomeProperties() );
    }

    public void testUserProperties() throws Exception
    {
        System.out.println( "user: " + m_builder.getUserProperties() );
    }

    public void testDirProperties() throws Exception
    {
        System.out.println( "dir: " + m_builder.getDirProperties() );
    }

    public void testConsolidatedProperties() throws Exception
    {
        File base = new File( System.getProperty( "basedir" ) );
        File dir = new File( base, "target/test-classes" );
        File props = new File( dir, "test.keys" );
        Properties properties = DefaultsBuilder.getProperties( props );
        String[] keys = (String[]) properties.keySet().toArray( new String[0] );
        Properties defaults = 
          DefaultsBuilder.getProperties(
            DefaultsBuilderTestCase.class.getClassLoader(),
            "static.properties" );
        System.out.println( 
          "con: " 
          + m_builder.getConsolidatedProperties( defaults, keys ) );
    }

}
