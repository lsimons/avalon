/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.launcher;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * PhoenixLoader is the class that bootstraps and sets up engine ClassLoader.
 * It also sets up a default policy that gives full permissions to engine code.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public final class Main
{
    private final static String MAIN_CLASS = "org.apache.avalon.phoenix.frontends.CLIMain";
    private final static String MAIN_JAR = "phoenix-engine.jar";
    private final static String LOADER_JAR = "phoenix-loader.jar";

    private static Object c_frontend;

    ///The code to return to system using exit code
    private static int c_exitCode;

    ///The code to return to system using exit code
    private static boolean c_blocking;

    /**
     * Main entry point for Phoenix.
     *
     * @param args the command line arguments
     * @exception Exception if an error occurs
     */
    public final static void main( final String[] args )
        throws Exception
    {
        c_blocking = true;
        startup( args, new Hashtable() );
        System.exit( c_exitCode );
    }

    /**
     * Method to call to startup Phoenix from an 
     * external (calling) application. Protected to allow
     * access from DaemonLauncher.
     *
     * @param args the command line arg array
     * @param data a set of extra parameters to pass to embeddor
     * @exception Exception if an error occurs
     */
    protected final static void startup( final String[] args, final Hashtable data )
        throws Exception
    {
        try
        {
            //setup new Policy manager
            Policy.setPolicy( new FreeNEasyPolicy() );

            //Create engine ClassLoader
            final File mainJar = findEngineJar();
            final URL archive = mainJar.toURL();
            final URLClassLoader classLoader = new URLClassLoader( new URL[]{ archive } );

            //Setup context classloader
            Thread.currentThread().setContextClassLoader( classLoader );

            //Create main launcher
            final Class clazz = classLoader.loadClass( MAIN_CLASS );
            final Class[] paramTypes = 
                new Class[] { args.getClass(), Hashtable.class, Boolean.TYPE };
            final Method method = clazz.getMethod( "main", paramTypes );
            c_frontend = clazz.newInstance();
            
            //kick the tires and light the fires....
            final Integer integer = 
                (Integer)method.invoke( c_frontend, new Object[] { args, data, new Boolean( c_blocking ) } );
            c_exitCode = integer.intValue();
        }
        catch( final Exception e )
        {
            e.printStackTrace();
        }
    }

    /**
     * Method to call to shutdown Phoenix from an 
     * external (calling) application. Protected to allow
     * access from DaemonLauncher.
     */
    protected final static void shutdown()
    {
        if( null == c_frontend ) return;
        try
        {
            final Class clazz = c_frontend.getClass();
            final Method method = clazz.getMethod( "shutdown", new Class[ 0 ] );

            //Lets put this sucker to sleep
            method.invoke( c_frontend, new Object[ 0 ] );
        }
        catch( final Exception e )
        {
            e.printStackTrace();
        }
        finally
        {
            c_frontend = null;
        }
    }

    /**
     * Find the "engine" jar from which to run main phoenix kernel.
     *
     * @return the engine file
     * @exception Exception if an error occurs
     */
    private final static File findEngineJar()
        throws Exception
    {
        String phoenixHome = System.getProperty( "phoenix.home", null );

        if( null == phoenixHome )
        {
            final File loaderDir = findLoaderDir();
            phoenixHome = loaderDir.getAbsoluteFile().getParentFile() + File.separator;
        }

        phoenixHome = (new File( phoenixHome )).getCanonicalFile().toString();
        System.setProperty( "phoenix.home", phoenixHome );

        final String filename =
            phoenixHome + File.separator + "bin" + File.separator + MAIN_JAR;
        return (new File( filename )).getCanonicalFile();
    }

    /**
     *  Finds the LOADER_JAR file in the classpath.
     */
    private final static File findLoaderDir()
        throws Exception
    {
        final String classpath = System.getProperty( "java.class.path" );
        final String pathSeparator = System.getProperty( "path.separator" );
        final StringTokenizer tokenizer = new StringTokenizer( classpath, pathSeparator );

        while( tokenizer.hasMoreTokens() )
        {
            final String element = tokenizer.nextToken();

            if( element.endsWith( LOADER_JAR ) )
            {
                File file = ( new File( element ) ).getCanonicalFile();
                file = file.getParentFile();
                return file;
            }
        }

        throw new Exception( "Unable to locate " + LOADER_JAR + " in classpath" );
    }

    /**
     * Default polic class to give every code base all permssions.
     * Will be replaced once the kernel loads.
     */
    private static class FreeNEasyPolicy
        extends Policy
    {
        public PermissionCollection getPermissions( final CodeSource codeSource )
        {
            final Permissions permissions = new Permissions();
            permissions.add( new java.security.AllPermission() );
            return permissions;
        }

        public void refresh()
        {
        }
    }
}
