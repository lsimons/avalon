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
    * The timestamping policy.
    */
    private final Policy m_timestamp;

   /**
    * The info policy.
    */
    private final Monitor m_monitor;

    // ------------------------------------------------------------------------
    // mutable state
    // ------------------------------------------------------------------------

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

    // ------------------------------------------------------------------------
    // constructors
    // ------------------------------------------------------------------------

    /**
     * Creates an initial context using properties resolved from 
     * the current directory, the users home directory, and METRO_HOME 
     * through aggregration of metro.property files.
     *
     * @exception IOException is an instantiation error occurs
     */
    DefaultInitialContext()
    {
        this( InitialContextFactory.PROPERTIES );
    }

    /**
     * Creates an initial context relative to a supplied application home. 
     * Properties recognized the implementation include:
     * <ul>
     * <li>metro.initial.cache</li>
     * <li>metro.initial.hosts</li>
     * <li>metro.initial.online</li>
     * <li>metro.initial.timestamp</li>
     * <li>metro.initial.debug</li>
     * </ul>
     *
     * @param home the application home directory
     * @param props the initial properties
     * @throws InitialContextException if an error occurs during establishment
     * @throws NullPointerException if the supplied properties are null
     */
    DefaultInitialContext( Properties properties ) 
    {
        if( null == properties )
        {
            throw new NullPointerException( "properties" ); 
        }
        String path = properties.getProperty( HOME_KEY );
        if( null == path )
        {
            throw new NullPointerException( HOME_KEY ); 
        }
        File home = new File( path );

        //
        // handle proxy settings
        //

        String proxy = getProxyHost( properties );
        if( null != proxy )
        {
            int port = getProxyPort( properties );
            String username = getProxyUsername( properties );
            String password = getProxyPassword( properties );
            setupProxy( proxy, port, username, password );
        }

        //
        // setup the initial context state
        //

        m_cache = getCacheDirectory( home, properties );
        m_hosts = getHosts( properties );
        m_online = getOnlineMode( properties );
        m_timestamp = getTimestampPolicy( properties );
        m_debug = getDebugPolicy( properties );

        m_monitor = new ConsoleMonitor( m_debug, "bootstrap" );

        if( m_debug )
        {
             handleTrace();
        }
    }

    /**
     * Creates an initial context.
     *
     * @param cache the cache directory
     * @param hosts a set of initial remote repository addresses 
     * @param online the online policy
     * @param timestamp the timestamp policy
     * @param debug the debug policy
     * @throws NullPointerException the cache or hosts argument is null
     */
    DefaultInitialContext( Monitor monitor, File cache, 
      String[] hosts, boolean online, Policy timestamp, boolean debug ) 
    {
        if( null == cache ) throw new NullPointerException( "cache" ); 
        if( null == hosts ) throw new NullPointerException( "hosts" ); 

        m_cache = cache;
        m_online = online;
        m_timestamp = timestamp;
        m_hosts = hosts;
        m_debug = debug;
        m_monitor = monitor;

        if( debug )
        {
             handleTrace();
        }
    }

    private void handleTrace()
    {
        m_monitor.debug( CACHE_KEY + " = " + m_cache );
        m_monitor.debug( ONLINE_KEY + " = " + m_online );
        m_monitor.debug( TIMESTAMP_KEY + " = " + m_timestamp );
        m_monitor.debug( HOSTS_KEY + " = " + m_hosts.length );
        for( int i=0; i<m_hosts.length; i++ )
        {
             m_monitor.debug( "Host (" + (i+1) + "): " + m_hosts[i] );
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

   /**
    * Return the initial context monitor.
    * @return the context monitor
    */
    public Monitor getMonitor()
    {
        return m_monitor;
    }

    //---------------------------------------------------------------
    // internal
    //---------------------------------------------------------------

    private String getProxyHost( Properties properties )
    {
        String proxy = System.getProperty( PROXY_HOST_KEY );
        if( null != proxy )
           return proxy;
        proxy = properties.getProperty( PROXY_HOST_KEY );
        if( null != proxy )
           return proxy;
        return null;
    }

    private String getProxyUsername( Properties properties )
    {
        String username = System.getProperty( PROXY_USERNAME_KEY );
        if( null != username )
           return username;
        username = properties.getProperty( PROXY_USERNAME_KEY );
        if( null != username )
           return username ;
        return null;
    }

    private String getProxyPassword( Properties properties )
    {
        String password = System.getProperty( PROXY_PASSWORD_KEY );
        if( null != password )
           return password;
        password = properties.getProperty( PROXY_PASSWORD_KEY );
        if( null != password )
           return password ;
        return null;
    }

    private int getProxyPort( Properties properties )
    {
        String port = System.getProperty( PROXY_PORT_KEY );
        if( null != port )
           return Integer.parseInt( port );
        port = properties.getProperty( PROXY_PORT_KEY );
        if( null != port )
           return Integer.parseInt( port );
        return 0;
    }

    private boolean getOnlineMode( Properties properties )
    {
        return getPolicy( properties, ONLINE_KEY, false );
    }

    private Policy getTimestampPolicy( Properties properties ) throws IllegalArgumentException
    {
        try
        {
            String value = properties.getProperty( TIMESTAMP_KEY );
            return Policy.createPolicy( value );
        }
        catch( PolicyException pe )
        {
            throw new IllegalArgumentException( pe.getMessage() );
        }
    }

    private boolean getDebugPolicy( Properties properties )
    {
        return getPolicy( properties, DEBUG_KEY, false );
    }

    private boolean getPolicy( Properties properties, String key, boolean policy )
    {
        String flag = System.getProperty( key );
        if( null != flag )
           return !flag.equals( "false" );
        flag = properties.getProperty( key );
        if( null != flag )
           return !flag.equals( "false" );
        return policy;
    }

    private File getCacheDirectory( File home, Properties properties )
    {
        String cache = System.getProperty( CACHE_KEY );
        if( null != cache )
           return new File( cache );
        cache = properties.getProperty( CACHE_KEY );
        if( null != cache )
           return new File( cache );
        return new File( home, "main" );
    }

    private String[] getHosts( Properties properties )
    {
        String hosts = System.getProperty( HOSTS_KEY );
        if( null != hosts )
           return expandHosts( hosts );
        hosts = properties.getProperty( HOSTS_KEY );
        if( null != hosts )
           return expandHosts( hosts );
        return new String[0];
    }

    private String[] expandHosts( String hosts )
    {
        if( null == hosts )
          return new String[0];
        ArrayList list = new ArrayList();
        StringTokenizer tokenizer = new StringTokenizer( hosts, "," );
        while( tokenizer.hasMoreTokens() )
        {
            list.add( tokenizer.nextToken() );
        }
        return (String[]) list.toArray( new String[0] );
    }

    private void setupProxy( 
      final String host, final int port, final String username, final String password )
    {
        if( null == host ) return;
        Properties system = System.getProperties();
        system.put( "proxySet", "true" );
        system.put( "proxyHost", host );
        system.put( "proxyPort", String.valueOf( port ) );
        if( null != username )
        {
            Authenticator authenticator = 
              new DefaultAuthenticator( username, password );
            Authenticator.setDefault( authenticator );
        }
    }
}
