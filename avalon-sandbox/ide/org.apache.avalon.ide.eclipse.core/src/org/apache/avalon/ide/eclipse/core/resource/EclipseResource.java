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
 *  
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

                //project.refreshLocal(IProject.DEPTH_INFINITE, new
                // SubProgressMonitor(monitor, 1));

            } catch (CoreException e)
            {
                throw new InvocationTargetException(e);
            }
            return project;
        }
    }

}
