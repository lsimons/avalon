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

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.activity.Initializable;

import org.apache.avalon.http.HttpService;
import org.apache.avalon.http.HttpRequestHandler;


/**
 * DefaultModelListener listens to application model events
 * and registeres itself as a factory relative to servlet 
 * components.
 *
 * @avalon.component name="listener" lifestyle="singleton"
 * @avalon.service type="org.apache.avalon.composition.event.CompositionListener"
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 */
public class DefaultModelListener extends AbstractLogEnabled 
    implements Contextualizable, Serviceable, Initializable, 
               CompositionListener
{
   //---------------------------------------------------------
   // state
   //---------------------------------------------------------

   /**
    * The http server supplied during the serviceable 
    * lifecycle phase.
    */
    private HttpService m_server;

   /**
    * The root application model supplied during the 
    * contextualization phase.
    */
    private ContainmentModel m_model;

   //---------------------------------------------------------
   // Contextualizable
   //---------------------------------------------------------

  /**
   * Contextulaization of the listener by the container during 
   * which we are supplied with the root composition model for 
   * the application.
   *
   * @param context the supplied listener context
   * @avalon.entry key="urn:composition:containment.model" 
   *    type="org.apache.avalon.composition.model.ContainmentModel" 
   * @exception ContextException if a contextualization error occurs
   */
   public void contextualize( Context context ) throws ContextException
   {
       m_model = 
         (ContainmentModel) context.get( 
           "urn:composition:containment.model" );
   }

   //---------------------------------------------------------
   // Serviceable
   //---------------------------------------------------------

  /**
   * Assignment of dependent services by the container during which
   * we resolve the HTTP service used to register servlets.
   *
   * @param manager the supplied service manager
   * @avalon.dependency type="org.apache.avalon.http.HttpService" key="http"
   * @exception ServiceException if an error is service assignment occurs
   */
   public void service( ServiceManager manager ) throws ServiceException
   {
       m_server = (HttpService) manager.lookup( "http" );
   }

   //---------------------------------------------------------
   // Initializable
   //---------------------------------------------------------

   /**
    * Initialization of the component by the container.
    */
    public void initialize() throws Exception
    {
        if( getLogger().isInfoEnabled() )
        {
            getLogger().debug( 
              "registering listener: " + m_model );
        }

        synchronized( m_model )
        {
            processModel( m_model, true );
        }
    }

    private void processModel( DeploymentModel model, boolean flag )
    {
        if( model instanceof ContainmentModel ) 
        {
            ContainmentModel containment = 
              (ContainmentModel) model;
            if( flag )
            {
                containment.addCompositionListener( this );
            }
            else
            {
                containment.removeCompositionListener( this );
            }
            DeploymentModel[] models = containment.getModels();
            for( int i=0; i<models.length; i++ )
            {
                processModel( models[i], flag );
            }
        }
        else if( model instanceof ComponentModel )
        {
            ComponentModel component = (ComponentModel) model;
            Class clazz = component.getDeploymentClass();
            if( HttpRequestHandler.class.isAssignableFrom( clazz ) )
            {
                if( flag )
                {
                    m_server.register( component );
                }
                else
                {
                    m_server.unregister( component );
                }
            }
        }
    }

   //---------------------------------------------------------
   // CompositionListener
   //---------------------------------------------------------

   /**
    * Model addition.
    */
    public void modelAdded( CompositionEvent event )
    {
        DeploymentModel model = event.getChild();
        processModel( model, true );
    }

   /**
    * Model removal.
    */
    public void modelRemoved( CompositionEvent event )
    {
        DeploymentModel model = event.getChild();
        processModel( model, false );
    }
}
