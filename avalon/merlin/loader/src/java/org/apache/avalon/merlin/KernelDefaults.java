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

import java.util.ArrayList ;
import java.util.Properties ;
import java.util.Enumeration ;

import org.apache.avalon.merlin.env.Env ;
import org.apache.avalon.merlin.env.EnvAccessException ;


/**
 * Maintains algorithms for determining default values to kernel parameters.  
 * This involves searching classpaths, finding variables in the environment in
 * an operating system dependent fashion, and finding variables within the 
 * system properties.  Basically the policy of finding the values to these
 * kernel parameters are maintained here.
 * 
 * @todo document the property keys and make sure they reflect those defined by
 * Steve in his emails
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $Author: mcconnell $
 * @version $Revision: 1.3 $
 */
public class KernelDefaults
{
    private static final String [] s_keys =
    {
        "merlin.kernel.isserver",
        "merlin.kernel.isinfo",
        "merlin.kernel.isdebug",
        "merlin.kernel.remoterepo",
        "merlin.kernel.userrepo",
        "merlin.kernel.systemrepo",
        "merlin.kernel.homepath",
        "merlin.kernel.configurl",
        "merlin.kernel.kernelurl",
        "java.io.tmpdir",
        "merlin.kernel.librarypath"
    } ;
    
    
    public static final String IS_SERVER_KEY = 
        s_keys[0] ;
    public static final String IS_INFO_KEY =
        s_keys[1] ;
    public static final String IS_DEBUG_KEY =
        s_keys[2] ;
    
    public static final String REMOTE_REPO_KEY =
        s_keys[3] ;
    public static final String USER_REPO_KEY =
        s_keys[4] ;
    public static final String SYSTEM_REPO_KEY =
        s_keys[5] ;
    public static final String HOME_PATH_KEY =
        s_keys[6] ;
    public static final String CONFIG_URL_KEY =
        s_keys[7] ;
    public static final String KERNEL_URL_KEY =
        s_keys[8] ;
    public static final String TEMP_PATH_KEY =
        s_keys[9] ;
    public static final String LIBRARY_PATH_KEY =
        s_keys[10] ;
    
    public static final String BLOCK_URLS_BASE =
        "merlin.kernel.targeturls" ;
    public static final String MERLIN_FILE_BASE =
        "merlin.properties" ;
    
    /** Overlay of default properties discovered */
    private static final Properties s_defaults = new Properties() ;
    
    
    static 
    {
        loadDefaults() ;
    }
    
    
    /**
     * Loads the defaults for the kernel configuration.  Not fail fast meaning
     * if it fails at one stage it continues to gather defaults rather than
     * bombing out. 
     */
    private static void loadDefaults()
    {
        /*
         * Stage I - load the default properties bundled with this jar  
         */
        
        InputStream l_in = 
            KernelDefaults.class.getResourceAsStream( MERLIN_FILE_BASE ) ;
        
        try
        {
            s_defaults.load( l_in ) ;
        }
        catch ( IOException e )
        {
            e.printStackTrace( System.err ) ;
        }
        
        /*
         * Stage II - get some properties from the shell environment 
         */
        File l_merlinHome = null ;
        try 
        {
            l_merlinHome = new File( Env.getVariable( "MERLIN_HOME" ) ) ;
        }
        catch( EnvAccessException e )
        {
            e.printStackTrace( System.err ) ;
        }
        
        if ( null != l_merlinHome && l_merlinHome.exists() )
        {    
            File l_sysRepo = new File( l_merlinHome, "system" ) ;
            if ( l_sysRepo.exists() )
            {    
                s_defaults.setProperty( SYSTEM_REPO_KEY, 
                    l_sysRepo.getAbsolutePath() ) ;
            }
            
            File l_userRepo = new File( l_merlinHome, "repository" ) ;
            if ( l_userRepo.exists() )
            {
                s_defaults.setProperty( USER_REPO_KEY, 
                    l_userRepo.getAbsolutePath() ) ;
            }
        }
        
        /*
         * Stage III - check for overriding values of .merlin.properties within
         * the user's home directory.
         */
        String l_userHome = System.getProperty( "user.home" ) ;
        File l_userProps = new File( l_userHome, "." + MERLIN_FILE_BASE ) ;
        if ( l_userProps.exists() )
        {
            try 
            {
                s_defaults.load( new FileInputStream( l_userProps ) ) ;
            }
            catch ( IOException e )
            {
                e.printStackTrace( System.err ) ;
            }
        }
        
        /*
         * Stage IV - Check for overriding values within the System properties
         */
        for( int ii = 0; ii < s_keys.length; ii++ )
        {
            /*
             * If s_defaults has the property use it as default in case property
             * does not exist in the system properties.
             */
            String l_default = System.getProperty( s_keys[ii], 
                    s_defaults.getProperty( s_keys[ii] ) ) ;
            if ( null != l_default )
            {    
                s_defaults.setProperty( s_keys[ii], l_default ) ;
            }
        }

        // Now we overlay all the targets properties defined
        Enumeration l_list = System.getProperties().keys() ;
        while ( l_list.hasMoreElements() )
        {
            String l_key = ( String ) l_list.nextElement() ;
            if ( l_key.startsWith( BLOCK_URLS_BASE ) )
            {
                String l_default = System.getProperty( l_key,
                        s_defaults.getProperty( l_key ) ) ;
                
                if ( null != l_default )
                {
                    s_defaults.setProperty( l_key, l_default ) ;
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
            if ( l_key.startsWith( REMOTE_REPO_KEY ) )
            {
                l_urlArray.add( s_defaults.getProperty( l_key ) ) ;
            }
        }
        return ( String [] ) l_urlArray.toArray( new String [0] ) ;

    }

    
    /**
     * Gets the default value for the user repository path.
     *  
     * @see org.apache.avalon.merlin.kernel.KernelConfig#
     * getUserRepositoryPath()
     */
    public static String getUserRepositoryPath()
    {
        return s_defaults.getProperty( USER_REPO_KEY ) ;
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
            if ( l_key.startsWith( BLOCK_URLS_BASE ) )
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
}


