/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.services.rmification;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * FIXME: INPROGRESS
 * This service provides a way to publish an <code>Remote<code> object via RMI.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public interface RMIfication
{
    String ROLE = "org.apache.avalon.cornerstone.services.rmification.RMIfication";

    /**
     * Publish a set of interfaces
     *
     * @param remote the remote object to publish
     * @param publicationName The name to publish it as.
     */
    void publish( Remote remote, String publicationName )
        throws RemoteException, MalformedURLException;

    /**
     * Unpublish
     *
     * @param publicationName the name of the object to unpublish
     */
    public void unpublish( final String publicationName )
        throws RemoteException, NotBoundException, MalformedURLException;
}
