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

import org.apache.avalon.repository.Repository;
import org.apache.avalon.repository.RepositoryException;

/**
 * A service that provides write access to a cache and support
 * for repository creation.
 *
 * @author <a href="mailto:mcconnell@osm.net">Stephen McConnell</a>
 * @version $Revision: 1.4 $ $Date: 2004/01/24 23:20:05 $
 */
public interface CacheManager
{        
    /**
     * Return cache root directory.
     * 
     * @return the cache directory
     */
    File getCacheDirectory();

    /**
     * Install a block archive into the repository.
     * @param url the block archive url
     * @return the block manifest
     */
    BlockManifest install( URL url, StringBuffer buffer ) 
        throws RepositoryException ;

   /**
    * Creation of a new repository handler using the default hosts.
    * @return the repository
    */
    Repository createRepository();

   /**
    * Creation of a new repository handler.
    * @param hosts the set of hosts to assign to the repository
    * @return the repository
    */
    Repository createRepository( String[] hosts );

}
