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
package org.apache.avalon.ide.repository;

import java.io.InputStream;
import java.util.Locale;



/** The RepositoryAgent is a representative of the Repository (local or remote)
 *  that knows how to communicate with the repository.
 * 
 * @author Niclas Hedhman, niclas@hedhman.org
 */
public interface RepositoryAgent
{
    /** Checks if the Repository is accessible and operational.
     * 
     * @return true if the Repository is expected to work.
     */
    boolean isRepositoryAvailable();
    
    /** Returns the Repository Name.
     * 
     * @return The Name of the Repository.
     */
    String getName();

    /** Returns a human-readable description of the Repository.
     * 
     * @return A localized and human-readable description of the Repository.
     */
    String getDescription();
    
    /** Returns the physical location of the Repository.
     * 
     * @return A URL or other unique identifier of where the repository is located. 
     */
    String getLocation();
    
    /** Returns the Locale that this RepositoryAgent is initialized to.
     * 
     * @return The Locale that this RepositoryAgent is initialized to.
     */    
    Locale getLocale();
    
    /** Loads the ResourceInfo from the Repository.
     * 
     * <p>
     * Instructs the RepositoryAgent to load a particular ResourceInfo in 
     * background, and notify over the Event interface.
     * </p>
     * @param resourceIdentification is the identification of the resource, relative
     * to the repository root. If the parameter
     * is an empty string, the root resource group is requested. The identification must be
     * repository relative.
     */ 
    void loadResourceInfo( String resourceIdentification );

    /** Opens the InputStream to the actual resource.
     * 
     * @param resource The ResourceInfo for which to open the InputStream to its actual object.
     * 
     * @return An opened InputStream to the actual resource object. It is expected that the 
     * implementation uses buffered I/O, so clients don't need to optimize access.
     * 
     */
    InputStream openInputStream( ResourceInfo resource );
    
    /** Updates the RepositoryAgent.
     * 
     * For remote repositories, it is desireable that the RepositoryAgent caches
     * the meta content locally, on file or in-memory. This method explicitly
     * tells the RepositoryAgent to drop the cache. The method should return quickly
     * so if a pre-fetch algorithm is used, it must be done in seperate thread.
     */
    void refresh();
    
    /** Adds a RepositoryAgentListener.
     * 
     * <p>
     * If an identical listener already exists, the listener in this call will
     * not be added and no event generated.
     * </p>
     * @param listener The listener to add.
     */
    void addRepositoryAgentListener( RepositoryAgentListener listener );
    
    /** Removes a RepositoryAgentListener.
     * 
     * <p>
     * If the given listener does not exist, nothing will happen.
     * </p>
     * @param listener The listener to remove.
     */
    void removeRepositoryAgentListener( RepositoryAgentListener listener );
}
