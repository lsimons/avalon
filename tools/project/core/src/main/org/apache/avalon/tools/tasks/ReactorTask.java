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
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Mkdir;
import org.apache.tools.ant.taskdefs.Checksum;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.Path;

import org.apache.avalon.tools.home.Context;
import org.apache.avalon.tools.home.Home;
import org.apache.avalon.tools.project.Definition;
import org.apache.avalon.tools.project.ProjectRef;
import org.apache.avalon.tools.project.PluginRef;

/**
 * Build a set of projects taking into account dependencies within the 
 * supplied fileset. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class ReactorTask extends SystemTask
{
    private static final String BANNER = 
      "------------------------------------------------------------------------";
    private Path m_path;
    private List m_defs;

    public void init()
    {
        m_path = new Path( getProject() );
    }

    public void addConfigured( final Path path )
    {
        m_path.add( path );
    }

    public void addConfigured( final DirSet dirset )
    {
        m_path.addDirset( dirset );
    }

    public void addConfigured( final FileSet fileset )
    {
        m_path.addFileset( fileset );
    }

    public void addConfigured( final FileList list ) 
    {
        m_path.addFilelist( list );
    }

    public void execute() throws BuildException 
    {
        final Project project = getProject();
        m_defs = getDefinitions();
        final Definition[] defs = walkGraph();
        log( "Build sequence for project group:" );
        project.log( BANNER );
        for( int i=0; i<defs.length; i++ )
        {
            final Definition def = defs[i];
            project.log( def.toString() );
        }
        project.log( BANNER );
        for( int i=0; i<defs.length; i++ )
        {
            final Definition def = defs[i];
            try
            {
                build( def );
            }
            catch( Throwable e )
            {
                throw new BuildException( e );
            }
        }
    }

    public void build( final Definition definition )
    {
        final Ant ant = (Ant) getProject().createTask( "ant" );
        ant.setDir( definition.getBasedir() );
        ant.setInheritRefs( false );
        ant.setInheritAll( false );
        ant.init();
        ant.execute();
    }

    private Definition[] walkGraph()
    {
        final ArrayList result = new ArrayList();
        final ArrayList done = new ArrayList();

        final int size = m_defs.size();
        for( int i = 0; i < size; i++ )
        {
            final Definition def = (Definition) m_defs.get( i );
            visit( def, done, result );
        }

        final Definition[] returnValue = new Definition[result.size()];
        return (Definition[]) result.toArray( returnValue );
    }

    private void visit( final Definition def, final ArrayList done,
            final ArrayList order )
    {
        if( done.contains( def ) ) return;
        done.add( def );
        visitProviders( def, done, order );
        order.add( def );
    }

    private void visitProviders( 
      final Definition def, final ArrayList done, final ArrayList order )
    {
        final Definition[] providers = getProviders( def );
        for( int i = (providers.length - 1); i > -1; i-- )
        {
            visit( providers[i], done, order );
        }
    }

    private Definition[] getProviders( final Definition def )
    {
        final ArrayList list = new ArrayList();
        final ProjectRef[] refs = def.getProjectRefs();
        for( int i=0; i<refs.length; i++ )
        {
            final Definition d = getHome().getDefinition( refs[i] );
            if( m_defs.contains( d ) )
            {
                list.add( d );
            }
        }
        final PluginRef[] prefs = def.getPluginRefs();
        for( int i=0; i<prefs.length; i++ )
        {
            final Definition d = getHome().getPlugin( prefs[i] );
            if( m_defs.contains( d ) )
            {
                list.add( d );
            }
        }
        return (Definition[]) list.toArray( new Definition[0] );
    }

    private List getDefinitions()
    {
        final ArrayList list = new ArrayList();
        final Project project = getProject();
        final File basedir = project.getBaseDir();
        final String[] names = m_path.list();
        for( int i=0; i<names.length; i++ )
        {
            final String path = names[i];
            final File file = Context.getFile( basedir, path );
            final File dir = getDir( file );
            final File props = new File( dir, "build.xml" );
            if( file.exists() )
            {
                final String key = getProjectName( dir );
                if( null != key )
                {
                    list.add( getHome().getDefinition( key ) );
                }
                else
                {
                    log( "Skipping dir: " 
                      + dir 
                      + "due to unresolve project name." );
                }
            }
            else
            {
                log( "Skipping dir: " 
                  + dir 
                  + "due to missing build.properties file" );
            }
        }
        return list;
    }

    private String getProjectName( final File dir )
    {
        try
        {
            final Properties properties = new Properties();
            final File props = new File( dir, "build.properties" );
            final InputStream stream = new FileInputStream( props );
            properties.load( stream );
            return properties.getProperty( "project.name" );
        }
        catch( IOException ioe )
        {
            throw new BuildException( ioe );
        }
    }

    private File getDir( final File file )
    {
        if( file.isDirectory() ) return file;
        return file.getParentFile();
    }
}
