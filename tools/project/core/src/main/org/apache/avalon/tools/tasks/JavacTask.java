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
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.taskdefs.Mkdir;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

import org.apache.avalon.tools.home.Context;
import org.apache.avalon.tools.home.Home;
import org.apache.avalon.tools.project.Definition;

/**
 * Compile sources.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class JavacTask extends SystemTask
{
    public static final String BUILD_CLASSES_KEY = "classes";
    public static final String BUILD_CLASSES_PATH = "classes";

    public static final String DEBUG_KEY = "java.compile.debug";
    public static final boolean DEBUG_VALUE = false;

    public static final String FORK_KEY = "java.compile.fork";
    public static final boolean FORK_VALUE = false;

    public void init() throws BuildException 
    {
        if( !isInitialized() )
        {
            super.init();
            Project project = getProject();
            project.setNewProperty( DEBUG_KEY, "" + DEBUG_VALUE );
            project.setNewProperty( FORK_KEY, "" + FORK_VALUE );
            getContext().setBuildPath( 
              BUILD_CLASSES_KEY, 
              BUILD_CLASSES_PATH );
        }
    }

    public void execute() throws BuildException 
    {
        Project project = getProject();
        File build = getContext().getBuildDirectory();
        String mainPath = project.getProperty( Context.SRC_MAIN_KEY );
        File main = new File( build, mainPath );

        if( main.exists() )
        {
            File classes = getContext().getBuildPath( BUILD_CLASSES_KEY );
            mkDir( classes );

            Path classpath = 
              getHome().getRepository().createPath( 
                getProject(), getHome().getDefinition( getKey() ) );
            compile( main, classes, classpath );

            Copy copy = (Copy) getProject().createTask( "copy" );
            copy.setPreserveLastModified( true );
            copy.setTodir( classes );

            FileSet fileset = new FileSet();
            fileset.setDir( main );
            fileset.setIncludes( "**/**" );
            fileset.setExcludes( "**/*.java,**/package.html" );
            copy.addFileset( fileset );
            copy.init();
            copy.execute();
        }
        else
        {
            log( "no src main to compile : " + main.toString() );
        }
    }

    private void compile( File sources, File classes, Path classpath )
    {        
        File basedir = getProject().getBaseDir();
        Javac javac = (Javac) getProject().createTask( "javac" );
        Path src = javac.createSrc();
        Path.PathElement element = src.createPathElement();
        element.setLocation( sources );
        javac.setDestdir( classes );
        javac.setDebug( getDebugProperty() );
        javac.setFork( getForkProperty() );
        javac.setClasspath( classpath );
        javac.init();
        javac.execute();
    }

    private boolean getDebugProperty()
    {
        return getBooleanProperty( DEBUG_KEY, DEBUG_VALUE );
    }

    private boolean getForkProperty()
    {
        return getBooleanProperty( FORK_KEY, FORK_VALUE );
    }

    private boolean getBooleanProperty( String key, boolean fallback )
    {
        String value = getProject().getProperty( key );
        if( null == value )
        {
            return fallback;
        }
        else
        {
            return getProject().toBoolean( value );
        }
    }
}
