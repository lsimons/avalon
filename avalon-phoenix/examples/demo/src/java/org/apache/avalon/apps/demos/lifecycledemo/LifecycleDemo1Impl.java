/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002,2003 The Apache Software Foundation. All rights
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
 * 4. The names "Avalon", and "Apache Software Foundation"
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
package org.apache.avalon.apps.demos.lifecycledemo;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

/**
 * A demo of the lifecycle methods.  Mount the SAR fle contaning there blocks in Phoenix, go
 * to the JMX console ..
 *   http://localhost:8082/mbean?objectname=Phoenix%3Aapplication%3Davalon-lifecycledemo%2Ctopic%3DApplication
 * .. and try stopt/starting the blocks.
 * @phoenix:block
 * @phoenix:service name="org.apache.avalon.apps.demos.lifecycledemo.LifecycleDemo1"
 * @author  Paul Hammant <Paul_Hammant@yahoo.com>
 * @version 1.0
 */
public class LifecycleDemo1Impl implements LogEnabled, Startable, Initializable, Contextualizable,
    Serviceable, Configurable, Disposable, LifecycleDemo1
{

    /**
     * The method from our service interface -> LifecycleDemo1
     * @return
     */
    public int myServiceMethod()
    {
        System.out.println( "LifecycleDemo1Impl.myServiceMethod() called." );
        System.out.flush();
        return 123;
    }

    public LifecycleDemo1Impl()
    {
        System.out.println( "LifecycleDemo1Impl.constructor() called. "
                            + "(You should never do too much in here)" );
        System.out.flush();
    }

    // Lifecycle methods themselves.


    /**
     * Enable Logging
     * @param logger The logger to use
     */
    public void enableLogging( Logger logger )
    {
        System.out.println( "LifecycleDemo1Impl.enableLogging() called." );
        System.out.flush();
    }

    /**
     * Start
     * @throws Exception If a problem
     */
    public void start() throws Exception
    {
        System.out.println( "LifecycleDemo1Impl.start() called." );
        System.out.flush();
    }

    /**
     * Stop
     * @throws Exception If a problem
     */
    public void stop() throws Exception
    {
        System.out.println( "LifecycleDemo1Impl.stop() called." );
        System.out.flush();
    }

    /**
     * Initialize
     * @throws Exception If a problem
     */
    public void initialize() throws Exception
    {
        System.out.println( "LifecycleDemo1Impl.initialize() called." );
        System.out.flush();
    }

    /**
     * Contextualize
     * @throws ContextException If a problem
     */
    public void contextualize( Context context ) throws ContextException
    {
        System.out.println( "LifecycleDemo1Impl.contextualize() called (things like base directory passed in here)." );
        System.out.flush();
    }

    /**
     * Service : No dependencies.
     * @param serviceManager
     * @throws ServiceException
     */
    public void service( ServiceManager serviceManager ) throws ServiceException
    {
        System.out.println( "LifecycleDemo1Impl.service() called (lookup on other services possible now)." );
        System.out.flush();
    }

    /**
     * Configure
     * @throws ConfigurationException If a problem
     */
    public void configure( Configuration configuration ) throws ConfigurationException
    {
        System.out.println( "LifecycleDemo1Impl.configure() called (configuration from config.xml passed here)." );
        System.out.flush();
    }

    /**
     * Dispose
     */
    public void dispose()
    {
        System.out.println( "LifecycleDemo1Impl.dispose() Called" );
        System.out.flush();
    }

}
