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

import java.net.ServerSocket;

import org.apache.avalon.cornerstone.services.connection.
    ConnectionHandlerFactory;
import org.apache.avalon.cornerstone.services.connection.ConnectionManager;
import org.apache.avalon.cornerstone.services.sockets.ServerSocketFactory;
import org.apache.avalon.cornerstone.services.sockets.SocketManager;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Executable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

/**
 * A tutorial that demonstrates a simple web server component for merlin that
 * uses the <code>SocketManager</code> and <code>ConnectionManager</code>
 * components in the cornerstone and excalibur component packages.
 *
 * @author <a href="mailto:timothy.bennett@gxs.com">Timothy Bennett</a>
 *
 * @avalon.component version="1.0" name="simple-web-server"
 */
public class SimpleWebServerComponent
    implements LogEnabled, Serviceable, Configurable, Executable, Disposable {
  /**
   * Internal reference to the logging channel supplied by the container.
   */
  private Logger m_logger;
  /**
   * Internal reference to the socket manager supplied by the container.
   */
  private SocketManager m_socketManager;
  /**
   * Internal reference to the connection manager supplied by the container.
   */
  private ConnectionManager m_connectionManager;
  /**
   * Internal reference to connection handler factory supplied by container
   */
  private ConnectionHandlerFactory m_connectionHandlerFactory;
  /**
   * Internal reference to the web server's listening port supplied
   * by the conponent configuration.  Defaults to 80.
   */
  private int m_port;
  /**
   * Alias for the name of our HTTP connection handler.
   */
  private final static String HTTP_LISTENER = "http-listener";

  /**
   * Supply of a logging channel by the container.
   *
   * @param logger the logging channel for this component
   *
   * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
   */
  public void enableLogging(Logger logger) {
    m_logger = logger;
    getLogger().info("logging");
  }

  /**
   * Servicing of the component by the container during which service
   * dependencies declared under the component can be resolved using the
   * supplied service manager.
   *
   * @param manager the service manager
       * @throws ServiceException if an failure occurs while servicing the component
   *
   * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
   *
   * @avalon.dependency type="org.apache.avalon.cornerstone.services.sockets.SocketManager:1.0" key="socket-manager"
   * @avalon.dependency type="org.apache.avalon.cornerstone.services.connection.ConnectionManager:1.0" key="connection-manager"
   * @avalon.dependency type="org.apache.avalon.cornerstone.services.connection.ConnectionHandlerFactory:1.0" key="connection-handler-factory"
   */
  public void service(ServiceManager manager) throws ServiceException {
    m_socketManager = (SocketManager) manager.lookup("socket-manager");
    m_connectionManager = (ConnectionManager) manager.lookup(
        "connection-manager");
    m_connectionHandlerFactory = (ConnectionHandlerFactory) manager.lookup("connection-handler-factory");
  }

  /**
   * Configuration of the component by the container.
   *
   * TODO: Describe the configuration of the component.
   *
   * @param config the component configuration
   * @throws ConfigurationException if a configuration error occurs
   *
   * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
   */
  public void configure(Configuration config) throws ConfigurationException {
    getLogger().info("Configuring...");

    // Add an HTTP socket listener?
    Configuration httpConfig = config.getChild("http-listener", true);
    if (httpConfig == null) {
      throw new ConfigurationException("port attribute not found!");
    }
    else {
      m_port = httpConfig.getAttributeAsInteger("port", 80);
    }
  }

  /**
   * Component execution trigger by the container following
   * completion of the initialization stage.
   *
   * @see org.apache.avalon.framework.activity.Executable#execute()
   */
  public void execute() throws Exception {
    // Use the Cornerstone SocketManager to give us a factory object for creating
    // a plain ole server socket
    ServerSocketFactory ssf = m_socketManager.getServerSocketFactory("plain");

    // Use that factory to create a server socket
    ServerSocket serverSocket = ssf.createServerSocket(m_port);

    // attach the server socket to our connection manager
    m_connectionManager.connect(HTTP_LISTENER, serverSocket, m_connectionHandlerFactory);

    // we've started!
    getLogger().info("Started HTTP listener socket on port {" + m_port + "}");
  }

  /**
   * Component disposal trigger by the container during which
   * the component will release consumed resources.
   *
   * @see org.apache.avalon.framework.activity.Disposable#dispose()
   */
  public void dispose() {
    try {
      // forcefully tear down all handlers...
      m_connectionManager.disconnect(HTTP_LISTENER, true);
    }
    catch (Exception e) {
      getLogger().error("Unexpected error while shutting down HTTP listener", e);
    }
    m_connectionManager = null;
    m_socketManager = null;
  }

  /**
   * Return the logging channel assigned to us by the container.
   * @return the logging channel
   */
  private Logger getLogger() {
    return m_logger;
  }
}
