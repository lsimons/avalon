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
	using Apache.Avalon.Meta;
	
	/// <summary> Default implementation of a the context entry constructor model.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.2 $ $Date: 2004/02/29 18:07:17 $
	/// </version>
	public class DefaultConstructorModel : DefaultEntryModel
	{
		//==============================================================
		// static
		//==============================================================
		
		//==============================================================
		// immutable state
		//==============================================================
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_directive '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private ConstructorDirective m_directive;
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_descriptor '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private EntryDescriptor m_descriptor;
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_context '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private IComponentContext m_context;
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_map '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private System.Collections.IDictionary m_map;
		
		//==============================================================
		// mutable state
		//==============================================================
		
		private System.Object m_value;
		
		//==============================================================
		// constructor
		//==============================================================
		
		/// <summary> Creation of a new context entry import model.
		/// 
		/// </summary>
		/// <param name="descriptor">the context entry descriptor
		/// </param>
		/// <param name="directive">the context entry directive
		/// </param>
		/// <param name="context">the containment context
		/// </param>
		/// <param name="map">a map of available context entries
		/// </param>
		public DefaultConstructorModel(EntryDescriptor descriptor, ConstructorDirective directive, 
			IComponentContext context, System.Collections.IDictionary map):base(descriptor)
		{
			if (directive == null)
			{
				throw new System.ArgumentNullException("directive");
			}
			if (context == null)
			{
				throw new System.ArgumentNullException("context");
			}
			m_descriptor = descriptor;
			m_directive = directive;
			m_context = context;
			m_map = map;
			
			Validate();
		}
		
		private void Validate()
		{
			String descriptorClassName = m_descriptor.Type.FullName;
			String directiveClassName = m_directive.Classname;
			validatePair(descriptorClassName, directiveClassName);
			Parameter[] params_Renamed = m_directive.Parameters;
			
			//
			// TODO:
			// wizz through and validate all of the parameter declarations
			// and make sure that constructors exist that match the sub-parameter
			// delcarations
			//
		}
		
		private void  validatePair(String descriptorClass, String directiveClass)
		{
			String key = m_descriptor.Key;
			
			System.Type target = null;
			try
			{
				target = Type.GetType(descriptorClass);
			}
			catch (System.Exception)
			{
				String error = "constructor.descriptor.unknown.error " + key + " " + descriptorClass;
				throw new ModelException(error);
			}
			
			System.Type source = null;
			try
			{
				source = Type.GetType(directiveClass);
			}
			catch (System.Exception)
			{
				String error = "constructor.directive.unknown.error" + " " + key + " " + directiveClass;
				throw new ModelException(error);
			}
			
			if (!target.IsAssignableFrom(source))
			{
				String error = "constructor.invalid-model.error" + " " + key + " " + descriptorClass + " " + directiveClass;
				throw new ModelException(error);
			}
		}
		
		
		//==============================================================
		// IEntryModel
		//==============================================================
		
		/// <summary> Return the context entry value.
		/// 
		/// </summary>
		/// <returns> the context entry value
		/// </returns>
		public override System.Object Value
		{
			get
			{
				if (m_value != null)
				{
					return m_value;
				}
			
				String target = m_descriptor.Key;
				System.Object object_Renamed = null;
				try
				{
					String typename = m_directive.Classname;
					String argument = m_directive.Argument;
					Parameter[] params_Renamed = m_directive.Parameters;
					System.Type type = getParameterClass(typename);
					object_Renamed = GetValue(type, argument, params_Renamed);
				}
					//UPGRADE_NOTE: Exception 'java.lang.Throwable' was converted to 'System.Exception' which has different behavior. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1100"'
				catch (System.Exception e)
				{
					//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					String error = "Cannot establish a constructed context entry for the key " + target + " due to a runtime failure.";
					throw new ModelException(error, e);
				}
			
				if (!m_descriptor.Volatile)
				{
					m_value = object_Renamed;
				}
			
				return object_Renamed;
			}
		}
		
		/// <summary> Return the context entry value.
		/// 
		/// </summary>
		/// <returns> the context entry value
		/// </returns>
		public virtual System.Object GetValue(Parameter p)
		{
			//UPGRADE_ISSUE: Class 'java.lang.ClassLoader' was not converted. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1000_javalangClassLoader"'
			String typename = p.Classname;
			String argument = p.Argument;
			Parameter[] params_Renamed = p.Parameters;
			System.Type type = getParameterClass(typename);
			return GetValue(type, argument, params_Renamed);
		}
		
		/// <summary> Return the derived parameter value.</summary>
		/// <param name="loader">the classloader to use
		/// </param>
		/// <param name="type">the constructor class
		/// </param>
		/// <param name="argument">a single string constructor argument
		/// </param>
		/// <param name="parameters">an alternative sequence of arguments
		/// </param>
		/// <returns> the value
		/// </returns>
		/// <exception cref=""> ModelException if the parameter value cannot be resolved
		/// </exception>
		public virtual System.Object GetValue(System.Type type, String argument, Parameter[] parameters)
		{
			//
			// if the parameter contains a text argument then check if its a reference
			// to a map entry (in the form"${<key>}" ), otherwise its a simple constructor
			// case with a single string paremeter
			//
			
			if (parameters.Length == 0)
			{
				if ((System.Object) argument == null)
				{
					return getNullArgumentConstructorValue(type);
				}
				else
				{
					return getSingleArgumentConstructorValue( type, argument);
				}
			}
			else
			{
				return GetMultiArgumentConstructorValue(type, parameters);
			}
		}
		
		private System.Object GetMultiArgumentConstructorValue(System.Type type, Parameter[] parameters)
		{
			//
			// getting here means we are dealing with 0..n types parameter constructor where the
			// parameters are defined by the nested parameter definitions
			//
			
			if (parameters.Length == 0)
			{
				try
				{
					return Activator.CreateInstance(type);
				}
				catch (System.UnauthorizedAccessException e)
				{
					String error = "Cannot access null constructor for the class: '" + type.FullName + "'.";
					throw new ModelException(error, e);
				}
				catch (System.Exception e)
				{
					String error = "Unable to instantiate instance of class: " + type.FullName;
					throw new ModelException(error, e);
				}
			}
			else
			{
				System.Type[] params_Renamed = new System.Type[parameters.Length];
				for (int i = 0; i < parameters.Length; i++)
				{
					String typename = parameters[i].Classname;
					try
					{
						params_Renamed[i] = Type.GetType(typename);
					}
					catch (System.Exception e)
					{
						String error = "Unable to resolve sub-parameter class: " + typename + " for the parameter " + type.FullName;
						throw new ModelException(error, e);
					}
				}
				
				System.Object[] values = new System.Object[parameters.Length];
				for (int i = 0; i < parameters.Length; i++)
				{
					Parameter p = parameters[i];
					String typename = p.Classname;
					try
					{
						values[i] = GetValue(p);
					}
					catch (System.Exception e)
					{
						String error = "Unable to instantiate sub-parameter for value: " + typename + " inside the parameter " + type.FullName;
						throw new ModelException(error, e);
					}
				}
				System.Reflection.ConstructorInfo constructor = null;
				try
				{
					constructor = type.GetConstructor(params_Renamed);
				}
				catch (System.MethodAccessException e)
				{
					String error = "Supplied parameters for " + type.FullName + " do not match the available class constructors.";
					throw new ModelException(error, e);
				}
				
				try
				{
					return constructor.Invoke(values);
				}
				catch (System.UnauthorizedAccessException e)
				{
					String error = "Cannot access multi-parameter constructor for the class: '" + type.FullName + "'.";
					throw new ModelException(error, e);
				}
				catch (System.Exception e)
				{
					String error = "Unable to instantiate an instance of a multi-parameter constructor for class: '" + type.FullName + "'.";
					throw new ModelException(error, e);
				}
			}
		}
		
		private System.Object getNullArgumentConstructorValue(System.Type type)
		{
			try
			{
				//UPGRADE_TODO: Method 'java.lang.Class.newInstance' was converted to 'SupportClass.CreateNewInstance' which has a different behavior. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1073"'
				//UPGRADE_WARNING: Method 'java.lang.Class.newInstance' was converted to 'SupportClass.CreateNewInstance' which may throw an exception. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1101"'
				return Activator.CreateInstance(type);
			}
			//UPGRADE_NOTE: Exception 'java.lang.InstantiationException' was converted to 'System.Exception' which has different behavior. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1100"'
			catch (System.Exception e)
			{
				//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
				String error = "Unable to instantiate instance of class: " + type.FullName;
				throw new ModelException(error, e);
			}
		}
		
		//UPGRADE_ISSUE: Class 'java.lang.ClassLoader' was not converted. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1000_javalangClassLoader"'
		private System.Object getSingleArgumentConstructorValue(System.Type type, String argument)
		{
			if (argument.StartsWith("${"))
			{
				if (argument.EndsWith("}"))
				{
					//UPGRADE_NOTE: Final was removed from the declaration of 'key '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					String key = argument.Substring(2, (argument.Length - 1) - (2));
					System.Object value_Renamed = null;
					try
					{
						return m_context.Resolve(key);
					}
					catch (ContextException)
					{
						//UPGRADE_TODO: Method 'java.util.Map.get' was converted to 'System.Collections.IDictionary.Item' which has a different behavior. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1073_javautilMapget_javalangObject"'
						value_Renamed = m_map[key];
						if (value_Renamed != null)
						{
							return value_Renamed;
						}
						else
						{
							//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
							String error = "Unresolvable primative context value: '" + key + "'.";
							throw new ModelException(error);
						}
					}
				}
				else
				{
					//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					String error = "Illegal format for context reference: '" + argument + "'.";
					throw new ModelException(error);
				}
			}
			else
			{
				//
				// the argument is a simple type that takes a single String value
				// as a constructor argument
				//
				
				try
				{
					//UPGRADE_NOTE: Final was removed from the declaration of 'params '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					System.Type[] params_Renamed = new System.Type[]{typeof(String)};
					System.Reflection.ConstructorInfo constructor = type.GetConstructor(params_Renamed);
					//UPGRADE_NOTE: Final was removed from the declaration of 'values '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					System.Object[] values = new System.Object[]{argument};
					//UPGRADE_ISSUE: Method 'java.lang.reflect.Constructor.newInstance' was not converted. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1000_javalangreflectConstructornewInstance_javalangObject[]"'
					return constructor.Invoke(values);
				}
				catch (System.MethodAccessException)
				{
					//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					String error = "Class: '" + type.FullName + "' does not implement a single string argument constructor.";
					throw new ModelException(error);
				}
				//UPGRADE_NOTE: Exception 'java.lang.InstantiationException' was converted to 'System.Exception' which has different behavior. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1100"'
				catch (System.Exception e)
				{
					//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					String error = "Unable to instantiate instance of class: " + type.FullName + " with the single argument: '" + argument + "'";
					throw new ModelException(error, e);
				}
			}
		}
		
		/// <summary> Return the typename of the parameter implementation to use.</summary>
		/// <param name="loader">the classloader to use
		/// </param>
		/// <returns> the parameter class
		/// </returns>
		/// <exception cref=""> ModelException if the parameter class cannot be resolved
		/// </exception>
		//UPGRADE_ISSUE: Class 'java.lang.ClassLoader' was not converted. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1000_javalangClassLoader"'
		internal virtual System.Type getParameterClass(String typename)
		{
			try
			{
				//UPGRADE_ISSUE: Method 'java.lang.ClassLoader.loadClass' was not converted. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1000_javalangClassLoader"'
				return Type.GetType(typename);
			}
			//UPGRADE_NOTE: Exception 'java.lang.ClassNotFoundException' was converted to 'System.Exception' which has different behavior. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1100"'
			catch (System.Exception e)
			{
				if (typename.Equals("int"))
				{
					return typeof(int);
				}
				else if (typename.Equals("short"))
				{
					return typeof(short);
				}
				else if (typename.Equals("long"))
				{
					return typeof(long);
				}
				else if (typename.Equals("sbyte"))
				{
					return typeof(sbyte);
				}
				else if (typename.Equals("double"))
				{
					return typeof(double);
				}
				else if (typename.Equals("sbyte"))
				{
					return typeof(sbyte);
				}
				else if (typename.Equals("float"))
				{
					return typeof(float);
				}
				else if (typename.Equals("char"))
				{
					return typeof(char);
				}
				else if (typename.Equals("bool"))
				{
					return typeof(bool);
				}
				else
				{
					throw new ModelException("Could not locate the parameter implemetation for class: '" + typename + "'.", e);
				}
			}
		}
	}
}