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

package org.apache.avalon.activation.appliance;

/**
 * Exception raised in response to a fatal assembly failure.
 * If this exception is thrown, it is probable that the entire
 * JVM is not stable, and the container should terminate execution
 * completely.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.3 $ $Date: 2004/01/24 23:25:20 $
 */
public final class FatalDeploymentException extends DeploymentException
{

    /**
     * Construct a new <code>FatalDeploymentException</code> instance.
     *
     * @param message The detail message for this exception.
     */
    public FatalDeploymentException( final String message )
    {
        this( message, null );
    }

    /**
     * Construct a new <code>FatalDeploymentException</code> instance.
     *
     * @param message The detail message for this exception.
     * @param throwable the root cause of the exception
     */
    public FatalDeploymentException( final String message, final Throwable throwable )
    {
        super( message, throwable );
    }
}

