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

package org.apache.avalon.repository.provider;

import java.io.File;

import org.apache.avalon.repository.Artifact;
import org.apache.avalon.repository.RepositoryException;


/**
 * The initial context established by an initial repository factory.
 *
 * @author <a href="mailto:mcconnell@osm.net">Stephen McConnell</a>
 * @version $Revision: 1.6 $ $Date: 2004/01/25 13:17:51 $
 */
public interface InitialContext
{        
   /**
    * The property key used when resolving the default cache directory.
    */
    String CACHE_KEY = "avalon.repository.cache";

   /**
    * The property key used when evaluating the default remote hosts.  The
    * value assigned to this property is a comma seperated list of urls.
    */
    String HOSTS_KEY = "avalon.repository.hosts";

    /**
     * Return the base working directory.
     * 
     * @return the base directory
     */
    File getInitialWorkingDirectory();

    /**
     * Return cache root directory.
     * 
     * @return the cache directory
     */
    File getInitialCacheDirectory();
    
    /**
     * Return the initial set of host names.
     * @return the host names sequence
     */
    String[] getInitialHosts();

   /**
    * Return the initial repository factory.
    * @return the initial repository factory
    */
    Factory getInitialFactory();

   /**
    * Create a factory builder using a supplied artifact.
    * @param artifact the factory artifact
    * @return the factory builder
    * @exception Exception if a builder creation error occurs
    */
    Builder newBuilder( Artifact artifact )
      throws Exception;

   /**
    * Create a factory builder using a supplied artifact.
    * @param classloader the parent classloader
    * @param artifact the factory artifact
    * @return the factory
    * @exception Exception if a factory creation error occurs
    */
    Builder newBuilder( ClassLoader classloader, Artifact artifact )
      throws Exception;

}
