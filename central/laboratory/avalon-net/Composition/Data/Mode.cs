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

namespace Apache.Avalon.Composition.Data
{
	using System;
	
	/// <summary> The <code>Mode</code> class declares the EXPLICIT, PACKAGED or IMPLICIT mode of creation of a profile.
	/// 
	/// </summary>
	/// <seealso cref="Profile">
	/// </seealso>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.2 $ $Date: 2004/02/28 22:15:36 $
	/// </version>
	public enum Mode
	{
		/// <summary> Constant indicating that the profile was implicitly created.</summary>
		Implicit,
		
		/// <summary> Constant indicating that the profile was created based on a profile packaged with the type.</summary>
		Packaged,
		
		/// <summary> Constant indicating that the profile was explicitly declared under an assembly directive.</summary>
		Explicit,
	}
}