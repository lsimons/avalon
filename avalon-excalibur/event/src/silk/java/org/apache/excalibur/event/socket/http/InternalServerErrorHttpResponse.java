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
 * A HttpResponse corresponding to a '500 Internal Server Error' 
 *
 * @version $Revision: 1.1 $
 * @author Matt Welsh
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class InternalServerErrorHttpResponse extends AbstractHttpResponse
{
    //-------------------------- InternalServerErrorHttpResponse constructors
    /**
     * Create an InternalServerErrorHttpResponse corresponding 
     * to the given request with the given reason.
     * @since Sep 26, 2002
     */
    public InternalServerErrorHttpResponse(HttpRequest request, String reason)
    {
        super(AbstractHttpResponse.RESPONSE_INTERNAL_SERVER_ERROR, "text/html");

        final StringBuffer buffer = new StringBuffer();
        
        buffer.append("<html><head>");
        buffer.append("<title>500 Internal Server Error</title>");
        buffer.append("</head><body bgcolor=white>");
        buffer.append("<font face=\"helvetica\"><big><big>");
        buffer.append("<b>500 Internal Server Error</b></big></big>");
        buffer.append("The URL you requested:<p><blockquote><tt>");
        buffer.append(request.getURL());
        buffer.append("</tt></blockquote><p>");
        buffer.append("generated an internal server error. ");
        buffer.append("The reason given by the server was:");
        buffer.append("<p><blockquote><tt>");
        buffer.append(reason);
        buffer.append("</tt></blockquote></body></html>\n");
        
        final Buffer payload = new Buffer(buffer.toString().getBytes());
        setPayload(payload);
    }

    //-------------------------- AbstractHttpResponse implementation
    /**
     * @see AbstractHttpResponse#getEntityHeader() 
     */
    protected String getEntityHeader()
    {
        return null;
    }
}
