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
 * This class represents an exhaustive set of kernel parameters in their most 
 * rudimentary/common representation.  
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $Author: mcconnell $
 * @version $Revision: 1.3 $
 */
public interface KernelParameters
{
    /**
     * Gets whether or not the kernel should operate in server mode.
     * 
     * @return true if it operates in server mode, false otherwise
     */
    boolean isServer() ;
    
    /**
     * Gets whether or not debugging is enabled in the kernel.
     * 
     * @return true if debugging is enabled false otherwise
     */
    boolean isDebugEnabled() ;
    
    /**
     * Gets whether or not informative logging is enabled in the kernel.  
     * @todo determine whether or not this is absolutely necessary and if there
     * is mutual exclusion between this method and the debug methods return.
     * 
     * @return true if informative logging is enabled false otherwise
     */
    boolean isInfoEnabled() ;
    
    /**
     * Urls of a set of potentially remote repositories used to pull 
     * depenendencies into the other repositories on artifact misses.
     * 
     * @return urls to the remote repositories from where we download resources
     */
    String[] getRemoteRepositoryUrls() ;
    
    /**
     * Path to the user repository base directory where the repository 
     * resides. 
     * 
     * @return the user artifact repository path
     */
    String getUserRepositoryPath() ;
    
    /**
     * Path to the system artifact repository used to bootstrap the kernel. 
     * 
     * @return the system artifact repo path/url
     */
    String getSystemRepositoryPath() ;
    
    /**
     * Gets the path to an optional directory that is the root anchor for 
     * optional-extension jar files.
     * 
     * @return the optional extention jar directory path
     */
    String getLibraryPath() ;
    
    /**
     * Gets the working home directory path.
     * 
     * @return the kernel's working directory path
     */
    String getHomePath() ;
    
    /**
     * Gets a temporary directory path.
     * 
     * @return a temp space path
     */
    String getTempPath() ;
    
    /**
     * Gets the path/url to alternative configuration overrides: config.xml 
     * file.
     * 
     * @return path/url to config overrides
     */
    String getConfigUrl() ;

    /**
     * Gets the paths or urls to block.xml files to load into the kernel.
     * 
     * @return paths/urls to the block.xml file
     */
    String [] getBlockUrls() ;
    
    /**
     * Gets the path/url to the kernel configuration file.
     * 
     * @return path/url to the kernel config file
     */
    String getKernelUrl() ;
}


