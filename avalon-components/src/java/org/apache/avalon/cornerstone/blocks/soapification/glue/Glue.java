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

    public void initialize()
    {
    }

    public void dispose()
    {
    }

    public void configure( Configuration configuration ) throws ConfigurationException {
        mConfiguration = configuration;
        mPort = configuration.getChild("port").getValueAsInteger( 8765 );
    }

    public void start()
        throws Exception
    {
        HTTP.startup( "http://127.0.0.1:"+ mPort +"/soap" );
    }

    public void stop()
    {
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
        //TODO (PH) dynamic proxy
        publish(obj, publicationName);
    }

    public void publish( Object obj, String publicationName, Class interfaceToExpose ) throws SOAPificationException {
        //TODO (PH) dynamic proxy
        publish(obj, publicationName);        
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

