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

package org.apache.avalon.tools.model;

import org.apache.tools.ant.Project;

import java.io.File;

/**
 * Defintion of a project. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class Definition extends Resource
{
    private ResourceRef[] m_plugins;
    private File m_basedir;

    public Definition( 
      final Home home, final String key, final File basedir, final Info info,
      final ResourceRef[] resources, final ResourceRef[] plugins )
    {
        super( home, key, info, resources );

        m_basedir = basedir;
        m_plugins = plugins;
    }

    public File getBaseDir()
    {
        return m_basedir;
    }

    public ResourceRef[] getPluginRefs()
    {
        return m_plugins;
    }

    public File getDocDirectory()
    {
        final File cache = getHome().getDocsRepository().getCacheDirectory();
        final String spec = getInfo().getSpecification( "/", "/" );
        return new File( cache, spec );
    }

    public String toString()
    {
        return "[" + getInfo().getGroup() + "/" + getInfo().getName() + "]";
    }

    public boolean equals( final Object other )
    {
        if( super.equals( other ) && ( other instanceof Definition ))
        {
            final Definition def = (Definition) other;

            final ResourceRef[] plugins = getPluginRefs();
            final ResourceRef[] plugins2 = def.getPluginRefs();
            for( int i=0; i<plugins.length; i++ )
            {
                if( !plugins[i].equals( plugins2[i] ) ) return false;
            }
            return true;
        }
        return false;
    }
}
