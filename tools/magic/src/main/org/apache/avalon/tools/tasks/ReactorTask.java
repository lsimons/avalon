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
import org.apache.avalon.tools.model.ResourceRef;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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

    private String m_target;
    private Path m_path;
    private List m_defs;

    public void init()
    {
        if( !isInitialized() )
        {
            super.init();
        }
    }

    public void setTarget( final String target )
    {
        m_target = target;
    }

    public void addConfigured( final Path path )
    {
        getPath().add( path );
    }

    public void addConfigured( final DirSet dirset )
    {
        getPath().addDirset( dirset );
    }

    public void addConfigured( final FileSet fileset )
    {
        getPath().addFileset( fileset );
    }

    public void addConfigured( final FileList list ) 
    {
        getPath().addFilelist( list );
    }

    public void execute() throws BuildException 
    {
        final Project project = getProject();
        log( "Preparing build sequence." );
        m_defs = getDefinitions();
        final Definition[] defs = walkGraph();
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
                execute( def, m_target );
            }
            catch( Throwable e )
            {
                throw new BuildException( e );
            }
        }
    }

    private Path getPath()
    {
        if( null == m_path )
        {
            m_path = new Path( getProject() );
        }
        return m_path;
    }

    public void execute( final Definition definition )
    {
        execute( definition, null );
    }

    public void execute( final Definition definition, final String target )
    {
        final Ant ant = (Ant) getProject().createTask( "ant" );
        ant.setDir( definition.getBaseDir() );
        ant.setInheritRefs( false );
        ant.setInheritAll( false );
        if( null != target )
        {
            ant.setTarget( target );
        }
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
        final ResourceRef[] refs = def.getResourceRefs();
        for( int i=0; i<refs.length; i++ )
        {
            final ResourceRef ref = refs[i];
            if( getHome().isaDefinition( ref ) )
            {
                final Definition d = getHome().getDefinition( ref );
                if( m_defs.contains( d ) )
                {
                    list.add( d );
                }
            }
        }

        final ResourceRef[] plugins = def.getPluginRefs();
        for( int i=0; i<plugins.length; i++ )
        {
            final ResourceRef ref = plugins[i];
            if( getHome().isaDefinition( ref ) )
            {
                final Definition plugin = getHome().getPlugin( ref );
                if( m_defs.contains( plugin ) )
                {
                    list.add( plugin );
                }
            }
        }

        return (Definition[]) list.toArray( new Definition[0] );
    }

    private List getDefinitions()
    {
        final Project project = getProject();
        final File basedir = project.getBaseDir(); 
        if( null == m_path )
        {
            return getLocalDefinitions( basedir );
        }
        else
        {
            return getExplicitDefinitions( basedir );
        }
    }

    private List getLocalDefinitions( final File basedir )
    {
        try
        {
            final ArrayList list = new ArrayList();
            final String path = basedir.getCanonicalPath();
            final Definition[] defs = getHome().getDefinitions();
            for( int i=0; i<defs.length; i++ )
            {
                final Definition def = defs[i];
                final String base = def.getBaseDir().getCanonicalPath();
                if( base.startsWith( path ) )
                {
                    list.add( def );
                }
            }
            return list;
        }
        catch( IOException ioe )
        {
            throw new BuildException( ioe );
        }
    }

    private List getExplicitDefinitions( final File basedir )
    {
        final ArrayList list = new ArrayList();

        final String[] names = getPath().list();
        for( int i=0; i<names.length; i++ )
        {
            final String path = names[i];
            final File file = Context.getFile( basedir, path );
            final File dir = getDir( file );
            if( file.exists() )
            {
                final String key = getProjectName( dir );
                if( null != key )
                {
                    final ResourceRef ref = new ResourceRef( key );
                    list.add( getHome().getDefinition( ref ) );
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
                  + " as it does not exist." );
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
