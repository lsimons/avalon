/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.apps.sevak.blocks.catalina;


import java.lang.reflect.Method;
import org.apache.avalon.framework.logger.Logger;

import java.io.File;

import java.net.URL;
import java.net.URLClassLoader;

import java.util.ArrayList;

/**
 * Catalina Sevak Class Loader Factory.
 *
 * This class dynamically loads the org.apache.catalina.loader.StandardClassLoader class used within
 * catalina for bootstrapping.
 *
 *
 * @see <a href="http://jakarta.apache.org/tomcat">Tomcat Project Page</a>
 *
 * @author  Daniel Krieg<dkrieg@kc.rr.com>
 * @version 1.0
 */
public final class CatalinaSevakClassLoaderFactory {
    private static Logger m_logger;

    public static ClassLoader createClassLoader( File unpacked[], File packed[], ClassLoader parent )
            throws Exception {
        getLogger().debug( "Creating new class loader" );

        // Construct the "class path" for this class loader
        ArrayList stringList = new ArrayList();
        ArrayList urlList = new ArrayList();

        // Add unpacked directories
        if( unpacked != null ) {
            for( int i = 0; i < unpacked.length; i++ ) {
                File file = unpacked[ i ];
                if( !file.isDirectory() || !file.exists() || !file.canRead() )
                    continue;
                getLogger().debug( "  Including directory " + file.getAbsolutePath() );
                URL url = new URL( "file", null,
                                   file.getCanonicalPath() + File.separator );
                stringList.add( url.toString() );
                urlList.add( url );
            }
        }

        // Add packed directory JAR files
        if( packed != null ) {
            for( int i = 0; i < packed.length; i++ ) {
                File directory = packed[ i ];
                if( !directory.isDirectory() || !directory.exists() ||
                        !directory.canRead() )
                    continue;
                String filenames[] = directory.list();
                for( int j = 0; j < filenames.length; j++ ) {
                    String filename = filenames[ j ].toLowerCase();
                    if( !filename.endsWith( ".jar" ) )
                        continue;
                    File file = new File( directory, filenames[ j ] );
                    getLogger().debug( ( "  Including jar file " + file.getAbsolutePath() ) );
                    URL url = new URL( "file", null,
                                       file.getCanonicalPath() );
                    stringList.add( url.toString() );
                    urlList.add( url );
                }
            }
        }

        // Construct the class loader itself
        String[] stringArray = (String[]) stringList.toArray( new String[ stringList.size() ] );
        URL[] urlArray = (URL[]) urlList.toArray( new URL[ urlList.size() ] );
        Class loaderClass = ( parent == null ) ? URLClassLoader.newInstance( urlArray ).loadClass( "org.apache.catalina.loader.StandardClassLoader" )
                : URLClassLoader.newInstance( urlArray, parent ).loadClass( "org.apache.catalina.loader.StandardClassLoader" );

        getLogger().debug( loaderClass.getName() + " successfully loaded." );
        Object loader = null;

        if( parent == null ) {
            loader = loaderClass.getConstructor( new Class[]{stringArray.getClass()} )
                    .newInstance( new Object[]{stringArray} );
        } else {
            loader = loaderClass.getConstructor( new Class[]{stringArray.getClass(), ClassLoader.class} )
                    .newInstance( new Object[]{stringArray, parent} );
        }

        getLogger().debug( "Setting loader to delegate=true" );
        Method delegating = loader.getClass().getMethod( "setDelegate", new Class[]{Boolean.TYPE} );
        delegating.invoke( loader, new Object[]{Boolean.TRUE} );
        getLogger().debug( "Class Loader Intance: " + loader );

        getLogger().debug( "ClassLoader creation completed..." );
        return (ClassLoader) loader;

    }

    public static void securityClassLoad( ClassLoader loader ) throws Exception {

        if( System.getSecurityManager() == null )
            return;

        String basePackage = "org.apache.catalina.";
        loader.loadClass
                ( basePackage +
                  "core.ApplicationContext$PrivilegedGetRequestDispatcher" );
        loader.loadClass
                ( basePackage +
                  "core.ApplicationContext$PrivilegedGetResource" );
        loader.loadClass
                ( basePackage +
                  "core.ApplicationContext$PrivilegedGetResourcePaths" );
        loader.loadClass
                ( basePackage +
                  "core.ApplicationContext$PrivilegedLogMessage" );
        loader.loadClass
                ( basePackage +
                  "core.ApplicationContext$PrivilegedLogException" );
        loader.loadClass
                ( basePackage +
                  "core.ApplicationContext$PrivilegedLogThrowable" );
        loader.loadClass
                ( basePackage +
                  "core.ApplicationDispatcher$PrivilegedForward" );
        loader.loadClass
                ( basePackage +
                  "core.ApplicationDispatcher$PrivilegedInclude" );
        loader.loadClass
                ( basePackage +
                  "core.ContainerBase$PrivilegedAddChild" );
        loader.loadClass
                ( basePackage +
                  "connector.HttpRequestBase$PrivilegedGetSession" );
        loader.loadClass
                ( basePackage +
                  "connector.HttpResponseBase$PrivilegedFlushBuffer" );
        loader.loadClass
                ( basePackage +
                  "loader.WebappClassLoader$PrivilegedFindResource" );
        loader.loadClass
                ( basePackage + "session.StandardSession" );
        loader.loadClass
                ( basePackage + "util.CookieTools" );
        loader.loadClass
                ( basePackage + "util.URL" );
        loader.loadClass( basePackage + "util.Enumerator" );
        loader.loadClass( "javax.servlet.http.Cookie" );

    }


    public static void setLogger( Logger logger ) {
        m_logger = logger;
    }

    private static Logger getLogger() {
        return m_logger;
    }
}
