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

package org.apache.avalon.tools.home;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Get;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.taskdefs.Sequential;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.taskdefs.optional.net.SetProxy;

import org.apache.avalon.tools.project.Definition;
import org.apache.avalon.tools.project.ResourceRef;
import org.apache.avalon.tools.project.Resource;
import org.apache.avalon.tools.project.Policy;

/**
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class Repository 
{
    public final String PROJECT_PROXY_HOST_KEY = "project.proxy.host";
    public final String PROJECT_PROXY_PORT_KEY = "project.proxy.port";
    public final String PROJECT_PROXY_USERNAME_KEY = "project.proxy.username";
    public final String PROJECT_PROXY_PASSWORD_KEY = "project.proxy.password";

    private final Home m_home;
    private final File m_root;
    private final File m_cache;
    private final String[] m_hosts;

    public Repository( 
      Project project, File system, String path, String hosts, Home home )
    {
        if( null == system ) 
        {
            throw new NullPointerException( "system" );
        }
        if( null == path ) 
        {
            throw new NullPointerException( "path" );
        }
        if( null == home ) 
        {
            throw new NullPointerException( "home" );
        }
        m_home = home;
        m_root = system;
        m_cache = getCanonicalFile( Context.getFile( system, path ) );
        m_hosts = getHostsSequence( hosts );

        setupProxy( project );
    }

    public File getCacheDirectory()
    {
        return m_cache;
    }

    public String[] getHosts()
    {
        return m_hosts;
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

    private String[] getHostsSequence( String path )
    {
        if( null == path )
        {
            return new String[0];
        }
        
        StringTokenizer tokenizer = new StringTokenizer( path, ";" );
        ArrayList list = new ArrayList();
        while( tokenizer.hasMoreTokens() )
        {
            list.add( tokenizer.nextToken() );
        }
        return (String[]) list.toArray( new String[0] );
    }

    private void setupProxy( Project project )
    {
        String host = project.getProperty( PROJECT_PROXY_HOST_KEY );
        if(( null == host ) || "".equals( host ) )
        {
            return;
        }
        else
        {
            int port = Integer.decode( 
              project.getProperty( PROJECT_PROXY_PORT_KEY ) ).intValue();
            String username = 
              project.getProperty( PROJECT_PROXY_USERNAME_KEY );
            String password = 
              project.getProperty( PROJECT_PROXY_PASSWORD_KEY );
            SetProxy proxy = 
              (SetProxy) project.createTask( "setproxy" );
            proxy.init();
            proxy.setProxyHost( host );
            proxy.setProxyPort( port );
            proxy.setProxyUser( username );
            proxy.setProxyPassword( password );
            proxy.execute();
        }
    }
}
