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
import java.util.Map;
import java.util.Hashtable;

import org.apache.avalon.composition.model.SystemContext;
import org.apache.avalon.composition.model.EntryModel;
import org.apache.avalon.composition.model.ContainmentContext;
import org.apache.avalon.composition.model.ComponentContext;
import org.apache.avalon.composition.model.ModelRuntimeException;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.composition.data.DeploymentProfile;
import org.apache.avalon.meta.info.Type;
import org.apache.avalon.meta.info.EntryDescriptor;


/**
 * Default implementation of a deployment context.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1.2.1 $ $Date: 2004/01/04 17:23:16 $
 */
public class DefaultComponentContext extends DefaultContext 
  implements ComponentContext
{
    //==============================================================
    // static
    //==============================================================

    private static final Resources REZ =
            ResourceManager.getPackageResources( DefaultComponentContext.class );

    //==============================================================
    // immutable state
    //==============================================================

    private final String m_name;

    private final ContainmentContext m_context;

    private final DeploymentProfile m_profile;

    private final Type m_type;

    private final Class m_class;

    private final File m_home;

    private final File m_temp;

    private final Logger m_logger;

    private final String m_partition;

   /**
    * Map containing context entry models 
    * keyed by entry key.
    */
    private final Map m_map = new Hashtable();

    //==============================================================
    // constructor
    //==============================================================

   /**
    * Creation of a new deployment context.
    *
    * @param logger the logging channel to assign
    * @param name the deployment context name
    * @param context the containment context in which this 
    *     deployment context is scoped
    * @param profile the deployment profile
    * @param type the underlying component type
    * @param clazz the compoent deployment class
    * @param home the home working directory
    * @param temp a temporary directory 
    * @param partition the partition name 
    */
    public DefaultComponentContext( 
      Logger logger, String name, ContainmentContext context, 
      DeploymentProfile profile, Type type, Class clazz, 
      File home, File temp, String partition )
    {
        if( context == null )
        {
            throw new NullPointerException( "context" );
        }
        if( logger == null )
        {
            throw new NullPointerException( "logger" );
        }
        if( clazz == null )
        {
            throw new NullPointerException( "clazz" );
        }
        if( type == null )
        {
            throw new NullPointerException( "type" );
        }
        if( profile == null )
        {
            throw new NullPointerException( "profile" );
        }
        if( partition == null )
        {
            throw new NullPointerException( "partition" );
        }

        if( home.exists() && !home.isDirectory() )
        {
            final String error = 
              REZ.getString( "deployment.context.home.not-a-directory.error", home  );
            throw new IllegalArgumentException( error );
        }
        if( temp.exists() && !temp.isDirectory() )
        {
            final String error = 
              REZ.getString( "deployment.context.temp.not-a-directory.error", temp  );
            throw new IllegalArgumentException( error );
        }

        m_name = name;
        m_home = home;
        m_temp = temp;
        m_context = context;
        m_type = type;
        m_logger = logger;
        m_profile = profile;
        m_partition = partition;
        m_class = clazz;

    }

    //==============================================================
    // ContainmentContext
    //==============================================================

   /**
    * Return the partition name that the component will execute within.
    *
    * @return the partition name
    */
    public String getPartitionName()
    {
        return m_partition;
    }

   /**
    * Return the name that the component will execute under.
    *
    * @return the name
    */
    public String getName()
    {
        return m_name;
    }

   /**
    * Return the system context.
    *
    * @return the system context
    */
    public SystemContext getSystemContext()
    {
        return m_context.getSystemContext();
    }

   /**
    * Return the containment context.
    *
    * @return the containment context
    */
    public ContainmentContext getContainmentContext()
    {
        return m_context;
    }

   /**
    * Return the working directory.
    *
    * @return the working directory
    */
    public File getHomeDirectory()
    {
        return m_home;
    }

   /**
    * Return the temporary directory.
    *
    * @return the temporary directory
    */
    public File getTempDirectory()
    {
        return m_temp;
    }

   /**
    * Return the logging channel.
    *
    * @return the logging channel
    */
    public Logger getLogger()
    {
        return m_logger;
    }

   /**
    * Return the deployment profile.
    *
    * @return the profile
    */
    public DeploymentProfile getProfile()
    {
        return m_profile;
    }

   /**
    * Return the component type.
    *
    * @return the type defintion
    */
    public Type getType()
    {
        return m_type;
    }

   /**
    * Return the component class.
    *
    * @return the class
    */
    public Class getDeploymentClass()
    {
        return m_class;
    }

   /**
    * Return the classloader for the component.
    *
    * @return the classloader
    */
    public ClassLoader getClassLoader()
    {
        return m_context.getClassLoader();
    }

   /**
    * Add a context entry model to the deployment context.
    * @param model the entry model
    * @exception IllegalArgumentException if model key is unknown
    */
    public void register( EntryModel model )
    {
        final String key = model.getKey();
        if( m_map.get( key ) == null )
        {
            m_map.put( key, model );
        }
        else
        {
            final String error = 
              REZ.getString( "deployment.registration.override.error", key );
            throw new IllegalArgumentException( error );
        }
    }

   /**
    * Get a context entry from the deployment context.
    * @param alias the entry lookup key
    * @return value the corresponding value
    * @exception ContextException if the key is unknown
    * @exception ModelRuntimeException if the key is unknown
    */
    public Object resolve( final String alias ) throws ContextException
    {
        if( alias == null ) throw new NullPointerException( "alias" );

        String key = alias;
        EntryDescriptor entry = 
          getType().getContext().getEntry( alias );

        if( entry != null )
        {
            key = entry.getKey();
        }
        
        if( key.startsWith( "urn:merlin:" ) )
        {
            return getSystemContext().get( key );
        }
        else if( key.equals( NAME_KEY ) )
        {
            return getName();
        }
        else if( key.equals( PARTITION_KEY ) )
        {
            return getPartitionName();
        }
        else if( key.equals( CLASSLOADER_KEY ) )
        {
            return getClassLoader();
        }
        else if( key.equals( HOME_KEY ) )
        {
            return getHomeDirectory();
        }
        else if( key.equals( TEMP_KEY ) )
        {
            return getTempDirectory();
        }
        else
        {
            Object object = m_map.get( key );
            if( null != object )
            {
                final String classname = object.getClass().getName();
                try
                {
                    return ((EntryModel)object).getValue();
                }
                catch( Throwable e )
                {
                    final String error = 
                      REZ.getString( 
                        "deployment.context.runtime-get", 
                        key, classname );
                    throw new ModelRuntimeException( error, e );
                }
            }
            else
            {
                final String error = 
                  REZ.getString( 
                   "deployment.context.runtime-get", key );
                throw new ModelRuntimeException( error );
            }
        }
    }
}
