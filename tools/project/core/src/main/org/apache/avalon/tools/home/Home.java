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

import org.apache.avalon.tools.event.StandardListener;
import org.apache.avalon.tools.project.Definition;
import org.apache.avalon.tools.project.ProjectRef;
import org.apache.avalon.tools.project.ResourceRef;
import org.apache.avalon.tools.project.Resource;
import org.apache.avalon.tools.project.PluginRef;
import org.apache.avalon.tools.project.Plugin;
import org.apache.avalon.tools.project.builder.XMLDefinitionBuilder;
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

    private static final String USER_PROPERTIES = "user.properties";
    private static final String BUILD_PROPERTIES = "build.properties";

    //-------------------------------------------------------------
    // mutable state
    //-------------------------------------------------------------

    private boolean m_init = false;

    private String m_id;

    private Home m_home;
    private Repository m_repository;
    private File m_system;
    private File m_file;

    private final Hashtable m_resources = new Hashtable();
    private BuildListener m_listener;

    //-------------------------------------------------------------
    // constructor
    //-------------------------------------------------------------

    public Home( Project project, String id )
    {
        setProject( project );
        m_id = id;
        setupHome();
    }

    //-------------------------------------------------------------
    // Task
    //-------------------------------------------------------------

    private String getHomeID()
    {
        if( null == m_id )
        {
            return KEY;
        }
        else
        {
            return m_id;
        }
    }

    private Home getHome()
    {
        if( m_home != null )
        {
            return m_home;
        }
        else
        {
            return this;
        }
    }

    //-------------------------------------------------------------
    // implementation
    //-------------------------------------------------------------

    public File getHomeDirectory()
    {
        return m_system;
    }

    public Repository getRepository()
    {
        return m_repository;
    }

    public Plugin getPlugin( PluginRef ref )
      throws BuildException
    {
        return (Plugin) getDefinition( ref );
    }

    public Definition getDefinition( ProjectRef ref )
      throws BuildException
    {
        return getDefinition( ref.getKey() );
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
        return getDefinition( key, true );
    }

    public Definition getDefinition( String key, boolean fail )
      throws BuildException
    {
        Resource def = (Resource) m_resources.get( key );
        if( null == def )
        {
            if( fail )
            {
                final String error = 
                  "Unknown definition [" + key + "]";
                throw new BuildException( error );
            }
        }
        else
        {
            if( def instanceof Definition )
            {
                return (Definition) def;
            }
            else
            {
                if( fail )
                {
                    final String error =
                      "Key [" + key + "] is not project.";
                    throw new BuildException( error );
                }
            }
        }
        return null;
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
    */

    //-------------------------------------------------------------
    // internal
    //-------------------------------------------------------------

    /*
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

    private void buildResourceList( Element resources )
    {
        if( null == resources ) return;

        Element[] resourceArray = ElementHelper.getChildren( resources, "resource" );
        log( "resources: " + resourceArray.length, Project.MSG_DEBUG );
        for( int i=0; i<resourceArray.length; i++ )
        {
            Element child = resourceArray[i];
            Resource resource = XMLDefinitionBuilder.createResource( this, child );
            String key = resource.getKey();
            m_resources.put( key, resource );
            log( 
              "resource: " + resource + " key=" + key, 
              Project.MSG_DEBUG );
        }
    }

    private void buildProjectList( Element projects )
    {
        if( null == projects ) return;

        Element[] entries = ElementHelper.getChildren( projects );
        log( 
          "projects: " + entries.length, 
          Project.MSG_DEBUG );
        for( int i=0; i<entries.length; i++ )
        {
            Element element = entries[i];
            Definition definition = 
              XMLDefinitionBuilder.createDefinition( this, element, m_system );
            String key = definition.getKey();
            m_resources.put( key, definition );
            log( 
              "project: " + definition + " key=" + key, 
              Project.MSG_DEBUG );
        }
    }

    private Repository createRepository( Element repo )
    {
        Repository repository = new Repository( this, repo );
        log( "cache: " + repository.getCacheDirectory(), Project.MSG_DEBUG );
        String[] hosts = repository.getHosts();
        log( "Hosts: " + hosts.length, Project.MSG_DEBUG );
        for( int i=0; i<hosts.length; i++ )
        {
            log( "  host: " + hosts[i], Project.MSG_DEBUG ); 
        }
        return repository;
    }

    private void setupHome()
    {
        Project project = getProject();
        log( "Building system definition." );
        try
        {
            File index = getIndexFile();
            m_system = index.getParentFile();
            setupProperties( project, m_system );
            Element root = ElementHelper.getRootElement( index );
            final Element repo = ElementHelper.getChild( root, "repository" );
            final Element resources = ElementHelper.getChild( root, "resources" );
            final Element projects = ElementHelper.getChild( root, "projects" );

            //
            // construct the repository, build the definition of the available 
            // resources and projects used within the system and associate a build
            // listener
            //

            m_repository = createRepository( repo );
            buildResourceList( resources );
            buildProjectList( projects );

        }
        catch( Throwable e )
        {
            throw new BuildException( e );
        }
    }

    private File getIndexFile()
    {
        if( null != m_file ) return m_file;

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

    private void setupProperties( Project project, File dir )
    {
        setupUserProperties( project, dir );
        setupBuildProperties( project, dir );
    }

    private void setupUserProperties( Project project, File basedir )
    {
        File user = Context.getFile( basedir, USER_PROPERTIES );
        readProperties( project, user );
    }

    private void setupBuildProperties( Project project, File basedir )
    {
        File build = Context.getFile( basedir, BUILD_PROPERTIES );
        readProperties( project, build );
    }

    private void readProperties( Project project, File file ) throws BuildException 
    {
        Property props = (Property) project.createTask( "property" );
        props.setFile( file );
        props.init();
        props.execute();
    }


}
