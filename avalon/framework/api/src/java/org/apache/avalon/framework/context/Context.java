/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework.context;

/**
 * The context is the interface through which the Component
 * and it's Container communicate.
 *
 * Each Container-Component relationship will also involve defining
 * a contract between two entities. This contract will specify the
 * services, settings and information that is supplied by the
 * Container to the Component.
 *
 * This relationship should be documented in a well known place.
 * It is sometimes convenient to derive from Context to provide
 * a particular style of Context for your Component-Container
 * relationship. The documentation for required entries in context
 * can then be defined there. (examples include MailetContext,
 * BlockContext etc.)
 *
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:pier@apache.org">Pierpaolo Fumagalli</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public interface Context
{
    /**
     * Retrieve an object from Context.
     *
     * @param key the key into context
     * @return the object
     * @exception ContextException if object not found. Note that this
     *            means that either Component is asking for invalid entry
     *            or the Container is not living up to contract.
     */
    Object get( Object key )
        throws ContextException;
}
