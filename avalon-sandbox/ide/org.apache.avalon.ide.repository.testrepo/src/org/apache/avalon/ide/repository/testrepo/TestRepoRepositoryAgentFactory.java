/*
 * 
 * ============================================================================
 * The Apache Software License, Version 1.1
 * ============================================================================
 * 
 * Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *  1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  3. The end-user documentation included with the redistribution, if any,
 * must include the following acknowledgment: "This product includes software
 * developed by the Apache Software Foundation (http://www.apache.org/)."
 * Alternately, this acknowledgment may appear in the software itself, if and
 * wherever such third-party acknowledgments normally appear.
 *  4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and "Apache
 * Software Foundation" must not be used to endorse or promote products derived
 * from this software without prior written permission. For written permission,
 * please contact apache@apache.org.
 *  5. Products derived from this software may not be called "Apache", nor may
 * "Apache" appear in their name, without prior written permission of the
 * Apache Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the Apache Software Foundation. For more information on the
 * Apache Software Foundation, please see <http://www.apache.org/> .
 *  
 */
package org.apache.avalon.ide.repository.testrepo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import org.apache.avalon.ide.repository.RepositoryAgent;
import org.apache.avalon.ide.repository.RepositoryAgentCreationException;
import org.apache.avalon.ide.repository.RepositoryAgentFactory;
import org.apache.avalon.ide.repository.RepositoryAgentFactoryEvent;
import org.apache.avalon.ide.repository.RepositoryAgentFactoryListener;

/**
 * @author Niclas Hedhman, niclas@hedhman.org
 */
public class TestRepoRepositoryAgentFactory implements RepositoryAgentFactory
{
    private ArrayList m_Listeners;
    private HashMap m_Agents;
    
    /**
	 *  
	 */
    public TestRepoRepositoryAgentFactory()
    {
        super();
        m_Agents = new HashMap();
    }

    /**
	 * Creates a RepositoryAgent for the given location.
	 * 
	 * <p>
	 * The semantics of the <i>location</i> parameter is implementation
	 * dependent. The RepositoryAgentFactory MUST handle any initial "urn:" and
	 * <i>[type]</i> of a urn: <i>[type]</i>:<i>[location]</i> format.
	 * </p>
	 * 
	 * @param location
	 *            Location of the repository.
	 * 
	 * @return @throws
	 *         RepositoryAgentCreationException
	 */
    public RepositoryAgent create(String location, Locale locale)
        throws RepositoryAgentCreationException
    {
        try
        {
            String loc = normalize( location );
            RepositoryAgent agent = new TestRepoRepositoryAgent(loc, locale);
            m_Agents.put( loc, agent );
            RepositoryAgentFactoryEvent event = new RepositoryAgentFactoryEvent(this, agent);
            Iterator list = m_Listeners.iterator();
            while (list.hasNext())
            {
                try
                {
                    RepositoryAgentFactoryListener listener =
                        (RepositoryAgentFactoryListener) list.next();
                    listener.createdRepositoryAgent(event);
                } catch (Exception e)
                {
                    // TODO report exception somewhere.
                }
            }
            return agent;
        } catch (MalformedURLException e)
        {
            throw new RepositoryAgentCreationException("Not a valid location.", e);
        } catch (IOException e)
        {
            throw new RepositoryAgentCreationException("Repository not responding.", e);
        }
    }

    /**
     * Returns the RepositoryAgent at that location, if any. The location
     * parameter is of the format <i><strong>urn:</strong> [type] <strong>:
     * </strong> [location]</i>, and the RepositoryAgentFactory MUST handle
     * the initial urn:[type], if present.
     * 
     * @param location
     *            The location of the RepositoryAgent to find.
     * @return Returns null if no RepositoryAgent exists at that URN.
     */
    public RepositoryAgent findRepositoryAgentByLocation(String location)
    {
        String loc = normalize( location );
        return (RepositoryAgent) m_Agents.get( loc );
    }

    /**
	 * Call to dispose the RepositoryAgent.
	 * 
	 * It is important that the factory is given a chance to clean up the
	 * RepositoryAgent, and therefor this method must be called when the
	 * RepositoryAgent is no longer needed.
	 * 
	 * @param agent
	 *            The RepositoryAgent to drop/delete.
	 */
    public void dispose(RepositoryAgent agent)
    {
        m_Agents.remove( agent.getLocation() );
        RepositoryAgentFactoryEvent event = new RepositoryAgentFactoryEvent(this, agent);
        Iterator list = m_Listeners.iterator();
        while (list.hasNext())
        {
            try
            {
                RepositoryAgentFactoryListener listener =
                    (RepositoryAgentFactoryListener) list.next();
                listener.deletedRepositoryAgent(event);
            } catch (Exception e)
            {
                // TODO report exception somewhere.
            }
        }
    }

    /**
	 * Adds a RepositoryAgentFactoryListener.
	 * 
	 * <p>
	 * If an equal listener already exists, the listener will not be added.
	 * </p>
	 * 
	 * @param listener
	 *            The listener to add.
	 */
    public void addRepositoryAgentFactoryListener(RepositoryAgentFactoryListener listener)
    {
        synchronized (this)
        {
            ArrayList v;
            if (m_Listeners == null)
                v = new ArrayList();
            else
                v = (ArrayList) m_Listeners.clone();
            v.add(listener);
            m_Listeners = v;
        }
    }

    /**
	 * Removes a RepositoryAgentFactoryListener.
	 * 
	 * <p>
	 * If the listener does not exist, nothing will happen.
	 * </p>
	 * 
	 * @param listener
	 *            The listener to be removed.
	 */
    public void removeRepositoryAgentFactoryListener(RepositoryAgentFactoryListener listener)
    {
        if( m_Listeners == null )
            return;
        synchronized (this)
        {
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
            location = location.substring( pos + 1 );
        if( ! location.endsWith( "/"))
            location = location + "/";
        return location;
    }
}
