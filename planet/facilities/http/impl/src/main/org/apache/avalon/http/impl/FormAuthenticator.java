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

import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;

import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;

import org.apache.avalon.http.HttpContextService;

import org.mortbay.http.Authenticator;

/** Wrapper for the Jetty FormAuthenticator.
 *
 * @avalon.component name="http-authenticator-form" lifestyle="singleton"
 * @avalon.service type="org.mortbay.http.Authenticator"
 */
public class FormAuthenticator extends org.mortbay.jetty.servlet.FormAuthenticator
    implements Serviceable, LogEnabled, Parameterizable
{
    private HttpContextService  m_Context;
    private Logger              m_Logger;
    
    public FormAuthenticator()
    {
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
     * @avalon.dependency type="org.apache.avalon.http.HttpContextService"
     *                    key="httpcontext" 
     */
    public void service( ServiceManager man )
        throws ServiceException
    {
        m_Context = (HttpContextService) man.lookup( "httpcontext" );
        m_Context.setAuthenticator( this );
    }
    
    public void parameterize( Parameters params )
        throws ParameterException
    {
        String loginPage = params.getParameter( "login-page", null );
        if( loginPage != null )
            setLoginPage( loginPage );
        
        String errorPage = params.getParameter( "error-page", null );
        if( errorPage != null )
            setErrorPage( errorPage );
    }
} 
 
