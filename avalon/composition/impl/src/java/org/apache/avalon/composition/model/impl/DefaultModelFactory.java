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
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.avalon.composition.model.ClassLoaderModel;
import org.apache.avalon.composition.model.ClassLoaderContext;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.ContainmentContext;
import org.apache.avalon.composition.model.DeploymentContext;
import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.ModelFactory;
import org.apache.avalon.composition.model.ModelException;
import org.apache.avalon.composition.model.SystemContext;
import org.apache.avalon.repository.Repository;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.extension.manager.OptionalPackage;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.composition.data.ContainmentProfile;
import org.apache.avalon.composition.data.ClassLoaderDirective;
import org.apache.avalon.composition.data.builder.ContainmentProfileBuilder;
import org.apache.avalon.composition.data.builder.XMLContainmentProfileCreator;

/**
 * A factory enabling the establishment of new composition model instances.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 09:32:10 $
 */
public class DefaultModelFactory extends AbstractLogEnabled 
  implements ModelFactory
{
    //==============================================================
    // static
    //==============================================================

    private static final XMLContainmentProfileCreator CREATOR = 
      new XMLContainmentProfileCreator();

    private static final ContainmentProfileBuilder BUILDER = 
      new ContainmentProfileBuilder();

    private static final Resources REZ =
      ResourceManager.getPackageResources( DefaultModelFactory.class );

    //==============================================================
    // immutable state
    //==============================================================

    final SystemContext m_system;

    //==============================================================
    // constructor
    //==============================================================

    public DefaultModelFactory( final SystemContext system )
    {
        if( system == null )
        {
            throw new NullPointerException( "system" );
        }
        m_system = system;
        enableLogging( system.getLogger() );
    }

    //==============================================================
    // ContainmentModelFactory
    //==============================================================

   /**
    * Creation of a new root containment model using 
    * a URL referring to a containment profile.
    *
    * @param url a composition profile source
    * @return the containment model
    */
    public ContainmentModel createContainmentModel( URL url ) 
      throws ModelException
    {
        //
        // START WORKAROUND
        // The code in the following if statement should not
        // not be needed, however, when attempting to load a 
        // url the referenes an XML source document we get a 
        // SAXParseException with the message "Content not 
        // allowed in prolog."  To get around this the if
        // statement forces loading via the XML creator.
        //

        if( url.toString().endsWith( ".xml" ) )
        {
            try
            {
                DefaultConfigurationBuilder builder = 
                  new DefaultConfigurationBuilder();
                Configuration config = 
                  builder.build( url.toString() );
                final ContainmentProfile profile = 
                  CREATOR.createContainmentProfile( config );
                return createContainmentModel( profile );
            }
            catch( ModelException e )
            {
                throw e;
            }
            catch( Throwable e )
            {
                final String error = 
                  "Could not create model due to a build related error.";
                throw new ModelException( error, e );
            }
        }

        //
        // This should work but does not.
        //

        try
        {
            final URLConnection connection = url.openConnection();
            final InputStream stream = connection.getInputStream();
            final ContainmentProfile profile = 
              BUILDER.createContainmentProfile( stream );
            return createContainmentModel( profile );
        }
        catch( Throwable e )
        {
            final String error = 
              REZ.getString( "factory.containment.create-url.error", url.toString() );
            throw new ModelException( error, e );
        }
    }

   /**
    * Creation of a new root containment model using 
    * a supplied profile.
    *
    * @param profile a containment profile 
    * @return the containment model
    */
    public ContainmentModel createContainmentModel( ContainmentProfile profile ) 
      throws ModelException
    {
        try
        {
            ContainmentContext context = createContainmentContext( profile );
            return createContainmentModel( context );
        }
        catch( Throwable e )
        {
            final String error = 
              REZ.getString( "factory.containment.create.error", profile.getName() );
            throw new ModelException( error, e );
        }
    }

   /**
    * Creation of a new root containment context.
    *
    * @param profile a containment profile 
    * @return the containment model
    */
    public ContainmentContext createContainmentContext( ContainmentProfile profile ) 
      throws ModelException
    {
        if( profile == null )
        {
            throw new NullPointerException( "profile" );
        }

        m_system.getLoggingManager().addCategories( profile.getCategories() );
        final Logger logger = m_system.getLoggingManager().getLoggerForCategory("");

        try
        {
            Repository repository = m_system.getRepository();
            File base = m_system.getBaseDirectory();

            ClassLoader root = m_system.getCommonClassLoader();
            ClassLoaderDirective classLoaderDirective = 
              profile.getClassLoaderDirective();

            ClassLoaderContext classLoaderContext =
              new DefaultClassLoaderContext( 
                logger, repository, base, root,
                classLoaderDirective );

            ClassLoaderModel classLoaderModel = 
              new DefaultClassLoaderModel( classLoaderContext );

            return new DefaultContainmentContext( 
                logger, m_system, classLoaderModel, profile );
        }
        catch( Throwable e )
        {
            final String error = 
              REZ.getString( "factory.containment.create.error", profile.getName() );
            throw new ModelException( error, e );
        }
    }


   /**
    * Creation of a new nested containment model.  This method is called
    * by a container implementation when constructing model instances.  The 
    * factory is identified by its implementation classname.
    *
    * @param context a potentially foreign containment context
    * @return the containment model
    */
    public ContainmentModel createContainmentModel( ContainmentContext context )
      throws ModelException
    {
        return new DefaultContainmentModel( context );
    }


   /**
    * Creation of a new nested deployment model.  This method is called
    * by a container implementation when constructing model instances.  The 
    * factory is identified by its implementation classname.
    *
    * @param context a potentially foreign deployment context
    * @return the deployment model
    */
    public DeploymentModel createDeploymentModel( DeploymentContext context )
      throws ModelException
    {
        return new DefaultDeploymentModel( context );
    }
}
