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

package org.apache.avalon.tools.home;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Get;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.taskdefs.Sequential;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.FileList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Text;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.apache.avalon.tools.util.ElementHelper;
import org.apache.avalon.tools.project.Definition;
import org.apache.avalon.tools.project.ResourceRef;
import org.apache.avalon.tools.project.ProjectRef;
import org.apache.avalon.tools.project.Resource;

/**
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class Repository 
{
    private final Home m_home;
    private final File m_root;
    private final String m_path;
    private final File m_cache;
    private final String[] m_hosts;

    public Repository( Home home, Element element )
    {
        m_home = home;
        m_root = m_home.getHomeDirectory();

        Element cache = ElementHelper.getChild( element, "cache" );
        m_path = getCachePath( cache );
        m_cache = getCanonicalFile( new File( m_root, m_path ) );

        Element hosts = ElementHelper.getChild( element, "hosts" );
        m_hosts = getHostsSequence( hosts );
    }

    public File getCacheDirectory()
    {
        return m_cache;
    }

    public String getCachePath()
    {
        return m_path;
    }

    public String[] getHosts()
    {
        return m_hosts;
    }

    public ResourceRef[] getResourceRefs( Definition def )
    {
        ArrayList list = new ArrayList();
        getResourceRefs( def, list );
        return (ResourceRef[]) list.toArray( new ResourceRef[0] );
    }

    public void getResourceRefs( Definition def, List list )
    {
        ResourceRef[] refs = def.getResourceRefs();
        for( int i=0; i<refs.length; i++ )
        {
            ResourceRef ref = refs[i];
            if( !list.contains( ref ) )
            {
                list.add( ref );
            }
        }

        ProjectRef[] projects = def.getProjectRefs();
        for( int i=0; i<projects.length; i++ )
        {
            ProjectRef ref = projects[i];
            if( !list.contains( ref ) )
            {
                Definition defintion = m_home.getDefinition( ref );
                getResourceRefs( defintion, list );
                list.add( ref );
            }
        }
    }

    public Path createPath( Project project, Definition def )
      throws BuildException
    {
        return createPath( project, def, false );
    }

    public Path createPath( Project project, Definition def, boolean flag )
      throws BuildException
    {
        Path path = new Path( project );

        //
        // add the projects direct resources
        //

        ResourceRef[] refs = def.getResourceRefs();

        //
        // load resources into the repository
        //

        int k = 0;
        StringBuffer buffer = new StringBuffer();

        for( int i=0; i<refs.length; i++ )
        {
            ResourceRef ref = refs[i];
            Resource resource = m_home.getResource( ref );
            try
            {
                getResource( project, resource );
                FileList file = getResourceFileList( project, resource );
                path.addFilelist( file );
            }
            catch( Throwable e )
            {
                k++;
                buffer.append( "\n" + k );
                buffer.append( ": " );
                buffer.append( resource.getInfo() );
            }
        }

        if( k > 0 )
        {
            String error = getError( k, def );
            project.log( error );
            project.log( buffer.toString() );
            throw new BuildException( error );
        }

        //
        // add each dependent project's path
        //

        ProjectRef[] projects = def.getProjectRefs();
        for( int i=0; i<projects.length; i++ )
        {
            ProjectRef ref = projects[i];
            Definition defintion = m_home.getDefinition( ref );
            Path projectPath = createPath( project, defintion, true );
            File file = new File( getCacheDirectory(), defintion.getInfo().getPath() );
            if( file.exists() )
            {
                //
                // TODO make sure the project artifact is up-to-date 
                // relative to the project src path
                //
 
                path.add( projectPath );
            }
            else
            {
                final String error = 
                  "Cannot construct a valid path for the project " 
                  + def + " because the dependent project " 
                  + defintion + " has not installed an artifact.";
                throw new BuildException( error ); 
            }
        }

        if( flag )
        {
            File file = new File( getCacheDirectory(), def.getInfo().getPath() );
            if( file.exists() )
            {
                path.createPathElement().setLocation( file );
            }
            else
            {
                final String error = 
                  "Cannot construct a valid path for the project " 
                  + def + " because the project has not installed an artifact.";
                throw new BuildException( error ); 
            }
        }

        return path;
    }

    private String getError( int count, Definition def )
    {
        if( count == 1 )
        {
            return "unresolved resource in project: " + def;
        }
        else
        {
            return "unresolved resources in project: " + def;
        }
    }

    public File getResource( Project project, Resource resource )
      throws Exception
    {
        String path = resource.getInfo().getPath();
        File target = new File( getCacheDirectory(), path );
        if( target.exists() ) 
        {
            return target;
        }

        target.getParentFile().mkdirs();
        String[] hosts = getHosts();
        for( int i=0; i<hosts.length; i++ )
        {
            String host = hosts[i];
            try
            {
                return getResource( project, host, resource );
            }
            catch( Throwable e )
            {
                // ignore
            }
        }

        throw new FileNotFoundException( resource.toString() );
    }

    public File getResource( Project project, String host, Resource resource ) 
      throws Exception
    {
        String path = resource.getInfo().getPath();
        URL url = new URL( host );
        URL source = new URL( url, path );
        File target = new File( getCacheDirectory(), path );

        Get get = (Get) project.createTask( "get" );
        get.setSrc( source );
        get.setDest( target );
        get.setIgnoreErrors( false );
        get.setUseTimestamp( true );
        get.setVerbose( true );
        get.execute();

        return target;
    }

    private FileList getResourceFileList( Project project, Resource resource )
      throws Exception
    {
        FileList list = new FileList();
        list.setProject( project );
        list.setDir( getCacheDirectory() );
        String path = resource.getInfo().getPath();
        list.setFiles( path );
        return list;   
    }

    private File getCanonicalFile( File file ) throws BuildException
    {
        try
        {
            return file.getCanonicalFile();
        }
        catch( IOException ioe )
        {
            throw new BuildException( ioe );
        }
    }

    private String[] getHostsSequence( Element element )
    {
        if( null == element )
        {
            return new String[0];
        }
        
        Element[] children = 
          ElementHelper.getChildren( element, "host" );
        String[] list = new String[ children.length ];
        for( int i=0; i<children.length; i++ )
        {
            Element child = children[i];
            list[i] = ElementHelper.getValue( child );
        }
        return list;
    }

    private String getCachePath( Element element )
    {
        if( null != element )
        {
            String path = element.getAttribute( "dir" );
            if( null != path )
            {
                return path;
            }
        }

        return ".cache";
    }
}