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
import java.security.Permission;

import org.apache.avalon.composition.model.ClassLoaderContext;
import org.apache.avalon.composition.model.TypeRepository;
import org.apache.avalon.composition.model.ServiceRepository;
import org.apache.avalon.repository.Repository;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.extension.manager.OptionalPackage;
import org.apache.avalon.extension.manager.ExtensionManager;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.composition.data.ClassLoaderDirective;

/**
 * Implementation of a system context that exposes a system wide set of parameters.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.3 $ $Date: 2004/01/19 01:26:19 $
 */
public class DefaultClassLoaderContext extends DefaultContext 
  implements ClassLoaderContext
{
    //==============================================================
    // static
    //==============================================================

    private static final Resources REZ =
            ResourceManager.getPackageResources( DefaultClassLoaderContext.class );

    //==============================================================
    // immutable state
    //==============================================================

   /**
    * The logging channel to apply to the classloader model.
    */
    private final Logger m_logger;

   /**
    * The parent classloader.
    */
    private final ClassLoader m_parent;

   /**
    * Option packages established through the chain of parent 
    * models.
    */
    private final OptionalPackage[] m_packages;

   /**
    * The base directory for resolution of extension directories and 
    * fileset directories.
    */
    private final File m_base;

   /**
    * The local jar repository.
    */
    private final Repository m_repository;

   /**
    * The optional extensions package manager.
    */
    private final ExtensionManager m_manager;

   /**
    * The parent type manager.
    */
    private final TypeRepository m_types;

   /**
    * The parent service manager.
    */
    private final ServiceRepository m_services;

   /**
    * The classloader directive.
    */
    private final ClassLoaderDirective m_directive;

   /**
    * Implied url to include in the classpath.
    */
    private final URL[] m_implied;

    //==============================================================
    // constructor
    //==============================================================

   /**
    * Creation of a root classloader context.
    *
    * @param logger the logging channel to assign to the classloader model
    * @param repository a local repository
    * @param base the system base directory
    * @param parent the parent classloader
    * @param directive the classloader directive
    */
    public DefaultClassLoaderContext( 
      Logger logger, Repository repository, File base, 
      ClassLoader parent, ClassLoaderDirective directive )
    {
        this( logger, repository, base, parent, 
          new OptionalPackage[0], null, null, null, directive, null );
    }

   /**
    * Creation of a new classloader context.
    *
    * @param logger the logging channel to assign to the classloader model
    * @param repository a local repository
    * @param base the system base directory
    * @param parent the parent classloader
    * @param packages the set of optional packages established under 
    *    current classloader chain
    * @param manager the optional extions package manager established 
    *    by the parent classloader
    * @param types the parent type manager
    * @param services the parent service manager
    * @param directive the classloader directive
    */
    public DefaultClassLoaderContext( 
      Logger logger, Repository repository, File base, 
      ClassLoader parent, OptionalPackage[] packages, 
      ExtensionManager manager, TypeRepository types,
      ServiceRepository services,
      ClassLoaderDirective directive,
      URL[] implied )
    {
        if( logger == null )
        {
            throw new NullPointerException( "logger" );
        }
        if( repository == null )
        {
            throw new NullPointerException( "repository" );
        }
        if( base == null )
        {
            throw new NullPointerException( "base" );
        }
        if( parent == null )
        {
            throw new NullPointerException( "parent" );
        }
        if( packages == null )
        {
            throw new NullPointerException( "packages" );
        }
        if( directive == null )
        {
            throw new NullPointerException( "directive" );
        }

        m_logger = logger;
        m_repository = repository;
        m_base = base;
        m_parent  = parent;
        m_packages = packages;
        m_manager = manager;
        m_types = types;
        m_services = services;
        m_directive = directive;

        if( implied == null )
        {
            m_implied = new URL[0];
        }
        else
        {
            m_implied = implied;
        }
    }

    //==============================================================
    // ClassLoaderContext
    //==============================================================

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
    * Return the system context.
    *
    * @return the system context
    */
    public Repository getRepository()
    {
        return m_repository;
    }

   /**
    * Return the base directory from which relative library directives
    * and fileset directory paths may be resolved.
    *
    * @return the base directory
    */
    public File getBaseDirectory()
    {
        return m_base;
    }

   /**
    * Return the classloader to be assigned as the parernt classloader.
    *
    * @return the classloader
    */
    public ClassLoader getClassLoader()
    {
        return m_parent;
    }

   /**
    * Return the containment classloader.
    *
    * @return the classloader
    */
    public OptionalPackage[] getOptionalPackages()
    {
        return m_packages;
    }

   /**
    * Return the extension manager established by the parent 
    * classloader model.
    *
    * @return the extension manager
    */
    public ExtensionManager getExtensionManager()
    {
        return m_manager;
    }

   /**
    * Return the type repository established by the parent classloader.
    *
    * @return the type repository
    */
    public TypeRepository getTypeRepository()
    {
        return m_types;
    }

   /**
    * Return the service repository established by the parent classloader.
    *
    * @return the service repository
    */
    public ServiceRepository getServiceRepository()
    {
        return m_services;
    }

   /**
    * Return the classloader directive to be applied to the 
    * classloader model.
    *
    * @return the classloader directive
    */
    public ClassLoaderDirective getClassLoaderDirective()
    {
        return m_directive;
    }

   /**
    * Return any implied urls to include in the classloader.
    *
    * @return the implied urls
    */
    public URL[] getImplicitURLs()
    {
        return m_implied;
    } 

}
