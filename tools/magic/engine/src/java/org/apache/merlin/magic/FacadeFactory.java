package org.apache.merlin.magic;

import java.io.File;
import java.io.IOException;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.LogEnabled;

public class FacadeFactory extends AbstractLogEnabled
{
    FacadeFactory()
    {
    }
    
    public PluginFacade create( PluginContext context )
        throws CreationException
    {
        File pluginDir = context.getPluginDir();
        File bshFile = new File( pluginDir, "build.bsh" );
        if( bshFile.exists() )
        {
            try
            {
                if( getLogger().isDebugEnabled() )
                    getLogger().debug( "Creating Script Facade: " + context.getProjectName() );
                ScriptFacade facade = new ScriptFacade( context );
                if( facade instanceof LogEnabled )
                {
                    ((LogEnabled) facade).enableLogging( getLogger() );
                }
                return facade;
            } catch( IOException e )
            {
                throw new CreationException( "Can't read the script for: " + pluginDir, e );
            }
        }
        throw new CreationException( "Unknown type of plugin: " + pluginDir );
    }
} 
 
