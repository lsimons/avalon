/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.engine.loader;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.security.Security;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;

/**
 * PhoenixLoader is the class that bootstraps and installs the security manager.
 * It also a default policy that gives all code all permssions.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class PhoenixLoader
{
    protected final static boolean         ENABLE_SECURITY_MANAGER =
        !Boolean.getBoolean("phoenix.insecure");

    protected final static String          RESTRICTED_PACKAGES = 
        System.getProperty( "phoenix.restricted.packages", 
                            Security.getProperty("package.access") );

    protected final static String          MAIN_JAR =
        System.getProperty( "phoenix.mainJar", "phoenix-engine.jar" );

    protected final static String          MAIN_CLASS =
        System.getProperty( "phoenix.mainClass", "org.apache.avalon.phoenix.engine.Main" );

    public final static void main( final String args[] )
        throws Exception
    {
        //setup restricted packages
        Security.setProperty( "phoenix.access", RESTRICTED_PACKAGES );

        //setup new Policy manager
        Policy.setPolicy( new FreeNEasyPolicy() );

        final File loaderDir = findLoaderDir();
        final String avalonHome = 
            loaderDir.getAbsoluteFile().getParentFile() + File.separator;
        System.setProperty( "phoenix.home", avalonHome );

        final File mainJar = new File( loaderDir, MAIN_JAR );

        //load main jar
        final URL archive = mainJar.toURL();
        final URLClassLoader classLoader = new URLClassLoader( new URL[] { archive } );

        runSystem( classLoader, args );
    }

    /**
     * load class and retrieve appropriate main method.
     */
    protected static void runSystem( final ClassLoader classLoader, final String[] args )
    {
        try
        {
            final Class clazz = classLoader.loadClass( MAIN_CLASS );
            final Method method = clazz.getMethod( "main", new Class[] { args.getClass() } );

            final Object instance = clazz.newInstance();

            // Set security manager unless it has been disabled by system property
            if( ENABLE_SECURITY_MANAGER )
            {
                System.setSecurityManager( new SecurityManager() );
            }

            //kick the tires and light the fires....
            try
            {
                final PrivilegedExceptionAction action = new PrivilegedExceptionAction() 
                {
                    public Object run() throws Exception
                    {        
                        method.invoke( instance, new Object[] { args } );
                        return null;
                    }
                };
                
                AccessController.doPrivileged( action );
            } 
            catch( final PrivilegedActionException pae )
            {
                // only "checked" exceptions will be "wrapped" in a PrivilegedActionException.
                throw pae.getException();
            }
        }
        catch( final Exception throwable ) 
        {
            throwable.printStackTrace( System.err );
        }
    }

    /**
     *  Finds the avalon-loader.jar file in the classpath.
     */
    protected final static File findLoaderDir() 
        throws Exception
    {
        final String classpath = System.getProperty( "java.class.path" );
        final String pathSeparator = System.getProperty( "path.separator" );
        final StringTokenizer tokenizer = new StringTokenizer( classpath, pathSeparator );
        
        while( tokenizer.hasMoreTokens() )
        {
            final String element = tokenizer.nextToken();
            
            if( element.endsWith( "phoenix-loader.jar" ) )
            {
                File file = (new File( element )).getCanonicalFile();
                file = file.getParentFile();
                return file;                
            }
        }
        
        throw new Exception( "Unable to locate avalon-loader.jar in classpath" );
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

        public void refresh() {}
    }
}
