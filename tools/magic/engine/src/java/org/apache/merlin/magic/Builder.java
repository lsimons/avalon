package org.apache.merlin.magic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.lang.reflect.Method;

import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;

import org.apache.tools.ant.Project;

public class Builder
{
    private Logger   m_Logger;
        
    private String[] m_CallMethods;
    private File     m_SystemDir;
    private File     m_ProjectDir;
    private File     m_PluginsDir;
    private File     m_TempDir;
    private Project  m_AntProject;
    
    public Builder( String[] methods, File projectDir )
    {
        m_Logger = new ConsoleLogger();
        
        m_CallMethods = new String[ methods.length ];
        for( int i=0 ; i < methods.length ; i++ )
            m_CallMethods[i] = methods[i];
        m_SystemDir = findSystemDir();
        m_ProjectDir = projectDir;
        m_PluginsDir = new File( m_SystemDir, "plugins" );
        m_TempDir = new File( m_SystemDir, "temp" );
        m_TempDir.mkdirs();
        
        m_Logger.info( " System Directory: " + m_SystemDir );
        m_Logger.info( "Project Directory: " + m_ProjectDir );
    }
    
    public void execute()
        throws Exception
    {
        PluginProperties props = new PluginProperties();
        
        // This is included twice, so a reference to other parts
        // can be obtained in the local project properties file.
        loadProjectLocalProperties( props );
        
        loadGlobalProperties( props );
        loadMagicSystemProperties( props );
        loadMagicPluginProperties( props );
        loadProjectSystemProperties( props );
        loadProjectLocalProperties( props );
        loadUserProjectProperties( props );
        loadUserSystemProperties( props );
        loadUserHomeProperties( props );
        
        m_AntProject = initializeAntProject( props );
        
        FacadeFactory factory = new FacadeFactory();
        if( factory instanceof LogEnabled )
            ((LogEnabled) factory).enableLogging( m_Logger );
            
        PluginServiceManager sm = new PluginServiceManager( factory, props, m_AntProject );
        sm.enableLogging( m_Logger );
        
        for( int i=0 ; i < m_CallMethods.length ; i++ )
        {
            String methodname = m_CallMethods[i];
            int pos = methodname.indexOf( "." );
            Plugin plugin;
            String pluginname = ".";
            if( pos <= 0 )
            {
                // project method
                plugin = sm.lookupPlugin( "." );
            }
            else
            {   
                // plugin method
                pluginname = methodname.substring( 0, pos );
                plugin = sm.lookupPlugin( pluginname );
                if( pos + 1 >= methodname.length() )
                    methodname = pluginname;
                else
                    methodname = methodname.substring( pos + 1 );
            }
            if( plugin != null )
            {
                Class pluginclass = plugin.getClass();
                Method m = pluginclass.getMethod( methodname, new Class[0] );
                if( m != null )
                {
                    m.invoke( plugin, new Object[0] );
                }
                else
                {
                    throw new ServiceException( "Plugin '" + pluginname + "' does not have the method: " + methodname );
                }
            }
            else
            {
                throw new ServiceException( "Plugin not found:" + pluginname );
            }
        }
    }
    
    protected Logger getLogger()
    {
        return m_Logger;
    }
    
    private void loadGlobalProperties( PluginProperties props )
    {
        props.put( "magic.home.dir", m_SystemDir.toString() );
        props.put( "magic.plugins.dir", m_PluginsDir.getAbsolutePath() );
        props.put( "magic.repository.dir", new File( m_SystemDir, "repository" ).toString() );
        props.put( "magic.project.dir", m_ProjectDir.getAbsolutePath() );
        props.put( "magic.temp.dir", m_TempDir.getAbsolutePath() );
        props.put( "user.home", System.getProperty( "user.home" ) );
        props.put( "java.home", System.getProperty( "java.home" ) );
        props.put( "java.version", System.getProperty( "java.version" ) );
    }
    
    private void loadMagicSystemProperties( PluginProperties props )
    {
        File file = new File( m_SystemDir, "build.properties" );
        if( file.exists() )
            load( props, file );
    }
    
    private void loadMagicPluginProperties( PluginProperties props )
    {
        File[] plugins = m_SystemDir.listFiles();
        for( int i=0 ; i < plugins.length ; i++ )
        {
            File file = new File( plugins[i], "build.properties" );
            if( file.exists() )
                load( props, file );
        }
    }
    
    private void loadProjectSystemProperties( PluginProperties props )
    {
        String projSys = props.getProperty( "project.system.dir" );
        if( projSys == null )
            return;
        File dir = new File( projSys );
        File file = new File( dir, "build.properties" );
        if( file.exists() )
            load( props, file );
    }
    
    private void loadProjectLocalProperties( PluginProperties props )
    {
        File file = new File( m_ProjectDir, "build.properties" );
        if( file.exists() )
            load( props, file );
    }
    
    private void loadUserProjectProperties( PluginProperties props )
    {
        File file = new File( m_ProjectDir, "user.properties" );
        if( file.exists() )
            load( props, file );
    }
    
    private void loadUserSystemProperties( PluginProperties props )
    {
        File file = new File( m_SystemDir, "user.properties" );
        if( file.exists() )
            load( props, file );
    }
    
    private void loadUserHomeProperties( PluginProperties props )
    {
        File dir = new File( System.getProperty( "user.dir" ) );
        File file = new File( dir, ".magic.properties" );
        if( file.exists() )
            load( props, file );
    }

    private void load( PluginProperties props, File file )
    {
        // TODO: Investigate if java.util.Properties already Buffer,
        // or we should do it for performance.
        FileInputStream in = null;
        try
        {
            in = new FileInputStream( file );
            props.load( in );
        } catch( IOException e )
        {
            getLogger().warn( "Properties file not found: " + file.getAbsolutePath() );
        } finally
        {
            try
            {
                if( in != null )
                    in.close();
            } catch( IOException e )
            {
                // Ignore... Don't think it can happen.
            }
        }
        
    }
        
    private File findSystemDir()
    {
        String system = System.getProperty( "magic.system.dir" );
        File systemDir = new File( system );
        return systemDir;
    }

    private Project initializeAntProject( PluginProperties props )
    {
        Project antProject = new Project();
        antProject.setBaseDir( m_ProjectDir );
        antProject.setCoreLoader( this.getClass().getClassLoader() );
        antProject.setName( props.getProperty( "project.name" ) );
        antProject.init();
        return antProject;
    }
        
} 
 
