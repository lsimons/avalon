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
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

import org.apache.avalon.tools.home.Home;
import org.apache.avalon.tools.home.Context;
import org.apache.avalon.tools.project.Definition;

/**
 * Load a goal. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class HomeTask extends ContextualTask
{
    public static final String SYSTEM_KEY = "project.system";

    private static final String CACHE_DIR_KEY = "project.home.cache.dir";
    private static final String USER_PROPERTIES = "user.properties";
    private static final String BUILD_PROPERTIES = "build.properties";
    private static final String INDEX_PROPERTIES = "index.properties";

    private static Home HOME;

    public void init()
    {
        if( !isInitialized() )
        {
            super.init();
            Project project = getProject();
            File index = getIndexFile();
            File system = getSystemHome( project, index );
            setupProperties( project, system );
            if( null == HOME )
            {
                setupSystemProperties( project, system );
                HOME = new Home( project, system, index );
            }
            project.addReference( Home.KEY, HOME );
            
            getProject().setNewProperty( 
              CACHE_DIR_KEY, 
              HOME.getRepository().getCacheDirectory().toString() );
        }
    }

    private File getSystemHome( Project project, File index )
    {
        String system = project.getProperty( SYSTEM_KEY );
        if(( null == system ) || "".equals( system ))
        {
            return index.getParentFile();
        }
        else
        {
            File anchor = project.getBaseDir();
            return Context.getFile( anchor, system );
        }
    }

    private void setupProperties( Project project, File dir )
    {
        setupUserProperties( project, dir );
        setupBuildProperties( project, dir );
    }

    private void setupUserProperties( Project project, File dir )
    {
        File user = Context.getFile( dir, USER_PROPERTIES );
        readProperties( project, user );
    }

    private void setupBuildProperties( Project project, File dir )
    {
        File build = Context.getFile( dir, BUILD_PROPERTIES );
        readProperties( project, build );
    }

    private void setupSystemProperties( Project project, File dir )
    {
        File build = Context.getFile( dir, INDEX_PROPERTIES );
        readProperties( project, build );
    }

    private void readProperties( Project project, File file ) throws BuildException 
    {
        Property props = (Property) project.createTask( "property" );
        props.init();
        props.setFile( file );
        props.execute();
    }

    private File getIndexFile()
    {
        String path = getProject().getProperty( Home.KEY );
        if( null != path )
        {
            File index = Context.getFile( project.getBaseDir(), path );
            if( index.exists() )
            {
                if( index.isDirectory() )
                {
                    return new File( index, "index.xml" );
                }
                else
                {
                    return index;
                }
            }
            else
            {
                final String error = 
                  "Property value 'project.home' references a non-existant file: "
                  + index;
                throw new BuildException( error );
            }
        }
        else
        {
            final String error = 
              "Cannot continue due to unresolved 'project.home' property.";
            throw new BuildException( error );
        }
    }

}
