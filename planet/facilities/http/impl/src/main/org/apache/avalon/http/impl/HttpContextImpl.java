/* 
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.avalon.http.impl;

import java.io.File;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Startable;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;

import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;

import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;

import org.apache.avalon.http.HttpContextService;
import org.apache.avalon.http.HttpService;

import org.mortbay.http.Authenticator;
import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpHandler;
import org.mortbay.http.RequestLog;
import org.mortbay.http.UserRealm;

/** Wrapper for the Jetty HttpContext.
 *
 * @avalon.component name="http-context" lifestyle="singleton"
 * @avalon.service type="org.apache.avalon.http.HttpContextService"
 */
public class HttpContextImpl
    implements LogEnabled, Contextualizable, Serviceable, Startable, 
               Disposable, Configurable, HttpContextService
{
    private HttpService m_HttpServer;
    private HttpContext m_HttpContext;
    private Logger      m_Logger;
    private boolean     m_Graceful;
        
    public HttpContextImpl()
    {
        m_HttpContext = new HttpContext();
    }
    
    public HttpContext getHttpContext()
    {
        return m_HttpContext;
    }
    
    /**
     * Enable the logging system.
     *
     * @avalon.logger name="http"
     */
    public void enableLogging( Logger logger )
    {
        m_Logger = logger;
    }
    
    public Logger getLogger()
    {
        return m_Logger;
    }
    
    /** 
     * @avalon.entry key="urn:avalon:temp" type="java.io.File"
     */
    public void contextualize( Context ctx )
        throws ContextException
    {
        File tmpDir = (File) ctx.get( "urn:avalon:temp" );
        tmpDir.mkdirs();
        m_HttpContext.setTempDirectory( tmpDir );
    }
    
    /**
     * @avalon.dependency type="org.apache.avalon.http.HttpService"
     *                    key="server"
     * @avalon.dependency type="org.mortbay.http.Authenticator"
     *                    key="authenticator" optional="true"
     * @avalon.dependency type="org.mortbay.http.UserRealm"
     *                    key="realm" optional="true"
     * @avalon.dependency type="org.mortbay.http.RequestLog"
     *                    key="request-log" 
     */
    public void service( ServiceManager man )
        throws ServiceException
    {
        m_HttpServer = (HttpService) man.lookup( "server" );
        
        if( man.hasService( "authenticator" ) )
        {
            Authenticator auth = (Authenticator) man.lookup( "authenticator" );
            m_HttpContext.setAuthenticator( auth );
        }
        
        if( man.hasService( "realm" ) )
        {
            UserRealm realm = (UserRealm) man.lookup( "realm" );
            m_HttpContext.setRealm( realm );
            m_HttpContext.setRealmName( realm.getName() ); // Is this necessary?
        } 
        
        RequestLog log = (RequestLog) man.lookup( "request-log" );
        m_HttpContext.setRequestLog( log );
    }

    public void parameterize( Parameters params )
        throws ParameterException
    {
        String[] names = params.getNames();
        for( int i=0 ; i < names.length ; i++ )
        {
            String value = params.getParameter( names[i] );
            m_HttpContext.setInitParameter( names[i], value );
        }
    }
    
    public void configure( Configuration conf )
        throws ConfigurationException
    {
        m_Graceful = conf.getChild( "graceful-stop" ).getValueAsBoolean( false );
        
        Configuration attributes = conf.getChild( "attributes" );
        configureAttributes( attributes );
        
        Configuration contextPath = conf.getChild( "context-path" );
        m_HttpContext.setContextPath( contextPath.getValue( "/" ) );
        
        Configuration virtualHosts = conf.getChild( "virtual-hosts" );
        configureVirtualHosts( virtualHosts );
    
        Configuration welcomeFiles = conf.getChild( "welcome-files" );
        configureWelcomeFiles( welcomeFiles );
    }
    
    private void configureAttributes( Configuration conf )
        throws ConfigurationException
    {
        Configuration[] children = conf.getChildren( "attribute" );
        for( int i = 0 ; i < children.length ; i++ )
            configureAttribute( children[i] );
    }
    
    private void configureAttribute( Configuration conf )
        throws ConfigurationException
    {
        String name = conf.getAttribute( "name" );
        String value = conf.getValue();
        
        // TODO: setAttribute() support Object as a value.
        //       need to figure out what that could be and introduce
        //       support for it.
        m_HttpContext.setAttribute( name, value );
    }
    
    private void configureVirtualHosts( Configuration conf )
        throws ConfigurationException
    {
        Configuration[] hosts = conf.getChildren( "host" );
        for( int i=0 ; i < hosts.length ; i++ )
            m_HttpContext.addVirtualHost( hosts[i].getValue() );
    }
    
    private void configureWelcomeFiles( Configuration conf )
        throws ConfigurationException
    {
        Configuration[] files = conf.getChildren( "file" );
        for( int i=0 ; i < files.length ; i++ )
            m_HttpContext.addWelcomeFile( files[i].getValue() );
    }
    
    public void start()
        throws Exception
    {
        if( m_Logger.isDebugEnabled() )
            m_Logger.debug( "Starting context: " + m_HttpContext );
        m_HttpServer.addContext( m_HttpContext );
        m_HttpContext.start();
    }
    
    public void stop()
        throws Exception
    {
        if( m_Logger.isDebugEnabled() )
            m_Logger.debug( "Stopping context: " + m_HttpContext );
        m_HttpContext.stop( m_Graceful );
        m_HttpServer.removeContext( m_HttpContext );
    }
    
    public void dispose()
    {
        if( m_Logger.isDebugEnabled() )
            m_Logger.debug( "Disposing context: " + m_HttpContext );
        m_HttpContext.destroy();
        m_HttpServer = null;
        m_HttpContext = null;
    }

    /* Service Interface */
    
    public void addHandler( HttpHandler handler )
    {
        if( m_Logger.isDebugEnabled() )
            m_Logger.debug( "Adding handler: " + handler );
        m_HttpContext.addHandler( handler );
    }
    
    public void removeHandler( HttpHandler handler )
    {
        if( m_Logger.isDebugEnabled() )
            m_Logger.debug( "Removing handler: " + handler );
        m_HttpContext.removeHandler( handler );
    }
}
