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
import java.util.Hashtable;
import java.util.Map;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Mkdir;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;

import org.apache.avalon.tools.home.Context;
import org.apache.avalon.tools.project.Definition;
import org.apache.avalon.tools.project.ResourceRef;
import org.apache.avalon.tools.project.Plugin;

/**
 * Load a goal. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class PrepareTask extends SystemTask
{
    private static final String SRC_FILTERED_INCLUDES_KEY = 
      "project.prepare.src.filtered.includes";
    private static final String SRC_FILTERED_INCLUDES_VALUE = 
      "**/*.java,**/*.x*,**/*.properties";

    private static final String ETC_FILTERED_INCLUDES_KEY = 
      "project.prepare.etc.filtered.includes";
    private static final String ETC_FILTERED_INCLUDES_VALUE = 
      "**/*.java,**/*.x*,**/*.properties";

    public void init() throws BuildException 
    {
        if( !isInitialized() )
        {
            super.init();
            Project project = getProject();
            project.setNewProperty(
              SRC_FILTERED_INCLUDES_KEY, SRC_FILTERED_INCLUDES_VALUE );
            project.setNewProperty(
              ETC_FILTERED_INCLUDES_KEY, ETC_FILTERED_INCLUDES_VALUE );
        }
    }

    public void execute() throws BuildException 
    {
        Project project = getProject();

        //
        // if the project declares plugin dependencies then install
        // these now
        //

        String key = getContext().getKey();
        Definition def = getHome().getDefinition( key );
        ResourceRef[] refs = def.getPluginRefs();
        for( int i=0; i<refs.length; i++ )
        {
            ResourceRef ref = refs[i];
            Plugin plugin = getHome().getPlugin( ref );
            String path = "plugin:" + plugin.getInfo().getSpec();
            PluginTask task = new PluginTask();
            task.setTaskName( "plugin" );
            task.setProject( project );
            task.setArtifact( path );
            task.init();
            task.execute();            
        }

        //
        // setup the file system
        //

        File target = getContext().getTargetDirectory();
        if( !target.exists() )
        {
            log( "creating target directory" );
            mkDir( target );
        }
        File src = getContext().getSrcDirectory();
        File etc = getContext().getEtcDirectory();
        File build = getContext().getBuildDirectory();
        if( src.exists() )
        {
            String filters = project.getProperty( SRC_FILTERED_INCLUDES_KEY );
            copy( src, build, true, filters, "" );
            copy( src, build, false, "**/*.*", filters );
        }
        if( etc.exists() )
        {
            File buildEtcDir = new File( build, "etc" );
            String filters = project.getProperty( ETC_FILTERED_INCLUDES_KEY );
            copy( etc, buildEtcDir, true, filters, "" );
            copy( etc, buildEtcDir, false, "**/*", filters );
        }
    }

    private void copy( 
       File src, File destination, boolean filtering, String includes, String excludes )
    {
        mkDir( destination );

        Copy copy = (Copy) getProject().createTask( "copy" );
        copy.setTodir( destination );
        copy.setFiltering( filtering );
        copy.setOverwrite( false );
        copy.setPreserveLastModified( true );

        FileSet fileset = new FileSet();
        fileset.setDir( src );
        fileset.setIncludes( includes );
        fileset.setExcludes( excludes );
        copy.addFileset( fileset );

        copy.init();
        copy.execute();
    }
}
