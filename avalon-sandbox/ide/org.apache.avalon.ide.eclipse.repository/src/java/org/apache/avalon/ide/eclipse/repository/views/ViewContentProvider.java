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
package org.apache.avalon.ide.eclipse.repository.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.avalon.ide.eclipse.repository.RepositoryPlugin;
import org.apache.avalon.ide.eclipse.repository.preferences.RepositoryPreferencePage;
import org.apache.avalon.ide.repository.Compliance;
import org.apache.avalon.ide.repository.RepositoryAgent;
import org.apache.avalon.ide.repository.RepositoryAgentCreationException;
import org.apache.avalon.ide.repository.RepositoryAgentEvent;
import org.apache.avalon.ide.repository.RepositoryAgentFactory;
import org.apache.avalon.ide.repository.RepositoryAgentFactoryEvent;
import org.apache.avalon.ide.repository.RepositoryAgentFactoryListener;
import org.apache.avalon.ide.repository.RepositoryAgentListener;
import org.apache.avalon.ide.repository.RepositoryTypeRegistry;
import org.apache.avalon.ide.repository.ResourceInfo;
import org.apache.avalon.ide.repository.RepositorySchemeDescriptor;
import org.apache.avalon.ide.repository.Version;
import org.apache.avalon.ide.repository.tools.common.NonVersion;
import org.apache.avalon.ide.repository.tools.common.ResourceInfoImpl;
import org.apache.avalon.ide.repository.tools.compliance.EmptyCompliance;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.Viewer;

/**
 * The content provider for the RepositoryView.
 * 
 * @author Niclas Hedhman, niclas@hedhman.org
 */
class ViewContentProvider
    implements
        IStructuredContentProvider,
        ITreeContentProvider,
        RepositoryAgentListener,
        RepositoryAgentFactoryListener,
        ITreeViewerListener,
        IPropertyChangeListener
{
    private final RepositoryView m_View;

    private ParentNode m_InvisibleRoot;

    private HashMap m_Repositories;

    /**
	 * @param RepositoryView
	 */
    ViewContentProvider(RepositoryView pView)
    {
        m_View = pView;
        RepositoryPlugin plugin = RepositoryPlugin.getDefault();
        RepositoryTypeRegistry reg = plugin.getRepositoryTypeRegistry();
        RepositorySchemeDescriptor[] urns = reg.getRegisteredURNs();
        for( int i=0 ; i < urns.length ; i++ )
        {
            RepositoryAgentFactory factory = reg.getRepositoryAgentFactory( urns[i] );
            factory.addRepositoryAgentFactoryListener( this );
        }
    }

    public void inputChanged(Viewer v, Object oldInput, Object newInput)
    {}

    public void dispose()
    {
        IPreferenceStore prefs = RepositoryPlugin.getDefault().getPreferenceStore();
        prefs.removePropertyChangeListener(this);
        
        RepositoryPlugin plugin = RepositoryPlugin.getDefault();
        RepositoryTypeRegistry reg = plugin.getRepositoryTypeRegistry();
        RepositorySchemeDescriptor[] urns = reg.getRegisteredURNs();
        for( int i=0 ; i < urns.length ; i++ )
        {
            RepositoryAgentFactory factory = reg.getRepositoryAgentFactory( urns[i] );
            factory.addRepositoryAgentFactoryListener( this );
        }

        ViewNode[] repos = m_InvisibleRoot.getChildren();
        for (int i = 0; i < repos.length; i++)
            removeRepository(repos[i]);
    }

    void removeRepository(ViewNode node)
    {
        m_InvisibleRoot.removeChild(node);
        RepositoryAgent agent = node.getRepositoryAgent();
        agent.removeRepositoryAgentListener(this);
    }

    public Object[] getElements(Object parent)
    {
        if (parent.equals(ResourcesPlugin.getWorkspace()))
        {
            if (m_InvisibleRoot == null)
                initialize();
            return getChildren(m_InvisibleRoot);
        }
        return getChildren(parent);
    }

    public Object getParent(Object child)
    {
        if (child instanceof ViewNode)
        {
            return ((ViewNode) child).getParent();
        }
        return null;
    }

    public Object[] getChildren(Object parent)
    {
        if (parent instanceof ParentNode)
        {
            return ((ParentNode) parent).getChildren();
        }
        return new Object[0];
    }

    public boolean hasChildren(Object parent)
    {
        if (parent instanceof ParentNode)
            return ((ParentNode) parent).hasChildren();
        return false;
    }

    private void initialize()
    {
        Version version = new NonVersion();
        Compliance compliance = new EmptyCompliance();
        String type = ResourceInfo.MIMETYPE_FOLDER;
        ResourceInfo info =
            new ResourceInfoImpl("", "", "Invisible Root Node", type, version, compliance);
        m_InvisibleRoot = new ParentNode(m_View, null, info);
        m_Repositories = new HashMap();
        IPreferenceStore prefs = RepositoryPlugin.getDefault().getPreferenceStore();
        prefs.addPropertyChangeListener(this);
        String repos = prefs.getString(RepositoryPreferencePage.P_REPOSITORIES);
        PropertyChangeEvent event =
            new PropertyChangeEvent(this, RepositoryPreferencePage.P_REPOSITORIES, "", repos);
        propertyChange(event);
    }

    public void available(RepositoryAgentEvent event)
    {
        System.out.println("Available:" + event.getResourceInfo().getName());
    }

    public void unavailable(RepositoryAgentEvent event)
    {
        System.out.println("Unavailable:" + event);
    }

    // IPropertyChangeListener method
    public void propertyChange(PropertyChangeEvent event)
    {
        String prop = event.getProperty();
        if (prop.equals(RepositoryPreferencePage.P_REPOSITORIES))
        {
            String oldValue = (String) event.getOldValue();
            String newValue = (String) event.getNewValue();
            ArrayList olds = parse(oldValue);
            ArrayList news = parse(newValue);

            ArrayList removes = new ArrayList(olds);
            removes.removeAll(news);

            ArrayList adds = new ArrayList(news);
            adds.removeAll(olds);

            RepositoryPlugin plugin = RepositoryPlugin.getDefault();
            RepositoryTypeRegistry reg = plugin.getRepositoryTypeRegistry();

            Iterator removeList = removes.iterator();
            while (removeList.hasNext())
            {
                String s = (String) removeList.next();
                RepositorySchemeDescriptor urn = reg.findByType(s);
                RepositoryAgentFactory factory = reg.getRepositoryAgentFactory(urn);
                RepositoryAgent agent = factory.findRepositoryAgentByLocation(s);
                factory.dispose(agent);
            }

            Iterator addList = adds.iterator();
            while (addList.hasNext())
            {
                String s = (String) addList.next();
                try
                {
                    RepositorySchemeDescriptor urn = reg.findByType(s);
                    RepositoryAgentFactory factory = reg.getRepositoryAgentFactory(urn);
                    factory.create(s, null);
                } catch (RepositoryAgentCreationException e)
                {
                    // SHOULD-DO Error Handling.
                    System.err.println("Could not create RepositoryAgent " + s);
                }
            }
        }
    }

    private ArrayList parse(String s)
    {
        ArrayList result = new ArrayList();
        StringTokenizer st = new StringTokenizer(s, ", ", false);
        while (st.hasMoreTokens())
        {
            String tok = st.nextToken();
            result.add(tok);
        }
        return result;
    }
    // RepositoryAgentFactoryListener method
    public void createdRepositoryAgent(RepositoryAgentFactoryEvent event)
    {
        RepositoryAgent agent = event.getRepositoryAgent();
        agent.addRepositoryAgentListener(this);
        agent.loadResourceInfo("/");
    }

    public void deletedRepositoryAgent(RepositoryAgentFactoryEvent event)
    {
        RepositoryAgent agent = event.getRepositoryAgent();
        agent.removeRepositoryAgentListener(this);
    }

    // RepositoryAgentListener methods
    public void resourceLoaded(RepositoryAgentEvent event)
    {
        RepositoryAgent agent = (RepositoryAgent) event.getSource();
        ParentNode rootNode = (ParentNode) m_Repositories.get(agent);
        ResourceInfo info = event.getResourceInfo();
        if (rootNode == null)
        {
            // First time for this Repository.
            ParentNode node = new ParentNode(m_View, agent, info);
            m_Repositories.put(agent, node);
            m_InvisibleRoot.addChild(node);
        } else
        {
            String id = info.getIdentification();
            ParentNode n = (ParentNode) rootNode.findObjectById(id);
            if (n != null)
            {
                System.err.println("Warning!! Node " + id + " has already been loaded.");
                return;
            }
            String parent = getParentIdentification(id);
            ParentNode parentNode = (ParentNode) rootNode.findObjectById(parent);
            ParentNode node = new ParentNode(m_View, agent, info);
            parentNode.addChild(node);
        }
        m_View.refreshViewer();
        String resourceName = event.getResourceInfo().getIdentification();
        System.out.println("Resource Loaded:" + resourceName);
    }

    public void resourceNotFound(RepositoryAgentEvent event)
    {
        System.out.println(event.getMessage());
    }

    // ITreeViewerListener methods

    public void treeExpanded(TreeExpansionEvent event)
    {
        ParentNode node = (ParentNode) event.getElement();
        RepositoryAgent agent = node.getRepositoryAgent();
        ViewNode[] children = node.getChildren();
        for (int i = 0; i < children.length; i++)
            agent.loadResourceInfo(children[i].getResourceInfo().getIdentification());
    }

    public void treeCollapsed(TreeExpansionEvent event)
    {
        // Do nothing for now.
    }

    // private methods
    private String getParentIdentification(String ident)
    {

        int pos = ident.lastIndexOf('/');
        return ident.substring(0, pos + 1);
    }

}