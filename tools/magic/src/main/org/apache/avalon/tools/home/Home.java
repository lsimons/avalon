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
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.List;
import java.util.Hashtable;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.taskdefs.Ant.Reference;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.taskdefs.Sequential;
import org.apache.tools.ant.taskdefs.Property;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Text;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.apache.avalon.tools.project.Definition;
import org.apache.avalon.tools.project.ResourceRef;
import org.apache.avalon.tools.project.Resource;
import org.apache.avalon.tools.project.Plugin;
import org.apache.avalon.tools.project.XMLDefinitionBuilder;
import org.apache.avalon.tools.util.ElementHelper;

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

    public Home( Project project, File system, File index )
    {
        setProject( project );
        m_index = index;

        m_system = system;

        try
        {
            String path = getCachePath( project );
            String hostsPath = project.getProperty( HOSTS_KEY );
            m_main = new Repository( project, m_system, path, hostsPath, this );

            String docs = getDocsCachePath( project );
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
        String[] hosts = m_main.getHosts();
        log( "Hosts: " + hosts.length, Project.MSG_VERBOSE );
        for( int i=0; i<hosts.length; i++ )
        {
            log( "  host: " + hosts[i], Project.MSG_VERBOSE ); 
        }
    }

    private String getCachePath( Project project ) throws IOException
    {
        String path = project.getProperty( MAIN_CACHE_KEY );
        if( null != path ) return path;

        Property property = (Property) project.createTask( "property" );
        property.setEnvironment( "env" );
        property.init();
        property.execute();

        String avalonHomePath = project.getProperty( "env.AVALON_HOME" );
        if( null != avalonHomePath )
        {
            File avalonHomeDirectory = new File( avalonHomePath );
            File cache = new File( avalonHomeDirectory, "repository" );
            return cache.getCanonicalPath();
        }
        else
        {
            return ".cache";
        }
    }

    private String getDocsCachePath( Project project ) throws IOException
    {
        String path = project.getProperty( DOCS_CACHE_KEY );
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

    public boolean isaDefinition( ResourceRef ref )
    {
        return ( getResource( ref ) instanceof Definition );
    }

    public Definition[] getDefinitions()
      throws BuildException
    {
        ArrayList list = new ArrayList();
        Resource[] resources = getResources();
        for( int i=0; i<resources.length; i++ )
        {
            Resource resource = resources[i];
            if( resource instanceof Definition )
            {
                list.add( resource );
            }
        }
        return (Definition[]) list.toArray( new Definition[0] );
    }

    public Resource getResource( String key )
      throws BuildException
    {
        ResourceRef ref = new ResourceRef( key );
        return getResource( ref );
    }

    public Resource getResource( ResourceRef ref )
      throws BuildException
    {
        final String key = ref.getKey();
        Resource resource = (Resource) m_resources.get( key );
        if( null == resource )
        {
            final String error = 
              "Unknown resource [" + key + "]";
            throw new BuildException( error );
        }
        return resource;
    }

    public Definition getDefinition( String key )
      throws BuildException
    {
        ResourceRef ref = new ResourceRef( key );
        return getDefinition( ref );
    }

    public Definition getDefinition( ResourceRef ref )
      throws BuildException
    {
        Resource resource = getResource( ref );
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

    public Plugin getPlugin( ResourceRef ref )
      throws BuildException
    {
        Resource resource = getResource( ref );
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

    private void buildList( File index, boolean remote )
    {
        Element root = ElementHelper.getRootElement( index );
        final Element[] elements = ElementHelper.getChildren( root );
        File system = index.getParentFile();
        buildList( system, elements, remote );
    }

    private void buildList( File system, Element[] children, boolean remote )
    {
        if( null == children ) return;

        log( "entry: " + children.length, Project.MSG_VERBOSE );
        for( int i=0; i<children.length; i++ )
        {
            Element element = children[i];
            final String tag = element.getTagName();
            if( isaResource( tag ) )
            {
                Resource resource = createResource( element, system, remote );
                String key = resource.getKey();
                m_resources.put( key, resource );
                log( 
                  "resource: " + resource 
                  + " key=" + key, Project.MSG_VERBOSE );
            }
            else if( "import".equals( element.getTagName() ) )
            { 
                String path = element.getAttribute( "index" );
                File index = Context.getFile( system, path );
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

    private Resource createResource( Element element, File system, boolean remote )
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

    private boolean isaResource( String tag )
    {
        return ( 
          "resource".equals( tag ) 
          || "project".equals( tag )
          || "plugin".equals( tag ) );
    }

    private File getIndexFile()
    {
        if( null != m_index ) return m_index;

        String path = project.getProperty( KEY );
        if( null != path )
        {
            File index = Context.getFile( project.getBaseDir(), path );
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
