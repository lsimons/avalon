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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * PhoenixLoader is the class that bootstraps and sets up engine ClassLoader.
 * It also sets up a default policy that gives full permissions to engine code.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public final class Main
{
    private static final String MAIN_CLASS = "org.apache.avalon.phoenix.frontends.CLIMain";

    private static final String LOADER_JAR = "phoenix-loader.jar";

    private static Object c_frontend;

    /**
     * Main entry point for Phoenix.
     *
     * @param args the command line arguments
     * @throws Exception if an error occurs
     */
    public static final void main( final String[] args )
        throws Exception
    {
        int exitCode = startup( args, new Hashtable(), true );
        System.exit( exitCode );
    }

    /**
     * Method to call to startup Phoenix from an
     * external (calling) application. Protected to allow
     * access from DaemonLauncher.
     *
     * @param args the command line arg array
     * @param data a set of extra parameters to pass to embeddor
     * @param blocking false if the current thread is expected to return.
     *
     * @return the exit code which should be used to exit the JVM
     *
     * @throws Exception if an error occurs
     */
    protected static final int startup( final String[] args,
                                        final Hashtable data,
                                        final boolean blocking )
        throws Exception
    {
        int exitCode;
        try
        {
            //setup new Policy manager
            Policy.setPolicy( new FreeNEasyPolicy() );

            //Create engine ClassLoader
            final URL[] urls = getEngineClassPath();
            final URLClassLoader classLoader = new URLClassLoader( urls );

            data.put( "common.classloader", ClassLoader.getSystemClassLoader() );
            data.put( "container.classloader", classLoader );
            data.put( "phoenix.home", new File( findPhoenixHome() ) );

            //Setup context classloader
            Thread.currentThread().setContextClassLoader( classLoader );

            //Create main launcher
            final Class clazz = classLoader.loadClass( MAIN_CLASS );
            final Class[] paramTypes =
                new Class[]{args.getClass(), Hashtable.class, Boolean.TYPE};
            final Method method = clazz.getMethod( "main", paramTypes );
            c_frontend = clazz.newInstance();

            //kick the tires and light the fires....
            final Integer integer = (Integer)method.invoke(
                c_frontend, new Object[]{args, data, new Boolean( blocking )} );
            exitCode = integer.intValue();
        }
        catch( final Exception e )
        {
            e.printStackTrace();
            exitCode = 1;
        }
        return exitCode;
    }

    /**
     * Method to call to shutdown Phoenix from an
     * external (calling) application. Protected to allow
     * access from DaemonLauncher.
     */
    protected static final void shutdown()
    {
        if( null == c_frontend )
        {
            return;
        }

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
     * Create a ClassPath for the engine.
     *
     * @return the set of URLs that engine uses to load
     * @throws Exception if unable to aquire classpath
     */
    private static URL[] getEngineClassPath()
        throws Exception
    {
        final ArrayList urls = new ArrayList();

        final File dir = findEngineLibDir();
        final File[] files = dir.listFiles();
        for( int i = 0; i < files.length; i++ )
        {
            final File file = files[ i ];
            if( file.getName().endsWith( ".jar" ) )
            {
                urls.add( file.toURL() );
            }
        }

        return (URL[])urls.toArray( new URL[ urls.size() ] );
    }

    /**
     * Find directory to load engine specific libraries from.
     *
     * @return the lib dir
     * @throws Exception if unable to aquire directory
     */
    private static File findEngineLibDir()
        throws Exception
    {
        final String phoenixHome = findPhoenixHome();
        final String engineLibDir =
            phoenixHome + File.separator + "bin" + File.separator + "lib";
        final File dir = new File( engineLibDir ).getCanonicalFile();
        if( !dir.exists() )
        {
            throw new Exception( "Unable to locate engine lib directory at " + engineLibDir );
        }
        return dir;
    }

    /**
     * Utility method to find the home directory
     * of Phoenix and make sure system property is
     * set to it.
     *
     * @return the location of phoenix directory
     * @throws Exception if unable to locate directory
     */
    private static String findPhoenixHome()
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
        return phoenixHome;
    }

    /**
     *  Finds the LOADER_JAR file in the classpath.
     */
    private static final File findLoaderDir()
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
                File file = (new File( element )).getCanonicalFile();
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
