/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002,2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
