// Copyright 2004 The Apache Software Foundation
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

namespace Apache.Avalon.Castle.MicroKernel.Subsystems.Events
{
	using System;

	/// <summary>
	/// 
	/// </summary>
	public class EventManagerData : EventArgs
	{
		private String m_componentName;
		private Type m_service;
		private Type m_implementation;
		private object m_instance;

		public EventManagerData( String componentName, Type service, Type implementation )
		{
			m_componentName = componentName;
			m_service = service;
			m_implementation = implementation;
		}

		public EventManagerData( String componentName, Type service, Type implementation, object componentInstance ) : 
			this( componentName, service, implementation )
		{
			m_instance = componentInstance;
		}

		public String ComponentName
		{
			get
			{
				return m_componentName;
			}
		}

		public Type Service
		{
			get
			{
				return m_service;
			}
		}

		public Type Implementation
		{
			get
			{
				return m_implementation;
			}
		}

		public Object Instance
		{
			get
			{
				return m_instance;
			}
			set
			{
				AssertUtil.ArgumentNotNull( value, "value" );
				m_instance = value;
			}
		}
	}
}
