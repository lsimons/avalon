// ============================================================================
//                   The Apache Software License, Version 1.1
// ============================================================================
// 
// Copyright (C) 2002-2003 The Apache Software Foundation. All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without modifica-
// tion, are permitted provided that the following conditions are met:
// 
// 1. Redistributions of  source code must  retain the above copyright  notice,
//    this list of conditions and the following disclaimer.
// 
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
// 
// 3. The end-user documentation included with the redistribution, if any, must
//    include  the following  acknowledgment:  "This product includes  software
//    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
//    Alternately, this  acknowledgment may  appear in the software itself,  if
//    and wherever such third-party acknowledgments normally appear.
// 
// 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"  
//    must not be used to endorse or promote products derived from this  software 
//    without  prior written permission. For written permission, please contact 
//    apache@apache.org.
// 
// 5. Products  derived from this software may not  be called "Apache", nor may
//    "Apache" appear  in their name,  without prior written permission  of the
//    Apache Software Foundation.
// 
// THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
// INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
// FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
// APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
// INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
// DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
// OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
// ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
// (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
// THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// 
// This software  consists of voluntary contributions made  by many individuals
// on  behalf of the Apache Software  Foundation. For more  information on the 
// Apache Software Foundation, please see <http://www.apache.org/>.
// ============================================================================

namespace Apache.Avalon.Castle.ManagementExtensions.Remote.Providers
{
	using System;
	using System.Collections.Specialized;

	using System.Runtime.Remoting;
	using System.Runtime.Remoting.Channels;
	using System.Runtime.Remoting.Channels.Http;

	using Apache.Avalon.Castle.ManagementExtensions.Remote.Server;
	using Apache.Avalon.Castle.ManagementExtensions.Remote.Client;

	/// <summary>
	/// Summary description for HttpChannelProvider.
	/// </summary>
	public class HttpChannelProvider : AbstractServerProvider
	{
		public HttpChannelProvider()
		{
		}

		protected override bool AcceptsChannel(String channel)
		{
			return "http".EndsWith(channel);
		}

		protected override bool AcceptsFormatter(String formatter)
		{
			return "binary".EndsWith(formatter) || "soap".EndsWith(formatter);
		}

		#region MProvider Members
		
		public override MConnector Connect(String url, System.Collections.Specialized.NameValueCollection properties)
		{
			String[] parts = StripUrl(url);

			String formatter = parts[2];
			String objectUri = parts[3];
			String objectUrl = null;
			
			HttpClientChannel channel = new HttpClientChannel();
			ChannelServices.RegisterChannel( channel );

			objectUrl = String.Format("{0}://{1}:{2}/{3}", 
				"http", GetHost(properties), GetPort(properties), objectUri);

			object ret = RemotingServices.Connect( typeof(MServer), objectUrl, null );

			return new MConnector( (MServer) ret, channel );
		}

		#endregion

		#region MServerProvider Members

		public override MConnectorServer CreateServer(String url, NameValueCollection properties, MServer server)
		{
			String[] parts = StripUrl(url);
			String formatter = parts[2];
			String objectUri = parts[3];
			
			HttpChannel channel = CreateChannel(formatter, properties, true);

			MConnectorServer connServer = null;

			if (server != null)
			{
				connServer = new MConnectorServer(server, objectUri);
			}
			else
			{
				connServer = new MConnectorServer(objectUri);
			}

			//connServer.Channel = channel;

			return connServer;
		}

		#endregion

		private HttpChannel CreateChannel(String formatter, NameValueCollection properties, bool createAsServer)
		{
			HttpChannel httpChannel = null;

			int portNum = GetPort(properties);
			
			bool alreadyRegistered = false;

			foreach(IChannel channel in ChannelServices.RegisteredChannels)
			{
				if (channel.ChannelName.Equals("http"))
				{
					HttpChannel item = (HttpChannel) channel;
					ChannelDataStore dataStore = (ChannelDataStore) item.ChannelData;
					// TODO: Check if is the same channel as the url specify
					
					httpChannel = item;
					
					alreadyRegistered = true;
					break;
				}
			}

			if (!alreadyRegistered)
			{
				if (createAsServer)
				{
					httpChannel = new HttpChannel(portNum);
				}
				else
				{
					httpChannel = new HttpChannel();
				}

				ChannelServices.RegisterChannel( httpChannel );
			}

			return httpChannel;
		}
	}
}
