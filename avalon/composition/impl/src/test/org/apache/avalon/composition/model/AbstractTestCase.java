

package org.apache.avalon.composition.model;

import java.io.File;

import org.apache.avalon.composition.model.impl.DefaultSystemContext;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.ConsoleLogger;

import junit.framework.TestCase;

public abstract class AbstractTestCase extends TestCase
{
    public int PRIORITY = ConsoleLogger.LEVEL_WARN;

   //-------------------------------------------------------
   // state
   //-------------------------------------------------------

    public ContainmentModel m_model;

    private Logger m_logger = new ConsoleLogger( PRIORITY );

    private String m_path;

   //-------------------------------------------------------
   // constructor
   //-------------------------------------------------------

   public AbstractTestCase( String path )
   {
       super( path );
       m_path = path;
   }

   //-------------------------------------------------------
   // constructor
   //-------------------------------------------------------

    protected Logger getLogger()
    {
        return m_logger;
    }

    protected File getTestDir()
    {
        return new File( System.getProperty( "basedir" ), "target" );
    }

    public void setUp() throws Exception
    {
        if( m_model == null )
        {
            File base = new File( getTestDir(), "test-classes" );
            File repository = new File( base, "repository" );

            File confDir = new File( base, "conf" );
            File source = new File( confDir, m_path );

            SystemContext system = 
              DefaultSystemContext.createSystemContext( base, repository, PRIORITY );
            m_model = system.getFactory().createContainmentModel( source.toURL() );
        }
    }
}
