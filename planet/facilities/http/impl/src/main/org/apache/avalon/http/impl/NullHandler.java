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

import org.apache.avalon.framework.activity.Startable;

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

/**
 * @avalon.component name="http-null-handler" lifestyle="singleton"
 * @avalon.server    type="org.mortbay.http.HttpHandler"
 */
public class NullHandler 
    extends org.mortbay.http.handler.NullHandler 
    implements Startable, Parameterizable, LogEnabled, 
               Serviceable, Contextualizable
{
    private Logger m_Logger;
    private HttpContextService  m_Context;
    private int m_Index;
    
    public NullHandler()
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
     * Contextulaization of the Handler.
     *
     * @param context the supplied listener context
     *
     * @exception ContextException if a contextualization error occurs
     *
     * @avalon.entry key="urn:avalon:name" 
     *               type="java.lang.String" 
     */
    public void contextualize( Context ctx ) 
        throws ContextException
    {
        String name = (String) ctx.get( "urn:avalon:name" );
        setName( name );
    }

    public void parameterize( Parameters params )
        throws ParameterException
    {
        m_Index = params.getParameterAsInteger( "handler-index", -1 );
    }

    /**  
     * @avalon.dependency type="org.apache.avalon.http.HttpContextService"
     *                    key="http-context" 
     */
    public void service( ServiceManager man )
        throws ServiceException
    {
        m_Context = (HttpContextService) man.lookup( "http-context" );
    }
 
    public void start()
    {
        if( m_Logger.isDebugEnabled() )
            m_Logger.debug( "Starting NullHandler: " + this );
        if( m_Index >= 0 )
            m_Context.addHandler( m_Index, this );
        else
            m_Context.addHandler( this );
    }
    
    public void stop()
    {
        if( m_Logger.isDebugEnabled() )
            m_Logger.debug( "Stopping NullHandler: " + this );
        m_Context.removeHandler( this );
    }
} 
 
