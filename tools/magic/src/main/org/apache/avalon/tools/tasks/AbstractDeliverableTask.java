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
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Mkdir;
import org.apache.tools.ant.taskdefs.Checksum;
import org.apache.tools.ant.taskdefs.Manifest;
import org.apache.tools.ant.taskdefs.ManifestException;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

import org.apache.avalon.tools.home.Context;
import org.apache.avalon.tools.home.Home;
import org.apache.avalon.tools.project.Definition;
import org.apache.avalon.tools.project.ResourceRef;

/**
 * Load a goal. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class AbstractDeliverableTask extends SystemTask
{
    public static final String MD5_EXT = "md5";
    public static final String ASC_EXT = "asc";
    public static final String GPG_EXE_KEY = "project.gpg.exe";

    public void checksum( File file )
    {
        log( "Creating md5 checksum" );

        File md5 = new File( file.toString() + "." + MD5_EXT );

        Delete delete = (Delete) getProject().createTask( "delete" );
        delete.setFile( md5 );
        delete.init();
        delete.execute();

        Checksum checksum = (Checksum) getProject().createTask( "checksum" );
        checksum.setFile( file );
        checksum.setFileext( "." + MD5_EXT );
        checksum.init();
        checksum.execute();
    }

    public void asc( File file ) throws IOException
    {
        File asc = new File( file.toString() + "." + ASC_EXT );
        if( asc.exists() )
        {
            asc.delete();
        }

        String gpg = getProject().getProperty( GPG_EXE_KEY );
        if(( null != gpg ) && !"".equals( gpg ) )
        {
            log( "Creating asc signature using '" + gpg + "']" );
            Execute execute = new Execute();
            execute.setCommandline( 
              new String[]{ gpg, "-a", "-b", file.toString() } );
            execute.setWorkingDirectory( getProject().getBaseDir() );
            execute.setSpawn( true );
            execute.execute();
        }
    }
}
