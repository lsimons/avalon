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
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.avalon.composition.data.ComponentProfile;
import org.apache.avalon.composition.data.ContainmentProfile;
import org.apache.avalon.composition.data.ClassLoaderDirective;
import org.apache.avalon.composition.data.builder.ContainmentProfileBuilder;
import org.apache.avalon.composition.data.builder.XMLContainmentProfileCreator;
import org.apache.avalon.composition.model.ClassLoaderModel;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.ComponentModel;
import org.apache.avalon.composition.model.ModelException;
import org.apache.avalon.composition.model.DependencyGraph;
import org.apache.avalon.composition.provider.SystemContext;
import org.apache.avalon.composition.provider.ModelFactory;
import org.apache.avalon.composition.provider.ContainmentContext;
import org.apache.avalon.composition.provider.ComponentContext;
import org.apache.avalon.composition.provider.ClassLoaderContext;

import org.apache.avalon.logging.provider.LoggingManager;
import org.apache.avalon.logging.data.CategoriesDirective;

import org.apache.avalon.repository.Repository;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;

import org.apache.avalon.meta.info.Type;

/**
 * A factory enabling the establishment of new composition model instances.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2004/02/10 16:23:34 $
 */
public class StandardModelFactory 
  implements ModelFactory
{
    //-------------------------------------------------------------------
    // static
    //-------------------------------------------------------------------

    private static final XMLContainmentProfileCreator CREATOR = 
      new XMLContainmentProfileCreator();

    private static final ContainmentProfileBuilder BUILDER = 
      new ContainmentProfileBuilder();

    private static final Resources REZ =
      ResourceManager.getPackageResources( StandardModelFactory.class );

    //-------------------------------------------------------------------
    // immutable state
    //-------------------------------------------------------------------

    private final SystemContext m_system;

    private final Logger m_logger;

    //-------------------------------------------------------------------
    // constructor
    //-------------------------------------------------------------------

    public StandardModelFactory( final SystemContext system )
    {
        if( system == null )
        {
            throw new NullPointerException( "system" );
        }
        m_system = system;
        m_logger = system.getLogger();
    }

    //-------------------------------------------------------------------
    // ModelFactory
    //-------------------------------------------------------------------

   /**
    * Creation of a new root containment model using 
    * a URL referring to a containment profile.
    *
    * @param url a composition profile source
    * @return the containment model
    */
    public ContainmentModel createRootContainmentModel( URL url ) 
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
                return createRootContainmentModel( profile );
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
            return createRootContainmentModel( profile );
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
    public ContainmentModel createRootContainmentModel( 
      ContainmentProfile profile ) 
      throws ModelException
    {
        try
        {
            ContainmentContext context = 
              createRootContainmentContext( profile );
            return createContainmentModel( context );
        }
        catch( Throwable e )
        {
            final String error = 
              REZ.getString( 
                "factory.containment.create.error", 
                profile.getName() );
            throw new ModelException( error, e );
        }
    }

   /**
    * Creation of a new nested deployment model.  This method is called
    * by a container implementation when constructing model instances.  The 
    * factory is identified by its implementation classname.
    *
    * @param context a potentially foreign deployment context
    * @return the deployment model
    */
    public ComponentModel createComponentModel( ComponentContext context )
      throws ModelException
    {
        return new DefaultComponentModel( context );
    }

   /**
    * Creation of a new nested containment model.  This method is called
    * by a container implementation when constructing model instances.  The 
    * factory is identified by its implementation classname.
    *
    * @param context a potentially foreign containment context
    * @return the containment model
    */
    public ContainmentModel createContainmentModel( 
      ContainmentContext context )
      throws ModelException
    {
        return new DefaultContainmentModel( context );
    }

    //-------------------------------------------------------------------
    // implementation
    //-------------------------------------------------------------------

   /**
    * Creation of a new root containment context.
    *
    * @param profile a containment profile 
    * @return the containment context
    */
    private ContainmentContext createRootContainmentContext( 
      ContainmentProfile profile ) 
      throws ModelException
    {
        if( profile == null )
        {
            throw new NullPointerException( "profile" );
        }

        m_system.getLoggingManager().addCategories( 
          profile.getCategories() );
        final Logger logger =
          m_system.getLoggingManager().getLoggerForCategory("");

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
                logger, m_system, classLoaderModel, null, null, profile );
        }
        catch( Throwable e )
        {
            final String error = 
              REZ.getString( 
                "factory.containment.create.error", 
                profile.getName() );
            throw new ModelException( error, e );
        }
    }

    private Logger getLogger()
    {
        return m_logger;
    }
}
