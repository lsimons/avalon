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

package org.apache.avalon.tools.project;

import java.io.File;

/**
 * Defintion of a project. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class Definition extends Resource
{
    private ProjectRef[] m_projects;
    private ResourceRef[] m_resources;
    private PluginRef[] m_plugins;
    private File m_basedir;

    public Definition( 
      String key, File basedir, Info info, 
      ResourceRef[] resources, ProjectRef[] projects, PluginRef[] plugins )
    {
        super( key, info );

        m_basedir = basedir;
        m_projects = projects;
        m_resources = resources;
        m_plugins = plugins;
    }

    public File getBasedir()
    {
        return m_basedir;
    }

    public ProjectRef[] getProjectRefs()
    {
        return m_projects;
    }

    public ResourceRef[] getResourceRefs()
    {
        return m_resources;
    }

    public PluginRef[] getPluginRefs()
    {
        return m_plugins;
    }

    public String toString()
    {
        return "[" + getInfo().getGroup() + "/" + getInfo().getName() + "]";
    }

    public boolean equals( Object other )
    {
        if( super.equals( other ) && ( other instanceof Definition ))
        {
            Definition def = (Definition) other;
            ProjectRef[] refs = getProjectRefs();
            ProjectRef[] refs2 = def.getProjectRefs();
            for( int i=0; i<refs.length; i++ )
            {
                if( !refs[i].equals( refs2[i] ) ) return false;
            }

            ResourceRef[] resources = getResourceRefs();
            ResourceRef[] resources2 = def.getResourceRefs();
            for( int i=0; i<resources.length; i++ )
            {
                if( !resources[i].equals( resources2[i] ) ) return false;
            }
            PluginRef[] plugins = getPluginRefs();
            PluginRef[] plugins2 = def.getPluginRefs();
            for( int i=0; i<plugins.length; i++ )
            {
                if( !plugins[i].equals( plugins2[i] ) ) return false;
            }
            return true;
        }
        return false;
    }
}
