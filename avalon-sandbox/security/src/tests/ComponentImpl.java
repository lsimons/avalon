
import java.security.*;

public class ComponentImpl
    implements Component
{
    public void doProtectedMethod()
    {
        Permission perm = new RuntimePermission( "doProtected" );
        AccessController.checkPermission( perm );
        
        System.out.println( "Phase 1 OK.      <--  Custom Permission in Component passed." );
        
        try
        {
            String s = System.getProperty( "java.home" );
            System.out.println( "JavaHome=" + s );
            System.out.println( "Phase 2 failed." );
        } catch( SecurityException e )
        {
            System.out.println( "Phase 2 OK.      <--  Component reads a non-allowed property, and is denied" );
        }
    }
    
} 
 
