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


/** A Registry holding the RepositoryAgentFactory for each URN.
 * 
 * <p>
 * A Repository is accessed using a URN schema of:
 * <pre>
 *   urn:[type]:[location]
 * </pre>
 * The <strong>[type]</strong> part is to which the RepositoryAgentFactory is registered, and
 * the <strong>[location]</strong> part is used in the RepositoryAgentFactpry.create() method.
 * </p>
 * <p>
 * The RepositoryTypeRegistry is responsible to parse the URN properly, so the client
 * code can pass in the full URN, without chopping off the type from the location.
 * </p>
 * <p>
 * Example;
 * <pre>
 *   urn:plain-url:file:///opt/avalon/repository/
 * </pre>
 * </p>
 * <p>
 * To register a RepositoryAgent, we do;
 * <pre>
 *   RepositoryAgentFactory factory = new PlainURLRepositoryAgentFactory();
 *   m_RepositoryTypeRegistry.add( "plain-url", factory );
 * </pre>
 * </p>
 * <p>
 * And to get to the repository of the URN above, we only need to do;
 * <pre>
 *   String urn = "urn:plain-url:file:///opt/avalon/repository/"
 *   RepositoryAgentFactory factory = m_RepositoryTypeRegistry.getRepositoryAgentFactory( urn );
 *   RepositoryAgent agent = factory.create( urn );
 *   if( agent.isRepositoryAvailable() )
 *   {
 *       ResourceInfo[] infos = agent.loadResourceInfo( "" );  // Load the root resource group.
 *       for( int i=0 ; i &lt; infos.length ; i++ )
 *       {
 *           String name = infos[i].getName();
 *           String description = infos[i].getDescription();
 *           :
 *           :
 *           InputStream in = agent.openInputStream( infos[i] );
 *           :
 *           :
 *       }
 *   }
 * </pre>
 * </p>
 * @author Niclas Hedhman, niclas@hedhman.org
 */
public interface RepositoryTypeRegistry
{
    /** Returns an array of registered URNs.
     * 
     * @return A string array filled with the URNs that has been registered.
     */
    RepositorySchemeDescriptor[] getRegisteredURNs();
    
    /** Returns the URNDescriptor registered under the type name.
     * 
     * The type parameter may contain the "urn:" and [location] literals. It
     * is the responsibility of the RepositoryTypeRegistry implementation to
     * do the parsing.
     * 
     * @param type The type of the urn to be looked up.
     * @return
     */
    RepositorySchemeDescriptor findByType( String type );
    
    /** Returns the RepositoryAgentFactory registered with the given URN.
     * 
     * @param urn The URN for which to return its RepositoryAgentFactory.
     * @return A RepositoryAgentFactory that has been registered to the URN.
     * @throws InvalidURNException If there is no RepositoryAgentFactory registered at that
     * URN.
     */
    RepositoryAgentFactory getRepositoryAgentFactory( RepositorySchemeDescriptor urn );
    
    /** Registers a RepositoryAgentFactory to a particular URN.
     * 
     * @param urn The URN to register the RepositoryAgentFactory to.
     * 
     * @param agentFactory The RepositoryAgentFactory to be registered.
     * 
     */
    void registerRepositoryAgentFactory( RepositorySchemeDescriptor urn, RepositoryAgentFactory agentFactory );
    
    /** Unregister a URN.
     * 
     * @param urn The URN to be un-registered from the RepositoryTypeRegistry.
     */
    void unregisterRepositoryAgentFactory( RepositorySchemeDescriptor urn );
    
    /** Adds a RepositoryRegistryListener.
     * 
     * <p>
     * If an identical listener already exists, the listener in this call will
     * not be added.
     * </p>
     * @param listener The listener to add.
     */
    void addRepositoryRegistryListener( RepositoryTypeRegistryListener listener );
    
    /** Removes a RepositoryRegistryListener.
     * 
     * <p>
     * If the given listener does not exist, nothing will happen.
     * </p>
     * @param listener The listener to remove.
     */
    void removeRepositoryRegistryListener( RepositoryTypeRegistryListener listener );
    
}
