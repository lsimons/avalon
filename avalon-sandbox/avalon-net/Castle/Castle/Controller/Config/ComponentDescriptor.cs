// Copyright 2003-2004 The Apache Software Foundation
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

namespace Apache.Avalon.Castle.Controller.Config
{
	using System;

	/// <summary>
	/// Summary description for ComponentDescriptor.
	/// </summary>
	public class ComponentDescriptor
	{
		String typeName;
		String name;
		AttributeDescriptorCollection attributes;
		DependencyDescriptorCollection dependencies;

		public ComponentDescriptor(String typeName, String name)
		{
			this.typeName = typeName;
			this.name = name;
		}

		public String Typename
		{
			get
			{
				return typeName;
			}
		}

		public String Name
		{
			get
			{
				return name;
			}
		}

		public AttributeDescriptorCollection Attributes 
		{
			get
			{
				if (attributes == null)
				{
					attributes = new AttributeDescriptorCollection();
				}

				return attributes;
			}
		}

		public DependencyDescriptorCollection Dependencies 
		{
			get
			{
				if (dependencies == null)
				{
					dependencies = new DependencyDescriptorCollection();
				}

				return dependencies;
			}
		}
	}
}
