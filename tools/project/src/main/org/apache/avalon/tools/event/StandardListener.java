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

package org.apache.avalon.tools.event;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Sequential;

import org.apache.avalon.tools.home.Home;
import org.apache.avalon.tools.project.Definition;
import org.apache.avalon.tools.project.ResourceRef;
import org.apache.avalon.tools.project.Plugin;

/**
 * An abstract build listener. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class StandardListener extends AbstractListener
{
    private static final String BANNER = 
      "------------------------------------------------------------------------------";

    private Home m_home;
    private Definition m_definition;
    private Task m_task;

    public StandardListener( Home home, Definition definition )
    {
        this( null, home, definition );
    }

    public StandardListener( Task task, Home home, Definition definition )
    {
        m_home = home;
        m_definition = definition;
    }

    /**
     * Signals that a build has started. This event
     * is fired before any targets have started.
     *
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     */
    public void buildStarted(BuildEvent event)
    {
        Project project = event.getProject();
        project.log( "" );
        project.log( BANNER );
        project.log( "project: " + getDefinition() );
        project.log( "basedir: " + project.getBaseDir() );
        project.log( BANNER );
    }

    private Task getTask()
    {
        return m_task;
    }

    private Home getHome()
    {
        return m_home;
    }

    private Definition getDefinition()
    {
        return m_definition;
    }
   
}
