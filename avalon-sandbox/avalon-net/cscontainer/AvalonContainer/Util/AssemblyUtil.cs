// Copyright 2004 Apache Software Foundation
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

namespace Apache.Avalon.Container.Util
{
	using System;
	using System.Reflection;
	using System.Collections;

	/// <summary>
	/// Summary description for AssemblyUtil.
	/// </summary>
	internal sealed class AssemblyUtil
	{
		private AssemblyUtil()
		{
		}

		public static Pair[] FindTypesUsingAttribute(Assembly assembly, Type attributeType, bool onlyPublicTypes)
		{
			ArrayList typesFound = new ArrayList();

			try
			{
				AssemblyName[] names = assembly.GetReferencedAssemblies();

				for(int i=0; i < names.Length; i++)
				{
					String dependsOn = names[i].FullName;
					// TODO: Handle assemblies dependencies gracefully
				}

				Type[] types = null;
				
				if (onlyPublicTypes)
				{
					types = assembly.GetExportedTypes();
				}
				else
				{
					types = assembly.GetTypes();
				}

				foreach(Type type in types)
				{
					if (!type.IsClass || type.IsAbstract)
					{
						continue;
					}

					object[] attrs = type.GetCustomAttributes(
						attributeType, false);

					if (attrs != null && attrs.Length == 1)
					{
						typesFound.Add(new Pair(type, attrs[0]));
					}
				}

			}
			catch(Exception e)
			{
				throw e;
			}

			Pair[] pairs = new Pair[typesFound.Count];
			typesFound.CopyTo(pairs);

			return pairs;
		}
	}
}
