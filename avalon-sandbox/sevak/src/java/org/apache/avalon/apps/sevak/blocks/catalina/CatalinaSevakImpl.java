/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.apps.sevak.blocks.catalina;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.avalon.apps.sevak.Sevak;
import org.apache.avalon.apps.sevak.SevakException;
import org.apache.avalon.apps.sevak.util.CatalinaLogger;
import org.apache.avalon.apps.sevak.util.CustomWebappLoader;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Loader;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.Embedded;
import org.apache.coyote.tomcat4.CoyoteConnector;

/**
 * @phoenix:block
 * @phoenix:service name="org.apache.avalon.apps.sevak.Sevak"
 *
 * Tomcat Wrapper.
 *
 *
 * @see <a href="http://jakarta.apache.org/tomcat">Tomcat Project Page</a>
 *
 * @author  Vinay Chandran<vinayc77@yahoo.com>
 * @version 1.0
 */
public class CatalinaSevakImpl
    extends AbstractLogEnabled
    implements Contextualizable, Serviceable, Configurable, Initializable, Sevak
{
    private BlockContext m_context;
    private Configuration m_configuration;
    private Engine m_engine = null;
    private Embedded m_embedded = null;
    private Host m_tomcatHost = null;
    private Loader m_catalinaCustomClassLoader = null;
    private int m_port;
    private String m_host = null;

    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */

    public void contextualize(final Context context)
    {
        getLogger().info("Sevak.contextualize()");
        m_context = (BlockContext) context;
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */

    public void configure(final Configuration configuration)
        throws ConfigurationException
    {

        m_configuration = configuration;

    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable
     *
     */
    public void service(final ServiceManager serviceManager)
        throws ServiceException
    {
        getLogger().info("Sevak.service()");

    }

    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */

    public void initialize() throws Exception
    {
        getLogger().info("Sevak.initialize()");

        //create the Logger
        CatalinaLogger catalinaLogger = new CatalinaLogger();
        catalinaLogger.enableLogging(getLogger());
        //create the Custom ClassLoader

        m_catalinaCustomClassLoader =
            new CustomWebappLoader(this.getClass().getClassLoader());

        //read the configuration properties
        String catalinaHome =
            m_configuration.getChild("catalina.home").getValue(null);
        if (catalinaHome == null)
        {
            catalinaHome=m_context.getBaseDirectory().getAbsolutePath();
            //Creating a conf/ folder  and dumping the default web.xml 
            File confDir=new File(m_context.getBaseDirectory(),"conf");
            confDir.mkdir();
            getLogger().info("Created conf/ folder");
            InputStream in= CatalinaSevakImpl.class.getResourceAsStream("default-web.xml");
            FileOutputStream fos =
                        new FileOutputStream(new File(confDir,"web.xml").getAbsolutePath());
            byte[] bytes= new byte[512];
            int read=0;
            while((read=in.read(bytes))!=-1)
            {
                fos.write(bytes,0,read);
            }
            fos.close();
            in.close();
        }
        /*
         * TODO : Hack Tomcat to be able to run without the catalina.home property set
        {
            System.out.println(
                "catalina.home property Not Found. Using : "
                    + m_context.getBaseDirectory().getAbsolutePath());
            catalinaHome = m_context.getBaseDirectory().getAbsolutePath();
        }
        */
        m_port = m_configuration.getChild("port").getValueAsInteger(8080);
        m_host = m_configuration.getChild("bind").getValue("localhost");

        //set the catalina home directory
        System.setProperty("catalina.home", new File(catalinaHome).getAbsolutePath());
        // Create an Embedded Tomcat server
        m_embedded = new Embedded();
        m_embedded.setDebug(0);
        m_embedded.setLogger(catalinaLogger);

        //Create Tomcat Engine
        m_engine = m_embedded.createEngine();
        m_engine.setDefaultHost("localhost");

        // Create Tomcat Host
        m_tomcatHost = m_embedded.createHost("localhost", "webapps");
        m_engine.addChild(m_tomcatHost);

        // Create HTTP Coyote Connector
        CoyoteConnector coyoteConnector = new CoyoteConnector();
        coyoteConnector.setPort(8080);
        coyoteConnector.setMinProcessors(5);
        coyoteConnector.setMaxProcessors(75);
        coyoteConnector.setEnableLookups(true);
        coyoteConnector.setAcceptCount(10);
        coyoteConnector.setDebug(0);
        coyoteConnector.setConnectionTimeout(20000);
        coyoteConnector.setUseURIValidationHack(false);

        m_embedded.addEngine(m_engine);
        m_embedded.addConnector(coyoteConnector);

        //START  Tomcat Instance
        try
        {
            m_embedded.start();
        }
        catch (LifecycleException le)
        {
            le.printStackTrace();
            throw new ConfigurationException("[FATAL] Could Not START Tomcat  ");

        }
    }

    /**
     * Deploy the given Web Application
     * @param context Context for the the webapp
     * @param dirToWebAppFolder path can be a war-archive or exploded directory
     * @throws org.apache.avalon.apps.sevak.SevakException Thrown when the context already exists
     */
    public void deploy(String context, File dirToWebAppFolder)
        throws SevakException
    {
        if (!dirToWebAppFolder.exists())
        {
            throw new SevakException(
                "Path not Found[" + dirToWebAppFolder + "]");
        }
        try
        {
            if (context == null)
            {
                throw new SevakException("Invalid Context[" + context + "]");
            }
            if (context.equals("/"))
            {
                context = "";
            }
            if (dirToWebAppFolder == null)
            {
                throw new SevakException(
                    "Invalid WAR [" + dirToWebAppFolder + "]");
            }
            //now deploy ....
            org.apache.catalina.Context catalinaContext =
                (org.apache.catalina.Context) m_tomcatHost.findChild(context);
            if (catalinaContext != null)
            {
                throw new Exception("Context " + context + " Already Exists!");
            }
            catalinaContext =
                createContext(context, dirToWebAppFolder.getAbsolutePath());
            m_tomcatHost.addChild(catalinaContext);
            System.out.println("Deployed [" + context + "] Context");
        }
        catch (Exception catalinaException)
        {

            throw new SevakException(
                "Catalina Internal Error",
                catalinaException);
        }

    }

    /**
     * Undeploy the given WebApp 
     * @param context Webapp context
     * @throws org.apache.avalon.apps.sevak.SevakException Thrown when context does NOT exist
     */
    public void undeploy(String context) throws SevakException
    {
        if (context == null)
        {
            throw new SevakException("Invalid Context[" + context + "]");
        }
        if (context.equals("/"))
        {
            context = "";
        }
        org.apache.catalina.Context catalinaContext =
            (org.apache.catalina.Context) m_tomcatHost.findChild(context);
        if (catalinaContext == null)
        {
            throw new SevakException("Context " + context + " does NOT Exist");
        }
        m_tomcatHost.removeChild(catalinaContext);
    }
    /**
     * Create a StandardContext 
     * @param path
     * @param docBase
     * @return Context
     */

    private org.apache.catalina.Context createContext(
        String path,
        String docBase)
    {

        StandardContext context = new StandardContext();

        context.setDebug(0);
        context.setDocBase(docBase);
        context.setPath(path);
        context.setLoader(m_catalinaCustomClassLoader);

        ContextConfig config = new ContextConfig();
        config.setDebug(0);
        ((Lifecycle) context).addLifecycleListener(config);

        return (context);

    }

}
