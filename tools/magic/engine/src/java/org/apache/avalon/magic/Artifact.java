/*
Copyright 2004 The Apache Software Foundation
Licensed  under the  Apache License,  Version 2.0  (the "License");
you may not use  this file  except in  compliance with the License.
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed  under the  License is distributed on an "AS IS" BASIS,
WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
implied.

See the License for the specific language governing permissions and
limitations under the License.
*/

package org.apache.avalon.magic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

public class Artifact
{
    private static final String DEFAULT_REPOSITORY = "http://www.ibiblio.org/maven";

    private String          m_Repository;    
    private String          m_ArtifactId;
    private String          m_GroupId;
    private String          m_Version;
    private String          m_Type;
    private PluginContext   m_Context;
    private Properties      m_Properties;
    
    private Artifact( PluginContext context, String artifactId, String groupId, String version, String type, String repository, String properties )
    {
        if( repository == null || "".equals( repository ) )
            repository = DEFAULT_REPOSITORY;
        
        m_Context = context;
        m_Repository = repository.trim();
        m_ArtifactId = artifactId.trim();
        m_GroupId = groupId.trim();
        m_Version = version.trim();
        m_Type = type.trim();
        m_Properties = parseProps( properties );
    }

    public static Artifact resolve( PluginContext context, String artifactId )
        throws IOException, ArtifactException
    {
        if( artifactId.startsWith( "artifact:" ) )
            return resolveDirect( context, artifactId, "" );
        else
            return resolveIndirect( context, artifactId, "" );
    }
    
    public static Artifact resolve( PluginContext context, String artifactId, String properties )
        throws IOException, ArtifactException
    {
        if( artifactId.startsWith( "artifact:" ) )
            return resolveDirect( context, artifactId, properties );
        else
            return resolveIndirect( context, artifactId, properties );
    }
    
    private static Artifact resolveDirect( PluginContext context, String artifactId, String properties )
        throws IOException, ArtifactException
    {
        String id = artifactId;
        int posColon1 = id.indexOf( ':' );
        int posColon2 = id.indexOf( ":", posColon1 + 1 );
        if( posColon2 < 0 )
            throw new ArtifactException( "Invalid format in '" + id + "'. No type." );
        int posHash = id.indexOf( '#' );
        int posSlash = id.lastIndexOf( '/' );
        if( posSlash < 0 )
            throw new ArtifactException( "Invalid format in '" + id + "'. No group." );
        
        String type = id.substring( posColon1 + 1, posColon2 );
        String group = id.substring( posColon2 + 1, posSlash );
        String name;
        String version;
        if( posHash < 0 )
        {
            name = id.substring( posSlash+1 );
            version = "1.0-dev.0";
        }
        else
        {
            name = id.substring( posSlash+1, posHash );
            version = id.substring( posHash+1 );
        }
        String repository = context.getProperty( "artifact.repository" );
        
        Artifact artifact = new Artifact( context, name, group, version, type, repository, properties );
        return artifact;
    }
    
    private static Artifact resolveIndirect( PluginContext context, String artifactId, String properties )
        throws IOException
    {
        File definitionsDir = new File( context.getProjectSystemDir(), "definitions" );
        File file = new File( definitionsDir, artifactId );
        Properties p = new Properties();
        if( file.exists() )
        {
            FileInputStream fis = new FileInputStream( file );
            try
            {
                p.load( fis );
            } finally
            {
                if( fis != null )
                    fis.close();
            }
        }
        
        String repository = p.getProperty( "artifact.repository" );
        if( repository == null )
            repository = context.getProperty( "artifact.repository" );

        String groupId = p.getProperty( "artifact.group" );
        if( groupId == null )
            groupId = artifactId;

        String version = p.getProperty( "artifact.version" );
        if( version == null )
            version = "1.0.dev-0";

        String type = p.getProperty( "artifact.type" );
        if( type == null )
            type = "jar";

        String props = p.getProperty( "artifact.properties" );
        if( props == null )
            props = "";
        if( properties != null && ! "".equals( properties ))
            props = props + "," + properties;  // override with explicit properties given.
        
        Artifact artifact = new Artifact( context, artifactId, groupId, version, type, repository, props );
        return artifact;
    }
    
    public Artifact[] getDependencies()
        throws IOException, ArtifactException
    {
        File depsDir = new File( m_Context.getProjectSystemDir(), "dependencies" );
        File file = new File( depsDir, getArtifactId() );
        if( ! file.exists() )
            return new Artifact[0];
        FileReader reader = null;
        BufferedReader br = null;
        ArrayList deps = new ArrayList();
        try
        {
            reader = new FileReader( file );
            br = new BufferedReader( reader );
            String line;
            while( ( line = br.readLine() ) != null )
            {
                line = line.trim();
                if( line.startsWith( "#" ) )
                    continue;
                int pos = line.indexOf( "//" );
                if( pos >= 0 )
                    line = line.substring( 0, pos ).trim();
                if( ! line.equals( "" ) )
                    deps.add( line );
            }
        } catch( IOException e )
        {
            e.printStackTrace();
            throw e;
        } finally
        {
            if( reader != null )
                reader.close();
            if( br != null )
                br.close();
        }
        Artifact[] result = new Artifact[ deps.size() ];
        Iterator list = deps.iterator();
        for( int i=0 ; list.hasNext() ; i++ )
        {
            String dep = (String) list.next();
            int pos = dep.indexOf( " " );
            String props = "";
            if( pos >= 0 )
            {
                props = dep.substring( pos + 1 );
                dep = dep.substring( 0, pos );
            }
            result[i] = resolve( m_Context, dep, props );
        }
        return result;
    }
    
    public String getRepository()
    {
        return m_Repository;
    }
    
    public String getGroupId()
    {
        return m_GroupId;
    }    
    
    public String getType()
    {
        return m_Type;
    }
    
    public String getArtifactId()
    {
        return m_ArtifactId;
    }

    public String getVersion()
    {
        return m_Version;
    }   
    
    public String toString()
    {
        return m_Repository + "/" + m_GroupId + "/" + m_Type + "s/" + m_ArtifactId + "-" + m_Version + "." + m_Type;
    } 
    
    public String getFilename()
    {
        return m_ArtifactId + "-" + m_Version + "." + m_Type;
    }
    
    public URL toRemoteURL()
        throws MalformedURLException
    {
        String href = getRepository() + "/" + 
                      getGroupId() + "/" +
                      getType() + "s/" +
                      getArtifactId() + "-" +
                      getVersion()  + ".jar" ;
        return new URL( href );
    }
    
    public File toLocalFile()
        throws IOException
    {
        String localRepo = m_Context.getProperty( "artifact.local.repository.dir" );
        if( localRepo == null || "".equals( localRepo ) )
            localRepo = m_Context.getProperty( "user.home" ) + "/.maven/repository";

        String href = localRepo + "/" + 
                      getGroupId() + "/" +
                      getType() + "s/" +
                      getArtifactId() + "-" +
                      getVersion()  + ".jar" ;
        File localFile = new File( href );
        return localFile;
    }
    
    public String toArtifactURL()
    {
        return "artifact:" + m_Type + ":" + m_GroupId + "/" + m_ArtifactId + "#" + m_Version;
    }
    
    public File getContentFile()
        throws IOException
    {
        File localfile = toLocalFile();
        if( ! localfile.exists() )
        {
            localfile.getParentFile().mkdirs();
            Util.download( this, localfile );
        }
        return localfile;
    }
    
    private Properties parseProps( String props ) 
    {
        Properties result = new Properties();
        if( props != null )
        {
            StringTokenizer st = new StringTokenizer( props, ";", false );
            while( st.hasMoreTokens() )
            {
                String pair = st.nextToken().trim();
                int pos = pair.indexOf( "=" );
                if( pos < 0 )
                    result.put( pair, "" );
                else
                {
                    String key = pair.substring( 0, pos );
                    String value = pair.substring( pos + 1 );
                    result.put( key, value );
                }
            }
        }
        return result;
    }
    
    public String getProperty( String key )
    {
        return m_Properties.getProperty( key );
    }
} 
