package org.apache.merlin.magic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Map;
import java.util.HashMap;
import java.util.Properties;

import org.apache.avalon.framework.activity.Initializable;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.LogEnabled;

import org.apache.avalon.framework.context.Contextualizable;

import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;

public class PluginServiceManager extends AbstractLogEnabled
    implements ServiceManager
{
    private final Object DUMMY;
    
    private Map m_PluginsByKey;
    private Map m_PluginsByValue;
    
    private File m_SystemDir;
    private File m_ProjectDir;
    private File m_LocalPlugins;

    private Properties m_GlobalProperties;
    private FacadeFactory m_FacadeFactory;

    PluginServiceManager( FacadeFactory factory, Properties globalprops )
    {
        DUMMY = new Object();
        m_FacadeFactory = factory;
        
        m_PluginsByKey = new HashMap();
        m_PluginsByValue = new HashMap();
        m_SystemDir = new File( globalprops.getProperty( "magic.home.dir" ) );
        m_LocalPlugins = new File( globalprops.getProperty( "magic.plugins.dir" ) );;
        m_ProjectDir = new File( globalprops.getProperty( "magic.project.dir" ) );;
    }
        
    public Object lookup( String service )
        throws ServiceException
    {
        return lookupPlugin( service );
    }
    
    public Plugin lookupPlugin( String service )
        throws ServiceException
    {
        synchronized( m_PluginsByKey )
        {
            if( service.equals( "" ) )
                service = ".";
            Plugin plugin;
            Object obj = m_PluginsByKey.get( service );
            if( obj == null )
            {
                try
                {
                    PluginFacade facade = locate( service );
                    plugin = facade.resolve();
                    
                    addCyclicMarker( service );
                    
                    if( plugin instanceof LogEnabled )
                        ((LogEnabled) plugin).enableLogging( getLogger() );

                    PluginContext context = facade.getPluginContext();
                    if( plugin instanceof Contextualizable )
                        ((Contextualizable) plugin).contextualize( context );

                    if( plugin instanceof Serviceable )
                        ((Serviceable) plugin).service( this );

                    if( plugin instanceof Initializable )
                        ((Initializable) plugin).initialize();

                    addPlugin( service, plugin );

                } catch( Exception e )
                {
                    String message = "Unable to instantiate " + service;
                    getLogger().error( message, e );
                    throw new ServiceException( message  );
                }
            }
            else if( obj == DUMMY )
            {
                throw new ServiceException( "Cyclic Dependency detected:" + service );
            }
            else
            {
                plugin = (Plugin) obj;
            }
            return plugin;
        }
    }
    
    public void release( Object obj )
    {
        synchronized( m_PluginsByKey )
        {
            String key = (String) m_PluginsByValue.get( obj );
            m_PluginsByKey.remove( key );
            m_PluginsByValue.remove( obj );
        }
    }
    
    public boolean hasService( String service )
    {
        return m_PluginsByKey.containsKey( service );
    }
    
    private void addCyclicMarker( String service )
    {
        synchronized( m_PluginsByKey )
        {
            m_PluginsByKey.put( service, DUMMY );
        }
    }
    
    private void addPlugin( String service, Plugin plugin )
    {
        synchronized( m_PluginsByKey )
        {
            m_PluginsByKey.put( service, plugin );
            m_PluginsByValue.put( plugin, service );
        }
    }
    
    private PluginFacade locate( String service )
        throws ServiceException
    {
        File pluginDir;
        if( service.equals( "" ) || service.equals( "." ) )
            pluginDir = m_ProjectDir;
        else
            pluginDir = new File( m_LocalPlugins, service );
        if( pluginDir == null )
            throw new ServiceException( "Plugin '" + service + "' is not present in " + m_LocalPlugins + "." );
            
        Properties props = new Properties( m_GlobalProperties );
        
        appendProperties( props, pluginDir );
        appendProperties( props, m_ProjectDir );
        String projectName = props.getProperty( "project.name" );
        
        PluginContext ctx = new PluginContext( projectName, m_ProjectDir, 
            props, service, pluginDir, m_SystemDir );
        
        try
        {
            PluginFacade facade = m_FacadeFactory.create( ctx );
            return facade;
        } catch( CreationException e )
        {
            throw new ServiceException( "Plugin can not be created.", e );
        }
    }
    
    private void appendProperties( Properties props, File dir )
        throws ServiceException
    {
        File file = new File( dir, "build.properties" );
        if( ! file.exists() )
            throw new ServiceException( "File Missing: " + file.getAbsolutePath() );
        loadPropertiesFile( props, file );
        file = new File( dir, "user.properties" );
        if( file.exists() )
            loadPropertiesFile( props, file );
    }
    
    private void loadPropertiesFile( Properties props, File file )
    {
        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream( file );
            props.load( fis );
        } catch( IOException e )
        {
            getLogger().warn( "Unable to read " + file, e ) ;
        } finally
        {
            try
            {
                if( fis != null )
                    fis.close();
            } catch( IOException e )
            {} // Ignore.
        }
    }
} 
