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
public class JarTask extends AbstractDeliverableTask
{
    public static final String JAR_EXT = "jar";
    public static final String JAR_MAIN_KEY = "project.jar.main.class";
    public static final String JAR_CLASSPATH_KEY = "project.jar.classpath";
    
    public void execute() throws BuildException 
    {
        File classes = 
          getContext().getBuildPath( JavacTask.BUILD_CLASSES_KEY );
        File deliverables = 
          getContext().getDeliverablesDirectory();

        Definition def = getHome().getDefinition( getKey() );
        File jarFile = getJarFile( deliverables );
        if( classes.exists() )
        {
            try
            {
                boolean modified = jar( def, classes, jarFile );
                if( modified )
                {
                    checksum( jarFile );
                    asc( jarFile );
                }
            }
            catch( IOException ioe )
            {
                throw new BuildException( ioe );
            }
        }
        getContext().setBuildPath( "jar", jarFile.toString() );
    }

    public File getJarFile( File deliverables )
    {
        Project project = getProject();
        Definition def = getHome().getDefinition( getKey() );
        String type = def.getInfo().getType();
        File types = new File( deliverables, type + "s" );
        String filename = def.getFilename( JAR_EXT );
        return new File( types, filename );
    }

    private boolean jar( Definition def, File classes, File jarFile )
    {
        File dir = jarFile.getParentFile();
        mkDir( dir );

        long modified = -1;
        if( jarFile.exists() )
        {
            modified = jarFile.lastModified();
        }
 
        Jar jar = (Jar) getProject().createTask( "jar" );
        jar.setDestFile( jarFile );
        jar.setBasedir( classes );
        jar.setIndex( true );
        addManifest( jar, def );
        jar.init();
        jar.execute();

        return jarFile.lastModified() > modified;
    }

    private void addManifest( Jar jar, Definition def )
    {
        try
        {
            Manifest manifest = new Manifest();
            Manifest.Section main = manifest.getMainSection();

            addAttribute( main, "Created-By", "Apache Avalon" );
            addAttribute( main, "Built-By", System.getProperty( "user.name" ) );
            addAttribute( main, "Extension-Name", def.getInfo().getName() );
            addAttribute( 
              main, "Specification-Vendor", 
              "The Apache Software Foundation Avalon Project" );

            if( null != def.getInfo().getVersion() )
            {

                // TODO: validate that the version is a dewy version

                addAttribute( 
                  main, "Specification-Version", 
                  def.getInfo().getVersion() );
            }
            else
            {
                addAttribute( main, "Specification-Version", "0" ); 
            }
            addAttribute( 
              main, "Implementation-Vendor", 
              "The Apache Software Foundation Avalon Project" );
            addAttribute( 
              main, "Implementation-Vendor-Id", 
              "org.apache.avalon" );

            // TODO: get a real implementation version id
            
            addAttribute( 
              main, "Implementation-Version", "UNKNOWN" ); 

            String classpath = getProject().getProperty( JAR_CLASSPATH_KEY );
            if( null != classpath )
            {
                addAttribute( main, "Class-Path", classpath );
            }

            String mainClass = getProject().getProperty( JAR_MAIN_KEY );
            if( null != mainClass )
            {
                addAttribute( main, "Main-Class", mainClass );
            }
            
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
}
