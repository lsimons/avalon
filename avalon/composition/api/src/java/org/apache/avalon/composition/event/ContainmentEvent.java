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

import org.apache.avalon.composition.model.ContainmentModel;


/**
 * A event object that descirbes a containment model related event.
 *
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.2 $ $Date: 2004/01/24 23:25:25 $
 */
public class ContainmentEvent extends ModelEvent
{
    /**
     * Create a ContainmentEvent instance.
     *
     * @param model the source containment model
     */
    public ContainmentEvent( final ContainmentModel model )
    {
        super( model );
    }

    /**
     * Return the the containment model that generated the event.
     *
     * @return the source containment model
     */
    public ContainmentModel getContainmentModel()
    {
        return (ContainmentModel) super.getModel();
    }

}
