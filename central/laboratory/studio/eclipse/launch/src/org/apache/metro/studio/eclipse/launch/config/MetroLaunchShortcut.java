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

import org.apache.metro.studio.eclipse.core.templateengine.BlockProjectManager;
import org.apache.metro.studio.eclipse.launch.MetroStudioLaunch;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team </a>
 *  
 */
public class MetroLaunchShortcut implements ILaunchShortcut
{
    private int exitCode = 0;
    
    /**
     * extract the project out of the selected editor and perfom the launching
     */
    public void launch(IEditorPart editor, String mode)
    {
        IEditorInput input = editor.getEditorInput();
        IProject project = (IProject) input.getAdapter(IProject.class);
        launch(project, mode);
    }

    /**
     * extract the project out of the selection in project tree and perfom the
     * launching
     */
    public void launch(ISelection selection, String mode)
    {
        if (selection instanceof IStructuredSelection)
        {
            Object sel = ((IStructuredSelection) selection).getFirstElement();
            IProject project = null;
            if (sel instanceof IJavaProject)
            {
                project = ((IJavaProject) sel).getProject();
            }
            // TODO: extract file selections too
            launch(project, mode);
        }
    }

    /**
     * Launch the selected project.
     * 
     * @param project
     * @param mode
     */
    protected void launch(IProject project, String mode)
    {
        
        try
        {
            // check whether it is a Metro project
            IProjectNature nature = BlockProjectManager.getNature(project);
            if (nature == null)
                return;

            // find the config for the given project. Otherwhise
            // create a new config
            ILaunchConfiguration config = findLaunchConfiguration(mode, nature);
            if (config != null)
            {
                DebugUITools.saveAndBuildBeforeLaunch();
                config.launch(mode, null);
                setExitCode(config.getAttribute(ILaunchConfigConstants.EXIT_CODE, 0));
                int i = getExitCode();
            }
        } catch (CoreException e)
        {
            MetroStudioLaunch.log(e, "Core exception while launching "
                    + project.getName());
        }
    }

    /**
     * Check, whether a config for the given project is already
     * present. If not create a new one.
     * @param mode
     * @param nature
     * @return ILaunchConfiguration config
     */
    protected ILaunchConfiguration findLaunchConfiguration(String mode,
            IProjectNature nature)
    {

        ILaunchConfigurationType configType = getMerlinContainerLaunchConfigType();
        try
        {
            // get all configs
            ILaunchConfiguration[] configs = getLaunchManager()
                    .getLaunchConfigurations(configType);
            for (int i = 0; i < configs.length; i++)
            {
                ILaunchConfiguration config = configs[i];
                if (nature.getProject().getName().equals(config.getName()))
                {
                    // found the config for the project
                    return config;
                }
            }
            // config is not there. Create a new one
            return createConfiguration(nature);
        } catch (CoreException e)
        {
            MetroStudioLaunch
                    .log(e, "findLaunchConfiguration(String mode) handling CoreException"); //$NON-NLS-1$
        }

        return null;
    }

    /**
     * Create a new config object
     * @param nature
     * @return
     */
    protected ILaunchConfiguration createConfiguration(IProjectNature nature)
    {
        ILaunchConfiguration config = null;
        try
        {
            ILaunchConfigurationType type = getMerlinContainerLaunchConfigType();
            String instanceName = nature.getProject().getName();
            ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(
                    null, instanceName);
            config = workingCopy.doSave();
        } catch (CoreException e)
        {
            MetroStudioLaunch.log(e,
                    "createConfiguration() handling CoreException"); //$NON-NLS-1$
        }
        return config;
    }

    protected ILaunchConfigurationType getMerlinContainerLaunchConfigType()
    {

        return getLaunchManager()
                .getLaunchConfigurationType(
                        MetroStudioLaunch.METRO_CONFIG_TYPE);
    }

    /**
     * get the LaunchManager
     * @return
     */
    protected ILaunchManager getLaunchManager()
    {
        return DebugPlugin.getDefault().getLaunchManager();
    }

    /**
     * @return Returns the exitCode.
     */
    public int getExitCode()
    {
        return exitCode;
    }
    /**
     * @param exitCode The exitCode to set.
     */
    public void setExitCode(int exitCode)
    {
        this.exitCode = exitCode;
    }
}