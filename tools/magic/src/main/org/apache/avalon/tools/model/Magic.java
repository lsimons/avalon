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
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.taskdefs.optional.net.SetProxy;
import org.apache.tools.ant.types.DataType;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class Magic extends DataType
{
    //-------------------------------------------------------------
    // static
    //-------------------------------------------------------------

    public static final String KEY = "magic.home";

    public static final String HOSTS_KEY = "magic.hosts";
    public static final String MAIN_CACHE_KEY = "magic.cache";
    public static final String DOCS_CACHE_KEY = "magic.docs";
    public static final String TEMPLATES_KEY = "magic.templates";

    public static final String PROXY_HOST_KEY = "magic.proxy.host";
    public static final String PROXY_PORT_KEY = "magic.proxy.port";
    public static final String PROXY_USERNAME_KEY = "magic.proxy.username";
    public static final String PROXY_PASSWORD_KEY = "magic.proxy.password";

    private static Magic SYSTEM;
    private static Repository MAIN;
    private static Repository DOCS;

    public static Magic getSystem( Project project )
    {
        if( null == SYSTEM )
        {
            SYSTEM = new Magic( project );
        }

        File system = SYSTEM.getSystemDirectory();
        project.setProperty( 
          KEY, system.toString() );

        File main = SYSTEM.getRepository().getCacheDirectory();
        project.setProperty( 
          MAIN_CACHE_KEY, Context.getCanonicalPath( main ) );

        File docs = SYSTEM.getRepository().getCacheDirectory();
        project.setProperty( 
          DOCS_CACHE_KEY, Context.getCanonicalPath( docs ) );

        project.setProperty( 
          TEMPLATES_KEY, getTemplatePath( system ) );
        project.addReference( KEY, SYSTEM );
        return SYSTEM;
    }

    private static String getTemplatePath( File system )
    {
        File templates = new File( system, "templates" );
        return Context.getCanonicalPath( templates ); 
    }

    //-------------------------------------------------------------
    // mutable state
    //-------------------------------------------------------------

    private File m_system;
    private Repository m_main;
    private Repository m_docs;
    private Map m_homes = new Hashtable();

    //-------------------------------------------------------------
    // constructor
    //-------------------------------------------------------------

    private Magic( final Project project )
    {
        setProject( project );

        m_system = getSystemDirectory( project );

        File user = new File( m_system, "user.properties" );
        loadProperties( project, user );

        File properties = new File( m_system, "magic.properties" );
        loadProperties( project, properties );

        final String hostsPath = project.getProperty( HOSTS_KEY );
        final String[] hosts = getHostsSequence( hostsPath );

        final File main = new File( m_system, "main" );
        m_main = new Repository( project, main, hosts );

        final File docs = new File( m_system, "docs" );
        m_docs = new Repository( project, docs, hosts );

        setupProxy( project );

        project.log( "Hosts: " + hosts.length, Project.MSG_VERBOSE );
        for( int i=0; i<hosts.length; i++ )
        {
            project.log( "  host: " + hosts[i], Project.MSG_VERBOSE ); 
        }
        project.log( 
          "artifact cache: " + m_main.getCacheDirectory(), 
          Project.MSG_VERBOSE );
        project.log( 
          "docs cache: " + m_docs.getCacheDirectory(), 
          Project.MSG_VERBOSE );
    }

    //-------------------------------------------------------------
    // public
    //-------------------------------------------------------------

    public File getSystemDirectory()
    {
        return m_system;
    }

    public Repository getRepository()
    {
        return m_main;
    }

    public Repository getDocsRepository()
    {
        return m_docs;
    }

    public Home getHome( Project project, String value )
    {
        File index = getIndexFile( project, value );
        String path = Context.getCanonicalFile( index ).toString();
        Home home = (Home) m_homes.get( path );
        if( null == home ) 
        {
            project.log( 
              "Creating home in project: " 
              + project.getName() 
              + " using index ["
              + index
              + "].", Project.MSG_VERBOSE );

            home = new Home( project, this, index );
        }
        m_homes.put( path, home );
        return home;
    }

    private File getIndexFile( Project project, String value )
    {
        File basedir = project.getBaseDir();
        if( null != value )
        {
            File index = Context.getFile( basedir, value );
            return resolve( index );
        }

        final String path = project.getProperty( Home.KEY );
        if( null != path )
        {
            final File index = Context.getFile( basedir, path ) ;
            return resolve( index );
        }
        else
        {
            final String error = 
              "Cannot continue due to missing index attribute.";
            throw new BuildException( error );
        }
    }

    private File resolve( final File index )
    {
        if( index.exists() )
        {
            if( index.isDirectory() )
            {
                return resolve( new File( index, "index.xml" ) );
            }
            else
            {
                return index;
            }
        }
        else
        {
            final FileNotFoundException e =
              new FileNotFoundException( index.toString() );
            throw new BuildException( e );
        }
    }

    //-------------------------------------------------------------
    // implementation
    //-------------------------------------------------------------

    private String[] getHostsSequence( final String path )
    {
        if( null == path )
        {
            return new String[0];
        }
        
        final StringTokenizer tokenizer = new StringTokenizer( path, ";" );
        final ArrayList list = new ArrayList();
        while( tokenizer.hasMoreTokens() )
        {
            final String host = tokenizer.nextToken();
            if( host.endsWith( "/" ) )
            {
                list.add( host );
            }
            else
            {
                list.add( host + "/" );
            }
        }
        return (String[]) list.toArray( new String[0] );
    }

    private void setupProxy( final Project project )
    {
        final String host = project.getProperty( PROXY_HOST_KEY );
        if(( null == host ) || "".equals( host ) )
        {
            return;
        }
        else
        {
            final int port = Integer.decode(
              project.getProperty( PROXY_PORT_KEY ) ).intValue();
            final String username =
              project.getProperty( PROXY_USERNAME_KEY );
            final String password =
              project.getProperty( PROXY_PASSWORD_KEY );
            final SetProxy proxy =
              (SetProxy) project.createTask( "setproxy" );
            proxy.init();
            proxy.setProxyHost( host );
            proxy.setProxyPort( port );
            proxy.setProxyUser( username );
            proxy.setProxyPassword( password );
            proxy.execute();
        }
    }

    private File getSystemDirectory( final Project project )
    {
        final Property property = (Property) project.createTask( "property" );
        property.setEnvironment( "env" );
        property.init();
        property.execute();

        final String systemPath = project.getProperty( "env.MAGIC_HOME" );
        if( null != systemPath )
        {
            File system = new File( systemPath );
            return Context.getCanonicalFile( system );
        }
        else
        {
            File user = new File( System.getProperty( "user.home" ) );
            File system = new File( user, ".merlin" );
            return Context.getCanonicalFile( system );
        }
    }

    protected void loadProperties( 
      final Project project, final File file ) throws BuildException
    {
        final Property props = (Property) project.createTask( "property" );
        props.init();
        props.setFile( file );
        props.execute();
    }
}
