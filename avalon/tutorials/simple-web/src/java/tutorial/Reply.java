/*
 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.
 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:
 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.
 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.
 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.
 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.
 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.
 */
package tutorial;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

/**
 * Static-only class to send a simple HTTP reply.
 *
 * @author <a href="mailto:timothy.bennett@gxs.com">Timothy Bennett</a>
 */
public class Reply {
  /**
   * Definition of a carriage-return/line-feed entity.
   */
  public final static String CRLF = "\r\n";
  /**
   * The HTTP RFC specification version supported by this web server.
   */
  public final static String HTTP_VERSION = "HTTP/1.0";
  /**
   * The HTTP RFC specification version supported by this web server.
   */
  public final static String WEB_SERVER_NAME = "Merlin Simple Server/1.0";
  /**
   * HTTP 200 OK
   */
  public final static String HTTP_200 = HTTP_VERSION + " 200 OK" + CRLF;
  /**
   * HTTP 400 Bad Request
   */
  public final static String HTTP_400 = HTTP_VERSION + " 400 Bad Request" +
      CRLF;
  /**
   * HTTP 403 Forbidden
   */
  public final static String HTTP_403 = HTTP_VERSION + " 404 Forbidden" + CRLF;
  /**
   * HTTP 404 Not Found
   */
  public final static String HTTP_404 = HTTP_VERSION + " 404 Not Found" + CRLF;
  /**
   * HTTP 405 Method Not Allowed
   */
  public final static String HTTP_405 = HTTP_VERSION +
      " 405 Method Not Allowed" + CRLF;

  /**
   * Private constructor to enforce static-only class.
   */
  private Reply() {
  }

  /**
   *
   * @param os
   * @param type
   * @param msg
   * @throws IOException
   */
  public static void sendHttpReply(OutputStream os, String type, String msg) throws
      IOException {

    DataOutputStream dos = new DataOutputStream(os);
    dos.writeBytes(type);
    dos.writeBytes("Connection: close" + CRLF);
    dos.writeBytes("Server: " + WEB_SERVER_NAME + CRLF);
    dos.writeBytes("Date: " + RfcDateFormat.format(new Date()) + CRLF);
    dos.writeBytes("Content-Type: text/html; charset=iso-8859-1" + CRLF);
    //dos.writeBytes("Content-Type: text/html" + CRLF);
    dos.writeBytes("Content-Length: " + msg.length() + CRLF);
    if (type.equals(HTTP_405)) {
      dos.writeBytes("Allow: GET" + CRLF);
    }
    dos.writeBytes(CRLF);

    String s = new String("<html><body><h1>" + msg + "</h1></body></html>");
    //dos.writeBytes(s);
    dos.writeUTF(s);
    dos.flush();
    dos.close();
  }
}
