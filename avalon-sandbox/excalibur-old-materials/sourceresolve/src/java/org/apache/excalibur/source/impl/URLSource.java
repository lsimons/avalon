/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
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
import java.util.Iterator;
import java.util.Map;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceParameters;
import org.apache.excalibur.source.SourceUtil;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.validity.TimeStampValidity;

/**
 * Description of a source which is described by an URL.
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Revision: 1.10 $ $Date: 2002/05/24 09:09:29 $
 */

public class URLSource
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

    /** The last modification date or 0 */
    protected long lastModificationDate;

    /** The system id */
    protected String systemId;

    /** The URL of the source */
    protected URL url;

    /** The connection for a real URL */
    protected URLConnection connection;

    /** Is this a file or a "real" URL */
    protected boolean isFile;

    /** Are we initialized? */
    protected boolean gotInfos;

    /** The <code>SourceParameters</code> used for a post*/
    protected SourceParameters parameters;

    /** Is this a post? */
    protected boolean isPost = false;

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
        this.isFile = systemId.startsWith( FILE );
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
        if( !isFile
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
     */
    protected void getInfos()
    {
        if( !this.gotInfos )
        {
            if( this.isFile )
            {
                File file = new File( this.systemId.substring( FILE.length() ) );
                this.lastModificationDate = file.lastModified();
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
                    }
                    catch( IOException ignore )
                    {
                        this.lastModificationDate = 0;
                    }
                }
                else
                {
                    // do not open connection when using post!
                    this.lastModificationDate = 0;
                }
            }
            this.gotInfos = true;
        }
    }

    /**
     * Get the last modification date of the source or 0 if it
     * is not possible to determine the date.
     */
    public long getLastModified()
    {
        getInfos();
        return this.lastModificationDate;
    }

    /**
     * Return an <code>InputStream</code> object to read from the source.
     *
     * @throws ResourceNotFoundException if file not found or
     *         HTTP location does not exist.
     * @throws IOException if I/O error occured.
     */
    public InputStream getInputStream()
        throws IOException, SourceException
    {
        try
        {
            getInfos();
            InputStream input = null;
            if( this.isFile == true )
            {
                input = new FileInputStream( this.systemId.substring( FILE.length() ) );
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
     * Return the unique identifer for this source
     */
    public String getSystemId()
    {
        return this.systemId;
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
        if( lm == -1 )
        {
            return null;
        }
        else
        {
            return new TimeStampValidity( lm );
        }
    }

    /**
     * Refresh this object and update the last modified date
     * and content length.
     */
    public void discardValidity()
    {
        // reset connection
        this.connection = null;
        this.gotInfos = false;
    }

    /**
     * The mime-type of the content described by this object.
     * If the source is not able to determine the mime-type by itself
     * this can be null.
     */
    public String getMimeType()
    {
        // FIXME
        return null;
    }
}
