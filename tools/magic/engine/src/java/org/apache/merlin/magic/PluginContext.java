package org.apache.merlin.magic;

import java.io.File;
import java.util.Stack;
import java.util.StringTokenizer;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.tools.ant.Project;

import bsh.Interpreter;

public class PluginContext extends AbstractLogEnabled
    implements Context 
{
    private String m_ProjectName;
    private File m_ProjectDir;
    private PluginProperties m_ProjectProperties;

    private String m_PluginClassname;    
    private String m_PluginName;
    private File m_PluginDir;
    private File m_SystemDir;
    private File m_TempDir;
   
    private Project  m_AntProject;
    
    
    PluginContext( File scriptDir )
    {
        this( "virtual", new File( "." ), new PluginProperties(), "virtual plugin", 
              scriptDir, new File( "." ), new File( "." ) );
    }
    
    PluginContext( String projectname, File projectDir, PluginProperties projectProps,
                   String pluginname, File pluginDir, File systemDir, File tempDir )
    {
        m_ProjectName = projectname.trim();
        m_ProjectDir = projectDir;
        m_ProjectProperties = projectProps;
        
        m_PluginDir = pluginDir;
        m_PluginName = pluginname.trim();
        
        m_SystemDir = systemDir;
        m_TempDir = tempDir;
        initializeAntProject();
    }

    private void initializeAntProject()
    {
        m_AntProject = new Project();
        m_AntProject.setBaseDir( m_ProjectDir );
        m_AntProject.setCoreLoader( this.getClass().getClassLoader() );
        m_AntProject.setName( m_ProjectName );
        m_AntProject.init();
    }
        
    public Object get( Object entry )
    {
        if( !( entry instanceof String ))
            return null;
        if( "project.name".equals( entry ) )
            return getProjectName();
        if( "project.dir".equals( entry ) )
            return getProjectDir();
        if( "project.properties".equals( entry ) )
            return getProjectProperties();
        if( "plugin.name".equals( entry ) )
            return getPluginName();
        if( "plugin.classname".equals( entry ) )
            return getPluginClassname();
        if( "plugin.dir".equals( entry ) )
            return getPluginDir();
        if( "system.dir".equals( entry ) )
            return getSystemDir();
        if( "temp.dir".equals( entry ) )
            return getTempDir();
        return null;
    }
    
    public String getProjectName()
    {
        return m_ProjectName;
    }
    
    public File getProjectDir()
    {
        return m_ProjectDir;
    }
    
    public PluginProperties getProjectProperties()
    {
        return m_ProjectProperties;
    }
    
    public String getPluginName()
    {
        return m_PluginName;
    }
    
    public File getPluginDir()
    {
        return m_PluginDir;
    }
    
    public File getSystemDir()
    {
        return m_SystemDir;
    }
    
    public File getTempDir()
    {
        return m_TempDir;
    }
    
    public String getPluginClassname()
    {
        return m_PluginClassname;
    }
    
    void setPluginClassname( String pluginclassname )
    {
        m_PluginClassname = pluginclassname;
    }
    
    public String getProperty( String name )
    {
        String value = m_ProjectProperties.getProperty( name );
        return value;
    }
    
    public Project getAntProject()
    {
        return m_AntProject;
    }
    
    public void enableBeanShellDebug( boolean on )
    {
        Interpreter.DEBUG = on;
    }

    public void enableBeanShellTracing( boolean on )
    {
        Interpreter.TRACE = on;
    }
    
    public String resolve( String value )
    {
        return m_ProjectProperties.resolve( value );
    }
}
