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
import java.util.Collections;
import java.util.Collection;
import java.util.Map;

import org.apache.excalibur.source.*;
import org.apache.excalibur.source.impl.validity.FileTimeStampValidity;
import org.apache.excalibur.source.impl.validity.TimeStampValidity;

/**
 * Description of a source which is described by an URL.
 * FIXME: Get mime-type
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Revision: 1.16 $ $Date: 2003/01/08 12:09:32 $
 */
public class URLSource
    extends AbstractSource
    implements Source
{
    /** With this parameter you can specify the method to use for a http request.
     *  Default is GET.
     */
    static public final String HTTP_METHOD = "org.apache.avalon.excalibur.source.Source.http.method";

    /** With this parameter you can specify additional request parameters which are
     *  appended to the URI.
     */
    static public final String REQUEST_PARAMETERS = "org.apache.avalon.excalibur.source.Source.request.parameters";

    /** Identifier for file urls */
    protected final String FILE = "file:";

    /** The URL of the source */
    protected URL url;

    /** The connection for a real URL */
    protected URLConnection connection;

    /** The file, if URL is a file */
    protected File file;

    /** The <code>SourceParameters</code> used for a post*/
    protected SourceParameters parameters;

    /** Is this a post? */
    protected boolean isPost = false;


    /** the prev returned SourceValidity */
    protected SourceValidity cachedValidity;

    protected long cachedLastModificationDate;

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
        this.systemId = url.toExternalForm();
        int pos = systemId.indexOf(':');
        this.protocol = systemId.substring(0, pos);
        if (systemId.startsWith( FILE ))
        {
            this.file = new File( this.systemId.substring( FILE.length() ) );
        }
        else
        {
            this.file = null;
        }
        this.url = url;
        this.gotInfos = false;
        this.isPost = false;
        if( null != parameters )
        {
            this.parameters = (SourceParameters)parameters.get( REQUEST_PARAMETERS );
            final String method = (String)parameters.get( HTTP_METHOD );
            if( "POST".equalsIgnoreCase( method ) )
                this.isPost = true;
        }
        if( null == this.file
            && null != this.parameters
            && this.parameters.hasParameters()
            && !this.isPost )
        {
            StringBuffer urlBuffer = new StringBuffer( this.systemId );
            String key;
            final Iterator i = this.parameters.getParameterNames();
            Iterator values;
            String value;
            boolean first = ( this.systemId.indexOf( '?' ) == -1 );
            if( first == true ) urlBuffer.append( '?' );
            while( i.hasNext() )
            {
                key = (String)i.next();
                values = this.parameters.getParameterValues( key );
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
            this.url = new URL( urlBuffer.toString() );
            this.parameters = null;
        }
    }

    /**
     * Get the last modification date and content length of the source.
     * Any exceptions are ignored.
     * Override this to get the real information
     */
    protected void getInfos()
    {
        if( null != this.file )
        {
            this.lastModificationDate = this.file.lastModified();
            this.contentLength = this.file.length();
        }
        else
        {
            if( !this.isPost )
            {
                try
                {
                    if( null == this.connection )
                    {
                        this.connection = this.url.openConnection();
                        String userInfo = this.getUserInfo();
                        if( this.url.getProtocol().startsWith( "http" ) && userInfo != null )
                        {
                            this.connection.setRequestProperty( "Authorization", "Basic " + SourceUtil.encodeBASE64( userInfo ) );
                        }
                    }
                    this.lastModificationDate = this.connection.getLastModified();
                    this.contentLength = this.connection.getContentLength();
                }
                catch( IOException ignore )
                {
                    super.getInfos();
                }
            }
            else
            {
                // do not open connection when using post!
                super.getInfos();
            }
        }
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
            if( null != this.file )
            {
                input = new FileInputStream( this.file );
            }
            else
            {
                if( this.connection == null )
                {
                    this.connection = this.url.openConnection();
                    /* The following requires a jdk 1.3 */
                    String userInfo = this.getUserInfo();
                    if( this.url.getProtocol().startsWith( "http" ) && userInfo != null )
                    {
                        this.connection.setRequestProperty( "Authorization", "Basic " + SourceUtil.encodeBASE64( userInfo ) );
                    }

                    // do a post operation
                    if( this.connection instanceof HttpURLConnection
                        && this.isPost )
                    {
                        StringBuffer buffer = new StringBuffer( 2000 );
                        String key;
                        Iterator i = this.parameters.getParameterNames();
                        Iterator values;
                        String value;
                        boolean first = true;
                        while( i.hasNext() )
                        {
                            key = (String)i.next();
                            values = this.parameters.getParameterValues( key );
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
                        HttpURLConnection httpCon = (HttpURLConnection)connection;
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
                        this.connection = null; // make sure a new connection is created next time
                        return input;
                    }
                }
                input = this.connection.getInputStream();
                this.connection = null; // make sure a new connection is created next time
            }
            return input;
        }
        catch (FileNotFoundException fnfe)
        {
            throw new SourceNotFoundException("Resource not found " + this.systemId);
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
                    return (String)URLSource.urlGetUserInfo.invoke( this.url, URLSource.emptyParams );
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
            // test if the url class supports the getUserInfo method
            try
            {
                URLSource.urlGetUserInfo = URL.class.getMethod( "getUserInfo", null );
                String ui = (String)URLSource.urlGetUserInfo.invoke( this.url, URLSource.emptyParams );
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
        final long lm = this.getLastModified();
        if( lm > 0 )
        {
            if (lm == this.cachedLastModificationDate)
                return this.cachedValidity;

            this.cachedLastModificationDate = lm;
            if (file != null)
            {
                this.cachedValidity = new FileTimeStampValidity(file, lm);
            }
            else
            {
                this.cachedValidity = new TimeStampValidity( lm );
            }
            return this.cachedValidity;
        }
        return null;
    }

    /**
     * Refresh this object and update the last modified date
     * and content length.
     */
    public void discardValidity()
    {
        // reset connection
        this.connection = null;
        super.discardValidity();
    }

    /**
     * Does this source point to a directory?
     */
    public boolean isDirectory()
    {
    	if ( null != this.file ) 
    	{
    		return this.file.isDirectory();
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
    	if ( null != this.file && this.file.isDirectory() )
    	{
    		final String[] files = this.file.list();
    		return Arrays.asList(files);
    	}
    	return Collections.EMPTY_LIST;
    }
}
