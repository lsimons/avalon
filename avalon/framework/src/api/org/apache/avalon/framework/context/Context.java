/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2002 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
 * <p>The values placed in the context are runtime values that can
 * only be provided by the container. The Context should <b>NOT</b> be
 * used to retrieve configuration values or services that can be provided
 * by peer components.</p>
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
 * @author <a href="mailto:avalon-dev@jakarta.apache.org">Avalon Development Team</a>
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
