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

namespace Apache.Avalon.Castle.MicroKernel.Test.Components
{
	using System;

	/// <summary>
	/// Contract for a MailService
	/// </summary>
	public interface IMailService
	{
		/// <summary>
		/// Sends an email.
		/// </summary>
		/// <param name="from">From</param>
		/// <param name="to">To</param>
		/// <param name="subject">Message's subject</param>
		/// <param name="message">Message's contents</param>
		void Send(String from, String to, String subject, String message);
	}
}