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
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

import org.apache.avalon.tools.home.Home;
import org.apache.avalon.tools.home.Context;
import org.apache.avalon.tools.project.Definition;

/**
 * Install the target/deliverables content into the local repository
 * cache. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class InstallTask extends SystemTask
{
    public void execute() throws BuildException 
    {
        installDeliverables();
        installDocs();
    }

    private void installDeliverables()
    {
        File deliverables = getContext().getDeliverablesDirectory();
        if( deliverables.exists() )
        {
            File cache = getHome().getRepository().getCacheDirectory();
            FileSet fileset = new FileSet();
            fileset.setDir( deliverables );
            fileset.createInclude().setName( "**/*" );
            String group = getHome().getDefinition( getKey() ).getInfo().getGroup();
            File target = new File( cache, group );
            copy( target, fileset );
        }
    }

    private void installDocs()
    {
        File cache = getHome().getDocsRepository().getCacheDirectory();
        File source = getContext().getDocsDirectory();
        if( source.exists() )
        {
            FileSet fileset = new FileSet();
            fileset.setDir( source );
            fileset.createInclude().setName( "**/*" );
            String group = getHome().getDefinition( getKey() ).getInfo().getGroup();
            String name = getHome().getDefinition( getKey() ).getInfo().getName();
            File parent = new File( cache, group );
            File target = new File( parent, name );
            copy( target, fileset );
        }
    }

    private void copy( File destination, FileSet fileset )
    {
        mkDir( destination );
        Copy copy = (Copy) getProject().createTask( "copy" );
        copy.setPreserveLastModified( true );
        copy.setTodir( destination );
        copy.addFileset( fileset );
        copy.init();
        copy.execute();
    }
}
