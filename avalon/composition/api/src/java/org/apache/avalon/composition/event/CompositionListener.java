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

import java.util.EventListener;


/**
 * A listener for model composition changes.
 *
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.1 $ $Date: 2004/02/07 06:06:30 $
 */
public interface CompositionListener extends EventListener
{
    /**
     * Notify the listener that a model has been added to 
     * a source containment model.
     *
     * @param event the containment event raised by the 
     *    source containment model
     */
    void modelAdded( CompositionEvent event );

    /**
     * Notify the listener that a model has been removed from 
     * a source containment model.
     *
     * @param event the containment event raised by the 
     *    source containment model
     */
    void modelRemoved( CompositionEvent event );


}