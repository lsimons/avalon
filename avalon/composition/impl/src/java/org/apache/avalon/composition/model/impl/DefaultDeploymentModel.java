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

package org.apache.avalon.composition.model.impl;

import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.DeploymentContext;
import org.apache.avalon.composition.model.DependencyGraph;
import org.apache.avalon.composition.model.SystemContext;
import org.apache.avalon.composition.data.Mode;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;


/**
 * Abstract model base class.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.13 $ $Date: 2004/01/24 23:25:27 $
 */
public abstract class DefaultDeploymentModel
  implements DeploymentModel
{
    //--------------------------------------------------------------
    // static
    //--------------------------------------------------------------

    private static final Resources REZ =
      ResourceManager.getPackageResources( DefaultDeploymentModel.class );

    //--------------------------------------------------------------
    // immutable state
    //--------------------------------------------------------------

    private final DeploymentContext m_context;

    //--------------------------------------------------------------
    // muttable state
    //--------------------------------------------------------------

    private Object m_handler = null;

    //--------------------------------------------------------------
    // constructor
    //--------------------------------------------------------------

   /**
    * Creation of an abstract model.  The model associated a 
    * name and a partition.
    *
    * @param context the deployment context
    */
    public DefaultDeploymentModel( DeploymentContext context )
    { 
        if( null == context )
        {
            throw new NullPointerException( "context" );
        }
        m_context = context;
    }

    //--------------------------------------------------------------
    // DeploymentModel
    //--------------------------------------------------------------

   /**
    * Return the profile name.
    * @return the name
    */
    public String getName()
    {
        return m_context.getName();
    }

   /**
    * Return the profile path.
    * @return the path
    */
    public String getPath()
    {
        if( null == m_context.getPartitionName() )
        {
            return SEPARATOR;
        }
        return m_context.getPartitionName();
    }

   /**
    * Return the model fully qualified name.
    * @return the fully qualified name
    */
    public String getQualifiedName()
    {
        return getPath() + getName();
    }

   /**
    * Return the mode of establishment.
    * @return the mode
    */
    public Mode getMode()
    {
        return m_context.getMode();
    }

   /**
    * Return the set of models consuming this model.
    * @return the consumers
    */
    public DeploymentModel[] getConsumerGraph()
    {
        return m_context.getDependencyGraph().getConsumerGraph( this );
    }

   /**
    * Return the set of models supplying this model.
    * @return the providers
    */
    public DeploymentModel[] getProviderGraph()
    {
        return m_context.getDependencyGraph().getProviderGraph( this );
    }

   /**
    * Set the runtime handler for the model.
    * @param handler the runtime handler
    */
    public void setHandler( Object handler )
    {
        m_handler = handler;
    }

   /**
    * Get the assigned runtime handler for the model.
    * @return the runtime handler
    */
    public Object getHandler()
    {
        return m_handler;
    }

   /**
    * Return the assigned logging channel.
    * @return the logging channel
    */
    public Logger getLogger()
    {
        return m_context.getLogger();
    }

    //--------------------------------------------------------------
    // implementation
    //--------------------------------------------------------------

    public String toString()
    {
        return "[" + getQualifiedName() + "]";
    }

    public boolean equals( Object other )
    {
        boolean equal = super.equals( other ); 
        return equal;
    }

   /** 
    * Return the default deployment timeout value declared in the 
    * kernel configuration.  The implementation looks for a value
    * assigned under the property key "urn:composition:deployment.timeout"
    * and defaults to 1000 msec if undefined.
    *
    * @return the default deployment timeout value
    */
    public long getDeploymentTimeout()
    {
        SystemContext system = m_context.getSystemContext();
        return system.getDefaultDeploymentTimeout();
    }

}
