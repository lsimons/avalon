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

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Checksum;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

import org.apache.avalon.tools.home.Home;
import org.apache.avalon.tools.project.Definition;

/**
 * Load a goal. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public abstract class DeliverableTask extends HomeTask
{
    public static final String DELIVERABLES_KEY = "avalon.target.deliverables";
    public static final String DELIVERABLES_VALUE = "deliverables";

    public static File getTargetDeliverablesDirectory( Project project )
    {
        File target = PrepareTask.getTargetDirectory( project );
        String deliverables = project.getProperty( DELIVERABLES_KEY );
        return new File( target, deliverables );
    }

    public static File getTargetDeliverablesTypeDirectory( 
      Project project, Definition def )
    {
        File deliverables = getTargetDeliverablesDirectory( project );
        return new File( deliverables, def.getInfo().getType() + "s" );
    }

    public void init() throws BuildException 
    {
        super.init();
        setProjectProperty( DELIVERABLES_KEY, DELIVERABLES_VALUE );
        createDirectory( getTargetDeliverablesTypeDirectory() );
    }

    protected File getTargetDeliverablesDirectory()
    {
        return getTargetDeliverablesDirectory( getProject() );
    }

    protected File getTargetDeliverablesTypeDirectory()
    {
        return getTargetDeliverablesTypeDirectory( 
          getProject(), getDefinition() );
    }

}
