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
package org.apache.avalon.ide.eclipse.merlin.nature;

import org.apache.avalon.ide.eclipse.merlin.launch.MerlinDeveloperLaunch;
import org.eclipse.core.resources.IProject;
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
    {

    }

    public void deconfigure() throws CoreException
    {
        // Remove the nature-specific information here.
    }

    public String getDocumentBase()
    {
        return this.getProject().getLocation().toString();
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
