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

import java.io.File;

import org.apache.avalon.logging.provider.LoggingManager;

import org.apache.avalon.repository.Repository;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;

/**
 * Defintion of a system context that exposes a system wide set of parameters.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.6 $ $Date: 2004/02/07 14:03:42 $
 */
public interface SystemContext extends Context
{
   /**
    * Return the model factory.
    *
    * @return the factory
    */
    ModelFactory getModelFactory();

   /**
    * Return the runtime factory.
    *
    * @return the factory
    */
    RuntimeFactory getRuntimeFactory();

   /**
    * Return the base directory from which relative references 
    * should be resolved.
    *
    * @return the base directory
    */
    File getBaseDirectory();

   /**
    * Return the home directory from which containers may establish
    * persistent content.
    *
    * @return the working directory
    */
    File getHomeDirectory();

   /**
    * Return the temp directory from which containers may establish
    * non-persistent content.
    *
    * @return the temp directory
    */
    File getTempDirectory();

   /**
    * Return the application repository from which resource 
    * directives can be resolved.
    *
    * @return the repository
    */
    Repository getRepository();

   /**
    * Return the system trace flag.
    *
    * @return the trace flag
    */
    boolean isTraceEnabled();

   /**
    * Return the system classloader.
    *
    * @return the system classloader
    */
    ClassLoader getSystemClassLoader();

   /**
    * Return the system classloader.
    *
    * @return the system classloader
    */
    ClassLoader getCommonClassLoader();

   /**
    * Return the logging manager.
    *
    * @return the logging manager.
    */
    LoggingManager getLoggingManager();

   /**
    * Return the system logging channel.
    *
    * @return the system logging channel
    */
    Logger getLogger();

   /**
    * Return the default deployment phase timeout value.
    * @return the timeout value
    */
    long getDefaultDeploymentTimeout();

   /**
    * Return the enabled status of the code security policy.
    * @return the code security enabled status
    */
    boolean isCodeSecurityEnabled();
}
