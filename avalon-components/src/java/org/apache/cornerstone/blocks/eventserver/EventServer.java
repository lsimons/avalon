/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.blocks.eventserver;

import java.util.HashMap;
import java.util.Iterator;
import org.apache.avalon.AbstractLoggable;
import org.apache.avalon.Disposable;
import org.apache.phoenix.Block;
import org.apache.cornerstone.services.Scheduler;

/**
 * The <code>EventServer</code> implements a service for setting
 * and receiving alarms within Avalon. The <code>EventServer</code> allows to set
 * a listener on a specific event.
 *
 * @deprecated This class is going to be merged with org.apache.cornerstone.blocks.scheduler.*
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class EventServer
    extends AbstractLoggable
    implements Block, Scheduler, Disposable
{
    protected HashMap           m_alarms         = new HashMap();

    public void setAlarm( final String name, 
                          final Scheduler.EventTrigger eventTrigger, 
                          final Scheduler.Target target )
    {
        eventTrigger.initialize( name, target, this );
        new Thread( eventTrigger ).start();
        m_alarms.put( name, eventTrigger );
    }

    /**
     * Removes an alarm by name.  If the alarm does not exist throws RuntimeException.
     *
     * @param name The name of the alarm
     */
    public void removeAlarm( final String name ) 
    {
        shutdownAlarm( name );
        m_alarms.remove( name );
    }

    public void shutdownAlarm( final String name ) 
    {
        try { getTrigger( name ).stop(); }
        catch( final Exception e )
        {
            getLogger().warn( "Error occured stopping trigger " + name, e );
        }
    }    

    /**
     * Resets a named alarm to restart the timer.
     */
    public void resetAlarm( final String name )
    {
        getTrigger( name ).reset();
    }

    public void dispose()
    {
        final Iterator e = m_alarms.keySet().iterator();
        while(e.hasNext()) 
        {
            shutdownAlarm( (String)e.next() );
        }

        m_alarms.clear();
    }
    
    private Scheduler.EventTrigger getTrigger( final String name ) 
    {
        final Scheduler.EventTrigger eventTrigger = 
            (Scheduler.EventTrigger)m_alarms.get( name );

        if( null == eventTrigger ) 
        {
            throw new IllegalArgumentException( "No alarm named " + name );
        }

        return eventTrigger;
    }
}



