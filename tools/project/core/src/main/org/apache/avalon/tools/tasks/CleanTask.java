/* 
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.avalon.tools.tasks;

import java.io.File;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Delete;

import org.apache.avalon.tools.home.Context;

/**
 * Load a goal. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class CleanTask extends Task
{
    private Context m_context;
    private boolean m_init = false;

    public void init() throws BuildException 
    {
        if( !m_init )
        {
            Project project = getProject();
            m_context = Context.getContext( project );
            m_init = true;
        }
    }

    public void execute() throws BuildException 
    {
        File target = m_context.getTargetDirectory();
        Project project = getProject();
        Delete delete = (Delete) project.createTask( "delete" );
        delete.setDir( target );
        delete.init();
        delete.execute();
    }
}
