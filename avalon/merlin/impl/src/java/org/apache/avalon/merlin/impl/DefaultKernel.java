/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

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

package org.apache.avalon.merlin.impl;

import java.net.URL;
import java.util.LinkedList;
import java.util.Iterator;

import org.apache.avalon.merlin.Kernel;
import org.apache.avalon.merlin.KernelContext;
import org.apache.avalon.merlin.KernelError;
import org.apache.avalon.merlin.KernelException;
import org.apache.avalon.merlin.KernelRuntimeException;
import org.apache.avalon.merlin.event.KernelEventListener;
import org.apache.avalon.merlin.event.KernelStateEvent;

import org.apache.avalon.activation.appliance.Appliance;
import org.apache.avalon.activation.appliance.Block;
import org.apache.avalon.activation.appliance.impl.DefaultBlock;

import org.apache.avalon.composition.data.TargetDirective;
import org.apache.avalon.composition.logging.LoggingManager;
import org.apache.avalon.composition.model.ContainmentContext;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.ComponentModel;
import org.apache.avalon.composition.model.SystemContext;
import org.apache.avalon.composition.util.StringHelper;

import org.apache.avalon.util.exception.ExceptionHelper;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.Logger;

/**
 * Implementation of the default Merlin Kernel.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.3 $ $Date: 2004/01/13 18:43:15 $
 */
public class DefaultKernel implements Kernel
{

    //--------------------------------------------------------------
    // immutable state
    //--------------------------------------------------------------

    private final LinkedList m_listeners = new LinkedList();

    private final KernelContext m_context;

    private final Block m_application;

    private final Block m_system;

    private final State m_state;

    //--------------------------------------------------------------
    // constructor
    //--------------------------------------------------------------

   /**
    * Creation of a new Merlin Kernel.
    * @param context the creation context
    * @exception KernelException if a kernel initialization error occurs
    */
    public DefaultKernel( KernelContext context ) throws KernelException
    {
        if( context == null ) 
          throw new NullPointerException( "context" );

        m_context = context;
        m_state = new State( this );

        setState( INITIALIZING );

        try
        {
            ContainmentModel facilities = 
              context.getFacilitiesModel();
            facilities.assemble();
            DefaultBlock system = 
              new DefaultBlock( facilities );
            system.deploy();
            m_system = system;
        }
        catch( Throwable e )
        {
            final String error = 
              "Cannot create system facilities.";
            throw new KernelError( error, e );
        }


        try
        {
            m_application = 
              new DefaultBlock( context.getApplicationModel() );
        }
        catch( Throwable e )
        {
            final String error = 
              "Cannot create application runtime.";
            throw new KernelError( error, e );
        }

        setState( INITIALIZED );
        if( getLogger().isDebugEnabled() )
        {
            m_context.getLogger().debug( "kernel established" );
        }
        setState( STOPPED );
    }

    //--------------------------------------------------------------
    // Kernel
    //--------------------------------------------------------------

   /**
    * Add a kernel listener.
    * @param listener the kernel listener to be added
    */
    public void addKernelEventListener( KernelEventListener listener )
    {
        m_state.addKernelEventListener( listener );
    }

   /**
    * Remove a kernel listener.
    * @param listener the kernel listener to be removed
    */
    public void removeKernelEventListener( KernelEventListener listener )
    {
        m_state.removeKernelEventListener( listener );
    }

   /**
    * Return the appliance matching the supplied path.
    * @param path an appliance path
    * @return the corresponding appliance
    * @exception KernelException if the path is unknown
    */
    public Appliance locate( String path ) throws KernelException
    {
        try
        {
            return m_application.locate( path );
        }
        catch( Throwable e )
        {
            final String error = 
              "Unable to resolve appliance relative to the path: [" 
              + path + "].";
            throw new KernelException( error, e );
        }
    }

   /**
    * Return the root application block.
    * @return the application containment block
    */
    public Block getBlock()
    {
        return m_application;
    }

    //--------------------------------------------------------------
    // DefaultKernel
    //--------------------------------------------------------------

   /**
    * Initiate the establishment of the root container.
    * @exception Exception if a startup error occurs
    */
    public void startup() throws Exception
    {
        //
        // instantiate the runtime root application block
        //

        synchronized( m_state )
        {
            if( m_state.getState() != STOPPED ) return;

            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "application assembly" );
            }

            try
            {
                setState( ASSEMBLY );
                m_application.getModel().assemble();
            }
            catch( Throwable e )
            {
                setState( INITIALIZED );
                final String error =
                  "Cannot assemble application.";
                throw new KernelException( error, e );
            }

            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "application deployment" );
            }

            try
            {
                setState( DEPLOYMENT );
                m_application.deploy();
            }
            catch( Throwable e )
            {
                setState( INITIALIZED );
                final String error =
                  "Cannot deploy application.";
                throw new KernelException( error, e );
            }
            
            setState( STARTED );
        }
    }

   /**
    * Shutdown the kernel during which orderly shutdown of all
    * installed blocks is undertaken.
    */
    public void shutdown()
    {
        synchronized( m_state )
        {
            if( m_state.getState() != STARTED ) return;

            setState( STOPPING );

            try
            {
                setState( DECOMMISSIONING );
                m_application.decommission();
            }
            catch( Throwable e )
            {
                if( getLogger().isWarnEnabled() )
                {
                    final String error =
                      "Ignoring block decommissioning error.";
                    getLogger().warn( error, e );
                }
            }

            /*
            try
            {
                setState( DISSASSEMBLY );
                getLogger().info( "dissassembly phase" );
                m_model.disassemble();
            }
            catch( Throwable e )
            {
                if( getLogger().isWarnEnabled() )
                {
                    final String error =
                      "Ignoring application dissassembly error.";
                    getLogger().warn( error, e );
                }
            }
            */

            if( getLogger().isDebugEnabled() )
            {
                int n = Thread.activeCount();
                getLogger().debug( "active threads (" + n + ")" );
            }

            setState( STOPPED );
        }
    }

    //--------------------------------------------------------------
    // internal
    //--------------------------------------------------------------

    /**
     * Set the state of the kernel.  The method also triggers the 
     * emmission of a attribute change notification containing the 
     * old and new state value.
     *
     * @param state a string representing the new kernel state
     */
     private void setState( int state )
     {
         if( getLogger().isDebugEnabled() )
         {
             getLogger().debug( "state: " + state );
         }
         m_state.setState( state );
     }

    private Logger getLogger()
    {
        return m_context.getLogger();
    }


    private class State
    {
        private int m_state = INITIALIZING;

        private LinkedList m_listeners = new LinkedList();

        private final Kernel m_kernel;

        State( Kernel kernel )
        {
            m_kernel = kernel;
        }

        public void addKernelEventListener( KernelEventListener listener )
        {
            synchronized( m_listeners )
            {
                m_listeners.add( listener );
            }
        }

        public void removeKernelEventListener( KernelEventListener listener )
        {
            synchronized( m_listeners )
            {
                m_listeners.remove( listener );
            }
        }

        public int getState()
        {
            return m_state;
        }

        public synchronized void setState( int state )
        {
            int oldValue = m_state;
            int newValue = state;

            m_state = newValue;

            KernelStateEvent event = 
              new KernelStateEvent( m_kernel, oldValue, newValue );
            fireStateChangedEvent( event );
        }

        private void fireStateChangedEvent( final KernelStateEvent event )
        {
            Iterator iterator = m_listeners.iterator();
            while( iterator.hasNext() )
            {
                final KernelEventListener listener = 
                  (KernelEventListener) iterator.next();
                try
                {
                    listener.stateChanged( event );
                }
                catch( Throwable e )
                {
                    final String error = 
                      ExceptionHelper.packException( e, true );
                    getLogger().warn( error );
                }
            }
        }
    }
}
