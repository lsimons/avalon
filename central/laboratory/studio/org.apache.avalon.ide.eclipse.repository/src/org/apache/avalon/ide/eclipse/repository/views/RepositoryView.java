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
package org.apache.avalon.ide.eclipse.repository.views;

import org.apache.avalon.ide.eclipse.repository.RepositoryPlugin;
import org.apache.avalon.ide.eclipse.repository.ResourceManager;
import org.apache.avalon.ide.repository.RepositoryTypeRegistry;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Niclas Hedhman, niclas@hedhman.org
 */

public class RepositoryView extends ViewPart
{
    private TreeViewer m_Viewer;
    private ViewContentProvider m_ContentProvider;
    private DrillDownAdapter m_DrillDownAdapter;
    private Action m_AddRepositoryAction;
    private Action m_RemoveRepositoryAction;
    private Action m_DoubleClickAction;

    /**
	 * The constructor.
	 */
    public RepositoryView()
    {}

    /**
	 * This is a callback that will allow us to create the viewer and
	 * initialize it.
	 */
    public void createPartControl(Composite parent)
    {
        m_Viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        m_DrillDownAdapter = new DrillDownAdapter(m_Viewer);
        m_ContentProvider = new ViewContentProvider(this);
        m_Viewer.addTreeListener(m_ContentProvider);
        m_Viewer.setContentProvider(m_ContentProvider);
        m_Viewer.setLabelProvider(new ViewLabelProvider(this));
        m_Viewer.setSorter(new NameSorter());
        m_Viewer.setInput(ResourcesPlugin.getWorkspace());
        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        contributeToActionBars();
    }

    private void hookContextMenu()
    {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener()
        {
            public void menuAboutToShow(IMenuManager manager)
            {
                RepositoryView.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(m_Viewer.getControl());
        m_Viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, m_Viewer);
    }

    private void contributeToActionBars()
    {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalPullDown(IMenuManager manager)
    {
        manager.add(m_AddRepositoryAction);
        manager.add(new Separator());
        manager.add(m_RemoveRepositoryAction);
    }

    private void fillContextMenu(IMenuManager manager)
    {
        manager.add(m_AddRepositoryAction);
        manager.add(m_RemoveRepositoryAction);
        manager.add(new Separator());
        m_DrillDownAdapter.addNavigationActions(manager);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator("Additions"));
    }

    private void fillLocalToolBar(IToolBarManager manager)
    {
        manager.add(m_AddRepositoryAction);
        manager.add(m_RemoveRepositoryAction);
        manager.add(new Separator());
        m_DrillDownAdapter.addNavigationActions(manager);
    }

    private void makeActions()
    {
        m_AddRepositoryAction = new Action()
        {
            public void run()
            {
                ResourceManager resources = RepositoryPlugin.getResourceManager();
                String message = resources.getStringResource("USE_PREFERENCES_INSTEAD");
                showMessage(message);
            }
        };
        m_AddRepositoryAction.setText("Add Repository...");
        m_AddRepositoryAction.setToolTipText("Adds a repository to the view");
        m_AddRepositoryAction.setImageDescriptor(
            PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
                ISharedImages.IMG_OBJS_INFO_TSK));

        m_RemoveRepositoryAction = new Action()
        {
            public void run()
            {
                ResourceManager resources = RepositoryPlugin.getResourceManager();
                String message = resources.getStringResource("USE_PREFERENCES_INSTEAD");
                showMessage(message);
            }
        };
        m_RemoveRepositoryAction.setText("Remove Repository...");
        m_RemoveRepositoryAction.setToolTipText("Removes a repository from the view");
        m_RemoveRepositoryAction.setImageDescriptor(
            PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
                ISharedImages.IMG_OBJS_TASK_TSK));
        m_DoubleClickAction = new Action()
        {
            public void run()
            {
                // TODO Double click.
                // Not sure what this should do!
                showMessage("Not implemented yet!");
            }
        };
    }

    void refreshViewer()
    {
        Display.getDefault().asyncExec(new Runnable()
        {
            public void run()
            {
                m_Viewer.refresh();
            }
        });
    }

    private void hookDoubleClickAction()
    {
        m_Viewer.addDoubleClickListener(new IDoubleClickListener()
        {
            public void doubleClick(DoubleClickEvent event)
            {
                m_DoubleClickAction.run();
            }
        });
    }
    private void showMessage(String message)
    {
        ResourceManager resources = RepositoryPlugin.getResourceManager();
        MessageDialog.openInformation(
            m_Viewer.getControl().getShell(),
            "Repository View Message",
            resources.getStringResource(message));
    }

    RepositoryTypeRegistry getRepositoryRegistry()
    {
        return RepositoryPlugin.getDefault().getRepositoryTypeRegistry();
    }
    /**
	 * Passing the focus request to the viewer's control.
	 */
    public void setFocus()
    {
        m_Viewer.getControl().setFocus();
    }
}