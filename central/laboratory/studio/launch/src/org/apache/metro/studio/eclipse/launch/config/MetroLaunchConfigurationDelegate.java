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
package org.apache.metro.studio.eclipse.launch.config;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.metro.studio.eclipse.launch.MetroStudioLaunch;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.ExecutionArguments;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.osgi.framework.internal.core.Constants;
import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.BundleException;

/**
 * 
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team </a>
 *  
 */
public class MetroLaunchConfigurationDelegate extends
        AbstractJavaLaunchConfigurationDelegate implements
        ILaunchConfigurationDelegate
{
    private static final String METRO_HOME_KEY = "merlin.home";

    final String CLASS_TO_LAUNCH = "org.apache.avalon.merlin.cli.Main";

    public void launch(ILaunchConfiguration config, String mode,
            ILaunch launch, IProgressMonitor monitor) throws CoreException
    {

        if (monitor == null)
        {
            monitor = new NullProgressMonitor();
        }
        IVMInstall vmInstall = getVMInstall(config);
        IVMRunner vmRunner = getVMRunner(mode, config, vmInstall);

        // Program & VM args
        String programArguments = getProgramArguments(config);
        String vmArguments = getVMArguments(config);
        ExecutionArguments executionArguments = new ExecutionArguments(
                vmArguments, programArguments);

        // Classpath
        String[] classpath = getClasspath(config);

        // Bootpath
        String[] bootClasspath = getBootClasspath(vmInstall);
        String[] vmArgs = new String[1];
        vmArgs[0] = executionArguments.getVMArguments();
        if (bootClasspath == null)
        {
            abort("Boot classpath not resolved", null, 5); //$NON-NLS-1$
        }

        VMRunnerConfiguration runnerConfig = new VMRunnerConfiguration(
                CLASS_TO_LAUNCH, classpath);
        runnerConfig.setVMArguments(vmArgs);
        runnerConfig.setProgramArguments(executionArguments
                .getProgramArgumentsArray());
        runnerConfig.setBootClassPath(bootClasspath);

        vmRunner.run(runnerConfig, launch, monitor);
        // get return code
        // setExitCode(config, launch);

        monitor.done();
    }

    private void setExitCode(ILaunchConfiguration config, ILaunch launch)
    {
        try
        {
            IProcess[] process = launch.getProcesses();
            int exit = process[0].getExitValue();
            ILaunchConfigurationWorkingCopy workingCopy = config.getWorkingCopy();
            workingCopy.setAttribute(ILaunchConfigConstants.EXIT_CODE, exit);
            // dont need to save

        } catch (DebugException e)
        {
            MetroStudioLaunch.log(e, "C'ant get exit code");
        } catch (CoreException e)
        {
            MetroStudioLaunch.log(e, "C'ant get process exit code");
        } 
    }
    private String[] getBootClasspath(IVMInstall vm)
    {
        ArrayList runtimeClasspaths = new ArrayList();
        LibraryLocation[] vmLibs = JavaRuntime.getLibraryLocations(vm);
        for (int i = 0; i < vmLibs.length; i++)
        {
            IRuntimeClasspathEntry runtimeEntry = JavaRuntime
                    .newArchiveRuntimeClasspathEntry(((LibraryLocation) (vmLibs[i]))
                            .getSystemLibraryPath());
            runtimeEntry
                    .setSourceAttachmentPath(((LibraryLocation) (vmLibs[i]))
                            .getSystemLibrarySourcePath());
            runtimeEntry
                    .setSourceAttachmentRootPath(((LibraryLocation) (vmLibs[i]))
                            .getPackageRootPath());
            runtimeEntry
                    .setClasspathProperty(IRuntimeClasspathEntry.STANDARD_CLASSES);
            runtimeClasspaths.add(runtimeEntry.getLocation());
        }

        if (runtimeClasspaths.size() == 0)
        {
            return null;
        } else
        {
            return (String[]) runtimeClasspaths
                    .toArray(new String[runtimeClasspaths.size()]);
        }
    }

    public String[] getClasspath(ILaunchConfiguration configuration)
    {
        ArrayList runtimeClasspaths = new ArrayList();
        LibraryLocation[] serverLibs = this.getLibraryLocations();
        for (int i = 0; i < serverLibs.length; i++)
        {
            IRuntimeClasspathEntry runtimeEntry = JavaRuntime
                    .newArchiveRuntimeClasspathEntry(((LibraryLocation) (serverLibs[i]))
                            .getSystemLibraryPath());
            runtimeEntry
                    .setSourceAttachmentPath(((LibraryLocation) (serverLibs[i]))
                            .getSystemLibrarySourcePath());
            runtimeEntry
                    .setSourceAttachmentRootPath(((LibraryLocation) (serverLibs[i]))
                            .getPackageRootPath());
            runtimeEntry
                    .setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
            runtimeClasspaths.add(runtimeEntry.getLocation());
        }

        if (runtimeClasspaths.size() == 0)
        {
            return null;
        } else
        {
            return (String[]) runtimeClasspaths
                    .toArray(new String[runtimeClasspaths.size()]);
        }

    }

    /**
     *  
     */
    protected LibraryLocation[] getLibraryLocations()
    {
        /*
         * String header = bundle.getHeaders().get(Constants.BUNDLE_CLASSPATH);
         * ManifestElement[] elements = ManifestElement.parseHeader(
         * Constants.BUNDLE_CLASSPATH, header);
         */
        Vector libraryLocations = new Vector();
        try
        {
            String header = (String) Platform.getBundle(
                    MetroStudioLaunch.PLUGIN_ID).getHeaders().get(
                    Constants.BUNDLE_CLASSPATH);
            ManifestElement[] elements = ManifestElement.parseHeader(
                    Constants.BUNDLE_CLASSPATH, header);
            IPath pluginPath = MetroStudioLaunch.getDefault()
                    .getPluginLocation();

            for (int i = 0; i < elements.length; i++)
            {
                libraryLocations.add(new LibraryLocation(
                // pluginPath.append(libraries[i].getPath()),
                        pluginPath.append(elements[i].getValue()), Path.EMPTY,
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
                        libraryLocations.add(new LibraryLocation(pluginPath
                                .append(path[i]), Path.EMPTY, Path.EMPTY));
                    }
                }
            }
        } catch (BundleException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return (LibraryLocation[]) libraryLocations
                .toArray(new LibraryLocation[libraryLocations.size()]);
    }

    public String getVMArguments(ILaunchConfiguration configuration)
    {
        String fallback = System.getProperty("user.home") + "/.merlin";
        String home = System.getProperty(METRO_HOME_KEY, fallback);

        return "-Djava.security.policy=" + home
                + "/bin/security.policy -Dmerlin.home=" + home;

    }

    public String getProgramArguments(ILaunchConfiguration config)
    {

        StringBuffer param = new StringBuffer();
        try
        {
            // create path to output location
            IProject project = MetroStudioLaunch.getWorkspace().getRoot()
                    .getProject(config.getName());
            IJavaProject proj = JavaCore.create(project);
            param.append('"');
            param.append(project.getLocation().append(
                    proj.getOutputLocation().lastSegment()).toString());
            param.append('"');
        } catch (JavaModelException e)
        {
            e.printStackTrace();
        }
        return param.toString() + " -execute -debug";
    }

    private IVMRunner getVMRunner(String mode, ILaunchConfiguration config,
            IVMInstall vmInstall) throws CoreException
    {
        // Virtual machine
        IVMRunner vmRunner = vmInstall.getVMRunner(mode);
        if (vmRunner == null)
        {
            if (mode == ILaunchManager.DEBUG_MODE)
            {
                abort(
                        MessageFormat
                                .format(
                                        "JRE {0} does not support debug mode.", new String[] { vmInstall.getName() }), null, 3); //$NON-NLS-1$
            } else
            {
                abort(
                        MessageFormat
                                .format(
                                        "JRE {0} does not support run mode.", new String[] { vmInstall.getName() }), null, 4); //$NON-NLS-1$
            }
        }
        return vmRunner;
    }

    protected void abort(String message, Throwable exception, int code)
            throws CoreException
    {
        throw new CoreException(new Status(IStatus.ERROR, ResourcesPlugin
                .getPlugin().getBundle().getSymbolicName(), code, message,
                exception));
    }

}

