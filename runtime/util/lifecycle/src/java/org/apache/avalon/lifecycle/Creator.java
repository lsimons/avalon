/* 
 * Copyright 2003-2004 The Apache Software Foundation
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
package org.apache.avalon.lifecycle;

import org.apache.avalon.framework.context.Context;

/**
 * The <code>Creator</code> interface describes the create and destroy
 * stages that occur between a component and a container
 * during service management.  Lifecycle extensions supporting create
 * and destroy stages must implement this interface.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id$
 */
public interface Creator
{
    /**
     * Create stage handler.
     *
     * @param object the object that is being created
     * @param context the context instance required by the create handler
     *    implementation
     * @exception Exception if an error occurs
     */
    void create( Object object, Context context )
        throws Exception;

    /**
     * Destroy stage handler.
     *
     * @param object the object that is being destroyed
     * @param context the context instance required by the handler
     *    implementation
     */
    void destroy( Object object, Context context );

}
