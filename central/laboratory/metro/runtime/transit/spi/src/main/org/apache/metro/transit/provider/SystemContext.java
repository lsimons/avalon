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

package org.apache.metro.transit.provider;

import java.io.File;

import org.apache.metro.transit.InitialContext;
import org.apache.metro.transit.Repository;
import org.apache.metro.logging.Logger;
import org.apache.metro.logging.provider.LoggingManager;

/**
 * The controller class manages the deployment of a set of plugins.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: Artifact.java 30977 2004-07-30 08:57:54Z niclas $
 */
public interface SystemContext 
{
    static final String GROUP = InitialContext.GROUP;
    static final String DIR_KEY = GROUP + ".dir";
    static final String LOGGING_IMPLEMENTATION_KEY = GROUP + ".logging.implementation";
    static final String LOGGING_CONFIGURATION_KEY = GROUP + ".logging.configuration.path";

    static final String HOME_KEY =           GROUP + ".home";
    static final String CACHE_KEY =          GROUP + ".cache";
    static final String HOSTS_KEY =          GROUP + ".hosts";
    static final String ONLINE_KEY =         GROUP + ".online";
    static final String TIMESTAMP_KEY =      GROUP + ".timestamp";
    static final String INFO_KEY =           GROUP + ".info";
    static final String DEBUG_KEY =          GROUP + ".debug";

    static final String[] KEYS = 
      new String[]{ 
        DIR_KEY, 
        LOGGING_IMPLEMENTATION_KEY,
        LOGGING_CONFIGURATION_KEY,
        HOME_KEY,
        CACHE_KEY,
        HOSTS_KEY,
        ONLINE_KEY,
        TIMESTAMP_KEY,
        INFO_KEY,
        DEBUG_KEY
      };

   /**
    * Return the working directory from which containers may establish
    * persistent content between sessions.
    *
    * @return the working directory
    */
    File getWorkingDirectory();

   /**
    * Return the temp directory from which containers may establish
    * transient non-persistent content.
    *
    * @return the temp directory
    */
    File getTempDirectory();

   /**
    * Return the info status flag.  If TRUE plugins should list information
    * concerning initialization parameters during establishment.
    * 
    * @return the info policy flag
    */
    boolean getInfoPolicy();

   /**
    * Return the system wide logging manager.
    *
    * @return the logging manager.
    */
    LoggingManager getLoggingManager();

   /**
    * Return the initial context.
    *
    * @return the repository inital context.
    */
    InitialContext getInitalContext();

   /**
    * Return the application repository cache controller.
    *
    * @return the repository
    */
    Repository getRepository();
}

