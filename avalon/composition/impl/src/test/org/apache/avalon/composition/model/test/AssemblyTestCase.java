

package org.apache.avalon.composition.model.test;

import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.ComponentModel;
import org.apache.avalon.composition.model.DependencyModel;
import org.apache.avalon.composition.model.AbstractTestCase;
import org.apache.avalon.util.exception.ExceptionHelper;

public class AssemblyTestCase extends AbstractTestCase
{      
   //-------------------------------------------------------
   // constructor
   //-------------------------------------------------------

    public AssemblyTestCase()
    {
        super( "dependency.xml" );
    }

   //-------------------------------------------------------
   // tests
   //-------------------------------------------------------

   /**
    * Validate the composition model.
    */
    public void testAssembly() throws Exception
    {
        try
        {
            m_model.assemble();
            System.out.println( "" );
            printModel( "", m_model );
        }
        catch( Throwable e )
        {
            final String message = "Assembly test failure";
            final String error = ExceptionHelper.packException( message, e, true );
            System.err.println( error );
            fail( error );
        }
    }

    private void printModel( String lead, DeploymentModel model )
    {
        if( model instanceof ContainmentModel )
        {
            printContainmentModel( lead, (ContainmentModel) model );
        }
        else if( model instanceof ComponentModel ) 
        {
            printComponentModel( lead, (ComponentModel) model );
        }
    }

    private void printContainmentModel( String lead, ContainmentModel model )
    {
        System.out.println( lead + "model:" + model );
        printCompositeModel( "\t" + lead, model );
        DeploymentModel[] models = model.getModels();
        if( models.length > 0 )
        {
            System.out.println( lead + "\tchildren:" );
            for( int i=0; i<models.length; i++ )
            {
                DeploymentModel m = models[i];
                printModel( "\t\t" + lead, m );
            }
        }
        models = model.getStartupGraph();
        if( models.length > 0 )
        {
            System.out.println( lead + "\tstartup:" );
            for( int i=0; i<models.length; i++ )
            {
                DeploymentModel m = models[i];
                System.out.println( "\t\t" + lead + (i+1) + ": " + m );
            }
        }
        models = ((ContainmentModel)model).getShutdownGraph();
        if( models.length > 0 )
        {
            System.out.println( lead + "\tshutdown:" );
            for( int i=0; i<models.length; i++ )
            {
                DeploymentModel m = models[i];
                System.out.println( "\t\t" + lead + (i+1) + ": " + m );
            }
        }
    }

    private void printComponentModel( String lead, ComponentModel model )
    {
        System.out.println( lead + "model:" + model );
        printCompositeModel( lead, model );
    }

    private void printCompositeModel( String lead, DeploymentModel model )
    {
        DeploymentModel[] models = model.getProviderGraph();
        for( int i=0; i<models.length; i++ )
        {
            DeploymentModel m = models[i];
            System.out.println( "\t" + lead + (i+1) + ": " + m + " (provider)" );
        }
        models = model.getConsumerGraph();
        for( int i=0; i<models.length; i++ )
        {
            DeploymentModel m = models[i];
            System.out.println( "\t" + lead + (i+1) + ": " + m + " (consumer)" );
        }
    }
}
