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
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;


/**
 * A utility class used to establish a new {@link InitialContext}
 * instance. 
 *
 * <pre>
 * final InitialContextFactory factory = 
 *   new InitialContextFactory();
 * InitialContext context = factory.createInitialContext();
 * </pre>
 * 
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: DefaultInitialContextFactory.java 46177 2004-09-16 13:33:18Z niclas $
 */
public class InitialContextFactory
{
    //------------------------------------------------------------------
    // static 
    //------------------------------------------------------------------

    public static Properties PROPERTIES = getProperties();

    private static InitialContext CONTEXT = new DefaultInitialContext( PROPERTIES );

    //------------------------------------------------------------------
    // state 
    //------------------------------------------------------------------

   /**
    * The initial cache directory.
    */
    private File m_cache;

   /**
    * The timestamping policy.
    */
    private Policy m_timestamp;

   /**
    * The info policy.
    */
    private Monitor m_monitor;

   /**
    * The online connection policy.
    */
    private Boolean m_online;

   /**
    * The debug policy.
    */
    private Boolean m_debug;

   /**
    * The initial remote host names.
    */
    private String[] m_hosts;

    // ------------------------------------------------------------------------
    // InitialContextFactory
    // ------------------------------------------------------------------------

   /**
    * Set the online mode of the repository. The default policy is to 
    * to enable online access to remote repositories.  Setting the onLine
    * mode to false disables remote repository access.   
    *
    * @param policy the connected policy
    */
    public void setOnlineMode( boolean policy )
    {
        m_online = new Boolean( policy );
    }

   /**
    *
    * @param policy the connected policy
    */
    public void setDebugPolicy( boolean policy )
    {
        m_debug = new Boolean( policy );
    }

   /**
    *
    * @param policy the connected policy
    */
    public void setTimestampPolicy( Policy policy )
    {
        m_timestamp = policy;
    }

   /**
    * The cache directory is the directory into which resources 
    * such as jar files are loaded by a repository cache manager.
    *
    * @param cache the repository cache directory
    */
    public void setCacheDirectory( File cache )
    {
        m_cache = cache;
    }

   /**
    * Set the initial hosts to be used by a repository cache manager 
    * implementation and the initial context implementation when 
    * resolving dependent resources.  If is resource is not present
    * in a local cache, remote hosts are checked in the order presented
    * in the supplied list. A host may be a file url or a http url.
    *
    * @param hosts a sequence of remote host urls
    */
    public void setHosts( String[] hosts )
    {
        m_hosts = hosts;
    }

   /**
    * Set the monitor to be used by the repository.
    *
    * @param monitor the repository monitor
    */
    public void setMonitor( Monitor monitor )
    {
        m_monitor = monitor;
    }

   /**
    * Creation of an inital context based on the system and working 
    * directory, parent classloader, repository cache manager implementation
    * artifact, cache directory, and remote hosts sequence supplied to the 
    * factory.
    *
    * @return a new initial context
    */
    public InitialContext createInitialContext() 
    {
        boolean online = getOnlineMode();
        boolean debug = getDebugPolicy();
        Monitor monitor = getMonitor( debug );
        File cache = getCacheDirectory();
        String[] hosts = getHosts();
        Policy timestamp = getTimestampPolicy();

        return new DefaultInitialContext( monitor, cache, hosts, online, timestamp, debug );
    }

    private Monitor getMonitor( boolean debug )
    {
        if( null == m_monitor )
        {
            return new ConsoleMonitor( debug, "context" );
        }
        else
        {
            return m_monitor;
        }
    }

   /**
    * Return the application key.
    */
    private boolean getOnlineMode()
    {
        if( null == m_online )
        {
            return CONTEXT.getOnlineMode();
        }
        else
        {
            return m_online.booleanValue();
        }
    }

   /**
    * Return the debug policy.
    */
    private boolean getDebugPolicy()
    {
        if( null == m_debug )
        {
            return CONTEXT.getDebugPolicy();
        }
        else
        {
            return m_debug.booleanValue();
        }
    }

   /**
    * Return the timestamp policy.
    */
    private Policy getTimestampPolicy()
    {
        if( null == m_timestamp )
        {
            return CONTEXT.getTimestampPolicy();
        }
        else
        {
            return m_timestamp;
        }
    }


   /** 
    * Return the assigned or default cache directory. If undefined
    * the cache directory shall default to ${avalon.home}/repository.
    *
    * @return the cache directory
    */
    private File getCacheDirectory()
    {
        if( null == m_cache )
        {
            return CONTEXT.getCacheDirectory();
        }
        else
        {
            return m_cache;
        }
    }

   /**
    * Return the assigned or default host sequence.
    * @return the remote host url sequence
    */
    public String[] getHosts()
    {
        if( null == m_hosts )
        {
            return CONTEXT.getHosts();
        }
        else
        {
            return m_hosts;
        }
    }

    //------------------------------------------------------------------
    // static utilities
    //------------------------------------------------------------------

   /**
    * Load all properties file from the standard locations.  Standard 
    * locations in priority order include:
    * <ul>
    * <li>${user.dir}/metro.properties</li>
    * <li>${user.home}/metro.properties</li>
    * <li>${metro.home}/metro.properties</li>
    * </ul>
    * @param home the metro home directory
    * @return the aggregated properties
    */
    public static Properties getProperties()
    {
         try
         {
             File home = getHomeDirectory();
             return getProperties( home );
         }
         catch( IOException ioe )
         {
             final String error = 
               "Unexpected error while attempting to construct initial context.";
             throw new InitialContextException( error, ioe );
         }
    }

    private static File getUserDirectory()
    {
        return new File( System.getProperty( "user.home" ) );
    }

    private static File getWorkingDirectory()
    {
        return new File( System.getProperty( "user.dir" ) );
    }


    private static File getHomeDirectory() throws IOException
    {
        String metro = System.getProperty( InitialContext.HOME_KEY );
        if( null != metro )
        {
            return new File( metro );
        }
        metro = Environment.getEnvVariable( InitialContext.HOME_SYMBOL );
        if( null != metro )
        {
            return new File( metro );
        }
        File user = getUserDirectory();
        return new File( user, "." + InitialContext.GROUP );
    }

   /**
    * Load all properties file from the standard locations.  Standard 
    * locations in priority order include:
    * <ul>
    * <li>${user.dir}/metro.properties</li>
    * <li>${user.home}/metro.properties</li>
    * <li>${metro.home}/metro.properties</li>
    * </ul>
    * @param home the metro home directory
    * @return the aggregated properties
    */
    private static Properties getProperties( File home ) throws IOException
    {
        //
        // get ${metro.home}/metro.properties
        //

        Properties properties = new Properties();
        File homePreferenceFile = new File( home, InitialContext.PROPERTY_FILENAME );
        if( homePreferenceFile.exists() )
        {
            InputStream input = new FileInputStream( homePreferenceFile );
            properties.load( input );
        }

        //
        // get ${user.home}/metro.properties
        //

        File user = getUserDirectory();
        File userPreferenceFile = new File( user, InitialContext.PROPERTY_FILENAME );
        if( userPreferenceFile.exists() )
        {
            InputStream input = new FileInputStream( userPreferenceFile );
            properties.load( input );
        }

        //
        // get ${user.dir}/metro.properties
        //

        File dir = getWorkingDirectory();
        File dirPreferenceFile = new File( dir, InitialContext.PROPERTY_FILENAME );
        if( dirPreferenceFile.exists() )
        {
            InputStream input = new FileInputStream( dirPreferenceFile );
            properties.load( input );
        }

        properties.setProperty( InitialContext.HOME_KEY, home.toString() );

        return properties;
    }

}
