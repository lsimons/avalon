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
package org.apache.excalibur.instrument.manager.http.server;

import org.apache.avalon.framework.activity.Startable;

import org.apache.excalibur.instrument.AbstractLogEnabledInstrumentable;

/**
 *
 * @author Leif Mortenson <leif@tanukisoftware.com>
 * @version $Revision: 1.1 $
 */
abstract class AbstractLogEnabledInstrumentableStartable
    extends AbstractLogEnabledInstrumentable
    implements Startable, Runnable
{
    /** Reference to the worker thread. */
    private Thread m_runner;
    
    /** Flag set when the m_runner thread has been asked to stop. */
    private boolean m_runnerStop = false;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new AbstractLogEnabledInstrumentableStartable.
     */
    public AbstractLogEnabledInstrumentableStartable()
    {
        super();
    }
    
    /*---------------------------------------------------------------
     * Startable Methods
     *-------------------------------------------------------------*/
    /**
     * Starts the runner thread.
     *
     * @throws Exception If there are any problems.
     */
    public void start()
        throws Exception
    {
        getLogger().debug( "Starting..." );
        
        m_runner = new Thread( this, getInstrumentableName() + "_runner" );
        m_runner.start();
    }
    
    /**
     * Stops the runner thread, blocking until it has stopped.
     *
     * @throws Exception If there are any problems stopping the component.
     */
    public void stop()
        throws Exception
    {
        getLogger().debug( "Stopping." );
        
        Thread runner = m_runner;
        m_runnerStop = true;
        if ( runner != null )
        {
            runner.interrupt();
        }
        
        // Give the user code a change to stop cleanly.
        try
        {
            stopRunner();
        }
        catch ( Throwable t )
        {
            getLogger().error( "Encountered a problem while stopping the component.", t );
        }
        
        getLogger().debug( "Waiting for runner thread to stop." );
        synchronized ( this )
        {
            while ( m_runner != null )
            {
                try
                {
                    // Wait to be notified that the thread has exited.
                    this.wait();
                }
                catch ( InterruptedException e )
                {
                    // Ignore
                }
            }
        }
        getLogger().debug( "Stopped." );
    }
    
    /*---------------------------------------------------------------
     * Runable Methods
     *-------------------------------------------------------------*/
    /**
     * Run method which is responsible for launching the runner method and
     *  handling the shutdown cycle.
     */
    public void run()
    {
        if ( Thread.currentThread() != m_runner )
        {
            throw new IllegalStateException( "Private method." );
        }
        
        getLogger().debug( "Runner thread started." );
        
        try
        {
            try
            {
                runner();
            }
            catch ( Throwable t )
            {
                getLogger().warn(
                    "The runner method threw an uncaught exception, runner is terminating,", t );
            }
        }
        finally
        {
            synchronized ( this )
            {
                m_runner = null;
                
                // Wake up the stop method if it is waiting for the runner to stop.
                this.notify();
            }

            getLogger().debug( "Runner thread stopped." );
        }
    }
    
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Called when the component is being stopped, the isStopping method will
     *  always return true when this method is called.  This version of the
     *  method does nothing.
     *
     * @throws Exception If there are any problems
     */
    protected void stopRunner()
        throws Exception
    {
    }
    
    /**
     * Runner method that will be called when the component is started.
     *  The method must monitor the isStopping() method and make sure
     *  that it returns in a timely manner when the isStopping() method
     *  returns true.
     */
    protected abstract void runner();
    
    /**
     * Returns true when the component is in the process of being stopped.
     *
     * @return True when the component is in the process of being stopped.
     */
    public boolean isStopping()
    {
        return m_runnerStop;
    }
}
