/*
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
 */
package org.apache.avalon.ide.eclipse.merlin.wizards;

import java.lang.reflect.InvocationTargetException;

import org.apache.avalon.ide.eclipse.core.tools.DynProjectParam;
import org.apache.avalon.ide.eclipse.merlin.operation.AbstractGeneratorOperation;
import org.apache.avalon.ide.eclipse.merlin.ui.MerlinDeveloperUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>*
 */
public class NewMerlinProjectWizard extends NewProjectWizard
{

    public boolean canFinish()
    {
        IWizardPage page = getContainer().getCurrentPage();
        if (page == getProjectPage())
            return false;
        if (page == getSelectionPage())
            return false;
        if (page == getContainerPage() && page.isPageComplete())
            return true;
        return false;
    }

    public boolean performFinish()
    {
        // collect all entered data from wizard pages.
        DynProjectParam param = new DynProjectParam();
        param.setProjectName(getProjectPage().getProjectName());
        param.setContainerName(getContainerPage().getContainerName());
        param.setVirtualServiceName(getContainerPage().getServiceName());
        param.setFullServiceClassName(getContainerPage().getServiceClassName());
        param.setFullImplementationClassName(getContainerPage().getComponentClassName());
        param.setVersion(getContainerPage().getVersion());

        AbstractGeneratorOperation operation =
            new AbstractGeneratorOperation(param, getSelectionPage().getSelectedProjectModel());
        IRunnableWithProgress op = new WorkspaceModifyDelegatingOperation(operation);
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
            MessageDialog.openError(getShell(), MerlinDeveloperUI.getResourceString("new.MerlinDevelopment.errorDialog.title"), realException.getMessage()); //$NON-NLS-1$
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