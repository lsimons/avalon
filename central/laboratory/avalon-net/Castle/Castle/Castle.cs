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
	using System.IO;
	using System.Threading;

	using ILogger = Apache.Avalon.Framework.ILogger;

	/// <summary>
	/// Summary description for Castle.
	/// </summary>
	public class Castle
	{
		protected CastleOptions m_options;

		protected ILogger m_logger = Logger.LoggerFactory.GetLogger("Castle");

		public Castle(CastleOptions options)
		{
			if (options == null)
			{
				throw new ArgumentNullException("options");
			}

			m_options = options;
		}

		public void Start()
		{
			m_logger.Info("Castle : Starting at {0} {1}", DateTime.Now.ToShortTimeString(), DateTime.Now.ToShortDateString());

			CastleLoader loader = new CastleLoader();

			try
			{
				InitializeDomainHooks();

				loader.Start(m_options);

				if (!m_options.NoThreadJoin)
				{
					// Thread.CurrentThread.Join();
					Console.In.ReadLine();
				}
			}
			catch(Exception ex)
			{
				PrintDetailErrorInformation(ex);

				// TODO: Shall we rethrow the exception to parent?

				throw ex;
			}
			finally
			{
				loader.Stop();
			}

			m_logger.Info("Castle : Service exiting at {0} {1}", 
				DateTime.Now.ToShortTimeString(), 
				DateTime.Now.ToShortDateString());
		}

		/// <summary>
		/// Recursively prints Exception information
		/// </summary>
		/// <param name="ex"></param>
		private void PrintDetailErrorInformation(Exception ex)
		{
			TextWriter writer = Console.Out;

			writer.WriteLine( "--------------------------------------------------------------------");
			writer.WriteLine( "Exception {0} at {1} occurred.", ex.GetType(), ex.TargetSite.ToString() );
			writer.WriteLine( );
			writer.WriteLine( "  Message: {0}", ex.Message );
			writer.WriteLine( );
			writer.WriteLine( "  Stack Trace:" );
			writer.WriteLine( ex.StackTrace );
			writer.WriteLine( );

			if (ex.InnerException != null)
			{
				PrintDetailErrorInformation(ex.InnerException);
			}
		}

		private void InitializeDomainHooks()
		{
			// TODO: Find out a way to handle CTRL + C for a correct clean up
			AppDomain.CurrentDomain.DomainUnload += new EventHandler(DomainUnload);
			AppDomain.CurrentDomain.UnhandledException += new UnhandledExceptionEventHandler(UnhandledException);
			AppDomain.CurrentDomain.ProcessExit += new EventHandler(ProcessExit);
		}

		private void DomainUnload(object sender, EventArgs e)
		{
			m_logger.Info("DomainUnload catched!");
		}

		private void UnhandledException(object sender, UnhandledExceptionEventArgs e)
		{
			m_logger.Info("UnhandledException catched!");
		}

		private void ProcessExit(object sender, EventArgs e)
		{
			m_logger.Info("ProcessExit catched!");
		}
	}
}
