/* 
* Copyright 2003-2004 The Apache Software Foundation
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
namespace Apache.Avalon.Composition.Model.Default
{
	using System;

	using Apache.Avalon.Framework;
	using Apache.Avalon.Composition.Data;
	using Apache.Avalon.Composition.Model;
	using Apache.Avalon.Meta;
	
	/// <summary> <p>Specification of a context model from which a 
	/// a fully qualifed context can be established.</p>
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.2 $ $Date: 2004/02/29 18:07:17 $
	/// </version>
	public class DefaultContextModel : DefaultDependent, IContextModel
	{
		private void  InitBlock()
		{
			m_models = new System.Collections.Hashtable();
			m_map = new System.Collections.Hashtable();
		}

		//==============================================================
		// static
		//==============================================================
		
		/// <summary> The default context implementation class to be used if
		/// no context class is defined.
		/// </summary>
		//UPGRADE_NOTE: Final was removed from the declaration of 'DEFAULT_CONTEXT_CLASS '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		public static readonly System.Type DEFAULT_CONTEXT_CLASS = typeof(DefaultContext);
		
		//==============================================================
		// immutable state
		//==============================================================
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_descriptor '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private ContextDescriptor m_descriptor;
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_directive '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private ContextDirective m_directive;
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_context '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private IComponentContext m_context;
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_strategy '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private System.Type m_strategy;
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_models '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		//UPGRADE_NOTE: The initialization of  'm_models' was moved to method 'InitBlock'. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1005"'
		private System.Collections.IDictionary m_models;
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_map '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		//UPGRADE_NOTE: The initialization of  'm_map' was moved to method 'InitBlock'. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1005"'
		private System.Collections.IDictionary m_map;
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_componentContext '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private IContext m_componentContext;
		
		//==============================================================
		// constructor
		//==============================================================
		
		/// <summary> <p>Default implementation of the context model.  The implementation
		/// takes an inital system context as the base for context value 
		/// establishment and uses this to set standard context entries.</p>
		/// 
		/// </summary>
		/// <param name="logger">the logging channel
		/// </param>
		/// <param name="descriptor">the contextualization stage descriptor
		/// </param>
		/// <param name="directive">the contextualization directive
		/// </param>
		/// <param name="context">the deployment context
		/// </param>
		public DefaultContextModel(ILogger logger, ContextDescriptor descriptor, ContextDirective directive, IComponentContext context):base(logger)
		{
			InitBlock();
			
			if (null == descriptor)
			{
				throw new System.ArgumentNullException("descriptor");
			}
			
			if (null == context)
			{
				throw new System.ArgumentNullException("context");
			}
			
			m_descriptor = descriptor;
			m_directive = directive;
			m_context = context;
			
			//UPGRADE_ISSUE: Class 'java.lang.ClassLoader' was not converted. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1000_javalangClassLoader"'
			m_strategy = loadStrategyClass(descriptor);
			
			//
			// get the set of context entries declared by the component type
			// and for for each entry determine the context entry model to 
			// use for context entry value resolution
			//
			
			EntryDescriptor[] entries = descriptor.Entries;
			for (int i = 0; i < entries.Length; i++)
			{
				EntryDescriptor entry = entries[i];
				//UPGRADE_NOTE: Final was removed from the declaration of 'key '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
				String key = entry.Key;
				if (key.StartsWith("urn:avalon:"))
				{
					try
					{
						System.Object value_Renamed = m_context.Resolve(key);
						object tempObject;
						tempObject = value_Renamed;
						m_map[key] = tempObject;
						System.Object generatedAux = tempObject;
					}
					catch (ContextException )
					{
						if (entry.Required)
						{
							String error = "context.non-standard-avalon-key.error" + " " + key;
							throw new ModelException(error);
						}
					}
				}
				else if (key.Equals(Apache.Avalon.Composition.Model.IContainmentModel_Fields.KEY))
				{
					//
					// TODO: check that the component has permission
					// to access the containment model
					//
					
					object tempObject2;
					tempObject2 = context.ContainmentModel;
					m_map[Apache.Avalon.Composition.Model.IContainmentModel_Fields.KEY] = tempObject2;
					System.Object generatedAux2 = tempObject2;
				}
				else if (key.StartsWith("urn:composition:"))
				{
					try
					{
						System.Object value_Renamed = m_context.SystemContext[key];
						object tempObject3;
						tempObject3 = value_Renamed;
						m_map[key] = tempObject3;
						System.Object generatedAux3 = tempObject3;
					}
					catch (ContextException )
					{
						if (entry.Required)
						{
							String error = "context.non-standard-avalon-key.error" + " " + key;
							throw new ModelException(error);
						}
					}
				}
				else
				{
					//
					// its a non standard context entry so check for a 
					// entry directive with a matching key to define
					// the mechanism for building the context entry
					//
					
					EntryDirective entryDirective = directive.getEntryDirective(key);
					if (null == entryDirective)
					{
						if (entry.Required)
						{
							String error = "context.missing-directive.error" + " " + key;
							throw new ModelException(error);
						}
					}
					else
					{
						//
						// there are only two context entry models - import
						// and constructor - identify the model to use then add
						// the resolved model to the map
						//
						
						if (entryDirective is ImportDirective)
						{
							ImportDirective importDirective = (ImportDirective) entryDirective;
							DefaultImportModel model = new DefaultImportModel(entry, importDirective, context, m_map);
							m_context.Register(model);
							object tempObject4;
							tempObject4 = model.Value;
							m_map[key] = tempObject4;
							System.Object generatedAux4 = tempObject4;
						}
						else if (entryDirective is ConstructorDirective)
						{
							ConstructorDirective constructor = (ConstructorDirective) entryDirective;
							DefaultConstructorModel model = new DefaultConstructorModel(entry, constructor, context, m_map);
							m_context.Register(model);
							object tempObject5;
							tempObject5 = model.Value;
							m_map[key] = tempObject5;
							System.Object generatedAux5 = tempObject5;
						}
						else
						{
							String modelClass = entryDirective.GetType().FullName;
							String error = "context.unsupported-directive.error" + " " + key + " " + modelClass;
							throw new ModelException(error);
						}
					}
				}
			}
			
			m_componentContext = CreateComponentContext(m_context, descriptor, directive);
			
			if (Logger.IsDebugEnabled)
			{
				Logger.Debug("context: " + m_map);
			}
		}
		
		//==============================================================
		// IContextModel
		//==============================================================
		
		/// <summary> Return the context object established for the component.
		/// 
		/// </summary>
		/// <returns> the context object
		/// </returns>
		public virtual IContext Context
		{
			get
			{
				return m_componentContext;
			}
			
		}

		/// <summary> Return the class representing the contextualization stage interface.
		/// 
		/// </summary>
		/// <returns> the class representing the contextualization interface
		/// </returns>
		public virtual System.Type StrategyClass
		{
			get
			{
				return m_strategy;
			}
		}
		
		//==============================================================
		// implementation
		//==============================================================
		
		/// <summary> Load the contextualization strategy class.</summary>
		/// <param name="descriptor">the context descriptor
		/// </param>
		/// <param name="classloader">the classloader 
		/// </param>
		/// <returns> the strategy class
		/// </returns>
		private System.Type loadStrategyClass(ContextDescriptor descriptor)
		{
			//UPGRADE_NOTE: Final was removed from the declaration of 'strategy '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
			String strategy = m_descriptor.GetAttribute(ContextDescriptor.STRATEGY_KEY, null);
			if ((System.Object) strategy != null)
			{
				try
				{
					//UPGRADE_ISSUE: Method 'java.lang.ClassLoader.loadClass' was not converted. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1000_javalangClassLoader"'
					System.Type type = Type.GetType(strategy);
					if (Logger.IsDebugEnabled)
					{
						//UPGRADE_NOTE: Final was removed from the declaration of 'message '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
						String message = "context.strategy.custom" + " " + strategy;
						Logger.Debug(message);
					}
					return type;
				}
				//UPGRADE_NOTE: Exception 'java.lang.ClassNotFoundException' was converted to 'System.Exception' which has different behavior. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1100"'
				catch (System.Exception )
				{
					//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					String error = "context.strategy.custom.missing.error" + " " + strategy;
					throw new ModelException(error);
				}
			}
			else
			{
				try
				{
					System.Type type = Type.GetType(Apache.Avalon.Composition.Model.IContextModel_Fields.DEFAULT_STRATEGY_CLASSNAME);
					if (Logger.IsDebugEnabled)
					{
						String message = "context.strategy.avalon";
						Logger.Debug(message);
					}
					return type;
				}
				catch (System.Exception )
				{
					String error = "context.strategy.avalon.missing.error" + " " + Apache.Avalon.Composition.Model.IContextModel_Fields.DEFAULT_STRATEGY_CLASSNAME;
					throw new ModelException(error);
				}
			}
		}
		
		/// <summary> Creates a compoent context using a deployment context that 
		/// has been pre-populated with constom context entry models.
		/// 
		/// </summary>
		/// <param name="context">the deployment context
		/// </param>
		/// <param name="descriptor">the context descriptor
		/// </param>
		/// <param name="directive">the context directive
		/// </param>
		/// <returns> the context object compliant with the context casting
		/// constraints declared by the component type
		/// </returns>
		/// <exception cref=""> ModelException if an error occurs while attempting to 
		/// construct the context instance
		/// </exception>
		private IContext CreateComponentContext(IComponentContext context, ContextDescriptor descriptor, ContextDirective directive)
		{
			//UPGRADE_ISSUE: Class 'java.lang.ClassLoader' was not converted. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1000_javalangClassLoader"'
			System.Type type = loadContextClass(directive);
			validateCastingConstraint(descriptor, type);
			IContext base_Renamed = new DefaultContext(context);
			
			if (type.Equals(typeof(DefaultContext)))
				return base_Renamed;
			
			//
			// its a custom context object so we need to create it 
			// using the classic context object as the constructor 
			// argument
			//
			
			try
			{
				System.Reflection.ConstructorInfo constructor = type.GetConstructor(new System.Type[]{typeof(IContext)});
				//UPGRADE_ISSUE: Method 'java.lang.reflect.Constructor.newInstance' was not converted. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1000_javalangreflectConstructornewInstance_javalangObject[]"'
				return (IContext) constructor.Invoke(new System.Object[]{base_Renamed});
			}
			catch (System.MethodAccessException e)
			{
				//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
				String error = "context.non-compliance-constructor.error" + " " + type.FullName;
				throw new ModelException(error, e);
			}
			catch (System.Exception e)
			{
				//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
				String error = "context.custom-unexpected.error" + " " + type.FullName;
				throw new ModelException(error, e);
			}
		}
		
		/// <summary> Load the context implementation class.</summary>
		/// <param name="directive">the context directive (possibly null)
		/// </param>
		/// <param name="classLoader">the classloader 
		/// </param>
		/// <returns> the strategy class
		/// </returns>
		//UPGRADE_ISSUE: Class 'java.lang.ClassLoader' was not converted. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1000_javalangClassLoader"'
		private System.Type loadContextClass(ContextDirective directive)
		{
			if (directive == null)
			{
				return DEFAULT_CONTEXT_CLASS;
			}
			
			String typename = directive.Classname;
			if ((System.Object) typename == null)
			{
				return DEFAULT_CONTEXT_CLASS;
			}
			else
			{
				try
				{
					return Type.GetType(typename);
				}
				catch (System.Exception e)
				{
					String error = "Cannot load custom context implementation class: " + typename;
					throw new ModelException(error, e);
				}
			}
		}
		
		/// <summary> Validate that the context implememtation class implements
		/// any casting constraint declared or implied by the context 
		/// descriptor.
		/// 
		/// </summary>
		/// <param name="descriptor">the context descriptor
		/// </param>
		/// <param name="classLoader">the classloader
		/// </param>
		/// <param name="type">the context implementation class
		/// </param>
		/// <exception cref=""> if a validation failure occurs
		/// </exception>
		private void  validateCastingConstraint(ContextDescriptor descriptor, System.Type type)
		{
			System.Type castingClass = descriptor.ContextInterface;
			
			if (castingClass == null)
			{
				try
				{
					castingClass = typeof(IContext);
				}
				catch (System.Exception e)
				{
					String error = "Cannot load standard Avalon context interface class: " + typeof(IContext).FullName;
					throw new ModelException(error, e);
				}
			}
			
			if (!castingClass.IsAssignableFrom(type))
			{
				String error = "Supplied context implementation class: " + type.FullName + " does not implement the interface: " + castingClass.FullName + ".";
				throw new ModelException(error);
			}
		}
	}
}