/* 
 * Copyright 2002-2004 Apache Software Foundation
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
package org.apache.excalibur.xml.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

/**
 * This interface must be implemented by classes willing
 * to provide an XML representation of their current state.
 * <br/>
 *
 * @author <a href="mailto:sylvain.wallez@anyware-tech.com">Sylvain Wallez</a>
 * @author <a href="mailto:ricardo@apache.org">Ricardo Rocha</a> for the original XObject class
 * @version CVS $Revision: 1.2 $ $Date: 2004/02/19 08:28:32 $
 */
public interface XMLFragment
{
    /**
     * Appends children representing the object's state to the given node.
     */
    void toDOM( Node node ) throws DOMException;
}
