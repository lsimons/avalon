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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 *
 */
public interface IAvalonContainer {

	public void setProjectName(String projectName);
	public String getProjectName();
	public void debug(ILaunch launch) throws CoreException;
	public boolean isStarted();
	public boolean isStopped();
	public void start(ILaunch launch) throws CoreException;
	public void stop() throws CoreException;
	public void terminate();

}
