/*
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
package org.apache.avalon.ide.eclipse.merlin.launch.container;

import java.util.Vector;

import org.apache.avalon.ide.eclipse.merlin.launch.MerlinDeveloperLaunch;
import org.apache.avalon.ide.eclipse.merlin.nature.MerlinProjectNature;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILibrary;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.LibraryLocation;
import org.eclipse.jdt.launching.sourcelookup.JavaSourceLocator;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 *  
 */
public class MerlinExternalContainer extends AvalonContainer
{

    private static final String MAIN_CLASS = "Merlin"; //$NON-NLS-1$
    private static final String MERLIN_HOME_KEY = "merlin.home";
    private String projectName;
    /**
	 *  
	 */
    public MerlinExternalContainer()
    {
        super();
    }

    /*
	 * (non-Javadoc)
	 *  
	 */
    protected LibraryLocation[] getLibraryLocations()
    {
        IPath pluginPath = MerlinDeveloperLaunch.getDefault().getPluginLocation();
        ILibrary[] libraries =
            MerlinDeveloperLaunch.getDefault().getDescriptor().getRuntimeLibraries();
        Vector libraryLocations = new Vector();

        for (int i = 0; i < libraries.length; i++)
        {
            libraryLocations.add(
                new LibraryLocation(
                    pluginPath.append(libraries[i].getPath()),
                    Path.EMPTY,
                    Path.EMPTY));
        }

        pluginPath = pluginPath.append("/lib/hibernate");
        String[] path = pluginPath.toFile().list();
        if (path != null)
        {
            for (int i = 0; i < path.length; i++)
            {
                if (path[i].endsWith(".jar"))
                { //$NON-NLS-1$
                    libraryLocations.add(
                        new LibraryLocation(pluginPath.append(path[i]), Path.EMPTY, Path.EMPTY));
                }
            }
        }
        return (LibraryLocation[]) libraryLocations.toArray(
            new LibraryLocation[libraryLocations.size()]);
    }

    /*
	 * (non-Javadoc)
	 *  
	 */
    protected String getMainClass()
    {

        return "Merlin";
    }

    /*
	 * (non-Javadoc)
	 *  
	 */
    protected String getProgramArguments()
    {

        String param = "";
        try
        {
            IProject project =
                MerlinDeveloperLaunch.getWorkspace().getRoot().getProject(projectName);
            IJavaProject proj = JavaCore.create(project);
            param = project.getLocation().append(proj.getOutputLocation().lastSegment()).toString();
        } catch (JavaModelException e)
        {
            e.printStackTrace();
        }
        return param + " -execute -debug";
    }

    /*
	 * (non-Javadoc)
	 *  
	 */
    protected String getVMArguments()
    {
        String fallback = System.getProperty("user.home") + "/.merlin";
        String home = System.getProperty(MERLIN_HOME_KEY, fallback);

        return "-Djava.security.policy=" + home + "/bin/security.policy -Dmerlin.home=" + home;
    }

    private ISourceLocator getSourceLocator() throws CoreException
    {

        MerlinProjectNature[] addedMerlinProjectCreators = getMerlinProjectCreators();
        IJavaProject[] javaProjects = new IJavaProject[addedMerlinProjectCreators.length];

        for (int i = 0; i < addedMerlinProjectCreators.length; i++)
        {
            if (addedMerlinProjectCreators[i].getJavaProject().isOpen())
            {
                javaProjects[i] = addedMerlinProjectCreators[i].getJavaProject();
            }
        }

        return new JavaSourceLocator(javaProjects, true);

    }

    /**
	 * Set the projects name for which the container is started
	 *  
	 */
    public void setProjectName(String projectName)
    {

        this.projectName = projectName;

    }

    /**
	 * Get the projects name for which the container is started
	 */
    public String getProjectName()
    {

        return projectName;
    }

    /**
	 * @return String server label
	 */
    public static String getServerLabel()
    {
        return "merlinContainerID";
    }
}
