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

import java.net.URL;
import java.io.File;
import java.util.jar.Manifest;

import org.apache.avalon.repository.Artifact;
import org.apache.avalon.repository.Repository;
import org.apache.avalon.repository.RepositoryException;


/**
 * The initial context established by an initial repository factory.
 *
 * @author <a href="mailto:mcconnell@osm.net">Stephen McConnell</a>
 * @version $Revision: 1.10 $ $Date: 2004/02/19 07:37:47 $
 */
public interface InitialContext 
{        
   /**
    * The property key used when resolving the default implementation
    * artifact spec.
    */
    String IMPLEMENTATION_KEY = "avalon.repository.implementation";

   /**
    * The property key used when resolving the default cache directory.
    */
    String CACHE_KEY = "avalon.repository.cache";

   /**
    * The property key used when evaluating the default remote hosts.  The
    * value assigned to this property is a comma seperated list of urls.
    */
    String HOSTS_KEY = "avalon.repository.hosts";

    String PROXY_HOST_KEY = "avalon.repository.proxy.host";
    String PROXY_PORT_KEY = "avalon.repository.proxy.port";
    String PROXY_USERNAME_KEY = "avalon.repository.proxy.username";
    String PROXY_PASSWORD_KEY = "avalon.repository.proxy.password";

    String LINE = 
      "\n-----------------------------------------------------------";

   /**
    * Return the application key.  The value of the key may be used 
    * to resolve property files by using the convention 
    * [key].properties.
    * 
    * @return the application key.
    */
    String getApplicationKey();

   /**
    * <p>Get the value of a property. If the property value does not 
    * exists a null value will be returned.  Property value resolution
    * shall take into account the following ordered property sources
    * (resolved relative to the application key):</p>
    * <ul>
    * <li>system properties</li>
    * <li>working directory properties</li>
    * <li>user properties</li>
    * <li>application properties</li>
    * </ul>
    *
    * @return the property value
    */
    String getProperty( String key );

    /**
     * Return cache root directory.
     * 
     * @return the cache directory
     */
    File getInitialCacheDirectory();

    /**
     * Return the base working directory.
     * 
     * @return the base directory
     */
    File getInitialWorkingDirectory();

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
    * Return the repository.
    * @return the repository
    */
    Repository getRepository();

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

   /**
    * Install a block archive into the repository.
    * @param url the block archive url
    * @return the block manifest
    */
    Manifest install( URL url ) throws RepositoryException;

}
