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

namespace Apache.Avalon.Castle.Controller.Config
{
	using System;
	using System.Collections;

	/// <summary>
	/// Summary description for AttributeDescriptorCollection.
	/// </summary>
	internal class AttributeDescriptorCollection : CollectionBase
	{
		public AttributeDescriptorCollection()
		{
		}

		public void Add(AttributeDescriptor desc)
		{
			InnerList.Add(desc);
		}

		public AttributeDescriptor this [int index]
		{
			get
			{
				return InnerList[index] as AttributeDescriptor;
			}
		}
	}
}
