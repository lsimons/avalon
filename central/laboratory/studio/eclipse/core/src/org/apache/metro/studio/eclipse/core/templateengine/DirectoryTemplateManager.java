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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.Set;

import org.apache.metro.studio.eclipse.core.MetroStudioCore;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Metro Development Team </a>
 *         11.08.2004 last change:
 *  
 */
public class DirectoryTemplateManager
{

    private Hashtable directoryTemplates = new Hashtable();

    /**
     *  
     */
    public DirectoryTemplateManager()
    {
        super();
    }

    public void addDirectoryTemplate(DirectoryTemplate template)
    {

        directoryTemplates.put(template.getId(), template);
    }

    public Set getDirectoryTemplates()
    {

        return directoryTemplates.entrySet();
    }

    public DirectoryTemplate getTemplate(String key)
    {

        return (DirectoryTemplate)directoryTemplates.get(key);
    }

    /**
     * @return
     */
    public IPath getRepositoryPath()
    {
        IPath repositoryPath = MetroStudioCore.getDefault()
                .getPluginLocation(); //$NON-NLS-1$
        repositoryPath = repositoryPath.append("lib/avalon-framework");
        return repositoryPath;
    }

    /**
     * Create a directory structure under a given project. Use the template with
     * id 'templateId' Add all requiered Libraries.
     * 
     * @param templateId
     * @param project
     */
    public DirectoryTemplate create(String templateId, IProject project)
    {

        DirectoryTemplate template = (DirectoryTemplate) directoryTemplates
                .get(templateId);
        if (template != null)
        {
            template.create(project);

        }
        return template;
    }

    /**
     * Load DirectoryStructureTemplates from file
     * 
     * @param filePathName
     * @return
     */
    public static DirectoryTemplateManager load(String filePathName)
    {

        XStream xstream = new XStream(new DomDriver());
        initXStream(xstream);
        
        FileReader reader = null;
        try
        {
            reader = new FileReader(filePathName);

        } catch (FileNotFoundException e)
        {
            MetroStudioCore.log(e,
                    "can't open Directory Template configuration file");
            return null;
        }
        return (DirectoryTemplateManager) xstream.fromXML(reader);
    }

    public static void initXStream(XStream xstream)
    {
        xstream.alias("DirectoryTemplates", DirectoryTemplateManager.class);
        xstream.alias("DirectoryTemplate", DirectoryTemplate.class);
        xstream.alias("Directory", Directory.class);
        xstream.alias("Library", Library.class);
        
    }
    /**
     * @param xstream
     */
    public void addXStreamAliases(XStream xstream)
    {
        initXStream(xstream);
    }
}