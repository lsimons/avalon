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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Map;

import org.apache.avalon.composition.data.BlockCompositionDirective;
import org.apache.avalon.composition.data.BlockIncludeDirective;
import org.apache.avalon.composition.data.CategoriesDirective;
import org.apache.avalon.composition.data.ContainmentProfile;
import org.apache.avalon.composition.data.DeploymentProfile;
import org.apache.avalon.composition.data.NamedDeploymentProfile;
import org.apache.avalon.composition.data.Profile;
import org.apache.avalon.composition.data.ResourceDirective;
import org.apache.avalon.composition.data.ServiceDirective;
import org.apache.avalon.composition.data.TargetDirective;
import org.apache.avalon.composition.data.builder.XMLTargetsCreator;
import org.apache.avalon.composition.data.builder.ContainmentProfileBuilder;
import org.apache.avalon.composition.data.builder.XMLContainmentProfileCreator;
import org.apache.avalon.composition.event.CompositionEvent;
import org.apache.avalon.composition.event.CompositionEventListener;
import org.apache.avalon.composition.model.ClassLoaderContext;
import org.apache.avalon.composition.model.ClassLoaderModel;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.ContainmentContext;
import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.Model;
import org.apache.avalon.composition.model.ModelException;
import org.apache.avalon.composition.model.ModelRuntimeException;
import org.apache.avalon.composition.model.ModelSelector;
import org.apache.avalon.composition.model.ProfileSelector;
import org.apache.avalon.composition.model.TypeRepository;
import org.apache.avalon.composition.model.ServiceModel;
import org.apache.avalon.composition.logging.LoggingManager;
import org.apache.avalon.composition.util.StringHelper;

import org.apache.avalon.repository.Repository;
import org.apache.avalon.repository.Artifact;
import org.apache.avalon.repository.RepositoryException;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.meta.info.DependencyDescriptor;
import org.apache.avalon.meta.info.ServiceDescriptor;
import org.apache.avalon.meta.info.StageDescriptor;
import org.apache.avalon.meta.info.Type;

import org.apache.avalon.util.exception.ExceptionHelper;


/**
 * Containment model implmentation within which composite models are aggregated
 * as a part of a containment deployment model.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.13.4.3 $ $Date: 2004/01/12 07:12:01 $
 */
public class DefaultContainmentModel extends DefaultModel 
  implements ContainmentModel
{
    //--------------------------------------------------------------
    // static
    //--------------------------------------------------------------

    private static final Resources REZ =
            ResourceManager.getPackageResources( DefaultContainmentModel.class );

    private static final ContainmentProfileBuilder BUILDER = 
      new ContainmentProfileBuilder();

    private static final XMLContainmentProfileCreator CREATOR = 
      new XMLContainmentProfileCreator();

    private static final XMLTargetsCreator TARGETS = 
      new XMLTargetsCreator();

    private static String getPath( ContainmentContext context )
    {
        if( context.getPartitionName() == null )
        {
            return SEPERATOR;
        }
        else
        {
            return context.getPartitionName();
        }
    }

    //==============================================================
    // state
    //==============================================================

    private ContainmentContext m_context;

    private String m_partition;

    private final Map m_models = new Hashtable();

    /**
     * The assigned logging categories.
     */
    private CategoriesDirective m_categories;

    private final LinkedList m_compositionListeners = new LinkedList();

    private ServiceModel[] m_services;

    //==============================================================
    // constructor
    //==============================================================

   /**
    * Creation of a new containment model.
    *
    * @param context the containment context that establishes
    *   the structural association of this containment model
    *   within a parent scope
    */
    public DefaultContainmentModel( final ContainmentContext context )
      throws ModelException
    {
        super( context.getLogger(), getPath( context ), context.getName(), 
          context.getContainmentProfile().getMode() );

        m_context = context;
        if( context.getPartitionName() == null )
        {
            m_partition = SEPERATOR;
        }
        else
        {
            m_partition = getPath() + getName() + SEPERATOR;
        }

        ClassLoader classloader = context.getClassLoader();
        ServiceDirective[] export = 
          context.getContainmentProfile().getExportDirectives();
        m_services = new DefaultServiceModel[ export.length ];
        for( int i=0; i<export.length; i++ )
        {
            ServiceDirective service = export[i];
            String classname = service.getReference().getClassname();
            try
            {
                Class clazz = classloader.loadClass( classname );
                m_services[i] = new DefaultServiceModel( service, clazz ); 
            }
            catch( Throwable e )
            {
                final String error = 
                  "Cannot load service class [" 
                  + classname 
                  + "].";
                throw new ModelException( error, e );
            }
        }

        Profile[] profiles = context.getContainmentProfile().getProfiles();
        for( int i=0; i<profiles.length; i++ )
        {
            addModel( profiles[i] );
        }
    }

    //==============================================================
    // Model
    //==============================================================

   /**
    * Return the classloader model.
    *
    * @return the classloader model
    */
    public ClassLoaderModel getClassLoaderModel()
    {
        return m_context.getClassLoaderModel();
    }

   /**
    * Return the set of services produced by the model.
    * @return the services
    */
    public ServiceDescriptor[] getServices()
    {
        return m_context.getContainmentProfile().getExportDirectives();
    }

   /**
    * Return TRUE is this model is capable of supporting a supplied 
    * depedendency.
    * @return true if this model can fulfill the dependency
    */
    public boolean isaCandidate( DependencyDescriptor dependency )
    {
        ServiceDescriptor[] services = getServices();
        for( int i=0; i<services.length; i++ )
        {
            ServiceDescriptor service = services[i];
            if( service.getReference().matches( dependency.getReference() ) )
            {
                return true;
            }
        }
        return false;
    }

   /**
    * Return TRUE is this model is capable of supporting a supplied 
    * stage dependency. The containment model implementation will 
    * allways return FALSE.
    *
    * @return FALSE containers don't export stage handling support
    */
    public boolean isaCandidate( StageDescriptor stage )
    {
        //
        // TODO-LATER: requires declaration of extension handling 
        // export within the container meta-data - for now a container
        // only exports services
        //

        return false;
    }

    //==============================================================
    // ContainmentModel
    //==============================================================


   /**
    * Add a composition listener to the model.
    * @param listener the composition listener
    */
    public void addCompositionListener( CompositionEventListener listener )
    {
        synchronized( m_compositionListeners )
        {
            m_compositionListeners.add( listener );
        }
    }

   /**
    * Remove a composition listener from the model.
    * @param listener the composition listener
    */
    public void removeCompositionListener( CompositionEventListener listener )
    {
        synchronized( m_compositionListeners )
        {
            m_compositionListeners.remove( listener );
        }
    }

   /**
    * Return the set of service export mappings
    * @return the set of export directives published by the model
    */
    public ServiceModel[] getServiceModels()
    {
        return m_services;
    }

   /**
    * Return the set of service export directives for a supplied class.
    * @param clazz a cleaa identifying the directive
    * @return the export directives
    */
    public ServiceModel getServiceModel( Class clazz )
    {
        ServiceModel[] models = getServiceModels();
        for( int i=0; i<models.length; i++ )
        {
            ServiceModel model = models[i];
            if( clazz.isAssignableFrom( model.getServiceClass() ) )
            {
                return model;
            }
        }
        return null;
    }

   /**
    * Return the logging categories. 
    * @return the logging categories
    */
    public CategoriesDirective getCategories()
    {
        if( m_categories == null ) 
          return m_context.getContainmentProfile().getCategories();
        return m_categories;
    }

   /**
    * Set categories. 
    * @param categories the logging categories
    */
    public void setCategories( CategoriesDirective categories )
    {
        m_categories = categories; // TODO: merge with existing categories
    }

   /**
    * Add a model referenced by a url to this model.
    * @param url the url of the model to include
    * @return the model 
    * @exception ModelException if a model related error occurs
    */
    public Model addModel( URL url ) throws ModelException
    {
        return addContainmentModel( url, null );
    }

    public ContainmentModel addContainmentModel( URL block, URL config ) 
      throws ModelException
    {
        ContainmentModel model = createContainmentModel( null, block );
        addModel( model.getName(), model );
        applyTargets( config );
        return model;
    }

    public Model addModel( Profile profile ) throws ModelException
    {
        if( null == profile )
          throw new NullPointerException( "profile" );

        Model model = null;
        final String name = profile.getName();
        if( profile instanceof ContainmentProfile )
        {
            ContainmentProfile containment = (ContainmentProfile) profile;
            model = createContainmentModel( containment );
        }
        else if( profile instanceof DeploymentProfile ) 
        {
            DeploymentProfile deployment = (DeploymentProfile) profile;
            model = createDeploymentModel( deployment );
        }
        else if( profile instanceof NamedDeploymentProfile ) 
        {
            DeploymentProfile deployment = 
              createDeploymentProfile( (NamedDeploymentProfile) profile );
            model = createDeploymentModel( deployment );
        }
        else if( profile instanceof BlockIncludeDirective ) 
        {
            BlockIncludeDirective directive = (BlockIncludeDirective) profile;
            model = createContainmentModel( directive );
        }
        else if( profile instanceof BlockCompositionDirective ) 
        {
            BlockCompositionDirective directive = (BlockCompositionDirective) profile;
            model = createContainmentModel( directive );
        }
        else
        {
            //
            // TODO: establish the mechanisms for the declaration
            // of a custom profile handler.
            //

            final String error = 
              REZ.getString( 
                "containment.unknown-profile-class.error", 
                getPath(), 
                profile.getClass().getName() );
            throw new ModelException( error );
        }
        return addModel( name, model );
    }

    private Model addModel( String name, Model model ) throws ModelException
    {
        synchronized( m_models )
        {
            m_models.put( name, model );
            CompositionEvent event = new CompositionEvent( this, model );
            fireModelAddedEvent( event );
            return model;
        }
    }

    private void fireModelAddedEvent( CompositionEvent event )
    {
        Iterator iterator = m_compositionListeners.iterator();
        while( iterator.hasNext() )
        {
            final CompositionEventListener listener = 
              (CompositionEventListener) iterator.next();
            try
            {
                listener.modelAdded( event );
            }
            catch( Throwable e )
            {
                final String message = 
                  "A composition listener raised an exception";
                final String error = 
                  ExceptionHelper.packException( message, e, true );
                getLogger().warn( error );
            }
        }
    }


   /**
    * Removal of a named model for the containment model.
    *
    * @param name the name of the subsidiary model to be removed
    * @exception IllegalArgumentException if the supplied name is unknown
    */
    public void removeModel( String name ) throws IllegalArgumentException
    {
        synchronized( m_models )
        {
            Model model = (Model) m_models.get( name );
            if( null == name )
            {
                final String error = 
                  "No model named [" + name 
                  + "] is referenced with the model [" 
                  + this + "].";
                throw new IllegalArgumentException( error ); 
            }
            else
            {
                m_models.remove( name );
                CompositionEvent event = new CompositionEvent( this, model );
                fireModelRemovedEvent( event );
            }
        }
    }

    private void fireModelRemovedEvent( CompositionEvent event )
    {
        Iterator iterator = m_compositionListeners.iterator();
        while( iterator.hasNext() )
        {
            final CompositionEventListener listener = 
              (CompositionEventListener) iterator.next();
            try
            {
                listener.modelRemoved( event );
            }
            catch( Throwable e )
            {
                final String message = 
                  "A composition listener raised an exception";
                final String error = 
                  ExceptionHelper.packException( message, e, true );
                getLogger().warn( error );
            }
        }
    }

   /**
    * Creation of a new instance of a deployment model within
    * this containment context.
    *
    * @param profile a containment profile 
    * @return the composition model
    */
    private DeploymentModel createDeploymentModel( final DeploymentProfile profile ) 
      throws ModelException
    {
        if( null == profile ) 
          throw new NullPointerException( "profile" );

        final String name = profile.getName();
        final String partition = getPartition();
        final Logger logger = getLogger().getChildLogger( name );

        if( getLogger().isDebugEnabled() )
        {
            final String message = 
              StringHelper.toString( REZ.getString( "containment.add", name ) );
            getLogger().debug(  message );
        }

        try
        {
            ClassLoader classLoader = m_context.getClassLoader();
            Class base = classLoader.loadClass( profile.getClassname() );
            Type type = 
              m_context.getClassLoaderModel().getTypeRepository().getType( base );
            final File home = new File( m_context.getHomeDirectory(), name );
            final File temp = new File( m_context.getTempDirectory(), name );
            DefaultDeploymentContext context = 
              new DefaultDeploymentContext( 
                logger, name, m_context, profile, type, base, home, temp, partition );

            //
            // TODO: lookup the profile for a factory declaration, then 
            // use the factory to create the model using the context as 
            // the argument.
            //

            return new DefaultDeploymentModel( context );
        }
        catch( Throwable e )
        {
            final String error = 
              REZ.getString( 
                "containment.deployment.create.error", 
                getPath(), 
                profile.getName() );
            throw new ModelException( error, e );
        }
    }

   /**
    * Creation of a new instance of a containment model within
    * this containment context.
    *
    * @param profile a containment profile 
    * @return the composition model
    */
    private ContainmentModel createContainmentModel( final ContainmentProfile profile ) 
      throws ModelException
    {
        final String name = profile.getName();
        return createContainmentModel( name, profile );
    }

   /**
    * Creation of a new instance of a containment model within
    * this containment context.
    *
    * @param name the containment name
    * @param profile a containment profile 
    * @return the composition model
    */
    private ContainmentModel createContainmentModel( 
      final String name, final ContainmentProfile profile ) 
      throws ModelException
    {
        return createContainmentModel( name, profile, new URL[0] );
    }

   /**
    * Creation of a new instance of a containment model within
    * this containment context.
    *
    * @param name the containment name
    * @param profile a containment profile 
    * @param implicit any implicit urls to include in the container classloader
    * @return the composition model
    */
    private ContainmentModel createContainmentModel( 
      final String name, final ContainmentProfile profile, URL[] implicit ) 
      throws ModelException
    {
        final String partition = getPartition();

        if( getLogger().isDebugEnabled() )
        {
            final String message = 
              StringHelper.toString( REZ.getString( "containment.add", name ) );
            getLogger().debug( message );
        }


        LoggingManager logging = m_context.getSystemContext().getLoggingManager();
        final String base = partition + name;
        logging.addCategories( base, profile.getCategories() );
        Logger log = logging.getLoggerForCategory( base );
        
        try
        {
            ClassLoaderContext cntx = 
              m_context.getClassLoaderModel().createChildContext( 
                log, profile, implicit );

            final ClassLoaderModel classLoaderModel = 
              DefaultClassLoaderModel.createClassLoaderModel( cntx );
            final File home = new File( m_context.getHomeDirectory(), name );
            final File temp = new File( m_context.getTempDirectory(), name );
            final Logger logger = getLogger().getChildLogger( name );

            DefaultContainmentContext context = 
              new DefaultContainmentContext( 
                logger, m_context.getSystemContext(), 
                classLoaderModel, home, temp, profile, partition, name );

            //
            // TODO: lookup the profile for a factory declaration, then 
            // use the factory to create the model using the context as 
            // the argument.
            //

            return new DefaultContainmentModel( context );
        }
        catch( ModelException e )
        {
            throw e;
        }
        catch( Throwable e )
        {
            final String error = 
              REZ.getString( 
                "containment.container.create.error", 
                getPath(), 
                profile.getName() );
            throw new ModelException( error, e );
        }
    }

   /**
    * Add a containment profile that is derived from an external resource.
    * @param directive the block composition directive
    * @return the containment model established by the include
    */
    private ContainmentModel createContainmentModel( 
      BlockCompositionDirective directive ) throws ModelException
    {
        final String name = directive.getName();
        final ResourceDirective resource = directive.getResource();
        final String id = resource.getId();
        final String group = resource.getGroup();
        final String resourceName = resource.getName();
        final String version = resource.getVersion();
        final String type = resource.getType();
        
        ContainmentModel model = null;
        try
        {
            Repository repository = m_context.getSystemContext().getRepository();
            Artifact artifact = 
              Artifact.createArtifact( group, resourceName, version, type );
            final URL url = repository.getResource( artifact );
            model = createContainmentModel( name, url );
        }
        catch( RepositoryException e )
        {
            final String error = 
              "Unable to include block [" + name 
              + "] into the containmment model [" 
              + getQualifiedName() 
              + "] because of a repository related error.";
            throw new ModelException( error, e );
        }

        TargetDirective[] targets = directive.getTargetDirectives();
        for( int i=0; i<targets.length; i++ )
        {
            TargetDirective target = targets[i];
            Model child = model.getModel( target.getPath() );
            if( child != null )
            {
                if( target.getConfiguration() != null )
                {
                    if( child instanceof DeploymentModel )
                    {
                        ((DeploymentModel)child).setConfiguration( 
                          target.getConfiguration() );
                    }
                    else if( child instanceof ContainmentModel )
                    {
                        final String warn = 
                          "Ignoring target configuration as the path [" 
                          + target.getPath() 
                          + "] does not refer to a deployment model";
                    }
                }
                if( target.getCategoriesDirective() != null )
                {
                    if( child instanceof DeploymentModel )
                    {
                        ((DeploymentModel)child).setCategories( 
                           target.getCategoriesDirective() );
                    }
                    else if( child instanceof ContainmentModel )
                    {
                        ((ContainmentModel)child).setCategories( 
                          target.getCategoriesDirective() );
                    }
                }
            }
            else
            {
                final String warning = 
                  "Unrecognized target path: " + target.getPath();
                getLogger().warn( warning );
            }
        }
        return model;
    }

   /**
    * Create a containment model that is derived from an external 
    * source profile defintion.
    *
    * @param directive the block include directive
    * @return the containment model established by the include
    */
    private ContainmentModel createContainmentModel( BlockIncludeDirective directive )
      throws ModelException
    {
        final String name = directive.getName();
        final String path = directive.getPath();

        try
        {
            if( path.indexOf( ":" ) < 0 )
            {
                URL anchor = m_context.getSystemContext().getBaseDirectory().toURL();
                URL url = new URL( anchor, path );
                return createContainmentModel( name, url );
            }
            else
            {
                URL url = new URL( path );
                return createContainmentModel( name, url );
            }
        }
        catch( MalformedURLException e )
        {
            final String error = 
              "Unable to include block [" + name 
              + "] into the containmment model [" 
              + getQualifiedName() 
              + "] because of a url related error.";
            throw new ModelException( error, e );
        }
    }

   /**
    * Create a containment model that is derived from an external 
    * source containment profile defintion.
    *
    * @param directive the block include directive
    * @return the containment model established by the include
    */
    private ContainmentModel createContainmentModel( String name, URL url )
      throws ModelException
    {
        final String path = url.toString();

        try
        {
            if( path.endsWith( ".jar" ) )
            {
                final URL jarURL = convertToJarURL( url );
                final URL blockURL = new URL( jarURL, "/BLOCK-INF/block.xml" );
                final InputStream stream = blockURL.openStream();

                try
                {
                    final ContainmentProfile profile = 
                      BUILDER.createContainmentProfile( stream );

                    final String message = 
                      "including composite block: " + blockURL.toString();
                    getLogger().debug( message );

                    return createContainmentModel( 
                      getName( name, profile ), profile, new URL[]{ url } );
                }
                catch( Throwable e )
                {
                    final String error = 
                    "Unable to create block from embedded descriptor [" 
                      + blockURL.toString() 
                    + "] in the containmment model [" 
                    + getQualifiedName() 
                    + "] due to a build related error.";
                    throw new ModelException( error, e );
                }
            }
            else if( path.endsWith( ".xml" ) || path.endsWith( ".block" ))
            {
                DefaultConfigurationBuilder builder = 
                  new DefaultConfigurationBuilder();
                Configuration config = 
                  builder.build( path );

                final ContainmentProfile profile = 
                  CREATOR.createContainmentProfile( config );

                final String message = 
                  "including composite block: " + path;
                getLogger().debug( message );

                return createContainmentModel( getName( name, profile ), profile );
            }
            else if( path.endsWith( "/" ) )
            {
                verifyPath( path );

                final URL blockURL = 
                  new URL( url.toString() + "BLOCK-INF/block.xml" );

                DefaultConfigurationBuilder builder = 
                  new DefaultConfigurationBuilder();
                Configuration config = 
                  builder.build( blockURL.toString() );

                final ContainmentProfile profile = 
                  CREATOR.createContainmentProfile( config );

                final String message = 
                  "including composite block: " + blockURL.toString();
                getLogger().debug( message );

                return createContainmentModel( 
                  getName( name, profile ), profile, new URL[]{ url }  );
            }
            else if( path.endsWith( ".bar" ) )
            {
                final String error = 
                  "Cannot execute a block archive: " + path;
                throw new ModelException( error );
            }
            else
            {
                verifyPath( path );
                return createContainmentModel( name, new URL( path + "/" ) );
            }
        }
        catch( ModelException e )
        {
            throw e;
        }
        catch( MalformedURLException e )
        {
            final String error = 
              "Unable to include block [" + path 
              + "] into the containmment model [" 
              + getQualifiedName() 
              + "] because of a url related error.";
            throw new ModelException( error, e );
        }
        catch( IOException e )
        {
            final String error = 
              "Unable to include block [" + path 
              + "] into the containmment model [" 
              + getQualifiedName() 
              + "] because of a io related error.";
            throw new ModelException( error, e );
        }
        catch( Throwable e )
        {
            final String error = 
              "Unable to include block [" + path 
              + "] into the containmment model [" 
              + getQualifiedName() 
              + "] because of an unexpected error.";
            throw new ModelException( error, e );
        }
    }

   /**
    * Verify the a path is valid.  The implementation will 
    * throw an exception if a connection to a url established 
    * using the path agument cann be resolved.
    *
    * @exception ModelException if the path is not resolvable 
    *    to a url connection
    */
    private void verifyPath( String path ) throws ModelException
    {
        try
        {
            URL url = new URL( path );
            URLConnection connection = url.openConnection();
            connection.connect();
        }
        catch( java.io.FileNotFoundException e )
        {
            final String error = 
              "File not found: " + path;
            throw new ModelException( error );
        }
        catch( Throwable e )
        {
            final String error = 
              "Invalid path: " + path;
            throw new ModelException( error, e );
        }
    }

    private String getName( String name, Profile profile )
    {
        if( name != null ) return name;
        return profile.getName();
    }

   /**
    * Return the partition name established by this containment context.
    * @return the partition name
    */
    public String getPartition()
    {
        return m_partition;
    }

   /**
    * Return the set of immediate child models nested 
    * within this model.
    *
    * @return the nested model
    */
    public Model[] getModels()
    {
        return (Model[]) m_models.values().toArray( new Model[0] );
    }

   /**
    * Return a child model relative to a supplied name.
    *
    * @param path a relative or absolute path
    * @return the named model or null if the name is unknown
    * @exception IllegalArgumentException if the name if badly formed
    */
    public Model getModel( String path )
    {
        if( path.startsWith( "/" ) )
        {
            return getModel( path.substring( 1, path.length() ) );
        }

        int j = path.indexOf( "/" );
        if( j > -1 )
        {
            String key = path.substring( 0, j );
            if( key.equals( "" ) )
            {
                return this;
            }
            else
            {
                String remainder = path.substring( j+1 );
                Model model = getModel( key );
                if( model == null )
                {
                    return null;
                }
                else if( model instanceof ContainmentModel )
                {
                    return ((ContainmentModel)model).getModel( remainder );
                }
                else
                {
                    final String error = 
                      "Bad path element: " + key + " in path: " + path;
                    throw new IllegalArgumentException( error );
                } 
            }
        }
        else
        {
            if( path.equals( "" ) )
            {
                return this;
            }
            else
            {
                return (Model) m_models.get( path );
            }
        }
    }

   /**
    * Get a local model relative to a supplied service dependency.
    * @param dependency the service dependency descriptor
    * @exception ModelRuntimeException if an error occurs during model establishment
    */
    public Model getModel( DependencyDescriptor dependency )
      throws ModelRuntimeException
    {
        //
        // if an existing model exists return it
        //

        Model[] models = getModels();
        ModelSelector modelSelector = new DefaultModelSelector();
        Model model = modelSelector.select( models, dependency );
        if( model != null ) return model;

        //
        // otherwise, check for any packaged profiles that 
        // we could use to construct the model
        //

        TypeRepository repository = 
          m_context.getClassLoaderModel().getTypeRepository();
        ArrayList list = new ArrayList();
        try
        {
            Type[] types = repository.getTypes( dependency );
            for( int i=0; i<types.length; i++ )
            {
                Profile[] profiles = repository.getProfiles( types[i] );
                for( int j=0; j<profiles.length; j++ )
                {
                    list.add( profiles[j] );
                }
            }

            //
            // TODO: update this to handle meta-data directed selector
            // creation (e.g. an extension urn) - problem is that we either
            // declare that this method is invoked when we are auto 
            // creating a model on demand - in effect what we need is a 
            // DependencyDirective instead of the descriptor.
            //

            Profile[] collection = (Profile[]) list.toArray( new Profile[0] );
            ProfileSelector selector = new DefaultProfileSelector();
            Profile profile = selector.select( collection, dependency );
            if( profile != null ) return addModel( profile );
            return null;
        }
        catch( Throwable e )
        {
            // should not happen
            final String error = 
              REZ.getString( 
                "containment.model.create.error", 
                getPath(), 
                dependency.toString() );
            throw new ModelRuntimeException( error, e );
        }
    }

   /**
    * Return a model relative to a supplied stage descriptor.
    * @param stage the stage descriptor
    * @return model of a an stage handler or null if the stage is unresolvable
    * @exception ModelRuntimeException if an error occurs during model establishment
    */
    public Model getModel( StageDescriptor stage ) 
      throws ModelRuntimeException
    {
        //
        // if an existing model exists return it
        //

        Model[] models = getModels();
        ModelSelector modelSelector = new DefaultModelSelector();
        Model model = modelSelector.select( models, stage );
        if( model != null ) return model;

        //
        // otherwise, check for any packaged profiles that 
        // we could use to construct the model
        //

        TypeRepository repository = 
          m_context.getClassLoaderModel().getTypeRepository();

        ArrayList list = new ArrayList();
        try
        {
            Type[] types = repository.getTypes( stage );
            for( int i=0; i<types.length; i++ )
            {
                Profile[] profiles = repository.getProfiles( types[i] );
                for( int j=0; j<profiles.length; j++ )
                {
                    list.add( profiles[j] );
                }
            }

            //
            // TODO: update this to handle meta-data directed selector
            // creation (e.g. an extension urn) - problem is that we either
            // declare that this method is invoked when we are auto 
            // creating a model on demand - in effect what we need is a 
            // DependencyDirective instead of the descriptor.
            //

            Profile[] collection = (Profile[]) list.toArray( new Profile[0] );
            ProfileSelector selector = new DefaultProfileSelector();
            Profile profile = selector.select( collection, stage );
            if( profile != null ) return addModel( profile );
            return null;
        }
        catch( Throwable e )
        {
            // should not happen
            final String error = 
              REZ.getString( 
                "containment.model.create.error", 
                getPath(), 
                stage.toString() );
            throw new ModelRuntimeException( error, e );
        }
    }


    //==============================================================
    // implementation
    //==============================================================

   /**
    * Conver a classic url to a jar url.  TIf the supplied url protocol is not 
    * the "jar" protocol, a ne url is created by prepending jar: and adding the 
    * trailing "!/".
    * @param url the url to convert
    * @return the converted url
    * @exception MalformedURLException if something goes wrong
    */
    private URL convertToJarURL( URL url ) throws MalformedURLException
    {
        if( url.getProtocol().equals( "jar" ) ) return url;
        return new URL( "jar:" + url.toString() + "!/" );
    }

   /**
    * Return a simple string represention of the containment model.
    * @return the string representation
    */
    public String toString()
    {
        return "[containment model: " + getQualifiedName() + "]";
    }

   /**
    * Create a full deployment profile using a supplied named profile reference.
    * @param profile the named profile reference directive
    * @return the deployment profile
    * @exception ModelException if an error occurs during profile creation
    */
    private DeploymentProfile createDeploymentProfile( NamedDeploymentProfile profile )
      throws ModelException
    {
        try
        {
            NamedDeploymentProfile holder = (NamedDeploymentProfile) profile;
            final String classname = holder.getClassname();
            final String key = holder.getKey();
            TypeRepository repository = 
              m_context.getClassLoaderModel().getTypeRepository();
            Type type = repository.getType( classname );
            DeploymentProfile template = repository.getProfile( type, key );
            return new DeploymentProfile( profile.getName(), template );
        }
        catch( Throwable e )
        {
            final String error = 
              REZ.getString( 
                "containment.model.create.deployment.error", 
                profile.getKey(), 
                getPath(), 
                profile.getClassname() );
            throw new ModelException( error, e );
        }
    }

   /**
    * Apply a set of override targets resolvable from a supplied url.
    * @param config a url resolvable to a TargetDirective[]
    * @exception ModelException if an error occurs
    */
    public void applyTargets( URL config )
      throws ModelException
    {
        if( config != null )
        {
            TargetDirective[] targets = getTargets( config );
            applyTargets( targets );
        }
    }

   /**
    * Apply a set of override targets.
    * @param targets a set of target directives
    */
    public void applyTargets( TargetDirective[]targets )
    {
        for( int i=0; i<targets.length; i++ )
        {
            TargetDirective target = targets[i];
            final String path = target.getPath();
            Object model = getModel( path );
            if( model != null )
            {
                if( model instanceof DeploymentModel )
                {
                    DeploymentModel deployment = (DeploymentModel) model;
                    if( target.getConfiguration() != null )
                    {
                        deployment.setConfiguration( target.getConfiguration() );
                    }
                    if( target.getCategoriesDirective() != null )
                    {
                        deployment.setCategories( target.getCategoriesDirective() );
                    }
                }
                else if( model instanceof ContainmentModel )
                {
                    ContainmentModel containment = (ContainmentModel) model;
                    if( target.getCategoriesDirective() != null )
                    {
                        containment.setCategories( target.getCategoriesDirective() );
                    }
                }
            }
            else
            {
                final String warning = 
                  "Ignoring target directive as the path does not refer to a known component: " 
                  + path;
                getLogger().warn( warning );
            }
        }
    }

    private TargetDirective[] getTargets( final URL url )
      throws ModelException
    {
        try
        {
            DefaultConfigurationBuilder builder = 
              new DefaultConfigurationBuilder();
            Configuration config = builder.build( url.toString() );
            return TARGETS.createTargets( config ).getTargets();
        }
        catch( Throwable e )
        {
            final String error = 
              "Could not load the targets directive: " + url;
            throw new ModelException( error, e );
        }
        
    }
}
