package org.apache.avalon.util.exception;
import junit.framework.TestCase ;


/**
 * ExceptionHelper tests.
 * 
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.1 $
 */
public class ExceptionHelperTest extends TestCase
{    
    /**
     * Constructor for ExceptionHelperTest.
     * @param name
     */
    public ExceptionHelperTest( String name )
    {
        super( name );
    }

    final public void testCompositeExceptionReport()
        throws Exception
    {
        Throwable e1 = null;
        Throwable e2 = null;
        try
        {
            doSomething();
        }
        catch( Throwable e )
        {
            e1 = e;
        }
        try
        {
            doSomething();
        }
        catch( Throwable e )
        {
            e2 = e;
        }
        final String message = 
           ExceptionHelper.packException( 
             "Composite exception report", 
             new Throwable[]{ e1, e2 }, false );
        assertNotNull( message );
        System.out.println( message );
    }

    final public void testExceptionWithMessageReport()
        throws Exception
    {
        try
        {
            doSomething();
        }
        catch( Throwable e )
        {
            final String message = 
              ExceptionHelper.packException( "An error occured.", e );
            assertNotNull( message );
        }
    }

    final public void testExceptionWithStackTrace()
        throws Exception
    {
        try
        {
            doSomething();
        }
        catch( Throwable e )
        {
            final String message = 
              ExceptionHelper.packException( e, true );
            assertNotNull( message );
        }
    }

    final public void testExceptionWithMessageAndStackTrace()
        throws Exception
    {
        try
        {
            doSomething();
        }
        catch( Throwable e )
        {
            final String message = 
              ExceptionHelper.packException( "An error occured.", e, true );
            assertNotNull( message );
        }
    }

    private void doSomething() throws StandardException
    {
        try
        {
            doSomethingElse();
        }
        catch( Throwable e )
        {
            final String error =
              "Unable to do something due to a error condition.";
            throw new StandardException( error, e );
        }
    }

    private void doSomethingElse()
    {
        try
        {
            causeSomeError();
        }
        catch( Throwable e )
        {
            final String error =
              "Unable to do something else due to a error condition.";
            throw new StandardRuntimeException( error, e );
        }
    }

    private void causeSomeError()
    {
        final String error = 
          "Raising exception because that's what I'm programmed to do.";
        throw new StandardError( error );
    }    
}
