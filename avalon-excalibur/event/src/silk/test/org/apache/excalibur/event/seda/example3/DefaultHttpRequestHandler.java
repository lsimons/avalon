/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.seda.example3;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.excalibur.event.SinkException;
import org.apache.excalibur.event.socket.Buffer;
import org.apache.excalibur.event.socket.http.HttpConnection;
import org.apache.excalibur.event.socket.http.HttpRequest;
import org.apache.excalibur.event.socket.http.InternalServerErrorHttpResponse;
import org.apache.excalibur.event.socket.http.NotFoundHttpResponse;
import org.apache.excalibur.event.socket.http.OKHttpResponse;

/**
 * Default implementation of the HttpRequestHandler interface. Receives 
 * and handles socket connection events.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class DefaultHttpRequestHandler
    extends AbstractLogEnabled
    implements HttpRequestHandler, Configurable
{
    /** The file directory with static html files */
    private String m_fileDir = ".";
    
    /** Write posten content to the temporary directory */
    private boolean m_temp = false;

    //------------------------- HttpRequestHandler implementation
    /**
     * @see HttpRequestHandler#service(HttpRequest)
     */
    public void service(HttpRequest request) throws SinkException
    {
        final String url = request.getURL();
        if (getLogger().isInfoEnabled())
        {
            getLogger().info("-> Requested resource: '" + url + "'");
        }

        final Buffer content = request.getContent();
        if (content != null)
        {
            if (getLogger().isInfoEnabled())
            {
                getLogger().info("Posted " + content.getSize() + " bytes.");
            }
            if(m_temp)
            {
                try
                {
                    final File temp =
                        File.createTempFile(
                            "www-temp", ".tmp");
                    temp.deleteOnExit();
                    final OutputStream out =
                        new BufferedOutputStream(new FileOutputStream(temp));
    
                    out.write(
                        content.getData(),
                        content.getOffset(),
                        content.getSize());
                    out.close();
                }
                catch (IOException e)
                {
                    if (getLogger().isErrorEnabled())
                    {
                        getLogger().error("Error writing temp file.", e);
                    }
                }
            }
        }

        final HttpConnection connection = request.getConnection();

        File resource = new File(m_fileDir + url);

        if (resource.exists() && !resource.isFile())
        {
            resource = new File(resource, "index.html");
        }

        if (!resource.exists() || !resource.isFile())
        {
            connection.write(
                new NotFoundHttpResponse(request, "File not Found!"));
        }
        else
        {
            try
            {
                final FileInputStream in = new FileInputStream(resource);
                final FileChannel channel = in.getChannel();
                final int size = (int) channel.size();
                final byte[] array = new byte[size];
                channel.map(FileChannel.MapMode.READ_ONLY, 0, size).get(array);

                final Buffer element = new Buffer(array);
                connection.write(new OKHttpResponse("text/html", element));
            }
            catch (Exception e)
            {
                connection.write(
                    new InternalServerErrorHttpResponse(request, e.toString()));
            }

            if (getLogger().isDebugEnabled())
            {
                getLogger().debug("-> Resource returned!");
            }
        }

        connection.flush();
        connection.close();
    }

    /**
     * @see HttpRequestHandler#accept(HttpConnection)
     */
    public void accept(HttpConnection connection) throws SinkException
    {
        if (getLogger().isInfoEnabled())
        {
            getLogger().info("==============================");
            getLogger().info("Received Connection. Now read.");
            getLogger().info("==============================");
        }

        connection.read();
    }

    //------------------------- Configurable implementation
    /**
     * @see Configurable#configure(Configuration)
     */
    public void configure(Configuration configuration)
    {
        m_fileDir = configuration.getAttribute("file-dir", ".");
        m_temp = configuration.getAttributeAsBoolean("write-post", false);
    }

}