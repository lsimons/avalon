/*
 * 
 * ============================================================================
 * The Apache Software License, Version 1.1
 * ============================================================================
 * 
 * Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowledgment: "This product includes software developed by the Apache
 * Software Foundation (http://www.apache.org/)." Alternately, this
 * acknowledgment may appear in the software itself, if and wherever such
 * third-party acknowledgments normally appear. 4. The names "Jakarta", "Apache
 * Avalon", "Avalon Framework" and "Apache Software Foundation" must not be
 * used to endorse or promote products derived from this software without prior
 * written permission. For written permission, please contact
 * apache@apache.org. 5. Products derived from this software may not be called
 * "Apache", nor may "Apache" appear in their name, without prior written
 * permission of the Apache Software Foundation.
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
package org.apache.avalon.ide.repository;

import java.util.Locale;

/**
 * A Factory interface for creating RepositoryAgents.
 * 
 * <p>
 * The RepositoryTypeRegistry holds references to each registered
 * RepositoryAgentFactory.
 * </p>
 * 
 * @see org.apache.avalon.repository.tools.RepositoryTypeRegistry
 * 
 * @author Niclas Hedhman, niclas@hedhman.org
 */
public interface RepositoryAgentFactory
{
    /**
	 * Creates a RepositoryAgent for the given location.
	 * 
	 * The semantics of the <i>location</i> parameter is implementation
	 * dependent. The RepositoryAgentFactory MUST handle any initial "urn:" and
	 * <i>[type]</i> of a urn: <i>[type]</i>:<i>[location]</i> format.
	 * 
	 * @param location
	 *            Location of the repository.
	 * 
	 * @return A RepositoryAgent for the given location and locale.
	 * @throws RepositoryAgentCreationException
	 */
    RepositoryAgent create(String location, Locale locale) throws RepositoryAgentCreationException;

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
    RepositoryAgent findRepositoryAgentByLocation(String location);

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
    void dispose(RepositoryAgent agent);

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
    void addRepositoryAgentFactoryListener(RepositoryAgentFactoryListener listener);

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
    void removeRepositoryAgentFactoryListener(RepositoryAgentFactoryListener listener);

}
