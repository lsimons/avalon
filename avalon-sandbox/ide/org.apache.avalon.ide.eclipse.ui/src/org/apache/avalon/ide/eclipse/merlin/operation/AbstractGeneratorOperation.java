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
package org.apache.avalon.ide.eclipse.merlin.operation;

import java.lang.reflect.InvocationTargetException;

import org.apache.avalon.ide.eclipse.core.resource.ProjectResourceManager;
import org.apache.avalon.ide.eclipse.core.tools.DynProjectParam;
import org.apache.avalon.ide.eclipse.core.xmlmodel.ProjectModel;
import org.apache.avalon.ide.eclipse.merlin.launch.MerlinDeveloperLaunch;
import org.apache.avalon.ide.eclipse.merlin.nature.MerlinProjectNature;
import org.apache.avalon.ide.eclipse.merlin.ui.MerlinDeveloperUI;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 *
 */
public class AbstractGeneratorOperation implements IRunnableWithProgress
{

    private MerlinProjectNature merlinProject;
    private ProjectModel projectModel;
    private DynProjectParam pParam;

    public AbstractGeneratorOperation()
    {}

    /**
	 * @param pParam
	 * @param pModel
	 */
    public AbstractGeneratorOperation(DynProjectParam param, ProjectModel projectModel)
    {
        pParam = param;
        this.projectModel = projectModel;
    }

    public void run(IProgressMonitor monitor)
        throws InvocationTargetException, InterruptedException
    {
        if (monitor == null)
        {
            monitor = new NullProgressMonitor();
        }
        try
        {
            monitor.beginTask(MerlinDeveloperUI.getResourceString("NewWebAppProjectOperation.createWebAppTask.description"), 1); //$NON-NLS-1$

            new ProjectResourceManager(
                projectModel,
                pParam,
                MerlinDeveloperUI.PLUGIN_ID,
                MerlinDeveloperLaunch.MERLIN_PROJECT_NATURE_ID);
            
            //prm.addBuilder(MerlinDeveloperLaunch.MERLIN_BUILDER_ID);

        } finally
        {
            monitor.done();
        }
    }

}