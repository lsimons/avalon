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

package org.apache.avalon.fortress.examples.extended.extensions;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.lifecycle.AbstractAccessor;

/**
 * Some custom extensions for this container's components.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.9 $ $Date: 2004/02/24 22:31:21 $
 */
public class Extensions
    extends AbstractAccessor
{
    /**
     * Access, called when the given component is being
     * accessed (ie. via lookup() or select()).
     *
     * @param component a <code>Component</code> instance
     * @param context a <code>Context</code> instance
     * @exception java.lang.Exception if an error occurs
     */
    public void access( Object component, Context context )
        throws Exception
    {
        if( component instanceof SecurityManageable )
        {
            // pass in a simple security manager for testing, a real
            // system might want to pass in specialized/custom security managers
            ( (SecurityManageable)component ).secure( new SecurityManager() );
        }
    }
}

