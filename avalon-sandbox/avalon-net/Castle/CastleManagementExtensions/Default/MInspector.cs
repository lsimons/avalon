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

namespace Apache.Avalon.Castle.ManagementExtensions.Default
{
	using System;
	using System.Reflection;

	enum ComponentType
	{
		None,
		Standard,
		Dynamic
	}

	/// <summary>
	/// Summary description for MInspector.
	/// </summary>
	class MInspector
	{
		private MInspector()
		{
		}

		/// <summary>
		/// TODO: Summary
		/// </summary>
		/// <param name="instance"></param>
		/// <returns></returns>
		public static ComponentType Inspect(Object instance)
		{
			Type target = instance.GetType();

			if (typeof(MDynamicSupport).IsAssignableFrom(target))
			{
				return ComponentType.Dynamic;
			}
			else
			{
				if (target.IsDefined( typeof(ManagedComponentAttribute), true ))
				{
					return ComponentType.Standard;
				}
			}

			return ComponentType.None;
		}

		/// <summary>
		/// TODO: Summary
		/// </summary>
		/// <param name="instance"></param>
		/// <returns></returns>
		public static ManagementInfo BuildInfoFromStandardComponent(Object instance)
		{
			ManagementInfo info = new ManagementInfo();

			SetupManagedComponent(info, instance);
			SetupManagedOperations(info, instance);
			SetupManagedAttributes(info, instance);

			return info;
		}

		private static void SetupManagedComponent(ManagementInfo info, Object instance)
		{
			Object[] componentAtt = 
				instance.GetType().GetCustomAttributes( 
					typeof(ManagedComponentAttribute), true );

			if (componentAtt == null || componentAtt.Length == 0)
			{
				throw new StandardComponentException("Standard component must use ManagedComponentAttribute attribute.");
			}

			ManagedComponentAttribute compAtt = componentAtt[0] as ManagedComponentAttribute;

			info.Description = compAtt.Description;
		}
		
		private static void SetupManagedOperations(ManagementInfo info, Object instance)
		{
			MethodInfo[] methods = instance.GetType().GetMethods(BindingFlags.Public|BindingFlags.Instance);

			foreach(MethodInfo minfo in methods)
			{
				if (minfo.IsDefined( typeof(ManagedOperationAttribute), true ))
				{
					object[] atts = minfo.GetCustomAttributes( typeof(ManagedOperationAttribute), true );

					ManagedOperationAttribute att = (ManagedOperationAttribute) atts[0];

					ManagementOperation operation = new ManagementOperation(minfo.Name, att.Description);

					info.Operations.Add(operation);
				}
			}
		}

		private static void SetupManagedAttributes(ManagementInfo info, Object instance)
		{
			PropertyInfo[] properties = instance.GetType().GetProperties(BindingFlags.Public|BindingFlags.Instance);

			foreach(PropertyInfo minfo in properties)
			{
				if (minfo.IsDefined( typeof(ManagedAttributeAttribute), true ))
				{
					object[] atts = minfo.GetCustomAttributes( typeof(ManagedAttributeAttribute), true );

					ManagedAttributeAttribute att = (ManagedAttributeAttribute) atts[0];

					ManagementAttribute attribute = new ManagementAttribute(minfo.Name, att.Description, minfo.PropertyType);

					info.Attributes.Add(attribute);
				}
			}
		}
	}
}
