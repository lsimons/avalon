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
public class BarTask extends SystemTask
{
    public static final String MD5_EXT = "md5";
    public static final String BAR_EXT = "bar";
    public static final String ASC_EXT = "asc";
    public static final String GPG_EXE_KEY = "project.gpg.exe";

    private String m_name;

    public void setName( String name )
    {
        m_name = name;
    }
    
    private String getName( Definition def )
    {
        if( null == m_name )
        {
            return def.getFilename( BAR_EXT );
        }
        else
        {
            return m_name;
        }
    }

    public void execute() throws BuildException 
    {
        File deliverables = 
          getContext().getDeliverablesDirectory();
        Definition def = getHome().getDefinition( getKey() );
        String filename = getName( def );
        String type = def.getInfo().getType();
        File types = new File( deliverables, BAR_EXT + "s" );
        File bar = new File( types, filename );

        if( deliverables.exists() )
        {
            try
            {
                boolean modified = bar( def, deliverables, bar );
                if( modified )
                {
                    checksum( bar );
                    asc( bar );
                }
            }
            catch( IOException ioe )
            {
                throw new BuildException( ioe );
            }
        }
    }

    private boolean bar( Definition def, File deliverables, File bar )
    {
        File dir = bar.getParentFile();
        mkDir( dir );

        long modified = -1;
        if( bar.exists() )
        {
            modified = bar.lastModified();
        }

        FileSet fileset = new FileSet();
        fileset.setDir( deliverables );
        fileset.createInclude().setName( "**/*" );
        fileset.createExclude().setName( "**/*." + BAR_EXT + "*" );
 
        Jar jar = (Jar) getProject().createTask( "jar" );
        jar.setDestFile( bar );
        jar.addFileset( fileset );
        jar.setIndex( true );
        addManifest( jar, def );
        jar.init();
        jar.execute();

        return bar.lastModified() > modified;
    }

    private void addManifest( Jar jar, Definition def )
    {
        try
        {
            Manifest manifest = new Manifest();
            Manifest.Section main = manifest.getMainSection();
            addAttribute( main, "Created-By", "Apache Avalon" );
            addAttribute( main, "Built-By", System.getProperty( "user.name" ) );    

            Manifest.Section block = new Manifest.Section();
            block.setName( "Block" );
            addAttribute( block, "Block-Group", def.getInfo().getGroup() );    
            addAttribute( block, "Block-Name", def.getInfo().getName() );
            if( null != def.getInfo().getVersion() )
            {
                addAttribute( block, "Block-Version", def.getInfo().getVersion() );
            }

            manifest.addConfiguredSection( block );
            jar.addConfiguredManifest( manifest );
        }
        catch( Throwable e )
        {
            throw new BuildException( e );
        }
    }

    private void addAttribute( 
      Manifest.Section section, String name, String value )
      throws ManifestException
    {
        Manifest.Attribute attribute = new Manifest.Attribute( name, value );
        section.addConfiguredAttribute( attribute );
    }

    private void checksum( File file )
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

    private void asc( File file ) throws IOException
    {
        File asc = new File( file.toString() + "." + ASC_EXT );

        Delete delete = (Delete) getProject().createTask( "delete" );
        delete.init();
        delete.setFile( asc );
        delete.execute();

        String gpg = getProject().getProperty( GPG_EXE_KEY );
        if( null != gpg )
        {
            log( "Creating asc signature" );
            Execute execute = new Execute();
            execute.setCommandline( 
              new String[]{ gpg, "-a", "-b", file.toString() } );
            execute.setWorkingDirectory( getProject().getBaseDir() );
            execute.setSpawn( true );
            execute.execute();
        }
    }
}
