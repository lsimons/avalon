/* 
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.metro.logging.log4j.test;

import java.io.File;
import java.util.Map;

import org.apache.metro.logging.provider.LoggingManager;
import org.apache.metro.transit.Artifact;
import org.apache.metro.transit.Factory;
import org.apache.metro.transit.Repository;
import org.apache.metro.transit.StandardLoader;
import org.apache.metro.transit.InitialContext;
import org.apache.metro.transit.InitialContextFactory;

/**
 * 
 * 
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: LoggingManagerHelper.java 30977 2004-07-30 08:57:54Z niclas $
 */
public class LoggingManagerHelper
{
    //-------------------------------------------------------------------
    // utilities
    //-------------------------------------------------------------------

    /**
     * Setup the initial context. 
     * @param filename the name of a file in the test/conf directory
     * @param bootstrap the boostrap logger logging level
     * @return the logging manager
     */
    public static LoggingManager setUpLoggingManager( String filename ) throws Exception
    {
        File base = new File( System.getProperty( "project.dir" ) );
        File cache = new File( System.getProperty( "project.repository.cache.path" ) );
        
        InitialContextFactory icf = new InitialContextFactory();
        icf.setCacheDirectory( cache );
        InitialContext context = icf.createInitialContext();
        Repository repository = new StandardLoader( context );
        Artifact artifact = Artifact.createArtifact( "@LOGGING_PLUGIN_SPEC@" );

        //
        // create the logging manager
        //

        ClassLoader loader = LoggingManagerHelper.class.getClassLoader();
        Object[] args = new Object[0];
        Factory factory = (Factory) repository.getPlugin( loader, artifact, args );
        Map criteria = factory.createDefaultCriteria();

        //
        // customize the criteria
        //

        File basedir = new File( System.getProperty( "project.dir" ) );
        File config = new File( basedir, filename );
        criteria.put( "metro.logging.configuration", config );
        criteria.put( "metro.logging.basedir", basedir );

        return (LoggingManager) factory.create( criteria );
    }

    //-------------------------------------------------------------------
    // utilities
    //-------------------------------------------------------------------

    private static File getBaseDir()
    {
        String path = System.getProperty( "project.dir" );
        if( null != path )
        {
            return new File( path );
        }
        else
        {
            final String error = 
              "Required property 'project.dir' referencing the runtime target/test directory is undefined.";
            throw new IllegalStateException( error );
        }
    }
}
