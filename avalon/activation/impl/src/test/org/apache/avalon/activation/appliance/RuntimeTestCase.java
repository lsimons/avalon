

package org.apache.avalon.activation.appliance;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.avalon.activation.appliance.*;
import org.apache.avalon.activation.appliance.impl.*;
import org.apache.avalon.composition.logging.LoggingManager;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.SystemContext;
import org.apache.avalon.composition.model.impl.DefaultModelFactory;
import org.apache.avalon.composition.model.impl.DefaultSystemContext;
import org.apache.avalon.composition.data.ServiceDirective;
import org.apache.avalon.composition.data.ContainmentProfile;
import org.apache.avalon.composition.data.ClassLoaderDirective;
import org.apache.avalon.composition.util.ExceptionHelper;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.meta.info.DependencyDescriptor;
import org.apache.avalon.meta.info.ServiceDescriptor;
import org.apache.excalibur.mpool.PoolManager;

import junit.framework.TestCase;

public class RuntimeTestCase extends AbstractTestCase
{
   //-------------------------------------------------------
   // constructor
   //-------------------------------------------------------

    public RuntimeTestCase( )
    {
        this( "model" );
    }

    public RuntimeTestCase( String name )
    {
        super( name );
    }

   //-------------------------------------------------------
   // setup
   //-------------------------------------------------------

   public String getPath()
   {
      return "block.xml";
   }

   //-------------------------------------------------------
   // tests
   //-------------------------------------------------------

   /**
    * Validate the composition model.
    */
    public void testDeploymentCycle() throws Exception
    {
        try
        {
            executeDeploymentCycle();
        }
        catch( Throwable e )
        {
            final String error = "Test failure.";
            final String message = ExceptionHelper.packException( error, e, true );
            getLogger().error( message );
            throw new Exception( message );
        }
    }

   /**
    * Validate the composition model.
    */
    public void executeDeploymentCycle() throws Exception
    {

        //
        // 1. create the root block using the service context
        //    and the root containment model
        //

        getLogger().debug( "creating root block" );
        Block block = DefaultBlock.createRootBlock( m_context, m_model );
        getLogger().debug( "block: " + block );

        //
        // 2. assemble the block during which all dependencies
        //    are resolved (deployment and runtime)
        //

        if( block instanceof Composite )
        {
            ((Composite)block).assemble();
        }

        //
        // 3. deploy the block during which any 'activate on startup'
        //    components are created which in turn my cause activation
        //    of lazy components
        //

        block.deploy();

        //
        // 4-5. suspend and resume the root block (not implemented yet)
        //
        // 6. decommission the block during which all managed appliances
        //    are decommissioned resulting in the decommissioning of all
        //    appliance instances
        //

        block.decommission();

        //
        // 7. disassemble the block during which reference between 
        //    appliances established at assembly time are discarded
        //

        if( block instanceof Composite )
        {
            ((Composite)block).disassemble();
        }

        //
        // 8. dispose of the appliance during which all subsidiary 
        //    appliances are disposed of in an orderly fashion
        //

        if( block instanceof Disposable )
        {
            ((Disposable)block).dispose();
        }

        assertTrue( true );
    }
}
