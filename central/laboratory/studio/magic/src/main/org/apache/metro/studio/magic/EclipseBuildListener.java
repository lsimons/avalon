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
package org.apache.metro.studio.magic;

import java.io.File;

import org.apache.avalon.tools.model.MagicPath;
import org.apache.avalon.tools.model.Policy;

import org.apache.avalon.tools.tasks.ReplicateTask;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

/**
 * An abstract build listener. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: MetaBuildListener.java 30977 2004-07-30 08:57:54Z niclas $
 */
public class EclipseBuildListener 
    implements BuildListener
{
    private static final String PREPARE_TASK_KEY = 
      "antlib:org.apache.avalon.tools:prepare";

    private static final String INSTALL_TASK_KEY = 
      "antlib:org.apache.avalon.tools:install";

    private final String m_uri;

    private boolean m_PluginXmlExecuted;
    private boolean m_PluginJarExecuted;

    public EclipseBuildListener( String uri )
    {
        m_uri = uri;
        m_PluginXmlExecuted = false;
        m_PluginJarExecuted = false;
    }

    /**
     * Signals that a build has started. This event
     * is fired before any targets have started.
     *
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     */
    public void buildStarted( BuildEvent event )
    {
        // will not happen
    }

    /**
     * Signals that the last target has finished. This event
     * will still be fired if an error occurred during the build.
     *
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     *
     * @see BuildEvent#getException()
     */
    public void buildFinished( BuildEvent event )
    {
    }

    /**
     * Signals that a target is starting.
     *
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     *
     * @see BuildEvent#getTarget()
     */
    public void targetStarted( BuildEvent event )
    {
    }

    /**
     * Signals that a target has finished. This event will
     * still be fired if an error occurred during the build.
     *
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     *
     * @see BuildEvent#getException()
     */
    public void targetFinished( BuildEvent event )
    {
    }

    /**
     * Signals that a task is starting.
     *
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     *
     * @see BuildEvent#getTask()
     */
    public void taskStarted( BuildEvent event )
    {
    }

    /**
     * Signals that a task has finished. This event will still
     * be fired if an error occurred during the build.
     *
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     *
     * @see BuildEvent#getException()
     */
    public void taskFinished( BuildEvent event )
    {
        String type = event.getTask().getTaskType();
        if( PREPARE_TASK_KEY.equals( type ) )
        {
            if( m_PluginXmlExecuted ) 
                return;

            Project project = event.getProject();
            generatePluginXML( project );
            m_PluginXmlExecuted = true;
        }
        if( INSTALL_TASK_KEY.equals( type ) )
        {
            if( m_PluginJarExecuted ) 
                return;
            Project project = event.getProject();
            generatePluginJar( project );
            m_PluginJarExecuted = true;
        }
    }

    private void generatePluginXML( Project project )
    {
        File basedir = project.getBaseDir();
        File pluginSpec = new File( basedir, "target/build/etc/plugin-spec.xml" );
        if( pluginSpec.exists() )
        {
            File dest = new File( basedir, "target/deliverables/" );
            dest.mkdirs();
            
            MagicPath path = new MagicPath( project );
            path.setMode( "RUNTIME" );
// TODO!!!!
            
            ReplicateTask repl = new ReplicateTask();
            repl.setProject( project );
            repl.setTaskName( "replicate" );
            repl.init();
            File f = new File( basedir, "target/deliverables/lib" );
            f.mkdirs();
            repl.setTodir( f );
            
            repl.setRefid( "deps.path" );
// path ---------^

            repl.execute();
            
            EclipseTask task = new EclipseTask();
            task.setProject( project );
            task.setTaskName( "eclipse" );
            task.init();
            
            task.setDestDir( dest );
            task.setPluginSpec( pluginSpec );
            task.execute();
        }
    }

    private void generatePluginJar( Project project )
    {
        File basedir = project.getBaseDir();
    }
    
    /**
     * Signals a message logging event.
     *
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     *
     * @see BuildEvent#getMessage()
     * @see BuildEvent#getPriority()
     */
    public void messageLogged( BuildEvent event )
    {
    }   
}
