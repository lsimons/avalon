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

import java.lang.reflect.InvocationTargetException;

import org.apache.avalon.ide.eclipse.merlin.operation.AbstractGeneratorOperation;
import org.apache.avalon.ide.eclipse.merlin.ui.MerlinDeveloperUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a> *
 */
public class NewMerlinNatureWizard extends NewProjectWizard
{

    public boolean canFinish()
    {
        IWizardPage page = getContainer().getCurrentPage();
        if (page == getProjectPage())
            return false;
        if (page == getSelectionPage())
            return false;
        if (page == getContainerPage() && getContainerPage().isPageComplete())
            return true;
        return false;
    }

    public boolean performFinish()
    {
        AbstractGeneratorOperation runnable = new AbstractGeneratorOperation();
        IRunnableWithProgress op = new WorkspaceModifyDelegatingOperation(runnable);
        try
        {
            getContainer().run(false, true, op);
        } catch (InterruptedException e)
        {
            MerlinDeveloperUI.log(e, "performFinish() handling InterruptedException"); //$NON-NLS-1$
            return false;
        } catch (InvocationTargetException e)
        {
            Throwable realException = e.getTargetException();
            MerlinDeveloperUI.log(realException, "performFinish() handling InvocationTargetException"); //$NON-NLS-1$
            MessageDialog.openError(getShell(), MerlinDeveloperUI.getResourceString("NewWebAppProjectWizard.errorDialog.title"), realException.getMessage()); //$NON-NLS-1$
            return false;
        }
        return true;
    }

    public void init(IWorkbench workbench, IStructuredSelection selection)
    {
        setWindowTitle(MerlinDeveloperUI.getResourceString("new.blockDevelopment.window.title")); //$NON-NLS-1$
        setDefaultPageImageDescriptor(MerlinDeveloperUI.getImageDescriptor("icons/full/wizban/newjprj_wiz.gif")); //$NON-NLS-1$
    }

}