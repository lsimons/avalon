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

namespace Apache.Avalon.Container.Context
{
	using System;
	using System.Collections;
	using System.Runtime.Serialization; 

	using Apache.Avalon.Framework;

	/// <summary>
	/// Default implementation of IContext.
	/// </summary>
	public class DefaultContext : IContext
	{
		[Serializable]
		private sealed class Hidden
		{
		}

		private static Hidden HIDDEN_MAKER = new Hidden();

		private IDictionary m_contextData;
		private IContext m_parent;
		private bool m_readOnly;

		/// <summary>
		/// Create a Context with specified data and parent.
		/// </summary>
		/// <param name="contextData">the context data</param>
		/// <param name="parent">the parent Context (may be null)</param>
		public DefaultContext( IDictionary contextData, IContext parent )
		{
			m_parent = parent;
			m_contextData = contextData;
		}

		/// <summary>
		/// Create a Context with specified data.
		/// </summary>
		/// <param name="contextData">the context data</param>
		public DefaultContext( IDictionary contextData ) : this( contextData, null )
		{
		}

		/// <summary>
		/// Create a Context with specified parent.
		/// </summary>
		/// <param name="parent">the parent Context (may be null)</param>
		public DefaultContext( IContext parent ) 
			: this( Hashtable.Synchronized( new Hashtable() ), parent )
		{
		}

		/// <summary>
		/// Create a Context with no parent.
		/// </summary>
		public DefaultContext() : this( (IContext)null )
		{
		}

		#region IContext Members

		/// <summary>
		/// Retrieve an item from the Context.
		/// </summary>
		/// <param name="key">the key of item</param>
		/// <returns>the item stored in context</returns>
		/// <exception cref="ContextException">if item not present</exception>
		public object this[ object key ]
		{
			get 
			{
				object data = m_contextData[key];

				if( null != data )
				{
					if( data is Hidden )
					{
						// Always fail.
						string message = "Unable to locate " + key;
						throw new ContextException( message );
					}

					if( data is IResolvable )
					{
						return ( (IResolvable)data ).Resolve( this );
					}

					return data;
				}

				// If data was null, check the parent
				if( null == m_parent )
				{
					// There was no parent, and no data
					string message = "Unable to resolve context key: " + key;
					throw new ContextException( message );
				}

				return m_parent[ key ];
			}
		}

		#endregion

		/// <summary>
		/// Helper method fo adding items to Context.
		/// </summary>
		/// <param name="key">the items key</param>
		/// <param name="value">the item</param>
		/// <exception cref="ContextException">if context is read only</exception>
		public void Put( object key, object value )
		{
			checkWriteable();
			if( null == value )
			{
				m_contextData.Remove( key );
			}
			else
			{
				m_contextData[ key ] = value;
			}
		}

		/// <summary>
		/// Hides the item in the context.
		/// After Hide(key) has been called, a Get(key)
		/// will always fail, even if the parent context
		/// has such a mapping.
		/// </summary>
		/// <param name="key">the items key</param>
		/// <exception cref="ContextException">if context is read only</exception>
		public void Hide( object key )
		{
			checkWriteable();
			m_contextData[ key ] = HIDDEN_MAKER;
		}

		/// <summary>
		/// Utility method to retrieve context data.
		/// </summary>
		/// <returns>the context data</returns>
		protected IDictionary GetContextData()
		{
			return m_contextData;
		}

		/// <summary>
		/// Get parent context if any.
		/// </summary>
		/// <returns>the parent Context (may be null)</returns>
		protected IContext GetParent()
		{
			return m_parent;
		}

		/// <summary>
		/// Make the context read-only.
		/// Any attempt to write to the context via Add()
		/// will result in an IllegalStateException.
		/// </summary>
		public void MakeReadOnly()
		{
			m_readOnly = true;
		}

		/// <summary>
		/// Utility method to check if context is writeable and if not throw exception.
		/// </summary>
		/// <exception cref="ContextException">if context is read only</exception>
		protected void checkWriteable()
		{
			if( m_readOnly )
			{
				string message = "Context is read only and can not be modified";
				throw new ContextException( message );
			}
		}
	}
}
