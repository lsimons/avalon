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
import java.util.List;
import java.util.ArrayList;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.FileSet;

import org.apache.avalon.tools.home.Home;

/**
 * Defintion of a project. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class Definition extends Resource
{
    private ResourceRef[] m_resources;
    private ResourceRef[] m_plugins;
    private File m_basedir;

    public Definition( 
      Home home, String key, File basedir, Info info, 
      ResourceRef[] resources, ResourceRef[] plugins )
    {
        super( home, key, info );

        m_basedir = basedir;
        m_resources = resources;
        m_plugins = plugins;
    }

    public File getBasedir()
    {
        return m_basedir;
    }

    public ResourceRef[] getResourceRefs()
    {
        return m_resources;
    }

    public ResourceRef[] getResourceRefs( int mode, int tag, boolean flag )
    {
        ArrayList list = new ArrayList();
        getResourceRefs( list, mode, tag, flag );
        return (ResourceRef[]) list.toArray( new ResourceRef[0] );
    }

    protected void getResourceRefs( List list, int mode, int tag, boolean flag )
    {
        ResourceRef[] refs = getResourceRefs();
        for( int i=0; i<refs.length; i++ )
        {
            ResourceRef ref = refs[i];
            if( !list.contains( ref ) )
            {
                Policy policy = ref.getPolicy();
                if( policy.matches( mode ) && ref.matches( tag ) )
                {
                    list.add( ref );
                    if( flag && getHome().isaDefinition( ref ) )
                    {
                        Definition def = getHome().getDefinition( ref );
                        def.getResourceRefs( list, mode, ResourceRef.ANY, flag );
                    }
                }
            }
        }
    }

    public ResourceRef[] getPluginRefs()
    {
        return m_plugins;
    }

    public Path getPath( Project project, int mode )
    {
        if( null == project )
        {
            throw new NullPointerException( "project" );
        }

        Path path = new Path( project );
        ResourceRef[] refs = getResourceRefs( mode, ResourceRef.ANY, true );
        for( int i=0; i<refs.length; i++ )
        {
            ResourceRef ref = refs[i];
            Resource resource = getHome().getResource( ref );
            File file = resource.getArtifact( project );
            path.createPathElement().setLocation( file );
        }
        
        return path;
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
            ResourceRef[] refs = getResourceRefs();
            ResourceRef[] references = def.getResourceRefs();
            for( int i=0; i<refs.length; i++ )
            {
                if( !refs[i].equals( references[i] ) ) return false;
            }

            ResourceRef[] plugins = getPluginRefs();
            ResourceRef[] plugins2 = def.getPluginRefs();
            for( int i=0; i<plugins.length; i++ )
            {
                if( !plugins[i].equals( plugins2[i] ) ) return false;
            }
            return true;
        }
        return false;
    }
}
