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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException ;
import java.lang.reflect.Method ;

import org.apache.avalon.logging.provider.LoggingManager;
import org.apache.avalon.logging.data.CategoryDirective;

import org.apache.avalon.composition.data.SecurityProfile;
import org.apache.avalon.composition.data.TargetDirective;
import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.impl.DefaultModelFactory;
import org.apache.avalon.composition.provider.ModelFactory;
import org.apache.avalon.composition.provider.SystemContext;
import org.apache.avalon.composition.provider.SystemException;
import org.apache.avalon.composition.provider.SecurityModel;
import org.apache.avalon.composition.provider.Runtime;

import org.apache.avalon.repository.Artifact;
import org.apache.avalon.repository.Repository;
import org.apache.avalon.repository.provider.InitialContext;
import org.apache.avalon.repository.provider.Builder;
import org.apache.avalon.repository.provider.Factory;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.parameters.Parameters;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;

/**
 * Implementation of a system context that exposes a system wide set of parameters.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.23 $ $Date: 2004/02/29 22:25:26 $
 */
public class DefaultSystemContext extends DefaultContext 
  implements SystemContext
{
    //--------------------------------------------------------------
    // static
    //--------------------------------------------------------------

    private static final Resources REZ =
            ResourceManager.getPackageResources( DefaultSystemContext.class );

    //--------------------------------------------------------------
    // immutable state
    //--------------------------------------------------------------

    private final File m_base;

    private final File m_home;

    private final File m_temp;

    private final Repository m_repository;

    private final ClassLoader m_system;

    private final ClassLoader m_common;

    private final LoggingManager m_logging;

    private final Logger m_logger;

    private ModelFactory m_factory;

    private Runtime m_runtime;

    private final long m_timeout;

    private final boolean m_secure;

    //--------------------------------------------------------------
    // mutable state
    //--------------------------------------------------------------

    private boolean m_trace;

    //--------------------------------------------------------------
    // constructor
    //--------------------------------------------------------------

   /**
    * Creation of a new system context.
    *
    * @param context a repository initial context
    * @param artifact an artifact identifying the default runtime
    * @param logging the logging manager
    * @param base the base directory from which relative references 
    *   within a classpath or library directive shall be resolved
    * @param home the home directory
    * @param temp the temp directory
    * @param repository the application repository to be used when resolving 
    *   resource directives
    * @param category the kernel logging category name
    * @param trace flag indicating if internal logging is enabled
    * @param timeout a system wide default deployment timeout
    * @param security the security profiles
    */
    /*
    public DefaultSystemContext( 
      InitialContext context,
      Artifact artifact, 
      LoggingManager logging, 
      File base, 
      File home, 
      File temp, 
      Repository repository, 
      String category, 
      boolean trace, 
      long timeout, 
      SecurityProfile[] security,
      TargetDirective[] targets ) throws SystemException
    {
        this( 
          context, artifact, null, logging, base, home, temp, 
          repository, category, trace, timeout, security,
          targets );
    }
    */

   /**
    * Creation of a new system context.
    *
    * @param clazz the runtime class
    * @param logging the logging manager
    * @param base the base directory from which relative references 
    *   within a classpath or library directive shall be resolved
    * @param home the home directory
    * @param temp the temp directory
    * @param repository the application repository to be used when resolving 
    *   resource directives
    * @param category the kernel logging category name
    * @param trace flag indicating if internal logging is enabled
    * @param timeout a system wide default deployment timeout
    * @param security the security profiles
    */
    /*
    public DefaultSystemContext( 
      Class clazz, 
      LoggingManager logging, 
      File base, 
      File home, 
      File temp, 
      Repository repository, 
      String category, 
      boolean trace, 
      long timeout, 
      SecurityProfile[] security,
      TargetDirective[] targets ) throws SystemException
    {
        this( 
          null, null, clazz, logging, base, home, temp, repository, category, 
          trace, timeout, security, targets );
    }
    */

   /**
    * Creation of a new system context.
    *
    * @param context the repository intial context
    * @param artifact the runtime artifact
    * @param runtime the runtime class
    * @param logging the logging manager
    * @param base the base directory from which relative references 
    *   within a classpath or library directive shall be resolved
    * @param home the home directory
    * @param temp the temp directory
    * @param repository the application repository to be used when resolving 
    *   resource directives
    * @param category the kernel logging category name
    * @param trace flag indicating if internal logging is enabled
    * @param timeout a system wide default deployment timeout
    * @param security the security profiles
    */
    DefaultSystemContext( 
      InitialContext context, 
      Context parent, 
      Artifact artifact, 
      Class runtime, 
      LoggingManager logging, 
      File base, 
      File home, 
      File temp, 
      Repository repository, 
      String category, 
      boolean trace, 
      long timeout, 
      SecurityProfile[] security,
      TargetDirective[] targets ) throws SystemException
    {
        super( parent );
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
            throw new NullPointerException( "logging" );
        }
        if( !base.isDirectory() )
        {
            final String error = 
              REZ.getString( "system.error.base-not-a-directory", base  );
            throw new IllegalArgumentException( error );
        }

        m_base = base;
        m_home = home;
        m_temp = temp;
        m_trace = trace;
        m_repository = repository;
        m_logging = logging;
        m_timeout = timeout;

        m_logger = m_logging.getLoggerForCategory( category );
        m_system = SystemContext.class.getClassLoader();
        m_common = DeploymentModel.class.getClassLoader();

        if( security.length > 0 )
        {
            m_secure = true;
            if( System.getSecurityManager() == null )
            {
                System.setSecurityManager( new SecurityManager() );
            }
        }
        else
        {
            m_secure = false;
        }

        m_factory = new DefaultModelFactory( this, security, targets );

        //
        // use avalon-repository to load the runtime
        //

        if( null != runtime )
        {
            m_runtime = buildRuntimeInstance( context, runtime );
        }
        else if( null != artifact )
        {
            Class clazz = resolveRuntimeClass( context, artifact );
            m_runtime = buildRuntimeInstance( context, clazz );
        }
        else
        {
            m_runtime = null;
        }
    }

    //--------------------------------------------------------------
    // SystemContext
    //--------------------------------------------------------------

   /**
    * Return the enabled status of the code security policy.
    * @return the code security enabled status
    */
    public boolean isCodeSecurityEnabled()
    {
        return m_secure;
    }

   /**
    * Return the model factory.
    *
    * @return the factory
    */
    public ModelFactory getModelFactory()
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
    * Return the API classloader.
    *
    * @return the system classloader
    */
    public ClassLoader getAPIClassLoader()
    {
        return m_common;
    }

   /**
    * Return the SPI classloader. 
    *
    * @return the system classloader
    */
    public ClassLoader getSPIClassLoader()
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

    //------------------------------------------------------------------
    // runtime operations
    //------------------------------------------------------------------

   /**
    * Request the commissioning of a runtime for a supplied deployment 
    * model.
    * @param model the deployment model 
    * @exception Exception of a commissioning error occurs
    */
    public void commission( DeploymentModel model ) throws Exception
    {
        getRuntime().commission( model );
    }

   /**
    * Request the decommissioning of a runtime for a supplied deployment 
    * model.
    * @param model the deployment model 
    * @exception Exception of a commissioning error occurs
    */
    public void decommission( DeploymentModel model )
    {
        getRuntime().decommission( model );
    }

   /**
    * Request resolution of an object from the runtime.
    * @param model the deployment model
    * @exception Exception if a deployment error occurs
    */
    public Object resolve( DeploymentModel model ) throws Exception
    {
        return getRuntime().resolve( model );
    }

   /**
    * Request the release of an object from the runtime.
    * @param model the deployment model
    * @param instance the object to release
    * @exception Exception if a deployment error occurs
    */
    public void release( DeploymentModel model, Object instance )
    {
        getRuntime().release( model, instance );
    }

    //------------------------------------------------------------------
    // runtime operations
    //------------------------------------------------------------------

   /**
    * Return the runtime factory.
    *
    * @return the factory
    */
    private Runtime getRuntime()
    {
        if( null == m_runtime ) 
        {
            throw new IllegalStateException( "runtime" );
        }
        else
        {
            return m_runtime;
        }
    }

    private Class resolveRuntimeClass( InitialContext context, Artifact artifact )
      throws SystemException
    {
        if( null != artifact )
        {
            return getRuntimeClass( context, artifact );
        }
        else
        {
            return getRuntimeClass( 
              context, getDefaultRuntimeArtifact( context ) );
        }
    }

   /**
    * Get the runtime class referenced by the artifact.
    * @param context the repository initial context
    * @param artifact the factory artifact
    * @return the Runtime class
    */
    private Class getRuntimeClass( InitialContext context, Artifact artifact )
      throws SystemException
    {
        if( null == artifact )
        {
            throw new NullPointerException( "artifact" );
        }

        try
        {
            ClassLoader classloader = 
              DefaultSystemContext.class.getClassLoader();
            Builder builder = 
              context.newBuilder( classloader, artifact );
            return builder.getFactoryClass();
        }
        catch( Throwable e )
        {
            final String error = 
              REZ.getString( "system.error.load", artifact.toString() );
            throw new SystemException( error, e );
        }
    }

    private Artifact getDefaultRuntimeArtifact( InitialContext context )
    {
        Artifact[] artifacts = 
          context.getRegistry().getCandidates( Runtime.class );
        if( artifacts.length > 0 ) return artifacts[0];

        final String error =
          "Initial context does not declare an artifact for the class: ["
          + Runtime.class.getName() + "].";
        throw new IllegalStateException( error );
    }

   /**
    * Build a runtime using a supplied class.
    *
    * @param clazz the log target factory class
    * @return a instance of the class
    * @exception SystemException if the class does not expose a public 
    *    constructor, or the constructor requires arguments that the 
    *    builder cannot resolve, or if a unexpected instantiation error 
    *    ooccurs
    */ 
    public Runtime buildRuntimeInstance( InitialContext context, Class clazz ) 
      throws SystemException
    {
        if( null == clazz ) return null;

        Constructor[] constructors = clazz.getConstructors();
        if( constructors.length < 1 ) 
        {
            final String error = 
              REZ.getString( 
                "system.error.runtime.no-constructor", 
                clazz.getName() );
            throw new SystemException( error );
        }

        //
        // log target factories only have one constructor
        //

        Constructor constructor = constructors[0];
        Class[] classes = constructor.getParameterTypes();
        Object[] args = new Object[ classes.length ];
        for( int i=0; i<classes.length; i++ )
        {
            Class c = classes[i];
            if( SystemContext.class.isAssignableFrom( c ) )
            {
                args[i] = this;
            }
            else if( InitialContext.class.isAssignableFrom( c ) )
            {
                args[i] = context;
            }
            else
            {
                final String error = 
                  REZ.getString( 
                    "system.error.unrecognized-runtime-parameter", 
                    c.getName(),
                    clazz.getName() );
                throw new SystemException( error );
            }
        }

        //
        // instantiate the factory
        //

        return instantiateRuntime( constructor, args );
    }

   /**
    * Instantiation of a runtime using a supplied constructor 
    * and arguments.
    * 
    * @param constructor the runtime constructor
    * @param args the constructor arguments
    * @return the runtime instance
    * @exception SystemException if an instantiation error occurs
    */
    private Runtime instantiateRuntime( 
      Constructor constructor, Object[] args ) 
      throws SystemException
    {
        Class clazz = constructor.getDeclaringClass();
        try
        {
            return (Runtime) constructor.newInstance( args );
        }
        catch( Throwable e )
        {
            final String error = 
              REZ.getString( 
                "system.error.runtime-instantiation", 
                clazz.getName() );
            throw new SystemException( error, e );
        }
    }
}
