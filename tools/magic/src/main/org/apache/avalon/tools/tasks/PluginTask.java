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
import java.net.URL;
import java.lang.reflect.Constructor;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.taskdefs.Antlib;
import org.apache.tools.ant.helper.ProjectHelper2;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.BuildListener;

import org.apache.avalon.tools.model.Home;
import org.apache.avalon.tools.model.Context;
import org.apache.avalon.tools.model.Definition;
import org.apache.avalon.tools.model.Info;
import org.apache.avalon.tools.model.Resource;
import org.apache.avalon.tools.model.Plugin;
import org.apache.avalon.tools.model.Plugin.TaskDef;
import org.apache.avalon.tools.model.Plugin.ListenerDef;
import org.apache.avalon.tools.model.XMLDefinitionBuilder;
import org.apache.avalon.tools.model.ElementHelper;

import org.w3c.dom.Element;

/**
 * Load a plugin. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class PluginTask extends SystemTask
{
    private String m_id;

    public void setArtifact( String id )
    {
        m_id = id;
    }

    private String getArtifactSpec()
    {
        if( null != m_id )
        {
            return m_id;
        }
        else
        {
            final String error = 
              "Missing plugin 'artifact' attribute.";
            throw new BuildException( error );
        }
    }

    public void execute() throws BuildException 
    {
        try
        {
            //
            // get the xml definition of the plugin
            //

            String id = getArtifactSpec();

            Info info = Info.create( id );
            Project project = getProject();
            Resource resource = new Resource( getHome(), info );
            File file = resource.getArtifact( project );

            //
            // create a utility data object from the defintion
            //

            AntLibData data = new AntLibData( getHome(), getProject(), file );

            AntClassLoader classloader = project.createClassLoader( data.getPath() );
            String spec = data.getInfo().getSpec();
            String uri = "plugin:" + spec.substring( 0, spec.indexOf( "#" ) );
            log( "Install \"" + uri + "\"" );

            //
            // install the ant task defintions
            //

            ComponentHelper helper =
              ComponentHelper.getComponentHelper( project );
            TaskDef[] defs = data.getTaskDefs();
            for( int i=0; i<defs.length; i++ )
            {
                TaskDef def = defs[i];
                Class taskClass = classloader.loadClass( def.getClassname() );
                String name = uri + ":" + def.getName();
                helper.addTaskDefinition( name, taskClass );
                log( "Task \"" + name + "\"" );
            }

            //
            // register plugins that declare themselves as build listeners
            //

            ListenerDef[] listeners = data.getListenerDefs();
            for( int i=0; i<listeners.length; i++ )
            {
                ListenerDef def = listeners[i];
                Class listenerClass = classloader.loadClass( def.getClassname() );
                BuildListener listener = createBuildListener( listenerClass, data, uri );
                getProject().addBuildListener( listener );
            }
        }
        catch( Throwable e )
        {
            throw new BuildException( e );
        }
    }

   /**
    * Create a build listerer using a supplied class.  The implementation
    * checks the first available constructor arguments and builds a set of 
    * arguments based on the arguments supplied to this task.
    *
    * @param clazz the listener class
    * @return a instance of the class
    * @exception BuildException if the class does not expose a public 
    *    constructor, or the constructor requires arguments that the 
    *    method cannot resolve, or if a unexpected instantiation error 
    *    ooccurs
    */ 
    public BuildListener createBuildListener( Class clazz, AntLibData data, String uri ) 
      throws BuildException
    {
        if( !BuildListener.class.isAssignableFrom( clazz ) )
        {
            final String error = 
              "Listener class [" + clazz.getName() 
              + "] declared within the plugin ["
              + uri
              + "] does not implement BuildListener.";
            throw new BuildException( error );
        }

        Constructor[] constructors = clazz.getConstructors();
        if( constructors.length < 1 ) 
        {
            final String error = 
              "Cannot handle listeners classes with more than one public constructor.";
            throw new BuildException( error );
        }

        Constructor constructor = constructors[0];
        Class[] classes = constructor.getParameterTypes();
        Object[] args = new Object[ classes.length ];
        for( int i=0; i<classes.length; i++ )
        {
            Class c = classes[i];
            if( AntLibData.class.isAssignableFrom( c ) )
            {
                args[i] = data;
            }
            else if( String.class.isAssignableFrom( c ) )
            {
                args[i] = uri;
            }
            else
            {
                final String error = 
                  "Unrecognized constructor parameter: " + c.getName();
                throw new BuildException( error );
            }
        }

        //
        // instantiate the factory
        //

        return instantiateBuildListener( constructor, args );
    }

   /**
    * Instantiation of a listener instance using a supplied constructor 
    * and arguments.
    * 
    * @param constructor the class constructor
    * @param args the constructor arguments
    * @return the listener instance
    * @exception BuildException if an instantiation error occurs
    */
    private BuildListener instantiateBuildListener( 
      Constructor constructor, Object[] args ) 
      throws BuildException
    {
        Class clazz = constructor.getDeclaringClass();
        try
        {
            return (BuildListener) constructor.newInstance( args );
        }
        catch( Throwable e )
        {
            throw new BuildException( e );
        }
    }

    private class AntLibData 
    {
        private final Info m_info;
        private final Path m_path = new Path( project );
        private final TaskDef[] m_tasks;
        private final ListenerDef[] m_listeners;

        public AntLibData( Home home, Project project, File file ) throws Exception
        {
            Element root = ElementHelper.getRootElement( file );
            Element infoElement = ElementHelper.getChild( root, "info" );
            m_info = XMLDefinitionBuilder.createInfo( infoElement );
            Element tasksElement = ElementHelper.getChild( root, "tasks" );
            m_tasks = XMLDefinitionBuilder.getTaskDefs( tasksElement );
            Element listenerElement = ElementHelper.getChild( root, "listeners" );
            m_listeners = XMLDefinitionBuilder.getListenerDefs( listenerElement );

            Element classpathElement = ElementHelper.getChild( root, "classpath" );
            Element[] children = ElementHelper.getChildren( classpathElement, "artifact" );
            for( int i=0; i<children.length; i++ )
            {
                Element child = children[i];
                String value = ElementHelper.getValue( child );
                String type = getArtifactType( value );
                String uri = getArtifactURI( value );
                Info info = Info.create( type, uri );
                Resource resource = new Resource( home, info );
                File jar = resource.getArtifact( project );
                m_path.createPathElement().setLocation( jar );
            }
        }

        public Info getInfo()
        {
            return m_info;
        }

        public TaskDef[] getTaskDefs()
        {
            return m_tasks;
        }

        public ListenerDef[] getListenerDefs()
        {
            return m_listeners;
        }

        public Path getPath()
        {
            return m_path;
        }

        private String getArtifactType( String value )
        {
            int i = value.indexOf( ":" );
            if( i > 0 ) return value.substring( 0, i );
            return "jar";
        }

        private String getArtifactURI( String value )
        {
            int i = value.indexOf( ":" );
            if( i > 0 ) return value.substring( i+1 );
            return value;
        }
    }
}
