package org.apache.avalon.merlin.env;
import junit.framework.TestCase ;


/**
 * Env tests.
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $Author: mcconnell $
 * @version $Revision: 1.1 $
 */
public class EnvTest extends TestCase
{
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(EnvTest.class);
    }

    
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    
    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }
    

    /**
     * Constructor for EnvTest.
     * @param arg0
     */
    public EnvTest(String arg0)
    {
        super(arg0);
    }

    
    final public void testGetProperty()
        throws Exception
    {
        assertNull( Env.getVariable( "PAT" ) ) ;
        assertNotNull( Env.getVariable( "PATH" ) ) ;
        
        EnvAccessException l_error = null ;
        try 
        {
            Env.getVariable( "--*&^%^%$" ) ;
        }
        catch( EnvAccessException e )
        {
            l_error = e ;
        }
        
        assertNotNull( l_error ) ;
    }

}
