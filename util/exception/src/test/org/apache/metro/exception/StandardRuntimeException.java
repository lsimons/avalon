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

package org.apache.metro.exception;

/**
 * RuntimeException containing a causal exception.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: StandardRuntimeException.java 30977 2004-07-30 08:57:54Z niclas $
 */
public class StandardRuntimeException
    extends RuntimeException
{
    private final Throwable m_throwable;

    /**
     * Construct a new <code>StandardRuntimeException</code> instance.
     *
     * @param message The detail message for this exception.
     */
    public StandardRuntimeException( final String message )
    {
        this( message, null );
    }

    /**
     * Construct a new <code>StandardRuntimeException</code> instance.
     *
     * @param message The detail message for this exception.
     * @param throwable the root cause of the exception
     */
    public StandardRuntimeException( final String message, final Throwable throwable )
    {
        super( message );
        m_throwable = throwable;
    }

    /**
     * Retrieve root cause of the exception.
     *
     * @return the root cause
     */
    public final Throwable getCause()
    {
        return m_throwable;
    }
}
