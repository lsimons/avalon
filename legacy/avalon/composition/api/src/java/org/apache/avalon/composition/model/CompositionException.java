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
 * Exception to indicate that there was a appliance related error.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2004/02/10 16:23:33 $
 */
public class CompositionException
        extends CascadingException
{

    /**
     * Construct a new <code>CompositionException</code> instance.
     *
     * @param message The detail message for this exception.
     */
    public CompositionException( final String message )
    {
        this( message, null );
    }

    /**
     * Construct a new <code>CompositionException</code> instance.
     *
     * @param message The detail message for this exception.
     * @param throwable the root cause of the exception
     */
    public CompositionException( final String message, final Throwable throwable )
    {
        super( message, throwable );
    }
}

