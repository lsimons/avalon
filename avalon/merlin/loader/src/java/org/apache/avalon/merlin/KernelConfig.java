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

import org.apache.avalon.repository.ProxyContext;

/**
 * Kernel configuration parameters.
 * 
 * @todo finish and double check javadocs - ask Steve to take a look at em.
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $Author: mcconnell $
 * @version $Revision: 1.3 $
 */
public class KernelConfig 
{
    /** flag for the kernel's server mode */
    private boolean m_isServer = KernelDefaults.isServer() ;
    
    /** flag for the kernel's debug mode */
    private boolean m_isDebug = KernelDefaults.isDebugEnabled() ; 

    /** flag for the kernel's info mode */
    private boolean m_isInfo = KernelDefaults.isInfoEnabled() ;
    
    /** Returns a set of remote repositories to pull jars from */
    private String [] m_remoteRepositoryUrls = 
         KernelDefaults.getRemoteRepositoryUrls() ;

    /** @todo confirm default value */
    private String m_systemRepositoryPath = 
        KernelDefaults.getSystemRepositoryPath() ;

    /** the path to the application specific repository */
    private String m_applicationRepositoryPath =
        KernelDefaults.getApplicationRepositoryPath() ;

    /** @todo confirm default value */
    private String m_configUrl = KernelDefaults.getConfigUrl() ;

    /** @todo confirm default value */
    private String m_homePath = KernelDefaults.getHomePath() ;

    /** @todo confirm default value */
    private String m_kernelUrl = KernelDefaults.getKernelUrl() ;

    /** @todo confirm default value */
    private String m_libraryPath = KernelDefaults.getLibraryPath() ;

    /** @todo confirm default value */
    private String m_tempPath = KernelDefaults.getTempPath() ;
    
    /** @todo confirm default value */
    private String [] m_blockUrls = KernelDefaults.getBlockUrls() ;
    
    /** descriptor for proxy used by the kernel to access remote repositories */
    private ProxyContext m_proxyCtx = KernelDefaults.getProxyContext() ;
    
    
   /**
    * Return TRUE if the kernel is launched in server model.
    * @return the server mode policy
    */
    public boolean isServer()
    {
        return m_isServer ;
    }
    
   /**
    * Return the debug policy.  If TRUE debug level logging 
    * shall be enabled on all channels.
    * @return the debug policy
    */
    public boolean isDebugEnabled()
    {
        return m_isDebug ;
    }

   /**
    * Return the info policy.  If TRUE an environment
    * summary is listed during kernel establishment.
    * @return the info policy
    */
    public boolean isInfoEnabled()
    {
        return m_isInfo ;
    }
    
    /**
     * Gets the ProxyContext used by the Kernel to access remote repositories.
     * 
     * @return the proxy context used by the Kernel
     */
    public ProxyContext getProxyContext()
    {
        return m_proxyCtx ;
    }

    /**
     * Gets the set of remote repositories used to download artifacts.
     * 
     * @return the remote repositories to use
     */
    public String[] getRemoteRepositoryUrls()
    {
        return m_remoteRepositoryUrls ;
    }

    
    /**
     * Gets the path to the application repository directory.  This directory 
     * will be used during the retrival of resources such as jar files 
     * referenced by block include directives and classloader resource 
     * references.  It is not used to for Kernel bootstrapping however at the
     * discretion of the user this and the system repository may be one and the 
     * same.
     * 
     * @return the local application repository path
     */
    public String getApplicationRepositoryPath()
    {
        return m_applicationRepositoryPath ;
    }

    /**
     * The local system repository used to access artifacts used to bootstrap 
     * the Kernel.
     * 
     * @return the local system repository path
     */
    public String getSystemRepositoryPath()
    {
        return m_systemRepositoryPath ;
    }

    
    /**
     * Gets the path to the directory used as the base anchor for resolution of 
     * relative path references for jar extension library directory statements 
     * in classloader directives.  If not supplied the value defaults to the 
     * current working directory or a file system root.
     * 
     * @return the path to the library directory
     */
    public String getLibraryPath()
    {
        return m_libraryPath ;
    }

    
    /**
     * The path to the root working directory for the kernel.  This config
     * parameter determines the paths of some key Context properties obtained 
     * during the Contextualizable life-cycle phase.
     * 
     * @return the path to the kernel root
     */
    public String getHomePath()
    {
        return m_homePath ;
    }

    /**
     * Gets the path to a temporary directory.
     * 
     * @return path to temororary directory
     */
    public String getTempPath()
    {
        return m_tempPath ;
    }

    /**
     * Gets the url for a configuration file.  This file is for the block target
     * configuration overrides.
     * 
     * @return the url to a config file
     */
    public String getConfigUrl()
    {
        return m_configUrl ;
    }

    
    public String[] getBlockUrls()
    {
        return m_blockUrls ;
    }

    /**
     * Gets the url for a Kernel XML configuration file.
     * 
     * @return the path to a Kernel XML configuration file
     */
    public String getKernelUrl()
    {
        return m_kernelUrl ;
    }

    /**
     * Sets the ProxyContext for this KernelConfig.
     * 
     * @param a_proxyCtx sets the proxy context
     */
    public void setProxyContext( ProxyContext a_proxyCtx )
    {
        m_proxyCtx = a_proxyCtx ;
    }

    /**
     * Set the configuration url.
     * @param a_configUrl The configUrl to set.
     */
    public void setConfigUrl( String a_configUrl )
    {
        m_configUrl = a_configUrl ;
    }

    /**
     * @param a_homePath The homePath to set.
     */
    public void setHomePath( String a_homePath )
    {
        m_homePath = a_homePath ;
    }

    
    /**
     * @param a_isDebug The isDebug to set.
     */
    public void setDebug( boolean a_isDebug )
    {
        m_isDebug = a_isDebug ;
    }

    
    /**
     * @param a_isInfo The isInfo to set.
     */
    public void setInfo( boolean a_isInfo )
    {
        m_isInfo = a_isInfo ;
    }

    
    /**
     * @param a_isServer The isServer to set.
     */
    public void setServer( boolean a_isServer )
    {
        m_isServer = a_isServer ;
    }

    
    /**
     * @param a_kernelUrl The kernelUrl to set.
     */
    public void setKernelUrl( String a_kernelUrl )
    {
        m_kernelUrl = a_kernelUrl ;
    }

    
    /**
     * @param a_libraryPath The libraryPath to set.
     */
    public void setLibraryPath( String a_libraryPath )
    {
        m_libraryPath = a_libraryPath ;
    }

    
    /**
     * @param a_remoteRepositoryUrls The remoteRepositoryUrl to set.
     */
    public void setRemoteRepositoryUrls( String[] a_remoteRepositoryUrls )
    {
        m_remoteRepositoryUrls = a_remoteRepositoryUrls ;
    }


    /**
     * @param a_systemRepositoryPath The systemRepositoryPath to set.
     */
    public void setSystemRepositoryPath( String a_systemRepositoryPath )
    {
        m_systemRepositoryPath = a_systemRepositoryPath ;
    }


    /**
     * @param a_blockUrls The blockUrls to set.
     */
    public void setBlockUrls( String [] a_blockUrls )
    {
        m_blockUrls = a_blockUrls ;
    }


    /**
     * @param a_tempPath The tempPath to set.
     */
    public void setTempPath( String a_tempPath )
    {
        m_tempPath = a_tempPath ;
    }
    
    /**
     * @param path the path to the application repository
     */
    public void setApplicationRepositoryPath( String path )
    {
        m_applicationRepositoryPath = path ;
    }
}
