/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.apps.sevak.blocks.jetty;

import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.CascadingRuntimeException;
import org.apache.avalon.apps.sevak.Sevak;
import org.apache.avalon.apps.sevak.SevakException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.WebApplicationContext;
import org.mortbay.util.MultiException;


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
        Configurable
{

    private Server m_server;
    private String m_hostName;
    private HashMap webapps = new HashMap();


    public JettySevak()
    {
        m_server = new Server();
    }

    public void contextualize(final Context context)
    {
        //this.m_context = (BlockContext) context;
    }

    public void configure(final Configuration configuration) throws ConfigurationException
    {
        m_hostName = configuration.getChild("hostname").getValue("localhost");
    }

    public final void start()
    {
        try
        {
            m_server.start();
        }
        catch (MultiException e)
        {
            throw new CascadingRuntimeException("Some problem starting Jetty",e);
        }
    }

    public void stop()
    {
        try
        {
            m_server.stop();
        }
        catch (InterruptedException e)
        {
            throw new CascadingRuntimeException("Some problem stopping Jetty",e);
        }
    }

    //----------------------------------------------------------------------------
    // 'deploy' interface
    //----------------------------------------------------------------------------
    /**
     * @param context context path
     * @param pathToWebAppFolder
     */
    public void deploy(String context, File pathToWebAppFolder) throws SevakException
    {
        try
        {
            WebApplicationContext ctx = m_server.addWebApplication(m_hostName, context, pathToWebAppFolder.getAbsolutePath());
            webapps.put(context,ctx);
        }
        catch (IOException ioe)
        {
            throw new SevakException("Problem deploying web application in Jetty",ioe);
        }

    }

    public void undeploy(String context) throws SevakException
    {
        WebApplicationContext ctx = (WebApplicationContext) webapps.get(context);
        ctx.destroy();
        webapps.remove(context);
    }


}
