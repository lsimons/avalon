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

import java.io.File;
import java.io.IOException;

import java.util.Iterator;
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
    private File m_ProjectSystemDir;
    private File m_SystemDir;
    private File m_TempDir;
   
    private Project  m_AntProject;
    private PropertyResolver m_Resolver;
    
    
    PluginContext( File scriptDir )
    {
        this( "virtual", new File( "." ), new File( "../system" ), new PluginProperties(), "virtual plugin", 
              scriptDir, new File( "." ), new File( "." ), new Project() );
    }
    
    PluginContext( String projectName, File projectDir, File projectSystemDir, 
                   PluginProperties projectProps, String pluginName, 
                   File pluginDir, File systemDir, File tempDir, Project ant )
    {
        if( projectName == null )
            throw new IllegalArgumentException( "Null argument: projectName" );
        if( projectDir == null )
            throw new IllegalArgumentException( "Null argument: projectDir" );
        if( projectProps == null )
            throw new IllegalArgumentException( "Null argument: projectProps" );
        if( pluginName == null )
            throw new IllegalArgumentException( "Null argument: pluginName" );
        if( pluginDir == null )
            throw new IllegalArgumentException( "Null argument: pluginDir" );
        if( systemDir == null )
            throw new IllegalArgumentException( "Null argument: systemDir" );
        if( tempDir == null )
            throw new IllegalArgumentException( "Null argument: tempDir" );
        if( ant == null )
            throw new IllegalArgumentException( "Null argument: ant" );
            
        m_Resolver = new DefaultResolver( this );
        
        m_ProjectName = projectName.trim();
        m_ProjectDir = projectDir;
        m_ProjectProperties = projectProps;
        
        m_PluginDir = pluginDir;
        m_PluginName = pluginName.trim();
        
        m_SystemDir = systemDir;
        m_TempDir = tempDir;
        m_AntProject = ant;
        m_ProjectSystemDir = projectSystemDir;
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
    
    public File getProjectSystemDir()
    {
        return m_ProjectSystemDir;
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
        name = name.trim();
        String value = m_ProjectProperties.getProperty( name, m_Resolver );
        if( value == null )
        {
            if( name.equals( "plugin.dir" ) )
                return m_PluginDir.getAbsolutePath();
            if( name.equals( "system.dir" ) )
                return m_SystemDir.getAbsolutePath();
            if( name.equals( "temp.dir" ) )
                return m_TempDir.getAbsolutePath();
        }
        return value;
    }
    
    public void setProperty( String name, String value )
    {
        m_ProjectProperties.setProperty( name, value );
    }
    
    public Iterator getPropertyKeys()
    {
        return m_ProjectProperties.keySet().iterator();
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
        return m_ProjectProperties.resolve( value, m_Resolver );
    }
}
