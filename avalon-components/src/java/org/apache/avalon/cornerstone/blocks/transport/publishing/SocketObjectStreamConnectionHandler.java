
/*
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jan 9, 2002
 * Time: 10:25:12 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.apache.avalon.cornerstone.blocks.transport.publishing;



import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.cornerstone.services.connection.ConnectionHandler;
import org.apache.commons.altrmi.server.impl.socket.PartialSocketObjectStreamServer;

import java.net.Socket;
import java.net.ProtocolException;

import java.io.IOException;


/**
 * Class SocketObjectStreamConnectionHandler
 *
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.3 $
 */
public class SocketObjectStreamConnectionHandler extends AbstractLogEnabled
        implements Component, ConnectionHandler
{

    private PartialSocketObjectStreamServer m_PartialSocketObjectStreamServer;

    /**
     * Constructor SocketObjectStreamConnectionHandler
     *
     *
     * @param partialSocketObjectStreamServer
     *
     */
    public SocketObjectStreamConnectionHandler(
            PartialSocketObjectStreamServer partialSocketObjectStreamServer)
    {
        m_PartialSocketObjectStreamServer = partialSocketObjectStreamServer;
    }

    /**
     * Handle a connection.
     * This handler is responsible for processing connections as they occur.
     *
     * @param connection the connection
     * @exception IOException if an error reading from socket occurs
     * @exception ProtocolException if an error handling connection occurs
     */
    public void handleConnection(Socket connection) throws IOException, ProtocolException
    {
        m_PartialSocketObjectStreamServer.handleConnection(connection);
    }
}
