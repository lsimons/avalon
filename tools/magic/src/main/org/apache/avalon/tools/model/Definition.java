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
import org.apache.tools.ant.types.Path;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
      final Home home, final String key, final File basedir, final Info info,
      final ResourceRef[] resources, final ResourceRef[] plugins )
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

    public ResourceRef[] getResourceRefs( final int mode, final int tag, final boolean flag )
    {
        final ArrayList list = new ArrayList();
        getResourceRefs( list, mode, tag, flag );
        return (ResourceRef[]) list.toArray( new ResourceRef[0] );
    }

    protected void getResourceRefs( final List list, final int mode, final int tag, final boolean flag )
    {
        final ResourceRef[] refs = getResourceRefs();
        for( int i=0; i<refs.length; i++ )
        {
            final ResourceRef ref = refs[i];
            if( !list.contains( ref ) )
            {
                final Policy policy = ref.getPolicy();
                if( policy.matches( mode ) && ref.matches( tag ) )
                {
                    list.add( ref );
                    if( flag && getHome().isaDefinition( ref ) )
                    {
                        final Definition def = getHome().getDefinition( ref );
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

    public Path getPath( final Project project, final int mode )
    {
        if( null == project )
        {
            throw new NullPointerException( "project" );
        }

        final Path path = new Path( project );
        final ResourceRef[] refs = getResourceRefs( mode, ResourceRef.ANY, true );
        for( int i=0; i<refs.length; i++ )
        {
            final ResourceRef ref = refs[i];
            final Resource resource = getHome().getResource( ref );
            final File file = resource.getArtifact( project );
            path.createPathElement().setLocation( file );
        }
        
        return path;
    }

    public ResourceRef[] getQualifiedRefs( final List visited, final int category )
    {
        final ArrayList list = new ArrayList();
        final ResourceRef[] refs =
          getResourceRefs( Policy.RUNTIME, category, true );
        for( int i=0; i<refs.length; i++ )
        {
            final ResourceRef ref = refs[i];
            if( !visited.contains(  ref ) )
            {
                list.add( ref );
                visited.add( ref );
            }
        }
        return (ResourceRef[]) list.toArray( new ResourceRef[0] );
    }

    public File getDocDirectory()
    {
        final File cache = getHome().getDocsRepository().getCacheDirectory();
        final File root = new File( cache, getInfo().getGroup() );
        final File artifact = new File( root, getInfo().getName() );
        final String version = getInfo().getVersion();
        if( null == version )
        {
            return artifact;
        }
        else
        {
            return new File( artifact, version );
        }
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
            final ResourceRef[] refs = getResourceRefs();
            final ResourceRef[] references = def.getResourceRefs();
            for( int i=0; i<refs.length; i++ )
            {
                if( !refs[i].equals( references[i] ) ) return false;
            }

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
