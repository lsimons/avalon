
import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;

public class InnerContainerImpl
    implements InnerContainer
{
    private Component m_Component;
    private AccessControlContext m_AccContext;
    
    public InnerContainerImpl()
        throws Exception
    {
        ClassLoader parentCL = getClass().getClassLoader();
        File f = new File( System.getProperty( "user.dir" ), "component.jar" );
        CodeSource cs = new CodeSource( f.toURL(), null );        
        
        Permissions p = new Permissions();
        
        Permission perm = new RuntimePermission( "doProtected" );
        p.add( perm );
        
        ProtectionDomain pd = new ProtectionDomain( cs, p );
        ProtectionDomain[] domains = new ProtectionDomain[] { pd };
        
        m_AccContext = new AccessControlContext( domains );    
        
        m_Component = (Component) Main.instantiate( f, "ComponentImpl", parentCL );
    }
    
    public void doThatMethod() throws Exception
    {
        String s = System.getProperty( "java.home" );
        System.out.println( "Phase 0 OK.      <--  Container reads an allowed Property, and passed." );
        AccessController.doPrivileged( new PrivilegedExceptionAction()
        {
            public Object run() throws Exception
            {
                m_Component.doProtectedMethod();
                return null;
            }
        }, m_AccContext );
        
        try
        {
            s = System.getProperty( "java.vendor" );
            System.out.println( "Java Vendor=" + s );
        } catch( SecurityException e )
        {
            System.out.println( "Phase 3 OK.      <--  Container reads a non-allowed Property, and is denied." );
        }
    }
}
