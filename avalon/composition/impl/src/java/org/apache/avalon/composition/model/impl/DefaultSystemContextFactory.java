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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException ;
import java.lang.reflect.Method ;
import java.util.Hashtable;
import java.util.Map;

import org.apache.avalon.logging.provider.LoggingException;
import org.apache.avalon.logging.provider.LoggingFactory;
import org.apache.avalon.logging.provider.LoggingCriteria;
import org.apache.avalon.logging.provider.LoggingManager;
import org.apache.avalon.logging.data.CategoryDirective;

import org.apache.avalon.composition.data.SecurityProfile;
import org.apache.avalon.composition.data.TargetDirective;
import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.ModelRuntimeException;
import org.apache.avalon.composition.model.impl.DefaultModelFactory;
import org.apache.avalon.composition.provider.ModelFactory;
import org.apache.avalon.composition.provider.SystemContext;
import org.apache.avalon.composition.provider.SystemContextFactory;
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
 * @version $Revision: 1.5 $ $Date: 2004/03/07 00:00:59 $
 */
public class DefaultSystemContextFactory implements SystemContextFactory
{
    //--------------------------------------------------------------
    // static
    //--------------------------------------------------------------

    private static final Resources REZ =
      ResourceManager.getPackageResources( 
        DefaultSystemContextFactory.class );

    //--------------------------------------------------------------
    // immutable state
    //--------------------------------------------------------------

    private final InitialContext m_context;

    //--------------------------------------------------------------
    // mutable state
    //--------------------------------------------------------------

    private File m_home;

    private File m_temp;

    private Repository m_repository;

    private LoggingManager m_logging;

    private long m_timeout = 10000;

    private SecurityModel m_security;

    private boolean m_trace = false;

    private Class m_runtime;

    private Artifact m_artifact;

    private Artifact m_lifestyle;

    private String m_name = "system";

    private SecurityProfile[] m_profiles;

    private Map m_grants;

    private Context m_parent;

    private boolean m_secure = false;

    //--------------------------------------------------------------
    // constructor
    //--------------------------------------------------------------

   /**
    * Creation of a new system context factory.
    *
    * @param context a repository initial context
    */
    public DefaultSystemContextFactory( InitialContext context ) 
    {
        m_context = context;
    }

    //--------------------------------------------------------------
    // SystemContextFactory
    //--------------------------------------------------------------

    public void setSecurityEnabled( boolean secure )
    {
        m_secure = secure;
    }

    public void setParentContext( Context parent )
    {
        m_parent = parent;
    }

    public void setRuntime( Artifact artifact )
    {
        m_artifact = artifact;
    }

    public void setLifestyleArtifact( Artifact artifact )
    {
        m_lifestyle = artifact;
    }

    public void setRuntime( Class clazz )
    {
        m_runtime = clazz;
    }

    public void setRepository( Repository repository )
    {
        m_repository = repository;
    }

    public void setLoggingManager( LoggingManager logging )
    {
        m_logging = logging;
    }

    public void setDefaultDeploymentTimeout( long timeout )
    {
        m_timeout = timeout;
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

    public void setSecurityModel( SecurityModel security )
    {
        m_security = security;
    }

    public void setWorkingDirectory( File work )
    {
        m_home = work;
    }

    public void setTemporaryDirectory( File temp )
    {
        m_temp = temp;
    }

    public void setName( String name )
    {
        m_name = name;
    }

    public void setSecurityProfiles( SecurityProfile[] profiles )
    {
        m_profiles = profiles;
    }

   /**
    * Set the initial set of address to security profile grants.
    * @param grants the initial grants table
    */
    public void setGrantsTable( Map grants )
    {
        m_grants = grants;
    }

   /**
    * Creation of a new system context using supplied and default
    * values.
    * @return a new system context instance
    * @exception SystemException if a stytem context creation error occurs
    */
    public SystemContext createSystemContext()
      throws SystemException
    {
        return new DefaultSystemContext( 
          m_context, 
          getParentContext(), 
          getRuntimeArtifact(), 
          getLifestyleArtifact(), 
          getRuntimeClass(), 
          getLoggingManager(), 
          getBaseDirectory(), 
          getHomeDirectory(), 
          getTempDirectory(), 
          getRepository(), 
          getName(), 
          isTraceEnabled(), 
          getDefaultDeploymentTimeout(), 
          getSecurityEnabled(),
          getSecurityProfiles(), 
          getGrantsTable()
        );
    }

   /**
    * Return the security enabled status flag. If the value
    * return is TRUE then the composition model and runtime
    * will validate component initiated requires against 
    * named permission profiles.  If the value is FALSE the 
    * permission validation actions will not be performed.
    * The default value is FALSE.
    *
    * @return the security enabled flag
    */
    public boolean getSecurityEnabled()
    {
        return m_secure;
    }

   /**
    * Return the parent context to use when establishing a 
    * new SystemContext.
    *
    * @return the parent context
    */ 
    public Context getParentContext()
    {
        return m_parent;
    }

   /**
    * Return the name assigned to the system context.
    *
    * @return system context name
    */
    public String getName()
    {
        return m_name;
    }

   /**
    * Return the initial grants table.
    * 
    * @return the grants table
    */
    public Map getGrantsTable()
    {
        if( null == m_grants ) return new Hashtable();
        return m_grants;
    }

   /**
    * Return the assigned security profiles. If not profiles has been 
    * assigned the implementation returns an empty profile array.
    *
    * @return the security profiles
    */
    public SecurityProfile[] getSecurityProfiles()
    {
        if( null != m_profiles ) return m_profiles;
        return new SecurityProfile[0];
    }

   /**
    * Return the base directory from which relative classloader 
    * references may be resolved.
    *
    * @return the base directory
    */
    public File getBaseDirectory()
    {
        return m_context.getInitialWorkingDirectory();
    }

   /**
    * Return the working directory from which containers may 
    * establish persistent content.
    *
    * @return the working directory
    */
    public File getHomeDirectory()
    {
        if( null != m_home ) return m_home;
        return new File( getBaseDirectory(), "home" );
    }

   /**
    * Return the temporary directory from which a container 
    * may use to establish a transient content directory. 
    *
    * @return the temporary directory
    */
    public File getTempDirectory()
    {
        if( null != m_temp ) return m_temp;
        return new File( getBaseDirectory(), "temp" );
    }

   /**
    * Return the system wide repository from which resource 
    * directives can be resolved.
    *
    * @return the repository
    */
    public Repository getRepository()
    {
        if( null != m_repository ) return m_repository;
        return m_context.getRepository();
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
    * Return the logging manager.
    *
    * @return the logging manager.
    */
    public LoggingManager getLoggingManager()
    {
        if( null != m_logging ) return m_logging; 
        return createLoggingManager();
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
    * Return the class implementating the runtime.
    * @return the runtime class
    */
    public Class getRuntimeClass()
    {
        return m_runtime;
    }

   /**
    * Return the artifact referencing the runtime.
    * @return the runtime artifact
    */
    public Artifact getRuntimeArtifact()
    {
        return m_artifact;
    }

   /**
    * Return the artifact referencing the lifestyle factory.
    * @return the lifestyle factory artifact
    */
    public Artifact getLifestyleArtifact()
    {
        return m_lifestyle;
    }

   /**
    * Utility method to create the LoggingManager.
    * @param criteria the kernel creation criteria
    * @param config the kernel configuration 
    * @return the logging manager
    */
    private LoggingManager createLoggingManager()
    {
        Artifact[] artifacts = 
          m_context.getRepository().getCandidates( 
            LoggingManager.class );
        if( artifacts.length < 1 )
        {
            final String error =
              "No factory registered for the class [" 
              + LoggingManager.class.getName() + "].";
            throw new IllegalStateException( error );
        }

        Artifact artifact = artifacts[0];

        try
        {
            File dir = getBaseDirectory();
            ClassLoader classloader = 
              DefaultSystemContextFactory.class.getClassLoader();
            Builder builder = m_context.newBuilder( classloader, artifact );
            LoggingFactory factory = (LoggingFactory) builder.getFactory();
            LoggingCriteria params = factory.createDefaultLoggingCriteria();
            params.setDebugEnabled( isTraceEnabled() );
            params.setBaseDirectory( dir );
            return factory.createLoggingManager( params );
        }
        catch( Throwable e )
        {
            final String error = 
              "Cannot create logging manager due to a construction error.";
            throw new ModelRuntimeException( error, e );
        }
    }
}
