/*
 * Copyright 1997-2004 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avalon.framework.parameters;

import org.apache.avalon.framework.CascadingException;

/**
 * Thrown when a <code>Parameterizable</code> component cannot be parameterized
 * properly, or if a value cannot be retrieved properly.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.15 $ $Date: 2004/02/11 14:34:25 $
 */
public final class ParameterException
    extends CascadingException
{
    /**
     * Construct a new <code>ParameterException</code> instance.
     *
     * @param message The detail message for this exception.
     */
    public ParameterException( final String message )
    {
        this( message, null );
    }

    /**
     * Construct a new <code>ParameterException</code> instance.
     *
     * @param message The detail message for this exception.
     * @param throwable the root cause of the exception
     */
    public ParameterException( final String message, final Throwable throwable )
    {
        super( message, throwable );
    }
}
