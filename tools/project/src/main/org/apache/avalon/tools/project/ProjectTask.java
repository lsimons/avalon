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
import org.apache.tools.ant.taskdefs.Sequential;
import org.apache.tools.ant.types.Path;

import org.apache.avalon.tools.home.Home;
import org.apache.avalon.tools.home.Repository;

/**
 * Load the home defintion. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class ProjectTask extends Sequential
{
    private File m_index;

    private File m_definition;

    private Home m_home;

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

    public void execute() throws BuildException 
    {
        //
        // get the Home defintion
        //

        File index = getIndex();
        log( "index: " + index, Project.MSG_INFO );
        try
        {
            m_home = new Home( getProject(), index );
        }
        catch( Throwable e )
        {
            final String error =
              "Error occured while loading system defintion.";
            throw new BuildException( error, e );
        }

        Repository repo = m_home.getRepository();
        File cache = repo.getCacheDirectory();

        //
        // get the definition for this project
        //
   
        log( "project: " + getProject().getName(), Project.MSG_DEBUG );
        log( "basedir: " + getProject().getBaseDir(), Project.MSG_DEBUG );

        final String key = getKey();
        Definition definition = m_home.getDefinition( key );
        Info info = definition.getInfo();

        log( "name: " + info.getName(), Project.MSG_DEBUG );
        log( "group: " + info.getGroup(), Project.MSG_DEBUG );
        log( "version: " + info.getVersion(), Project.MSG_DEBUG );

        //
        // Path path = repo.createPath( getProject(), m_home, definition );
        // log( "path: " + path );
        //

        m_home.build( definition );

        super.execute();
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
}
