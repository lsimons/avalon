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

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import org.apache.avalon.framework.CascadingRuntimeException;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.validity.TimeStampValidity;

/**
 * HTTP URL Source object, based on the Jakarta Commons
 * <a href="http://jakarta.apache.org/commons/httpclient/">HttpClient</a>
 * project.
 *
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version CVS $Id: HTTPClientSource.java,v 1.1 2003/07/02 13:23:58 crafterm Exp $
 */
public class HTTPClientSource extends AbstractLogEnabled 
    implements Source, Initializable, Disposable
{
    /**
     * Constant used when obtaining the Content-Type from HTTP Headers
     */
    public static final String CONTENT_TYPE   = "Content-Type";

    /**
     * Constant used when obtaining the Content-Length from HTTP Headers
     */
    public static final String CONTENT_LENGTH = "Content-Length";

    /**
     * Constant used when obtaining the Last-Modified date from HTTP Headers
     */
    public static final String LAST_MODIFIED  = "Last-Modified";

    /**
     * The URI being accessed.
     */
    private final String m_uri;

    /**
     * Contextual parameters passed via the {@link SourceFactory}.
     */
    private final Map m_parameters;

    /**
     * The {@link HttpClient} object.
     */
    private HttpClient m_client;

    /**
     * The {@link HttpMethod} being performed on the {@link HttpClient}.
     */
    private GetMethod m_method;

    /**
     * HTTP response returned from server after the {@link HttpMethod}
     * has been performed.
     */
    private int m_response;

    /**
     * Stored {@link SourceValidity} object.
     */
    private  SourceValidity m_cachedValidity;

    /**
     * Cached last modification date.
     */
    private long m_cachedLastModificationDate;

    /**
     * Constructor, creates a new {@link HTTPClientSource} instance.
     *
     * @param uri URI
     * @param parameters contextual parameters passed to this instance
     * @exception Exception if an error occurs
     */
    public HTTPClientSource( final String uri, final Map parameters )
        throws Exception
    {
        m_uri = uri;
        m_parameters = parameters;
    }

    /**
     * Initializes this {@link HTTPClientSource} instance.
     *
     * @exception Exception if an error occurs
     */
    public void initialize() throws Exception
    {
        m_client = new HttpClient();

        // REVISIT(MC): assume HTTP GET for the moment
        m_method = new GetMethod( m_uri );
        m_response = m_client.executeMethod( m_method );
    }

    /**
     * Method to ascertain whether the given resource actually exists.
     *
     * @return <code>true</code> if the resource pointed to by the 
     *         URI during construction exists, <code>false</code> 
     *         otherwise.
     */
    public boolean exists()
    {
        // REVISIT(MC): should this return true if the server does not return
        // a 404, or a 410, or should it only return true if the user can
        // successfully get an InputStream from it without getting errors.

        // resource does not exist if HttpClient returns a 404 or a 410
        return !( m_response == HttpStatus.SC_GONE || 
                  m_response == HttpStatus.SC_NOT_FOUND );
    }
    
    /**
     * Method to obtain an {@link InputStream} to read the response
     * from the server.
     *
     * @return {@link InputStream} containing data sent from the server.
     * @throws IOException if some I/O problem occurs.
     * @throws SourceNotFoundException if the source doesn't exist.
     */
    public InputStream getInputStream()
        throws IOException, SourceNotFoundException
    {
        // throw SourceNotFoundException - according to Source API we
        // need to throw this if the source doesn't exist.
        if ( !exists() )
        {
            final StringBuffer error = new StringBuffer();
            error.append( "Unable to retrieve URI: " );
            error.append( m_method.getURI() );
            error.append( " (" );
            error.append( m_response );
            error.append( ")" );

            throw new SourceNotFoundException( error.toString() );
        }

        return m_method.getResponseBodyAsStream();
    }

    /**
     * Obtain the absolute URI this {@link Source} object references.
     * 
     * @return the absolute URI this {@link String} object references.
     */
    public String getURI()
    {
        try
        {
            return m_method.getURI().getURI();
        }
        catch ( final URIException e )
        {
            throw new CascadingRuntimeException( "Unable to determine URI", e );
        }
    }

    /**
     * Return the URI scheme identifier, ie.  the part preceding the fist ':' 
     * in the URI (see <a href="http://www.ietf.org/rfc/rfc2396.txt">RFC 2396</a>).
     * 
     * @return the URI scheme identifier
     */
    public String getScheme()
    {
        try
        {
            return m_method.getURI().getScheme();
        }
        catch ( final URIException e )
        {
            throw new CascadingRuntimeException( "Unable to determine URI Scheme", e );
        }
    }
    
    /**
     * Obtain a {@link SourceValidity} object.
     * 
     * @return a {@link SourceValidity} object, or 
     *         <code>null</code> if this is not possible.
     */
    public SourceValidity getValidity()
    {
        // Implementation taken from URLSource.java, Kudos :)

        final long lm = getLastModified();

        if ( lm > 0 )
        {
            if ( lm == m_cachedLastModificationDate )
            {
                return m_cachedValidity;
            }

            m_cachedLastModificationDate = lm;
            m_cachedValidity = new TimeStampValidity( lm );
            return m_cachedValidity;
        }
        return null;
    }

    /**
     * Refreshes this {@link Source} object.
     */
    public void refresh()
    {
        try
        {
            recycle();
            initialize();
        }
        catch ( final Exception e )
        {
            final StringBuffer buf = new StringBuffer();
            buf.append( "Refresh on " );
            buf.append( m_uri );
            buf.append( " failed" );

            if ( getLogger().isWarnEnabled() )
            {
                getLogger().warn( buf.toString(), e );
            }
        }
    }

    /**
     * Obtain the mime-type for the referenced resource.
     * 
     * @return mime-type for the referenced resource.
     */
    public String getMimeType()
    {
        // REVISIT: should this be the mime-type, or the content-type -> URLSource
        // returns the Content-Type, so we'll follow that for now.
        return m_method.getResponseHeader( CONTENT_TYPE ).getValue();
    }

    /**
     * Obtain the content length of the referenced resource.
     * 
     * @return content length of the referenced resource, or 
     *         -1 if unknown/uncalculatable
     */
    public long getContentLength()
    {
        try
        {
            final Header length = 
                m_method.getResponseHeader( CONTENT_LENGTH );
            return length == null ? -1 : Long.parseLong( length.getValue() );
        }
        catch ( final NumberFormatException e )
        {
            if ( getLogger().isDebugEnabled() )
            {
                getLogger().debug(
                    "Unable to determine content length, returning -1", e 
                );
            }

            return -1;
        }
    }

    /**
     * Get the last modification date of this source. This date is
     * measured in milliseconds since the Epoch (00:00:00 GMT, January 1, 1970).
     * 
     * @return the last modification date or <code>0</code> if unknown.
     */
    public long getLastModified()
    {
        final Header lastModified = m_method.getResponseHeader( LAST_MODIFIED );
        return lastModified == null ? 0 : Date.parse( lastModified.getValue() );
    }

    /**
     * Disposes this {@link HTTPClientSource} instance.
     */
    public void dispose()
    {
        if ( m_method != null )
        {
            m_method.releaseConnection();
        }
    }

    /**
     * Recycles this {@link HTTPClientSource} object so that it may be reused
     * to refresh it's content. Note, after this method is called,
     * {@link HTTPClientSource}.invoke should be invoked.
     */
    private void recycle()
    {
        m_method.recycle();
    }
}
