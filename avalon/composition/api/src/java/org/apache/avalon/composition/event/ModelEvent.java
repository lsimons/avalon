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

package org.apache.avalon.composition.event;

import java.util.EventObject;

import org.apache.avalon.composition.model.DeploymentModel;


/**
 * A event object that descirbes a model related event.
 *
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.4 $ $Date: 2004/01/24 23:25:25 $
 */
public abstract class ModelEvent extends EventObject
{
    /**
     * The source model.
     */
    private final DeploymentModel m_model;

    /**
     * Create a ModelEvent instance.
     *
     * @param model the model raising the event
     */
    public ModelEvent( final DeploymentModel model )
    {
        super( model );
        if( null == model ) 
          throw new NullPointerException( "model" ); 
        m_model = model;
    }

    /**
     * Return the the model that generated the event.
     *
     * @return the source model
     */
    public DeploymentModel getModel()
    {
        return m_model;
    }

}
