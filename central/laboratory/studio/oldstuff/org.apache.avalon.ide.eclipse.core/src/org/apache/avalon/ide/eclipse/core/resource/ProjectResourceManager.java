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
package org.apache.avalon.ide.eclipse.core.resource;

import java.lang.reflect.InvocationTargetException;

import org.apache.avalon.ide.eclipse.core.tools.ClassNameAnalyzer;
import org.apache.avalon.ide.eclipse.core.tools.DynProjectParam;
import org.apache.avalon.ide.eclipse.core.tools.EclipseDirectoryHelper;
import org.apache.avalon.ide.eclipse.core.xmlmodel.Directory;
import org.apache.avalon.ide.eclipse.core.xmlmodel.ProjectModel;
import org.apache.avalon.ide.eclipse.core.xmlmodel.ProjectModelConfiguration;
import org.apache.avalon.ide.eclipse.core.xmlmodel.Template;
import org.apache.avalon.ide.eclipse.merlin.core.MerlinDeveloperCore;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 *  
 */
public class ProjectResourceManager extends AbstractResourceManager
{

    private String pluginId;

    /**
	 * @param ProjectModel
	 *            model
	 * @param DynProjectParam
	 *            param
	 * @param String
	 *            callingPluginId
	 * @param String
	 *            nature
	 */
    public ProjectResourceManager(
        ProjectModel model,
        DynProjectParam param,
        String callingPluginId,
        String nature)
    {
        pluginId = callingPluginId;

        try
        {
            // create a eclipse project with a given nature
            setProject(EclipseResource.createMerlinProject(param.getProjectName(), nature));

            // config templateResource
            setTemplateResource(new TemplateResource(getProject()));

            // create project structure and generate files.
            createProjectResources(model, param);
            
            // refresh the directory
            getProject().refreshLocal(IProject.DEPTH_INFINITE, null);

        } catch (InvocationTargetException e)
        {
            MerlinDeveloperCore.log(
                e,
                "InvocationTargetException while creating project resources");

        } catch (InterruptedException e)
        {
            MerlinDeveloperCore.log(e, "InterruptedException while creating project resources");
        } catch (CoreException e)
        {
            MerlinDeveloperCore.log(e, "CoreException while creating project resources");
        }
    }

    public ProjectResourceManager(IProject project)
    {
        super(project);
    }

    /**
	 * @return Returns the pluginId. @uml property=pluginId
	 */
    public String getPluginId()
    {
        return pluginId;
    }

    /**
	 * @param wizardMeta.
	 *            The selected WizardMetadata, which reprsents a project
	 */
    private void createProjectResources(ProjectModel wizardMeta, DynProjectParam param)
    {

        EclipseDirectoryHelper helper = new EclipseDirectoryHelper(getProject());

        getTemplateResource().setTemplateSourcePath(getPluginPathName() + "templates/");
        getTemplateResource().setParameter(param);

        for (int i = 0; wizardMeta.getDirectories().size() > i; i++)
        {
            Directory dir = (Directory) wizardMeta.getDirectories().get(i);
            helper.createDirectory(dir.getName(), dir.isSource());

            for (int y = 0; dir.getTemplates().size() > y; y++)
            {
                Template templ = (Template) dir.getTemplates().get(y);
                if (templ.getName() != null)
                {
                    String className = TemplateResource.replaceParam(templ.getFileName(), param);
                    ClassNameAnalyzer cna = new ClassNameAnalyzer();
                    cna.setFullClassName(className);
                    helper.createDirectory(dir.getName() + "/" + cna.getPath(), false);

                    String dest = helper.getFullPathName(dir.getName());
                    dest = dest + "/" + cna.getPath() + cna.getFileName();
                    getTemplateResource().createTemplate(dest, templ.getName());

                }
            }

        }
        // add required libs
        helper.setClasspath();
       }

    /**
	 * @return
	 */
    private String getPluginPathName()
    {
        return EclipseDirectoryHelper.getPluginLocation(pluginId).toString();
    }

    /**
	 * @param pString
	 * @param pString2
	 * @return
	 */
    public static ProjectModelConfiguration getProjectModelConfiguration(
        String fileName,
        String pluginId)
    {

        XMLResource xml = new XMLResource();
        xml.setPluginId(pluginId);
        xml.setFileName(fileName);
        return ProjectModelConfiguration.newInstance(xml);
    }
}
