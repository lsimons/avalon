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
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.playground.basic.BasicService;

/**
 * This is a demonstration component that declares no interface but
 * has dependecies on two services.  These include SimpleService and
 * BasicService.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 */

public class ComplexComponent extends AbstractLogEnabled
        implements ComplexService, Serviceable, Initializable, Startable, Disposable
{

    private ServiceManager m_manager;
    private SimpleService m_simple;
    private BasicService m_basic;
    private Thread m_thread;
    protected boolean m_continue = false;

    //=================================================================
    // Serviceable
    //=================================================================

    /**
     * Pass the <code>ServiceManager</code> to the <code>Serviceable</code>.
     * The <code>Serviceable</code> implementation uses the specified
     * <code>ServiceManager</code> to acquire the services it needs for
     * execution.
     *
     * @param manager The <code>ServiceManager</code> which this
     *                <code>Serviceable</code> uses.
     */
    public void service( ServiceManager manager )
    {
        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "service" );
        }
        m_manager = manager;
    }


    //=======================================================================
    // Initializable
    //=======================================================================

    /**
     * Initialization of the componet.
     * @exception Exception if an initialization error occurs
     */
    public void initialize()
            throws Exception
    {
        if( getLogger().isInfoEnabled() )
        {
            getLogger().info( "initialize" );
        }

        //
        // verify current state
        //

        if( getLogger() == null )
        {
            throw new IllegalStateException(
                    "Logging channel has not been assigned." );
        }

        if( m_manager == null )
        {
            throw new IllegalStateException(
                    "Manager has not been declared." );
        }

        //
        // lookup the primary service
        //

        m_simple = (SimpleService) m_manager.lookup( "simple" );
        m_basic = (BasicService) m_manager.lookup( "basic" );

        getLogger().info( "simple: " + m_simple );
        getLogger().info( "basic: " + m_basic );

        Logger logger = getLogger().getChildLogger( "internal" );
        if( logger.isInfoEnabled() )
        {
            logger.info( "ready" );
        }
    }

    //=======================================================================
    // Startable
    //=======================================================================

    /**
     * Start the component.
     * @exception Exception if an error eoccurs
     */
    public void start() throws Exception
    {
        getLogger().info( "starting" );
        m_continue = true;
        m_thread = new Thread(
                new Runnable()
                {
                    public void run()
                    {
                        while( m_continue )
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
        getLogger().info( "started" );
    }

    /**
     * Stop the component.
     */
    public void stop()
    {
        getLogger().info( "stopping" );
        m_continue = false;
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
        if( getLogger().isInfoEnabled() )
        {
            getLogger().info( "dispose" );
        }

        m_manager.release( m_simple );
        m_manager.release( m_basic );
        m_manager = null;
    }
}
