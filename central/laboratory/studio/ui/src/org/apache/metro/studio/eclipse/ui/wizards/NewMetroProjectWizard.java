/*
   
      Copyright 2004. The Apache Software Foundation.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License. 
   
 */
package org.apache.metro.studio.eclipse.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.apache.metro.facility.presentationservice.api.IModelChannel;

import org.apache.metro.facility.presentationservice.impl.ModelChannel;

import org.apache.metro.studio.eclipse.ui.MetroStudioUI;

import org.apache.metro.studio.eclipse.ui.controller.CreateBlockProjectOperation;
import org.apache.metro.studio.eclipse.ui.controller.NewMetroProjectWizardController;

import org.eclipse.jface.operation.IRunnableWithProgress;

import org.eclipse.jface.viewers.IStructuredSelection;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;

import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>*
 */
public class NewMetroProjectWizard extends Wizard implements INewWizard
{
    
    private WizardNewProjectCreationPage projectPage;
    private NewMetroProjectSelectionPage selectionPage;
    
    public static final String NEW_PROJECT_NAME = "new.project.name";
    public static final String NEW_PROJECT_FINISH = "new.project.finish";
    
    private IModelChannel channel;

    public boolean canFinish()
    {
        IWizardPage page = getContainer().getCurrentPage();
        if (page == getProjectPage())
            return false;
        if (page == getSelectionPage())
            return true;
        return false;
    }

    public boolean performFinish()
    {
        try
        {
                channel.putValue(NEW_PROJECT_NAME, getProjectPage().getProjectName());
                // run project creation in background
                CreateBlockProjectOperation operation =
                    new CreateBlockProjectOperation(channel);
                IRunnableWithProgress op = new WorkspaceModifyDelegatingOperation(operation);
                    getContainer().run(false, true, op);
        } catch (InvocationTargetException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

    public void init(IWorkbench workbench, IStructuredSelection selection)
    {
        setWindowTitle(MetroStudioUI.getResourceString("new.blockDevelopment.window.title")); //$NON-NLS-1$
        setDefaultPageImageDescriptor(MetroStudioUI.getImageDescriptor("icons/full/wizban/newjprj_wiz.gif")); //$NON-NLS-1$
        
    }

    public void addPages()
        {
        	channel = new ModelChannel("newBlockWizard");
            new NewMetroProjectWizardController().initialize();
        	
            projectPage = new WizardNewProjectCreationPage("mainPage"); //$NON-NLS-1$
            projectPage.setTitle(MetroStudioUI.getResourceString("new.blockDevelopment.mainPage.title")); //$NON-NLS-1$
            projectPage.setDescription(MetroStudioUI.getResourceString("new.blockDevelopment.mainPage.description")); //$NON-NLS-1$
            addPage(projectPage);
    
            selectionPage = new NewMetroProjectSelectionPage("selectionPage", channel);
            selectionPage.setTitle(MetroStudioUI.getResourceString("new.blockDevelopment.selectionPage.title")); //$NON-NLS-1$
            selectionPage.setDescription(MetroStudioUI.getResourceString("new.blockDevelopment.mainPage.description")); //$NON-NLS-1$
            addPage(selectionPage);
        }

    /**
     * @return
     */
    public WizardNewProjectCreationPage getProjectPage()
    {
        return projectPage;
    }

    /**
     * @return
     */
    public NewMetroProjectSelectionPage getSelectionPage()
    {
        return selectionPage;
    }

}
