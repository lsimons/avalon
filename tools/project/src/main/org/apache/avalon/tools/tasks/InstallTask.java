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
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Jar;
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
public class InstallTask extends HomeTask
{
    public void execute() throws BuildException 
    {
        String filename = JarTask.getJarFilename( getDefinition() );
        File jar = JarTask.getJarFile( getProject(), getDefinition() );
        if( jar.exists() )
        {
            install();
        }
    }

    private void install()
    {
        FileSet fileset = new FileSet();
        fileset.setDir( PrepareTask.getTargetDirectory( getProject() ) );
        String filename = JarTask.getJarFilename( getDefinition() );
        fileset.createInclude().setName( filename );
        fileset.createInclude().setName( filename + "." + JarTask.MD5_EXT );

        File cache = getHome().getRepository().getCacheDirectory();
        String group = getDefinition().getInfo().getGroup();
        String type = getDefinition().getInfo().getType();
        File repoGroup = new File( cache, group );
        File repoType = new File( repoGroup, type + "s" );

        Copy copy = (Copy) getProject().createTask( "copy" );
        copy.addFileset( fileset );
        copy.setTodir( repoType );

        copy.init();
        copy.execute();
    }
}
