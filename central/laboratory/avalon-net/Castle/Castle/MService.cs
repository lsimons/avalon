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

namespace Apache.Avalon.Castle
{
	using System;

	using Apache.Avalon.Castle.ManagementExtensions;

	public enum ManagedObjectState
	{
		Undefined,
		Created,
		Started,
		Stopped, 
		Destroyed
	}

	/// <summary>
	/// Summary description for MService.
	/// </summary>
	public interface MService : MRegistrationListener
	{
		ManagedObjectName ManagedObjectName
		{
			get;
		}

		ManagedObjectName ParentName
		{
			get;
		}

		ManagedObjectName[] Children
		{
			get;
		}

		ManagedObjectState ManagedObjectState
		{
			get;
		}

		void AddChild(ManagedObjectName childName);

		void RemoveChild(ManagedObjectName childName);

		void SetParent(ManagedObjectName parentName);

		void Create();

		void Start();

		void Stop();

		void Destroy();
	}
}
