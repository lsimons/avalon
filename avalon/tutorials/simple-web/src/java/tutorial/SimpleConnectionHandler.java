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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ProtocolException;
import java.net.Socket;
import java.nio.ByteBuffer;

import org.apache.avalon.cornerstone.services.connection.ConnectionHandler;
import org.apache.avalon.framework.logger.Logger;

/**
 * Simple connection handler for our web server.  It conforms to HTTP/1.0 only.
 * This handler will perform the following tasks:
 * <ul>
 *  <li>Parse the request
 *  <li>Build the reply
 *  <li>Send the reply
 * </ul>
 *
 * @author <a href="mailto:timothy.bennett@gxs.com">Timothy Bennett</a>
 */
public class SimpleConnectionHandler
    implements ConnectionHandler {
  /**
   * Internal reference to the logging channel supplied by the container.
   */
  private Logger m_logger;

  /**
   * Processes connections as they occur.
   *
   * @param socket the socket connection
   * @throws IOException if an error reading from the socket occurs
   * @throws ProtocolException if an error handling the connection occurs
   *
   * @see org.apache.avalon.cornerstone.services.connection.ConnectionHandler#handleConnection(java.net.Socket)
   */
  public void handleConnection(Socket socket) throws IOException,
      ProtocolException {
    Request req;

    // who has connected to us?
    String remoteHost = socket.getInetAddress().getHostName();
    String remoteIP = socket.getInetAddress().getHostAddress();
    getLogger().info("Connection received on port " + socket.getLocalPort()
                     + " from " + remoteHost + " (" + remoteIP + ")");

    // get a request object...
    InputStream input = socket.getInputStream();
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    byte[] bytes = new byte[1024];
    int count = input.read(bytes);
    while (count > -1) {
      output.write(bytes, 0, count);
      if (input.available() == 0) {
        break;
      }
      count = input.read(bytes);
    }
    output.flush();
    output.close();

    getLogger().debug("Preparing byte buffer...");
    ByteBuffer bb = ByteBuffer.allocate(output.size());
    bb.put(output.toByteArray());
    bb.flip();
    getLogger().debug("Byte buffer prepared");
    try {
      getLogger().debug("Parsing request...");
      req = Request.parse(bb);
    }
    catch (MalformedRequestException e) {
      getLogger().error(e.getMessage(), e);
      throw new ProtocolException(e.getMessage());
    }

    // Handle the request -- only accept HTTP GET
    if (!req.isGet()) {
      getLogger().debug("Sending HTTP 405...");
      Reply.sendHttpReply(socket.getOutputStream(), Reply.HTTP_405,
                          "This server only accepts GET");
    }
    else {
      getLogger().debug("Sending HTTP 200...");
      Reply.sendHttpReply(socket.getOutputStream(), Reply.HTTP_200,
                          "Hello, World!");
    }
  }

  /**
   * Sets the container-supplied logger.
   */
  public void setLogger(Logger logger) {
    m_logger = logger;
  }

  /**
   * Return the logging channel assigned to us by the container.
   * @return the logging channel
   */
  private Logger getLogger() {
    return m_logger;
  }
}
