package org.apache.avalon.util.env;
import junit.framework.TestCase ;


/**
 * Env tests.
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $Author: akarasulu $
 * @version $Revision: 1.2 $
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

    
    final public void testEnvVariable()
        throws Exception
    {
        if ( Env.isUnix() )
        {    
            assertNotNull( Env.getEnvVariable( "PATH" ) ) ;
        }
        
        if ( Env.isWindows() )
        {
            assertNotNull( Env.getEnvVariable( "Path" ) ) ;
        }
        
        assertNull( Env.getEnvVariable( "PAT" ) ) ;
    }

    
    public void testEnv()
        throws EnvAccessException
    {
        Env l_env = new Env() ;
        assertNull( l_env.getProperty( "PAT" ) ) ;
        
        if ( Env.isUnix() )
        {    
            assertNotNull( l_env.getProperty( "PATH" ) ) ;
        }
        
        if ( Env.isWindows() )
        {
            assertNotNull( l_env.getProperty( "PROMPT" ) ) ;
        }
    }
}
