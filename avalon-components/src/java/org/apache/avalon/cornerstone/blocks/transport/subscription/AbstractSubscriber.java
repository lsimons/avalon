/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.blocks.transport.subscription;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.phoenix.Block;
import org.apache.excalibur.altrmi.client.AltrmiFactory;
import org.apache.excalibur.altrmi.client.AltrmiHostContext;
import org.apache.excalibur.altrmi.client.AltrmiInterfaceLookup;
import org.apache.excalibur.altrmi.client.impl.ClientClassAltrmiFactory;
import org.apache.excalibur.altrmi.client.impl.ServerClassAltrmiFactory;
import org.apache.excalibur.altrmi.common.AltrmiAuthentication;
import org.apache.excalibur.altrmi.common.AltrmiConnectionException;

/**
 * @phoenix:service name="org.apache.excalibur.altrmi.client.AltrmiInterfaceLookup"
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.11 $
 */
public abstract class AbstractSubscriber
    extends AbstractLogEnabled
    implements AltrmiInterfaceLookup, Configurable, Initializable, Block
{
    protected AltrmiFactory m_altrmiFactory;
    protected AltrmiHostContext m_hostContext;

    /**
     * Pass the <code>Configuration</code> to the <code>Configurable</code>
     * class. This method must always be called after the constructor
     * and before any other method.
     *
     * @param configuration the class configurations.
     */
    public void configure( Configuration configuration ) throws ConfigurationException
    {

        String proxyClassLocation = configuration.getChild( "proxyClassLocation" ).getValue();

        if( proxyClassLocation.equals( "client" ) )
        {
            m_altrmiFactory = new ClientClassAltrmiFactory( false );
        }
        else if( proxyClassLocation.equals( "server" ) )
        {
            m_altrmiFactory = new ServerClassAltrmiFactory( false );
        }
        else
        {
            throw new ConfigurationException( "proxyClassLocation must be 'client' or 'server'" );
        }
    }

    public Object lookup( String publishedName ) throws AltrmiConnectionException
    {
        return m_altrmiFactory.lookup( publishedName );
    }

    public Object lookup( String publishedName, AltrmiAuthentication authentication )
        throws AltrmiConnectionException
    {
        return m_altrmiFactory.lookup( publishedName, authentication );
    }

    public String getTextToSignForAuthentication()
    {
        return m_altrmiFactory.getTextToSignForAuthentication();
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
        m_altrmiFactory.setHostContext( m_hostContext );
    }

    public void close()
    {
        m_altrmiFactory.close();
    }

    public String[] list()
    {
        return m_altrmiFactory.list();
    }
}
