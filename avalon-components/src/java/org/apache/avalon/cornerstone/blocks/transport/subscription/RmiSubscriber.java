/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.blocks.transport.subscription;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.excalibur.altrmi.client.impl.rmi.RmiHostContext;

/**
 * @phoenix:service name="org.apache.excalibur.altrmi.client.AltrmiInterfaceLookup"
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.7 $
 */
public class RmiSubscriber
    extends AbstractSubscriber
{
    private String m_host;
    private int m_port;

    /**
     * Pass the <code>Configuration</code> to the <code>Configurable</code>
     * class. This method must always be called after the constructor
     * and before any other method.
     *
     * @param configuration the class configurations.
     */
    public void configure( final Configuration configuration )
        throws ConfigurationException
    {

        super.configure( configuration );

        m_port = configuration.getChild( "port" ).getValueAsInteger();
        m_host = configuration.getChild( "host" ).getValue();
    }

    /**
     * Initialialize the component. Initialization includes
     * allocating any resources required throughout the
     * components lifecycle.
     *
     * @exception Exception if an error occurs
     */
    public void initialize()
        throws Exception
    {
        m_hostContext = new RmiHostContext( m_host, m_port );
        super.initialize();
    }
}
