/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.avalon.composition.model.impl;

import java.io.File;
import java.net.URL;

import org.apache.avalon.composition.logging.LoggingManager;
import org.apache.avalon.composition.logging.LoggingDescriptor;
import org.apache.avalon.composition.logging.TargetDescriptor;
import org.apache.avalon.composition.logging.impl.DefaultLoggingManager;
import org.apache.avalon.composition.model.ModelFactory;
import org.apache.avalon.composition.model.SystemContext;
import org.apache.avalon.repository.Repository;
import org.apache.avalon.repository.provider.CacheManager;
import org.apache.avalon.repository.impl.DefaultCacheManager;
import org.apache.avalon.repository.impl.DefaultRepository;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;

import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.parameters.Parameters;

import org.apache.avalon.composition.data.CategoryDirective;


/**
 * Implementation of a system context that exposes a system wide set of parameters.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.6 $ $Date: 2004/01/13 11:41:26 $
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
    * Creation of a new system context.
    *
    * @param base the base directory from which relative references 
    *   within a classpath or library directive shall be resolved
    * @param root a repository root directory
    * @param priority logging manager default priority
    * @return a system context
    */
    public static SystemContext createSystemContext( 
      File base, File root, int priority ) throws Exception
    {
        LoggingManager logging = createLoggingManager( base, priority );
        Logger logger = logging.getLoggerForCategory( "" );
        Repository repository = createRepository( root );
        final File working = new File( System.getProperty( "user.dir" ), "working" );
        final File home = new File( working, "home" );
        final File temp = new File( working, "temp" );

        return new DefaultSystemContext( 
          logging, base, home, temp, repository, "system", false, null );
    }

    private static Repository createRepository( File root ) throws Exception
    {
        String dpml = "http://dpml.net";
        String ibiblio = "http://www.ibiblio.org/maven";
        CacheManager manager = new DefaultCacheManager( root, null, new String[]{ dpml, ibiblio } );
        return manager.createRepository();
    }

    private static LoggingManager createLoggingManager( File base, int priority ) throws Exception
    {
        final String level = getStringPriority( priority );
        LoggingDescriptor logging =
                new LoggingDescriptor(
                        "", level, null,
                        new CategoryDirective[0],
                        new TargetDescriptor[0] );

        DefaultLoggingManager manager = 
          new DefaultLoggingManager( base, logging );
        return manager;
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

    private final Parameters m_parameters;

    private ModelFactory m_factory;

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
    * @param repository a resource repository to be used when resolving 
    *   resource directives
    * @param trace flag indicating if internal logging is enabled
    */
    public DefaultSystemContext( 
      LoggingManager logging, File base, File home, File temp, 
      Repository repository, String category, boolean trace, Parameters params )
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

        if( params == null )
        {
            m_parameters = new Parameters();
            m_parameters.makeReadOnly();
        }
        else
        {
            m_parameters = params;
        }

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
    * Returns the configurable kernel parameters.
    *
    * @return a Parameters object populated with the system
    * parameters.
    */
    public Parameters getSystemParameters()
    {
        return m_parameters;
    }

}
