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

import org.apache.excalibur.event.socket.Buffer;

/**
 * A HttpResponse corresponding to a '200 OK' response.
 *
 * @version $Revision: 1.1 $
 * @author Matt Welsh
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class OKHttpResponse extends AbstractHttpResponse
{
    //-------------------------- OKHttpResponse constructors
    /**
     * Create an OKHttpResponse with the given m_payload 
     * corresponding to the given request, using the given 
     * MIME content-type.
     * @since Sep 26, 2002
     */
    public OKHttpResponse(String contentType, Buffer payload)
    {
        super(AbstractHttpResponse.RESPONSE_OK, contentType, payload);
    }

    /**
     * Create an OKHttpResponse with the given m_payload
     * corresponding to the given request, using the given 
     * MIME content-type. Use the given content length in 
     * the header of the response.
     * @since Sep 26, 2002
     */
    public OKHttpResponse(
        String contentType,
        Buffer payload,
        int contentLength,
        boolean closeConnection,
        boolean sendHeader)
    {
        super(
            AbstractHttpResponse.RESPONSE_OK,
            contentType,
            payload,
            contentLength,
            closeConnection,
            sendHeader);
    }
 
    /**
     * Create an OKHttpResponse with a given MIME type.
     * @since Sep 26, 2002
     */
    public OKHttpResponse(String contentType)
    {
        super(AbstractHttpResponse.RESPONSE_OK, contentType);
    }

//    /**
//     * Create an OKHttpResponse with a given response payload 
//     * size and MIME type.
//     * @since Sep 26, 2002
//     */
//    public OKHttpResponse(String contentType, int payloadSize)
//    {
//        super(
//            AbstractHttpResponse.RESPONSE_OK,
//            contentType,
//            payloadSize);
//    }

    //-------------------------- AbstractHttpResponse implementation
    /**
     * @see AbstractHttpResponse#getEntityHeader() 
     */
    protected String getEntityHeader()
    {
        return null;
    }
}
