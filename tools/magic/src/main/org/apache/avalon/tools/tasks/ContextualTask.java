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
import org.apache.tools.ant.taskdefs.Mkdir;
import org.apache.tools.ant.taskdefs.optional.junit.FormatterElement;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTask;
import org.apache.tools.ant.taskdefs.optional.junit.BatchTest;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

import org.apache.avalon.tools.model.Home;
import org.apache.avalon.tools.model.Context;
import org.apache.avalon.tools.model.Definition;

/**
 * Load a goal. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public abstract class ContextualTask extends Task
{
    private static final String USER_PROPERTIES = "user.properties";
    private static final String BUILD_PROPERTIES = "build.properties";

    private boolean m_init = false;
    private Context m_context;

    public void init() throws BuildException 
    {
        if( !isInitialized() )
        {
            Project project = getProject();
            setupProperties( project, project.getBaseDir() );
            m_context = Context.getContext( project );
            m_init = true;
        }
    }

    public boolean isInitialized()
    {
        return m_init;
    }

    public String getKey()
    {
        return getContext().getKey();
    }

    public Context getContext()
    {
        if( null == m_context )
        {
            throw new IllegalStateException( "context" );
        }
        return m_context;
    }

    public void mkDir( File dir )
    {
        Mkdir mkdir = (Mkdir) getProject().createTask( "mkdir" );
        mkdir.setDir( dir );
        mkdir.init();
        mkdir.execute();
    }

    protected void setupProperties( Project project, File dir )
    {
        setupUserProperties( project, dir );
        setupBuildProperties( project, dir );
    }

    private void setupUserProperties( Project project, File dir )
    {
        File user = Context.getFile( dir, USER_PROPERTIES );
        loadProperties( project, user );
    }

    private void setupBuildProperties( Project project, File dir )
    {
        File build = Context.getFile( dir, BUILD_PROPERTIES );
        loadProperties( project, build );
    }

    protected void loadProperties( Project project, File file ) throws BuildException 
    {
        Property props = (Property) project.createTask( "property" );
        props.init();
        props.setFile( file );
        props.execute();
    }
}
