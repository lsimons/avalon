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
                    getLogger().debug( "Creating Script Facade: " + bshFile );
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
 
