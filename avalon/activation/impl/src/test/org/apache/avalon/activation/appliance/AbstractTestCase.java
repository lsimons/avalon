

package org.apache.avalon.activation.appliance;

import java.io.File;

import junit.framework.TestCase;

import org.apache.avalon.composition.data.ContainmentProfile;
import org.apache.avalon.composition.data.builder.XMLContainmentProfileCreator;
import org.apache.avalon.composition.logging.LoggingManager;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.SystemContext;
import org.apache.avalon.composition.model.impl.DefaultSystemContext;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;


public abstract class AbstractTestCase extends TestCase
{
   //-------------------------------------------------------
   // state
   //-------------------------------------------------------

    protected Logger m_logger;

    protected ContainmentModel m_model;

    protected SystemContext m_system;

   //-------------------------------------------------------
   // constructor
   //-------------------------------------------------------

    public AbstractTestCase( )
    {
        this( "data" );
    }

    public AbstractTestCase( String name )
    {
        super( name );
    }

   //-------------------------------------------------------
   // setup
   //-------------------------------------------------------

    public abstract String getPath();

   /**
    * The setup process covers the establishment of the base
    * directory (from which relative references for extension directories
    * and fileset base directories are resolved), a file repository (not
    * used in this test case at this time), and a class loader model from 
    * which a classpath will be established.
    *
    * @exception Exception if things don't work out
    */
    public void setUp() throws Exception
    {
        File base = new File( getTestDir(), "test-classes" );

        //
        // WARNING: ALMOST EVIL
        // Next couple of lines are using a convinience operation
        // on DefaultSystemContext to create the system context.  This 
        // is temporary and will be replaced when a clean configurable 
        // system context factory is in place
        //

        File local = new File( base, "repository" );
        m_system = 
          DefaultSystemContext.createSystemContext( 
            base, local, ConsoleLogger.LEVEL_DEBUG );
        m_logger = m_system.getLogger();

        //
        // load the meta data using the profile returned from getPath()
        // and establish a containment model for the unit test
        //

        ContainmentProfile profile = setUpProfile( new File( base, getPath() ) );
        m_model = m_system.getFactory().createContainmentModel( profile );
    }

    protected ContainmentProfile setUpProfile( File file )
      throws Exception
    {
        XMLContainmentProfileCreator creator = new XMLContainmentProfileCreator();
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        Configuration config = builder.buildFromFile( file );
        return creator.createContainmentProfile( config );
    }

    protected static File getTestDir()
    {
        return new File( System.getProperty( "basedir" ), "target" );
    }

    protected Logger getLogger()
    {
        return m_logger;
    }

}
