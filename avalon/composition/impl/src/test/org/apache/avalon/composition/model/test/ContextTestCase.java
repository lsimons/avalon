

package org.apache.avalon.composition.model.test;

import java.io.File;
import java.util.Date;

import org.apache.avalon.composition.model.ComponentModel;
import org.apache.avalon.composition.model.ContextModel;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;

public class ContextTestCase extends AbstractTestCase
{
   private static final String FACADE_CLASSNAME =
     "org.apache.avalon.composition.model.testa.DefaultFacade";
      
   //-------------------------------------------------------
   // constructor
   //-------------------------------------------------------

    public ContextTestCase( )
    {
        super( "context.xml" );
    }

   //-------------------------------------------------------
   // tests
   //-------------------------------------------------------

   /**
    * Validate the composition model.
    */
    public void testStandardContextModel() throws Exception
    {
        ComponentModel model = (ComponentModel) m_model.getModel( "test-a" );
        if( model == null )
        {
            fail( "null deployment model" );
        }

        ContextModel contextModel = model.getContextModel();
        if( contextModel == null )
        {
            fail( "null context model" );
        }

        Context context = contextModel.getContext();
        if( context == null )
        {
            fail( "null context" );
        }

        try
        {
            ClassLoader loader = (ClassLoader) context.get( "urn:avalon:classloader" );
        }
        catch( ContextException e )
        {
            assertTrue( "urn:avalon:classloader", false );
        }
        
        try
        {
            File home = (File) context.get( "urn:avalon:home" );
        }
        catch( ContextException e )
        {
            assertTrue( "urn:avalon:home", false );
        }

        try
        {
            File temp = (File) context.get( "urn:avalon:temp" );
        }
        catch( ContextException e )
        {
            assertTrue( "urn:avalon:temp", false );
        }

        try
        {
            String partition = (String) context.get( "urn:avalon:partition" );
        }
        catch( ContextException e )
        {
            assertTrue( "urn:avalon:partition", false );
        }

        //
        // validate context entry lookup using an alias
        //

        try
        {
            String name = (String) context.get( "name" );
        }
        catch( ContextException e )
        {
            assertTrue( "name", false );
        }

        //
        // validate volatile entries
        //

        Date date = null;
        try
        {
            date = (Date) context.get( "time" );
            try
            {
                Thread.sleep( 1200 );
            }
            catch( Throwable e )
            {
                // continue
            }
        }
        catch( ContextException e )
        {
            assertTrue( "date", false );
        }

        try
        {
            Date now = (Date) context.get( "time" );
            assertTrue( "volatile", now.after( date ) );
        }
        catch( ContextException e )
        {
            assertTrue( "now", false );
        }

        //
        // validate an imported context entry
        //

        try
        {
            String path = (String) context.get( "path" );
        }
        catch( ContextException e )
        {
            assertTrue( "path", false );
        }

        //
        // validate context safe-casting
        // (e.g. ((MyContext)m_context).myMethod() type of thing)
        //

        final String className =  context.getClass().getName();
        boolean classNameMatches = className.equals( FACADE_CLASSNAME );
        assertTrue( "custom context", classNameMatches );
    }
}
