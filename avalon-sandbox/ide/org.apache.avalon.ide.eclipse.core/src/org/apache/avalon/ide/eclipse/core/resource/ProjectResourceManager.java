/*
 * 
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
package org.apache.avalon.ide.eclipse.core.resource;

import java.lang.reflect.InvocationTargetException;

import org.apache.avalon.ide.eclipse.core.tools.*;
import org.apache.avalon.ide.eclipse.core.tools.ClassNameAnalyzer;
import org.apache.avalon.ide.eclipse.core.tools.DynProjectParam;
import org.apache.avalon.ide.eclipse.core.xmlmodel.Directory;
import org.apache.avalon.ide.eclipse.core.xmlmodel.ProjectModel;
import org.apache.avalon.ide.eclipse.core.xmlmodel.ProjectModelConfiguration;
import org.apache.avalon.ide.eclipse.core.xmlmodel.Template;
import org.apache.avalon.ide.eclipse.merlin.core.MerlinDeveloperCore;
import org.eclipse.core.resources.IProject;
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
