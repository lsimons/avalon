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

import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.taskdefs.Sequential;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Text;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.apache.avalon.tools.util.ElementHelper;
import org.apache.avalon.tools.project.Definition;
import org.apache.avalon.tools.project.ProjectRef;
import org.apache.avalon.tools.project.ResourceRef;
import org.apache.avalon.tools.project.Resource;
import org.apache.avalon.tools.project.PluginRef;
import org.apache.avalon.tools.project.Plugin;
import org.apache.avalon.tools.project.builder.XMLDefinitionBuilder;

/**
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class Home 
{
    private final File m_file;
    private final Project m_project;
    private final Repository m_repository;
    private final File m_home;

    private final Hashtable m_resources = new Hashtable();
    private final Hashtable m_definitions = new Hashtable();
    private final Hashtable m_plugins = new Hashtable();


    public Home( Project project, File file )
    {
        m_file = file;
        m_project = project;
        m_home = file.getParentFile();

        Element root = ElementHelper.getRootElement( file );
        project.log( "home: " + m_home, Project.MSG_DEBUG );

        final Element repo = ElementHelper.getChild( root, "repository" );
        final Element resources = ElementHelper.getChild( root, "resources" );
        final Element projects = ElementHelper.getChild( root, "projects" );
        final Element plugins = ElementHelper.getChild( root, "plugins" );

        //
        // construct the repository
        //

        m_repository = createRepository( repo );

        //
        // build the definition of the available resources  
        // used within the system
        //

        buildResourceList( resources );

        //
        // build the definition of the available projects  
        // used within the system
        //

        buildProjectList( projects );

        //
        // build the definition of the available plugins  
        // used within the system
        //

        buildPluginList( plugins );

    }

    private void buildResourceList( Element resources )
    {
        if( null == resources ) return;

        Element[] resourceArray = ElementHelper.getChildren( resources, "resource" );
        m_project.log( "resources: " + resourceArray.length, Project.MSG_DEBUG );
        for( int i=0; i<resourceArray.length; i++ )
        {
            Element child = resourceArray[i];
            Resource resource = XMLDefinitionBuilder.createResource( child );
            String key = resource.getKey();
            m_resources.put( key, resource );
            m_project.log( 
              "resource: " + resource + " key=" + key, Project.MSG_DEBUG );
        }
    }

    private void buildProjectList( Element projects )
    {
        if( null == projects ) return;

        Element[] entries = ElementHelper.getChildren( projects, "project" );
        m_project.log( "projects: " + entries.length, Project.MSG_DEBUG );
        for( int i=0; i<entries.length; i++ )
        {
            Element element = entries[i];
            Definition definition = 
              XMLDefinitionBuilder.createDefinition( m_home, element );
            String key = definition.getKey();
            m_definitions.put( key, definition );
            m_project.log( 
              "project: " + definition + " key=" + key, Project.MSG_DEBUG );
        }
    }


    private void buildPluginList( Element element )
    {

        System.out.println( "\n# PLUGINS ELEMENT: " + element );

        if( null != element )
        {
            System.out.println( 
                "# LENGTH: " + element.getChildNodes().getLength() );

            Element[] plugins = ElementHelper.getChildren( element, "plugin" );

            System.out.println( "# PLUGINS LENGTH: " + plugins.length );

            m_project.log( "plugins: " + plugins.length, Project.MSG_DEBUG );
            for( int i=0; i<plugins.length; i++ )
            {
                Element child = plugins[i];
                Plugin plugin = 
                  XMLDefinitionBuilder.createPlugin( m_home, child );
                String key = plugin.getKey();
                m_plugins.put( key, plugin );
                m_project.log( 
                  "plugin: " + plugin + " key=" + key, Project.MSG_DEBUG );
                System.out.println( 
                  "plugin: " + plugin + " key=" + key );
            }
        }
    }

    public File getHomeDirectory()
    {
        return m_home;
    }

    public Repository getRepository()
    {
        return m_repository;
    }

    public File getFile()
    {
        return m_file;
    }

    public Project getProject()
    {
        return m_project;
    }

    public Definition getDefinition( ProjectRef ref )
      throws BuildException
    {
        return getDefinition( ref.getKey() );
    }

    public Definition getDefinition( String key )
      throws BuildException
    {
        Definition def = (Definition) m_definitions.get( key );
        if( null == def )
        {
            final String error = 
              "Unknown definition [" + key + "]";
            throw new BuildException( error );
        }
        return def;
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

    public Plugin getPlugin( PluginRef ref )
      throws BuildException
    {
        final String key = ref.getKey();
        Plugin plugin = (Plugin) m_plugins.get( key );
        if( null == plugin )
        {
            final String error = 
              "Unknown plugin [" + key + "]";
            throw new BuildException( error );
        }
        return plugin;
    }


    public void build( Definition definition )
    {
        m_project.log( "\n  build sequence for definition: " + definition + "\n");
        Definition[] targets = getBuildSequence( definition );
        for( int i=0; i<targets.length; i++ )
        {
            Definition def = targets[i];
            m_project.log( "   target (" + (i+1) + "): " + def ); 
        }

        for( int i=0; i<targets.length; i++ )
        {
            Definition def = targets[i];
            buildProject( def );
        }

        m_project.log( "composite build complete" );
    }

    private void buildProject( Definition definition )
    {
        m_project.log( "\nbuild target: " + definition );

        Path path = getRepository().createPath( m_project, definition );
        m_project.log( "compile path: " + path );

        //
        // here is where we need to launch plugins that will 
        // result in population of the repository
        //

        m_project.log( "build complete" );
    }

    private Definition[] getBuildSequence( Definition definition )
    {
        ArrayList visited = new ArrayList();
        ArrayList targets = new ArrayList();
        PluginRef[] pluginRefs = definition.getPluginRefs();
        for( int i=0; i<pluginRefs.length; i++ )
        {
            Plugin plugin = getPlugin( pluginRefs[i] );
            getBuildSequence( visited, targets, plugin );
        }
        ProjectRef[] refs = definition.getProjectRefs();
        for( int i=0; i<refs.length; i++ )
        {
            Definition def = getDefinition( refs[i] );
            getBuildSequence( visited, targets, def );
        }
        targets.add( definition );
        return (Definition[]) targets.toArray( new Definition[0] );
    }

    private void getBuildSequence( List visited, List targets, Definition definition )
    {
        if( visited.contains( definition ) ) return;
        visited.add( definition );

        PluginRef[] pluginRefs = definition.getPluginRefs();
        for( int i=0; i<pluginRefs.length; i++ )
        {
            Plugin plugin = getPlugin( pluginRefs[i] );
            if( visited.contains( plugin ) )
            {
                final String error =
                  "Recursive reference identified in project: " 
                     + definition 
                     + " in plugin " + plugin + ".";
                throw new BuildException( error );
            }
            getBuildSequence( visited, targets, plugin );
        }

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
