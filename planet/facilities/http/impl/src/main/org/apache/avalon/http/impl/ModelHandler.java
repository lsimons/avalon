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

import org.apache.avalon.composition.event.CompositionEvent;
import org.apache.avalon.composition.event.CompositionListener;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.ComponentModel;
import org.apache.avalon.composition.model.DeploymentModel;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;

import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;

import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;

import org.apache.avalon.http.HttpRequestHandler;

import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpHandler;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;

/** Wrapper for the Jetty HttpContext.
 *
 * @avalon.component name="http-model-handler" lifestyle="singleton"
 * @avalon.service type="org.mortbay.http.HttpHandler"
 */
public class ModelHandler
    implements Serviceable, Configurable, Contextualizable, LogEnabled,
               HttpHandler, CompositionListener
{
    private Logger           m_Logger;
    private ContainmentModel m_Model;
    private HttpContext      m_Context;
    private String           m_Name;
    private boolean          m_Started;
    
    public ModelHandler()
    {
        m_Started = false;
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
     * Contextulaization of the listener by the container during 
     * which we are supplied with the root composition model for 
     * the application.
     *
     * @param context the supplied listener context
     *
     * @exception ContextException if a contextualization error occurs
     *
     * @avalon.entry key="urn:composition:containment.model" 
     *               type="org.apache.avalon.composition.model.ContainmentModel" 
     *
     * @avalon.entry key="urn:avalon:name" 
     *               type="java.lang.String" 
     */
    public void contextualize( Context ctx ) 
        throws ContextException
    {
        m_Model = (ContainmentModel) ctx.get( "urn:composition:containment.model" );
        m_Name = (String) ctx.get( "urn:avalon:name" );
    }
   
    public void service( ServiceManager man )
        throws ServiceException
    {
    }
    
    public void configure( Configuration conf )
        throws ConfigurationException
    {
    }

    /* HttpHandler interface */
   
    public HttpContext getHttpContext()
    {
        return m_Context;
    }
    
    public String getName()
    {
        return m_Name;
    }

    public void handle( String pathInContext, String pathParams, 
                        HttpRequest request, HttpResponse response ) 
    {
        getLogger().info( "Request: " + pathInContext + ", " + pathParams );
    }

    public void initialize( HttpContext context )
    {
        m_Context = context;
    }

    /* Jetty LifeCycle interface */
    
    public boolean isStarted()
    {
        return m_Started;
    }
    
    public void start()
    {
        m_Started = true;
    }
    
    public void stop()
    {
        m_Started = false;
    }
   
   /* CompositionListener interface  */

   /**
    * Model addition.
    */
    public void modelAdded( CompositionEvent event )
    {
        DeploymentModel model = event.getChild();
        if( ! ( model instanceof HttpRequestHandler ) )
            return;
            
        // TODO:
    }

   /**
    * Model removal.
    */
    public void modelRemoved( CompositionEvent event )
    {
        DeploymentModel model = event.getChild();
        if( ! ( model instanceof HttpRequestHandler ) )
            return;
            
        // TODO:
    }
} 
