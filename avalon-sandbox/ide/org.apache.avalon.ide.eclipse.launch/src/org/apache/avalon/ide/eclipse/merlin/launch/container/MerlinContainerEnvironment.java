/*
 * Created on 14.02.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.avalon.ide.eclipse.merlin.launch.container;

import java.io.File;
import java.util.Map;

import org.apache.avalon.merlin.cli.Main;
import org.apache.avalon.merlin.impl.DefaultCriteria;
import org.apache.avalon.repository.Artifact;
import org.apache.avalon.repository.main.DefaultInitialContext;
import org.apache.avalon.repository.provider.Factory;
import org.apache.avalon.repository.provider.InitialContext;
import org.apache.avalon.util.env.Env;
import org.apache.avalon.util.exception.ExceptionHelper;

/**
 * @author Andreas Develop
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class MerlinContainerEnvironment
{

    public static void main(String[] args)
    {

        try
        {
            // must be the dir, where merlin is launched
            File dir = getBaseDirectory();
            File system = new File( getMerlinHome( ), "system" );
            
            ClassLoader parent = Main.class.getClassLoader();
            Artifact impl = null; // default
            String[] bootstrap = null; // default
            
            InitialContext context = 
            new DefaultInitialContext( 
                    dir, parent, impl, system, bootstrap );
            
            Factory factory = context.getInitialFactory();
            
            // getting the proxy settings for Repository access
            Map repCriteria = factory.createDefaultCriteria();
            
            // getting all other settings
            Map criteria = new DefaultCriteria(context);
            Object obj = factory.create(repCriteria); 
            Object o1 = obj;
            
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }

    /**
     * Return the functional base directory.  The implementation looks
     * for the ${merlin.dir} system property and if not found, looks for 
     * the ${basedir} system property, and as a last resort, returns the 
     * JVM ${user.dir} value.
     *
     * @return the merlin install directory
     */
    private static File getBaseDirectory()
    {
        final String merlin = System.getProperty( "merlin.dir" );
        if( null != merlin )
           {
            return new File( merlin );
        }
        final String base = System.getProperty( "basedir" );
        if( null != base )
           {
            return new File( base );
        }
        return new File( System.getProperty( "user.dir" ) );
    }

    /**
     * Return the merlin home directory.
     * @return the merlin install directory
     */
    private static File getMerlinHome()
    {
        return new File( getMerlinHomePath() );
    }

    /**
     * Return the merlin home directory path.
     * @return the merlin install directory path
     */
    private static String getMerlinHomePath()
    {
        try
        {
            String merlin = 
            System.getProperty( 
                    "merlin.home", 
                    Env.getEnvVariable( "MERLIN_HOME" ) );
            if( null != merlin ) return merlin;
            return System.getProperty( "user.home" ) 
            + File.separator + ".merlin";
        }
        catch( Throwable e )
        {
            final String error = 
            "Internal error while attempting to access MERLIN_HOME environment.";
            final String message = 
            ExceptionHelper.packException( error, e, true );
            throw new RuntimeException( message );
        }
    }
    
}
