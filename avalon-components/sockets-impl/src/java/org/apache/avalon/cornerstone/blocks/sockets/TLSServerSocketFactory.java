/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Apache Avalon", "Avalon Components", "Avalon
    Framework" and "Apache Software Foundation"  must not be used to endorse
    or promote products derived  from this  software without  prior written
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

package org.apache.avalon.cornerstone.blocks.sockets;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import org.apache.avalon.cornerstone.services.sockets.ServerSocketFactory;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * Manufactures TLS server sockets. Configuration element inside a
 * SocketManager would look like:
 * <pre>
 *  &lt;factory name="secure"
 *            class="org.apache.avalon.cornerstone.blocks.sockets.TLSServerSocketFactory" &gt;
 *   &lt;ssl-factory /&gt; &lt;!-- see {@link SSLFactoryBuilder} --&gt;
 *   &lt;timeout&gt; 0 &lt;/timeout&gt;
 *   &lt;!-- With this option set to a non-zero timeout, a call to
 *     accept() for this ServerSocket will block for only this amount of
 *     time. If the timeout expires, a java.io.InterruptedIOException is
 *     raised, though the ServerSocket is still valid. Default value is 0. --&gt;
 *   &lt;authenticate-client&gt;false&lt;/authenticate-client&gt;
 *   &lt;!-- Whether or not the client must present a certificate to
 *      confirm its identity. Defaults to false. --&gt;
 * &lt;/factory&gt;
 * </pre>
 *
 * @author Peter Donald
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:charles@benett1.demon.co.uk">Charles Benett</a>
 * @author <a href="mailto:">Harish Prabandham</a>
 * @author <a href="mailto:">Costin Manolache</a>
 * @author <a href="mailto:">Craig McClanahan</a>
 * @author <a href="mailto:myfam@surfeu.fi">Andrei Ivanov</a>
 * @author <a href="mailto:greg-avalon-apps at nest.cx">Greg Steuck</a>
 */
public class TLSServerSocketFactory
    extends AbstractTLSSocketFactory
    implements ServerSocketFactory
{
    private SSLServerSocketFactory m_factory;
    protected boolean m_keyStoreAuthenticateClients;

    /**
     * Configures the factory.
     *
     * @param configuration the Configuration
     * @exception ConfigurationException if an error occurs
     */
    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        super.configure( configuration );
        m_keyStoreAuthenticateClients =
            configuration.getChild( "authenticate-client" ).getValueAsBoolean( false );
    }

    protected void visitBuilder( SSLFactoryBuilder builder )
    {
        m_factory = builder.buildServerSocketFactory();
    }

    /**
     * Creates a socket on specified port.
     *
     * @param port the port
     * @return the created ServerSocket
     * @exception IOException if an error occurs
     */
    public ServerSocket createServerSocket( final int port )
        throws IOException
    {
        final ServerSocket serverSocket = m_factory.createServerSocket( port );
        initServerSocket( serverSocket );
        return serverSocket;
    }

    /**
     * Creates a socket on specified port with a specified backLog.
     *
     * @param port the port
     * @param backLog the backLog
     * @return the created ServerSocket
     * @exception IOException if an error occurs
     */
    public ServerSocket createServerSocket( int port, int backLog )
        throws IOException
    {
        final ServerSocket serverSocket = m_factory.createServerSocket( port, backLog );
        initServerSocket( serverSocket );
        return serverSocket;
    }

    /**
     * Creates a socket on a particular network interface on specified port
     * with a specified backLog.
     *
     * @param port the port
     * @param backLog the backLog
     * @param bindAddress the network interface to bind to.
     * @return the created ServerSocket
     * @exception IOException if an error occurs
     */
    public ServerSocket createServerSocket( int port, int backLog, InetAddress bindAddress )
        throws IOException
    {
        final ServerSocket serverSocket =
            m_factory.createServerSocket( port, backLog, bindAddress );
        initServerSocket( serverSocket );
        return serverSocket;
    }

    protected void initServerSocket( final ServerSocket serverSocket )
        throws IOException
    {
        final SSLServerSocket socket = (SSLServerSocket)serverSocket;

        // Set client authentication if necessary
        socket.setNeedClientAuth( m_keyStoreAuthenticateClients );
        // Sets socket timeout
        socket.setSoTimeout( m_socketTimeOut );
    }
}
