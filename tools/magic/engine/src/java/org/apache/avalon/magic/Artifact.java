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

public class Artifact
{
    private static final String DEFAULT_REPOSITORY = "http://www.ibiblio.org/maven";

    private String m_Repository;    
    private String m_ArtifactId;
    private String m_GroupId;
    private String m_Version;
    private String m_Type;

    public Artifact( String id, String version )
    {
        this( id, id, version );
    }
    
    public Artifact( String artifactId, String groupId, String version)
    {
        this( artifactId, groupId, version, "jar" );
    }
    
    public Artifact( String artifactId, String groupId, String version, String type )
    {
        this( artifactId, groupId, version, type, DEFAULT_REPOSITORY );
    }
    
    public Artifact( String artifactId, String groupId, String version, String type, String repository )
    {
        m_Repository = repository;
        m_ArtifactId = artifactId;
        m_GroupId = groupId;
        m_Version = version;
        m_Type = type;
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
} 
