/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package phoenixdemo.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import phoenixdemo.api.PDKDemoServer;

/**
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.3 $
 */
public class PDKDemoServerImpl 
    implements PDKDemoServer
{
    public void processSocket( final Socket socket )
    {
        try
        {
            final ObjectInputStream ois = 
                new ObjectInputStream( socket.getInputStream() );

            String string = null;

            try { string = (String)ois.readObject(); }
            catch( final ClassNotFoundException cnfe) {}

            System.out.println( "String passed = " + string );
            ois.close();
            socket.close();
        } 
        catch( final IOException ioe )
        {
            System.out.println( "Unexpected IO Exception" );
        }
    }

    public static void main( final String[] args ) 
        throws IOException
    {
        final PDKDemoServerImpl svr = new PDKDemoServerImpl();
        final ServerSocket serverSocket = new ServerSocket(7654);

        System.out.println( "PDK Demo listening on port " + 7654 );
        System.out.println( "Ctrl-C to exit" );

        while( true )
        {
            svr.processSocket( serverSocket.accept() );
        }
    }
}
