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
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.types.FileSet;

import org.apache.avalon.tools.home.Home;
import org.apache.avalon.tools.project.Definition;
import org.apache.avalon.tools.tasks.HomeTask;

/**
 * Load a goal. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class PrepareTask extends HomeTask
{
    public static String SRC = "src";
    public static String TARGET = "target";
    public static String TARGET_SRC = TARGET + "/src";
    public static String TARGET_SRC_MAIN = TARGET_SRC + "/main";

    public static final String TARGET_KEY = "avalon.target";

    public static File getTargetDirectory( Project project )
    {
        String target = project.getProperty( TARGET_KEY );
        return new File( project.getBaseDir(), target );
    }

    public void init() throws BuildException 
    {
        setProjectProperty( "avalon.src", SRC );
        setProjectProperty( "avalon.target", TARGET );
        setProjectProperty( "avalon.target.src", TARGET_SRC );
        setProjectProperty( "avalon.target.src.main", TARGET_SRC_MAIN );
    }

    public void execute() throws BuildException 
    {
        File target = getTargetDirectory();
        if( !target.exists() )
        {
            log( "creating target directory" );
            createDirectory( target );
        }
        File src = getSrcDirectory();
        if( src.exists() )
        {
            copySrcToBuildWithFiltering( target );
            copySrcToBuildWithoutFiltering( target );
        }
    }

    private File getSrcDirectory()
    {
        String src = getProject().getProperty( "avalon.src" );
        return new File( getProject().getBaseDir(), src );
    }

    private File getTargetDirectory()
    {
        return getTargetDirectory( getProject() );
    }

    private void copySrcToBuildWithFiltering( File target )
    {
        copySrcToBuild( target, true, "**/*.java,**/*.x*,**/*.properties", "" );
    }

    private void copySrcToBuildWithoutFiltering( File target )
    {
        copySrcToBuild( target, false, "**/*.*", "**/*.java,**/*.x*,**/*.properties" );
    }

    private void copySrcToBuild( 
       File target, boolean filtering, String includes, String excludes )
    {
        File targetSrc = new File( target, SRC );
        Copy copy = (Copy) getProject().createTask( "copy" );
        copy.setTodir( targetSrc );
        copy.setFiltering( filtering );
        copy.setOverwrite( false );

        FileSet fileset = new FileSet();
        fileset.setDir( getSrcDirectory() );
        fileset.setIncludes( includes );
        fileset.setExcludes( excludes );
        copy.addFileset( fileset );

        copy.init();
        copy.execute();
    }

}
