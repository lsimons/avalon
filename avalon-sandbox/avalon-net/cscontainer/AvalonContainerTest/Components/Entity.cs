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

namespace Apache.Avalon.Container.Test
{
	using System;
	using Apache.Avalon.Framework;

	/// <summary>
	/// Summary description for IEntity.
	/// </summary>
	public interface IEntity
	{
		String Name
		{
			get;
			set;
		}

		int ID
		{
			get;
		}
	}

	/// <summary>
	/// IEntity implementation.
	/// </summary>
	[AvalonService( typeof( IEntity ) )]
	[AvalonComponent( "Entity", Lifestyle.Transient )]
	public class Entity : IEntity
	{
		private String m_name;
		private int m_id;

		public Entity()
		{
			System.Security.Cryptography.RandomNumberGenerator generator = 
				System.Security.Cryptography.RNGCryptoServiceProvider.Create();

			byte[] intBytes = new byte[4];
			generator.GetBytes(intBytes);

			m_id = BitConverter.ToInt32(intBytes, 0);
		}

		#region IEntity Members

		public String Name
		{
			get
			{
				return m_name;
			}
			set
			{
				m_name = value;
			}
		}

		public int ID
		{
			get
			{
				return m_id;
			}
		}

		#endregion
	}

}
