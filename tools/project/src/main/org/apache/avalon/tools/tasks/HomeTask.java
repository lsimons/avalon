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
 * Load a goal. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class HomeTask extends ContextualTask
{
    private static final String CACHE_DIR_KEY = "project.home.cache.dir";

    private static Home HOME;

    private String m_id;

   /**
    * Set the home ref id to the system home.  If not supplied the 
    * default system home 'project.home' id will apply.
    *
    * @param id a home id
    */
    public void setRefid( String id )
    {
        m_id = id;
    }

    public void init() 
    {
        if( !isInitialized() )
        {
            super.init();
            if( null == HOME )
            {
                HOME = new Home( getProject(), Home.KEY );
            }
            getProject().addReference( Home.KEY, HOME );
            getProject().setNewProperty( 
              CACHE_DIR_KEY, 
              HOME.getRepository().getCacheDirectory().toString() );
        }
    }

    private String getHomeID()
    {
        if( null == m_id )
        {
            return Home.KEY;
        }
        else
        {
            return m_id;
        }
    }

    private Home getHomeFromReference( String id )
    {
        Object object = getProject().getReference( id );
        if( null == object )
        {
            return null;
        }
        if( object instanceof Home )
        {
            return (Home) object;
        }
        else
        {
            final String error = 
              "System home ref id '" + id 
              + "' declared or implied in task [" 
              + getTaskName() 
              + "] in the project ["
              + getProject().getName() 
              + "] references a object that is not a system home.";
            throw new BuildException( error );
        }
    }
}
