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
public class CatalinaSevakBootstrap
        extends AbstractLogEnabled
        implements Contextualizable, Configurable, Initializable, Startable, Sevak, MultihostSevak {
    private BlockContext m_context;
    private Configuration m_configuration;
    private Object m_sevak;
    private String m_catalinaHome;
    private String m_catalinaBase;
    private boolean m_useNaming;
    private String m_configFile;
    private final static String C_HOST = "localhost";

    public void contextualize( Context context ) throws ContextException {
        getLogger().debug( "CatalinaSevakBootstrap.contextualize()" );
        m_context = (BlockContext)context;
    }

    public void configure( Configuration configuration ) throws ConfigurationException {
        getLogger().debug( "CatalinaSevakBootstrap.configure()" );
        m_configuration = configuration;

        m_catalinaHome = m_configuration.getChild( "catalina-home" ).getValue( m_context.getBaseDirectory().getAbsolutePath()
        );
        m_catalinaBase = m_configuration.getChild( "catalina-base" ).getValue( m_context.getBaseDirectory().getAbsolutePath() );
        m_configFile = m_configuration.getChild( "config-file" ).getValue( "conf/server.xml" );
        m_useNaming = m_configuration.getChild( "use-naming" ).getValueAsBoolean( true );

        setCatalinaHome();
        setCatalinaBase();
        getLogger().debug( "Catalina Home: " + getCatalinaHome() );
        getLogger().debug( "Catalina Base: " + getCatalinaBase() );
    }

    public void initialize() throws Exception {
        getLogger().debug( "CatalinaSevakBootstrap.initialize()" );
        CatalinaSevakClassLoaderFactory.setLogger( getLogger() );

        // Construct the class loaders we will need
        ClassLoader bootstrapLoader = null;
        ClassLoader commonLoader = null;
        ClassLoader sevakLoader = null;
        ClassLoader sharedLoader = null;
        try {
            File[] unpacked = new File[ 1 ];
            File[] packed = new File[ 1 ];
            File[] packed2 = new File[ 2 ];

            //  load bootstrap classes
            packed[ 0 ] = new File( getCatalinaHome(), "bin" );
            bootstrapLoader = CatalinaSevakClassLoaderFactory.createClassLoader( null, packed, null );
            getLogger().debug( "Loaded bin dir..." );

            //  load common directory
            unpacked[ 0 ] = new File( getCatalinaHome(), "common" + File.separator + "classes" );
            packed2[ 0 ] = new File( getCatalinaHome(), "common" + File.separator + "endorsed" );
            packed2[ 1 ] = new File( getCatalinaHome(), "common" + File.separator + "lib" );
            commonLoader = CatalinaSevakClassLoaderFactory.createClassLoader( unpacked, packed2, bootstrapLoader );
            getLogger().debug( "Loaded common dir..." );

            //  load server directory
            unpacked[ 0 ] = new File( getCatalinaHome(), "server" + File.separator + "classes" );
            packed2[ 0 ] = new File( getCatalinaHome(), "server" + File.separator + "lib" );
            packed2[ 1 ] = new File( getStartupDir() );
            sevakLoader = CatalinaSevakClassLoaderFactory.createClassLoader( unpacked, packed2, commonLoader );
            getLogger().debug( "Loaded server dir..." );
            getLogger().debug( "Loaded startup dir..." + packed2[ 1 ].getAbsoluteFile() );

            //  load shared directory
            unpacked[ 0 ] = new File( getCatalinaBase(), "shared" + File.separator + "classes" );
            packed[ 0 ] = new File( getCatalinaBase(), "shared" + File.separator + "lib" );
            sharedLoader = CatalinaSevakClassLoaderFactory.createClassLoader( unpacked, packed, commonLoader );
            getLogger().debug( "Loaded shared dir..." );
        } catch( Exception e ) {
            getLogger().fatalError( "Class loader creation threw exception", e );
            throw e;
        }

        try {
            // Load our startup class and run its lifecycle
            Thread.currentThread().setContextClassLoader( sevakLoader );
            CatalinaSevakClassLoaderFactory.securityClassLoad( sevakLoader );

            // Instantiate a startup class instance
            getLogger().debug( "Loading startup class" );
            Class startupClass =
                    sevakLoader.loadClass( "org.apache.avalon.apps.sevak.blocks.catalina.CatalinaSevak" );
            getLogger().debug( "Startup class loaded: " + startupClass );
            m_sevak = startupClass.newInstance();

            // Set the shared extensions class loader
            Class paramTypes[] = new Class[ 1 ];
            Object paramValues[] = new Object[ 1 ];
            getLogger().debug( "Setting startup class properties" );

            String methodName = "setParentClassLoader";
            paramTypes[ 0 ] = Class.forName( "java.lang.ClassLoader" );
            paramValues[ 0 ] = commonLoader;
            Method method = m_sevak.getClass().getMethod( methodName, paramTypes );
            method.invoke( m_sevak, paramValues );

            methodName = "setUseNaming";
            paramTypes[ 0 ] = Boolean.TYPE;
            paramValues[ 0 ] = ( m_useNaming ) ? Boolean.TRUE : Boolean.FALSE;
            method = m_sevak.getClass().getMethod( methodName, paramTypes );
            method.invoke( m_sevak, paramValues );

            methodName = "setConfigFile";
            paramTypes[ 0 ] = String.class;
            paramValues[ 0 ] = m_configFile;
            method = m_sevak.getClass().getMethod( methodName, paramTypes );
            method.invoke( m_sevak, paramValues );

            methodName = "initialize";
            method = m_sevak.getClass().getMethod( methodName, null );
            method.invoke( m_sevak, null );
        } catch( Exception e ) {
            getLogger().fatalError( "Exception during startup processing", e );
            throw e;
        }
        getLogger().debug( "CatalinaSevakBootstrap complete." );
    }

    public void start() throws Exception {
        getLogger().debug( "CatalinaSevakBootstrap.start()" );
        m_sevak.getClass().getMethod( "start", null ).invoke( m_sevak, null );
    }

    public void stop() throws Exception {
        getLogger().debug( "CatalinaSevakBootstrap.stop()" );
        m_sevak.getClass().getMethod( "stop", null ).invoke( m_sevak, null );
    }

    public void deploy( String context, File pathToWebAppFolder ) throws SevakException {
        getLogger().debug( "CatalinaSevakBootstrap.deploy()" );
        deploy( C_HOST, context, pathToWebAppFolder );
    }

    public void undeploy( String context ) throws SevakException {
        getLogger().debug( "CatalinaSevakBootstrap.undeploy()" );
        undeploy( C_HOST, context );
    }

    public void deploy( String host, String context, File pathToWebAppFolder ) throws SevakException {
        Class[] paramTypes = {String.class, String.class, File.class};
        Object[] paramValues = {host, context, pathToWebAppFolder};
        try {
            m_sevak.getClass().getMethod( "deploy", paramTypes ).invoke( m_sevak, paramValues );
        } catch( Exception e ) {
            throw new SevakException( "Unable to deploy", e );
        }
    }

    public void deploy(String context, File pathToWebAppFolder, ServiceManager serviceManager) throws SevakException
    {
        throw new UnsupportedOperationException();
    }

    public void undeploy( String host, String context ) throws SevakException {
        Class[] paramTypes = {String.class, String.class};
        Object[] paramValues = {host, context};
        try {
            m_sevak.getClass().getMethod( "undeploy", paramTypes ).invoke( m_sevak, paramValues );
        } catch( Exception e ) {
            throw new SevakException( "Unable to undeploy", e );
        }
    }

    private void setCatalinaBase() {
        if( m_catalinaBase.equals( "default" ) ) {
            System.setProperty( "catalina.base", getCatalinaHome() );
        } else if( m_catalinaBase.equals( "user.dir" ) ) {
            System.setProperty( "catalina.base", System.getProperty( "user.dir" ) );
        } else {
            System.setProperty( "catalina.base", m_catalinaBase );
        }
    }

    private String getCatalinaBase() {
        if( System.getProperty( "catalina.base" ) == null ) {
            setCatalinaBase();
        }
        return System.getProperty( "catalina.base" );
    }

    private void setCatalinaHome() {
        if( m_catalinaHome.equals( "default" ) ) {
            System.setProperty( "catalina.home", System.getProperty( "phoenix.home" ) + File.separator + "catalina" );
        } else {
            System.setProperty( "catalina.home", m_catalinaHome );
        }
    }

    private String getCatalinaHome() {
        if( System.getProperty( "catalina.home" ) == null ) {
            setCatalinaHome();
        }
        return System.getProperty( "catalina.home" );
    }

    private String getStartupDir() {
        String startup = this.getClass().getClassLoader()
                .getResource( "org/apache/avalon/apps/sevak/blocks/catalina/CatalinaSevak.class" )
                .toExternalForm();
        startup = startup.substring( "jar:file:/".length(), startup.indexOf( "!" ) );
        getLogger().debug( "Start up JAR " + startup );
        return new File( startup ).getParent();
    }
}
