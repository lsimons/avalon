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
	using System.Reflection;
	using System.Collections;

	/// <summary>
	/// Summary description for MemberResolver.
	/// </summary>
	public class MemberResolver
	{
		private Hashtable attributes = 
			new Hashtable(CaseInsensitiveHashCodeProvider.Default, CaseInsensitiveComparer.Default);
		
		private Hashtable operations = 
			new Hashtable(CaseInsensitiveHashCodeProvider.Default, CaseInsensitiveComparer.Default);

		public MemberResolver(ManagementInfo info, Type target)
		{
			ResolveAttributes(info, target);
			ResolveOperations(info, target);
		}

		public MethodInfo[] Methods
		{
			get
			{
				MethodInfo[] methods = new MethodInfo[operations.Count];
				int index = 0;
				foreach(MethodInfo info in operations.Values)
				{
					methods[index++] = info;
				}
				return methods;
			}
		}

		public MethodInfo GetMethod(String methodName)
		{
			return (MethodInfo) operations[methodName];
		}

		public PropertyInfo GetProperty(String propertyName)
		{
			return (PropertyInfo) attributes[propertyName];
		}

		private void ResolveAttributes(ManagementInfo info, Type target)
		{
			foreach(ManagementObject item in info.Attributes)
			{
				PropertyInfo property = target.GetProperty( 
					item.Name, 
					BindingFlags.Public|BindingFlags.Instance );

				attributes.Add( item.Name, property );
			}
		}

		private void ResolveOperations(ManagementInfo info, Type target)
		{
			foreach(ManagementObject item in info.Operations)
			{
				MethodInfo method = target.GetMethod( 
					item.Name, 
					BindingFlags.Public|BindingFlags.Instance );

				operations.Add( BuildOperationName(item.Name, method.GetParameters()), method );
			}
		}

		public static String BuildOperationName(String name, ParameterInfo[] args)
		{
			String result = String.Format("{0}[{1}]", name, GetArraySig(args));
			return result;
		}

		public static String BuildOperationName(String name, Type[] args)
		{
			String result = String.Format("{0}[{1}]", name, GetArraySig(args));
			return result;
		}

		private static String GetArraySig(ParameterInfo[] args)
		{
			if (args == null)
			{
				return String.Empty;
			}

			StringBuilder sb = new StringBuilder();
			
			foreach(ParameterInfo parameter in args)
			{
				if (sb.Length != 0)
				{
					sb.Append(",");
				}
				sb.Append(parameter.ParameterType);
			}

			return sb.ToString();
		}

		private static String GetArraySig(Type[] args)
		{
			if (args == null)
			{
				return String.Empty;
			}

			StringBuilder sb = new StringBuilder();
			
			foreach(Type parameter in args)
			{
				if (sb.Length != 0)
				{
					sb.Append(",");
				}
				sb.Append(parameter);
			}

			return sb.ToString();
		}
	}
}