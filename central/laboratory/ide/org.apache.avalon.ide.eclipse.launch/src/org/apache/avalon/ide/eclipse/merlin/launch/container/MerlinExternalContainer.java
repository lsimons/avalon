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
        return "org.apache.avalon.merlin.cli.Main";
    }

    /*
	 * (non-Javadoc)
	 *  
	 */
    protected String getProgramArguments()
    {

        StringBuffer param = new StringBuffer();
        try
        {
            IProject project =
                MerlinDeveloperLaunch.getWorkspace().getRoot().getProject(projectName);
            IJavaProject proj = JavaCore.create(project);
            param.append('"');
            param.append(project.getLocation().append(proj.getOutputLocation().lastSegment()).toString());
            param.append('"');
        } catch (JavaModelException e)
        {
            e.printStackTrace();
        }
        return param.toString() + " -execute -debug";
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
