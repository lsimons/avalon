

package org.apache.avalon.composition.model.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

import org.apache.avalon.composition.model.Model;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.DependencyModel;
import org.apache.avalon.composition.model.AbstractTestCase;
import org.apache.avalon.composition.util.ExceptionHelper;
import org.apache.avalon.meta.info.DependencyDescriptor;

public class IncludesTestCase extends AbstractTestCase
{      
   //-------------------------------------------------------
   // constructor
   //-------------------------------------------------------

    public IncludesTestCase()
    {
        super( "includes.xml" );
    }

   //-------------------------------------------------------
   // tests
   //-------------------------------------------------------

   /**
    * Validate the the included block was created.
    */
    public void testInclusion() throws Throwable
    {
        try
        {
            ContainmentModel test = (ContainmentModel) m_model.getModel( "test" );
            assertTrue( test != null );
        }
        catch( Throwable e )
        {
            final String msg = 
              ExceptionHelper.packException( "Huston, we have a problem", e, true );
            System.err.println( msg );
            throw e;
        }
    }
}
