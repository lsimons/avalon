/* 
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
public class ComplexComponent
        implements ComplexService, Serviceable, Initializable, Startable, Disposable
{
    //-----------------------------------------------------------------
    // immutable
    //-----------------------------------------------------------------

    private final Logger m_logger;

    //-----------------------------------------------------------------
    // mutable
    //-----------------------------------------------------------------

    private ServiceManager m_manager;
    private SimpleService m_simple;
    private BasicService m_basic;
    private Thread m_thread;
    protected boolean m_continue = false;

    //-----------------------------------------------------------------
    // constructor
    //-----------------------------------------------------------------

    public ComplexComponent( Logger logger )
    {
        m_logger = logger;
    }

    //-----------------------------------------------------------------
    // Serviceable
    //-----------------------------------------------------------------

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

    private Logger getLogger()
    {
        return m_logger;
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

        Logger logger = getLogger().getChildLogger( "internal" );
        if( logger.isInfoEnabled() )
        {
            logger.debug( "ready" );
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
