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

namespace Apache.Avalon.Repository
{
	using System;

	/// <summary>
	/// Summary description for Artifact.
	/// </summary>
	[Serializable]
	public class Artifact
	{
		public static readonly String SEP = "/";

		public static readonly String GROUP_KEY = "avalon.artifact.group";
		public static readonly String NAME_KEY = "avalon.artifact.name";
		public static readonly String VERSION_KEY = "avalon.artifact.version";
		public static readonly String TYPE_KEY = "avalon.artifact.type";
	}
}
