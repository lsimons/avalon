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

package org.apache.avalon.logging.provider;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.avalon.repository.Artifact;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.Logger;

/**
 * DefaultLoggingCriteria is a class holding the values supplied by a user 
 * for application to a LoggingManager factory.
 *
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.2 $
 */
public interface LoggingCriteria extends Map
{
    //--------------------------------------------------------------
    // criteria keys
    //--------------------------------------------------------------

   /**
    * The logging artifact key.
    */
    String FACTORY_ARTIFACT_KEY = 
      "avalon.logging.factory.artifact";

   /**
    * The logging configuration key.
    */
    String LOGGING_CONFIGURATION_KEY = 
      "avalon.logging.configuration";

   /**
    * The logging system bootstrap logger key.
    */
    String LOGGING_BOOTSTRAP_KEY = 
      "avalon.logging.bootstrap";

   /**
    * The key of a the criteria argument that declares the base 
    * directory to be used for persistent content. For example,
    * logging file should be created in the directory 
    * assigned to this key.
    */
    String LOGGING_BASEDIR_KEY = 
      "avalon.logging.basedir";

   /**
    * Debug mode.
    */
    String LOGGING_DEBUG_KEY = 
      "avalon.logging.debug";

    //--------------------------------------------------------------
    // operations
    //--------------------------------------------------------------

   /**
    * Set the bootstrap logging channel
    * @param logger the boootstrap logging channel
    */
    void setBootstrapLogger( Logger logger );

   /**
    * Set the base directory for logging resources.
    * @param dir the base directory
    */
    void setBaseDirectory( File dir );

   /**
    * Set the artifact referencing the implementation factory.
    * @param artifact the implementation artifact
    */
    void setFactoryArtifact( Artifact artifact );

   /**
    * Set the logging system configuration
    * @param config the configuration
    */
    void setConfiguration( Configuration config );

   /**
    * Set the debug enabled policy
    * @param mode TRUE to enabled debug mode else FALSE
    */
    void setDebugEnabled( boolean mode );

   /**
    * Get the bootstrap logging channel
    * @return the boootstrap logging channel
    */
    Logger getBootstrapLogger();

   /**
    * Return the base directory for logging resources.
    * @return the base directory
    */
    File getBaseDirectory();

   /**
    * Return the logging system configuration
    * @return the configuration
    */
    Configuration getConfiguration();

   /**
    * Return the artifact reference to the logging implementation factory .
    * @return the logging implementation factory classname 
    */
    Artifact getFactoryArtifact() throws IOException;

   /**
    * Return debug policy.  If TRUE all logging channels will be 
    * set to debug level.
    *
    * @return the debug policy
    */
    public boolean isDebugEnabled();

}
