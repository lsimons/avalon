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
package org.apache.avalon.ide.eclipse.repository.plugins;

import org.apache.avalon.ide.repository.InvalidSchemeException;
import org.apache.avalon.ide.repository.RepositoryAgentFactory;
import org.apache.avalon.ide.repository.RepositorySchemeDescriptor;
import org.apache.avalon.ide.repository.RepositoryTypeRegistry;
import org.apache.avalon.ide.repository.tools.common.GenericSchemeDescriptor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IPluginDescriptor;

/**
 * @author Niclas Hedhman, niclas@hedhman.org
 */
public class PluginHandlerInstalled implements PluginHandler
{
    private RepositoryTypeRegistry m_Registry;

    public PluginHandlerInstalled(RepositoryTypeRegistry registry)
    {
        super();
        m_Registry = registry;
    }

    /**
	 * Handle the IPluginEvent.INSTALLED.
	 * 
	 * @see org.apache.avalon.ide.eclipse.repository.PluginHandler#handle(org.eclipse.core.runtime.IPluginDescriptor)
	 */
    public void handle(IPluginDescriptor descriptor) throws PluginHandlerException
    {
        String prefix = null;
        try
        {
            IExtension[] extensions = descriptor.getExtensions();
            for (int i = 0; i < extensions.length; i++)
            {
                IConfigurationElement[] elements = extensions[i].getConfigurationElements();
                for (int j = 0; j < elements.length; j++)
                {
                    if ("scheme".equals(elements[j].getName()))
                    {
                        RepositoryAgentFactory factory =
                            (RepositoryAgentFactory) elements[j].createExecutableExtension("class");
                        prefix = elements[j].getAttribute("prefix");
                        String name = elements[j].getAttribute("name");
                        String description = null;
                        IConfigurationElement[] desc = elements[j].getChildren("description");
                        if (desc.length > 0)
                            description = desc[0].getValue();
                        RepositorySchemeDescriptor rsd =
                            new GenericSchemeDescriptor(prefix, name, description);

                        m_Registry.registerRepositoryAgentFactory(rsd, factory);
                    }
                }
            }
        } catch (InvalidSchemeException e)
        {
            throw new PluginHandlerException(
                "Scheme '" + prefix + "' contains invalid characters.",
                e);
        } catch (CoreException e)
        {
            throw new PluginHandlerException(
                "Unable to instantiate '" + descriptor.getUniqueIdentifier() + "'",
                e);
        }
    }

}
