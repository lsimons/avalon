/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.blocks.transport.publishing;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.altrmi.server.impl.rmi.RmiServer;

/**
 * @phoenix:block
 * @phoenix:service name="org.apache.excalibur.altrmi.server.AltrmiPublisher"
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.13 $
 */
public class RmiPublisher
    extends AbstractPublisher
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
    public void configure( Configuration configuration ) throws ConfigurationException
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
    public void initialize() throws Exception
    {

        setAbstractServer( new RmiServer( m_host, m_port ) );

        setupLogger( getAbstractServer() );
        super.initialize();
    }

    /**
     * Service as per Serviceable interface
     * @param manager a service manager
     * @throws ServiceException if a problem during servicing
     */
    public void service( ServiceManager manager )
        throws ServiceException
    {
        super.service( manager );
    }
}
