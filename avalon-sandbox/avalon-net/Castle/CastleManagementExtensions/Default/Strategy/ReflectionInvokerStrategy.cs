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

namespace Apache.Avalon.Castle.ManagementExtensions.Default.Strategy
{
	using System;
	using System.Text;
	using System.Collections;
	using System.Reflection;

	/// <summary>
	/// Summary description for ReflectionInvokerStrategy.
	/// </summary>
	public class ReflectionInvokerStrategy : InvokerStrategy
	{
		public ReflectionInvokerStrategy()
		{
		}

		#region InvokerStrategy Members

		public MDynamicSupport Create(Object instance)
		{
			ManagementInfo info = MInspector.BuildInfoFromStandardComponent(instance);

			return new ReflectedDynamicSupport(
				instance, info, 
				new MemberResolver(info, instance.GetType()));
		}

		#endregion
	}

	/// <summary>
	/// 
	/// </summary>
	class ReflectedDynamicSupport : MDynamicSupport
	{
		private Object instance;
		private ManagementInfo info;
		private MemberResolver resolver;

		public ReflectedDynamicSupport(Object instance, ManagementInfo info, MemberResolver resolver)
		{
			this.info     = info;
			this.instance = instance;
			this.resolver = resolver;
		}

		#region MDynamicSupport Members

		/// <summary>
		/// TODO: Summary
		/// </summary>
		/// <param name="action"></param>
		/// <param name="args"></param>
		/// <param name="signature"></param>
		/// <returns></returns>
		public Object Invoke(String action, Object[] args, Type[] signature)
		{
			if (action == null)
			{
				throw new ArgumentNullException("action");
			}

			ManagementOperation operation = (ManagementOperation) info.Operations[action];

			if (operation == null)
			{
				throw new InvalidOperationException(String.Format("Operation {0} doesn't exists.", action));
			}

			MethodInfo method = resolver.GetMethod(MemberResolver.BuildOperationName(action, signature));

			return method.Invoke(instance, args);
		}

		/// <summary>
		/// TODO: Summary
		/// </summary>
		/// <param name="name"></param>
		/// <returns></returns>
		public Object GetAttributeValue(String name)
		{
			if (name == null)
			{
				throw new ArgumentNullException("name");
			}

			ManagementAttribute attribute = (ManagementAttribute) info.Attributes[name];

			if (attribute == null)
			{
				throw new InvalidOperationException(String.Format("Attribute {0} doesn't exists.", name));
			}

			PropertyInfo property = resolver.GetProperty(attribute.Name);
			
			if (!property.CanRead)
			{
				throw new InvalidOperationException(String.Format("Attribute {0} can't be read.", name));
			}

			MethodInfo getMethod = property.GetGetMethod();

			return getMethod.Invoke(instance, null);
		}

		/// <summary>
		/// TODO: Summary
		/// </summary>
		/// <param name="name"></param>
		/// <param name="value"></param>
		public void SetAttributeValue(String name, Object value)
		{
			if (name == null)
			{
				throw new ArgumentNullException("name");
			}

			ManagementAttribute attribute = (ManagementAttribute) info.Attributes[name];

			if (attribute == null)
			{
				throw new InvalidOperationException(String.Format("Attribute {0} doesn't exists.", name));
			}
			
			PropertyInfo property = resolver.GetProperty(attribute.Name);
				
			if (!property.CanWrite)
			{
				throw new InvalidOperationException(String.Format("Attribute {0} is read-only.", name));
			}

			MethodInfo setMethod = property.GetSetMethod();

			setMethod.Invoke(instance, new object[] { value } );
		}

		/// <summary>
		/// TODO: Summary
		/// </summary>
		public ManagementInfo Info
		{
			get
			{
				return info;
			}
		}

		#endregion
	}
}
