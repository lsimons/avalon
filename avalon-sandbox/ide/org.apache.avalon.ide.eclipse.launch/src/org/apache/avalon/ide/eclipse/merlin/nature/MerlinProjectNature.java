/*
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
 */
package org.apache.avalon.ide.eclipse.merlin.nature;

import org.apache.avalon.ide.eclipse.merlin.launch.MerlinDeveloperLaunch;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 *
 */
public class MerlinProjectNature implements IProjectNature
{

    private IProject project;
    private IJavaProject javaProject;

    static public MerlinProjectNature create(IJavaProject javaProject) {
        MerlinProjectNature result = null;
        try {
            result =
            (MerlinProjectNature) javaProject.getProject().getNature(
                    MerlinDeveloperLaunch.MERLIN_PROJECT_NATURE_ID);
            if (result != null)
                result.setJavaProject(javaProject);
        } catch (CoreException e) {
            MerlinDeveloperLaunch.log(e, "create(IJavaProject javaProject) handling CoreException"); //$NON-NLS-1$
        }
        return result;
    }

    /**
     * This is the entry method, called by NewMerlinBlockOperation
     * 
     * @param project
     * @return
     */
    static public MerlinProjectNature create(IProject project) {

        IJavaProject javaProject = JavaCore.create(project);
        if (javaProject != null) {
            return MerlinProjectNature.create(javaProject);
        } else {
            return null;
        }
    }
    
    /**
	 * Normaly, the whole initialization stuff (creating directory structure,
	 * initial files etc. ) is done here. But because we need greater
	 * flexibility, this is done in the wizards operastions classes.
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#configure()
	 */
    public void configure() throws CoreException
    {}

    public void deconfigure() throws CoreException
    {
        // Remove the nature-specific information here.
    }

    public String getDocumentBase()
    {
        return this.getProject().getLocation().toString();
    }

    // not used yet
    protected void addToBuildSpec(String builderID) throws CoreException
    {

        IProjectDescription description = getProject().getDescription();
        ICommand[] commands = description.getBuildSpec();
        boolean found = false;

        for (int i = 0; i < commands.length; ++i)
        {
            if (commands[i].getBuilderName().equals(builderID))
            {
                found = true;
                break;
            }
        }

        if (!found)
        {
            //add builder to project
            ICommand command = description.newCommand();
            command.setBuilderName(builderID);
            ICommand[] newCommands = new ICommand[commands.length + 1];

            // Add it before other builders.
            System.arraycopy(commands, 0, newCommands, 1, commands.length);
            newCommands[0] = command;
            description.setBuildSpec(newCommands);
            getProject().setDescription(description, null);
        }

    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#getProject()
	 */
    public IProject getProject()
    {
        return project;
    }

    /**
	 * This method is automatically called by eclipse during project creation
	 * before the configure() method is called
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core.resources.IProject)
	 */
    public void setProject(IProject project)
    {
        this.project = project;
    }

    /**
     * @param pJavaProject
     */
    private void setJavaProject(IJavaProject pJavaProject)
    {
        javaProject = pJavaProject;
        
    }
    
    /**
     * @return Returns the javaProject.
     */
    public IJavaProject getJavaProject()
    {
        return javaProject;
    }

}
