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
package org.apache.avalon.ide.eclipse.merlin.wizards;

import org.apache.avalon.ide.eclipse.merlin.ui.MerlinDeveloperUI;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
//import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 */
public class NewProjectWizard extends Wizard implements INewWizard
{

    private WizardNewProjectCreationPage projectPage;
    private NewMerlinProjectWizardPage containerPage;
    private NewMerlinProjectSelectionPage selectionPage;

    public void addPages()
    {
        projectPage = new WizardNewProjectCreationPage("mainPage"); //$NON-NLS-1$
        projectPage.setTitle(MerlinDeveloperUI.getResourceString("new.MerlinDevelopment.mainPage.title")); //$NON-NLS-1$
        projectPage.setDescription(MerlinDeveloperUI.getResourceString("new.MerlinDevelopment.mainPage.description")); //$NON-NLS-1$
        addPage(projectPage);

        selectionPage = new NewMerlinProjectSelectionPage("selectionPage");
        selectionPage.setTitle(MerlinDeveloperUI.getResourceString("new.MerlinDevelopment.selectionPage.title")); //$NON-NLS-1$
        selectionPage.setDescription(MerlinDeveloperUI.getResourceString("new.MerlinDevelopment.mainPage.description")); //$NON-NLS-1$
        addPage(selectionPage);

        containerPage = new NewMerlinProjectWizardPage("webAppPage"); //$NON-NLS-1$
        containerPage.setTitle(MerlinDeveloperUI.getResourceString("new.MerlinDevelopment.containerPage.title")); //$NON-NLS-1$
        containerPage.setDescription(MerlinDeveloperUI.getResourceString("new.MerlinDevelopment.containerPage.description")); //$NON-NLS-1$
        addPage(containerPage);

    }

    public boolean performFinish()
    {
        return true;
    }

    public void init(IWorkbench workbench, IStructuredSelection selection)
    {}

    /**
	 * @return
	 */
    public NewMerlinProjectWizardPage getContainerPage()
    {
        return containerPage;
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
    public NewMerlinProjectSelectionPage getSelectionPage()
    {
        return selectionPage;
    }
}