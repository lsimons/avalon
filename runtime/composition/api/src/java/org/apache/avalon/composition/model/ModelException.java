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

package org.apache.avalon.composition.model;

import org.apache.avalon.framework.CascadingException;

/**
 * Exception to indicate that there was a model related error.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id$
 */
public class ModelException
    extends CascadingException
{

    /**
     * Construct a new <code>ModelException</code> instance.
     *
     * @param message The detail message for this exception.
     */
    public ModelException( final String message )
    {
        this( message, null );
    }

    /**
     * Construct a new <code>ModelException</code> instance.
     *
     * @param message The detail message for this exception.
     * @param throwable the root cause of the exception
     */
    public ModelException( final String message, final Throwable throwable )
    {
        super( message, throwable );
    }
}

