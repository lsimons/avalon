/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1997-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

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

package phoenixdemo.block;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import phoenixdemo.server.PDKDemoServerImpl;

/**
 * Class SocketThread
 *
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.5 $
 */
public class SocketThread
    extends Thread
{
    private PDKDemoServerImpl m_pdkDemoServerImpl;
    private ServerSocket m_serverSocket;

    protected SocketThread( final PDKDemoServerImpl pdkDemoServerImpl,
                            final int port )
    {

        m_pdkDemoServerImpl = pdkDemoServerImpl;

        try
        {
            m_serverSocket = new ServerSocket( port );
        }
        catch( final IOException ioe )
        {
            final String message = "Unable to open listening port. " +
                "It is probably already being listened to.";
            throw new RuntimeException( message );
        }
    }

    /**
     * Method run
     *
     *
     */
    public void run()
    {

        while( true )
        {
            try
            {
                ConnectionThread ct = new ConnectionThread( m_serverSocket.accept() );

                ct.start();
            }
            catch( IOException ioe )
            {
                System.out.println( "Some problem with getting a socket for the connetion." );
            }
        }
    }

    /**
     * Class ConnectionThread
     *
     * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
     * @version $Revision: 1.5 $
     */
    class ConnectionThread extends Thread
    {
        private Socket m_socket;

        private ConnectionThread( final Socket socket )
        {
            m_socket = socket;
        }

        public void run()
        {
            m_pdkDemoServerImpl.processSocket( m_socket );
        }
    }
}
