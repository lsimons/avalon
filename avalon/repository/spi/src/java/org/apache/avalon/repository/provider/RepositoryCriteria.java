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
import java.util.Map;

/**
 * Interface defining the operations available to manipulate repository
 * factory criteria.
 * 
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.1.2.1 $
 */
public interface RepositoryCriteria extends Map
{
   /**
    * An array of property keys that are used to locate default
    * values.
    * 
    * @see InitialContext#CACHE_KEY
    * @see InitialContext#HOSTS_KEY
    */
    String[] KEYS = 
      new String[]{
        InitialContext.CACHE_KEY,
        InitialContext.HOSTS_KEY };

   /**
    * The cache directory is the directory into which resources 
    * such as jar files are loaded by a repository.
    *
    * @param cache the repository cache directory
    */
    void setCacheDirectory( File cache );

   /**
    * Set the hosts to be used by a repository cache manager 
    * implementation and the initial context implementation when 
    * resolving dependent resources.  If is resource is not present
    * in a local cache, remote hosts are checked in the order presented
    * in the supplied list. A host may be a file url or a http url.
    *
    * @param hosts a sequence of remote host urls
    */
    void setHosts( String[] hosts );
}
