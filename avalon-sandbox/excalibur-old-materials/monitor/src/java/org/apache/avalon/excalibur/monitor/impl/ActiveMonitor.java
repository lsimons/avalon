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
package org.apache.avalon.excalibur.monitor.impl;

/**
 * The ActiveMonitor is used to actively check a set of resources to see if they have
 * changed. It will poll the resources with a frequency as specified or if
 * unspecified with the default (60 seconds).
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Id: ActiveMonitor.java,v 1.7 2003/03/22 12:46:50 leosimons Exp $
 */
public class ActiveMonitor
    extends AbstractMonitor
    implements Runnable
{
    private static final long DEFAULT_FREQUENCY = 1000L * 60L;

    /**
     * The frequency to scan resources for changes measured
     * in milliseconds.
     */
    private long m_frequency = DEFAULT_FREQUENCY;

    /**
     * The priority of the thread that monitors resources. Defaults
     * to System specific {@link Thread#MIN_PRIORITY}.
     */
    private int m_priority = Thread.MIN_PRIORITY;

    /**
     * The thread that does the monitoring.
     */
    private final Thread m_monitorThread = new Thread( this );

    /**
     * Set to false to shutdown the thread.
     */
    private volatile boolean m_keepRunning = true;

    /**
     * Set the frequency with which the monitor
     * checks the resources. This can be changed
     * anytime and will be enabled the next time
     * through the check.
     *
     * @param frequency the frequency to scan resources for changes
     */
    public void setFrequency( final long frequency )
    {
        m_frequency = frequency;
    }

    /**
     * Set the priority of the active monitors thread.
     *
     * @param priority the priority of the active monitors thread.
     */
    public void setPriority( final int priority )
    {
        m_priority = priority;
    }

    public void start()
        throws Exception
    {
        m_keepRunning = true;
        m_monitorThread.setDaemon( true );
        m_monitorThread.setPriority( m_priority );
        m_monitorThread.start();
    }

    public void stop()
        throws Exception
    {
        m_keepRunning = false;
        m_monitorThread.interrupt();
        m_monitorThread.join();
    }

    public final void run()
    {
        try
        {
            while( m_keepRunning )
            {
                Thread.sleep( m_frequency );
                scanAllResources();
            }
        }
        catch( InterruptedException e )
        {
            // clears the interrupted status
            Thread.interrupted();
        }
    }
}
