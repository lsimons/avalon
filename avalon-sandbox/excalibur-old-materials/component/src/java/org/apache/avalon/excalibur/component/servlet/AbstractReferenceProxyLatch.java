/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.component.servlet;

import org.apache.avalon.excalibur.logger.LoggerManager;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.excalibur.instrument.InstrumentManager;

/**
 * Servlet containers do not have a guaranteed order in which servlets will
 *  be destroyed like there is with initialization.  This means that the
 *  servlet which created and controls an object may be destroyed while other
 *  servlets are still using it. This presents a problem in environments where
 *  common objects are placed into the ServletContext and used by more than
 *  one servlet.
 *
 * To solve this problem an object is placed into the ServletContext wrapped
 *  in a ReferenceProxy.  Whe nthe servlet is ready to be shutdown.  This
 *  proxy latch is used to wait until all other servlets are done with the
 *  components before disposing them.
 *
 * @author <a href="mailto:leif@apache.org">Leif Mortenson</a>
 * @version CVS $Revision: 1.3 $ $Date: 2002/11/07 05:11:35 $
 * @since 4.2
 */
abstract class AbstractReferenceProxyLatch
    extends AbstractLogEnabled
{
    /** Name of the latch */
    private String m_name;
    
    /** Number of registered proxies which have not yet been finalized. */
    private int m_waitingProxies;
    
    /** Flag that keeps track of when the trigger is requested. */
    private boolean m_triggerReq
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Create a new ReferenceProxyLatch.
     */
    public AbstractReferenceProxyLatch()
    {
    }

    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * @param object Object to be protected.
     * @param name Name of the object.
     * @return A new ReferenceProxy instance protecting the object.
     */
    public ReferenceProxy createProxy( Object object, String name )
    {
        m_name = name;

        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "Creating a proxy named '" + m_name + "' for a "
                               + object.getClass().getName() );
        }

        AbstractReferenceProxy proxy;
        if( object instanceof LoggerManager )
        {
            proxy = new LoggerManagerReferenceProxy( (LoggerManager)object, this, name );
        }
        else if( object instanceof ComponentManager )
        {
            proxy = new ComponentManagerReferenceProxy( (ComponentManager)object, this, name );
        }
        else if( object instanceof InstrumentManager )
        {
            proxy = new InstrumentManagerReferenceProxy( (InstrumentManager)object, this, name );
        }
        else
        {
            throw new IllegalArgumentException( "Don't know how to create a proxy for a "
                                                + object.getClass().getName() );
        }

        m_waitingProxies++;

        return proxy;
    }

    /**
     * Request that the triggered() method be called by asking all of the proxies
     *  managed by the latch to notify that they are no longer accepting requests
     *  to reference their internal objects.
     */
    public void requestTrigger()
    {
        int waitingProxies;
        synchronized( this )
        {
            waitingProxies = m_waitingProxies;
        }

        if( waitingProxies > 0 )
        {
            // Invoke garbage collection so that any proxies will be GCed if possible.
            System.gc();

            // Give the JVM a little time for the proxies to be GCed
            try
            {
                Thread.sleep( 1500 );
            }
            catch( InterruptedException e )
            {
            }
        }

        synchronized( this )
        {
            m_triggerRequested = true;
            waitingProxies = m_waitingProxies;
        }

        if( waitingProxies > 0 )
        {
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Trigger requested.  " + waitingProxies
                                   + " proxies have not yet been finalized." );
            }
        }
        else
        {
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Trigger requested.  All proxies have been finalized." );
            }

            try
            {
                triggered();
            }
            catch( Exception e )
            {
                getLogger().error( "Encountered an unexpected error in the trigger callback:", e );
            }
        }
    }

    /**
     * Called by a proxy when it is finalized.
     *
     * @proxy proxy The AbstractRefernceProxy that is ready.
     */
    void notifyFinalized( AbstractReferenceProxy proxy )
    {
        synchronized( this )
        {
            m_waitingProxies--;

            // Was that the last proxy?
            if( m_waitingProxies > 0 )
            {
                if( getLogger().isDebugEnabled() )
                {
                    getLogger().debug( "The proxy named '" + proxy.getName() + "' was finalized.  "
                                       + m_waitingProxies + " proxies remaining." );
                }
                return;
            }
        }
}
        }
        
        // Do this outside the synchronization block.
        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "The proxy named '" + proxy.getName() + "' was finalized.  "
                + "All proxies have been finalized." );
        }
        
        if ( m_triggerRequested )
        {
            try
            {
                triggered();
            }
            catch ( Exception e )
            {
                getLogger().error( "Encountered an unexpected error in the trigger callback:", e );
            }
        }
    }
    
    /**
     * Called when all of the proxies have notified that they are done.
     */
    public abstract void triggered() throws Exception;
}
