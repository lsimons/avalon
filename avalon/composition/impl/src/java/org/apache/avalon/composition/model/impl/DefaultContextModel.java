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

import java.util.Map;
import java.util.Hashtable;
import java.lang.reflect.Constructor;

import org.apache.avalon.composition.data.ContextDirective;
import org.apache.avalon.composition.data.EntryDirective;
import org.apache.avalon.composition.data.ImportDirective;
import org.apache.avalon.composition.data.ConstructorDirective;
import org.apache.avalon.composition.model.EntryModel;
import org.apache.avalon.composition.model.ContextModel;
import org.apache.avalon.composition.model.ComponentModel;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.ModelException;
import org.apache.avalon.composition.provider.ComponentContext;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;

import org.apache.avalon.meta.info.ContextDescriptor;
import org.apache.avalon.meta.info.EntryDescriptor;


/**
 * <p>Specification of a context model from which a 
 * a fully qualifed context can be established.</p>
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.12 $ $Date: 2004/02/22 17:28:30 $
 */
public class DefaultContextModel extends DefaultDependent implements ContextModel
{
    //==============================================================
    // static
    //==============================================================

    private static final Resources REZ =
      ResourceManager.getPackageResources( DefaultContextModel.class );

    /**
     * The default context implementation class to be used if
     * no context class is defined.
     */
    public static final Class DEFAULT_CONTEXT_CLASS = 
      DefaultContext.class;

    public static boolean isaStandardKey( String key )
    {
        return ( key.startsWith( "urn:avalon:" ) 
          || key.startsWith( "urn:composition:" ));
    }

    //==============================================================
    // immutable state
    //==============================================================

    private final ContextDescriptor m_descriptor;

    private final ContextDirective m_directive;

    private final ComponentContext m_context;

    private final Class m_strategy;

    private final Map m_models = new Hashtable();

    private final Map m_map = new Hashtable();

    private final Context m_componentContext;

    //==============================================================
    // constructor
    //==============================================================

   /**
    * <p>Default implementation of the context model.  The implementation
    * takes an inital system context as the base for context value 
    * establishment and uses this to set standard context entries.</p>
    *
    * @param logger the logging channel
    * @param descriptor the contextualization stage descriptor that describes
    *   the set of context entries that the component type is requesting
    * @param directive the contextualization directive that describes a set 
    *   of context entry creation strategies
    * @param context the component model context argument
    */
    public DefaultContextModel( 
      Logger logger, ContextDescriptor descriptor, 
      ContextDirective directive, ComponentContext context )
      throws ModelException
    {
        super( logger );

        if( null == descriptor )
        {
            throw new NullPointerException( "descriptor" );
        }

        if( null == context )
        {
            throw new NullPointerException( "context" );
        }

        m_descriptor = descriptor;
        m_directive = directive;
        m_context = context;

        ClassLoader classLoader = context.getClassLoader();
        m_strategy = loadStrategyClass( descriptor, classLoader );

        //
        // get the set of context entries declared by the component type
        // and for for each entry determine the context entry model to 
        // use for context entry value resolution
        //

        EntryDescriptor[] entries = descriptor.getEntries();
        for( int i=0; i<entries.length; i++ )
        {
            EntryDescriptor entry = entries[i];
            String alias = entry.getAlias();
            final String key = entry.getKey();
            if( isaStandardKey( key ) )
            {
                 DefaultImportModel model = 
                   new DefaultImportModel( entry, key, m_context );
                 setEntryModel( alias, model );
            }
            else
            {
                //
                // its a non standard context entry so check for a 
                // entry directive with a matching key to define
                // the mechanism for building the context entry
                //

                EntryDirective entryDirective = 
                  directive.getEntryDirective( key );
                if( null == entryDirective )
                {
                    if( entry.isRequired() )
                    {
                        final String error = 
                          REZ.getString( 
                            "context.missing-directive.error", key );
                        throw new ModelException( error );
                    }
                }
                else
                {
                    //
                    // there are only two context entry models - import
                    // and constructor - identify the model to use then add
                    // the resolved model to the map
                    //

                    if( entryDirective instanceof ImportDirective )
                    {
                        //
                        // importing under an alias of a container scoped key
                        //

                        ImportDirective importDirective = 
                          (ImportDirective) entryDirective;
                        String ref = importDirective.getImportKey();
                        DefaultImportModel model = 
                          new DefaultImportModel( entry, ref, m_context );
                        setEntryModel( alias, model );
                    }
                    else if( entryDirective instanceof ConstructorDirective )
                    {
                        ConstructorDirective constructor = 
                          (ConstructorDirective) entryDirective;
                        DefaultConstructorModel model = 
                          new DefaultConstructorModel( 
                            entry, 
                            constructor, 
                            context, 
                            m_map );
                        setEntryModel( alias, model );
                    }
                    else
                    {
                        String modelClass = 
                          entryDirective.getClass().getName();
                        final String error = 
                          REZ.getString( 
                            "context.unsupported-directive.error", 
                            key, modelClass );
                        throw new ModelException( error );
                    }
                }
            }
        }

        m_componentContext = 
          createComponentContext( classLoader, descriptor, directive, m_map );
    }

    
    //==============================================================
    // ContextModel
    //==============================================================

   /**
    * Return the set of entry models associated with this context model.
    * 
    * @return the entry models
    */
    public EntryModel[] getEntryModels()
    {
        return (EntryModel[]) m_map.values().toArray( new EntryModel[0] );
    }

   /**
    * Return an entry model matching the supplied key.
    * 
    * @return the entry model or null if tyhe key is unknown
    */
    public EntryModel getEntryModel( String key )
    {
        return (EntryModel) m_map.get( key ); 
    }

   /**
    * Set the entry model relative to a supplied key.
    * 
    * @param key the entry key
    * @param model the entry model
    */
    public void setEntryModel( String key, EntryModel model )
    {
        m_map.put( key, model ); 
    }

   /**
    * Set the entry to a suplied value.
    * 
    * @param key the entry key
    * @param value the entry value
    */
    public void setEntry( String key, Object value )
    {
        EntryDescriptor descriptor = m_descriptor.getEntry( key );
        OverrideEntryModel model = 
          new OverrideEntryModel( descriptor, value );
        setEntryModel( key, model );
    }

   /**
    * Return the class representing the contextualization stage interface.
    * 
    * @return the class representing the contextualization interface
    */
    public Class getStrategyClass()
    {
        return m_strategy;
    }

   /**
    * Return the context object established for the component.
    * 
    * @return the context object
    */
    public Context getContext()
    {
        return m_componentContext;
    }

    //==============================================================
    // implementation
    //==============================================================

   /**
    * Load the contextualization strategy class.
    * @param descriptor the context descriptor
    * @param classloader the classloader 
    * @return the strategy class
    */
    private Class loadStrategyClass( 
       ContextDescriptor descriptor, ClassLoader classloader )
      throws ModelException
    {
        final String strategy = m_descriptor.getAttribute( 
          ContextDescriptor.STRATEGY_KEY, null );
        if( strategy != null )
        {
            try
            {
                Class clazz = classloader.loadClass( strategy );
                if( getLogger().isDebugEnabled() )
                {
                    final String message = 
                      REZ.getString( "context.strategy.custom", strategy );
                    getLogger().debug( message );
                }
                return clazz;
            }
            catch( ClassNotFoundException e )
            {
                final String error = 
                  REZ.getString( "context.strategy.custom.missing.error", strategy );
                throw new ModelException( error );
            }
            catch( Throwable e )
            {
                final String error = 
                  REZ.getString( "context.strategy.custom.unexpected.error", strategy );
                throw new ModelException( error, e );
            }
        }
        else
        {
            try
            {
                Class clazz = classloader.loadClass( DEFAULT_STRATEGY_CLASSNAME );
                if( getLogger().isDebugEnabled() )
                {
                    final String message = 
                      REZ.getString( "context.strategy.avalon" );
                    getLogger().debug( message );
                }
                return clazz;
            }
            catch( ClassNotFoundException e )
            {
                final String error = 
                  REZ.getString( 
                    "context.strategy.avalon.missing.error", 
                    DEFAULT_STRATEGY_CLASSNAME );
                throw new ModelException( error );
            }
            catch( Throwable e )
            {
                final String error = 
                  REZ.getString( 
                    "context.strategy.avalon.unexpected.error",
                    DEFAULT_STRATEGY_CLASSNAME );
                throw new ModelException( error, e );
            }
        }
    }

   /**
    * Creates a component context instance.
    * 
    * @param classloader the deployment context classloader
    * @param descriptor the context descriptor
    * @param directive the context directive
    * @return the context object compliant with the context casting
    *   constraints declared by the component type
    * @exception ModelException if an error occurs while attempting to 
    *   construct the context instance
    */
    private Context createComponentContext( 
      ClassLoader classloader, 
      ContextDescriptor descriptor, 
      ContextDirective directive, Map map )
      throws ModelException
    {
        Class clazz = loadContextClass( directive, classloader );
        validateCastingConstraint( descriptor, classloader, clazz );
        Context base = new DefaultContext( map );

        if( clazz.equals( DefaultContext.class ) ) return base; 

        //
        // its a custom context object so we need to create it 
        // using the classic context object as the constructor 
        // argument
        //

        try
        {
            Constructor constructor = clazz.getConstructor(
                new Class[]{ Context.class } );
            return (Context) constructor.newInstance( new Object[]{ base } );
        }
        catch( NoSuchMethodException e )
        {
            final String error =
              REZ.getString( "context.non-compliance-constructor.error", clazz.getName() );
            throw new ModelException( error, e );
        }
        catch( Throwable e )
        {
            final String error =
              REZ.getString( "context.custom-unexpected.error", clazz.getName() );
            throw new ModelException( error, e );
        }
    }

   /**
    * Load the context implementation class.
    * @param directive the context directive (possibly null)
    * @param classLoader the classloader 
    * @return the strategy class
    */
    private Class loadContextClass( 
       ContextDirective directive, ClassLoader classLoader )
      throws ModelException
    {
        if( directive == null )
        {
            return DEFAULT_CONTEXT_CLASS;
        }

        final String classname = directive.getClassname();
        if( classname == null )
        {
            return DEFAULT_CONTEXT_CLASS;
        }
        else
        {
            try
            {
                return classLoader.loadClass( classname );
            }
            catch( Throwable e )
            {
                final String error = 
                  "Cannot load custom context implementation class: "
                  + classname;
                throw new ModelException( error, e );
            }
        }
    }

   /**
    * Validate that the context implememtation class implements
    * any casting constraint declared or implied by the context 
    * descriptor.
    * 
    * @param descriptor the context descriptor
    * @param classLoader the classloader
    * @param clazz the context implementation class
    * @exception if a validation failure occurs
    */
    private void validateCastingConstraint( 
      ContextDescriptor descriptor, ClassLoader classLoader, Class clazz )
      throws ModelException
    {

        Class castingClass = null;

        final String castingClassName = 
          descriptor.getContextInterfaceClassname();

        if( castingClassName != null )
        {
            try
            {
                castingClass =
                  classLoader.loadClass( castingClassName );
            }
            catch( Throwable e )
            {
                final String error = 
                  "Cannot load custom context interface class: "
                  + castingClassName;
                throw new ModelException( error, e );
            }
        }
        else
        {
            try
            {
                castingClass =
                  classLoader.loadClass( Context.class.getName() );
            }
            catch( Throwable e )
            {
                final String error = 
                  "Cannot load standard Avalon context interface class: "
                  + Context.class.getName();
                throw new ModelException( error, e );
            }
        }

        if( !castingClass.isAssignableFrom( clazz ) )
        {
            final String error = 
              "Supplied context implementation class: " 
              + clazz.getName() 
              + " does not implement the interface: " 
              + castingClass.getName()
              + ".";
            throw new ModelException( error );
        }
    }
}
