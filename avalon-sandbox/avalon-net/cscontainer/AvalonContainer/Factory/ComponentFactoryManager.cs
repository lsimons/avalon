// ============================================================================
//                   The Apache Software License, Version 1.1
// ============================================================================
//
// Copyright (C) 2002-2003 The Apache Software Foundation. All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modifica-
// tion, are permitted provided that the following conditions are met:
//
// 1. Redistributions of  source code must  retain the above copyright  notice,
//    this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
//
// 3. The end-user documentation included with the redistribution, if any, must
//    include  the following  acknowledgment:  "This product includes  software
//    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
//    Alternately, this  acknowledgment may  appear in the software itself,  if
//    and wherever such third-party acknowledgments normally appear.
//
// 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"
//    must not be used to endorse or promote products derived from this  software
//    without  prior written permission. For written permission, please contact
//    apache@apache.org.
//
// 5. Products  derived from this software may not  be called "Apache", nor may
//    "Apache" appear  in their name,  without prior written permission  of the
//    Apache Software Foundation.
//
// THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
// INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
// FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
// APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
// INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
// DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
// OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
// ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
// (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
// THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
// This software  consists of voluntary contributions made  by many individuals
// on  behalf of the Apache Software  Foundation. For more  information on the
// Apache Software Foundation, please see <http://www.apache.org/>.
// ============================================================================

namespace Apache.Avalon.Container.Factory
{
	using System;
	using System.Collections;
	using System.Reflection;

	using Apache.Avalon.Framework;
	using Apache.Avalon.Container.Attributes;
	using Apache.Avalon.Container.Services;
	using Apache.Avalon.Container.Util;

	/// <summary>
	/// Summary description for ComponentFactoryManager.
	/// </summary>
	internal interface IComponentFactoryManager
	{
		IComponentFactory GetFactory(ComponentEntry entry);
	}

	internal class ComponentFactoryManager : IComponentFactoryManager, IInitializable, IDisposable
	{
		private Hashtable m_factoryPrototype;
		private ArrayList m_builders;

		#region IComponentFactoryManager Members

		public IComponentFactory GetFactory(ComponentEntry entry)
		{
			Type factoryType = (Type) m_factoryPrototype[entry.Lifestyle];

			if (factoryType == null)
			{
				throw new UnsupportedLifestyleException(entry.Lifestyle);
			}

			FactoryBuilder builder = ResolveFactoryBuilder(factoryType);

			return builder.GetFactory(entry.ComponentType);
		}

		#endregion

		#region IInitializable Members

		public void Initialize()
		{
			m_builders = new ArrayList();
			InitializeFactories();
		}

		#endregion

		private FactoryBuilder ResolveFactoryBuilder(Type factoryType)
		{
			object[] attributes = factoryType.GetCustomAttributes(
				typeof(CustomFactoryBuilderAttribute), true);

			if (attributes.Length == 0)
			{
				return new FactoryBuilder(factoryType);
			}

			CustomFactoryBuilderAttribute attribute = 
				(CustomFactoryBuilderAttribute) attributes[0];
			
			FactoryBuilder builder = (FactoryBuilder) 
				Activator.CreateInstance(attribute.FactoryBuilder);

			m_builders.Add(new WeakReference(builder));
			
			return builder;
		}

		private void InitializeFactories()
		{
			m_factoryPrototype = new Hashtable();
			Pair[] pairs = FindFactories();
			InitializePrototype(pairs);
		}

		private Pair[] FindFactories()
		{
			Assembly currentAssembly = Assembly.GetExecutingAssembly();

			return AssemblyUtil.FindTypesUsingAttribute(
				currentAssembly, typeof(LifestyleTargetAttribute), false);
		}

		private void InitializePrototype(Pair[] pairs)
		{
			foreach(Pair pair in pairs)
			{
				Type type = pair.First as Type;
				LifestyleTargetAttribute att = pair.Second as LifestyleTargetAttribute;

				Lifestyle style = att.Lyfestyle;
				
				System.Diagnostics.Debug.Assert(!m_factoryPrototype.Contains(style), "Lifestyle already handle by a factory.");

				// Simplest situation (one handler registered 
				// to each supported lifestyle)

				m_factoryPrototype[style] = type;
			}
		}

		#region IDisposable Members

		public void Dispose()
		{
			m_factoryPrototype.Clear();

			foreach(WeakReference weakRef in m_builders)
			{
				if (weakRef.IsAlive)
				{
					ContainerUtil.Shutdown(weakRef);
				}
			}
		}

		#endregion
	}
}
