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
public class JavacTask extends HomeTask
{
    public static final String CLASSES_KEY = "avalon.target.classes";
    public static final String CLASSES_VALUE = "classes";

    public static final String DEBUG_KEY = "java.compile.debug";
    public static final boolean DEBUG_FLAG = false;

    public static final String FORK_KEY = "java.compile.fork";
    public static final boolean FORK_FLAG = false;

    public static File getTargetClassesDirectory( Project project )
    {
        File target = PrepareTask.getTargetDirectory( project );
        String classes = project.getProperty( CLASSES_KEY );
        return new File( target, classes );
    }

    public void init() throws BuildException 
    {
        super.init();
        setProjectProperty( CLASSES_KEY, CLASSES_VALUE );
        setProjectProperty( DEBUG_KEY, "" + DEBUG_FLAG );
        setProjectProperty( FORK_KEY, "" + FORK_FLAG );
    }

    public void execute() throws BuildException 
    {
        File src = getTargetSrcMainDirectory();
        if( src.exists() )
        {
            File classes = getTargetClassesDirectory();
            if( !classes.exists() )
            {
                log( "creating target classes directory" );
                createDirectory( classes );
            }
            Path classpath = 
              getHome().getRepository().createPath( 
                getProject(), getDefinition() );
            compile( src, classes, classpath );
        }
    }

    private File getTargetSrcMainDirectory()
    {
        String src = getProject().getProperty( PrepareTask.TARGET_SRC_MAIN_KEY );
        return new File( getProject().getBaseDir(), src );
    }

    private File getTargetClassesDirectory()
    {
        return getTargetClassesDirectory( getProject() );
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
        return getBooleanProperty( DEBUG_KEY, DEBUG_FLAG );
    }

    private boolean getForkProperty()
    {
        return getBooleanProperty( FORK_KEY, FORK_FLAG );
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
