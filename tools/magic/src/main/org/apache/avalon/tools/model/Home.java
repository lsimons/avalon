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
import org.apache.tools.ant.taskdefs.Get;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.types.DataType;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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
    public static final String INDEX_KEY = "project.index";
    public static final String HOSTS_KEY = "project.hosts";

    //-------------------------------------------------------------
    // immutable state
    //-------------------------------------------------------------

    private final File m_index;
    private final Hashtable m_resources = new Hashtable();
    private final Magic m_system;

    //-------------------------------------------------------------
    // constructor
    //-------------------------------------------------------------

    protected Home( final Project project, Magic system, File index )
    {
        m_index = index;
        m_system = system;
        setProject( project );
        buildList( index, false );
    }

    //-------------------------------------------------------------
    // implementation
    //-------------------------------------------------------------

    public File getIndex()
    {
        return m_index;
    }

    public long getIndexLastModified()
    {
        return m_index.lastModified();
    }

    public boolean isaResourceKey( String key )
    {
        return ( null != m_resources.get( key ) );
    }

    public Repository getRepository()
    {
        return m_system.getRepository();
    }

    public Repository getDocsRepository()
    {
        return m_system.getDocsRepository();
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

    private void buildList( 
      final File system, final Element[] children, final boolean remote )
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
                final String path = element.getAttribute( "href" );
                if(( null != path ) && ( !"".equals( path ) ))
                {
                    File index = createTempFile();
                    index.deleteOnExit(); // safety harness in case we abort
                    final URL url = createURL( path );
                    final Get get = (Get) project.createTask( "get" );
                    get.setSrc( url );
                    get.setDest( index );
                    get.setIgnoreErrors( false );
                    get.setUseTimestamp( true );
                    get.setVerbose( false );
                    get.execute();
                    buildList( index, true );
                }
                else
                {
                    final String filename = element.getAttribute( "index" );
                    if(( null != filename ) && ( !"".equals( filename ) ))
                    {
                        final File index = Context.getFile( system, path );
                        buildList( index, true );
                    }
                    else
                    {
                        final String error = 
                          "Invalid import - no href or index attribute.";
                        throw new BuildException( error );
                    }
                }
            }
            else
            {
                final String error =
                  "Unrecognized element type \"" + tag + "\".";
                throw new BuildException( error );
            }
        }
    }

    private URL createURL( String path )
    {
        try
        {
            return new URL( path );
        }
        catch( IOException ioe )
        {
            throw new BuildException( ioe );
        }
    }

    private File createTempFile()
    {
        try
        {
            return File.createTempFile( "~magic", ".tmp" );
        }
        catch( IOException ioe )
        {
            throw new BuildException( ioe );
        }
    }

    private Resource createResource( 
      final Element element, final File system, final boolean remote )
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
