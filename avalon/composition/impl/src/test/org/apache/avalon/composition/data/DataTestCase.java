

package org.apache.avalon.composition.data;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileInputStream;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.composition.data.ContainmentProfile;
import org.apache.avalon.composition.data.builder.ContainmentProfileBuilder;
import org.apache.avalon.composition.data.builder.SerializedContainmentProfileCreator;
import org.apache.avalon.composition.data.builder.XMLContainmentProfileCreator;
import org.apache.avalon.composition.data.writer.SerializedContainmentProfileWriter;
import org.apache.avalon.composition.data.writer.XMLContainmentProfileWriter;
import org.apache.excalibur.configuration.ConfigurationUtil;

import junit.framework.TestCase;

public class DataTestCase extends TestCase
{

    private File m_source;
    private ContainmentProfile m_profile;
    private File m_serial;
    private File m_xml;

    public DataTestCase( )
    {
        this( "data" );
    }

    public DataTestCase( String name )
    {
        super( name );
    }

    protected void setUp() throws Exception
    {

        //
        // create a block profile from an XML source file
        //

        m_source = new File( getTestDir(), "test-classes/conf/data-test.xml" );
        m_profile = readFromXML( m_source );

        //
        // write the block to an XML file (in principal the 
        // created file should contain the same information 
        // as the input block.xml)
        //

        m_xml = new File( getTestDir(), "test-classes/test.xml" );
        writeToXML( m_profile, m_xml );

        //
        // write the block to a serial file
        //

        m_serial = new File( getTestDir(), "test-classes/test.block" );
        writeToSerial( m_profile, m_serial );
    }

    public void testXML() throws Exception
    {
        Configuration source = loadConfiguration( m_source );
        Configuration test = loadConfiguration( m_xml );
        boolean equality = compare( source, test );
        if( !equality )
        {
            System.out.println( "PROBLEM: " + source.getName() );
        }
        assertTrue( "equality", equality );
    }

    public boolean compare( Configuration a, Configuration b ) throws Exception
    {
        if( a.equals( b ) )
        {
            return true;
        }
        else
        {
            Configuration[] aaa = a.getChildren();
            Configuration[] bbb = b.getChildren();
            for( int i=0; i<aaa.length; i++ )
            {
                Configuration aa = aaa[i];
                Configuration bb = bbb[i];
                if( !compare( aa, bb ) )
                {
                    System.out.println( "CONFIG " + aa.getName() + " != " + bb.getName() );
                    System.out.println( ConfigurationUtil.list( aa ) );
                    System.out.println( ConfigurationUtil.list( bb ) );
                    return false;
                }
            }
            return false;    
        }
    }

    public void testSerialEqualsXML() throws Exception
    {
        ContainmentProfile xml = readFromXML( m_xml );
        ContainmentProfile serial = readFromSerial( m_serial );
        //print( xml );
        assertTrue( true );
    }

    private void print( ContainmentProfile profile ) throws Exception
    {
        File temp = new File( getTestDir(), "test-classes/test.xml" );
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        Configuration config = builder.buildFromFile( temp );
        System.out.println( ConfigurationUtil.list( config ) );
    }

    private static File getTestDir()
    {
        return new File( System.getProperty( "basedir" ), "target" );
    }

    private ContainmentProfile readFromXML( File file )
      throws Exception
    {
        XMLContainmentProfileCreator creator = new XMLContainmentProfileCreator();
        return creator.createContainmentProfile( loadConfiguration( file ) );
    }

    private Configuration loadConfiguration( File file )
      throws Exception
    {
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        return builder.buildFromFile( file );
    }


    private ContainmentProfile readFromSerial( File file )
      throws Exception
    {
        SerializedContainmentProfileCreator creator = 
          new SerializedContainmentProfileCreator();
        FileInputStream input = new FileInputStream( file );
        return creator.createContainmentProfile( input );
    }

    private void writeToXML( ContainmentProfile profile, File file )
      throws Exception
    {
        XMLContainmentProfileWriter writer = 
          new XMLContainmentProfileWriter();
        FileOutputStream output = new FileOutputStream( file );
        writer.writeContainmentProfile( profile, output );
    }

    private void writeToSerial( ContainmentProfile profile, File file )
      throws Exception
    {
        SerializedContainmentProfileWriter writer = 
          new SerializedContainmentProfileWriter();
        FileOutputStream output = new FileOutputStream( file );
        writer.writeContainmentProfile( profile, output );
    }
}
