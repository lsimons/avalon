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

package org.apache.metro.transit;

import java.io.IOException;
import java.net.URL;

/**
 * A service that provides access to versioned resources.
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: Repository.java 30977 2004-07-30 08:57:54Z niclas $
 */
public interface Repository
{
   /**
    * Get a resource url relative to the supplied artifact.
    * 
    * @param artifact the artifact describing the resource
    * @return the resource url
    * @exception IOException if the resource is unresolvable
    */
    URL getResource( Artifact artifact ) throws IOException;

   /**
    * Get a plugin class relative to a supplied artifact.
    * 
    * @param parent the parent classloader
    * @param artifact the plugin artifact
    * @return the plugin class
    * @exception IOException if plugin loading exception occurs
    */
    Class getPluginClass( ClassLoader parent, Artifact artifact ) 
       throws IOException;

   /**
    * Creates a Factory from an artifact reference.
    * 
    * @param parent the parent classloader
    * @param artifact the reference to the application
    * @param args commandline arguments
    * @return the plugin instance
    * @exception IOException if plugin loading exception occurs
    */
    public Object getPlugin( ClassLoader parent, Artifact artifact, Object[] args  )
      throws IOException;

}
