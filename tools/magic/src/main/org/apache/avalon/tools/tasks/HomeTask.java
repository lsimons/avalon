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
import org.apache.avalon.tools.model.Definition;
import org.apache.avalon.tools.model.Info;
import org.apache.avalon.tools.model.Home;
import org.apache.avalon.tools.model.Magic;
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
    private String m_path;

    public void setIndex( final String path )
    {
        m_path = path;
    }

    public void execute()
    {
        final Project project = getProject();

        if( null != getProject().getReference( Home.KEY ) ) return;

        Magic system = Magic.getSystem( project );
        Home home = system.getHome( project, m_path );
        project.setProperty( Home.HOME_KEY, getHomePath( home ) );
        project.setProperty( Home.INDEX_KEY, getIndexPath( home ) );
        project.addReference( Home.KEY, home );

        final String key = getKey();
        if( home.isaResourceKey( key ) )
        {
            final Definition def = home.getDefinition( getKey() );
            final Info info = def.getInfo();
            final String name = info.getName();
            project.setProperty( "project.name", name );
            final String group = info.getGroup();
            project.setProperty( "project.group", group );
            final String version = info.getVersion();
            if( null != version )
            {
                project.setProperty( "project.version", version );
            }
        }
    }

    private String getHomePath( Home home )
    {
        return home.getIndex().getParentFile().toString();
    }

    private String getIndexPath( Home home )
    {
        return home.getIndex().toString();
    }
}
