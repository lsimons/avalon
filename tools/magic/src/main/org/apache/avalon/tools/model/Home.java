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
import org.apache.tools.ant.types.DataType;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class Home extends DataType
{
    //-------------------------------------------------------------
    // static
    //-------------------------------------------------------------

    public static final String KEY = "project.home";
    public static final String HOME_KEY = "project.home";

    public static final String HOSTS_KEY = "project.hosts";
    public static final String MAIN_CACHE_KEY = "project.main.cache";
    public static final String DOCS_CACHE_KEY = "project.docs.cache";

    //-------------------------------------------------------------
    // mutable state
    //-------------------------------------------------------------

    private boolean m_init = false;

    private Home m_home;
    private Repository m_main;
    private Repository m_docs;
    private File m_system;
    private File m_index;

    private final Hashtable m_resources = new Hashtable();
    private BuildListener m_listener;

    //-------------------------------------------------------------
    // constructor
    //-------------------------------------------------------------

    public Home( final Project project, final File system, final File index )
    {
        setProject( project );
        m_index = index;

        m_system = system;

        try
        {
            final String path = getCachePath( project );
            final String hostsPath = project.getProperty( HOSTS_KEY );
            m_main = new Repository( project, m_system, path, hostsPath, this );

            final String docs = getDocsCachePath( project );
            m_docs = new Repository( project, m_system, docs, hostsPath, this );

            //
            // construct the repository, build the definition of the available 
            // resources and projects used within the system and associate a build
            // listener
            //

            buildList( m_index, false );
        }
        catch( Throwable e )
        {
            throw new BuildException( e );
        }

        log( "cache: " + m_main.getCacheDirectory(), Project.MSG_VERBOSE );
        final String[] hosts = m_main.getHosts();
        log( "Hosts: " + hosts.length, Project.MSG_VERBOSE );
        for( int i=0; i<hosts.length; i++ )
        {
            log( "  host: " + hosts[i], Project.MSG_VERBOSE ); 
        }
    }

    private String getCachePath( final Project project ) throws IOException
    {
        final String path = project.getProperty( MAIN_CACHE_KEY );
        if( null != path ) return path;

        final Property property = (Property) project.createTask( "property" );
        property.setEnvironment( "env" );
        property.init();
        property.execute();

        final String avalonHomePath = project.getProperty( "env.AVALON_HOME" );
        if( null != avalonHomePath )
        {
            final File avalonHomeDirectory = new File( avalonHomePath );
            final File cache = new File( avalonHomeDirectory, "repository" );
            return cache.getCanonicalPath();
        }
        else
        {
            return ".cache";
        }
    }

    private String getDocsCachePath( final Project project )
    {
        final String path = project.getProperty( DOCS_CACHE_KEY );
        if( null != path ) return path;
        return ".docs";
    }

    //-------------------------------------------------------------
    // implementation
    //-------------------------------------------------------------

    public File getHomeDirectory()
    {
        return m_system;
    }

    public long getIndexLastModified()
    {
        return getIndexFile().lastModified();
    }

    public Repository getRepository()
    {
        return m_main;
    }

    public Repository getDocsRepository()
    {
        return m_docs;
    }

    public Resource[] getResources()
    {
        return (Resource[]) m_resources.values().toArray( new Resource[0] );
    }

    public boolean isaDefinition( final ResourceRef ref )
    {
        return ( getResource( ref ) instanceof Definition );
    }

    public Definition[] getDefinitions()
      throws BuildException
    {
        final ArrayList list = new ArrayList();
        final Resource[] resources = getResources();
        for( int i=0; i<resources.length; i++ )
        {
            final Resource resource = resources[i];
            if( resource instanceof Definition )
            {
                list.add( resource );
            }
        }
        return (Definition[]) list.toArray( new Definition[0] );
    }

    public Resource getResource( final String key )
      throws BuildException
    {
        final ResourceRef ref = new ResourceRef( key );
        return getResource( ref );
    }

    public Resource getResource( final ResourceRef ref )
      throws BuildException
    {
        final String key = ref.getKey();
        final Resource resource = (Resource) m_resources.get( key );
        if( null == resource )
        {
            final String error = 
              "Unknown resource [" + key + "]";
            throw new BuildException( error );
        }
        return resource;
    }

    public Definition getDefinition( final String key )
      throws BuildException
    {
        final ResourceRef ref = new ResourceRef( key );
        return getDefinition( ref );
    }

    public Definition getDefinition( final ResourceRef ref )
      throws BuildException
    {
        final Resource resource = getResource( ref );
        if( resource instanceof Definition )
        {
            return (Definition) resource;
        }
        else
        {
            final String error = 
              "Reference [" + ref + "] does not refer to a projects.";
            throw new BuildException( error );
        }
    }

    public Plugin getPlugin( final ResourceRef ref )
      throws BuildException
    {
        final Resource resource = getResource( ref );
        if( resource instanceof Plugin )
        {
            return (Plugin) resource;
        }
        else
        {
            final String error = 
              "Reference [" + ref + "] does not refer to a plugin.";
            throw new BuildException( error );
        }
    }

    //-------------------------------------------------------------
    // internal
    //-------------------------------------------------------------

    private void buildList( final File index, final boolean remote )
    {
        final Element root = ElementHelper.getRootElement( index );
        final Element[] elements = ElementHelper.getChildren( root );
        final File system = index.getParentFile();
        buildList( system, elements, remote );
    }

    private void buildList( final File system, final Element[] children, final boolean remote )
    {
        if( null == children ) return;

        log( "entry: " + children.length, Project.MSG_VERBOSE );
        for( int i=0; i<children.length; i++ )
        {
            final Element element = children[i];
            final String tag = element.getTagName();
            if( isaResource( tag ) )
            {
                final Resource resource = createResource( element, system, remote );
                final String key = resource.getKey();
                m_resources.put( key, resource );
                log( 
                  "resource: " + resource 
                  + " key=" + key, Project.MSG_VERBOSE );
            }
            else if( "import".equals( element.getTagName() ) )
            { 
                final String path = element.getAttribute( "index" );
                final File index = Context.getFile( system, path );
                buildList( index, true );
            }
            else
            {
                final String error =
                  "Unrecognized element type \"" + tag + "\".";
                throw new BuildException( error );
            }
        }
    }

    private Resource createResource( final Element element, final File system, final boolean remote )
    {
        if( remote )
        {
            return XMLDefinitionBuilder.createResource( this, element );
        }
        else
        {
            return XMLDefinitionBuilder.createResource( this, element, system );
        }
    }

    private boolean isaResource( final String tag )
    {
        return ( 
          "resource".equals( tag ) 
          || "project".equals( tag )
          || "plugin".equals( tag ) );
    }

    private File getIndexFile()
    {
        if( null != m_index ) return m_index;

        final String path = project.getProperty( KEY );
        if( null != path )
        {
            final File index = Context.getFile( project.getBaseDir(), path );
            if( index.exists() )
            {
                if( index.isDirectory() )
                {
                    return new File( index, "index.xml" );
                }
                else
                {
                    return index;
                }
            }
            else
            {
                final String error = 
                  "Property value 'project.home' references a non-existant file: "
                  + index;
                throw new BuildException( error );
            }
        }
        else
        {
            final String error = 
              "Cannot continue due to missing index attribute.";
            throw new BuildException( error );
        }
    }

    /*

    public void build( Definition definition )
    {
        Ant ant = (Ant) getProject().createTask( "ant" );
        Property property = ant.createProperty();
        property.setName( "urn:avalon.definition.key" );
        property.setValue( definition.getKey() );
        ant.setDir( definition.getBasedir() );
        ant.setInheritRefs( true );
        ant.init();
        ant.execute();
    }

    public Definition[] getBuildSequence( Definition definition )
    {
        ArrayList visited = new ArrayList();
        ArrayList targets = new ArrayList();
        ProjectRef[] refs = definition.getProjectRefs();
        for( int i=0; i<refs.length; i++ )
        {
            Definition def = getDefinition( refs[i] );
            getBuildSequence( visited, targets, def );
        }
        return (Definition[]) targets.toArray( new Definition[0] );
    }

    private void getBuildSequence( List visited, List targets, Definition definition )
    {
        if( visited.contains( definition ) ) return;
        visited.add( definition );

        ProjectRef[] refs = definition.getProjectRefs();
        for( int i=0; i<refs.length; i++ )
        {
            Definition def = getDefinition( refs[i] );
            if( visited.contains( def ) )
            {
                final String error =
                  "Recursive reference identified in project: " 
                     + definition 
                     + " in dependency " + def + ".";
                throw new BuildException( error );
            }
            getBuildSequence( visited, targets, def );
        }

        if( !targets.contains( definition ) )
        {
            targets.add( definition );
        }
    }
    */

}
