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

	using Apache.Avalon.Composition.Model;
	
	/// <summary> A deployment request handler.</summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/29 18:07:17 $
	/// </version>
	class CommissionRequest
	{
		//------------------------------------------------------------
		// immutable state
		//------------------------------------------------------------
		
		private IDeploymentModel m_model;
		private Thread m_thread;
		// private long m_timeout;
		
		//------------------------------------------------------------
		// mutable state
		//------------------------------------------------------------
		
		private bool m_completed;
		private bool m_interrupted;
		private System.Exception m_exception;
		
		//------------------------------------------------------------
		// constructor
		//------------------------------------------------------------
		
		/// <summary> Creation of a new commission request.</summary>
		/// <param name="model">the model to commission
		/// </param>
		/// <param name="thread">the deployment thread
		/// </param>
		internal CommissionRequest(IDeploymentModel model, Thread thread)
		{
			m_model = model;
			m_completed = false;
			m_interrupted = false;
			m_exception = null;
			m_thread = thread;
		}
		
		//------------------------------------------------------------
		// implementation
		//------------------------------------------------------------
		
		/// <summary> Return the deployment model that it the subject of the 
		/// commission request.
		/// </summary>
		/// <returns> the model
		/// </returns>
		internal virtual IDeploymentModel DeploymentModel
		{
			get
			{
				return m_model;
			}
			
		}

		internal virtual long waitForCompletion()
		{
			long t1 = (System.DateTime.Now.Ticks - 621355968000000000) / 10000;
			lock (this)
			{
				long timeout = DeploymentModel.DeploymentTimeout;
				System.Threading.Monitor.Wait(this, TimeSpan.FromMilliseconds(timeout)); // wait for commission/decommission
				processException();
				if (m_completed)
				{
					long t2 = (System.DateTime.Now.Ticks - 621355968000000000) / 10000;
					return t2 - t1;
				}
				m_thread.Interrupt();
				System.Threading.Monitor.Wait(this, TimeSpan.FromMilliseconds(timeout)); // wait for shutdown
				processException();
				if (m_interrupted || m_completed)
				{
					System.String error = "target: [" + m_model + "] did not respond within the timeout period: [" + timeout + "] and was successfully interrupted.";
					throw new CommissioningException(error);
				}
				else
				{
					System.String error = "target: [" + m_model + "] did not respond within the timeout period: [" + timeout + "] and failed to respond to an interrupt.";
					throw new FatalCommissioningException(error);
				}
			}
		}
		
		private void  processException()
		{
			if (m_exception != null)
			{
				if (m_exception is System.ApplicationException)
				{
					throw (System.ApplicationException) m_exception;
				}
				else
				{
					System.String error = "Unexpected deployment error.";
					throw new System.Reflection.TargetInvocationException(error, m_exception);
				}
			}
		}
		
		internal virtual void  done()
		{
			lock (this)
			{
				m_completed = true;
				System.Threading.Monitor.PulseAll(this);
			}
		}
		
		internal virtual void  interrupted()
		{
			m_interrupted = true;
			lock (this)
			{
				System.Threading.Monitor.Pulse(this);
			}
		}
		
		internal virtual void  exception(System.Exception e)
		{
			m_exception = e;
			lock (this)
			{
				System.Threading.Monitor.Pulse(this);
			}
		}
	}
}