/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework.service;

/**
 * A servicable is a class that need to connect to software components using
 * a "role" abstraction, thus not depending on particular implementations
 * but on behavioral interfaces.
 * <br />
 *
 * The contract surrounding a <code>Serviceable</code> is that it is a user.
 * The <code>Serviceable</code> is able to use <code>Object</code>s managed
 * by the <code>ServiceManager</code> it was initialized with.  As part
 * of the contract with the system, the instantiating entity must call
 * the <code>compose</code> method before the <code>Serviceable</code> 
 * can be considered valid. 
 *
 * @see org.apache.avalon.framework.service.ServiceManager
 * @see org.apache.avalon.framework.service.ServiceResolver
 *
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:pier@apache.org">Pierpaolo Fumagalli</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 */
public interface Serviceable
{
    /**
     * Pass the <code>ServiceManager</code> to the <code>servicable</code>.
     * The <code>Servicable</code> implementation should use the specified
     * <code>ServiceManager</code> to acquire the components it needs for
     * execution.
     *
     * @param manager The <code>ServiceManager</code> which this
     *                <code>Servicable</code> uses.
     */
    void service( ServiceManager manager )
        throws ServiceException;
}
