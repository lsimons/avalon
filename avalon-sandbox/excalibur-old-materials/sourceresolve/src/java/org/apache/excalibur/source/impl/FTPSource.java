/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.excalibur.source.impl;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.excalibur.source.ModifiableSource;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceNotFoundException;

import sun.net.ftp.FtpClient;

/**
 * Source implementation for the File Transfer Protocol.
 * 
 * @author <a href="mailto:unico@hippo.nl">Unico Hommes</a>
 */
public class FTPSource extends URLSource implements ModifiableSource
{	
	
	public FTPSource()
	{
		super();
	}

	/**
	 * Can the data sent to an <code>OutputStream</code> returned by
	 * {@link #getOutputStream()} be cancelled ?
	 *
	 * @return <code>true</code> if the stream can be cancelled
	 */
	public boolean canCancel( final OutputStream stream )
	{
		if (stream instanceof FTPSourceOutputStream)
		{
			FTPSourceOutputStream fsos = (FTPSourceOutputStream) stream;
			if ( fsos.getSource() == this )
			{
				return fsos.canCancel();
			}
		}

		throw new IllegalArgumentException( "The stream is not associated to this source" );
	}

	/**
	 * Cancel the data sent to an <code>OutputStream</code> returned by
	 * {@link #getOutputStream()}.
	 * <p>
	 * After cancel, the stream should not be used.
	 */
	public void cancel( final OutputStream stream ) throws IOException
	{
		if (stream instanceof FTPSourceOutputStream)
		{
			FTPSourceOutputStream fsos = (FTPSourceOutputStream) stream;
			if ( fsos.getSource() == this )
			{
				try
				{
					fsos.cancel();
				}
				catch ( Exception e )
				{
					throw new SourceException( "Exception during cancel.", e );
				}
				return;
			}
		}

		throw new IllegalArgumentException( "The stream is not associated to this source" );
	}

	/**
	 * Delete the source.
	 */
	public void delete() throws SourceException
	{
		EnhancedFtpClient ftpClient = null;
		try
		{
			ftpClient = getFtpClient();
			final String relativePath = m_url.getPath().substring( 1 );
			ftpClient.delete( relativePath );
		}
		catch ( IOException e )
		{
			if ( e instanceof FileNotFoundException )
			{
				throw new SourceNotFoundException( e.getMessage() );
			}
			else
			{
				final String message =
					"Failure during delete";
				throw new SourceException( message, e );
			}
		}
		finally
		{
			if ( ftpClient != null )
			{
				try
				{
					ftpClient.closeServer();
				}
				catch ( IOException e ) {}
			}
		}
	}

	/**
	 * Return an {@link OutputStream} to write to.
	 */
	public OutputStream getOutputStream() throws IOException
	{
		return new FTPSourceOutputStream( this );
	}
	
	/**
	 * Creates an FtpClient and logs in the current user.
	 */
	private final EnhancedFtpClient getFtpClient()
		throws IOException
	{
		final EnhancedFtpClient ftpClient = 
			new EnhancedFtpClient( m_url.getHost() );
		ftpClient.login( getUser(), getPassword() );
		return ftpClient;
	}
	
	/**
	 * @return the user part of the user info string, 
	 * <code>null</code> if there is no user info.
	 */
	private final String getUser() 
	{
		final String userInfo = getUserInfo();
		if ( userInfo != null )
		{
			int index = userInfo.indexOf( ':' );
			if ( index != -1 )
			{
				return userInfo.substring( 0, index );
			}
		}
		return null;
	}
	
	/**
	 * @return the password part of the user info string, 
	 * <code>null</code> if there is no user info.
	 */
	private final String getPassword()
	{
		final String userInfo = getUserInfo();
		if ( userInfo != null )
		{
			int index = userInfo.indexOf( ':' );
			if ( index != -1 && userInfo.length() > index + 1 )
			{
				return userInfo.substring( index + 1 );
			}
		}
		return null;
	}
	
	/**
	 * Need to extend FtpClient in order to get to protected issueCommand
	 * and implement additional functionality.
	 */
	private static class EnhancedFtpClient extends FtpClient
	{
		
		private EnhancedFtpClient( String host ) throws IOException
		{
			super( host );
		}
		
		void delete( final String path ) throws IOException
		{
			issueCommand( "DELE " + path );
		}
		
		/**
		 * Create a directory in the current working directory.
		 */
		void mkdir( final String directoryName ) throws IOException
		{
			issueCommand( "MKD " + directoryName );
		}
		
		/**
		 * Create all directories along a directory path if they
		 * do not already exist.
		 * 
		 * The algorithm traverses the directory tree in reversed
		 * direction. cd'ing first to the deepest level 
		 * and if that directory doesn't exist try cd'ing to its
		 * parent from where it can be created.
		 * 
		 * NOTE: after completion the current working directory 
		 * will be the directory identified by directoryPath.
		 */
		void mkdirs( final String directoryPath ) throws IOException
		{
			try
			{
				cd( directoryPath );
			}
			catch ( FileNotFoundException e )
			{
				// doesn't exist, create it
				String directoryName = null;
				final int index = directoryPath.lastIndexOf( '/' );
				if ( index != -1 )
				{
					final String parentDirectoryPath = 
						directoryPath.substring( 0, index );
					directoryName = directoryPath.substring( index + 1 );
					mkdirs( parentDirectoryPath );
				}
				else
				{
					directoryName = directoryPath;
				}
				mkdir( directoryName );
				cd( directoryName );
			}			
		}
					
	}
	
	/**
	 * Buffers the output in a byte array and only writes to the remote 
	 * FTP location at closing time.
	 */
	private static class FTPSourceOutputStream extends ByteArrayOutputStream
	{
		private final FTPSource m_source;
		private boolean m_isClosed = false;
		
		FTPSourceOutputStream( final FTPSource source )
		{
			super( 8192 );
			m_source = source;
		}
		
		public void close() throws IOException
		{			
			if ( !m_isClosed )
			{
				EnhancedFtpClient ftpClient = null;
				OutputStream out = null;
				try
				{
					ftpClient = m_source.getFtpClient();
					String parentPath = null;
					String fileName = null;
					final String relativePath = m_source.m_url.getPath().substring( 1 );
					final int index = relativePath.lastIndexOf( '/' );
					if ( index != -1 )
					{
						parentPath = relativePath.substring( 0, index );
						fileName = relativePath.substring( index + 1 );
						ftpClient.mkdirs( parentPath );
					}
					else
					{
						fileName = relativePath;
					}
					out = ftpClient.put( fileName );
					final byte[] bytes = toByteArray();
					out.write( bytes );
				}
				finally 
				{
					if ( out != null )
					{
						try
						{
							out.close();
						}
						catch ( IOException e ) {}
					}
					if ( ftpClient != null )
					{
						try
						{
							ftpClient.closeServer();
						}
						catch ( IOException e ) {}
					}
					m_isClosed = true;
				}
			}
		}
		
		boolean canCancel()
		{
			return !m_isClosed;
		}

		void cancel() throws Exception
		{
			if ( m_isClosed )
			{
				final String message =
					"Cannot cancel: outputstrem is already closed";
				throw new IllegalStateException( message );
			}
			m_isClosed = true;
		}
		
		FTPSource getSource()
		{
			return m_source;
		}
		
	}

}
