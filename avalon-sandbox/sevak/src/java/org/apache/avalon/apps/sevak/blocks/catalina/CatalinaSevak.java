/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.apps.sevak.blocks.catalina;

import java.security.Security;
import org.apache.catalina.Container;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.Deployer;
import org.apache.catalina.startup.ContextRuleSet;
import org.apache.catalina.startup.EngineRuleSet;
import org.apache.catalina.startup.HostRuleSet;
import org.apache.catalina.startup.NamingRuleSet;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileInputStream;

/**
 * Tomcat Wrapper.  This is the true CatalinaSevak service.  It must be dynamically loaded through a bootstrap
 * approach because it directly manipulates Tomcat libraries.  The bootstrap abstraction layer separates Tomcat
 * from Phoenix, thus allowing them to coexist.  This class contains a Tomcat-specific ClassLoader
 * i.e. org.apache.catalina.loader.StandardClassLoader that handles the true bootstrapping of Tomcat.
 *
 * @see <a href="http://jakarta.apache.org/tomcat">Tomcat Project Page</a>
 *
 * @author  Daniel Krieg<dkrieg@kc.rr.com>
 * @version 1.0
 */
public class CatalinaSevak {
    private ClassLoader m_parentLoader = ClassLoader.getSystemClassLoader();
    private Server m_server = null;
    private String m_configFile;
    private boolean m_useNaming = true;

    public void setParentClassLoader( ClassLoader parentLoader ) {
        m_parentLoader = parentLoader;
    }

    public void setConfigFile( String configFile ) {
        m_configFile = configFile;
    }

    public void setUseNaming( boolean useNaming ) {
        m_useNaming = useNaming;
    }

    public void setServer( Server server ) {
        this.m_server = server;
    }

    public void initialize() throws Exception {
        Digester digester = createStartDigester();
        File file = configFile();
        try {
            InputSource is =
                    new InputSource( "file://" + file.getAbsolutePath() );
            FileInputStream fis = new FileInputStream( file );
            is.setByteStream( fis );
            digester.push( this );
            digester.parse( is );
            fis.close();
        } catch( Exception e ) {
            e.printStackTrace();
            return;
        }

        if( !m_useNaming ) {
            System.setProperty( "catalina.useNaming", "false" );
        } else {
            System.setProperty( "catalina.useNaming", "true" );
            String value = "org.apache.naming";
            String oldValue =
                    System.getProperty( javax.naming.Context.URL_PKG_PREFIXES );
            if( oldValue != null ) {
                value = value + ":" + oldValue;
            }
            System.setProperty( javax.naming.Context.URL_PKG_PREFIXES, value );
            value = System.getProperty
                    ( javax.naming.Context.INITIAL_CONTEXT_FACTORY );
            if( value == null ) {
                System.setProperty
                        ( javax.naming.Context.INITIAL_CONTEXT_FACTORY,
                          "org.apache.naming.java.javaURLContextFactory" );
            }
        }

        if( System.getSecurityManager() != null ) {
            String access = Security.getProperty( "package.access" );
            if( access != null && access.length() > 0 )
                access += ",";
            else
                access = "sun.,";
            Security.setProperty( "package.access",
                                  access + "org.apache.catalina.,org.apache.jasper." );
            String definition = Security.getProperty( "package.definition" );
            if( definition != null && definition.length() > 0 )
                definition += ",";
            else
                definition = "sun.,";
            Security.setProperty( "package.definition",
                                  // FIX ME package "javax." was removed to prevent HotSpot
                                  // fatal internal errors
                                  definition + "java.,org.apache.catalina.,org.apache.jasper." );
        }

        if( m_server instanceof Lifecycle ) {
            m_server.initialize();
        }
//        Service[] services = m_server.findServices();
//        for( int i = 0; i < services.length; i++ ) {
//            Service service = services[ i ];
//            System.out.println( service.getName() + ": " + service.getInfo() );
//
//            Container container = service.getContainer();
//            System.out.println( container.getName() + ": " + container.getInfo() );
//            Container[] children = container.findChildren();
//            for( int j = 0; j < children.length; j++ ) {
//                Container child = children[ j ];
//                System.out.println( "    Child: " + child.getName() + ": " + child.getInfo() );
//            }
//            System.out.println();
//        }
        System.out.println( "CatalinaSevak Server initialized" );
    }

    public void start() throws Exception {
        if( m_server instanceof Lifecycle ) {
            try {
                ( (Lifecycle) m_server ).start();
                m_server.await();
            } catch( LifecycleException e ) {
                System.out.println( "Catalina.start: " + e );
                e.printStackTrace( System.out );
                if( e.getThrowable() != null ) {
                    System.out.println( "----- Root Cause -----" );
                    e.getThrowable().printStackTrace( System.out );
                }
            } catch( Throwable throwable ) {
                throwable.printStackTrace();
            }
        } else {
            throw new Exception( "Unable to start CatalinSevak Server: " + m_server.getClass().getName() );
        }
    }

    public void stop() throws Exception {
        if( m_server instanceof Lifecycle ) {
            try {
                ( (Lifecycle) m_server ).stop();
            } catch( LifecycleException e ) {
                System.out.println( "Catalina.stop: " + e );
                e.printStackTrace( System.out );
                if( e.getThrowable() != null ) {
                    System.out.println( "----- Root Cause -----" );
                    e.getThrowable().printStackTrace( System.out );
                }
            }
        } else {
            throw new Exception( "Unable to start CatalinSevak Server: " + m_server.getClass().getName() );
        }
    }

    public void deploy( String host, String context, File pathToWebAppFolder ) throws Exception {
        Service[] services = m_server.findServices();
        Container child = null;
        found_host: {
            for( int i = 0; i < services.length; i++ ) {
                Service service = services[ i ];
                Container[] children = service.getContainer().findChildren();
                for( int j = 0; j < children.length; j++ ) {
                    child = children[ j ];
                    if( child.getName().equals( host ) ) {
                        break found_host;
                    }
                }
            }
        }
        if( child == null ) {
            throw new IllegalArgumentException( host + ": no such host." );
        }
        if( !( child instanceof Deployer ) ) {
            throw new Exception( host + ": not able to deploy " + context );
        }

        final Deployer deployer = (Deployer) child;
        if( deployer.findDeployedApp( context ) != null ) {
            throw new Exception( context + " already deployed to host " + host );
        }
        deployer.install( context, pathToWebAppFolder.toURL() );
//        deployer.start( context );
    }

    public void undeploy( String host, String context ) throws Exception {
        Service[] services = m_server.findServices();
        Container child = null;
        found_host: {
            for( int i = 0; i < services.length; i++ ) {
                Service service = services[ i ];
                Container[] children = service.getContainer().findChildren();
                for( int j = 0; j < children.length; j++ ) {
                    child = children[ j ];
                    if( child.getName().equals( host ) ) {
                        break found_host;
                    }
                }
            }
        }
        if( child == null ) {
            throw new IllegalArgumentException( host + ": no such host." );
        }
        if( !( child instanceof Deployer ) ) {
            throw new Exception( host + ": not able to undeploy " + context );
        }
        final Deployer deployer = (Deployer) child;
        if( deployer.findDeployedApp( context ) == null ) {
            throw new Exception( context + " does not exist in host " + host );
        }
//        deployer.start( context );
        deployer.remove( context );
    }

    protected Digester createStartDigester() {
        Digester digester = new Digester();
        digester.setValidating( false );

        digester.addObjectCreate( "Server",
                                  "org.apache.avalon.apps.sevak.blocks.catalina.CatalinaSevakServer" );
        digester.addSetProperties( "Server" );
        digester.addSetNext( "Server",
                             "setServer",
                             "org.apache.catalina.Server" );

        digester.addObjectCreate( "Server/GlobalNamingResources",
                                  "org.apache.catalina.deploy.NamingResources" );
        digester.addSetProperties( "Server/GlobalNamingResources" );
        digester.addSetNext( "Server/GlobalNamingResources",
                             "setGlobalNamingResources",
                             "org.apache.catalina.deploy.NamingResources" );

        digester.addObjectCreate( "Server/Listener",
                                  null, // MUST be specified in the element
                                  "className" );
        digester.addSetProperties( "Server/Listener" );
        digester.addSetNext( "Server/Listener",
                             "addLifecycleListener",
                             "org.apache.catalina.LifecycleListener" );

        digester.addObjectCreate( "Server/Service",
                                  "org.apache.catalina.core.StandardService",
                                  "className" );
        digester.addSetProperties( "Server/Service" );
        digester.addSetNext( "Server/Service",
                             "addService",
                             "org.apache.catalina.Service" );

        digester.addObjectCreate( "Server/Service/Listener",
                                  null, // MUST be specified in the element
                                  "className" );
        digester.addSetProperties( "Server/Service/Listener" );
        digester.addSetNext( "Server/Service/Listener",
                             "addLifecycleListener",
                             "org.apache.catalina.LifecycleListener" );

        digester.addObjectCreate( "Server/Service/Connector",
                                  "org.apache.catalina.connector.http.HttpConnector",
                                  "className" );
        digester.addSetProperties( "Server/Service/Connector" );
        digester.addSetNext( "Server/Service/Connector",
                             "addConnector",
                             "org.apache.catalina.Connector" );

        digester.addObjectCreate( "Server/Service/Connector/Factory",
                                  "org.apache.catalina.net.DefaultServerSocketFactory",
                                  "className" );
        digester.addSetProperties( "Server/Service/Connector/Factory" );
        digester.addSetNext( "Server/Service/Connector/Factory",
                             "setFactory",
                             "org.apache.catalina.net.ServerSocketFactory" );

        digester.addObjectCreate( "Server/Service/Connector/Listener",
                                  null, // MUST be specified in the element
                                  "className" );
        digester.addSetProperties( "Server/Service/Connector/Listener" );
        digester.addSetNext( "Server/Service/Connector/Listener",
                             "addLifecycleListener",
                             "org.apache.catalina.LifecycleListener" );

        digester.addRuleSet( new NamingRuleSet( "Server/GlobalNamingResources/" ) );
        digester.addRuleSet( new EngineRuleSet( "Server/Service/" ) );
        digester.addRuleSet( new HostRuleSet( "Server/Service/Engine/" ) );
        digester.addRuleSet( new ContextRuleSet( "Server/Service/Engine/Default" ) );
        digester.addRuleSet( new NamingRuleSet( "Server/Service/Engine/DefaultContext/" ) );
        digester.addRuleSet( new ContextRuleSet( "Server/Service/Engine/Host/Default" ) );
        digester.addRuleSet( new NamingRuleSet( "Server/Service/Engine/Host/DefaultContext/" ) );
        digester.addRuleSet( new ContextRuleSet( "Server/Service/Engine/Host/" ) );
        digester.addRuleSet( new NamingRuleSet( "Server/Service/Engine/Host/Context/" ) );

        digester.addRule( "Server/Service/Engine",
                          new ParentClassLoaderRule( digester,
                                                        m_parentLoader ) );
        return ( digester );
    }

    protected File configFile() {

        File file = new File( m_configFile );
        if( !file.isAbsolute() )
            file = new File( System.getProperty( "catalina.base" ), m_configFile );
        return ( file );

    }
}

