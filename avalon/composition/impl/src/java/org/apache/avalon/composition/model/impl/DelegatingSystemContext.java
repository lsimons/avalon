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

import org.apache.avalon.logging.provider.LoggingManager;
import org.apache.avalon.composition.model.ModelFactory;
import org.apache.avalon.composition.model.RuntimeFactory;
import org.apache.avalon.composition.model.SystemContext;
import org.apache.avalon.repository.Repository;
import org.apache.avalon.framework.logger.Logger;

import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.parameters.Parameters;

import org.apache.avalon.logging.data.CategoryDirective;


/**
 * Implementation of a system context the delegates operations to a 
 * a parent system context.  This implementation enables the seperation
 * of a system context assigned to an application as distinct from a 
 * system context supplied to internal facilities within which 
 * supplimentary context entries can be added.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.4 $ $Date: 2004/02/07 14:03:42 $
 */
public class DelegatingSystemContext extends DefaultContext 
  implements SystemContext
{
    //--------------------------------------------------------------
    // immutable state
    //--------------------------------------------------------------

    private final SystemContext m_parent;

    //--------------------------------------------------------------
    // constructor
    //--------------------------------------------------------------

   /**
    * Creation of a new delegating system context.
    *
    * @param parent the parent system context
    */
    public DelegatingSystemContext( SystemContext parent )
    {
        super( parent );
        if( parent == null )
        {
            throw new NullPointerException( "parent" );
        }
        m_parent = parent;
    }

    //--------------------------------------------------------------
    // SystemContext
    //--------------------------------------------------------------

   /**
    * Return the model factory.
    *
    * @return the factory
    */
    public ModelFactory getModelFactory()
    {
        return m_parent.getModelFactory();
    }

   /**
    * Return the runtime factory.
    *
    * @return the factory
    */
    public RuntimeFactory getRuntimeFactory()
    {
        return m_parent.getRuntimeFactory();
    }


   /**
    * Return the base directory from which relative classloader 
    * references may be resolved.
    *
    * @return the base directory
    */
    public File getBaseDirectory()
    {
        return m_parent.getBaseDirectory();
    }

   /**
    * Return the working directory from which containers may 
    * establish persistent content.
    *
    * @return the working directory
    */
    public File getHomeDirectory()
    {
        return m_parent.getHomeDirectory();
    }

   /**
    * Return the temporary directory from which a container 
    * may use to establish a transient content directory. 
    *
    * @return the temporary directory
    */
    public File getTempDirectory()
    {
        return m_parent.getTempDirectory();
    }

   /**
    * Return the system wide repository from which resource 
    * directives can be resolved.
    *
    * @return the repository
    */
    public Repository getRepository()
    {
        return m_parent.getRepository();
    }

   /**
    * Return the system classloader. This classloader is equivalent to the
    * API classloader.
    *
    * @return the system classloader
    */
    public ClassLoader getCommonClassLoader()
    {
        return m_parent.getCommonClassLoader();
    }

   /**
    * Return the system classloader.  This classloader is equivalent to the
    * SPI privileged classloader.
    *
    * @return the system classloader
    */
    public ClassLoader getSystemClassLoader()
    {
        return m_parent.getSystemClassLoader();
    }

   /**
    * Return the system trace flag.
    *
    * @return the trace flag
    */
    public boolean isTraceEnabled()
    {
        return m_parent.isTraceEnabled();
    }

   /**
    * Return the logging manager.
    *
    * @return the logging manager.
    */
    public LoggingManager getLoggingManager()
    {
        return m_parent.getLoggingManager();
    }

   /**
    * Return the system logging channel.
    *
    * @return the system logging channel
    */
    public Logger getLogger()
    {
        return m_parent.getLogger();
    }

   /**
    * Return the default deployment phase timeout value.
    * @return the timeout value
    */
    public long getDefaultDeploymentTimeout()
    {
        return m_parent.getDefaultDeploymentTimeout();
    }

   /**
    * Return the enabled status of the code security policy.
    * @return the code security enabled status
    */
    public boolean isCodeSecurityEnabled()
    {
        return m_parent.isCodeSecurityEnabled();
    }
}
