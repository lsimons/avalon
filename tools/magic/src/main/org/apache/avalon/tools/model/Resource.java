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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Get;
import org.apache.tools.ant.types.Path;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Defintion of a resource. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class Resource 
{
    private static final ResourceRef[] EMPTY_REFS = new ResourceRef[0];

    private final String m_key;
    private Info m_info;
    private ResourceRef[] m_resources;
    private Home m_home;

    public Resource( final Home home, final Info info )
    {
        this( home, null, info, EMPTY_REFS );
    }

    public Resource( 
      final Home home, final String key, final Info info, final ResourceRef[] resources )
    {
        m_key = key;
        m_info = info;
        m_resources = resources;
        m_home = home;
    }

    public String getKey()
    {
        return m_key;
    }

    public Info getInfo()
    {
        return m_info;
    }

    public ResourceRef[] getResourceRefs()
    {
        return m_resources;
    }

    protected Home getHome()
    {
        return m_home;
    }

    public ResourceRef[] getResourceRefs( final int mode, final int tag, final boolean flag )
    {
        final ArrayList list = new ArrayList();
        getResourceRefs( list, mode, tag, flag );
        return (ResourceRef[]) list.toArray( new ResourceRef[0] );
    }

    protected void getResourceRefs( 
      final List list, final int mode, final int tag, final boolean flag )
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
                    if( flag )
                    {
                        final Resource def = getHome().getResource( ref );
                        def.getResourceRefs( list, mode, ResourceRef.ANY, flag );
                    }
                }
            }
        }
    }

    public Path getPath( final Project project, final int mode )
    {
        if( null == project )
        {
            throw new NullPointerException( "project" );
        }

        final ArrayList visited = new ArrayList();
        final Path path = new Path( project );
        final ResourceRef[] refs = getResourceRefs( mode, ResourceRef.ANY, true );
        for( int i=0; i<refs.length; i++ )
        {
            final ResourceRef ref = refs[i];
            if( !visited.contains( ref ) )
            {
                final Resource resource = getHome().getResource( ref );
                final File file = resource.getArtifact( project );
                path.createPathElement().setLocation( file );
                visited.add( ref );
            }
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
            if( !visited.contains( ref ) )
            {
                list.add( ref );
                visited.add( ref );
            }
        }
        return (ResourceRef[]) list.toArray( new ResourceRef[0] );
    }


    public File getArtifact( final Project project )
    {
        final String path = getInfo().getPath();
        final File cache = getHome().getRepository().getCacheDirectory();
        final File target = new File( cache, path );
        if( target.exists() ) 
        {
            return target;
        }
        else
        {
            return get( project, target, path );
        }
    }

    private File get( final Project project, final File target, final String path )
    {
        target.getParentFile().mkdirs();
        final String[] hosts = getHome().getRepository().getHosts();
        for( int i=0; i<hosts.length; i++ )
        {
            final String host = hosts[i];
            try
            {
                final URL url = new URL( host );
                final URL source = new URL( url, path );

                final Get get = (Get) project.createTask( "get" );
                get.setSrc( source );
                get.setDest( target );
                get.setIgnoreErrors( false );
                get.setUseTimestamp( true );
                get.setVerbose( false );
                get.execute();

                return target;
            }
            catch( Throwable e )
            {
                // ignore
            }
        }
        throw new BuildException( new FileNotFoundException( path ) );
    }

    public String getFilename()
    {
        return getFilename( getInfo().getType() );
    }

    public String getFilename( final String type )
    {
        final String name = getInfo().getName();
        if( null != getInfo().getVersion() )
        {
            return name + "-" + getInfo().getVersion() + "." + type;
        }
        else
        {
            return name + "." + type;
        }
    }

    public String toString()
    {
        return "[" + getInfo().toString() + "]";
    }

    public boolean equals( final Object other )
    {
        if( other instanceof Resource )
        {
            final Resource def = (Resource) other;
            if( !getInfo().equals( def.getInfo() ) ) return false;

            final ResourceRef[] refs = getResourceRefs();
            final ResourceRef[] references = def.getResourceRefs();
            for( int i=0; i<refs.length; i++ )
            {
                if( !refs[i].equals( references[i] ) ) return false;
            }

            return true;
        }
        return false;
    }
}
