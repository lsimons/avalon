/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.monitor;

import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.framework.thread.ThreadSafe;

import java.lang.reflect.Constructor;

import java.util.Map;
import java.util.HashMap;

/**
 * The ActiveMonitor is used to actively check a set of resources to see if they have
 * changed.  It will be implemented as a Component, that can be retrieved from
 * the ComponentManager.  It defaults to checking every 1 minute.  The configuration
 * looks like this:
 *
 * <pre>
 *   &lt;monitor&gt;
 *     &lt;thread priority="<i>5</i>" frequency="<i>60000</i>"/&gt;
 *     &lt;init-resources&gt;
 *       &lt;-- This entry can be repeated for every resource you want to register immediately --&gt;
 *
 *       &lt;resource key="<i>file:./myfile.html</i>" class="<i>org.apache.avalon.excalibur.monitor.FileResource</i>"/&gt;
 *     &lt;/init-resources&gt;
 *   &lt;/monitor&gt;
 * </pre>
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version $Id: ActiveMonitor.java,v 1.4 2001/12/11 16:14:31 bloritsch Exp $
 */
public final class ActiveMonitor extends AbstractLoggable
    implements Monitor, Component, Startable, ThreadSafe, Configurable, Runnable
{
    private static final Class[]    m_constructorParams = new Class[] { String.class };
    private static final Resource[] m_arrayType         =  new Resource[] {};
    private        final Thread     m_monitorThread     = new Thread( this );
    private              long       m_frequency;
    private              int        m_priority;
    private              Map        m_resources         = new HashMap();
    private              boolean    m_keepRunning       = true;

    /**
     * Configure the ActiveMonitor.
     */
    public final void configure( Configuration conf )
        throws ConfigurationException
    {
        m_frequency = conf.getChild("thread").getAttributeAsLong("frequency", 1000L * 60L );
        m_priority = conf.getChild("thread").getAttributeAsInteger("priority", Thread.MIN_PRIORITY);
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Configuration[] initialResources = conf.getChild("init-resources").getChildren("resource");

        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug("Active monitor will sample all resources every " +
                              m_frequency + " milliseconds with a thread priority of " +
                              m_priority + "(Minimum = " + Thread.MIN_PRIORITY +
                              ", Normal = " + Thread.NORM_PRIORITY +
                              ", Maximum = " + Thread.MAX_PRIORITY + ").");
        }

        for ( int i = 0; i < initialResources.length; i++ )
        {
            String key = initialResources[i].getAttribute( "key", "*** KEY NOT SPECIFIED ***" );
            String className = initialResources[i].getAttribute( "class", "*** CLASSNAME NOT SPECIFIED ***" );

            try
            {
                Class clazz = loader.loadClass( className );
                Constructor initializer = clazz.getConstructor( ActiveMonitor.m_constructorParams );
                this.addResource( (Resource) initializer.newInstance( new Object[] { key } ) );

                if (getLogger().isDebugEnabled())
                {
                    getLogger().debug("Initial Resource: \"" + key + "\" Initialized.");
                }
            }
            catch ( Exception e )
            {
                if ( getLogger().isWarnEnabled() )
                {
                    getLogger().warn( "Initial Resource: \"" + key +
                            "\" Failed (" + className + ").", e );
                }
            }
        }
    }

    public final void start()
        throws Exception
    {
        m_monitorThread.setDaemon( true );
        m_monitorThread.setPriority( Thread.MIN_PRIORITY );
        m_monitorThread.start();
    }

    public final void stop()
        throws Exception
    {
        m_keepRunning = false;
        m_monitorThread.join();
    }

    /**
     * Add a resource to monitor.  The resource key referenced in the other
     * interfaces is derived from the resource object.
     */
    public final void addResource( final Resource resource )
    {

        synchronized ( m_resources )
        {
            if ( m_resources.containsKey( resource.getResourceKey() ) )
            {
                Resource original = (Resource) m_resources.get( resource.getResourceKey() );
                original.addPropertyChangeListenersFrom( resource );
            }
            else
            {
                m_resources.put( resource.getResourceKey(), resource );
            }
        }
    }

    /**
     * Find a monitored resource.  If no resource is available, return null
     */
    public final Resource getResource( final String key )
    {
        synchronized (m_resources)
        {
            return (Resource) m_resources.get( key );
        }
    }

    /**
     * Remove a monitored resource by key.
     */
    public final void removeResource( final String key )
    {
        synchronized (m_resources)
        {
            Resource resource = (Resource) m_resources.remove( key );
            resource.removeAllPropertyChangeListeners();
        }
    }

    /**
     * Remove a monitored resource by reference.
     */
    public final void removeResource( final Resource resource )
    {
        this.removeResource( resource.getResourceKey() );
    }

    public final void run()
    {
        while( m_keepRunning )
        {
            long currentTestTime = System.currentTimeMillis();
            long sleepTillTime = currentTestTime + m_frequency;

            while ( System.currentTimeMillis() < sleepTillTime )
            {
                try
                {
                    Thread.sleep( sleepTillTime - System.currentTimeMillis() );
                }
                catch ( InterruptedException e )
                {
                    // ignore interrupted exception and keep sleeping until it's
                    // time to wake up
                }
            }

            Resource[] resources;

            synchronized (m_resources)
            {
                resources = (Resource[]) m_resources.values().toArray( ActiveMonitor.m_arrayType );
            }

            for ( int i = 0; i < resources.length; i++ )
            {
                resources[i].testModifiedAfter( currentTestTime );
            }
        }
    }
}
