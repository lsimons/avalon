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

package org.apache.avalon.composition.provider;

import org.apache.avalon.composition.model.Commissionable;
import org.apache.avalon.composition.model.Resolver;

/**
 * A lifestyle handler provides support for a particular lifestyle policy.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2004/03/04 03:42:30 $
 */
public interface LifestyleManager extends Commissionable, Resolver
{
    /**
     * Release and finalize the supplied object.
     *
     * @param instance the object to be released
     */
    void finalize( Object instance );

}
