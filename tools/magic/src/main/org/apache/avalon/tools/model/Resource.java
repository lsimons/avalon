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

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

/**
 * Defintion of a resource. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class Resource 
{
    private final String m_key;
    private Info m_info;
    private Home m_home;

    public Resource( final Home home, final Info info )
    {
        this( home, null, info );
    }

    public Resource( final Home home, final String key, final Info info )
    {
        m_key = key;
        m_info = info;
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

    protected Home getHome()
    {
        return m_home;
    }

    public File getArtifact( final Project project )
    {
        //
        // TODO: add support for snapshot semantics
        // based on a resource feature
        //

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
                get.setIgnoreErrors( true );
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
            return true;
        }
        return false;
    }
}
