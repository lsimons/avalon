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
import org.apache.commons.altrmi.client.AltrmiFactory;
import org.apache.commons.altrmi.client.AltrmiHostContext;
import org.apache.commons.altrmi.client.AltrmiInterfaceLookup;
import org.apache.commons.altrmi.client.impl.ClientClassAltrmiFactory;
import org.apache.commons.altrmi.client.impl.ServerClassAltrmiFactory;
import org.apache.commons.altrmi.common.AltrmiAuthentication;
import org.apache.commons.altrmi.common.AltrmiConnectionException;

/**
 * Class AbstractSubscriber
 *
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.9 $
 */
public abstract class AbstractSubscriber extends AbstractLogEnabled
    implements AltrmiInterfaceLookup, Configurable, Initializable, Block
{

    protected AltrmiFactory mAltrmiFactory;
    protected AltrmiHostContext mHostContext;

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
            mAltrmiFactory = new ClientClassAltrmiFactory( false );
        }
        else if( proxyClassLocation.equals( "server" ) )
        {
            mAltrmiFactory = new ServerClassAltrmiFactory( false );
        }
        else
        {
            throw new ConfigurationException( "proxyClassLocation must be 'client' or 'server'" );
        }
    }

    /**
     * Method lookup
     *
     *
     * @param publishedName
     *
     * @return
     *
     * @throws AltrmiConnectionException
     *
     */
    public Object lookup( String publishedName ) throws AltrmiConnectionException
    {
        return mAltrmiFactory.lookup( publishedName );
    }

    /**
     * Method lookup
     *
     *
     * @param publishedName
     * @param authentication
     *
     * @return
     *
     * @throws AltrmiConnectionException
     *
     */
    public Object lookup( String publishedName, AltrmiAuthentication authentication )
        throws AltrmiConnectionException
    {
        return mAltrmiFactory.lookup( publishedName, authentication );
    }

    /**
     * Method getTextToSignForAuthentication
     *
     *
     * @return
     *
     */
    public String getTextToSignForAuthentication()
    {
        return mAltrmiFactory.getTextToSignForAuthentication();
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
        mAltrmiFactory.setHostContext( mHostContext );
    }

    /**
     * Method close
     *
     *
     */
    public void close()
    {
        mAltrmiFactory.close();
    }

    public String[] list()
    {
        return mAltrmiFactory.list();
    }
}
