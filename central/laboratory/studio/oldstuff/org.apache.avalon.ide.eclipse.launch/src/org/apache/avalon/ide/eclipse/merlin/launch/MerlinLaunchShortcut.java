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
package org.apache.avalon.ide.eclipse.merlin.launch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.apache.avalon.ide.eclipse.merlin.launch.container.IAvalonContainer;
import org.apache.avalon.ide.eclipse.merlin.launch.container.MerlinExternalContainer;
import org.apache.avalon.ide.eclipse.merlin.nature.MerlinProjectNature;
import org.eclipse.core.resources.IProject;
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
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 *
 */
public class MerlinLaunchShortcut implements ILaunchShortcut
{

    private IAvalonContainer container;

    protected void searchAndLaunch(Object[] search, String mode)
    {
        MerlinProjectNature[] merlinProjectCreators = null;
        if (search != null)
        {
            merlinProjectCreators = findTargets(search);
            launch(merlinProjectCreators, mode);
        }
    }

    private MerlinProjectNature[] findTargets(Object[] elements)
    {
        Vector result = new Vector();
        try
        {
            for (int i = 0; i < elements.length; i++)
            {
                if (elements[i] instanceof IProject)
                {
                    IProject project = (IProject) elements[i];
                    if (project.getNature(MerlinDeveloperLaunch.MERLIN_PROJECT_NATURE_ID) != null)
                    {
                        result.add(MerlinProjectNature.create(project));
                    }
                }
                if (elements[i] instanceof IJavaProject)
                {
                    IJavaProject javaProject = (IJavaProject) elements[i];
                    if (javaProject
                        .getProject()
                        .getNature(MerlinDeveloperLaunch.MERLIN_PROJECT_NATURE_ID)
                        != null)
                    {
                        result.add(MerlinProjectNature.create(javaProject));
                    }
                }
            }
        } catch (CoreException e)
        {
            MerlinDeveloperLaunch.log(e, "findTargets(Object[] elements) handling CoreException"); //$NON-NLS-1$
        }
        return (MerlinProjectNature[]) result.toArray(new MerlinProjectNature[result.size()]);
    }

    protected void launch(MerlinProjectNature[] merlinProjectCreators, String mode)
    {
        //this.container = containerManager.getInstance().createServer();
        try
        {
            ILaunchConfiguration config = findLaunchConfiguration(mode, merlinProjectCreators[0]);
            if (config != null)
            {
                if (merlinProjectCreators.length != 0)
                {
                    this.container = new MerlinExternalContainer();
                    for (int i = 0; i < merlinProjectCreators.length; i++)
                    {
                        // move to launch delegate tab
                        ILaunchConfigurationWorkingCopy workingCopy = config.getWorkingCopy();
                        workingCopy.setAttribute("project", merlinProjectCreators[i].getProject().getName());
                        config = workingCopy.doSave();
                    }
                }
                DebugUITools.saveAndBuildBeforeLaunch();
                config.launch(mode, null);
            }
        } catch (CoreException e)
        {
            MerlinDeveloperLaunch.log(e, "erorr(1) while launching"); //$NON-NLS-1$
        }
    }

    protected ILaunchConfiguration findLaunchConfiguration(
        String mode,
        MerlinProjectNature project)
    {

        ILaunchConfigurationType configType = getMerlinContainerLaunchConfigType();
        List candidateConfigs = Collections.EMPTY_LIST;
        try
        {
            ILaunchConfiguration[] configs = 
                DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(configType);
            candidateConfigs = new ArrayList(configs.length);
            for (int i = 0; i < configs.length; i++)
            {
                ILaunchConfiguration config = configs[i];
                if (config
                    .getAttribute(MerlinDeveloperLaunch.ATTR_MERLIN_CONTAINER_ID, "")
                    .equals(MerlinExternalContainer.getServerLabel())
                    && (config.getName().equals(project.getProject().getName())))
                { //$NON-NLS-1$
                    candidateConfigs.add(config);
                }
            }
        } catch (CoreException e)
        {
            MerlinDeveloperLaunch.log(e, "findLaunchConfiguration(String mode) handling CoreException"); //$NON-NLS-1$
        }

        int candidateCount = candidateConfigs.size();
        if (candidateCount < 1)
        {
            return createConfiguration(project);
        } else if (candidateCount == 1)
        {
            return (ILaunchConfiguration) candidateConfigs.get(0);
        } 

        return null;
    }

    protected ILaunchConfiguration createConfiguration(MerlinProjectNature project)
    {
        ILaunchConfiguration config = null;
        try
        {
            ILaunchConfigurationType type = getMerlinContainerLaunchConfigType();
            String instanceName;
            if (project.getProject() != null)
            {
                instanceName = project.getProject().getName();
            } else
            {
                instanceName = MerlinExternalContainer.getServerLabel();
            }
            ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(null, instanceName);
            workingCopy.setAttribute(
                MerlinDeveloperLaunch.ATTR_MERLIN_CONTAINER_ID,
                MerlinExternalContainer.getServerLabel());
            workingCopy.setAttribute("project", instanceName);
            config = workingCopy.doSave();
        } catch (CoreException e)
        {
            MerlinDeveloperLaunch.log(e, "createConfiguration() handling CoreException"); //$NON-NLS-1$
        }
        return config;
    }

    protected ILaunchConfigurationType getMerlinContainerLaunchConfigType()
    {

        return getLaunchManager().getLaunchConfigurationType(
            MerlinDeveloperLaunch.ID_MERLIN_CONTAINER);
    }

    protected ILaunchManager getLaunchManager()
    {
        return DebugPlugin.getDefault().getLaunchManager();
    }

     protected void launch(MerlinProjectNature merlinProjectCreator, String mode)
    {
        launch(new MerlinProjectNature[] { merlinProjectCreator }, mode);
    }

    public void launch(IEditorPart editor, String mode)
    {
        IEditorInput input = editor.getEditorInput();
        IProject project = (IProject) input.getAdapter(IProject.class);
        if (project != null)
        {
            searchAndLaunch(new Object[] { project }, mode);
        } else
        {
            // error
        }
    }

    public void launch(ISelection selection, String mode)
    {
        if (selection instanceof IStructuredSelection)
        {
            searchAndLaunch(((IStructuredSelection) selection).toArray(), mode);
        } else
        {
            // error
        }
    }

}
