package org.apache.merlin.magic;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Properties;

import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;

public class Builder
{
    private Logger   m_Logger;
        
    private String[] m_CallMethods;
    private File     m_SystemDir;
    private File     m_ProjectDir;
    private File     m_PluginsDir;
    
    public Builder( String[] methods, File projectDir )
    {
        
        m_CallMethods = new String[ methods.length ];
        for( int i=0 ; i < methods.length ; i++ )
            m_CallMethods[i] = methods[i];
        m_SystemDir = findSystemDir();
        m_ProjectDir = projectDir;
        m_PluginsDir = new File( m_SystemDir, "plugins" );
        m_Logger = new ConsoleLogger();
        
        m_Logger.info( " System Directory: " + m_SystemDir );
        m_Logger.info( "Project Directory: " + m_ProjectDir );
    }
    
    public void execute()
        throws Exception
    {
        Properties globalProps = loadGlobalProperties();
        
        FacadeFactory factory = new FacadeFactory();
        if( factory instanceof LogEnabled )
            ((LogEnabled) factory).enableLogging( m_Logger );
            
        PluginServiceManager sm = new PluginServiceManager( factory, globalProps );
        sm.enableLogging( m_Logger );
        
        loadAllPlugins( sm );
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
    
    private Properties loadGlobalProperties()
    {
        Properties props = new Properties();
        
        //TODO; Load the various properties;
        //      $GLOBAL/project.properties
        //      $GLOBAL/user.properties
        //  more?  Project and Plugin properties are loaded elsewhere.
        props.put( "magic.home.dir", m_SystemDir.toString() );
        props.put( "magic.plugins.dir", m_PluginsDir.getAbsolutePath() );
        props.put( "magic.repository.dir", new File( m_SystemDir, "repository" ).toString() );
        props.put( "magic.project.dir", m_ProjectDir.getAbsolutePath() );
        return props;
    }
    
    private File findSystemDir()
    {
        String classpath = System.getProperty( "java.class.path" );
        File f = new File( classpath );
        f = f.getParentFile();
        String dir = f.getParent();
        File system = new File( dir );
        return system.getAbsoluteFile();
    }
    
    private void loadAllPlugins( PluginServiceManager sm )
    {
        String[] plugins = m_PluginsDir.list();
        for( int i=0 ; i < plugins.length ; i++ )
        {
            try
            {
                sm.lookup( plugins[i] );
            } catch( ServiceException e )
            {
                m_Logger.error( "Unable to load plugin: " + plugins[i], e );
            }
        }
    }
    
} 
 
