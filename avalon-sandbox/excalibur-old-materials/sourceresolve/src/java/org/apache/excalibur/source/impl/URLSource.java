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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceParameters;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.SourceUtil;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.validity.FileTimeStampValidity;
import org.apache.excalibur.source.impl.validity.TimeStampValidity;

/**
 * Description of a source which is described by an URL.
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Revision: 1.22 $ $Date: 2003/03/29 18:53:26 $
 */
public class URLSource
    extends AbstractSource
    implements Source
{
    /** Identifier for file urls */
    protected final String FILE = "file:";

    /** The URL of the source */
    protected URL m_url;

    /** The connection for a real URL */
    protected URLConnection m_connection;

    /** The file, if URL is a file */
    protected File m_file;

    /** The <code>SourceParameters</code> used for a post*/
    protected SourceParameters m_parameters;

    /** Is this a post? */
    protected boolean m_isPost = false;
    
    /** Does this source exist ? */
    protected boolean m_exists = false;


    /** the prev returned SourceValidity */
    protected SourceValidity m_cachedValidity;

    protected long m_cachedLastModificationDate;

    /** The content type (if known) */
    protected String m_mimeType;
    
    /**
     * Constructor
     */
    public URLSource()
    {
    }

    /**
     * Initialize a new object from a <code>URL</code>.
     * @param parameters This is optional
     */
    public void init( URL url,
                      Map parameters )
        throws IOException
    {
        String systemId = url.toExternalForm();
        setSystemId(systemId);
        int pos = systemId.indexOf(':');
        setScheme(systemId.substring(0, pos));
        if (systemId.startsWith( FILE ))
        {
            m_file = new File( systemId.substring( FILE.length() ) );
        }
        else
        {
            m_file = null;
        }
        m_url = url;
        m_isPost = false;
        
        if( null != parameters )
        {
            m_parameters = (SourceParameters)parameters.get( SourceResolver.URI_PARAMETERS );
            final String method = (String)parameters.get( SourceResolver.METHOD );
            if( "POST".equalsIgnoreCase( method ) )
                this.m_isPost = true;
        }
        if( null == this.m_file
            && null != this.m_parameters
            && this.m_parameters.hasParameters()
            && !this.m_isPost )
        {
            StringBuffer urlBuffer = new StringBuffer( systemId );
            String key;
            final Iterator i = this.m_parameters.getParameterNames();
            Iterator values;
            String value;
            boolean first = ( systemId.indexOf( '?' ) == -1 );
            if( first == true ) urlBuffer.append( '?' );
            while( i.hasNext() )
            {
                key = (String)i.next();
                values = this.m_parameters.getParameterValues( key );
                while( values.hasNext() == true )
                {
                    value = SourceUtil.encode( (String)values.next() );
                    if( first == false ) urlBuffer.append( '&' );
                    first = false;
                    urlBuffer.append( key );
                    urlBuffer.append( '=' );
                    urlBuffer.append( value );
                }
            }
            this.m_url = new URL( urlBuffer.toString() );
            this.m_parameters = null;
        }
    }

    /**
     * Get the last modification date and content length of the source.
     * Any exceptions are ignored.
     * Override this to get the real information
     */
    protected void getInfos()
    {
        // exists will be set below depending on the m_url type
        this.m_exists = false;
        
        if( null != this.m_file )
        {
            setLastModified( m_file.lastModified() );
            setContentLength( m_file.length() );
            m_exists = m_file.exists();
        }
        else
        {
            if( !this.m_isPost )
            {
                try
                {
                    if( null == this.m_connection )
                    {
                        this.m_connection = this.m_url.openConnection();
                        String userInfo = this.getUserInfo();
                        if( this.m_url.getProtocol().startsWith( "http" ) && userInfo != null )
                        {
                            this.m_connection.setRequestProperty( "Authorization", "Basic " + SourceUtil.encodeBASE64( userInfo ) );
                        }
                    }
                    setLastModified(m_connection.getLastModified());
                    setContentLength(m_connection.getContentLength());
                    m_mimeType = m_connection.getContentType();
                    m_exists = true;
                }
                catch( IOException ignore )
                {
                    super.getInfos();
                }
            }
            else
            {
                // do not open m_connection when using post!
                super.getInfos();
            }
        }
    }

    /**
     * Does this source exist ?
     */
    public boolean exists()
    {
        this.checkInfos();
        return this.m_exists;
    }

    /**
     * Return an <code>InputStream</code> object to read from the source.
     *
     * @throws SourceException if file not found or
     *         HTTP location does not exist.
     * @throws IOException if I/O error occured.
     */
    public InputStream getInputStream()
        throws IOException, SourceException
    {
        try
        {
            this.checkInfos();
            InputStream input = null;
            if( null != this.m_file )
            {
                input = new FileInputStream( this.m_file );
            }
            else
            {
                if( this.m_connection == null )
                {
                    this.m_connection = this.m_url.openConnection();
                    /* The following requires a jdk 1.3 */
                    String userInfo = this.getUserInfo();
                    if( this.m_url.getProtocol().startsWith( "http" ) && userInfo != null )
                    {
                        this.m_connection.setRequestProperty( "Authorization", "Basic " + SourceUtil.encodeBASE64( userInfo ) );
                    }

                    // do a post operation
                    if( this.m_connection instanceof HttpURLConnection
                        && this.m_isPost )
                    {
                        StringBuffer buffer = new StringBuffer( 2000 );
                        String key;
                        Iterator i = this.m_parameters.getParameterNames();
                        Iterator values;
                        String value;
                        boolean first = true;
                        while( i.hasNext() )
                        {
                            key = (String)i.next();
                            values = this.m_parameters.getParameterValues( key );
                            while( values.hasNext() == true )
                            {
                                value = SourceUtil.encode( (String)values.next() );
                                if( first == false ) buffer.append( '&' );
                                first = false;
                                buffer.append( key.toString() );
                                buffer.append( '=' );
                                buffer.append( value );
                            }
                        }
                        HttpURLConnection httpCon = (HttpURLConnection)m_connection;
                        httpCon.setDoInput( true );

                        if( buffer.length() > 1 )
                        { // only post if we have parameters
                            String postString = buffer.toString();
                            httpCon.setRequestMethod( "POST" ); // this is POST
                            httpCon.setDoOutput( true );
                            httpCon.setRequestProperty( "Content-type", "application/x-www-form-urlencoded" );

                            // A content-length header must be contained in a POST request
                            httpCon.setRequestProperty( "Content-length", Integer.toString( postString.length() ) );
                            java.io.OutputStream out = new java.io.BufferedOutputStream( httpCon.getOutputStream() );
                            out.write( postString.getBytes() );
                            out.close();
                        }
                        input = httpCon.getInputStream();
                        this.m_connection = null; // make sure a new m_connection is created next time
                        return input;
                    }
                }
                input = this.m_connection.getInputStream();
                this.m_connection = null; // make sure a new m_connection is created next time
            }
            return input;
        }
        catch (FileNotFoundException fnfe)
        {
            throw new SourceNotFoundException("Resource not found " + getURI());
        }
    }

    private static boolean checkedURLClass = false;
    private static boolean urlSupportsGetUserInfo = false;
    private static Method urlGetUserInfo = null;
    private static Object[] emptyParams = new Object[ 0 ];

    /**
     * Check if the <code>URL</code> class supports the getUserInfo()
     * method which is introduced in jdk 1.3
     */
    protected String getUserInfo()
    {
        if( URLSource.checkedURLClass == true )
        {
            if( URLSource.urlSupportsGetUserInfo == true )
            {
                try
                {
                    return (String)URLSource.urlGetUserInfo.invoke( m_url, URLSource.emptyParams );
                }
                catch( Exception e )
                {
                    // ignore this anyway
                }
            }
            return null;
        }
        else
        {
            // test if the m_url class supports the getUserInfo method
            try
            {
                URLSource.urlGetUserInfo = URL.class.getMethod( "getUserInfo", null );
                String ui = (String)URLSource.urlGetUserInfo.invoke( m_url, URLSource.emptyParams );
                URLSource.checkedURLClass = true;
                URLSource.urlSupportsGetUserInfo = true;
                return ui;
            }
            catch( Exception e )
            {
            }
            URLSource.checkedURLClass = true;
            URLSource.urlSupportsGetUserInfo = false;
            URLSource.urlGetUserInfo = null;
            return null;
        }
    }

    /**
     *  Get the Validity object. This can either wrap the last modification
     *  date or the expires information or...
     *  If it is currently not possible to calculate such an information
     *  <code>null</code> is returned.
     */
    public SourceValidity getValidity()
    {
        final long lm = getLastModified();
        if( lm > 0 )
        {
            if (lm == m_cachedLastModificationDate)
                return m_cachedValidity;

            m_cachedLastModificationDate = lm;
            if (m_file != null)
            {
                m_cachedValidity = new FileTimeStampValidity(m_file, lm);
            }
            else
            {
                m_cachedValidity = new TimeStampValidity( lm );
            }
            return m_cachedValidity;
        }
        return null;
    }

    /**
     * Refresh this object and update the last modified date
     * and content length.
     */
    public void refresh()
    {
        // reset m_connection
        m_connection = null;
        super.refresh();
    }

    /**
     * Does this source point to a directory?
     */
    public boolean isDirectory()
    {
    	if ( null != m_file ) 
    	{
    		return m_file.isDirectory();
    	}
    	return false;
    }
    
    /**
     * Return the URIs of the children
     * The returned URIs are relative to the URI of the parent
     * (this object)
     */
    public Collection getChildrenLocations() 
    {
    	if ( null != m_file && m_file.isDirectory() )
    	{
    		final String[] files = m_file.list();
    		return Arrays.asList(files);
    	}
    	return Collections.EMPTY_LIST;
    }
    
    /**
     * The mime-type of the content described by this object.
     * If the source is not able to determine the mime-type by itself
     * this can be null.
     */
    public String getMimeType()
    {
        return m_mimeType;
    }
    
}
