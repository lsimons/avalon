package org.apache.avalon.merlin ;


/**
 * Kernel configuration parameters.
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $Author: mcconnell $
 * @version $Revision: 1.2 $
 */
public class KernelConfig 
{
    /** @todo confirm default value */
    private boolean m_isServer = KernelDefaults.isServer() ;
    /** @todo confirm default value */
    private boolean m_isDebug = KernelDefaults.isDebugEnabled() ; 
    /** @todo confirm default value */
    private boolean m_isInfo = KernelDefaults.isInfoEnabled() ;
    
    /** @todo confirm default value */
    private String[] m_remoteRepositoryUrls = 
        KernelDefaults.getRemoteRepositoryUrls() ;
    /** @todo confirm default value */
    private String m_systemRepositoryPath = 
        KernelDefaults.getSystemRepositoryPath() ;
    /** @todo confirm default value */
    private String m_userRepositoryPath =
        KernelDefaults.getUserRepositoryPath() ;
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

    
    public String[] getRemoteRepositoryUrls()
    {
        return m_remoteRepositoryUrls ;
    }

    
    public String getUserRepositoryPath()
    {
        return m_userRepositoryPath ;
    }

    
    public String getSystemRepositoryPath()
    {
        return m_systemRepositoryPath ;
    }

    
    public String getLibraryPath()
    {
        return m_libraryPath ;
    }

    
    public String getHomePath()
    {
        return m_homePath ;
    }

    
    public String getTempPath()
    {
        return m_tempPath ;
    }

    
    public String getConfigUrl()
    {
        return m_configUrl ;
    }

    
    public String[] getBlockUrls()
    {
        return m_blockUrls ;
    }

    
    public String getKernelUrl()
    {
        return m_kernelUrl ;
    }

    
    /**
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
     * @param a_userRepositoryPath The userRepositoryPath to set.
     */
    public void setUserRepositoryPath( String a_userRepositoryPath )
    {
        m_userRepositoryPath = a_userRepositoryPath ;
    }
}
