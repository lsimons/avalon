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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.apache.metro.studio.eclipse.core.MetroStudioCore;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;

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
     * Add standard libraries to the project.
     * 
     * @param template
     * @param project
     */
    public void addLibraries(DirectoryTemplate template, IProject project)
    {
        Vector libraries = new Vector();
        Vector libraryNames;

        IPath repositoryPath = getRepositoryPath();

        libraries.addAll(collectLibraries(template, repositoryPath));
        libraries.addAll(collectSourceFolders(template, project));

        // add all Libraries to project.
        try
        {
            IJavaProject javaProject = JavaCore.create(project);
            javaProject.setRawClasspath((IClasspathEntry[]) libraries
                    .toArray(new IClasspathEntry[libraries.size()]),
                    javaProject.getOutputLocation(), null);

        } catch (JavaModelException e)
        {
            MetroStudioCore.log(e, "could not add libraries to project");
        }

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
     * @param template
     * @param libraries
     * @param repositoryPath
     */
    public Vector collectLibraries(DirectoryTemplate template,
            IPath repositoryPath)
    {
        Vector libraryNames;
        Vector libraries = new Vector();

        // first add the java standard library
        libraries.add(JavaRuntime.getJREVariableEntry());

        // Now collect libraries from template.
        libraryNames = template.getLibraryNames();
        Iterator it = libraryNames.iterator();

        while (it.hasNext())
        {
            String lib = (String) it.next();
            if (new File(lib).exists())
            {
                libraries.add(JavaCore.newLibraryEntry(repositoryPath
                        .append(lib), null, null));
            }
        }
        return libraries;
    }

    /**
     * @param template
     * @param libraries
     * @param repositoryPath
     */
    public Vector collectSourceFolders(DirectoryTemplate template,
            IProject project)
    {
        Vector folderNames;
        Vector libraries = new Vector();

        // Now collect libraries from template.
        folderNames = template.getSourceFolderNames();
        Iterator it = folderNames.iterator();

        while (it.hasNext())
        {
            IPath folder = new Path((String)it.next());
            libraries.add(JavaCore.newSourceEntry(project.getFullPath().append(folder)));
        }
        return libraries;
    }

    /**
     * Create a directory structure under a given project. Use the template with
     * id 'templateId' Add all requiered Libraries.
     * 
     * @param templateId
     * @param project
     */
    public void create(String templateId, IProject project)
    {

        DirectoryTemplate template = (DirectoryTemplate) directoryTemplates
                .get(templateId);
        if (template != null)
        {
            template.create(project);
            addLibraries(template, project);
        }
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