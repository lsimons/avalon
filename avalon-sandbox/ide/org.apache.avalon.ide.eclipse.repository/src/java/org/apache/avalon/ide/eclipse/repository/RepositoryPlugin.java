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
package org.apache.avalon.ide.eclipse.repository;
       
import java.util.HashMap;

import org.apache.avalon.ide.eclipse.repository.plugins.*;
import org.apache.avalon.ide.repository.RepositoryAgentFactory;
import org.apache.avalon.ide.repository.RepositoryTypeRegistry;
import org.apache.avalon.ide.repository.tools.common.SimpleRepositoryRegistry;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IPluginEvent;
import org.eclipse.core.runtime.IPluginListener;
import org.eclipse.core.runtime.IPluginRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author Niclas Hedhman, niclas@hedhman.org
 */
public class RepositoryPlugin extends AbstractUIPlugin
    implements IPluginListener
{
    //The shared instance.
    private static RepositoryPlugin m_Plugin;

    private ResourceManager m_ResourceManager;
    private SimpleRepositoryRegistry m_RepositoryTypeRegistry;

    private HashMap m_PluginHandlers;
    
    /**
	 * The constructor.
	 */
    public RepositoryPlugin(IPluginDescriptor descriptor)
    {
        super(descriptor);
        m_Plugin = this;
        m_ResourceManager = new ResourceManager();
        m_RepositoryTypeRegistry = new SimpleRepositoryRegistry();
        createHandlers();
        
        Platform.addPluginListener( this );
    }

    public RepositoryTypeRegistry getRepositoryTypeRegistry()
    {
        return m_RepositoryTypeRegistry;
    }

    /**
	 * Returns the shared instance.
	 */
    public static RepositoryPlugin getDefault()
    {
        return m_Plugin;
    }

    /**
	 * Returns the workspace instance.
	 */
    public static IWorkspace getWorkspace()
    {
        return ResourcesPlugin.getWorkspace();
    }

    public static ResourceManager getResourceManager()
    {
        return m_Plugin.m_ResourceManager;
    }
    
    private void instantiateRepositoryAgentFactories()
        throws CoreException
    {
        IPluginRegistry registry = Platform.getPluginRegistry();
        IExtensionPoint point = registry.getExtensionPoint("org.eclipse.sample.sampleExtensionPoint");
        IExtension[] extensions = point.getExtensions();
        for (int i = 0; i < extensions.length; i++) 
        {
            IConfigurationElement[] elements = extensions[i].getConfigurationElements();
            for (int j = 0; j < elements.length; j++) 
            {
                RepositoryAgentFactory object = (RepositoryAgentFactory) elements[j].createExecutableExtension("extension");
                System.out.println("Found an executable extension: " + object);
            }
       }
    }
    
    public void pluginChanged( IPluginEvent[] event )
    {
        for( int i=0 ; i < event.length ; i++ )
        {
            Integer type = new Integer( event[i].getType() );
            PluginHandler handler = (PluginHandler) m_PluginHandlers.get( type );
            IPluginDescriptor descriptor = event[i].getPluginDescriptor();
            try
            {
                handler.handle( descriptor );
            } catch( PluginHandlerException e )
            {
                // SHOULD-DO Error Handling
                e.printStackTrace();
                
            }
        }
    }
    
    private void createHandlers()
    {
        m_PluginHandlers = new HashMap();
        createHandler(IPluginEvent.INSTALLED, new PluginHandlerInstalled() );
        createHandler(IPluginEvent.RESOLVED, new PluginHandlerResolved() );
        createHandler(IPluginEvent.STARTED, new PluginHandlerStarted() );
        createHandler(IPluginEvent.STOPPED, new PluginHandlerStopped() );
        createHandler(IPluginEvent.UNINSTALLED, new PluginHandlerUninstalled() );
        createHandler(IPluginEvent.UNRESOLVED, new PluginHandlerUnresolved() );
        createHandler(IPluginEvent.UPDATED, new PluginHandlerUpdated() );
    }
    
    private void createHandler( int type, PluginHandler handler )
    {
        Integer t = new Integer( type );
        m_PluginHandlers.put( t, handler );
    }
}
     
