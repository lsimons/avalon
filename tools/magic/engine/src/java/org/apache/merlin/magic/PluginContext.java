package org.apache.merlin.magic;

import java.io.File;
import java.util.Properties;
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
    private Properties m_ProjectProperties;

    private String m_PluginClassname;    
    private String m_PluginName;
    private File m_PluginDir;
    private File m_SystemDir;
   
    private Project  m_AntProject;
    
    
    PluginContext( String projectname, File projectDir, Properties projectProps,
                   String pluginname, File pluginDir, File systemDir )
    {
        m_ProjectName = projectname.trim();
        m_ProjectDir = projectDir;
        m_ProjectProperties = projectProps;
        
        m_PluginDir = pluginDir;
        m_PluginName = pluginname.trim();
        
        m_SystemDir = systemDir;
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
    
    public Properties getProjectProperties()
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
        String value = m_ProjectProperties.getProperty( name );
        if( value == null )
            return null;
        value = value.trim();
        return resolve( value );
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
        // optimization for common case.
        int pos1 = value.indexOf( "${" );
        if( pos1 < 0 )
            return value;
        
        Stack stack = new Stack();
        StringTokenizer st = new StringTokenizer( value, "${}", true );
        
        while( st.hasMoreTokens() )
        {
            String token = st.nextToken();
            if( token.equals( "}" ) )
            {
                String name = (String) stack.pop();
                String open = (String) stack.pop();
                if( open.equals( "${" ) )
                {
                    String propValue = getProperty( name );
                    if( propValue == null )
                        push( stack, "${" + name + "}" );
                    else
                        push( stack, propValue );
                }
                else
                {
                    push( stack, "${" + name + "}" );
                }
            }
            else
            {
                if( token.equals( "$" ) )
                    stack.push( "$" );
                else
                {
                    push( stack, token );
                }
            }
        }
        String result = "";
        while( stack.size() > 0 )
        {
            result = (String) stack.pop() + result;
        }
        return result;
    }
    
    private void push( Stack stack , String value )
    {
        if( stack.size() > 0 )
        {
	        String data = (String) stack.pop();
	        if( data.equals( "${" ) )
	        {
	            stack.push( data );
	            stack.push( value );
	        }
	        else
	        {
	            stack.push( data + value );
	        }
        }
        else
        {
            stack.push( value );
        }
    }
}
