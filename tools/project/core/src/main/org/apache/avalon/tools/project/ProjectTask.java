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

package org.apache.avalon.tools.project;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.taskdefs.Sequential;
import org.apache.tools.ant.types.Path;

import org.apache.avalon.tools.home.Home;
import org.apache.avalon.tools.home.Repository;
import org.apache.avalon.tools.event.StandardListener;

/**
 * Load the home defintion. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class ProjectTask extends Sequential
{
    private static final String USER_PROPERTIES = "user.properties";
    private static final String BUILD_PROPERTIES = "build.properties";

    private File m_index;

    private File m_props;

    private String m_key;

    public void setKey( String key )
    {
        m_key = key;
    }

    public void setIndex( File file ) throws BuildException
    {
        if( file.isAbsolute() )
        {
            m_index = getCanonicalFile( file );
        }
        else
        {
            String path = file.toString();
            File basedir = getProject().getBaseDir();
            File index = new File( basedir, path );
            m_index = getCanonicalFile( index );
        }
    }

    public void setProperties( File file ) throws BuildException
    {
        if( !file.exists() )
        {
            final String error = 
              "Properties file [" + file + "] does not exist.";
            throw new BuildException( error );
        }
        if( file.isAbsolute() )
        {
            m_props = getCanonicalFile( file );
        }
        else
        {
            String path = file.toString();
            File basedir = getProject().getBaseDir();
            File index = new File( basedir, path );
            m_props = getCanonicalFile( index );
        }
    }

    public void execute() throws BuildException 
    {
        //
        // make sure that the build.properties file is loaded
        //

        readProperties( 
          new File( getProject().getBaseDir(), USER_PROPERTIES ) );
        readProperties( getPropertiesFile() );

        //
        // make sure we have a common defintion available
        //

        final String key = getKey();
        if( !Home.isInitialized() )
        {
            log( "index: " + m_index, Project.MSG_INFO );
            Home.initialize( getProject(), m_index );
        }

        Home home = Home.getHome( getProject() );

        //
        // load the defintion for this project
        // and populate the project with required properties
        //

        Definition definition = home.getDefinition( key );
        BuildListener listener = new StandardListener( this, home, definition );
        getProject().addBuildListener( listener );
        setProjectProperties( home, definition );
        listener.buildStarted( new BuildEvent( getProject() ) );

        super.execute();

        /*
        if( !Home.isInitialized() )
        {
            log( "index: " + m_index, Project.MSG_INFO );
            Home.initialize( getProject(), m_index );
            Home home = Home.getHome( getProject() );

            //
            // log the build sequence
            //

            Definition definition = home.getDefinition( key );
            Definition[] targets = home.getBuildSequence( definition );
            log( "build sequence for definition: " + definition + "\n");
            for( int i=0; i<targets.length; i++ )
            {
                Definition def = targets[i];
                getProject().log( "   target (" + (i+1) + "): " + def ); 
            }
            getProject().log( "" );

            //
            // execute the build sequence
            //

            for( int i=0; i<targets.length; i++ )
            {
                Definition def = targets[i];
                home.build( def );
            }

            setProjectProperties( home, definition );
        }
        else
        {
            Home home = Home.getHome( getProject() );          
            Definition definition = home.getDefinition( key );
            buildProject( home, definition );
        }
        */
    }

    private File getPropertiesFile()
    {
        if( null == m_props )
        {
            return new File( getProject().getBaseDir(), BUILD_PROPERTIES );
        }
        else
        {
            return m_props;
        }
    }

    private void setProjectProperties( Home home, Definition definition )
    {
        if( null == home )
        {
            throw new NullPointerException( "home" );
        }
        if( null == definition )
        {
            throw new NullPointerException( "definition" );
        }

        File root = home.getHomeDirectory();
        getProject().setProperty( 
          "avalon.home", root.toString() );

        getProject().setProperty( 
          "avalon.project.key", definition.getKey() );
        getProject().setProperty( 
          "avalon.project.name", definition.getInfo().getName() );
        getProject().setProperty( 
          "avalon.project.group", definition.getInfo().getGroup() );
        if( null != definition.getInfo().getVersion() )
        {
            getProject().setProperty( 
              "avalon.project.version", definition.getInfo().getVersion() );
        }
    }

    private void buildProject( Home home, Definition definition )
    {
        log( "build: " + definition );
        setProjectProperties( home, definition );
        super.execute();
    }

    private Home createHome()
    {
        File index = getIndex();
        log( "index: " + index, Project.MSG_INFO );
        try
        {
            return new Home( getProject(), index );
        }
        catch( Throwable e )
        {
            final String error =
              "Error occured while loading system defintion.";
            throw new BuildException( error, e );
        }
    }

    private String getKey()
    {
        if( null != m_key )
        {
            return m_key;
        }
        else
        {
            return getProject().getName();
        }
    }

    private File getIndex()
    {
        if( null != m_index )
        {
            if( !m_index.exists() )
            {
                final String error = 
                  "Project index not found: [" + m_index + "]";
                throw new BuildException( error );
            }
            return m_index;
        }
        else
        {
            final String error = 
              "Missing index declaration.";
            throw new BuildException( error );
        }
    }

    private File getCanonicalFile( File file ) throws BuildException
    {
        try
        {
            return file.getCanonicalFile();
        }
        catch( IOException ioe )
        {
            throw new BuildException( ioe );
        }
    }

    private void readProperties( File file ) throws BuildException 
    {
        Property props = (Property) getProject().createTask( "property" );
        props.setFile( file );
        props.init();
        props.execute();
    }
}
