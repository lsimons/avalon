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

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.taskdefs.Antlib;
import org.apache.tools.ant.helper.ProjectHelper2;
import org.apache.tools.ant.UnknownElement;

import org.apache.avalon.tools.home.Home;
import org.apache.avalon.tools.project.Definition;
import org.apache.avalon.tools.project.Info;
import org.apache.avalon.tools.project.Resource;
import org.apache.avalon.tools.project.Plugin;
import org.apache.avalon.tools.project.Plugin.TaskDef;
import org.apache.avalon.tools.project.builder.XMLDefinitionBuilder;
import org.apache.avalon.tools.util.ElementHelper;

import org.w3c.dom.Element;

/**
 * Load a plugin. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class PluginTask extends HomeTask
{
    private String m_id;
    private File m_definition;
    private String m_uri;

    public void setId( String id )
    {
        m_id = id;
    }

    private String getId()
    {
        if( null != m_id )
        {
            return m_id;
        }
        else
        {
            final String error = 
              "Missing plugin id attribute.";
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

            String id = getId();
            Info info = Info.create( id, "ant" );
            Project project = getProject();
            Resource resource = new Resource( info );
            m_definition = getHome().getRepository().getResource( project, resource );

            //
            // create a utility data object from the defintion
            //

            AntLibData data = new AntLibData( getProject(), m_definition );
            ClassLoader classloader = project.createClassLoader( data.getPath() );
            String uri = data.getInfo().getURI(); 

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
                log( "Added task definition \"" + name + "\"", Project.MSG_DEBUG );
            }
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

        public AntLibData( Project project, File file ) throws Exception
        {
            Element root = ElementHelper.getRootElement( file );
            Element infoElement = ElementHelper.getChild( root, "info" );
            m_info = XMLDefinitionBuilder.createInfo( infoElement );
            Element tasksElement = ElementHelper.getChild( root, "tasks" );
            m_tasks = XMLDefinitionBuilder.getTaskDefs( tasksElement );

            Element classpathElement = ElementHelper.getChild( root, "classpath" );
            Element[] children = ElementHelper.getChildren( classpathElement );
            for( int i=0; i<children.length; i++ )
            {
                Element child = children[i];
                String value = ElementHelper.getValue( child );
                String type = child.getTagName();
                Info info = Info.create( value, type );
                Resource resource = new Resource( info );
                File jar = 
                  getHome().getRepository().getResource( 
                    project, resource );
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

        public Path getPath()
        {
            return m_path;
        }
    }
}
