/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.monitor;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 * The PassiveMonitor is used to passively check a set of resources to see if they have
 * changed.  It will be implemented as a Component, that can be retrieved from
 * the ComponentLocator.  It defaults to checking every 1 minute.  The configuration
 * looks like this:
 *
 * <pre>
 *   &lt;monitor&gt;
 *     &lt;init-resources&gt;
 *       &lt;-- This entry can be repeated for every resource you want to register immediately --&gt;
 *
 *       &lt;resource key="<i>file:./myfile.html</i>" class="<i>org.apache.avalon.excalibur.monitor.FileMonitor</i>"/&gt;
 *     &lt;/init-resources&gt;
 *   &lt;/monitor&gt;
 * </pre>
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version $Id: PassiveMonitor.java,v 1.11 2002/09/07 12:14:01 donaldp Exp $
 */
public final class PassiveMonitor
    extends org.apache.avalon.excalibur.monitor.impl.PassiveMonitor
    implements LogEnabled, Configurable, ThreadSafe
{
    private Logger m_logger;

    public void enableLogging( final Logger logger )
    {
        m_logger = logger;
    }

    public final void configure( final Configuration config )
        throws ConfigurationException
    {
        final Configuration[] initialResources =
            config.getChild( "init-resources" ).getChildren( "resource" );
        final Resource[] resources =
            MonitorUtil.configureResources( initialResources, m_logger );
        addResources( resources );
    }
}
