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

package org.apache.avalon.composition.model.impl;

import java.io.File;
import java.net.URL;
import java.util.Map;

import org.apache.avalon.logging.provider.LoggingManager;
import org.apache.avalon.logging.data.CategoryDirective;

import org.apache.avalon.composition.model.ModelFactory;
import org.apache.avalon.composition.model.SystemContext;
import org.apache.avalon.composition.model.ContainmentModel;

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
 * @version $Revision: 1.12 $ $Date: 2004/01/25 13:18:48 $
 */
public class DefaultSystemContext extends DefaultContext 
  implements SystemContext
{
    //==============================================================
    // static
    //==============================================================

    private static final Resources REZ =
            ResourceManager.getPackageResources( DefaultSystemContext.class );

   /**
    * Convinience function to create a new system context. This function
    * is intended for test purposes only.
    *
    * @param base the base directory from which relative references 
    *   within a classpath or library directive shall be resolved
    * @param root a repository root directory
    * @param priority logging manager default priority
    * @return a system context
    */
    public static SystemContext createSystemContext( 
      InitialContext context, File base, File root,
      int priority, boolean secure ) 
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
          logging, base, home, temp, repository, "system", false, 1000, secure );
    }

    private static CacheManager createCacheManager( InitialContext context, File root ) 
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

    //==============================================================
    // immutable state
    //==============================================================

    private final File m_base;

    private final File m_home;

    private final File m_temp;

    private final Repository m_repository;

    private final ClassLoader m_system;

    private final ClassLoader m_common;

    private final LoggingManager m_logging;

    private final Logger m_logger;

    private ModelFactory m_factory;

    private final long m_timeout;

    private boolean m_secure;

    //==============================================================
    // mutable state
    //==============================================================

    private boolean m_trace;

    //==============================================================
    // constructor
    //==============================================================

   /**
    * Creation of a new system context.
    *
    * @param logging the logging manager
    * @param base the base directory from which relative references 
    *   within a classpath or library directive shall be resolved
    * @param home the home directory
    * @param temp the temp directory
    * @param repository the application repository to be used when resolving 
    *   resource directives
    * @param trace flag indicating if internal logging is enabled
    */
    public DefaultSystemContext( 
      LoggingManager logging, File base, File home, File temp, 
      Repository repository, String category, boolean trace, 
      long timeout, boolean secure )
    {
        if( base == null )
        {
            throw new NullPointerException( "base" );
        }
        if( repository == null )
        {
            throw new NullPointerException( "repository" );
        }
        if( logging == null )
        {
            throw new NullPointerException( "logger" );
        }
        if( !base.isDirectory() )
        {
            final String error = 
              REZ.getString( "system.context.base.not-a-directory.error", base  );
            throw new IllegalArgumentException( error );
        }

        m_base = base;
        m_home = home;
        m_temp = temp;
        m_trace = trace;
        m_repository = repository;
        m_logging = logging;
        m_timeout = timeout;
        m_secure = secure;

        m_logger = m_logging.getLoggerForCategory( category );
        m_system = SystemContext.class.getClassLoader();
        m_common = Logger.class.getClassLoader();
        m_factory = new DefaultModelFactory( this );
    }

    //==============================================================
    // SystemContext
    //==============================================================

   /**
    * Return the model factory.
    *
    * @return the factory
    */
    public ModelFactory getFactory()
    {
        return m_factory;
    }

   /**
    * Return the base directory from which relative classloader 
    * references may be resolved.
    *
    * @return the base directory
    */
    public File getBaseDirectory()
    {
        return m_base;
    }

   /**
    * Return the working directory from which containers may 
    * establish persistent content.
    *
    * @return the working directory
    */
    public File getHomeDirectory()
    {
        return m_home;
    }

   /**
    * Return the temporary directory from which a container 
    * may use to establish a transient content directory. 
    *
    * @return the temporary directory
    */
    public File getTempDirectory()
    {
        return m_temp;
    }

   /**
    * Return the system wide repository from which resource 
    * directives can be resolved.
    *
    * @return the repository
    */
    public Repository getRepository()
    {
        return m_repository;
    }

   /**
    * Return the system classloader. This classloader is equivalent to the
    * API classloader.
    *
    * @return the system classloader
    */
    public ClassLoader getCommonClassLoader()
    {
        return m_common;
    }

   /**
    * Return the system classloader.  This classloader is equivalent to the
    * SPI privileged classloader.
    *
    * @return the system classloader
    */
    public ClassLoader getSystemClassLoader()
    {
        return m_system;
    }

   /**
    * Return the system trace flag.
    *
    * @return the trace flag
    */
    public boolean isTraceEnabled()
    {
        return m_trace;
    }

   /**
    * Set the system trace flag.
    *
    * @param trace the trace flag
    */
    public void setTraceEnabled( boolean trace )
    {
        m_trace = trace;
    }

   /**
    * Return the logging manager.
    *
    * @return the logging manager.
    */
    public LoggingManager getLoggingManager()
    {
        return m_logging;
    }

   /**
    * Return the system logging channel.
    *
    * @return the system logging channel
    */
    public Logger getLogger()
    {
        return m_logger;
    }

   /**
    * Return the default deployment phase timeout value.
    * @return the timeout value
    */
    public long getDefaultDeploymentTimeout()
    {
        return m_timeout;
    }

   /**
    * Return the enabled status of the code security policy.
    * @return the code security enabled status
    */
    public boolean isCodeSecurityEnabled()
    {
        return m_secure;
    }
}
