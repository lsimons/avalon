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

package org.apache.avalon.logging.log4j.test;

import java.io.File;
import java.net.URL;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.avalon.framework.logger.Logger;

import org.apache.avalon.logging.provider.LoggingManager;

import org.apache.avalon.repository.Artifact;
import org.apache.avalon.repository.provider.Factory;
import org.apache.avalon.repository.provider.InitialContext;
import org.apache.avalon.repository.provider.Builder;
import org.apache.avalon.repository.main.DefaultInitialContextFactory;
import org.apache.avalon.repository.main.DefaultBuilder;

import org.apache.avalon.util.env.Env;
import org.apache.avalon.util.exception.ExceptionHelper;

/**
 * 
 * 
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @author $Author: niclas $
 * @version $Revision: 1.1 $
 */
public class LoggingManagerHelper
{
    //-------------------------------------------------------------------
    // utilities
    //-------------------------------------------------------------------

    /**
     * Setup the logging system. 
     * @param filename the name of a file in the test/conf directory
     * @param bootstrap the boostrap logger logging level
     * @return the logging manager
     */
    public static LoggingManager setUpLoggingManager( String filename ) throws Exception
    {
        DefaultInitialContextFactory initial = 
           new DefaultInitialContextFactory( "avalon", getBaseDir() );
        initial.setCacheDirectory( getMavenRepositoryDirectory() );
        InitialContext context = initial.createInitialContext();

        //
        // FIX ME - remove hard reference (get from a property)
        //

        Artifact artifact = Artifact.createArtifact( 
          "avalon-logging", "avalon-log4j-impl", "1.0-SNAPSHOT" );

        Builder builder = context.newBuilder( artifact );
        Factory factory = builder.getFactory();
        Map criteria = factory.createDefaultCriteria();

        //
        // customize the criteria
        //

        File basedir = getBaseDir();
        File target = new File( basedir, "target" );

        File conf = new File( basedir, "conf" );
        File file = new File( conf, filename );

        criteria.put( "avalon.logging.configuration", file );
        criteria.put( "avalon.logging.basedir", target );

        //
        // create the logging manager
        //

        return (LoggingManager) factory.create( criteria );

    }

    //-------------------------------------------------------------------
    // utilities
    //-------------------------------------------------------------------

    private static File getMavenRepositoryDirectory()
    {
        return new File( getMavenHomeDirectory(), "repository" );
    }

    private static File getMavenHomeDirectory()
    {
        return new File( getMavenHome() );
    }

    private static String getMavenHome()
    {
        try
        {
            String local = 
              System.getProperty( 
                "maven.home.local", 
                Env.getEnvVariable( "MAVEN_HOME_LOCAL" ) );
            if( null != local ) return local;

            return System.getProperty( "user.home" ) + File.separator + ".maven";

        }
        catch( Throwable e )
        {
            final String error = 
              "Internal error while attempting to access environment.";
            final String message = 
              ExceptionHelper.packException( error, e, true );
            throw new RuntimeException( message );
        }
    }

    protected static File getBaseDir()
    {
        return new File( System.getProperty( "basedir" ) );
    }

}
