/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.monitor.impl;

/**
 * The ActiveMonitor is used to actively check a set of resources to see if they have
 * changed. It will poll the resources with a frequency as specified or if
 * unspecified with the default (60 seconds).
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Id: ActiveMonitor.java,v 1.3 2002/09/08 00:02:45 donaldp Exp $
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
    private boolean m_keepRunning = true;

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
        m_monitorThread.join();
    }

    public final void run()
    {
        while( m_keepRunning )
        {
            long currentTestTime = System.currentTimeMillis();
            final long sleepTillTime = currentTestTime + m_frequency;

            while( (currentTestTime = System.currentTimeMillis()) < sleepTillTime )
            {
                delay( sleepTillTime - currentTestTime );
            }

            scanAllResources();
        }
    }

    private void delay( final long delay )
    {
        try
        {
            Thread.sleep( delay );
        }
        catch( InterruptedException e )
        {
            // ignore interrupted exception and keep sleeping until it's
            // time to wake up
        }
    }
}
