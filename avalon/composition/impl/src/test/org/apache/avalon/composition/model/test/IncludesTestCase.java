

package org.apache.avalon.composition.model.test;

import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.AbstractTestCase;

import org.apache.avalon.util.exception.ExceptionHelper;

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
