/* 
* Copyright 2003-2004 The Apache Software Foundation
* Licensed  under the  Apache License,  Version 2.0  (the "License");
* you may not use  this file  except in  compliance with the License.
* You may obtain a copy of the License at 
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed  under the  License is distributed on an "AS IS" BASIS,
* WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
* implied.
* 
* See the License for the specific language governing permissions and
* limitations under the License.
*/

namespace Apache.Avalon.Composition.Model.Default
{
	using System;
	using System.Threading;

	using Apache.Avalon.Framework;
	
	/// <summary> Runnable deployment thread that handles the commissioning of an 
	/// arbitary number of commissionable instances.  The commissioner maintains a 
	/// list of commissioning requests which are queued on a first come first 
	/// serve basis.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/29 18:07:17 $
	/// </version>
	/// <seealso cref="CommissionRequest">
	/// </seealso>
	class Commissioner
	{
		private void  InitBlock()
		{
			m_queue = new SimpleFIFO();
		}
		//------------------------------------------------------------
		// static
		//------------------------------------------------------------
		
		private static int m_ThreadCounter = 0;
		
		//------------------------------------------------------------
		// immutable state
		//------------------------------------------------------------
		
		private ILogger m_logger;
		
		private SimpleFIFO m_queue;
		
		private System.String m_message;
		
		private bool m_flag;
		
		//------------------------------------------------------------
		// mutable static
		//------------------------------------------------------------
		
		private Thread m_thread;
		
		//------------------------------------------------------------
		// constructor
		//------------------------------------------------------------
		
		internal Commissioner(ILogger logger, bool flag)
		{
			InitBlock();
			m_logger = logger;
			m_ThreadCounter++;
			System.String name = "Commissioner [" + m_ThreadCounter + "]";
			m_flag = flag;
			if (flag)
			{
				m_message = "commissioning";
			}
			else
			{
				m_message = "decommissioning";
			}
			m_thread = new Thread( new System.Threading.ThreadStart(Run) );
			m_thread.Start();
		}
		
		//------------------------------------------------------------
		// implementation
		//------------------------------------------------------------
		
		/// <summary> Commissions the given Commissonable, and allows a maximum time
		/// for commissioning/decommissioning to complete.
		/// 
		/// </summary>
		/// <param name="model">the deployment model
		/// 
		/// @throws CommissioningException if the deployment was not 
		/// completed within the timeout deadline and interuption
		/// of the deployment was successful
		/// @throws FatalCommissioningException if the deployment was not 
		/// completed within the timeout deadline and interuption
		/// of the deployment was not successful
		/// @throws Exception any Exception or Error thrown within the
		/// deployment of the component is forwarded to the caller.
		/// @throws InvocationTargetException if the deployment throws a
		/// Throwable subclass that is NOT of type Exception or Error.
		/// 
		/// </param>
		internal virtual void  commission(IDeploymentModel model)
		{
			if (null == model)
			{
				throw new System.ArgumentNullException("model");
			}
			
			if (null != m_thread)
			{
				if (m_logger.IsDebugEnabled)
				{
					if (model is IContainmentModel)
					{
						m_logger.Debug(m_message + " container [" + model.Name + "]");
					}
					else
					{
						m_logger.Debug(m_message + " component [" + model.Name + "]");
					}
				}
				
				CommissionRequest request = new CommissionRequest(model, m_thread);
				m_queue.put(request);
				long t = request.waitForCompletion();
				if (m_logger.IsDebugEnabled)
				{
					m_logger.Debug(m_message + " of [" + model.Name + "] completed in " + t + " milliseconds");
				}
			}
			else
			{
				System.String warning = "Ignoring " + m_message + " request on a disposed commissioner.";
				m_logger.Warn(warning);
			}
		}
		
		/// <summary> Disposal of the Commissioner.
		/// The Commissioner allocates a deployment thread, which needs to be
		/// disposed of before releasing the Commissioner reference.
		/// 
		/// </summary>
		internal virtual void  dispose()
		{
			if (m_logger.IsDebugEnabled)
			{
				m_logger.Debug("disposal");
			}
			if (null != m_thread)
			{
				m_thread.Interrupt();
			}
		}
		
		public virtual void Run()
		{
			if (m_logger.IsDebugEnabled)
			{
				m_logger.Debug(m_message + " thread started");
			}
			try
			{
				while (true)
				{
					CommissionRequest request = (CommissionRequest) m_queue.get_Renamed();
					IDeploymentModel model = request.DeploymentModel;
					try
					{
						if (m_flag)
						{
							model.Commission();
						}
						else
						{
							model.Decommission();
						}
						request.done();
					}
					catch (System.Threading.ThreadInterruptedException )
					{
						request.interrupted();
					}
					catch (System.Exception e)
					{
						request.exception(e);
					}
				}
			}
			catch (System.Threading.ThreadInterruptedException)
			{
				// ignore, part of dispose;
			}
			m_thread = null;
		}
	}
}