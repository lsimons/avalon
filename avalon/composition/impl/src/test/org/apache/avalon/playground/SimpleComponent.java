/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation. All rights
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

package org.apache.avalon.playground;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.playground.basic.BasicService;

/**
 * This is a minimal demonstration component that a dependency on
 * BasicService and provides SimpleService.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 */
public class SimpleComponent extends AbstractLogEnabled
        implements Configurable, Serviceable, Initializable, Startable, SimpleService,
        Exploitable, Demonstratable, Disposable
{

    private String m_message;
    private BasicService m_basic;
    private Thread m_thread;
    protected boolean m_continuation = false;

    //=======================================================================
    // Configurable
    //=======================================================================

    /**
     * Configurate the component.
     * @param config the configuration
     */
    public void configure( Configuration config )
    {
        getLogger().info( "configure" );
        m_message = config.getChild( "message" ).getValue( null );
    }

    //=======================================================================
    // Serviceable
    //=======================================================================

    /**
     * Service the component.
     * @param manager the service manager holding the depedent services
     * @exception ServiceException if a service error occurs
     */
    public void service( ServiceManager manager ) throws ServiceException
    {
        getLogger().info( "service" );
        m_basic = (BasicService) manager.lookup( "basic" );
        manager.release( m_basic );
    }

    //=======================================================================
    // Exploitable
    //=======================================================================

    /**
     * The create stage interface implementation for the Exloitable extension.
     */
    public void incarnate()
    {
        getLogger().info( "incarnation stage" );
    }

    /**
     * The destroy stage interface implementation for the Exloitable extension.
     */
    public void etherialize()
    {
        getLogger().info( "etherialize stage" );
    }


    //=======================================================================
    // Demonstratable
    //=======================================================================

    /**
     * Prints out the supplied message.
     * @param message the message to print
     */
    public void demo( String message )
    {
        getLogger().info( "handling demonstratable stage: " + message );
    }

    //=======================================================================
    // Initializable
    //=======================================================================

    /**
     * Initialization of the component.
     */
    public void initialize()
    {
        getLogger().info( "initialize" );
        getLogger().debug( "context: " + Thread.currentThread().getContextClassLoader() );
        doObjective();
    }

    //=======================================================================
    // Startable
    //=======================================================================

    /**
     * Starts the component.
     * @exception Exception if an error occurs
     */
    public void start() throws Exception
    {
        getLogger().debug( "starting" );
        m_continuation = true;
        m_thread = new Thread(
                new Runnable()
                {
                    public void run()
                    {
                        while( m_continuation )
                        {
                            try
                            {
                                Thread.sleep( 100 );
                            } catch( Throwable e )
                            {
                                // ignore it
                            }
                        }
                    }
                }
        );
        m_thread.start();
        getLogger().debug( "started" );
    }

    /**
     * Stops the component.
     */
    public void stop()
    {
        getLogger().info( "stopping" );
        m_continuation = false;
        try
        {
            m_thread.join();
        } catch( Throwable e )
        {
            // timeout
        }
    }

    //=======================================================================
    // Disposable
    //=======================================================================

    /**
     * Dispose of the component.
     */
    public void dispose()
    {
        getLogger().info( "dispose" );
    }

    //=======================================================================
    // PrimaryService
    //=======================================================================

    /**
     * Prints out the message derived from the configuration.
     */
    public void doObjective()
    {
        getLogger().info( "handling service operation: " + m_message );
    }

}
