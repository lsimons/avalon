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

package org.apache.metro.transit.plugin;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Authenticator;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.JarURLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.jar.Manifest;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.naming.directory.Attributes;

import org.apache.metro.logging.Logger;
import org.apache.metro.transit.Artifact;
import org.apache.metro.transit.InitialContext;
import org.apache.metro.transit.RepositoryException;
import org.apache.metro.transit.Monitor;
import org.apache.metro.transit.Policy;


/**
 * Sets up the environment to create repositories by downloading the required 
 * jars, preparing a ClassLoader and delegating calls to repository factory 
 * methods using the newly configured ClassLoader.
 * 
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: DefaultInitialContext.java 30977 2004-07-30 08:57:54Z niclas $
 */
class DefaultInitialContext implements InitialContext
{

    //------------------------------------------------------------------
    // immutable state 
    //------------------------------------------------------------------

   /**
    * The initial cache directory.
    */
    private final File m_cache;

   /**
    * The info policy.
    */
    private final boolean m_info;

   /**
    * The server policy.
    */
    private final boolean m_server;


    // ------------------------------------------------------------------------
    // mutable state
    // ------------------------------------------------------------------------

   /**
    * The timestamping policy.
    */
    private Policy m_timestamp;

   /**
    * The online connection policy.
    */
    private boolean m_online;

   /**
    * The debug policy.
    */
    private boolean m_debug;

   /**
    * The initial remote host names.
    */
    private String[] m_hosts;

    private Monitor m_monitor;

    // ------------------------------------------------------------------------
    // constructors
    // ------------------------------------------------------------------------

    /**
     * Creates an initial context.
     *
     * @param cache the cache directory
     * @param hosts a set of initial remote repository addresses 
     * @param online the online policy
     * @param timestamp the timestamp policy
     * @param server the server policy
     * @param info the info flag
     * @param debug the debug flag
     * @throws RepositoryException if an error occurs during establishment
     */
    public DefaultInitialContext( Monitor monitor, File cache, 
      String[] hosts, boolean online, Policy timestamp, boolean server, boolean info, boolean debug ) 
      throws RepositoryException
    {
        if( null == cache ) throw new NullPointerException( "cache" ); 
        if( null == hosts ) throw new NullPointerException( "hosts" ); 

        m_cache = cache;
        m_online = online;
        m_timestamp = timestamp;
        m_hosts = hosts;
        m_debug = debug;
        m_info = info;
        m_server = server;
        m_monitor = monitor;

        m_monitor.debug( "cache: " + cache );
        m_monitor.debug( "online policy: " + online );
        m_monitor.debug( "timestamp policy: " + timestamp );
        for( int i=0; i<hosts.length; i++ )
        {
             m_monitor.debug( "Host (" + (i+1) + "): " + hosts[i] );
        }
    }

    // ------------------------------------------------------------------------
    // InitialContext
    // ------------------------------------------------------------------------

   /**
    * Get the online mode of the repository.
    *
    * @return the online mode
    */
    public boolean getOnlineMode()
    {
        return m_online;
    }

   /**
    * Get the info policy.
    *
    * @return the info policy
    */
    public boolean getInfoPolicy()
    {
        return m_info;
    }

   /**
    * Get the debug policy.
    *
    * @return the debug policy
    */
    public boolean getDebugPolicy()
    {
        return m_debug;
    }

   /**
    * Get the timestamp policy.
    *
    * @return the timestamp policy
    */
    public Policy getTimestampPolicy()
    {
        return m_timestamp;
    }

    /**
     * Return cache root directory.
     * 
     * @return the cache directory
     */
    public File getCacheDirectory()
    {
        return m_cache;
    }
    
    /**
     * Return the initial set of host names.
     * @return the host names sequence
     */
    public String[] getHosts()
    {
        return m_hosts;
    }

    public Monitor getMonitor()
    {
        return m_monitor;
    }
}
