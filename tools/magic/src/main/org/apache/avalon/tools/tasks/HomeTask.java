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

import org.apache.avalon.tools.model.Context;
import org.apache.avalon.tools.model.Home;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import java.io.File;
import java.io.FileNotFoundException;

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
    private static final String INDEX_PROPERTIES = "index.properties";

    private static Home HOME;

    private String m_path;

    private boolean m_executed = false;

    public void setIndex( final String path )
    {
        if( null != HOME )
        {
            log( 
              "Ignoring path - home is already initialized.", 
              Project.MSG_VERBOSE );
        }
        else
        {
            m_path = path;
        }
    }

    public void execute()
    {
        if( !m_executed )
        {
            final Project project = getProject();
            final File index = getIndexFile();
            final File system = getSystemHome( project, index );
            setupProperties( project, system );
            if( null == HOME )
            {
                log( "Building system definition." );
                setupSystemProperties( project, system );
                HOME = new Home( project, system, index );
            }
            project.addReference( Home.KEY, HOME );
            
            getProject().setNewProperty( 
              CACHE_DIR_KEY, 
              HOME.getRepository().getCacheDirectory().toString() );

            m_executed = true;
        }
    }

    private File getSystemHome( final Project project, final File index )
    {
        final String system = project.getProperty( SYSTEM_KEY );
        if(( null == system ) || "".equals( system ))
        {
            final File systemHome = index.getParentFile();
            return systemHome;
        }
        else
        {
            final File anchor = project.getBaseDir();
            return Context.getFile( anchor, system );
        }
    }

    private void setupSystemProperties( final Project project, final File dir )
    {
        final File build = Context.getFile( dir, INDEX_PROPERTIES );
        loadProperties( project, build );
    }

    private File getIndexFile()
    {
        if( null != m_path )
        {
            final File index = Context.getFile( project.getBaseDir(), m_path );
            return resolve( index );
        }
        else
        {

            //
            // try to resolve using ${project.home}
            //

            final String path = getProject().getProperty( Home.KEY );
            if( null != path )
            {
                final File root = Context.getFile( project.getBaseDir(), path );
                return resolve( root );
            }
            else
            {
                final String error = 
                  "Property value 'project.home' is not defined.";
                throw new BuildException( error );
            }
        }
    }

    private File resolve( final File index )
    {
        if( index.exists() )
        {
            if( index.isDirectory() )
            {
                return resolve( new File( index, "index.xml" ) );
            }
            else
            {
                return index;
            }
        }
        else
        {
            final FileNotFoundException e =
              new FileNotFoundException( index.toString() );
            throw new BuildException( e );
        }
    }
}
