/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.blocks.soapification.glue;

import electric.registry.Registry;
import electric.registry.RegistryException;
import electric.server.http.HTTP;
import electric.net.http.WebServer;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.phoenix.Block;
import org.apache.avalon.cornerstone.services.soapification.SOAPification;
import org.apache.avalon.cornerstone.services.soapification.SOAPificationException;

import java.util.Hashtable;
import java.io.IOException;


/**
 * Default implementation of SOAPification service.
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul Hammant</>
 */
public class Glue
    extends AbstractLoggable
    implements Block, SOAPification, Initializable, Startable, Disposable, Configurable {

    protected Configuration mConfiguration;
    protected int mPort;
    protected String mBindingAddress;
    protected WebServer mWebServer;

    public void initialize()
    {
    }

    public void dispose()
    {
    }

    public void configure( Configuration configuration ) throws ConfigurationException {
        mConfiguration = configuration;
        mPort = configuration.getChild("port").getValueAsInteger( 8765 );
        mBindingAddress = configuration.getChild("binding-address").getValue( "127.0.0.1" );
    }

    public void start() throws IOException
    {
        String svr = "http://"+ mBindingAddress +":"+ mPort +"/soap";
        //mWebServer = WebServer.startWebServer( svr );
        //mWebServer.startup();
        HTTP.startup( "http://127.0.0.1:"+ mPort +"/soap" );    
        getLogger().info("WebServer started as " + svr );
    }

    public void stop() throws IOException
    {
        //mWebServer.shutdown();
        HTTP.shutdown();
    }

    /**
     * Publish 
     *
     * @param obj the object to publish
     * @param publicationName The name to publish it as.
     */
    public void publish( Object obj, String publicationName ) throws SOAPificationException {
        try 
        {
            Registry.publish( publicationName, obj);
        }
        catch (RegistryException re) 
        {
            throw new SOAPificationException("Can't publish object as " + publicationName , re);
        }
    }

    /**
     * Publish 
     *
     * @param obj the object to publish
     * @param publicationName The name to publish it as.
     */
    public void publish( Object obj, String publicationName, Class[] interfacesToExpose ) throws SOAPificationException {
        try 
        {
            Registry.publish( publicationName, obj, interfacesToExpose);
        }
        catch (RegistryException re) 
        {
            throw new SOAPificationException("Can't publish object as " + publicationName , re);
        }
    }

    public void publish( Object obj, String publicationName, Class interfaceToExpose ) throws SOAPificationException {
        publish(obj, publicationName, new Class[] {interfaceToExpose});        
    }

    public void unpublish( String publicationName ) throws SOAPificationException {
        try 
        {        
		    Registry.unpublish(publicationName);
        }
        catch (RegistryException re) 
        {
            throw new SOAPificationException("Can't unpublish object as " + publicationName , re);
        }        
    }
}

