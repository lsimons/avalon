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

import org.apache.avalon.composition.model.SystemContext;
import org.apache.avalon.composition.model.ContainmentContext;
import org.apache.avalon.composition.model.ClassLoaderModel;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.composition.data.ContainmentProfile;


/**
 * Implementation of a system context that exposes a system wide set of parameters.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2003/10/28 20:21:00 $
 */
public class DefaultContainmentContext extends DefaultContext 
  implements ContainmentContext
{
    //==============================================================
    // static
    //==============================================================

    private static final Resources REZ =
            ResourceManager.getPackageResources( DefaultContainmentContext.class );

    //==============================================================
    // immutable state
    //==============================================================

    private final File m_home;

    private final File m_temp;

    private final ClassLoaderModel m_model;

    private final Logger m_logger;

    private final ContainmentProfile m_profile;

    private final String m_partition;

    private final SystemContext m_system;

    private final String m_name;

    //==============================================================
    // constructor
    //==============================================================

   /**
    * Creation of a new containment context.
    *
    * @param logger the logging channel to assign
    * @param system the system context
    * @param model the classloader model
    * @param profile the containment profile
    */
    public DefaultContainmentContext( 
      Logger logger, SystemContext system, ClassLoaderModel model, 
      ContainmentProfile profile )
    {
        this( logger, system, model, 
          system.getHomeDirectory(), system.getTempDirectory(), 
          profile, null, "" );
    }

   /**
    * Creation of a new containment context.
    *
    * @param logger the logging channel to assign
    * @param system the system context
    * @param model the classloader model
    * @param home the directory for the container
    * @param temp a temporary directory for the container
    * @param profile the containment profile
    * @param partition the partition that this containment
    *    context is established within
    * @param name the assigned containment context name
    */
    public DefaultContainmentContext( 
      Logger logger, SystemContext system, ClassLoaderModel model, 
      File home, File temp, ContainmentProfile profile, String partition, String name )
    {
        if( logger == null )
        {
            throw new NullPointerException( "logger" );
        }
        if( system == null )
        {
            throw new NullPointerException( "system" );
        }
        if( model == null )
        {
            throw new NullPointerException( "model" );
        }
        if( home == null )
        {
            throw new NullPointerException( "home" );
        }
        if( temp == null )
        {
            throw new NullPointerException( "temp" );
        }
        if( profile == null )
        {
            throw new NullPointerException( "profile" );
        }

        if( home.exists() && !home.isDirectory() )
        {
            final String error = 
              REZ.getString( 
                "containment.context.home.not-a-directory.error", home.toString() );
            throw new IllegalArgumentException( error );
        }
        if( temp.exists() && !temp.isDirectory() )
        {
            final String error = 
              REZ.getString( 
                "containment.context.temp.not-a-directory.error", temp.toString() );
            throw new IllegalArgumentException( error );
        }

        m_logger = logger;
        m_system = system;
        m_model = model;
        m_home = home;
        m_temp = temp;
        m_profile = profile;
        m_partition = partition;
        m_name = name;
    }

    //==============================================================
    // ContainmentContext
    //==============================================================

   /**
    * Return the name that the container has been assigned.
    *
    * @return the container name
    */
    public String getName()
    {
        return m_name;
    }

   /**
    * Return the partition name that the container is 
    * established with.
    *
    * @return the partition name
    */
    public String getPartitionName()
    {
        return m_partition;
    }

   /**
    * Return the logging channel.
    *
    * @return the containment models logging channel
    */
    public Logger getLogger()
    {
        return m_logger;
    }

   /**
    * Return the system context.
    *
    * @return the system context
    */
    public SystemContext getSystemContext()
    {
        return m_system;
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
    * Return the containment profile.
    *
    * @return the containment profile
    */
    public ContainmentProfile getContainmentProfile()
    {
        return m_profile;
    }

   /**
    * Return the containment classloader model.
    *
    * @return the classloader model
    */
    public ClassLoaderModel getClassLoaderModel()
    {
        return m_model;
    }

   /**
    * Return the containment classloader.  This method is a 
    * convinience operation equivalent to getClassLoaderModel().getClassLoader(); 
    *
    * @return the classloader
    */
    public ClassLoader getClassLoader()
    {
        return m_model.getClassLoader();
    }
}
