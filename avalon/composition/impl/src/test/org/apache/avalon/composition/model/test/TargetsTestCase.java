

package org.apache.avalon.composition.model.test;

import java.io.File;

import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.ComponentModel;
import org.apache.avalon.composition.model.AbstractTestCase;
import org.apache.avalon.composition.data.Targets;
import org.apache.avalon.composition.data.TargetDirective;
import org.apache.avalon.composition.util.ExceptionHelper;
import org.apache.avalon.composition.data.builder.XMLTargetsCreator;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.excalibur.configuration.ConfigurationUtil;

public class TargetsTestCase extends AbstractTestCase
{      
   //-------------------------------------------------------
   // constructor
   //-------------------------------------------------------

    public TargetsTestCase()
    {
        super( "targets.xml" );
    }

   //-------------------------------------------------------
   // tests
   //-------------------------------------------------------

   /**
    * Validate the the included block was created.
    */
    public void testTargetsApplication() throws Exception
    {
        try
        {
            File source = 
              new File( getTestDir(), "/test-classes/conf/targets-config.xml" );
            TargetDirective[] targets = loadTargets( source ).getTargets();
            for( int i=0; i<targets.length; i++ )
            {
                TargetDirective target = targets[i];
                final String path = target.getPath();
                DeploymentModel model = m_model.getModel( path );
                if( model instanceof ComponentModel )
                {
                    ComponentModel deployment = (ComponentModel) model;
                    deployment.setConfiguration( target.getConfiguration() );
                    getLogger().debug( "model: " + deployment );
                    getLogger().debug( 
                      ConfigurationUtil.list( deployment.getConfiguration() ) );
                }
                else
                {
                    final String warning = 
                      "Cannot apply target: " + path + " to a containment model.";
                    getLogger().warn( warning );
                }
            }
        }
        catch( Throwable e )
        {
            final String error = "Test failure.";
            final String message = ExceptionHelper.packException( error, e, true );
            getLogger().error( message );
            throw new Exception( message );
        }
    }

    private Targets loadTargets( File file )
      throws Exception
    {
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        Configuration config = builder.buildFromFile( file );
        XMLTargetsCreator creator = new XMLTargetsCreator();
        return creator.createTargets( config );

    }

}
