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
package org.apache.avalon.ide.repository.tools.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.avalon.ide.repository.RepositoryAgentFactory;
import org.apache.avalon.ide.repository.RepositoryTypeRegistry;
import org.apache.avalon.ide.repository.RepositoryTypeRegistryEvent;
import org.apache.avalon.ide.repository.RepositoryTypeRegistryListener;
import org.apache.avalon.ide.repository.RepositorySchemeDescriptor;

/**
 * @author Niclas Hedhman, niclas@hedhman.org
 */
public class SimpleRepositoryRegistry implements RepositoryTypeRegistry
{
    private HashMap m_Registrations;
    private HashMap m_URNTypes;
    private ArrayList m_Listeners;

    /**
     * 
     */
    public SimpleRepositoryRegistry()
    {
        super();
        m_Registrations = new HashMap();
        m_URNTypes = new HashMap();
        m_Listeners = new ArrayList();
    }

    /** Returns an array of registered URNs.
     * 
     * @return A string array filled with the URNs that has been registered.
     */
    public RepositorySchemeDescriptor[] getRegisteredURNs()
    {
        synchronized( this )
        {
            RepositorySchemeDescriptor[] result = new RepositorySchemeDescriptor[ m_Registrations.size() ];
            Iterator list = m_Registrations.keySet().iterator();
            for( int i=0 ; list.hasNext() ; i++ )
                result[i] = (RepositorySchemeDescriptor) list.next();
            return result;
        }
    }
    
    /** Returns the URNDescriptor registered under the type name.
     * 
     * The type parameter may contain the "urn:" and [location] literals. It
     * is the responsibility of the RepositoryTypeRegistry implementation to
     * do the parsing.
     * 
     * @param location The location containing the type of the urn to be looked up.
     * @return
     */
    public RepositorySchemeDescriptor findByType( String location )
    {
        String type = normalize( location );
        return (RepositorySchemeDescriptor) m_URNTypes.get( type );
    }

    /** Returns the RepositoryAgentFactory registered with the given URN.
     * 
     * @param urn The URN for which to return its RepositoryAgentFactory.
     * @return A RepositoryAgentFactory that has been registered to the URN.
     * @throws InvalidURNException If there is no RepositoryAgentFactory registered at that
     * URN.
     */
    public RepositoryAgentFactory getRepositoryAgentFactory( RepositorySchemeDescriptor urn )
    {
        synchronized( this )
        {        
            return (RepositoryAgentFactory) m_Registrations.get( urn );
        }
    }
    
    /** Registers a RepositoryAgentFactory to a particular URN.
     * 
     * @param urn The URN to register the RepositoryAgentFactory to. The URN may contain both the 
     * initial "urn:" part of the URN. The RepositoryTypeRegistry is responsible for parsing out 
     * the relevant part.
     * 
     * @param agentFactory The RepositoryAgentFactory to be registered.
     * 
     * @exception InvalidURNException if the URN parameter is in invalid format or is already 
     * in use.
     */
    public void registerRepositoryAgentFactory(RepositorySchemeDescriptor urn, RepositoryAgentFactory factory)
    {
        if (m_Registrations.get(urn) != null)
            return;

        Iterator list;
        synchronized (this)
        {
            m_URNTypes.put( urn.getScheme(), urn );
            m_Registrations.put(urn, factory);
            list = m_Listeners.iterator();
        }
        RepositoryTypeRegistryEvent event = new RepositoryTypeRegistryEvent(this, factory);
        while (list.hasNext())
        {
            try
            {
                RepositoryTypeRegistryListener listener =
                    (RepositoryTypeRegistryListener) list.next();
                listener.addedRepositoryAgent(event);
            } catch (Exception e)
            {
                // TODO Some form of error handling...
                e.printStackTrace();
            }
        }
    }

    /** Unregister a URN.
     * 
     * @param urn The URN to be un-registered from the RepositoryTypeRegistry.
     */
    public void unregisterRepositoryAgentFactory( RepositorySchemeDescriptor urn )
    {
        Iterator list;
        RepositoryAgentFactory factory;
        synchronized (this)
        {
            factory = (RepositoryAgentFactory) m_Registrations.get(urn);
            m_URNTypes.remove( urn.getScheme() );
            if (factory == null)
                return;
            list = m_Listeners.iterator();
        }
        RepositoryTypeRegistryEvent event = new RepositoryTypeRegistryEvent(this, factory);
        while (list.hasNext())
        {
            try
            {
                RepositoryTypeRegistryListener listener =
                    (RepositoryTypeRegistryListener) list.next();
                listener.removedRepositoryAgent(event);
            } catch (Exception e)
            {
                // TODO Some form of error handling...
                e.printStackTrace();
            }
        }
    }


    /** Adds a RepositoryRegistryListener.
     * 
     * <p>
     * If an identical listener already exists, the listener in this call will
     * not be added.
     * </p>
     * @param listener The listener to add.
     */
    public void addRepositoryRegistryListener(RepositoryTypeRegistryListener listener)
    {
        synchronized (this)
        {
            if (m_Listeners != null && m_Listeners.contains(listener))
                return;

            ArrayList v = (ArrayList) m_Listeners.clone();
            v.add(listener);
            m_Listeners = v;
        }
    }

    /** Removes a RepositoryRegistryListener.
     * 
     * <p>
     * If the given listener does not exist, nothing will happen.
     * </p>
     * @param listener The listener to remove.
     */
    public void removeRepositoryRegistryListener(RepositoryTypeRegistryListener listener)
    {
        synchronized (this)
        {
            if (m_Listeners == null)
                return;
            if (!m_Listeners.contains(listener))
                return;
            ArrayList v = (ArrayList) m_Listeners.clone();
            v.remove(listener);
            m_Listeners = v;
        }
    }
    
    private String normalize( String location )
    {
        if( location.startsWith( "urn:" ))
            location = location.substring( 4 );
        int pos = location.indexOf( ":" );
        if( pos >= 0 )
            location = location.substring( 0, pos );
        return location;
    }
}
