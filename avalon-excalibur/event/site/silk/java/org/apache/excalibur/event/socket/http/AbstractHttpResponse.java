/*
 * Copyright (c) 2001 by Matt Welsh and The Regents of the University of 
 * California. All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose, without fee, and without written agreement is
 * hereby granted, provided that the above copyright notice and the following
 * two paragraphs appear in all copies of this software.
 * 
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
 * OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE UNIVERSITY OF
 * CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
 * ON AN "AS IS" BASIS, AND THE UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO
 * PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 */

/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.socket.http;

import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.socket.Buffer;

/**
 * This is an abstract class corresponding to an HTTP response.
 * Use one of the subclasses (such as OKHttpResponse or NotFoundHttpResponse)
 * to push responses back to the client.
 * @see OKHttpResponse
 * @see NotFoundHttpResponse
 * 
 * @version $Revision: 1.1 $
 * @author Matt Welsh
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public abstract class AbstractHttpResponse
{
    /** The m_code corresponding to the response. */
    protected int m_code = RESPONSE_OK;
    
    /** Code corresponding to '200 OK'. */
    public static final int RESPONSE_OK = 200;
    /** Code corresponding to '301 Moved Permanently'. */
    public static final int RESPONSE_REDIRECT = 301;
    /** Code corresponding to '400 Bad Request'. */
    public static final int RESPONSE_BAD_REQUEST = 400;
    /** Code corresponding to '404 Not Found'. */
    public static final int RESPONSE_NOT_FOUND = 404;
    /** Code corresponding to '500 Internal Server Error'. */
    public static final int RESPONSE_INTERNAL_SERVER_ERROR = 500;
    /** Code corresponding to '503 Service Unavailable'. */
    public static final int RESPONSE_SERVICE_UNAVAILABLE = 503;
    
    /** The default MIME type for responses, which is "text/html". */
    public static final String DEFAULT_MIME_TYPE = "text/html";

    /** Whether the connection should be closed after sending. */
    private boolean m_closeConnection;
    /** Whether the header should be send or not */
    private boolean m_sendHeader;

    /** The actual data of the response. */
    protected Buffer m_combinedData;
    /** The m_header for the response. */
    protected Buffer m_header;
    /** The m_payload for the response. */
    protected Buffer m_payload;
    /** The MIME type of the response. */
    protected String m_contentType;
    /** The content-length m_header. */
    protected int m_contentLength;

    /** The default response m_header. */
    protected static String m_defaultHeader =
        "Server: Sandstorm/Avalon (unknown version)" + HttpConstants.CRLF;
        
//    /**
//     * Create an httpResponder with the given response, with the
//     * connection being derived from the given request.
//     */
//    public httpResponder(AbstractHttpResponse resp, HttpRequest req)
//    {
//        this(
//            resp,
//            req.getConnection(),
//            ((req.getHttpVer() < HttpRequest.HTTPVER_11) ? (true) : (false)));
//    }

    //------------------------- AbstractHttpResponse constructors
    /**
     * Create an AbstractHttpResponse with the given 
     * response m_code with the given m_payload. 
     * @since Sep 26, 2002
     *
     * @param m_code 
     *  The response m_code; should be one of the constants
     *  from AbstractHttpResponse.RESPONSE_*.
     * @param m_contentType 
     *  The MIME type of the response content. Should
     *  not be CRLF-terminated.
     * @param m_payload 
     *  The m_payload of the response.
     */
    protected AbstractHttpResponse(int code, String contentType, Buffer payload)
    {
        this(code, contentType, payload, payload.getSize(), false, true);
    }

    /**
     * Create an AbstractHttpResponse with the given 
     * response m_code with the given m_payload. 
     *
     * @param m_code 
     *  The response code; should be one of the constants
     *  from AbstractHttpResponse.RESPONSE_*.
     * @param m_contentType 
     *  The MIME type of the response content. Should not be 
     *  CRLF-terminated.
     * @param m_payload 
     *  The m_payload of the response.
     * @param m_contentLength 
     *  The m_contentLength to place in the m_header.
     */
    protected AbstractHttpResponse(
        int code,
        String contentType,
        Buffer payload,
        int contentLength,
        boolean closeConnection,
        boolean sendHeader)
    {
        m_code = code;
        m_contentType = contentType;
        m_contentLength = contentLength;
        
        m_closeConnection = closeConnection;
        m_sendHeader = sendHeader;
        m_combinedData = null;
        
        updateHeader();
        m_payload = payload;
    }

    /**
     * Create an AbstractHttpResponse with the given 
     * response m_code with no m_payload.A m_payload can be assigned 
     * later using {@link #setPayload()}.
     *
     * @param m_code 
     *  The response code; should be one of the constants
     *  from AbstractHttpResponse.RESPONSE_*.
     * @param m_contentType 
     *  The MIME type of the response content. Should not be 
     *  CRLF-terminated.
     */
    protected AbstractHttpResponse(int code, String contentType)
    {
        m_code = code;
        m_contentType = contentType;
        m_contentLength = 0;

        m_combinedData = null;
        m_header = null;
        m_payload = null;
        m_closeConnection = false;
        m_sendHeader = true;
    }

//    /**
//     * Create an AbstractHttpResponse with the the given 
//     * response code, with an empty m_payload of the given size. 
//     * This can be more efficient than providing a m_payload separately, 
//     * as the entire contents of the AbstractHttpResponse can 
//     * be sent as a single TCP packet. The m_payload can be filled in 
//     * using the {@link #getPayload()} method.
//     *
//     * @param m_code 
//     *  The response m_code; should be one of the constants
//     *  from AbstractHttpResponse.RESPONSE_*.
//     * @param m_contentType 
//     *  The MIME type of the response content. Should not be 
//     *  CRLF-terminated.
//     * @param payloadSize 
//     *  The size of the m_payload to allocate.
//     * @param completionQueue 
//     *  The completion queue for the m_payload.
//     */
//    protected AbstractHttpResponse(
//        int code, String contentType, int payloadSize, Sink completionQueue)
//    {
//        this(code, contentType, payloadSize, completionQueue, false, true);
//    }
    
//    /**
//     * Create an AbstractHttpResponse with the the given 
//     * response code, with an empty m_payload of the given size. 
//     * This can be more efficient than providing a m_payload separately, 
//     * as the entire contents of the AbstractHttpResponse can 
//     * be sent as a single TCP packet. The m_payload can be filled in 
//     * using the {@link #getPayload()} method.
//     *
//     * @param m_code 
//     *  The response m_code; should be one of the constants
//     *  from AbstractHttpResponse.RESPONSE_*.
//     * @param m_contentType 
//     *  The MIME type of the response content. Should not be 
//     *  CRLF-terminated.
//     * @param payloadSize 
//     *  The size of the m_payload to allocate.
//     * @param completionQueue 
//     *  The completion queue for the m_payload.
//     */
//    protected AbstractHttpResponse(
//        int code, 
//        String contentType, 
//        int payloadSize, 
//        Sink completionQueue,
//        boolean closeConnection,
//        boolean sendHeader)
//    {
//        m_code = code;
//        m_contentType = contentType;
//        m_contentLength = payloadSize;
//        m_closeConnection = closeConnection;
//        m_sendHeader = sendHeader;
//
//        final String string = generateHeader();
//        final byte header[] = string.getBytes();
//        final int length = update;
//        
//        m_combinedData = new Buffer(length + payloadSize);
//        m_combinedData.setCompletionQueue(completionQueue);
//        
//        m_header = new Buffer(m_combinedData.getData(), 0, length);
//        
//        System.arraycopy(header, 0, m_header.getData(), 0, length);
//        m_payload = new Buffer(m_combinedData.getData(), length, payloadSize);
//    }

//    /**
//     * Create an AbstractHttpResponse with the the given 
//     * response code, with an empty m_payload of the given size. This 
//     * can be more efficient than providing a m_payload separately, as 
//     * the entire contents of the AbstractHttpResponse can be 
//     * sent as a single TCP packet. The m_payload can be filled in 
//     * using the {@link #setPayload()} method.
//     *
//     * @param m_code 
//     *  The response m_code; should be one of the constants
//     *  from AbstractHttpResponse.RESPONSE_*.
//     * @param m_contentType 
//     *  The MIME type of the response content. Should not be 
//     *  CRLF-terminated.
//     * @param payloadSize 
//     *  The size of the m_payload to allocate.
//     */
//    protected AbstractHttpResponse(
//        int code, String contentType, int payloadSize)
//    {
//        this(code, contentType, payloadSize, null);
//    }

    //---------------------- AbstractHttpResponse abstract methods
    /** 
     * Return the entity header as a String. Must be 
     * implemented by subclasses.
     * @since Sep 26, 2002
     * 
     * @return String
     *  The entity header or <code>null</code> if none is 
     *  necessary.
     */
    protected abstract String getEntityHeader();

    //---------------------- AbstractHttpResponse specific implementation
    /**
     * Allows to set the payload after creating the response with 
     * an empty payload. XXX Should not be used if the payload 
     * was allocated by this response (that is, if the payloadSize 
     * was specified in the constructor). 
     * @since Sep 26, 2002
     * 
     * @param payload
     *  The payload as a buffer object.
     */
    public void setPayload(Buffer payload)
    {
        m_payload = payload;
        m_contentLength = payload.getSize();
    }

    /**
     * Returns the header for this response. Creates one if
     * none exists yet.
     * @since Sep 26, 2002
     * 
     * @return Buffer
     *  The header as a buffer object
     */
    public Buffer getHeader()
    {
        if (m_header == null)
        {
            updateHeader();
        }
        return m_header;
    }

    /**
     * Returns the payload buffer for this response.
     * @since Sep 26, 2002
     * 
     * @return Buffer
     *  The payload buffer for this response.
     */
    public Buffer getPayload()
    {
        return m_payload;
    }

    //---------------------- AbstractHttpResponse specific implementation
    /**
     * Get an array of Buffer elements corresponding to this
     * response. Used internally when sending the response 
     * to a client.
     *
     * @return Buffer[] 
     *  An array of Buffer elements corresponding to this
     *  response
     */
    Buffer[] getBuffers()
    {
        if (m_combinedData != null)
        {
            if (m_sendHeader)
            {
                return new Buffer[] { m_combinedData };
            }
            else
            {
                return new Buffer[] { m_payload };
            }
        }
        else if (m_sendHeader)
        {
            if (m_payload != null)
            {
                return new Buffer[] { getHeader(), getPayload() };
            }
            else
            {
                return new Buffer[] { getHeader() };
            }
        }
        else
        {
            if (m_payload != null)
            {
                return new Buffer[] { m_payload };
            }
            else
            {
                return null;
            }
        }
    }

    /**
     * Generates the header for this response.
     * @since Sep 26, 2002
     * 
     * @return String
     *  The header as a string
     */
    private int updateHeader()
    {
        final StringBuffer header = new StringBuffer();
        header.append(HttpConstants.HTTP_VERSION);
        
        switch (m_code)
        {
            case RESPONSE_OK :
                header.append(" 200 OK\n");
                break;
            case RESPONSE_REDIRECT :
                header.append(" 301 MOVED PERMANENTLY\n");
                break;
            case RESPONSE_BAD_REQUEST :
                header.append(" 400 BAD REQUEST\n");
                break;
            case RESPONSE_NOT_FOUND :
                header.append(" 404 NOT FOUND\n");
                break;
            case RESPONSE_INTERNAL_SERVER_ERROR :
                header.append(" 500 INTERNAL SERVER ERROR\n");
                break;
            case RESPONSE_SERVICE_UNAVAILABLE :
                header.append(" 503 SERVICE UNAVAILABLE\n");
                break;
            default :
                throw new Error("Bad response code: " + m_code);
        }
        if (m_defaultHeader != null)
        {
            header.append(m_defaultHeader);
        }
        if (m_contentType != null)
        {
            header.append("Content-Type: ");
            header.append(m_contentType);
            header.append(HttpConstants.CRLF);
        }
        if (m_contentLength != 0)
        {
            header.append("Content-Length: ");
            header.append(m_contentLength);
            header.append(HttpConstants.CRLF);
        }
        
        final String entityHeader = getEntityHeader();
        if (entityHeader != null)
        {
            header.append(entityHeader);
        }
        header.append(HttpConstants.CRLF);
        //header.append(HttpConstants.CRLF);
        
        m_header = new Buffer(header.toString().getBytes());
        return m_header.getSize();
    }

//    /**
//     * Return the connection for this Response.
//     * @since Sep 26, 2002
//     * 
//     * @param  
//     */
//    public HttpConnection getConnection()
//    {
//        return m_httpConnection;
//    }

//    /**
//     * Set the default m_header string sent in all responses.
//     */
//    public static void setDefaultHeader(String defhdr)
//    {
//        m_defaultHeader = defhdr;
//    }
//
//    /**
//     * Return the default m_header string sent in all responses.
//     */
//    public static String getDefaultHeader()
//    {
//        return m_defaultHeader;
//    }
//    /**
//     * Returns whether the connection should be closed after sending this 
//     * response. 
//     */
//    public boolean shouldClose()
//    {
//        return m_closeConnection;
//    }

//    /**
//     * Returns whether the response m_header should be sent.
//     */
//    public boolean sendHeader()
//    {
//        return m_sendHeader;
//    }
}
