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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.avalon.composition.model.ContextModel;
import org.apache.avalon.composition.model.DependencyModel;
import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.DeploymentContext;
import org.apache.avalon.composition.model.ModelException;
import org.apache.avalon.composition.model.SystemContext;
import org.apache.avalon.composition.model.StageModel;
import org.apache.avalon.composition.data.DependencyDirective;
import org.apache.avalon.composition.data.StageDirective;
import org.apache.avalon.composition.data.CategoriesDirective;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.composition.data.ContextDirective;
import org.apache.avalon.composition.data.Mode;
import org.apache.avalon.meta.info.ContextDescriptor;
import org.apache.avalon.meta.info.DependencyDescriptor;
import org.apache.avalon.meta.info.InfoDescriptor;
import org.apache.avalon.meta.info.ServiceDescriptor;
import org.apache.avalon.meta.info.StageDescriptor;
import org.apache.avalon.meta.info.Type;
import org.apache.excalibur.configuration.CascadingConfiguration;

/**
 * Deployment model defintion.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.6 $ $Date: 2003/11/03 06:11:30 $
 */
public class DefaultDeploymentModel extends DefaultModel implements DeploymentModel
{
    //==============================================================
    // static
    //==============================================================

    private static final Resources REZ =
            ResourceManager.getPackageResources( DefaultDeploymentModel.class );

   private static final String CONTEXTUALIZABLE = 
     "org.apache.avalon.framework.context.Contextualizable";

    private static final Configuration EMPTY_CONFIGURATION =
      new DefaultConfiguration( 
        "configuration", DeploymentModel.class.getName() );

    //==============================================================
    // immutable state
    //==============================================================

    private final DeploymentContext m_context;

    private final ContextModel m_contextModel;

    private final boolean m_contextDependent;

    private final DependencyModel[] m_dependencies;

    private final StageModel[] m_stages;

    //==============================================================
    // mutable state
    //==============================================================

    private CategoriesDirective m_categories;

    private Configuration m_config;

    private Parameters m_parameters;

    private boolean m_activation;

    private int m_collection;

    //==============================================================
    // constructor
    //==============================================================

   /**
    * Creation of a new deployment model.
    *
    * @param context the deployment context
    */
    public DefaultDeploymentModel( DeploymentContext context )
      throws ModelException
    {
        super( 
          context.getLogger(), 
          context.getPartitionName(), 
          context.getProfile().getName(), 
          context.getProfile().getMode() );

        m_context = context;
        m_activation = m_context.getProfile().getActivationPolicy();
        m_categories = m_context.getProfile().getCategories();

        setCollectionPolicy( m_context.getProfile().getCollectionPolicy() );

        ClassLoader classLoader = m_context.getClassLoader();

        if( isConfigurable() )
        {
            final Configuration defaults = m_context.getType().getConfiguration();
            final Configuration explicit = m_context.getProfile().getConfiguration();
            final Configuration consolidated = 
              consolidateConfigurations( explicit, defaults );
            if( consolidated != null )
            {
                m_config = consolidated;
            }
            else
            {
                m_config = EMPTY_CONFIGURATION;
            }
        }

        if( isParameterizable() )
        {
            final Parameters parameters = 
              m_context.getProfile().getParameters();
            if( parameters != null )
            {
                m_parameters = parameters;
            }
            else
            {
                m_parameters = Parameters.EMPTY_PARAMETERS;
            }
        }

        m_contextDependent = getContextDependentState();

        if( m_contextDependent )
        {
            final ContextDescriptor contextDescriptor = 
              m_context.getType().getContext();
            final ContextDirective contextDirective = 
              m_context.getProfile().getContext();
            final Logger log = getLogger().getChildLogger( "context" );
            final SystemContext system = 
              m_context.getSystemContext();

            m_contextModel = new DefaultContextModel( 
              log, contextDescriptor, contextDirective, context );
        }
        else
        {
            m_contextModel = null;
        }

        //
        // create the dependency models for subsequent assembly
        // management
        //

        DependencyDescriptor[] dependencies = 
          m_context.getType().getDependencies();
        m_dependencies = new DefaultDependencyModel[ dependencies.length ];

        for( int i=0; i<dependencies.length; i++ )
        {
            DependencyDescriptor descriptor = dependencies[i];
            DependencyDirective directive = 
              context.getProfile().getDependencyDirective( descriptor.getKey() );
            m_dependencies[i] = 
              new DefaultDependencyModel( 
                context.getLogger().getChildLogger( "deps" ), 
                context.getPartitionName(), 
                context.getProfile().getName(), 
                descriptor,
                directive );
        }

        //
        // create the stage models for subsequent assembly
        // management
        //

        StageDescriptor[] stages = 
          m_context.getType().getStages();
        m_stages = new DefaultStageModel[ stages.length ];

        for( int i=0; i<stages.length; i++ )
        {
            StageDescriptor descriptor = stages[i];
            StageDirective directive = 
              context.getProfile().getStageDirective( descriptor.getKey() );
            m_stages[i] = 
              new DefaultStageModel( 
                context.getLogger().getChildLogger( "stages" ), 
                context.getPartitionName(), descriptor, directive );
        }
    }

    //==============================================================
    // Model
    //==============================================================

   /**
    * Return the set of services produced by the model.
    *
    * @return the service descriptors
    */
    public ServiceDescriptor[] getServices()
    {
        return m_context.getType().getServices();
    }


   /**
    * Return TRUE is this model is capable of supporting a supplied 
    * depedendency.
    *
    * @param dependency the dependency descriptor
    * @return true if this model can fulfill the dependency
    */
    public boolean isaCandidate( DependencyDescriptor dependency )
    {
        return m_context.getType().getService( dependency.getReference() ) != null;
    }

   /**
    * Return TRUE is this model is capable of supporting a supplied 
    * stage dependency.
    *
    * @param stage the stage descriptor
    * @return TRUE if this model can fulfill the stage dependency
    */
    public boolean isaCandidate( StageDescriptor stage )
    {
        return m_context.getType().getExtension( stage ) != null;
    }

    //==============================================================
    // DeploymentModel
    //==============================================================

   /**
    * Return the logging categories. 
    * @return the logging categories
    */
    public CategoriesDirective getCategories()
    {
        return m_categories;
    }

   /**
    * Return the collection policy for the model. If a profile
    * does not declare a collection policy, then the collection
    * policy declared by the underlying component type
    * will be used.
    *
    * @return the collection policy
    * @see InfoDescriptor#LIBERAL
    * @see InfoDescriptor#DEMOCRAT
    * @see InfoDescriptor#CONSERVATIVE
    */
    public int getCollectionPolicy()
    {
        if( m_collection == InfoDescriptor.UNDEFINED )
        {
            return getTypeCollectionPolicy();
        }
        else
        {
            return m_collection;
        }
    }

   /**
    * Set the collection policy for the model.
    *
    * @param policy the collection policy
    * @see InfoDescriptor#LIBERAL
    * @see InfoDescriptor#DEMOCRAT
    * @see InfoDescriptor#CONSERVATIVE
    */
    public void setCollectionPolicy( int policy )
    {
        if( policy == InfoDescriptor.UNDEFINED )
        {
            m_collection = InfoDescriptor.UNDEFINED;
        }
        else
        {
            int minimum = getTypeCollectionPolicy();
            if( policy >= minimum )
            {
                m_collection = policy;
            }
            else
            {
                final String warning = 
                  "Ignoring collection policy override [" + policy 
                  + "] because the value is higher that type threshhold [" + minimum + "].";
                getLogger().warn( warning );
            }
        }
    }

    private int getTypeCollectionPolicy()
    {
        return m_context.getType().getInfo().getCollectionPolicy();
    }
    

   /**
    * Set categories. 
    * @param categories the categories directive
    */
    public void setCategories( CategoriesDirective categories )
    {
        //
        // TODO: merge categories with profile categories
        //

        m_categories = categories;
    }

   /**
    * Return the activation policy for the model. 
    * @return the activaltion policy
    */
    public boolean getActivationPolicy()
    {
        return m_activation;
    }

   /**
    * Set the activation policy for the model.
    *
    * @param policy the activaltion policy
    */
    public void setActivationPolicy( boolean policy )
    {
        m_activation = policy;
    }

   /**
    * Set the activation policy for the model to the default value. 
    */
    public void revertActivationPolicy()
    {
        if( m_context.getProfile().getMode() == Mode.EXPLICIT )
        {
            m_activation = true;
        }
        else
        {
            m_activation = false;
        }
    }

   /**
    * Return the component type descriptor.
    * @return the type descriptor
    */
    public Type getType()
    {
        return m_context.getType();
    }

   /**
    * Return the class for the deployable target.
    * @return the class
    */
    public Class getDeploymentClass()
    {
        return m_context.getDeploymentClass();
    }

   /**
    * Rest if the component type backing the model is 
    * parameterizable.
    *
    * @return TRUE if the compoent type is parameterizable
    *   otherwise FALSE
    */
    public boolean isParameterizable()
    {
        return Parameters.class.isAssignableFrom( getDeploymentClass() );
    }

   /**
    * Set the parameters to the supplied value.  The supplied 
    * parameters value will replace the existing parameters value.
    *
    * @param parameters the supplied parameters value
    * @exception IllegalStateException if the component type backing the 
    *   model does not implement the parameteriazable interface
    * @exception NullPointerException if the supplied parameters are null
    */
    public void setParameters( Parameters parameters )
    {
        setParameters( parameters, true );
    }

   /**
    * Set the parameters to the supplied value.  The supplied 
    * parameters value may suppliment or replace the existing 
    * parameters value.
    *
    * @param parameters the supplied parameters
    * @param policy if TRUE the supplied parameters replaces the current
    *   parameters value otherwise the existing and supplied values
    *   are aggregrated
    * @exception IllegalStateException if the component type backing the 
    *   model does not implement the parameteriazable interface
    * @exception NullPointerException if the supplied parameters are null
    */
    public void setParameters( Parameters parameters, boolean policy )
      throws IllegalStateException
    {
        if( !isParameterizable() )
        {
            final String error = 
              REZ.getString( 
                "deployment.parameters.irrational", 
                getDeploymentClass().getName(), 
                this.toString() );
            throw new IllegalStateException( error );
        }

        if( parameters == null )
        {
            throw new NullPointerException( "parameters" );
        }

        if( policy )
        {
            Properties props = Parameters.toProperties( m_parameters );
            Properties suppliment = Parameters.toProperties( parameters );
            Enumeration enum = suppliment.propertyNames();
            while( enum.hasMoreElements() )
            {
                String name = (String) enum.nextElement();
                String value = suppliment.getProperty( name );
                if( value == null )
                {
                    props.remove( name );
                }
                else
                {
                    props.setProperty( name, value );
                }
            }
            m_parameters = Parameters.fromProperties( props );
        }
        else
        {        
            m_parameters = parameters;
        }
    }

   /**
    * Return the parameters to be applied to the component.
    *
    * @return the parameters
    */
    public Parameters getParameters()
    {
        return m_parameters;
    }

   /**
    * Rest if the component type backing the model is 
    * configurable.
    *
    * @return TRUE if the component type is configurable
    *   otherwise FALSE
    */
    public boolean isConfigurable()
    {
        return Configurable.class.isAssignableFrom( getDeploymentClass() );
    }

   /**
    * Set the configuration to the supplied value.  The supplied 
    * configuration will replace the existing configuration.
    *
    * @param config the supplied configuration
    * @exception IllegalStateException if the component type backing the 
    *   model does not implement the configurable interface
    * @exception NullPointerException if the supplied configuration is null
    */
    public void setConfiguration( Configuration config )
      throws IllegalStateException, NullPointerException
    {
        setConfiguration( config, true );
    }

   /**
    * Set the configuration to the supplied value.  The supplied 
    * configuration may suppliment or replace the existing configuration.
    *
    * @param config the supplied configuration
    * @param policy if TRUE the supplied configuration replaces the current
    *   configuration otherwise the resoved configuration shall be layed above
    *   the configuration supplied with the profile which in turn is layer above 
    *   the type default configuration (if any)
    * @exception IllegalStateException if the component type backing the 
    *   model does not implement the configurable interface
    * @exception NullPointerException if the supplied configuration is null
    */
    public void setConfiguration( Configuration config, boolean policy )
      throws IllegalStateException, NullPointerException
    {
        if( !isConfigurable() )
        {
            final String error = 
              REZ.getString( 
                "deployment.configuration.irrational", 
                getDeploymentClass().getName(), 
                this.toString() );
            throw new IllegalStateException( error );
        }

        if( config == null )
        {
            throw new NullPointerException( "config" );
        }

        if( policy )
        {
            m_config = consolidateConfigurations( config, m_config );
        }
        else
        {
            m_config = config;
        }
    }

   /**
    * Return the configuration to be applied to the component.
    * The implementation returns the current configuration state.
    * If the the component type does not implementation the 
    * Configurable interface, the implementation returns null. 
    *
    * @return the qualified configuration
    */
    public Configuration getConfiguration()
    {
        return m_config;
    }

   /**
    * Test if the component type backing the model requires the 
    * establishment of a runtime context.
    *
    * @return TRUE if the component type requires a runtime
    *   context otherwise FALSE
    */
    public boolean isContextDependent()
    {
        return m_contextDependent;
    }

   /**
    * Return the context model for this deployment model.
    * 
    * @return the context model if this model is context dependent, else
    *   the return value is null
    */
    public ContextModel getContextModel()
    {
        return m_contextModel;
    }

   /**
    * Return the dependency models for this component type.
    *
    * @return the dependency models
    */
    public DependencyModel[] getDependencyModels()
    {
        return m_dependencies;
    }

   /**
    * Return the stage models for this component type.
    *
    * @return the stage models
    */
    public StageModel[] getStageModels()
    {
        return m_stages;
    }

   /**
    * Return the set of services produced by the model as a array of classes.
    *
    * @return the service classes
    */
    public Class[] getInterfaces()
    {
        //
        // TODO: add a SoftReference to hold the service class array
        // instad of generating each time
        //

        ClassLoader classLoader = m_context.getClassLoader();
        ArrayList list = new ArrayList();
        ServiceDescriptor[] services = getServices();
        for( int i=0; i<services.length; i++ )
        {
            final ServiceDescriptor service = services[i];
            final String classname = service.getReference().getClassname();
            list.add( getComponentClass( classLoader, classname ) );
        }

        //
        // if the component is an extension then add all implemented 
        // interfaces
        //

        if( getType().getExtensions().length > 0 )
        {
            Class[] interfaces = getDeploymentClass().getInterfaces();
            for( int i=0; i<interfaces.length; i++ )
            {
                list.add( interfaces[i] );
            }
        }

        return (Class[]) list.toArray( new Class[0] );
    }

    //==============================================================
    // implementation
    //==============================================================

   /**
    * Test if the component type backing the model requires the 
    * establishment of a runtime context.
    *
    * @param return TRUE if the component type requires a runtime
    *   context otherwise FALSE
    */
    private boolean getContextDependentState()
    {
        if( m_context.getType().getStages().length > 0 )
        {
            return true;
        }

        Class base = m_context.getDeploymentClass();
        String strategy = 
          m_context.getType().getContext().getAttribute( 
              ContextDescriptor.STRATEGY_KEY, null );
        ClassLoader classLoader = m_context.getClassLoader();

        if( strategy != null )
        {
            Class contextualizable = 
              getComponentClass( classLoader, strategy );


            if( contextualizable == null )
            {
                final String error = 
                  REZ.getString( 
                    "deployment.missing-strategy.error", 
                    strategy, base.getName() );
                throw new IllegalStateException( error );
            }
            else
            {
                if( contextualizable.isAssignableFrom( base ) )
                {
                    return true;
                }
                else
                {
                    final String error = 
                      REZ.getString( 
                        "deployment.inconsitent-strategy.error",
                        contextualizable, base );
                    throw new IllegalStateException( error );
                }
            }
        }
        else
        {
            Class contextualizable = 
              getComponentClass( classLoader, CONTEXTUALIZABLE );
            if( contextualizable != null )
            {
                if( contextualizable.isAssignableFrom( base ) )
                {
                    return true;
                }
            }
        }
        return false;
    }

    private Class getComponentClass( ClassLoader classLoader, String classname )
    {
        if( classLoader == null )
        {
            throw new NullPointerException( "classLoader" );
        }
        if( classname == null )
        {
            throw new NullPointerException( "classname" );
        }

        try
        {
            return classLoader.loadClass( classname );
        }
        catch( ClassNotFoundException e )
        {
            return null;
        }
    }

    private Configuration consolidateConfigurations( 
      final Configuration primary, final Configuration defaults )
    {
        if( primary == null )
        {
            return defaults;
        }
        else
        {
            if( defaults == null )
            {
                 return primary;
            }
            else
            {
                return new CascadingConfiguration( primary, defaults );
            }
        }
    }

    public String toString()
    {
        return "[deployment model : " + getQualifiedName() + "]";
    }
}
