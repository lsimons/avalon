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
import java.io.FileInputStream;
import java.io.IOException;

import java.lang.reflect.Method;

import java.util.Calendar;

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
        PluginProperties propset1 = new PluginProperties();
        PluginProperties propset2 = new PluginProperties();
        PluginProperties propset3 = new PluginProperties();
        
        // This is included twice, so a reference to other parts
        // can be obtained in the local project properties file.
        loadProjectLocalProperties( propset1 );
        
        loadGlobalProperties( propset1 );
        loadMagicSystemProperties( propset1 );
        
        String projSys = propset1.getProperty( "project.system" );
        loadProjectSystemProperties( propset2, projSys );
        loadProjectLocalProperties( propset2 );
        
        loadUserHomeProperties( propset3 );
        loadUserSystemProperties( propset3);
        loadUserProjectSystemProperties( propset3, projSys  );
        loadUserProjectProperties( propset3 );
        
        m_AntProject = initializeAntProject( propset1 );
        
        FacadeFactory factory = new FacadeFactory();
        if( factory instanceof LogEnabled )
            ((LogEnabled) factory).enableLogging( m_Logger );
            
        PluginServiceManager sm = new PluginServiceManager( factory, propset1, propset2, propset3, m_AntProject );
        sm.enableLogging( m_Logger );
        
        for( int i=0 ; i < m_CallMethods.length ; i++ )
        {
            String methodname = m_CallMethods[i];
            int pos = methodname.indexOf( "." );
            Plugin plugin;
            String pluginname = ".";
            if( pos <= 0 )
            {
                if( pluginname.startsWith( "@" ) )
                {
                    // indirect file
                    String filename = pluginname.substring(1);
                    File sequenceFile = new File( m_ProjectDir, filename );
                    Main.sequence( m_ProjectDir, sequenceFile );
                    continue;
                }
                else
                {
                    // project method
                    plugin = sm.lookupPlugin( "." );
                }
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
        props.setProperty( "magic.home.dir", m_SystemDir.toString() );
        props.setProperty( "magic.plugins.dir", m_PluginsDir.getAbsolutePath() );
        props.setProperty( "magic.repository.dir", new File( m_SystemDir, "repository" ).toString() );
        props.setProperty( "magic.project.dir", m_ProjectDir.getAbsolutePath() );
        props.setProperty( "magic.temp.dir", m_TempDir.getAbsolutePath() );
        props.setProperty( "user.home", System.getProperty( "user.home" ) );
        props.setProperty( "java.home", System.getProperty( "java.home" ) );
        props.setProperty( "java.version", System.getProperty( "java.version" ) );
        populateDateTimes( props );        
    }
    
    private void loadMagicSystemProperties( PluginProperties props )
    {
        File file = new File( m_SystemDir, "magic.properties" );
        if( file.exists() )
            load( props, file );
    }
    
    private void loadProjectSystemProperties( PluginProperties props, String projSys )
    {
        if( projSys == null )
            return;
        File dir = new File( projSys );
        File file = new File( dir, "magic.properties" );
        if( file.exists() )
            load( props, file );
    }
    
    private void loadProjectLocalProperties( PluginProperties props )
    {
        File file = new File( m_ProjectDir, "magic.properties" );
        if( file.exists() )
            load( props, file );
    }
    
    private void loadUserProjectProperties( PluginProperties props )
    {
        File file = new File( m_ProjectDir, "user-magic.properties" );
        if( file.exists() )
            load( props, file );
    }
    
    private void loadUserProjectSystemProperties( PluginProperties props, String projSys )
    {
        if( projSys == null )
            return;
        File dir = new File( projSys );
        File file = new File( dir, "user-magic.properties" );
        if( file.exists() )
        {
            load( props, file );
        }
    }
    
    private void loadUserSystemProperties( PluginProperties props )
    {
        File file = new File( m_SystemDir, "user-magic.properties" );
        if( file.exists() )
            load( props, file );
    }
    
    private void loadUserHomeProperties( PluginProperties props )
    {
        File dir = new File( System.getProperty( "user.home" ) );
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
        
    
    private void populateDateTimes( PluginProperties p )
    {
        Calendar cal = Calendar.getInstance();
        
        p.setProperty( "magic.year", "" + cal.get( Calendar.YEAR ) );
        p.setProperty( "magic.month", "" + cal.get( Calendar.MONTH ) );
        p.setProperty( "magic.date", "" + cal.get( Calendar.DATE ) );
        p.setProperty( "magic.hour", "" + cal.get( Calendar.HOUR_OF_DAY ) );
        p.setProperty( "magic.minute", "" + cal.get( Calendar.MINUTE ) );
        p.setProperty( "magic.second", "" + cal.get( Calendar.SECOND ) );
    }
} 
 
