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

package org.apache.avalon.tools.model;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class MagicPath extends Path
{
    private Context m_context;
    private Home m_home;
    private String m_key;
    private int m_mode = Policy.RUNTIME;

    public MagicPath( Project project )
    {
        super( project );
        setup();
    }

    public void setKey( final String key )
    {
        m_key = key;
    }

    public void setMode( final String mode )
    {
        if( "ANY".equalsIgnoreCase( mode ) )
        {
            m_mode = Policy.ANY;
        }
        else if( "BUILD".equalsIgnoreCase( mode ) )
        {
            m_mode = Policy.BUILD;
        }
        else if( "TEST".equalsIgnoreCase( mode ) )
        {
            m_mode = Policy.TEST;
        }
        else if( "RUNTIME".equalsIgnoreCase( mode ) )
        {
            m_mode = Policy.RUNTIME;
        }
        else
        {
            final String error = 
              "Invalid mode argument [" + mode 
              + "] - use ANY, BUILD, TEST or RUNTIME.";
            throw new BuildException( error );
        }
        setup();
    }

    private int getMode()
    {
        return m_mode;
    }

    private Definition getReferenceDefinition()
    {
        if( null != m_key )
        {
            return getHome().getDefinition( m_key );
        }
        else
        {
            return getHome().getDefinition( getKey() );
        }
    }

    private void setup()
    {
        if( null == m_context )
        {
            m_context = Context.getContext( getProject() );
        }
        if( null == m_home ) 
        {
            Home home = (Home) getProject().getReference( Home.KEY );
            if( null == home )
            {
                final String error = 
                  "Undefined home.";
                throw new BuildException( error );
            }
            else
            {
                m_home = home;
            }
        }

        Definition def = getReferenceDefinition();
        Path path = def.getPath( getProject(), getMode() );
        super.add( path );
    }

    private Context getContext()
    {
        return m_context;
    }

    private String getKey()
    {
        return getContext().getKey();
    }

    private Home getHome()
    {
        return m_home;
    }

}
