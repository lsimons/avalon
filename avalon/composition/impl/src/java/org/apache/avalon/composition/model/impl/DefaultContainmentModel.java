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
import org.apache.avalon.composition.data.ComponentProfile;
import org.apache.avalon.composition.data.NamedComponentProfile;
import org.apache.avalon.composition.data.DeploymentProfile;
import org.apache.avalon.composition.data.ResourceDirective;
import org.apache.avalon.composition.data.ServiceDirective;
import org.apache.avalon.composition.data.TargetDirective;
import org.apache.avalon.composition.data.builder.XMLTargetsCreator;
import org.apache.avalon.composition.data.builder.ContainmentProfileBuilder;
import org.apache.avalon.composition.data.builder.XMLContainmentProfileCreator;
import org.apache.avalon.composition.event.CompositionEvent;
import org.apache.avalon.composition.event.CompositionEventListener;
import org.apache.avalon.composition.model.AssemblyException;
import org.apache.avalon.composition.model.ClassLoaderContext;
import org.apache.avalon.composition.model.ClassLoaderModel;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.ContainmentContext;
import org.apache.avalon.composition.model.ContextModel;
import org.apache.avalon.composition.model.DependencyModel;
import org.apache.avalon.composition.model.DependencyGraph;
import org.apache.avalon.composition.model.ComponentModel;
import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.ModelException;
import org.apache.avalon.composition.model.ModelRuntimeException;
import org.apache.avalon.composition.model.ModelRepository;
import org.apache.avalon.composition.model.ModelSelector;
import org.apache.avalon.composition.model.ProfileSelector;
import org.apache.avalon.composition.model.ServiceModel;
import org.apache.avalon.composition.model.SystemContext;
import org.apache.avalon.composition.model.StageModel;
import org.apache.avalon.composition.model.TypeRepository;
import org.apache.avalon.composition.model.TypeUnknownException;
import org.apache.avalon.composition.logging.LoggingManager;
import org.apache.avalon.composition.util.StringHelper;
import org.apache.avalon.repository.Repository;
import org.apache.avalon.repository.Artifact;
import org.apache.avalon.repository.RepositoryException;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.parameters.Parameters;

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
 * @version $Revision: 1.13.2.16 $ $Date: 2004/01/12 05:41:05 $
 */
public class DefaultContainmentModel extends DefaultDeploymentModel 
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
            return SEPARATOR;
        }
        else
        {
            return context.getPartitionName();
        }
    }

    //--------------------------------------------------------------
    // immutable state
    //--------------------------------------------------------------

    private final LinkedList m_compositionListeners = new LinkedList();

    private final DefaultState m_assembly = new DefaultState();

    private final Map m_models = new Hashtable();

    private final ContainmentContext m_context;

    private final String m_partition;

    private final ServiceModel[] m_services;

    //--------------------------------------------------------------
    // state
    //--------------------------------------------------------------

    private CategoriesDirective m_categories;

    //--------------------------------------------------------------
    // constructor
    //--------------------------------------------------------------

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
        super( context );

        m_context = context;

        if( null == context.getPartitionName() )
        {
            m_partition = DeploymentModel.SEPARATOR;
        }
        else
        {
            m_partition = context.getPartitionName() 
              + context.getName()
              + DeploymentModel.SEPARATOR;
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

        DeploymentProfile[] profiles = context.getContainmentProfile().getProfiles();
        for( int i=0; i<profiles.length; i++ )
        {
            addModel( profiles[i] );
        }
    }

    //--------------------------------------------------------------
    // DeploymentModel
    //--------------------------------------------------------------

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
    * Returns the maximum allowable time for deployment.
    *
    * @return the maximum time expressed in millisecond of how 
    * long a deployment may take.
    */
    public long getDeploymentTimeout()
    {
        long n = super.getDeploymentTimeout();
        if( isAssembled() )
        {
            DeploymentModel[] startup = getStartupGraph();
            for( int i=0; i<startup.length; i++ )
            {
                n = ( n + startup[i].getDeploymentTimeout() );
            }
        }
        return n;
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
    * @return FALSE containers don't export stage handling
    */
    public boolean isaCandidate( StageDescriptor stage )
    {
        return false;
    }

    /**
     * Returns the assembled state of the model.
     * @return true if this model is assembled
     */
    public boolean isAssembled()
    {
        return m_assembly.isEnabled();
    }

    /**
     * Assemble the model.
     * @exception Exception if an error occurs during model assembly
     */
    public void assemble() throws AssemblyException
    {
        synchronized( m_assembly )
        {
            if( isAssembled() )
            {
                return;
            }

            getLogger().debug( "assembly phase" );
            DeploymentModel[] models = m_context.getModelRepository().getModels();
            for( int i=0; i<models.length; i++ )
            {
                DeploymentModel model = models[i];
                assembleModel( model );
            }

            m_assembly.setEnabled( true );
        }
    }

    private void assembleModel( DeploymentModel model ) throws AssemblyException
    {
         if( null == model )
         {
             throw new NullPointerException( "model" );
         }

         if( model.isAssembled() ) 
         {
             return;
         }
         else
         {
             if( model instanceof ComponentModel )
             {
                 assembleComponent( (ComponentModel) model );
             }
             else
             {
                 model.assemble();
             }
        }
    }

    private void assembleComponent( ComponentModel model ) throws AssemblyException
    {
        ModelRepository repository = m_context.getModelRepository();

        //
        // locate and assemble the component context handler
        //

        if( model.getContextModel() != null )
        {
            ContextModel context = model.getContextModel();
            Class clazz = context.getStrategyClass();
            if( !clazz.getName().equals( 
              ContextModel.DEFAULT_STRATEGY_CLASSNAME ) )
            {
                if( null == context.getProvider() )
                {
                    try
                    {
                        StageDescriptor stage = 
                          new StageDescriptor( clazz.getName() );
                        DeploymentModel provider = 
                          findExtensionProvider( repository, stage );
                        context.setProvider( provider );
                    }
                    catch( Throwable e )
                    {
                        final String error = 
                          "Unable to assemble component: " 
                          + model 
                         + " due to a component context phase handler establishment failure.";
                        throw new AssemblyException( error, e );
                    }
                }
            }
        }

        //
        // locate and resolve the stage providers
        //

        StageModel[] stages = model.getStageModels();
        for( int i=0; i<stages.length; i++ )
        {
            StageModel stage = stages[i];
            if( null == stage.getProvider() )
            {
                try
                {
                    DeploymentModel provider =
                      findExtensionProvider( repository, stage );
                    stage.setProvider( provider );
                }
                catch( Throwable e )
                {
                    final String error = 
                      "Unable to assemble component: " 
                      + model 
                      + " due to a component extension handler establishment failure.";
                    throw new AssemblyException( error, e );
                }
            }
        }

        //
        // locate and resolve the service providers
        //

        DependencyModel[] dependencies = model.getDependencyModels();
        for( int i=0; i<dependencies.length; i++ )
        {
            DependencyModel dependency = dependencies[i];
            if( null == dependency.getProvider() )
            {
                try
                {
                    DeploymentModel provider =
                      findDependencyProvider( repository, dependency );
                    dependency.setProvider( provider );
                }
                catch( Throwable e )
                {
                    final String error = 
                      "Unable to assemble component: " + model 
                      + " due to a service provider establishment failure.";
                    throw new AssemblyException( error, e );
                }
            }
        }
    }

    private DeploymentModel findDependencyProvider( 
      ModelRepository repository, DependencyModel dependency )
      throws AssemblyException
    {
        String path = dependency.getPath();
        if( null != path )
        {
            DeploymentModel model = getModel( path );
            if( null == model )
            {
                final String error = 
                  "Could not locate a model at the address: [" 
                  + path + "] in " + this + ".";
                throw new AssemblyException( error );
            }
            assembleModel( model );
            return model;
        }
        else
        {
            return findDependencyProvider( 
              repository, dependency.getDependency() );
        }
    }

    private DeploymentModel findDependencyProvider( 
      ModelRepository repository, DependencyDescriptor dependency )
      throws AssemblyException
    {
        DeploymentModel[] candidates = 
          repository.getCandidateProviders( dependency );
        ModelSelector selector = new DefaultModelSelector();
        DeploymentModel model = selector.select( candidates, dependency );
        if( model != null )
        {
            assembleModel( model );
            return model;
        }

        //
        // otherwise, check for any packaged profiles that 
        // we could use to construct the model
        //

        DeploymentProfile[] profiles = findDependencyProfiles( dependency );
        ProfileSelector profileSelector = new DefaultProfileSelector();
        DeploymentProfile profile = profileSelector.select( profiles, dependency );
        if( profile != null ) 
        {
            try
            {
                DeploymentModel solution = addModel( profile );
                assembleModel( solution );
                return solution;
            }
            catch( AssemblyException ae )
            {
                final String error = 
                  "Nested assembly failure while attempting to construct model"
                  + " for the profile: [" + profile + "] for the dependency: ["
                  + dependency + "].";
                throw new AssemblyException( error, ae );
            }
            catch( ModelException me )
            {
                final String error = 
                  "Nested model failure while attempting to add model"
                  + " for the profile: [" + profile + "] for the dependency: ["
                  + dependency + "].";
                throw new AssemblyException( error, me );
            }
        }
        else
        {
            final String error = 
              "Unable to locate a service provider for the dependency: [ "
              + dependency + "].";
            throw new AssemblyException( error );
        }
    }

    private DeploymentModel findExtensionProvider( 
      ModelRepository repository, StageModel stage )
      throws AssemblyException
    {
        String path = stage.getPath();
        if( null != path )
        {
            DeploymentModel model = getModel( path );
            if( null == model )
            {
                final String error = 
                  "Could not locate a model at the address: [" 
                  + path + "] in " + this + ".";
                throw new AssemblyException( error );
            }
            assembleModel( model );
            return model;
        }
        else
        {
            return findExtensionProvider( repository, stage.getStage() );
        }
    }

    private DeploymentModel findExtensionProvider( 
      ModelRepository repository, StageDescriptor stage )
      throws AssemblyException
    {
        DeploymentModel[] candidates = 
          repository.getCandidateProviders( stage );
        ModelSelector selector = new DefaultModelSelector();
        DeploymentModel model = selector.select( candidates, stage );
        if( model != null )
        {
            assembleModel( model );
            return model;
        }

        //
        // otherwise, check for any packaged profiles that 
        // we could use to construct the model
        //

        DeploymentProfile[] profiles = findExtensionProfiles( stage );
        ProfileSelector profileSelector = new DefaultProfileSelector();
        DeploymentProfile profile = profileSelector.select( profiles, stage );
        if( profile != null ) 
        {
            try
            {
                DeploymentModel solution = addModel( profile );
                assembleModel( solution );
                return solution;
            }
            catch( AssemblyException ae )
            {
                final String error = 
                  "Nested assembly failure while attempting to construct model"
                  + " for the extension profile: [" + profile 
                  + "] for the stage dependency: ["
                  + stage + "].";
                throw new AssemblyException( error, ae );
            }
            catch( ModelException me )
            {
                final String error = 
                  "Nested model failure while attempting to add model"
                  + " for the extension profile: [" + profile 
                  + "] for the stage dependency: ["
                  + stage + "].";
                throw new AssemblyException( error, me );
            }
        }
        else
        {
            final String error = 
              "Unable to locate a extension provider for the stage: [ "
              + stage + "].";
            throw new AssemblyException( error );
        }
    }

    private DeploymentProfile[] findExtensionProfiles( StageDescriptor stage )
    {
        TypeRepository repository = getClassLoaderModel().getTypeRepository();
        Type[] types = repository.getTypes( stage );
        try
        {
            return getProfiles( repository, types );
        }
        catch( TypeUnknownException tue )
        {
            // will not happen
            final String error = "An irrational condition has occured.";
            throw new ModelRuntimeException( error, tue );
        }
    }

    private DeploymentProfile[] findDependencyProfiles( DependencyDescriptor dependency )
    {
        TypeRepository repository = getClassLoaderModel().getTypeRepository();
        Type[] types = repository.getTypes( dependency );
        try
        {
            return getProfiles( repository, types );
        }
        catch( TypeUnknownException tue )
        {
            // will not happen
            final String error = "An irrational condition has occured.";
            throw new ModelRuntimeException( error, tue );
        }
    }

    private DeploymentProfile[] getProfiles( TypeRepository repository, Type[] types )
      throws TypeUnknownException
    {
        ArrayList list = new ArrayList();
        for( int i=0; i<types.length; i++ )
        {
            DeploymentProfile[] profiles = 
            repository.getProfiles( types[i] );
            for( int j=0; j<profiles.length; j++ )
            {
                list.add( profiles[j] );
            }
        }
        return (DeploymentProfile[]) list.toArray( new DeploymentProfile[0] );
    }

    /**
     * Disassemble the model.
     */
    public void disassemble()
    {
        synchronized( m_assembly )
        {
            if( !isAssembled() )
            {
                return;
            }

            getLogger().debug( "dissassembly phase" );
            DeploymentModel[] models = m_context.getModelRepository().getModels();
            for( int i=0; i<models.length; i++ )
            {
                DeploymentModel model = models[i];
                model.disassemble();
            }
            m_assembly.setEnabled( false );
        }
    }

    /**
     * Return the set of models assigned as providers.
     * @return the providers consumed by the model
     * @exception IllegalStateException if the model is not in an assembled state 
     */
    public DeploymentModel[] getProviders()
    {
        if( !isAssembled() ) 
        {
             final String error = 
               "Model is not assembled.";
             throw new IllegalStateException( error );
        }

        ArrayList list = new ArrayList();
        DeploymentModel[] models = m_context.getModelRepository().getModels();
        for( int i=0; i<models.length; i++ )
        {
            DeploymentModel model = models[i];
            DeploymentModel[] providers = model.getProviders();
            for( int j=0; j<providers.length; j++ )
            {
                DeploymentModel provider = providers[j];
                final String path = provider.getPath();
                final String root = getPartition();
                if( !path.startsWith( root ) )
                {
                    list.add( providers[j] );
                }
            }
        }
        return (DeploymentModel[]) list.toArray( new DeploymentModel[0] );
    }

    //--------------------------------------------------------------
    // ContainmentModel
    //--------------------------------------------------------------

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
            if( model.getServiceClass().isAssignableFrom( clazz ) )
            {
                return model;
            }
        }
        return null;
    }

    /**
     * Get the startup sequence for the model.
     */
    public DeploymentModel[] getStartupGraph()
    {
        return m_context.getDependencyGraph().getStartupGraph();
    }

    /**
     * Get the shutdown sequence for the model.
     */
    public DeploymentModel[] getShutdownGraph()
    {
        return m_context.getDependencyGraph().getShutdownGraph();
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
    public DeploymentModel addModel( URL url ) throws ModelException
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

    public DeploymentModel addModel( DeploymentProfile profile ) throws ModelException
    {
        if( null == profile )
          throw new NullPointerException( "profile" );

        DeploymentModel model = null;
        final String name = profile.getName();
        if( profile instanceof ContainmentProfile )
        {
            ContainmentProfile containment = (ContainmentProfile) profile;
            model = createContainmentModel( containment );
        }
        else if( profile instanceof ComponentProfile ) 
        {
            ComponentProfile deployment = (ComponentProfile) profile;
            model = createComponentModel( deployment );
        }
        else if( profile instanceof NamedComponentProfile ) 
        {
            ComponentProfile deployment = 
              createComponentProfile( (NamedComponentProfile) profile );
            model = createComponentModel( deployment );
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

    private DeploymentModel addModel( 
      String name, DeploymentModel model ) throws ModelException
    {
        ModelRepository repository = m_context.getModelRepository();
        synchronized( repository )
        {
            repository.addModel( name, model );
            m_context.getDependencyGraph().add( model );
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
        ModelRepository repository = m_context.getModelRepository();
        synchronized( repository )
        {
            DeploymentModel model = (DeploymentModel) repository.getModel( name );
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
                m_context.getDependencyGraph().add( model );
                repository.removeModel( model );
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
    private ComponentModel createComponentModel( final ComponentProfile profile ) 
      throws ModelException
    {
        if( null == profile )
        {
            throw new NullPointerException( "profile" );
        }

        final String name = profile.getName();
        final String partition = getPartition();

        LoggingManager logging = m_context.getSystemContext().getLoggingManager();
        CategoriesDirective categories = profile.getCategories();
        if( null != categories )
        {   
            logging.addCategories( partition, profile.getCategories() );
        }
        Logger logger = logging.getLoggerForCategory( partition + name );
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

            DefaultComponentContext context = 
              new DefaultComponentContext( 
                logger, name, m_context, this, 
                profile, type, base, home, temp, partition );

            //
            // TODO: lookup the profile for a factory declaration, then 
            // use the factory to create the model using the context as 
            // the argument.
            //

            return new DefaultComponentModel( context );
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

            ModelRepository modelRepository = 
              m_context.getModelRepository();

            DependencyGraph graph = 
              m_context.getDependencyGraph();

            DefaultContainmentContext context = 
              new DefaultContainmentContext( 
                logger, m_context.getSystemContext(), 
                classLoaderModel, modelRepository, graph, 
                home, temp, this, profile, partition, name );

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
            DeploymentModel child = model.getModel( target.getPath() );
            if( child != null )
            {
                if( target.getConfiguration() != null )
                {
                    if( child instanceof ComponentModel )
                    {
                        ((ComponentModel)child).setConfiguration( 
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
                    if( child instanceof ComponentModel )
                    {
                        ((ComponentModel)child).setCategories( 
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
    private ContainmentModel createContainmentModel( 
      BlockIncludeDirective directive )
      throws ModelException
    {
        final String name = directive.getName();
        final String path = directive.getPath();

        try
        {
            if( path.indexOf( ":" ) < 0 )
            {
                URL anchor = 
                  m_context.getSystemContext().getBaseDirectory().toURL();
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

    private String getName( String name, DeploymentProfile profile )
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
    public DeploymentModel[] getModels()
    {
        return m_context.getModelRepository().getModels();
    }

   /**
    * Return a child model relative to a supplied name.
    *
    * @param path a relative or absolute path
    * @return the named model or null if the name is unknown
    * @exception IllegalArgumentException if the name if badly formed
    */
    public DeploymentModel getModel( String path )
    {
        ContainmentModel parent = 
          m_context.getParentContainmentModel();

        if( path.equals( "" ) )
        {
            return this;
        }
        else if( path.startsWith( "/" ) )
        {
            //
            // its a absolute reference that need to be handled by the 
            // root container
            //

            if( null != parent )
            {
                return parent.getModel( path );
            }
            else
            {
                //
                // this is the root container thereforw the 
                // path can be transfored to a relative reference
                //

                return getModel( path.substring( 1 ) );
            }
        }
        else
        {
            //
            // its a relative reference in the form xxx/yyy/zzz
            // so if the path contains "/", then locate the token 
            // proceeding the "/" (i.e. xxx) and apply the remainder 
            // (i.e. yyy/zzz) as the path argument , otherwise, its 
            // a local reference that we can pull from the model 
            // repository
            //

            final String root = getRootName( path );

            if( root.equals( ".." ) )
            {
                //
                // its a relative reference in the form "../xxx/yyy" 
                // in which case we simply redirect "xxx/yyy" to the 
                // parent container
                //
 
                if( null != parent )
                {
                    final String remainder = getRemainder( root, path );
                    return parent.getModel( remainder );
                }
                else
                {
                    final String error = 
                      "Supplied path ["
                      + path 
                      + "] references a container above the root container.";
                    throw new IllegalArgumentException( error );
                }
            }
            else if( root.equals( "." ) )
            {
                //
                // its a path with a redundant "./xxx/yyy" which is 
                // equivalent to "xxx/yyy"
                //
 
                final String remainder = getRemainder( root, path );
                return getModel( remainder );
            }
            else if( path.indexOf( "/" ) < 0 )
            {
                // 
                // its a path in the form "xxx" so we can use this
                // to lookup and return a local child
                //

                return m_context.getModelRepository().getModel( path );
            }
            else
            {
                //
                // locate the relative root container, and apply 
                // getModel to the container
                //

                DeploymentModel model = 
                  m_context.getModelRepository().getModel( root );
                if( model != null )
                {
                    //
                    // we have the sub-container so we can apply 
                    // the relative path after subtracting the name of 
                    // this container and the path seperator character
                    //

                    if( model instanceof ContainmentModel )
                    {
                        ContainmentModel container = 
                          (ContainmentModel) model;
                        final String remainder = getRemainder( root, path );
                        return container.getModel( remainder );
                    }
                    else
                    {
                        final String error = 
                          "The path element [" + root 
                          + "] does not reference a containment model within ["
                          + this + "].";
                        throw new IllegalArgumentException( error );
                    }
                }
                else
                {
                    //
                    // path contains a token that does not map to 
                    // known container
                    //
                    
                    final String error = 
                      "Unable to locate a container with name [" 
                      + root + "] within the container [" 
                      + this + "].";
                    throw new IllegalArgumentException( error );
                }
            }
        }
    }

    private String getRootName( String path )
    {
        int n = path.indexOf( "/" );
        if( n < 0 ) 
        {
            return path;
        }
        else
        {
            return path.substring( 0, n ); 
        }
    }

    private String getRemainder( String name, String path )
    {
        return path.substring( name.length() + 1 );
    }

    //==============================================================
    // implementation
    //==============================================================

   /**
    * Conver a classic url to a jar url.  If the supplied url protocol is not 
    * the "jar" protocol, a ne url is created by prepending jar: and adding the 
    * trailing "!/".
    *
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
    * Create a full deployment profile using a supplied named 
    * profile reference.
    *
    * @param profile the named profile reference directive
    * @return the deployment profile
    * @exception ModelException if an error occurs during 
    *    profile creation
    */
    private ComponentProfile createComponentProfile( 
      NamedComponentProfile profile )
      throws ModelException
    {
        try
        {
            NamedComponentProfile holder = 
              (NamedComponentProfile) profile;
            final String classname = holder.getClassname();
            final String key = holder.getKey();
            TypeRepository repository = 
              m_context.getClassLoaderModel().getTypeRepository();
            Type type = repository.getType( classname );
            ComponentProfile template = 
              repository.getProfile( type, key );
            return new ComponentProfile( profile.getName(), template );
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
                if( model instanceof ComponentModel )
                {
                    ComponentModel deployment = (ComponentModel) model;
                    if( target.getConfiguration() != null )
                    {
                        deployment.setConfiguration( 
                          target.getConfiguration() );
                    }
                    if( target.getCategoriesDirective() != null )
                    {
                        deployment.setCategories( 
                          target.getCategoriesDirective() );
                    }
                }
                else if( model instanceof ContainmentModel )
                {
                    ContainmentModel containment = (ContainmentModel) model;
                    if( target.getCategoriesDirective() != null )
                    {
                        containment.setCategories( 
                          target.getCategoriesDirective() );
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