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

import org.apache.avalon.ide.eclipse.merlin.launch.container.MerlinExternalContainer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;


/**
 * This class is always called, if an Merlin Container is launched.
 * If the Merlin Container is launched through 'run as / Merlin Container'
 * the class MerlinLaunchShortcut is launched prior to MerlinLaunchConfigurationDelegate
 * 
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 *
 */
public class MerlinLaunchConfigurationDelegate implements ILaunchConfigurationDelegate {

	public void launch(ILaunchConfiguration launchConfig, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
				
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

        MerlinExternalContainer container = new MerlinExternalContainer();      
        //container.init();
        
        if (mode.equals(ILaunchManager.RUN_MODE)) {
            container.start(launch);
        } else if (mode.equals(ILaunchManager.DEBUG_MODE)) {
            container.debug(launch);
        }
		// check for cancellation
		if (monitor.isCanceled()) {
			return;
		}

		monitor.done();
	}
	
	protected void abort(String message, Throwable exception, int code) throws CoreException {
		throw new CoreException(new Status(IStatus.ERROR, MerlinDeveloperLaunch.getDefault().getDescriptor().getUniqueIdentifier(), code, message, exception));
	}
	
}	


