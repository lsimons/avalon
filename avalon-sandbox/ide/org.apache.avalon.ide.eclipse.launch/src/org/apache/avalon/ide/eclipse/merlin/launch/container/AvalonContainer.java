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

import java.text.MessageFormat;
import java.util.ArrayList;

import org.apache.avalon.ide.eclipse.merlin.launch.MerlinDeveloperLaunch;
import org.apache.avalon.ide.eclipse.merlin.nature.MerlinProjectNature;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.ExecutionArguments;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.jdt.launching.sourcelookup.JavaSourceLocator;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 *  
 */
public abstract class AvalonContainer implements IAvalonContainer
{

    protected static ILaunch merlinLaunch = null;
    protected static MerlinProjectNature merlinProjectNature;
    private static final String XML_PLUGIN_ID = "org.apache.xerces"; //$NON-NLS-1$

    protected AvalonContainer()
    {}

    protected abstract LibraryLocation[] getLibraryLocations();
    protected abstract String getMainClass();
    protected abstract String getProgramArguments();
    protected abstract String getVMArguments();

    public void debug(ILaunch launch) throws CoreException
    {

        merlinLaunch = run(ILaunchManager.DEBUG_MODE, launch);
        DebugPlugin.getDefault().getLaunchManager().addLaunch(merlinLaunch);
    }

    public void start(ILaunch launch) throws CoreException
    {

        merlinLaunch = run(ILaunchManager.RUN_MODE, launch);
        ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
        lm.addLaunch(merlinLaunch);
    }

    protected ILaunch run(String mode, ILaunch launch) throws CoreException
    {

        ILaunchConfigurationWorkingCopy working = launch.getLaunchConfiguration().getWorkingCopy();
        setProjectName(working.getAttribute("project", ""));
        launch.setAttribute(
            IDebugUIConstants.ATTR_TARGET_RUN_PERSPECTIVE,
            IDebugUIConstants.PERSPECTIVE_DEFAULT);
        launch.setAttribute(
            IDebugUIConstants.ATTR_TARGET_DEBUG_PERSPECTIVE,
            IDebugUIConstants.PERSPECTIVE_DEFAULT);
        launch.setAttribute("project", getProjectName());

        // Check Tools classpath
        if (getToolsLibraryLocation() == null)
        {
            abort("ToolsLibrary location not found", null, 1); //$NON-NLS-1$
        }

        // Class to launch
        String classToLaunch = this.getMainClass();
        if (classToLaunch == null || "".equals(classToLaunch))
        { //$NON-NLS-1$
            abort("Main class not defined", null, 2); //$NON-NLS-1$
        }

        // Virtual machine
        IVMInstall vmInstall = getVMInstall(null);
        IVMRunner vmRunner = vmInstall.getVMRunner(mode);
        if (vmRunner == null)
        {
            if (mode == ILaunchManager.DEBUG_MODE)
            {
                abort(MessageFormat.format("JRE {0} does not support debug mode.", new String[] { vmInstall.getName()}), null, 3); //$NON-NLS-1$
            } else
            {
                abort(MessageFormat.format("JRE {0} does not support run mode.", new String[] { vmInstall.getName()}), null, 4); //$NON-NLS-1$
            }
        }

        // Program & VM args
        String programArguments = this.getProgramArguments();
        String vmArguments = this.getVMArguments();
        ExecutionArguments executionArguments =
            new ExecutionArguments(vmArguments, programArguments);

        // Classpath
        String[] classpath = getClasspath();

        // Bootpath
        String[] bootClasspath = getBootClasspath(vmInstall);
        String[] vmArgs = new String[1];
        vmArgs[0] = executionArguments.getVMArguments();
        if (bootClasspath == null)
        {
            abort("Boot classpath not resolved", null, 5); //$NON-NLS-1$
        }

        VMRunnerConfiguration runnerConfig = new VMRunnerConfiguration(classToLaunch, classpath);
        runnerConfig.setVMArguments(vmArgs);
        runnerConfig.setProgramArguments(executionArguments.getProgramArgumentsArray());
        runnerConfig.setBootClassPath(bootClasspath);

        IProgressMonitor monitor = new NullProgressMonitor();
        vmRunner.run(runnerConfig, launch, monitor);

        if (mode.equals(ILaunchManager.DEBUG_MODE))
        {
            launch.setSourceLocator(getSourceLocator());
        }

        return launch;

    }

    public void stop() throws CoreException
    {
        terminate();
    }

    public boolean isStarted()
    {
        return true;
    }

    public boolean isStopped()
    {
        return false;
    }

    public void terminate()
    {
        if (!(merlinLaunch == null))
        {
            try
            {
                merlinLaunch.terminate();
            } catch (DebugException e)
            {
                MerlinDeveloperLaunch.log(e, "terminate() handling DebugException" + " " + e.getStatus().getMessage() + +e.getStatus().getCode()); //$NON-NLS-1$
            }
        }
    }
    
    protected LibraryLocation getToolsLibraryLocation() {
        IPath toolsJarPath = new Path(JavaRuntime.getDefaultVMInstall().getInstallLocation().getPath()).append("lib").append("tools.jar"); //$NON-NLS-1$ //$NON-NLS-2$
        if (toolsJarPath.toFile().exists()) {
            return new LibraryLocation(toolsJarPath, Path.EMPTY, Path.EMPTY);
        } else {
            return null;
        }
    }
    
    private IVMInstall getVMInstall(String vmInstallName)
    {
        if (vmInstallName != null)
        {
            IVMInstall[] vmInstalls = getVMInstalls();
            for (int i = 0; i < vmInstalls.length; i++)
            {
                if (vmInstallName.equals(vmInstalls[i].getName()))
                {
                    return vmInstalls[i];
                }
            }
        }
        // if vmInstallName not found just return default
        return JavaRuntime.getDefaultVMInstall();
    }

    private static IVMInstall[] getVMInstalls()
    {
        ArrayList vmInstalls = new ArrayList();
        IVMInstallType[] vmInstallTypes = JavaRuntime.getVMInstallTypes();
        for (int i = 0; i < vmInstallTypes.length; i++)
        {
            IVMInstall[] installs = vmInstallTypes[i].getVMInstalls();
            for (int k = 0; k < installs.length; k++)
            {
                vmInstalls.add(installs[k]);
            }
        }
        return (IVMInstall[]) vmInstalls.toArray(new IVMInstall[vmInstalls.size()]);
    }

    private String[] getClasspath()
    {
        ArrayList runtimeClasspaths = new ArrayList();
        LibraryLocation[] serverLibs = this.getLibraryLocations();
        for (int i = 0; i < serverLibs.length; i++)
        {
            IRuntimeClasspathEntry runtimeEntry =
                JavaRuntime.newArchiveRuntimeClasspathEntry(
                    ((LibraryLocation) (serverLibs[i])).getSystemLibraryPath());
            runtimeEntry.setSourceAttachmentPath(
                ((LibraryLocation) (serverLibs[i])).getSystemLibrarySourcePath());
            runtimeEntry.setSourceAttachmentRootPath(
                ((LibraryLocation) (serverLibs[i])).getPackageRootPath());
            runtimeEntry.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
            runtimeClasspaths.add(runtimeEntry.getLocation());
        }

        if (runtimeClasspaths.size() == 0)
        {
            return null;
        } else
        {
            return (String[]) runtimeClasspaths.toArray(new String[runtimeClasspaths.size()]);
        }
    }

    private String[] getBootClasspath(IVMInstall vm)
    {
        ArrayList runtimeClasspaths = new ArrayList();
        LibraryLocation[] vmLibs = JavaRuntime.getLibraryLocations(vm);
        for (int i = 0; i < vmLibs.length; i++)
        {
            IRuntimeClasspathEntry runtimeEntry =
                JavaRuntime.newArchiveRuntimeClasspathEntry(
                    ((LibraryLocation) (vmLibs[i])).getSystemLibraryPath());
            runtimeEntry.setSourceAttachmentPath(
                ((LibraryLocation) (vmLibs[i])).getSystemLibrarySourcePath());
            runtimeEntry.setSourceAttachmentRootPath(
                ((LibraryLocation) (vmLibs[i])).getPackageRootPath());
            runtimeEntry.setClasspathProperty(IRuntimeClasspathEntry.STANDARD_CLASSES);
            runtimeClasspaths.add(runtimeEntry.getLocation());
        }

        if (runtimeClasspaths.size() == 0)
        {
            return null;
        } else
        {
            return (String[]) runtimeClasspaths.toArray(new String[runtimeClasspaths.size()]);
        }
    }

    protected void abort(String message, Throwable exception, int code) throws CoreException
    {
        throw new CoreException(
            new Status(
                IStatus.ERROR,
                MerlinDeveloperLaunch.getDefault().getDescriptor().getUniqueIdentifier(),
                code,
                message,
                exception));
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

    protected MerlinProjectNature[] getMerlinProjectCreators() throws CoreException
    {

        IProject[] projects = MerlinDeveloperLaunch.getWorkspace().getRoot().getProjects();
        ArrayList merlinProjectCreators = new ArrayList();

        for (int i = 0; i < projects.length; i++)
        {
            if (projects[i].isOpen())
            {
                if (projects[i].hasNature(MerlinDeveloperLaunch.MERLIN_PROJECT_NATURE_ID))
                {
                    MerlinProjectNature merlinProjectCreator =
                        MerlinProjectNature.create(projects[i]);
                    merlinProjectCreators.add(merlinProjectCreator);
                }
            }
        }

        return (MerlinProjectNature[]) merlinProjectCreators.toArray(
            new MerlinProjectNature[merlinProjectCreators.size()]);
    }

}
