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