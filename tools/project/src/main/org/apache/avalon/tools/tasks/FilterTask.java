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
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Filter;

import org.apache.avalon.tools.home.Context;
import org.apache.avalon.tools.home.Home;
import org.apache.avalon.tools.project.Resource;
import org.apache.avalon.tools.project.Definition;
import org.apache.avalon.tools.project.ResourceRef;

/**
 * Build a set of projects taking into account dependencies within the 
 * supplied fileset. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class FilterTask extends SystemTask
{
    private String m_key;
    private String m_feature;
    private String m_token;

    public void init()
    {
        if( !isInitialized() )
        {
            super.init();
        }
    }

    public void setKey( String key )
    {
        m_key = key;
    }

    public void setFeature( String feature )
    {
        m_feature = feature;
    }

    public void setToken( String token )
    {
        m_token = token;
    }

    public void execute() throws BuildException 
    {
        if( null == m_key )
        {
            m_key = getContext().getKey();
        }
        if( null == m_feature )
        {
            log( "Ignoring request due to missing 'feature' attribute." );
            return;
        }
        if( null == m_token )
        {
            log( "Ignoring request due to missing 'token' attribute." );
            return;
        }

        String value = getFeature();
        if( null != value )
        {
            Filter filter = (Filter) getProject().createTask( "filter" );
            filter.init();
            filter.setToken( m_token );
            filter.setValue( value );
            filter.execute();
        }
        else
        {
            log( "Unrecognized or unsupported feature [" + m_feature + "]." );
        }
    }

    private String getFeature()
    {
        ResourceRef ref = new ResourceRef( m_key );
        Resource resource = getHome().getResource( ref );
        if( m_feature.equals( "name" ) )
        {
            return resource.getInfo().getName();
        }
        else if( m_feature.equals( "group" ) )
        {
            return resource.getInfo().getGroup();
        }
        else if( m_feature.equals( "version" ) )
        {
            return resource.getInfo().getVersion();
        }
        else if( m_feature.equals( "uri" ) )
        {
            return resource.getInfo().getURI();
        }
        return null;        
    }
}
