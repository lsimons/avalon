/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
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
 * @version $Revision: 1.3 $
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
     * @version $Revision: 1.3 $
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
