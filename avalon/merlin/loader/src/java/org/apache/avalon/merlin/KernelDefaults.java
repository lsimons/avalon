/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.avalon.merlin ;


import java.io.File ;
import java.io.IOException ;
import java.io.InputStream ;
import java.io.FileInputStream ;
import java.net.Authenticator ;
import java.net.PasswordAuthentication ;
import java.util.ArrayList ;
import java.util.Properties ;
import java.util.Enumeration ;

import org.apache.avalon.util.env.Env ;
import org.apache.avalon.util.env.EnvAccessException ;
import org.apache.avalon.repository.ProxyContext ;


/**
 * Contains search policy for discovering default values to kernel parameters.
 * This involves searching classpaths, finding variables in the environment in
 * an operating system dependent fashion, and finding variables within the 
 * system properties.  The expansion of macro values for properties are also
 * handled by this class.
 * 
 * @todo document the property keys and make sure they reflect those defined by
 * Steve in his emails
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $Author: akarasulu $
 * @version $Revision: 1.8 $
 */
public class KernelDefaults
{
    // ------------------------------------------------------------------------
    // Environment Variable Names 
    // ------------------------------------------------------------------------

    /** Merlin home environment variable name */
    public static final String MERLIN_HOME_ENV = "MERLIN_HOME" ;
    /** Merlin local application repository environment variable name */
    public static final String MERLIN_REPO_LOCAL_ENV = "MERLIN_REPO_LOCAL" ;


    // ------------------------------------------------------------------------
    // Single Valued Kernel Configuration Properties
    // ------------------------------------------------------------------------

    /** Array for all single valued property keys */
    private static final String [] s_keys =
    {
        "merlin.policy.server",                     // 0 IS_SERVER_KEY
        "merlin.policy.info",                       // 1 IS_INFO_KEY
        "merlin.policy.debug",                      // 2 IS_DEBUG_KEY
        "merlin.repository.application.path",       // 3 APPLICATION_REPO_KEY
        "merlin.repository.system.path",            // 4 SYSTEM_REPO_KEY 
        "merlin.work.path",                         // 5 HOME_PATH_KEY
        "merlin.config.url",                        // 6 CONFIG_URL_KEY
        "merlin.kernel.url",                        // 7 KERNEL_URL_KEY
        "java.io.tmpdir",                           // 8 TEMP_PATH_KEY
        "merlin.library.path",                      // 9 LIBRARY_PATH_KEY
        "merlin.proxy.host",                        // 10 MERLIN_PROXY_HOST      
        "merlin.proxy.port",                        // 11 MERLIN_PROXY_PORT      
        "merlin.proxy.username",                    // 12 MERLIN_PROXY_USERNAME  
        "merlin.proxy.password"                     // 13 MERLIN_PROXY_PASSWORD
    } ;
    
    
    /** Property to determine if the kernel runs in server mode */
    public static final String IS_SERVER_KEY = s_keys[0] ;
    /** Property to determine if all logging channels log at the info level */
    public static final String IS_INFO_KEY = s_keys[1] ;
    /** Property to determine if all logging channels log debugging info */
    public static final String IS_DEBUG_KEY = s_keys[2] ;
    /**
     * The key to the root directory for the local application repository.  This
     * directory will be used during the retrival of resources such as jar files
     * referenced by block include directives and classloader resource 
     * references.
     */
    public static final String APPLICATION_REPO_KEY = s_keys[3] ;
    public static final String SYSTEM_REPO_KEY = s_keys[4] ;
    /**
     * The key to the working base directory.  Component working directories
     * are created relative to this root directory.
     */
    public static final String HOME_PATH_KEY = s_keys[5] ;
    /** url to the config.xml */     
    public static final String CONFIG_URL_KEY = s_keys[6] ;
    /** The key to a kernel configuration. */
    public static final String KERNEL_URL_KEY = s_keys[7] ;
    /** A temporary directory */
    public static final String TEMP_PATH_KEY = s_keys[8] ;
    /**
     * The key to the directory used as the base anchor for resolution of 
     * relative path references for jar extension library directory statements 
     * in classloader directives.
     */
    public static final String LIBRARY_PATH_KEY = s_keys[9] ;
    /** the remote repository proxy port */
    public static final String MERLIN_PROXY_HOST = s_keys[10] ;
    /** the remote repository proxy host */
    public static final String MERLIN_PROXY_PORT = s_keys[11] ;
    /** the remote repository proxy username if required */
    public static final String MERLIN_PROXY_USERNAME = s_keys[12] ;
    /** the remote repository proxy password if required */
    public static final String MERLIN_PROXY_PASSWORD = s_keys[13] ;

    // ------------------------------------------------------------------------
    // Multivalued Kernel Configuration Property Bases
    // ------------------------------------------------------------------------

    /** key base used for enumerating the remote repositories */
    public static final String REMOTE_REPO_KEYBASE =
        "merlin.repository.remote.url" ;
    /** key base used for enumerating block.xml file urls */
    public static final String BLOCK_URLS_KEYBASE =
        "merlin.kernel.blockurl" ;

    // ------------------------------------------------------------------------
    // Misc. Constants
    // ------------------------------------------------------------------------

    /** file name base for the Merlin default properties using in phase I */
    public static final String MERLIN_FILE_BASE =
        "merlin.properties" ;
    
    // ------------------------------------------------------------------------
    // Static Variables, Blocks and Functions
    // ------------------------------------------------------------------------
     
    /** Overlay of system wide default properties discovered */
    private static final Properties s_defaults = new Properties() ;
     

    /*
     * Discover and cache defaults first then apply macro expansion right after 
     * this class is loaded into the VM.
     */
    static 
    {
        discoverDefaults( s_defaults ) ;
        macroExpand( s_defaults, null ) ;
    }
    
    /**
     * Discovers default values for the kernel configuration.  Not fail fast 
     * meaning if it fails at one stage it continues to gather defaults rather 
     * than bombing out.
     * 
     * Note that discovery does not entail macro expansion and that property 
     * value overrides downstream of a macro will overwrite macros before they
     * have a chance to expand.
     * 
     * @param a_props the Properties to populate with discovered defaults
     */
    private static void discoverDefaults( Properties a_props )
    {
        // --------------------------------------------------------------------
        //
        // Stage I - load the default properties bundled with this
        // jar under the resource name MERLIN_FILE_BASE.  This set the 
        // primative defaults.
        //
        // --------------------------------------------------------------------
        
        InputStream l_in = 
            KernelDefaults.class.getResourceAsStream( MERLIN_FILE_BASE ) ;
        
        try
        {
            a_props.load( l_in ) ;
        }
        catch ( IOException e )
        {
            e.printStackTrace( System.err ) ;
        }
        
        //
        // If no library path is defined and the user directory is unknown as
        // is the case in environments like a servlet container then we default
        // to the the first root within the file system.
        //

        String l_userDir = System.getProperty( "user.dir" ) ;
        if ( null == l_userDir && null == getLibraryPath() )
        {
            File [] l_roots = File.listRoots() ; 
            l_userDir = l_roots[0].getAbsolutePath() ;
            a_props.setProperty( LIBRARY_PATH_KEY, l_userDir ) ;
        }        
        
        // --------------------------------------------------------------------
        //
        // Stage II - get some properties from the environment.
        //
        // --------------------------------------------------------------------

        File l_merlinHome = getMerlinHome() ;
        
        if ( null != l_merlinHome && l_merlinHome.exists() )
        {    
            File l_sysRepo = new File( l_merlinHome, "system" ) ;
            if ( l_sysRepo.exists() )
            {    
                a_props.setProperty( SYSTEM_REPO_KEY, 
                    l_sysRepo.getAbsolutePath() ) ;
            }
            
            File l_applRepo = getMerlinApplicationRepository( l_merlinHome ) ;

            if ( l_applRepo.exists() )
            {
                a_props.setProperty( APPLICATION_REPO_KEY, 
                    l_applRepo.getAbsolutePath() ) ;
            }
        }
        
        // --------------------------------------------------------------------
        //
        // Stage III - check for overriding values of .merlin.properties within
        // the user's home directory.
        //
        // --------------------------------------------------------------------

        String l_userHome = System.getProperty( "user.home" ) ;
        File l_userProps = new File( l_userHome, "." + MERLIN_FILE_BASE ) ;
        if ( l_userProps.exists() )
        {
            try 
            {
                a_props.load( new FileInputStream( l_userProps ) ) ;
            }
            catch ( IOException e )
            {
                e.printStackTrace( System.err ) ;
            }
        }
        
        // --------------------------------------------------------------------
        //
        // Stage IV - Check for overriding values within the System properties.
        //
        // --------------------------------------------------------------------

        for( int ii = 0; ii < s_keys.length; ii++ )
        {
            /*
             * If a_props has the property use it as default in case property
             * does not exist in the system properties.
             */
            String l_default = System.getProperty( s_keys[ii], 
                    a_props.getProperty( s_keys[ii] ) ) ;
            if ( null != l_default )
            {    
                a_props.setProperty( s_keys[ii], l_default ) ;
            }
        }

        //
        // Now we overlay all the block properties defined
        //

        Enumeration l_list = System.getProperties().keys() ;
        while ( l_list.hasMoreElements() )
        {
            String l_key = ( String ) l_list.nextElement() ;
            if ( l_key.startsWith( BLOCK_URLS_KEYBASE ) )
            {
                String l_default = System.getProperty( l_key,
                        a_props.getProperty( l_key ) ) ;
                
                if ( null != l_default )
                {
                    a_props.setProperty( l_key, l_default ) ;
                }
            }
        }

        //
        // Now we overlay all the remote repo url properties defined
        //

        l_list = System.getProperties().keys() ;
        while ( l_list.hasMoreElements() )
        {
            String l_key = ( String ) l_list.nextElement() ;
            if ( l_key.startsWith( REMOTE_REPO_KEYBASE ) )
            {
                String l_default = System.getProperty( l_key,
                        a_props.getProperty( l_key ) ) ;
                
                if ( null != l_default )
                {
                    a_props.setProperty( l_key, l_default ) ;
                }
            }
        }
    }

    
    /**
     * Expands out a set of property key macros in the following format 
     * ${foo.bar} where foo.bar is a property key, by dereferencing the value 
     * of the key using the original source Properties and other optional 
     * Properties.
     * 
     * If the original expanded Properties contain the value for the macro key 
     * foo.bar then dereferencing stops by using the value in the expanded 
     * Properties: the other optional Properties are NOT used at all.
     * 
     * If the original expanded Properties do NOT contain the value for the 
     * macro key, then the optional Properties are used in order.  The first of
     * the optionals to contain the value for the macro key (foo.bar) shorts the
     * search.  Hence the first optional Properties in the array to contain a 
     * value for the macro key (foo.bar) is used to set the expanded value.
     * 
     * If a macro cannot be expanded because it's key was not defined within the 
     * expanded Properties or one of the optional Properties then it is left as
     * is.
     * 
     * @param a_expanded the Properties to perform the macro expansion upon
     * @param a_optionals null or an optional set of Properties to use for 
     * dereferencing macro keys (foo.bar)
     */
    public static void macroExpand( Properties a_expanded, 
                                    Properties [] a_optionals )
    {
        // Handle null optionals
        if ( null == a_optionals )
        {
            a_optionals = new Properties [ 0 ] ;
        }
        
        Enumeration l_list = a_expanded.propertyNames() ;
        while ( l_list.hasMoreElements() )
        {
            String l_key = ( String ) l_list.nextElement() ;
            String l_macro = a_expanded.getProperty( l_key ) ;
            
            /*
             * Skip all regular properties that are not macros: used de Morgans
             * to convert ! l_key.startsWith( "${" ) && l_key.endsWith( "}" )
             * to conditional below since it is faster.
             */
            if ( ! l_macro.startsWith( "${" ) || ! l_macro.endsWith( "}" ) )
            {
                continue ;
            }
            
            /*
             * Check if the macro key exists within the expanded Properties. If
             * so we continue onto the next macro skipping the optional props.
             */
            String l_macroKey = l_macro.substring( 2, l_macro.length() - 1 ) ;
            if ( a_expanded.containsKey( l_macroKey ) )
            {
                a_expanded.put( l_key, a_expanded.getProperty( l_macroKey ) ) ;
                continue ;
            }
            
            /*
             * Check if the macro key exists within the array of optional 
             * Properties.  Set expanded value to first Properties with the 
             * key and break out of the loop.
             */
            for ( int ii = 0; ii < a_optionals.length; ii++ )
            {
                if ( a_optionals[ii].containsKey( l_macroKey ) )
                {
                    String l_value = a_optionals[ii].getProperty( l_macroKey ) ;
                    a_expanded.put( l_key, l_value ) ;
                    break ;
                }
            }
        }
    }
    
    /**
     * Gets a default value for the server flag property for the KernelConfig.
     * 
     * @return true if the IS_SERVER_KEY property value is set to 1, true, yes 
     * or on, and false otherwise. 
     * @see org.apache.avalon.merlin.kernel.KernelConfig#isServer()
     */
    public static boolean isServer()
    {
        return getBoolean( IS_SERVER_KEY ) ;
    }
    

    /**
     * Gets a default value for the debug flag.
     * 
     * @see org.apache.avalon.merlin.kernel.KernelConfig#isDebugEnabled()
     */
    public static boolean isDebugEnabled()
    {
        return getBoolean( IS_DEBUG_KEY ) ;
    }

    
    /**
     * Gets a default value for the info flag.
     * 
     * @see org.apache.avalon.merlin.kernel.KernelConfig#isInfoEnabled()
     */
    public static boolean isInfoEnabled()
    {
        return getBoolean( IS_INFO_KEY ) ;
    }

    
    /**
     * Gets a default value for the remote repositories.
     * 
     * @see org.apache.avalon.merlin.kernel.KernelConfig#
     * getRemoteRepositoryUrls()
     */
    public static String[] getRemoteRepositoryUrls()
    {
        String [] l_urls = null ;
        ArrayList l_urlArray = new ArrayList() ;
        Enumeration l_list = s_defaults.keys() ;

        while ( l_list.hasMoreElements() )
        {
            String l_key = ( String ) l_list.nextElement() ;
            if ( l_key.startsWith( REMOTE_REPO_KEYBASE ) )
            {
                l_urlArray.add( s_defaults.getProperty( l_key ) ) ;
            }
        }
        return ( String [] ) l_urlArray.toArray( new String [0] ) ;
    }

    
    /**
     * Gets the default value for the application repository path.
     *  
     * @see org.apache.avalon.merlin.kernel.KernelConfig#
     * getApplicationRepositoryPath()
     */
    public static String getApplicationRepositoryPath()
    {
        return s_defaults.getProperty( APPLICATION_REPO_KEY ) ;
    }

    
    /**
     * Gets the default value for the system repository path.
     * 
     * @see org.apache.avalon.merlin.kernel.KernelConfig#
     * getSystemRepositoryPath()
     */
    public static String getSystemRepositoryPath()
    {
        return s_defaults.getProperty( SYSTEM_REPO_KEY ) ;
    }

    
    /**
     * Gets the default path to optional extention librarys.
     * 
     * @see org.apache.avalon.merlin.kernel.KernelConfig#getLibraryPath()
     */
    public static String getLibraryPath()
    {
        return s_defaults.getProperty( LIBRARY_PATH_KEY ) ;
    }


    /**
     * Gets the path to the instance home or working directory.
     * 
     * @see org.apache.avalon.merlin.kernel.KernelConfig#getHomePath()
     */
    public static String getHomePath()
    {
        return s_defaults.getProperty( HOME_PATH_KEY ) ;
    }


    /**
     * Gets the path to a temporary directory.
     * 
     * @see org.apache.avalon.merlin.kernel.KernelConfig#getTempPath()
     */
    public static String getTempPath()
    {
        return s_defaults.getProperty( TEMP_PATH_KEY ) ;
    }


    /**
     * Gets the path to the server configuration file or config.xml.
     * 
     * @see org.apache.avalon.merlin.kernel.KernelConfig#getConfigUrl()
     */
    public static String getConfigUrl()
    {
        return s_defaults.getProperty( CONFIG_URL_KEY ) ;
    }


    /**
     * Gets the set of block.xml file urls for components that are setup by the 
     * kernel.  Note that properties are enumerated off of the base key using 
     * dot number notation like base.1, base.2, ... , base.n.
     * 
     * @see org.apache.avalon.merlin.kernel.KernelConfig#getBlockUrls()
     */
    public static String[] getBlockUrls()
    {
        String [] l_urls = null ;
        ArrayList l_urlArray = new ArrayList() ;
        Enumeration l_list = s_defaults.keys() ;

        while ( l_list.hasMoreElements() )
        {
            String l_key = ( String ) l_list.nextElement() ;
            if ( l_key.startsWith( BLOCK_URLS_KEYBASE ) )
            {
                l_urlArray.add( s_defaults.getProperty( l_key ) ) ;
            }
        }

        return ( String [] ) l_urlArray.toArray( new String [0] ) ;
    }


    /**
     * Gets the url to the kernel configuration xml file.
     * 
     * @see org.apache.avalon.merlin.kernel.KernelConfig#getKernelUrl()
     */
    public static String getKernelUrl()
    {
        return s_defaults.getProperty( KERNEL_URL_KEY ) ;
    }
    
    /**
     * Creates and returns a new ProxyContext every time using default property
     * settings.  If criticial proxy parameters are not set then null is 
     * returned.
     * 
     * @return null if defaults do not exist or a ProxyContext representing 
     * default values
     */
    public static ProxyContext getProxyContext()
    {
        String l_host = s_defaults.getProperty( MERLIN_PROXY_HOST ) ;
        String l_port = s_defaults.getProperty( MERLIN_PROXY_PORT ) ;
        
        if ( null == l_host || null == l_port )
        {
            return null ;
        }
        
        Authenticator l_authenticator = new Authenticator()
        {
            protected PasswordAuthentication getPasswordAuthentication()
            {
                PasswordAuthentication l_pwdAuth = new PasswordAuthentication(
                    s_defaults.getProperty( MERLIN_PROXY_USERNAME ),
                    s_defaults.getProperty( MERLIN_PROXY_PASSWORD )
                        .toCharArray() ) ;
                return l_pwdAuth ;
            }
        } ;
        
        ProxyContext l_ctx = new ProxyContext( l_host, Integer.parseInt( 
            s_defaults.getProperty( MERLIN_PROXY_PORT ) ), l_authenticator ) ;
        return l_ctx ;
    }

    // ------------------------------------------------------------------------
    // Private Utility Methods
    // ------------------------------------------------------------------------

    
    /**
     * Utility method that gets a key's value and returns a boolean value to 
     * represent it.
     * 
     * @param a_key the boolean property key
     * @return true if the property is 1, true, yes or on, and false otherwise 
     */
    private static boolean getBoolean( String a_key )
    {
        String l_value = s_defaults.getProperty( a_key ) ;
        l_value = l_value.trim().toLowerCase() ;
        
        if ( l_value.equals( "1" )       ||
             l_value.equals( "on" )      ||
             l_value.equals( "yes" )     ||
             l_value.equals( "true" ) )
        {
            return true ;
        }
        
        return false ;
    }


   /**
    * Return the default value of the merlin installation home. The 
    * value returned corresponds to the environment variable MERLIN_HOME.
    * If the env variable is undefined, return ${user.home}/.merlin
    * @return the default merlin install path
    */ 
    private static File getMerlinHome()
    {
        File l_merlinHome = null ;
        try 
        {
            final String merlinHomeEnv = Env.getEnvVariable( MERLIN_HOME_ENV );
            if( merlinHomeEnv != null )
            {
                return new File( merlinHomeEnv ) ;
            }
            else
            {
                final String userHome = System.getProperty( "user.home" );
                if( userHome != null )
                {
                    File userDir = new File( userHome );
                    return new File( userDir, ".merlin" ) ;
                }
                else
                {
                    return null ;
                }
            }
        }
        catch( EnvAccessException e )
        {
            e.printStackTrace( System.err ) ;
            return null;
        }
    }

   /**
    * Return the default value of the merlin local application repository. The 
    * value returned corresponds to the environment variable MERLIN_REPO_LOCAL.
    * If the env variable is undefined, return ${merlin.home}/repository
    * @return the default merlin local user repository path
    */ 
    private static File getMerlinApplicationRepository( File home )
    {
        try 
        {
            final String userRepoEnv = 
                Env.getEnvVariable( MERLIN_REPO_LOCAL_ENV );
            if( userRepoEnv != null )
            {
                return new File( userRepoEnv ) ;
            }
            else
            {
                return new File( home, "repository" ) ;
            }
        }
        catch( EnvAccessException e )
        {
            e.printStackTrace( System.err ) ;
            return null ;
        }
    }
}


