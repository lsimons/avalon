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
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.taskdefs.Mkdir;

import org.apache.avalon.tools.home.Home;
import org.apache.avalon.tools.project.Definition;

/**
 * Abstract task that provides convinience opperations including 
 * access to the current project defintion and the system home 
 * model and repository.  
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public abstract class HomeTask extends Task
{
    private Home m_home;
    private Definition m_definition;

    public void init() throws BuildException 
    {
        m_home = Home.getHome( getProject() );
        String key = getProject().getProperty( "avalon.project.key" );
        m_definition = m_home.getDefinition( key );
    }

    protected Home getHome()
    {
        return m_home;
    }

    protected Definition getDefinition()
    {
        return m_definition;
    }

    protected void setProjectProperty( String key, String value )
    {
        Property props = (Property) getProject().createTask( "property" );
        props.setName( key );
        props.setValue( value );
        props.init();
        props.execute();
    }

    protected void createDirectory( File dir )
    {
        Mkdir mkdir = (Mkdir) getProject().createTask( "mkdir" );
        mkdir.setDir( dir );
        mkdir.init();
        mkdir.execute();
    }

    protected void delete( File file )
    {
        delete( file, true );
    }

    protected void delete( File file, boolean verbose )
    {
        Delete delete = (Delete) getProject().createTask( "delete" );
        delete.setFile( file );
        delete.setVerbose( verbose );
        delete.setQuiet( true );
        delete.init();
        delete.execute();
    }
}
