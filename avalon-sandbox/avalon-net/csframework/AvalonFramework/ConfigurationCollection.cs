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

namespace Apache.Avalon.Framework
{
	using System;
	using System.Collections; 

	/// <summary>
	/// A collection of <see cref="IConfiguration"/> objects.
	/// </summary>
	public class ConfigurationCollection: CollectionBase
	{
		/// <summary>
		/// Creates a new instance of <c>ConfigurationCollection</c>.
		/// </summary>
		public ConfigurationCollection()
		{
		}

		/// <summary>
		/// Creates a new instance of <c>ConfigurationCollection</c>.
		/// </summary>
		public ConfigurationCollection(ConfigurationCollection value)
		{

			this.AddRange(value);
		}

		/// <summary>
		/// Creates a new instance of <c>ConfigurationCollection</c>.
		/// </summary>
		public ConfigurationCollection(IConfiguration[] value)
		{
			this.AddRange(value);
		}

		/// <summary>
		/// Represents the entry at the specified index of the <see cref="IConfiguration"/>.
		/// </summary>
		/// <param name="index">
		/// The zero-based index of the entry to locate in the collection.
		/// </param>
		/// <value>
		/// The entry at the specified index of the collection.
		/// </value>
		/// <exception cref="System.ArgumentOutOfRangeException">
		/// <paramref name="index"/> is outside the valid range of indexes for the collection.
		/// </exception>
		public IConfiguration this[int index] 
		{

			get 
			{

				return (IConfiguration) List[index];
			}

			set 
			{

				List[index] = value;
			}

		}

		/// <summary>
		/// Adds an <see cref="IConfiguration"/>.
		/// </summary>
		/// <param name="value">The <see cref="IConfiguration"/> to add.</param>
		/// <returns>
		/// The index at which the new element was inserted.
		/// </returns>
		public int Add(IConfiguration value) 
		{

			return List.Add(value);
		}

		/// <summary>
		/// Adds an array of <see cref="IConfiguration"/>.
		/// </summary>
		/// <param name="value">The Array of <see cref="IConfiguration"/> to add.</param>
		public void AddRange(IConfiguration[] value) 
		{
			foreach(IConfiguration configuration in value)
			{
				this.Add(configuration);
			}
		}

		/// <summary>
		/// Adds a <see cref="ConfigurationCollection"/>.
		/// </summary>
		/// <param name="value">The <see cref="ConfigurationCollection"/> to add.</param>
		public void AddRange(ConfigurationCollection value) 
		{
			foreach(IConfiguration configuration in value)
			{
				this.Add(configuration);
			}
		}

		/// <summary>
		/// Copies the elements to a one-dimensional <see cref="Array"/> instance at the specified index.
		/// </summary>
		/// <param name="array">
		///	The one-dimensional <see cref="Array"/> must have zero-based indexing.
		///	</param>
		/// <param name="index">The zero-based index in array at which copying begins.</param>
		public void CopyTo(IConfiguration[] array, int index)
		{

			List.CopyTo(array, index);
		}

		/// <summary>
		/// Gets a value indicating whether the <see cref="IConfiguration"/> contains
		/// in the collection.
		/// </summary>
		/// <param name="value">The <see cref="IConfiguration"/> to locate.</param>
		/// <returns>
		/// <see langword="true"/> if the <see cref="IConfiguration"/> is contained in the collection; 
		/// otherwise, <see langword="false"/>.
		/// </returns>
		public bool Contains(IConfiguration value) 
		{

			return List.Contains(value);
		}

		/// <summary>
		/// Gets the index of a <see cref="IConfiguration"/> in 
		/// the collection.
		/// </summary>
		/// <param name="value">The <see cref="IConfiguration"/> to locate.</param>
		/// <returns>
		/// The index of the <see cref="IConfiguration"/> of <paramref name="value"/> in the 
		/// collection, if found; otherwise, -1.
		/// </returns>
		public int IndexOf(IConfiguration value) 
		{

			return List.IndexOf(value);
		}

		/// <summary>
		/// Inserts a <see cref="IConfiguration"/> into the collection
		/// at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index where <paramref name="value"/> should be inserted.</param>
		/// <param name="value">The <see cref="IConfiguration"/> to insert.</param>
		public void Insert(int index, IConfiguration value) 
		{

			List.Insert(index, value);
		}
		
		/// <summary>
		/// Removes a specific <see cref="IConfiguration"/> from the 
		/// collection.   
		/// </summary>
		/// <param name="value">The <see cref="IConfiguration"/> to remove from the collection.</param>
		/// <exception cref="ArgumentException">
		/// <paramref name="value"/> is not found in the collection.
		/// </exception>
		public void Remove(IConfiguration value) 
		{

			List.Remove(value);
		}
	}
}
