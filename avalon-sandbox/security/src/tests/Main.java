
import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;

public class Main
{
    static public void main( String[] args )
        throws Exception
    {
        System.out.println( "Starting." );
        
        ClassLoader parentCL = Main.class.getClassLoader();
        File f = new File( System.getProperty( "user.dir" ), "outer.jar" );
        OuterContainer container = (OuterContainer) instantiate( f, "OuterContainerImpl", parentCL );
        container.doThisMethod();
        
        System.out.println( "Finishing." );
    }
    
    static public Object instantiate( File jarFile, String classname, ClassLoader parent )
        throws Exception
    {
        URL url = jarFile.toURL();
        URL[] urls = new URL[] { url };
        URLClassLoader cl = new URLClassLoader( urls, parent );
        Class cls = cl.loadClass( classname );
        return cls.newInstance();
    }    
}
