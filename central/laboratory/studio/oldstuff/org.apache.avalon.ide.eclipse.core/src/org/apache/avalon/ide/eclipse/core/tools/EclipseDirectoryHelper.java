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
package org.apache.avalon.ide.eclipse.core.tools;

import java.net.URL;
import java.util.Iterator;
import java.util.Vector;

import org.apache.avalon.ide.eclipse.merlin.core.MerlinDeveloperCore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 *  
 */
public class EclipseDirectoryHelper
{

    /**
	 * @uml property=project associationEnd={multiplicity={(0 -1)}
	 * elementType=java.lang.String}
	 *  
	 */
    private IProject project;

    /**
	 * @uml property=classpathEntries associationEnd={multiplicity={(0 -1)}
	 * elementType=org.eclipse.jdt.core.IClasspathEntry}
	 *  
	 */
    private Vector classpathEntries = new Vector();

    /**
	 * @param project
	 */
    public EclipseDirectoryHelper(IProject pProject)
    {
        super();
        project = pProject;
    }

    public static IPath getPluginLocation(String pluginId)
    {
        try
        {
            URL installURL =
                Platform.getPluginRegistry().getPluginDescriptor(pluginId).getInstallURL();
            return new Path(Platform.resolve(installURL).getFile());
        } catch (Exception e)
        {
            MerlinDeveloperCore.log(e, "getPluginLocation() handling Exception"); //$NON-NLS-1$
            return null;
        }
    }

    /**
	 * @param project
	 * @param qualifiedClassName
	 * @return
	 */
    public static IFile findFile(IProject project, String qualifiedClassName)
    {

        IJavaProject proj = JavaCore.create(project);
        IFile file = null;

        try
        {
            IType type = proj.findType(qualifiedClassName);
            file = (IFile) type.getUnderlyingResource();

        } catch (JavaModelException e)
        {
            e.printStackTrace();
        }
        return file;
    }

    /**
	 * Method getOutputPath.
	 * 
	 * @param file
	 * @return String
	 */
    public static String getOutputPath(IResource file)
    {

        String result = null;

        IJavaProject proj = JavaCore.create(file.getProject());
        try
        {
            String fullPath = file.getProject().getLocation().toString();
            int i = 0;
            if ((i = fullPath.lastIndexOf('/')) != -1)
            {
                fullPath = fullPath.substring(0, i);
            }
            IPath path = proj.getOutputLocation();
            result = fullPath + path.toString();
        } catch (Exception e)
        {}

        return result;
    }

    /**
	 * @param eFile
	 */
    public static void refresh(IFile eFile)
    {

        try
        {
            eFile.refreshLocal(1, null);
        } catch (CoreException e)
        {
            e.printStackTrace();
        }
    }

    /**
	 * @param directories
	 */
    public void createDirectory(String directory, boolean isSource)
    {

        IFolder folder = null;

        try
        {
            ClassNameAnalyzer cna = new ClassNameAnalyzer();
            cna.setPath(directory);
            Iterator it = cna.getSegments().iterator();

            while (it.hasNext())
            {
                if (folder == null)
                {
                    // first segment
                    folder = project.getFolder((String) it.next());
                } else
                {
                    folder = folder.getFolder((String) it.next());
                }
                if (!folder.exists())
                {
                    folder.create(false, true, null);
                }
                if (isSource)
                {
                    classpathEntries.add(JavaCore.newSourceEntry(folder.getFullPath()));
                }
            }

        } catch (CoreException e)
        {
            e.printStackTrace();
        }
    }

    /**
	 * @param folderName
	 *            (relative to project)
	 * @return
	 */
    public String getFullPathName(String folderName)
    {

        String projectFolder = project.getLocation().toString();
        return projectFolder + "/" + folderName;
    }

    /**
	 *  
	 */
    public void setClasspath()
    {

        try
        {
            String[] libraries;
            IJavaProject javaProject = JavaCore.create(project);
            classpathEntries.add(JavaRuntime.getJREVariableEntry());

            IPath pluginPath = EclipseDirectoryHelper.getPluginLocation(MerlinDeveloperCore.PLUGIN_ID); //$NON-NLS-1$
            pluginPath = pluginPath.append("lib/avalon-framework");
            libraries = pluginPath.toFile().list();
            for (int i = 0; i < libraries.length; i++)
            {
                if (libraries[i].toLowerCase().startsWith("avalon-framework-api"))
                { //$NON-NLS-1$
                    classpathEntries.add(
                        JavaCore.newLibraryEntry(
                            pluginPath.append(libraries[i]),
                            null,
                            null));
                }
                if (libraries[i].toLowerCase().startsWith("avalon-framework-impl"))
                { //$NON-NLS-1$
                    classpathEntries.add(
                        JavaCore.newLibraryEntry(
                            pluginPath.append(libraries[i]),
                            null,
                            null));
                }
            }

            javaProject.setRawClasspath(
                (IClasspathEntry[]) classpathEntries.toArray(
                    new IClasspathEntry[classpathEntries.size()]),
                javaProject.getOutputLocation(),
                null);
        } catch (JavaModelException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
