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
package org.apache.metro.studio.eclipse.core.templateengine;

import java.util.List;

import org.apache.metro.studio.eclipse.core.MetroStudioCore;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Metro Development Team </a>
 *         11.08.2004 last change:
 *  
 */
public class ProjectManager
{
    final static String BASE = "org.apache.metro.studio.core";

    final static String BLOCK_NATURE_ID = BASE + ".blockNature";

    final static String FACILITY_NATURE_ID = BASE + ".facilityNature";

    final static String KERNEL_NATURE_ID = BASE + ".kernelNature";

    /**
     *  
     */
    public ProjectManager()
    {
        super();
    }

    /**
     * get a list of wizard pages, which are needed for a specific
     * ResourceTemplate.
     * 
     * @param resourceTemplateName
     * @return
     */
    public static List getWizards(String resourceTemplateName)
    {

        return null;
    }

    /**
     * This method is called from the "project new wizard" to create a new
     * project with all values entered in all wizard pages.
     * 
     * @param valueObject
     * @return
     */
    public static IProject createBlockProject(Object valueObject)
    {

        return null;
    }

    /**
     * This method is called to show all available template in the wizard so
     * that the user can choose one of them.
     */
    public List getResourceTemplateNames()
    {
        return null;
    }

    /**
     * Create a block project
     * 
     * @param name
     * @return
     */
    public static IProject createBlockProject(String name)
    {
        return createProject(name, BLOCK_NATURE_ID);
    }

    /**
     * Create a facility project
     * 
     * @param name
     * @return
     */
    public static IProject createFacilityProject(String name)
    {
        return createProject(name, FACILITY_NATURE_ID);
    }

    private static IProject createProject(String name, String nature)
    {
        IProject project = null;
        try
        {
            IWorkspaceRoot root = MetroStudioCore.getWorkspace().getRoot();
            // create the project in workspace.
            project = root.getProject(name);

            if (!project.exists())
            {
                project.create(null);
            }

            if (!project.isOpen())
            {
                project.open(null);
            }
            IProjectDescription description = project.getDescription();

            String[] natureIds = new String[] { JavaCore.NATURE_ID, nature };
            description.setLocation(null);
            description.setNatureIds(natureIds);
            project.setDescription(description, null);

        } catch (CoreException e)
        {
            MetroStudioCore.log(e, "can't create project");
        }

        return project;
    }

    public static void delete(IProject project)
    {
        try
        {
            project.delete(true, true, null);
        } catch (CoreException e)
        {
            MetroStudioCore.log(e, "can't delete project");
        }
    }
}