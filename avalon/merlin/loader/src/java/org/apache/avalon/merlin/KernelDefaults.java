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

package org.apache.avalon.merlin;


/**
 * Maintains algorithms for determining default values to kernel parameters.  
 * This involves searching classpaths, finding variables in the environment in
 * an operating system dependent fashion, and finding variables within the 
 * system properties.  Basically the policy of finding the values to these
 * kernel parameters are maintained here.
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $Author: mcconnell $
 * @version $Revision: 1.1 $
 */
public class KernelDefaults
{
    public static final String IS_SERVER_KEY = 
        "merlin.kernel.isserver" ;
    public static final String IS_INFO_KEY =
        "merlin.kernel.isinfo" ;
    public static final String IS_DEBUG_KEY =
        "merlin.kernel.isdebug" ;
    
    public static final String REMOTE_REPO_KEY =
        "merlin.kernel.remoterepo" ;
    public static final String USER_REPO_KEY =
        "merlin.kernel.userrepo" ;
    public static final String SYSTEM_REPO_KEY =
        "merlin.kernel.systemrepo" ;
    public static final String HOME_PATH_KEY =
        "merlin.kernel.homepath" ;
    public static final String CONFIG_URL_KEY =
        "merlin.kernel.configurl" ;
    public static final String KERNEL_URL_KEY =
        "merlin.kernel.kernelurl" ;
    public static final String TARGET_URLS_KEY =
        "merlin.kernel.targeturls" ;
    public static final String TEMP_PATH_KEY =
        "merlin.kernel.temppath" ;
    public static final String LIBRARY_PATH_KEY =
        "merlin.kernel.librarypath" ;
    

    /**
     * Gets a default value for the server flag.
     * 
     * @see org.apache.avalon.merlin.kernel.KernelParameters#isServer()
     */
    public static boolean isServer()
    {
        throw new UnsupportedOperationException( 
                "N O T   I M P L E M E N T E D   Y E T !" ) ;
    }
    

    /**
     * Gets a default value for the debug flag.
     * 
     * @see org.apache.avalon.merlin.kernel.KernelParameters#isDebugEnabled()
     */
    public static boolean isDebugEnabled()
    {
        throw new UnsupportedOperationException( 
                "N O T   I M P L E M E N T E D   Y E T !" ) ;
    }

    
    /**
     * Gets a default value for the info flag.
     * 
     * @see org.apache.avalon.merlin.kernel.KernelParameters#isInfoEnabled()
     */
    public static boolean isInfoEnabled()
    {
        throw new UnsupportedOperationException( 
                "N O T   I M P L E M E N T E D   Y E T !" ) ;
    }

    
    /**
     * Gets a default value for the remote repo.
     * 
     * @see org.apache.avalon.merlin.kernel.KernelParameters#
     * getRemoteRepositoryUrl()
     */
    public static String getRemoteRepositoryUrl()
    {
        throw new UnsupportedOperationException( 
                "N O T   I M P L E M E N T E D   Y E T !" ) ;
    }

    
    /**
     * 
     * @see org.apache.avalon.merlin.kernel.KernelParameters#
     * getUserRepositoryPath()
     */
    public static String getUserRepositoryPath()
    {
        throw new UnsupportedOperationException( 
                "N O T   I M P L E M E N T E D   Y E T !" ) ;
    }

    
    /**
     * @see org.apache.avalon.merlin.kernel.KernelParameters#
     * getSystemRepositoryPath()
     */
    public static String getSystemRepositoryPath()
    {
        throw new UnsupportedOperationException( 
                "N O T   I M P L E M E N T E D   Y E T !" ) ;
    }

    
    /**
     * @see org.apache.avalon.merlin.kernel.KernelParameters#getLibraryPath()
     */
    public static String getLibraryPath()
    {
        throw new UnsupportedOperationException( 
                "N O T   I M P L E M E N T E D   Y E T !" ) ;
    }


    /**
     * @see org.apache.avalon.merlin.kernel.KernelParameters#getHomePath()
     */
    public static String getHomePath()
    {
        throw new UnsupportedOperationException( 
                "N O T   I M P L E M E N T E D   Y E T !" ) ;
    }


    /**
     * @see org.apache.avalon.merlin.kernel.KernelParameters#getTempPath()
     */
    public static String getTempPath()
    {
        throw new UnsupportedOperationException( 
                "N O T   I M P L E M E N T E D   Y E T !" ) ;
    }


    /**
     * @see org.apache.avalon.merlin.kernel.KernelParameters#getConfigUrl()
     */
    public static String getConfigUrl()
    {
        throw new UnsupportedOperationException( 
                "N O T   I M P L E M E N T E D   Y E T !" ) ;
    }


    /**
     * @see org.apache.avalon.merlin.kernel.KernelParameters#getTargetUrls()
     */
    public static String[] getTargetUrls()
    {
        throw new UnsupportedOperationException( 
                "N O T   I M P L E M E N T E D   Y E T !" ) ;
    }


    /**
     * @see org.apache.avalon.merlin.kernel.KernelParameters#getKernelUrl()
     */
    public static String getKernelUrl()
    {
        throw new UnsupportedOperationException( 
                "N O T   I M P L E M E N T E D   Y E T !" ) ;
    }
}


