/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.demos.soaphelloworldserver;

import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.phoenix.Block;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.avalon.cornerstone.services.soapification.SOAPification;
import org.apache.avalon.cornerstone.demos.helloworldserver.HelloWorldServer;

/**
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul Hammant</a>
 * @version 1.0
 */
public class SOAPHelloWorldServerImpl
    extends AbstractLoggable
    implements Block, SOAPHelloWorldServer,
               Composable, Configurable, Initializable
{
    protected HelloWorldServer    mHelloWorldServer;

    protected SOAPification       mSOAPification;

    protected BlockContext        m_context;
    
    protected String publicationName;

    public void initialize() throws Exception {
        mSOAPification.publish(mHelloWorldServer, publicationName, HelloWorldServer.class);        
    }
    
    public void contextualize( final Context context )
    {
        m_context = (BlockContext)context;
    }

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        publicationName = configuration.getChild("pub-name").getValue();
    }

    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        getLogger().info("SOAPHelloWorldServer.compose()");

        mHelloWorldServer = (HelloWorldServer)componentManager.lookup( HelloWorldServer.ROLE );
        mSOAPification = (SOAPification)componentManager.lookup( SOAPification.ROLE );
    }

}
