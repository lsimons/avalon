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
 * <p>Each Container-Component relationship will also involve defining
 * a contract between two entities. This contract will specify the
 * services, settings and information that is supplied by the
 * Container to the Component.</p>
 *
 * <p>This relationship should be documented in a well known place.
 * It is sometimes convenient to derive from Context to provide
 * a particular style of Context for your Component-Container
 * relationship. The documentation for required entries in context
 * can then be defined there. (examples include MailetContext,
 * BlockContext etc.)</p>
 *
 * <p>There are traditionally four differet types of Context that may be
 * used in a system. These ideas are partially derived from linguistic theory
 * and partially from tradition computer science;</p>
 *
 * <ol>
 *   <li>World Context / Per-Application context: This describes application
 *   wide settings/context. An example may be the working directory of the
 *   application.</li>
 *
 *   <li>Person Context / Per-Component context: This contains context
 *   information specific to the component. An example may be the name of
 *   the component.</li>
 *
 *   <li>Conversation Context / Per-Session context: This contains context
 *   information specific to the component. An example may be the IP address
 *   of the entity who you are talking to.</li>
 *
 *   <li>Speach Act Context / Per-Request context: This contains information
 *   about a specific request in component. Example may include the parameter
 *   submitted to a particular web form or whatever.</li>
 *
 * </ol>
 *
 * <p>When we implement this (1) and (2) are generally merged into one interface.
 * For instance in the Pheonix Application Server there is a BlockContext. Part
 * of the BlockContext consists of two methods. One is getHomeDirectory() and that
 * belongs to (1) while the other is getName() which belongs to (2).</p>
 *
 * <p>(4) is usually passed into a service() style method as parameters. Often it will
 * named something like RequestObject. So you may have something like:</p>
 *
 * <pre>
 * void doMagic( int param1, int param2, Context otherParamsInHere );
 * </pre>
 *
 * <p>When (3) is needed in the system it is usually also passed into the a serice method
 * method, along with the request context (4). Alternatively it is made available via the
 * context representing (4).</p>
 *
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:pier@apache.org">Pierpaolo Fumagalli</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public interface Context
{
    /**
     * Retrieve an object from Context.
     *
     * @param key the key into context
     * @return the object
     * @throws ContextException if object not found. Note that this
     *            means that either Component is asking for invalid entry
     *            or the Container is not living up to contract.
     */
    Object get( Object key )
        throws ContextException;
}
