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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.avalon.ide.eclipse.core.tools.EclipseDirectoryHelper;
import org.apache.avalon.ide.eclipse.merlin.core.MerlinDeveloperCore;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 *  
 */
public class EclipseResource
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
     *  
     */
    public EclipseResource(IProject project)
    {
        super();
        this.project = project;
    }

    /**
     * extracts the fully qualified class name from a given filePathName
     * 
     * @param pProject
     * @param pFilePathName
     * @return the fully qualified className
     */
    public static String getQualifiedClassName(IResource pResource)
    {

        String qualifiedClassName = null;
        IProject project = pResource.getProject();

        try
        {
            IJavaProject proj = JavaCore.create(project);
            String filePathName = pResource.getLocation().toString();

            if (pResource.getFileExtension().toLowerCase().equals("class"))
            {
                String outputPath = proj.getOutputLocation().toString();
                String projectPath = project.getLocation().toString();
                outputPath = projectPath + outputPath.substring(project.getName().length());
                qualifiedClassName =
                    filePathName.substring(outputPath.length(), filePathName.length() - 6);
                return qualifiedClassName = qualifiedClassName.replace('/', '.');
            }

            IPackageFragmentRoot roots[] = proj.getPackageFragmentRoots();
            String projectPath = project.getLocation().toString();
            String sourcePath = null;

            for (int i = 0; roots.length > i; i++)
            {
                if (roots[i].isArchive())
                    continue;
                sourcePath = projectPath + "/" + roots[i].getElementName();
                if (sourcePath.equals(filePathName.substring(0, sourcePath.length())))
                {
                    break;
                }
            }
            qualifiedClassName =
                filePathName.substring(sourcePath.length() + 1, filePathName.length() - 5);
            qualifiedClassName = qualifiedClassName.replace('/', '.');

        } catch (JavaModelException e)
        {
            e.printStackTrace();
        }

        return qualifiedClassName;
    }

    /**
     * @param element
     * @return
     */
    public String[] getSourcePaths() throws Exception
    {

        List sourcePath = new ArrayList();

        String projectPath = project.getLocation().toString();
        IJavaProject javaProject = JavaCore.create(project);
        IPackageFragmentRoot[] roots = javaProject.getPackageFragmentRoots();

        for (int i = 0; roots.length > i; i++)
        {
            if (!roots[i].isArchive())
            {
                sourcePath.add(projectPath + "/" + roots[i].getElementName());
            }
        }
        return (String[]) sourcePath.toArray(new String[sourcePath.size()]);
    }

    public void addUuidProperty(String className, boolean b)
    {

        try
        {
            IJavaProject proj = JavaCore.create(project);
            IType type = proj.findType(className);
            addUuidField(type, b);
            addUuidAccessor(type, b);
        } catch (JavaModelException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    /**
     * @param b
     */
    private void addUuidAccessor(IType type, boolean b) throws Exception
    {

        IMethod method = type.getMethod("getUuid", new String[0]);
        if (b && !method.exists())
        {
            type.createMethod(getGetUuidMethodSource(), null, false, null);
        }
        if (!b && method.exists())
        {
            method.delete(true, null);
        }

        String str[] = new String[1];
        str[0] = "QString;";
        method = type.getMethod("setUuid", str);
        if (b && !method.exists())
        {
            type.createMethod(getSetUuidMethodSource(), null, false, null);
        }
        if (!b && method.exists())
        {
            method.delete(true, null);
        }
    }

    private String getGetUuidMethodSource()
    {

        StringBuffer buf = new StringBuffer();
        buf.append("    /**\n");
        buf.append("     * @uuid. Get the unique object identifier.\n");
        buf.append("     */\n");
        buf.append("    public String getUuid(){\n\n");
        buf.append("    	return uuid;\n");
        buf.append("    }\n\n");

        return buf.toString();
    }

    private String getSetUuidMethodSource()
    {

        StringBuffer buf = new StringBuffer();
        buf.append("    /**\n");
        buf.append("     * @uuid. Set the unique object identifier.\n");
        buf.append("     */\n");
        buf.append("    public void setUuid(String uuid){\n\n");
        buf.append("    	this.uuid = uuid;\n");
        buf.append("    }\n\n");

        return buf.toString();
    }
    /**
     * @param b
     */
    private void addUuidField(IType type, boolean b) throws Exception
    {

        IField field = type.getField("uuid");
        if (b && !field.exists())
        {
            type.createField(getUuidFieldSource(), null, false, null);
        }
        if (!b && field.exists())
        {
            field.delete(true, null);
        }
    }

    private String getUuidFieldSource()
    {

        StringBuffer buf = new StringBuffer();
        buf.append("    // The uuid is used to store a unique object identifier. **\n");
        buf.append("    private String uuid;\n\n");

        return buf.toString();
    }

    /**
     * @param file
     * @return
     */
    public static URLClassLoader getProjectClassLoader(IResource file)
    {

        URL urls[] = null;

        try
        {
            urls = new URL[1];
            urls[0] = new URL("file:/" + EclipseDirectoryHelper.getOutputPath(file) + "/");

        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        URLClassLoader cl = new URLClassLoader(urls, MerlinDeveloperCore.class.getClassLoader());

        return cl;
    }

    /**
     * Create a new Project with a given name and nature
     * 
     * @param pFile
     * @return
     */
    public static IProject createMerlinProject(String projectName, String nature)
        throws InvocationTargetException, InterruptedException
    {

        {
            IProject project;

            try
            {
                IWorkspaceRoot root = MerlinDeveloperCore.getWorkspace().getRoot();
                // create the project in workspace.
                project = root.getProject(projectName);

                if (!project.exists())
                {
                    project.create(null);
                }

                if (!project.isOpen())
                {
                    project.open(null);
                }
                // add the nature to the project
                IProjectDescription description = project.getDescription();

                String[] natureIds = new String[] { JavaCore.NATURE_ID, nature };
                description.setLocation(null);
                description.setNatureIds(natureIds);
                project.setDescription(description, null);

            } catch (CoreException e)
            {
                throw new InvocationTargetException(e);
            }
            return project;
        }
    }

}
