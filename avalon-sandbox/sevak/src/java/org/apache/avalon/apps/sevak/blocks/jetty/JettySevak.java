/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.apps.sevak.blocks.jetty;

import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.CascadingRuntimeException;
import org.apache.avalon.apps.sevak.Sevak;
import org.apache.avalon.apps.sevak.SevakException;
import org.apache.avalon.phoenix.BlockContext;

import java.io.File;
import java.util.HashMap;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.WebApplicationContext;
import org.mortbay.util.MultiException;
import org.mortbay.util.Log;
import org.mortbay.http.SocketListener;



/**
 * @phoenix:block
 * @phoenix:service name="org.apache.avalon.apps.sevak.Sevak"
 *
 * Jetty Wrapper.
 *
 *
 * @see <a href="http://jetty.mortbay.com/">Jetty Project Page</a>
 *
 * @author  Paul Hammant
 * @version 1.0
 */
public class JettySevak extends AbstractLogEnabled implements Sevak, Startable, Contextualizable,
        Configurable, Initializable
{

    private Server m_server;
    
    /** Virtual host to bind the context to.  null implies all hosts are in context. */
    private String m_hostName;
    
    private HashMap m_webapps = new HashMap();
    private int m_port;
    private File m_sarRootDir;


    /**
     * Contruct a Jetty block
     */
    public JettySevak()
    {
    }

    /**
     * Contextualize
     * @param context the context
     */

    public void contextualize(final Context context)
    {
        m_sarRootDir = ((BlockContext) context).getBaseDirectory();
    }

    /**
     * Configure
     * @param configuration the configuration
     * @throws ConfigurationException if a problem
     */
    public void configure(final Configuration configuration) throws ConfigurationException
    {
        m_hostName = configuration.getChild("hostname").getValue( null );
        m_port = configuration.getChild("port").getValueAsInteger(8080);
    }

    /**
     * Initialize
     * @throws Exception if a problem
     */
    public void initialize() throws Exception
    {
        m_server = new Server();
        final int minThreads = 5;
        final int maxThreads = 250;
        SocketListener listener = new SocketListener();
        listener.setPort(m_port);
        listener.setMinThreads(minThreads);
        listener.setMaxThreads(maxThreads);
        m_server.addListener(listener);
        PhoenixLogSink phoenixLogSink = new PhoenixLogSink();
        phoenixLogSink.enableLogging(getLogger());
        Log.instance().add(phoenixLogSink);
    }

    /**
     * Start
     */
    public final void start()
    {
        try
        {
            m_server.start();
        }
        catch (MultiException e)
        {
            throw new CascadingRuntimeException("Some problem starting Jetty", e);
        }
    }

    /**
     * Stop
     */
    public void stop()
    {
        try
        {
            m_server.stop();
        }
        catch (InterruptedException e)
        {
            throw new CascadingRuntimeException("Some problem stopping Jetty", e);
        }
    }

    /**
     * Deploy a webapp
     * @param context the contxct for the webapp
     * @param pathToWebAppFolder the pathc to it
     * @throws SevakException if a problem
     */
    public void deploy(String context, File pathToWebAppFolder) throws SevakException
    {

        try
        {
            String webAppURL = pathToWebAppFolder.toURL().toString();
            // This still does not work.
//            WebApplicationContext ctx = m_server.addWebApplication(m_hostName, context, webAppURL);

            WebApplicationContext ctx =
                new SevakWebApplicationContext(m_sarRootDir, webAppURL);
            ctx.setContextPath(context);
            m_server.addContext(m_hostName,ctx);

            System.out.println("deploying context=" + context + ", webapp=" + webAppURL
                + " to host=" + ( m_hostName == null ? "(All Hosts)" : m_hostName ) );
                
            ctx.setExtractWAR(true);
            m_webapps.put(context, ctx);
            ctx.start();
        }
        catch (Exception e)
        {
            throw new SevakException("Problem deploying web application in Jetty", e);
        }
    }

    /**
     * Undeploy a webapp.
     * @param context the context
     * @throws SevakException if a problem
     */
    public void undeploy(String context) throws SevakException
    {
        WebApplicationContext ctx = (WebApplicationContext) m_webapps.get(context);
        try
        {
            ctx.stop();
        }
        catch (InterruptedException e)
        {
            throw new SevakException("Problem stopping web application in Jetty", e);
        }
        m_server.removeContext(ctx);
        ctx.destroy();
        m_webapps.remove(context);
    }

}
