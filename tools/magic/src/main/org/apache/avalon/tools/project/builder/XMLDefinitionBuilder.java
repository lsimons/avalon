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

package org.apache.avalon.tools.project.builder;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Sequential;

import org.apache.avalon.tools.home.Home;
import org.apache.avalon.tools.home.Repository;

import org.apache.avalon.tools.util.ElementHelper;
import org.apache.avalon.tools.project.Info;
import org.apache.avalon.tools.project.Definition;
//import org.apache.avalon.tools.project.ProjectRef;
import org.apache.avalon.tools.project.ResourceRef;
import org.apache.avalon.tools.project.Resource;
//import org.apache.avalon.tools.project.PluginRef;
import org.apache.avalon.tools.project.Plugin;
import org.apache.avalon.tools.project.Plugin.TaskDef;
import org.apache.avalon.tools.project.Policy;

import org.w3c.dom.Element;

/**
 * Definition of a project. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class XMLDefinitionBuilder 
{

    public static Resource createResource( Home home, Element element )
    {
        Info info = 
          createInfo( ElementHelper.getChild( element, "info" ) );
        String key = getDefinitionKey( element, info );
        return new Resource( home, key, info );
    }

    public static Definition createDefinition( Home home, Element element, File anchor )
    {
        Info info = 
          createInfo( ElementHelper.getChild( element, "info" ) );

        String key = getDefinitionKey( element, info );

        File basedir = getBasedir( anchor, element );
       
        ResourceRef[] resources = 
          createResourceRefs( 
            ElementHelper.getChild( element, "dependencies" ) );
        
        ResourceRef[] plugins = 
          createPluginRefs( 
            ElementHelper.getChild( element, "plugins" ) );

        final String tag = element.getTagName();
        if( tag.equals( "project" ) )
        {
            return new Definition( 
              home, key, basedir, info, resources, plugins );
        }
        else if( tag.equals( "plugin" ) )
        {
            TaskDef[] tasks = 
              getTaskDefs( ElementHelper.getChild( element, "tasks" ) );
            return new Plugin( 
              home, key, basedir, info, resources, plugins, tasks );
        }
        else
        {
            final String error =
              "Unrecognized project type \"" + tag + "\".";
            throw new BuildException( error );
        }
    }

    public static TaskDef[] getTaskDefs( Element element )
    {
        Element[] children = ElementHelper.getChildren( element, "taskdef" );
        TaskDef[] refs = new TaskDef[ children.length ];
        for( int i=0; i<children.length; i++ )
        {
            Element child = children[i];
            String name = child.getAttribute( "name" );
            String classname = child.getAttribute( "class" );
            refs[i] = new TaskDef( name, classname );
        }
        return refs;
    }

    private static File getBasedir( File anchor, Element element )
    {
        String path = element.getAttribute( "basedir" );
        if( null == path )
        {
            final String error = 
              "Missing 'basedir' attribute.";
            throw new BuildException( error );
        }

        File basedir = new File( anchor, path );
        if( !basedir.exists() )
        {
            final String error = 
              "Declared basedir [" + basedir + "] does not exist.";
            throw new BuildException( error );
        }
        if( !basedir.isDirectory() )
        {
            final String error = 
              "Declared basedir [" + basedir + "] is not a directory.";
            throw new BuildException( error );
        }
        return basedir;
    }

    private static String getDefinitionKey( Element element, Info info )
    {
        String key = element.getAttribute( "key" );
        if( null == key ) 
        {
            return info.getName();
        }
        if( key.equals( "" ) )
        {
            return info.getName();
        }
        return key;
    }

    public static Info createInfo( Element info )
    {
        String group = 
          ElementHelper.getValue( 
            ElementHelper.getChild( info, "group" ) );
        String name = 
          ElementHelper.getValue( 
            ElementHelper.getChild( info, "name" ) );
        String version = 
          ElementHelper.getValue( 
            ElementHelper.getChild( info, "version" ) );
        String type = 
          ElementHelper.getValue( 
            ElementHelper.getChild( info, "type" ) );

        return new Info( group, name, version, type );
    }

    private static ResourceRef[] createResourceRefs( Element element )
      throws BuildException
    {
        Element[] children = ElementHelper.getChildren( element, "include" );
        ResourceRef[] refs = new ResourceRef[ children.length ];
        for( int i=0; i<children.length; i++ )
        {
            Element child = children[i];
            String key = child.getAttribute( "key" );
            int tag = ResourceRef.getCategory( child.getAttribute( "tag" ) );
            Policy policy = createPolicy( child );
            refs[i] = new ResourceRef( key, policy, tag );
        }
        return refs;
    }

    private static ResourceRef[] createPluginRefs( Element element )
      throws BuildException
    {
        Element[] children = ElementHelper.getChildren( element, "include" );
        ResourceRef[] refs = new ResourceRef[ children.length ];
        for( int i=0; i<children.length; i++ )
        {
            Element child = children[i];
            String key = child.getAttribute( "key" );
            int tag = ResourceRef.getCategory( child.getAttribute( "tag" ) );
            Policy policy = createPolicy( child, false, false, false );
            refs[i] = new ResourceRef( key, policy, tag );
        }
        return refs;
    }

    private static Policy createPolicy( Element element )
    {
        return createPolicy( element, true, true, true );
    }

    private static Policy createPolicy( 
      Element element, boolean defBuild, boolean defTest, boolean defRuntime )
    {
        boolean build = 
          ElementHelper.getBooleanAttribute( element, "build", defBuild );
        boolean test = 
          ElementHelper.getBooleanAttribute( element, "test", defTest );
        boolean runtime = 
          ElementHelper.getBooleanAttribute( element, "runtime", defRuntime );
        return new Policy( build, test, runtime );
    }
}
