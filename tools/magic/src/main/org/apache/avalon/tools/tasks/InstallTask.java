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

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

import org.apache.avalon.tools.model.Home;
import org.apache.avalon.tools.model.Context;
import org.apache.avalon.tools.model.Definition;

/**
 * Install the ${basedir}/target/deliverables content into the local 
 * repository cache. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class InstallTask extends SystemTask
{
    private String m_id;

    public void setId( String id )
    {
        m_id = id;
    }

    public void execute() throws BuildException 
    {
        Definition definition = getReferenceDefinition();
        installDeliverables( definition );
    }

    private Definition getReferenceDefinition()
    {
        if( null != m_id )
        {
            return getHome().getDefinition( m_id );
        }
        else
        {
            return getHome().getDefinition( getKey() );
        }
    }

    private void installDeliverables( Definition definition )
    {
        File basedir = definition.getBasedir();
        File target = new File( basedir, Context.TARGET );
        File deliverables = new File( target, Context.DELIVERABLES );
        if( deliverables.exists() )
        {
            log( "Installing deliverables", Project.MSG_VERBOSE );
            File cache = getHome().getRepository().getCacheDirectory();
            FileSet fileset = new FileSet();
            fileset.setDir( deliverables );
            fileset.createInclude().setName( "**/*" );
            String group = getHome().getDefinition( getKey() ).getInfo().getGroup();
            File destination = new File( cache, group );
            copy( destination, fileset );
        }
    }

    private void copy( File destination, FileSet fileset )
    {
        mkDir( destination );
        Copy copy = (Copy) getProject().createTask( "copy" );
        copy.setPreserveLastModified( true );
        copy.setTodir( destination );
        copy.addFileset( fileset );
        copy.init();
        copy.execute();
    }
}
