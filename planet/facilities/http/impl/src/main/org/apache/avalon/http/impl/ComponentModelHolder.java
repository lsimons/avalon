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

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;

import org.apache.avalon.composition.model.ComponentModel;
import org.apache.avalon.http.HttpRequestHandler;

import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.ServletHandler;

/**
 * The ComponentModelHolder ...
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version 1.0
 */
class ComponentModelHolder extends ServletHolder
{
    private final ComponentModel m_model;

    private final ServletAdapter m_adapter;

   /**
    * Construct a Servlet Holder using a supplied ComponentModel.
    *
    * @param model the component model
    * @param handler the servlet handler
    */
    public ComponentModelHolder( 
      ServletHandler handler, ComponentModel model )
    {
        super( 
          handler, model.getName(), 
          model.getType().getInfo().getClassname() );
        m_model = model;
        m_adapter = new ServletAdapter( m_model );
    }

   /** 
    * Intercept the servlet request and handle it locally
    * by redirecting the request directly to our component.
    *
    * @request the servlet request
    * @param response the servlet response
    * @exception ServletException if a servlet exception occurs
    * @exception UnavailableException if the servlet is unavailable
    * @exception IOException if an io related error occurs
    */
    public void handle(ServletRequest request,
                       ServletResponse response)
        throws ServletException,
               UnavailableException,
               IOException
    {        

        boolean servlet_error = true;

        try
        {
            m_adapter.service( request, response );
            servlet_error = false;
        }
        catch(UnavailableException e)
        {
            m_adapter.destroy();
            throw e;
        }
        finally
        {
            if( servlet_error )
            {
                request.setAttribute( 
                  "javax.servlet.error.servlet_name", 
                  m_model.getName() );
            }
        }
    }

    private class ServletAdapter extends GenericServlet
    {
        private final ComponentModel m_model;
 
        public ServletAdapter( final ComponentModel model )
        {
            m_model = model;
        }

        public void destroy()
        {
        }

        public void service(ServletRequest request, ServletResponse response)
          throws ServletException, IOException
        {
            try
            {
                getHandler().service( request, response );
            }
            catch( Throwable e )
            {
                throw new ServletException( e.getMessage(), e.getCause() );
            }
        }

        private HttpRequestHandler getHandler() throws ServletException
        {
            try
            {
                return (HttpRequestHandler) m_model.resolve();
            }
            catch( Throwable e )
            {
                throw new ServletException( e.getMessage(), e.getCause() );
            }
        }
    }
}
