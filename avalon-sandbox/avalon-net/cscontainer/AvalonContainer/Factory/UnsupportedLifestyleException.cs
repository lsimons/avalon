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

namespace Apache.Avalon.Container.Factory
{
	using System;
	using Apache.Avalon.Framework;

	/// <summary>
	/// Summary description for UnsupportedLifestyleException.
	/// </summary>
	public class UnsupportedLifestyleException : ApplicationException
	{
		public UnsupportedLifestyleException()
		{
		}

		public UnsupportedLifestyleException(String message) : base(message)
		{
		}

		public UnsupportedLifestyleException(Lifestyle lifestyle) : 
			base(String.Format("The requested lifestyle {0} is not supported as there is no " 
			+ "Component Factory registered that supports it.", lifestyle.ToString()))
		{
		}
	}
}
