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
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Mkdir;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;

import org.apache.avalon.tools.model.Context;
import org.apache.avalon.tools.model.Definition;
import org.apache.avalon.tools.model.ResourceRef;
import org.apache.avalon.tools.model.Resource;

/**
 * The initialize task loads and plugins that a project
 * has declared under the &lt;plugins&gt; element. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class InitializeTask extends SystemTask
{
    public void execute() throws BuildException 
    {
        Project project = getProject();

        //
        // if the project declares plugin dependencies then install
        // these now
        //

        String key = getContext().getKey();
        Definition def = getHome().getDefinition( key );
        ResourceRef[] refs = def.getPluginRefs();
        for( int i=0; i<refs.length; i++ )
        {
            ResourceRef ref = refs[i];
            Resource plugin = getHome().getResource( ref );
            String path = "plugin:" + plugin.getInfo().getSpec();
            PluginTask task = new PluginTask();
            task.setTaskName( "plugin" );
            task.setProject( project );
            task.setArtifact( path );
            task.init();
            task.execute();
        }
    }
}
