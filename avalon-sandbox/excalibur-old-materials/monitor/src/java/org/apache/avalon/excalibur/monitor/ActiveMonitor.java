/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.monitor;

import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 * The ActiveMonitor is used to actively check a set of resources to see if they have
 * changed.  It will be implemented as a Component, that can be retrieved from
 * the ComponentLocator.  It defaults to checking every 1 minute.  The configuration
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
 * @version $Id: ActiveMonitor.java,v 1.14 2002/09/07 12:14:01 donaldp Exp $
 */
public final class ActiveMonitor
    extends org.apache.avalon.excalibur.monitor.impl.ActiveMonitor
    implements LogEnabled, Configurable, Startable, ThreadSafe
{
    private Logger m_logger;

    public void enableLogging( final Logger logger )
    {
        m_logger = logger;
    }

    /**
     * Configure the ActiveMonitor.
     */
    public final void configure( final Configuration config )
        throws ConfigurationException
    {
        final Configuration thread = config.getChild( "thread" );
        final long frequency =
            thread.getAttributeAsLong( "frequency", 1000L * 60L );
        final int priority =
            thread.getAttributeAsInteger( "priority", Thread.MIN_PRIORITY );

        setFrequency( frequency );
        setPriority( priority );

        if( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "Active monitor will sample all resources every " +
                            frequency + " milliseconds with a thread priority of " +
                            priority + "(Minimum = " + Thread.MIN_PRIORITY +
                            ", Normal = " + Thread.NORM_PRIORITY +
                            ", Maximum = " + Thread.MAX_PRIORITY + ")." );
        }

        final Configuration[] resourcesConfig =
            config.getChild( "init-resources" ).getChildren( "resource" );
        final Resource[] resources =
            MonitorUtil.configureResources( resourcesConfig, m_logger );
        addResources( resources );

    }
}
