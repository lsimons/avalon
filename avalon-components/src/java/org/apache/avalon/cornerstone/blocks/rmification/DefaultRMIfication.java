/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.blocks.rmification;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.phoenix.Block;
import org.apache.avalon.cornerstone.services.rmification.RMIfication;

/**
 * FIXME: INPROGRESS and NOT TESTED
 * Default implementation of <code>RMIfication</code>.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 * @version $Revision: 1.2 $
 */
public class DefaultRMIfication
    extends AbstractLogEnabled
    implements Configurable, Initializable, Disposable, RMIfication, Block
{
    private int m_port;
    private Registry m_registry;
    private Map m_remoteObjects;

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_port = configuration.getChild( "port" ).getValueAsInteger();
    }

    public void initialize()
        throws Exception
    {
        m_remoteObjects = new HashMap();

        m_registry = LocateRegistry.createRegistry( m_port );
        getLogger().debug( "RMI registry created on port " + m_port );
    }

    public void dispose()
    {
        m_registry = null;

        m_remoteObjects.clear();
        m_remoteObjects = null;
    }

    public void publish( final Remote remote, final String publicationName )
        throws RemoteException, MalformedURLException
    {
        synchronized ( m_remoteObjects )
        {
            UnicastRemoteObject.exportObject( remote );
            m_registry.rebind( publicationName, remote );

            m_remoteObjects.put( publicationName, remote );
        }

        getLogger().debug( "Published " + publicationName );
    }

    public void unpublish( final String publicationName )
        throws RemoteException, NotBoundException, MalformedURLException
    {
        synchronized ( m_remoteObjects )
        {
            final Remote remote = (Remote)m_remoteObjects.get( publicationName );

            m_registry.unbind( publicationName );
            UnicastRemoteObject.unexportObject( remote, true );

            m_remoteObjects.remove( publicationName );
        }

        getLogger().debug( "Unpublished " + publicationName );
    }
}