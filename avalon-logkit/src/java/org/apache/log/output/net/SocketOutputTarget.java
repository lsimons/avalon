package org.apache.log.output.net;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ObjectOutputStream;

import java.net.Socket;
import java.net.SocketException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log.LogEvent;
import org.apache.log.output.AbstractOutputTarget;

/**
 * SocketOutputTarget
 *
 * Useful for writing the output to a TCP/IP client socket.
 *
 * @author <a href="mailto:rghorpade@onebridge.de"> Rajendra Ghorpade </a>
 * @version
 */
public class SocketOutputTarget extends AbstractOutputTarget
{

    /** Socket to communicate with the server */
    private Socket m_socket;

    /** Output strem to write the log */
    private ObjectOutputStream m_outputStream;

    /**
     * Creates output target with the end point  specified by the address and port
     *
     * @param address end point address
     * @param port the end point port
     * @exception IOException if an I/O error ocurrs when creating socket
     */
    public SocketOutputTarget( final InetAddress address,
                               final int port )
    throws IOException
    {
        m_socket = new Socket( address, port );
        m_outputStream = new ObjectOutputStream( m_socket.getOutputStream() );
        super.open();
    }

    /**
     * Creates the output target with the end point specified by host and port
     *
     * @param host end point host
     * @param port the end point port
     * @exception IOException if an I/O error ocurrs when creating socket
     */
    public SocketOutputTarget( final String host,
                               final int port )
    throws IOException
    {
        m_socket = new Socket( host, port );
        m_outputStream = new ObjectOutputStream( m_socket.getOutputStream() );
        super.open();
    }

    /**
     * Writes the output as a LogEvent without formatting.
     * Formatting ia applied on the server side where it is log.
     *
     * @param event the LogEvent
     */
    protected void write( LogEvent event )
    {
        try
        {
            m_outputStream.writeObject( event );
        }
        catch( final IOException ioex )
        {
            getErrorHandler().error( "Error writting to socket", ioex, null );
        }
    }

    /**
     * To process the LogEvent
     *
     * @param event the LogEvent
     */
    protected void doProcessEvent( LogEvent event )
    {
        write( event );
    }

    /**
     * Shutdown target.
     * Attempting to write to target after close() will cause errors to be logged.
     */
    public synchronized void close()
    {
        super.close();
        m_socket = null;
    }
}