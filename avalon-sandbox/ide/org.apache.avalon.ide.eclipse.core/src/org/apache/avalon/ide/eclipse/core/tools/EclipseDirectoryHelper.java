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
                if (libraries[i].equalsIgnoreCase("avalon-framework-api-4.1.5.jar"))
                { //$NON-NLS-1$
                    classpathEntries.add(
                        JavaCore.newLibraryEntry(
                            pluginPath.append("avalon-framework-api-4.1.5.jar"),
                            null,
                            null));
                }
                if (libraries[i].equalsIgnoreCase("avalon-framework-impl-4.1.5.jar"))
                { //$NON-NLS-1$
                    classpathEntries.add(
                        JavaCore.newLibraryEntry(
                            pluginPath.append("avalon-framework-impl-4.1.5.jar"),
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
