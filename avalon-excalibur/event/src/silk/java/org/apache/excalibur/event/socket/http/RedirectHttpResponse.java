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
 * An HttpResponse corresponding to a '301 Moved Permanently' 
 * response.
 *
 * @version $Revision: 1.1 $
 * @author Matt Welsh
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class RedirectHttpResponse extends AbstractHttpResponse
{
    /** The old url */
    private final String m_oldURL;
    
    /** The new one for the client to request */
    private final String m_newURL;

    //-------------------------- RedirectHttpResponse constructors
    /**
     * Create a redirect response corresponding to the given 
     * request with the given new URL.
     */
    public RedirectHttpResponse(HttpRequest request, String newURL)
    {
        super(AbstractHttpResponse.RESPONSE_REDIRECT, "text/html");
        
        m_oldURL = request.getURL();
        m_newURL = newURL;

        final StringBuffer buffer = new StringBuffer();
        
        buffer.append("<html><head>");
        buffer.append("<title>301 Moved Permanently</title>");
        buffer.append("</head><body bgcolor=white>");
        buffer.append("<font face=\"helvetica\"><big><big>");
        buffer.append("<b>301 Moved Permanently</b></big></big>");
        buffer.append("<p>The URL you requested:<p><blockquote><tt>");
        buffer.append(m_oldURL);
        buffer.append("</tt></blockquote><p>has moved permanently to:<p>");
        buffer.append("<blockquote><tt><a href=\"");
        buffer.append(m_newURL);
        buffer.append("\">");
        buffer.append(m_newURL);
        buffer.append("</a></tt></blockquote></body></html>\n");
        
        final Buffer payload = new Buffer(buffer.toString().getBytes());
        setPayload(payload);
    }
    
    //-------------------------- AbstractHttpResponse implementation
    /**
     * @see AbstractHttpResponse#getEntityHeader() 
     */
    protected String getEntityHeader()
    {
        final StringBuffer header = new StringBuffer();
        
        header.append("Location: ");
        header.append(m_newURL);
        header.append(HttpConstants.CRLF);
        header.append("Connection: close");
        header.append(HttpConstants.CRLF);
        
        return header.toString();
    }
}
