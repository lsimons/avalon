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
public class Home extends Sequential
{
    //-------------------------------------------------------------
    // static
    //-------------------------------------------------------------

    public static final String KEY = "project.home";
    public static final String HOME_KEY = "project.home";

    //-------------------------------------------------------------
    // mutable state
    //-------------------------------------------------------------

    private boolean m_init = false;
    private Context m_context;

    private String m_id;
    private String m_key;

    private Project m_project;
    private Repository m_repository;
    private File m_system;
    private File m_file;

    private final Hashtable m_resources = new Hashtable();
    private Definition m_definition;
    private BuildListener m_listener;

    //-------------------------------------------------------------
    // constructor
    //-------------------------------------------------------------

    public Home( Project project )
    {
        super();
        m_project = project;
    }

    //-------------------------------------------------------------
    // setters
    //-------------------------------------------------------------

    public void setIndex( File file ) throws BuildException
    {
        m_file = file;
    }

    public void setKey( String key )
    {
        m_key = key;
    }

    public void setId( String id )
    {
        m_id = id;
    }

    //-------------------------------------------------------------
    // Task
    //-------------------------------------------------------------

    public void init() throws BuildException 
    {
        if( !m_init )
        {
            Project project = getProject();
            m_context = Context.getContext( project );
            m_init = true;
        }
    }

    public void execute()
    {
        Project project = getProject();

        if( null == m_file )
        {
            String path = project.getProperty( HOME_KEY );
            if( null != path )
            {
                File index = Context.getFile( project.getBaseDir(), path );
                if( index.exists() )
                {
                    if( index.isDirectory() )
                    {
                        m_file = new File( index, "index.xml" );
                    }
                    else
                    {
                        m_file = index;
                    }
                }
                else
                {
                    final String error = 
                      "Property value 'project.home' in task defintion [" 
                      + getTaskName() 
                      + "] references a non-existant file: "
                      + index;
                    throw new BuildException( error );
                }
            }
            else
            {
                final String error = 
                  "Cannot continue due to missing index attribute in task defintion [" 
                  + getTaskName() + "].";
                throw new BuildException( error );
            }
        }

        if( null == m_id )
        {
            project.addReference( KEY, this );
        }
        else
        {
            project.addReference( m_id, this );
        }

        m_system = m_file.getParentFile();
        m_project.log( "home: " + m_system, Project.MSG_DEBUG );


        Element root = ElementHelper.getRootElement( m_file );
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

        final String key = getKey();
        m_definition = getDefinition( key, false );
        m_listener = new StandardListener( this, m_definition );

        super.execute();

    }

    private String getKey()
    {
        if( null != m_key )
        {
            return m_key;
        }
        else
        {
            return getProject().getName();
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

    public File getFile()
    {
        return m_file;
    }

    public Plugin getPlugin( PluginRef ref )
      throws BuildException
    {
        return (Plugin) getDefinition( ref );
    }

    public Definition getDefinition()
    {
        return m_definition;
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

    public void build( Definition definition )
    {
        Ant ant = (Ant) m_project.createTask( "ant" );
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

    //-------------------------------------------------------------
    // internal
    //-------------------------------------------------------------

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

    private void buildResourceList( Element resources )
    {
        if( null == resources ) return;

        Element[] resourceArray = ElementHelper.getChildren( resources, "resource" );
        m_project.log( "resources: " + resourceArray.length, Project.MSG_DEBUG );
        for( int i=0; i<resourceArray.length; i++ )
        {
            Element child = resourceArray[i];
            Resource resource = XMLDefinitionBuilder.createResource( this, child );
            String key = resource.getKey();
            m_resources.put( key, resource );
            m_project.log( 
              "resource: " + resource + " key=" + key, 
              Project.MSG_DEBUG );
        }
    }

    private void buildProjectList( Element projects )
    {
        if( null == projects ) return;

        Element[] entries = ElementHelper.getChildren( projects );
        m_project.log( 
          "projects: " + entries.length, 
          Project.MSG_DEBUG );
        for( int i=0; i<entries.length; i++ )
        {
            Element element = entries[i];
            Definition definition = 
              XMLDefinitionBuilder.createDefinition( this, element, m_system );
            String key = definition.getKey();
            m_resources.put( key, definition );
            m_project.log( 
              "project: " + definition + " key=" + key, 
              Project.MSG_DEBUG );
        }
    }

    private Repository createRepository( Element repo )
    {
        Repository repository = new Repository( this, repo );
        m_project.log( "cache: " + repository.getCacheDirectory(), Project.MSG_DEBUG );
        String[] hosts = repository.getHosts();
        m_project.log( "Hosts: " + hosts.length, Project.MSG_DEBUG );
        for( int i=0; i<hosts.length; i++ )
        {
            m_project.log( "  host: " + hosts[i], Project.MSG_DEBUG ); 
        }
        return repository;
    }
}
