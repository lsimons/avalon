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

package org.apache.avalon.activation.appliance.impl;

import org.apache.avalon.activation.appliance.Appliance;
import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;

/**
 * An Appliance is the basic tool merlin wraps around a component to
 * provide support for lifecycle and lifestyle management. Different
 * implementations of Appliance can be plugged into the merlin system
 * to allow merlin to manage a variety of components.
 *
 * The name appliance is used to call up an association with a kitchen
 * utility like a microwave. Merlin acts as a chef in his kitchen, and uses
 * various appliances to "cook up" various components as the restaurant
 * customers (which can be other components or systems on the other end
 * on the planet) ask for them.
 *
 * An appliance manages the establishment of a component
 * type relative to a deployment criteria. Once established, an appliance
 * provides support for the deployment of component instances on request.
 * An appliance is responsible for component lifestyle and lifecycle
 * management during the deployment and decommission cycles.
 *
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.6 $ $Date: 2004/02/07 23:32:04 $
 */
public abstract class AbstractAppliance extends AbstractLogEnabled implements Appliance, Disposable
{
    //-------------------------------------------------------------------
    // immutable state
    //-------------------------------------------------------------------

    private DeploymentModel m_model;

    private boolean m_enabled = true;

    //-------------------------------------------------------------------
    // constructor
    //-------------------------------------------------------------------

    public AbstractAppliance( DeploymentModel model )
    {
        if( null == model ) 
        {
            throw new NullPointerException( "model" );
        }
        enableLogging( model.getLogger() );
        m_model = model;
        m_model.setHandler( this );
    }

    //-------------------------------------------------------------------
    // Appliance
    //-------------------------------------------------------------------

    /**
     * Return the model backing the appliance.
     * @return the type that the appliance is managing
     */
    public DeploymentModel getModel()
    {
        return m_model;
    }

    //-------------------------------------------------------------------
    // Disposable
    //-------------------------------------------------------------------

    public void dispose()
    {
        m_model.setHandler( null );
        m_model = null;
        getLogger().debug( "disposal complete" );
    }

    //-------------------------------------------------------------------
    // Object
    //-------------------------------------------------------------------

    public String toString()
    {
        return "appliance:" + getModel().getQualifiedName();
    }
}
