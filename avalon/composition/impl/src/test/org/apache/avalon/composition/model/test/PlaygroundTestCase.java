

package org.apache.avalon.composition.model.test;

import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.ComponentModel;
import org.apache.avalon.composition.model.DependencyModel;
import org.apache.avalon.composition.model.AbstractTestCase;
import org.apache.avalon.util.exception.ExceptionHelper;

public class PlaygroundTestCase extends AbstractTestCase
{      
   //-------------------------------------------------------
   // constructor
   //-------------------------------------------------------

    public PlaygroundTestCase()
    {
        super( "playground.xml" );
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
            final String message = "Playground test failure";
            final String error = ExceptionHelper.packException( message, e, true );
            System.err.println( error );
            fail( error );
        }
    }
}
