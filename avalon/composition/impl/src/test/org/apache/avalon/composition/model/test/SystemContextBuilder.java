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

package org.apache.avalon.composition.model.test;

import java.io.File;
import java.net.URL;
import java.util.Map;

import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.impl.DefaultSystemContext;
import org.apache.avalon.composition.provider.ModelFactory;
import org.apache.avalon.composition.provider.SystemContext;

import org.apache.avalon.logging.provider.LoggingManager;
import org.apache.avalon.logging.data.CategoryDirective;

import org.apache.avalon.repository.Artifact;
import org.apache.avalon.repository.Repository;
import org.apache.avalon.repository.provider.CacheManager;
import org.apache.avalon.repository.provider.InitialContext;
import org.apache.avalon.repository.provider.Builder;
import org.apache.avalon.repository.provider.Factory;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.parameters.Parameters;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;

/**
 * Implementation of a system context that exposes a system wide set of parameters.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2004/02/10 16:24:48 $
 */
public class SystemContextBuilder
{
    //--------------------------------------------------------------
    // static
    //--------------------------------------------------------------

   /**
    * Convinience function to create a new system context. This function
    * is intended for test purposes only.
    *
    * @param context the intial repository context
    * @param base the base directory from which relative references 
    *   within a classpath or library directive shall be resolved
    * @param root a repository root directory
    * @param priority logging manager default priority
    * @return a system context
    */
    public static SystemContext createSystemContext( 
      InitialContext context, File base, File root,
      int priority, boolean secure, long timeout ) 
      throws Exception
    {
        //
        // ### FIX ME ##
        //
 
        Artifact artifact = Artifact.createArtifact( 
          "avalon-logging", "avalon-logkit-impl", "1.0-SNAPSHOT" );

        LoggingManager logging = 
          createLoggingManager( context, artifact, base, priority );

        Logger logger = logging.getLoggerForCategory( "" );
        CacheManager cache = createCacheManager( context, root );
        Repository repository = cache.createRepository();

        final File home = new File( base, "home" );
        final File temp = new File( base, "temp" );
 
        return new DefaultSystemContext( 
          context, null, logging, base, home, temp, repository, "system", 
          false, timeout, secure );
    }

    private static CacheManager createCacheManager( 
      InitialContext context, File root ) 
      throws Exception
    {
        String dpml = "http://dpml.net";
        String ibiblio = "http://www.ibiblio.org/maven";
        String[] hosts = new String[]{ dpml, ibiblio };

        Factory factory = context.getInitialFactory();
        Map criteria = factory.createDefaultCriteria();
        criteria.put( "avalon.repository.cache", root );
        criteria.put( "avalon.repository.hosts", hosts );

        return (CacheManager) factory.create( criteria );
    }

    private static LoggingManager createLoggingManager( 
      InitialContext context, Artifact artifact, File base, int priority ) 
      throws Exception
    {
        final String level = getStringPriority( priority );
        Builder builder = context.newBuilder( artifact );
        Factory factory = builder.getFactory();
        Map criteria = factory.createDefaultCriteria();
        File file = new File( base, "conf/logging.xml" );
        criteria.put( "avalon.logging.configuration", file );
        criteria.put( "avalon.logging.basedir", base );
        return (LoggingManager) factory.create( criteria );
    }

    private static String getStringPriority( int priority )
    {
        if( priority == ConsoleLogger.LEVEL_DISABLED )
        {
            return "NONE";
        }
        else if( priority == ConsoleLogger.LEVEL_DEBUG )
        {
            return "DEBUG";
        }
        else if( priority == ConsoleLogger.LEVEL_INFO )
        {
            return "INFO";
        }
        else if( priority == ConsoleLogger.LEVEL_WARN )
        {
            return "WARN";
        }
        else if( priority == ConsoleLogger.LEVEL_ERROR )
        {
            return "ERROR";
        }
        else if( priority == ConsoleLogger.LEVEL_FATAL )
        {
            return "FATAL";
        }
        else
        {
            final String error = 
             "Unrecognized logging priority: " + priority;
            throw new IllegalArgumentException( error );
        }
    }
}
