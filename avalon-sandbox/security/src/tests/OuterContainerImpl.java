

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;


public class OuterContainerImpl
    implements OuterContainer
{
    private InnerContainer m_Inner;
    private AccessControlContext m_AccContext;
    
    public OuterContainerImpl()
        throws Exception
    {
        ClassLoader parentCL = getClass().getClassLoader();
        File f = new File( System.getProperty( "user.dir" ), "inner.jar" );
        
        CodeSource cs = new CodeSource( f.toURL(), null );        
        
        Permissions p = new Permissions();
        
        Permission perm = new RuntimePermission( "doProtected" );
        p.add( perm );
        
        perm = new PropertyPermission( "java.home", "read" );
        p.add( perm );
        
        ProtectionDomain pd = new ProtectionDomain( cs, p );
        ProtectionDomain[] domains = new ProtectionDomain[] { pd };
        m_AccContext = new AccessControlContext( domains );    
        
        m_Inner = (InnerContainer) Main.instantiate( f, "InnerContainerImpl", parentCL );
    }
    
    public void doThisMethod() throws Exception
    {
        AccessController.doPrivileged( new PrivilegedExceptionAction()
        {
            public Object run() throws Exception
            {
                m_Inner.doThatMethod();
                return null;
            }
        }, m_AccContext );
    }
}
