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
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

import org.apache.avalon.tools.home.Context;
import org.apache.avalon.tools.home.Home;
import org.apache.avalon.tools.project.Definition;

/**
 * Load a goal. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class JarTask extends Task
{
    public static final String MD5_EXT = "md5";
    public static final String JAR_EXT = "jar";
    public static final String ASC_EXT = "asc";
    public static final String GPG_EXE_KEY = "project.gpg.exe";
    
    private boolean m_init = false;
    private Context m_context;
    private Home m_home;

   /**
    * Set the home ref id.
    * @param id a home id
    */
    public void setRefid( String id )
    {
        Object object = getProject().getReference( id );
        if( null == object )
        {
            final String error = 
              "Unknown ref id '" + id + "'.";
            throw new BuildException( error );
        }
        if( object instanceof Home )
        {
            m_home = (Home) object;
        }
        else
        {
            final String error = 
              "Supplied id '" + id + "' does not refer to a Home.";
            throw new BuildException( error );
        }
    }

    public void init() throws BuildException 
    {
        if( !m_init )
        {
            Project project = getProject();
            m_context = Context.getContext( project );
            m_init = true;
        }
    }

    public void execute() throws BuildException 
    {
        if( null == m_home ) 
        {
            final String error = 
              "Required system home 'refid' attribute not set in the task definition ["
              + getTaskName() + "].";
            throw new BuildException( error );
        }

        File classes = 
          m_context.getBuildPath( JavacTask.BUILD_CLASSES_KEY );
        File deliverables = 
          m_context.getDeliverablesDirectory();

        File jarFile = getJarFile( deliverables );
        if( classes.exists() )
        {
            try
            {
                boolean modified = jar( classes, jarFile );
                checksum( jarFile, modified );
                asc( jarFile, modified );
            }
            catch( IOException ioe )
            {
                throw new BuildException( ioe );
            }
        }
        m_context.setBuildPath( "jar", jarFile.toString() );
    }

    public File getJarFile( File deliverables )
    {
        Project project = getProject();
        Definition def = m_home.getDefinition();
        String type = m_home.getDefinition().getInfo().getType();
        File types = new File( deliverables, type + "s" );
        String filename = getJarFilename( def );
        return new File( types, filename );
    }

    public String getJarFilename( Definition def )
    {
        String name = def.getInfo().getName();
        if( null != def.getInfo().getVersion() )
        {
            return name + "-" + def.getInfo().getVersion() + "." + JAR_EXT;
        }
        else
        {
            return name + "." + JAR_EXT;
        }
    }

    private boolean jar( File classes, File jarFile )
    {
        File dir = jarFile.getParentFile();

        Mkdir mkdir = (Mkdir) getProject().createTask( "mkdir" );
        mkdir.setDir( dir );
        mkdir.init();
        mkdir.execute();

        long modified = -1;
        if( jarFile.exists() )
        {
            modified = jarFile.lastModified();
        }
 
        Jar jar = (Jar) getProject().createTask( "jar" );
        jar.setDestFile( jarFile );
        jar.setBasedir( classes );
        jar.setIndex( true );
        jar.init();
        jar.execute();

        return jarFile.lastModified() > modified;
    }

    private void checksum( File jar, boolean modified )
    {
        if( modified )
        {
            log( "Creating md5 checksum" );
        }

        Checksum checksum = (Checksum) getProject().createTask( "checksum" );
        checksum.setFile( jar );
        checksum.setFileext( "." + MD5_EXT );
        checksum.init();
        checksum.execute();
    }

    private void asc( File jar, boolean modified ) throws IOException
    {
        File md5 = new File( jar.toString() + "." + ASC_EXT );
        if( modified )
        {
            if( md5.exists() )
            {
                md5.delete();
            }

            String gpg = getProject().getProperty( GPG_EXE_KEY );
            if( null != gpg )
            {
                log( "Creating asc signature" );
                Execute execute = new Execute();
                execute.setCommandline( 
                  new String[]{ gpg, "-a", "-b", jar.toString() } );
                execute.setWorkingDirectory( getProject().getBaseDir() );
                execute.setSpawn( true );
                execute.execute();
            }
        }
    }
}
