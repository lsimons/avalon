
package org.apache.merlin.magic;

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
