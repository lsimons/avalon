

package org.apache.avalon.composition.model.test;

import org.apache.avalon.composition.model.Model;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.DeploymentModel;
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
            System.out.println( "\tstartup graph ");
            printStartupGraph( "\t", m_model );
            System.out.println( "\tshutdown graph ");
            printShutdownGraph( "\t", m_model );
        }
        catch( Throwable e )
        {
            final String message = "Assembly test failure";
            final String error = ExceptionHelper.packException( message, e, true );
            System.err.println( error );
            fail( error );
        }
    }

    private void printStartupGraph( String lead, Model model )
    {
        if( model instanceof ContainmentModel )
        {
            Model[] models = ((ContainmentModel)model).getStartupGraph();
            printStartupGraph( lead, models );
        }
    }
    private void printStartupGraph( String lead, Model[] models )
    {
        for( int i=0; i<models.length; i++ )
        {
            Model model = models[i];
            System.out.println( lead + (i+1) + ": " + model );
            if( model instanceof ContainmentModel )
            {
                printStartupGraph( lead + "\t", (ContainmentModel) model );
            }
        }
    }

    private void printShutdownGraph( String lead, Model model )
    {
        if( model instanceof ContainmentModel )
        {
            Model[] models = ((ContainmentModel)model).getShutdownGraph();
            printShutdownGraph( lead, models );
        }
    }
    private void printShutdownGraph( String lead, Model[] models )
    {
        for( int i=0; i<models.length; i++ )
        {
            Model model = models[i];
            System.out.println( lead + (i+1) + ": " + model );
            if( model instanceof ContainmentModel )
            {
                printShutdownGraph( lead + "\t", (ContainmentModel) model );
            }
        }
    }

}
