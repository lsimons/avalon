/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"
    must not be used to endorse or promote products derived from this  software
    without  prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/
package org.apache.avalon.excalibur.component.servlet;

import org.apache.avalon.excalibur.logger.LoggerManager;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceManager;
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
 * @deprecated ECM is no longer supported
 *
 * @author <a href="mailto:leif@apache.org">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 12:45:27 $
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
    private boolean m_triggerRequested;

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
        else if( object instanceof ServiceManager )
        {
            proxy = new ServiceManagerReferenceProxy( (ServiceManager)object, this, name );
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
     * @param proxy The AbstractRefernceProxy that is ready.
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
