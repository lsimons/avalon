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
	
	/// <summary>This class implements a simple thread-safe FirstIn-FirstOut queue.
	/// 
	/// It is not intended to be used outside this package,
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:42 $
	/// </version>
	class SimpleFIFO
	{
		private System.Collections.ArrayList m_Queue;
		
		internal SimpleFIFO()
		{
			m_Queue = new System.Collections.ArrayList();
		}
		
		internal virtual void  clear()
		{
			lock (this)
			{
				m_Queue.Clear();
			}
		}
		
		internal virtual void  put(System.Object obj)
		{
			lock (this)
			{
				m_Queue.Add(obj);
				System.Threading.Monitor.PulseAll(this);
			}
		}
		
		internal virtual System.Object get_Renamed()
		{
			lock (this)
			{
				while (m_Queue.Count == 0)
					System.Threading.Monitor.Wait(this, TimeSpan.FromMilliseconds(100));
				object ret = m_Queue[0];
				m_Queue.RemoveAt(0);
				return ret;
			}
		}
		
		internal virtual int size()
		{
			lock (this)
			{
				return m_Queue.Count;
			}
		}
	}
}