/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.manager;

import java.util.ArrayList;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;

/**
 * This is a hosted version of System Manager. It assumes
 * a MBeanServer is already running.
 *
 * @author <a href="mailto:sshort at postx.com">Steve Short</a>
 */
public class HostedSystemManager
    extends AbstractJMXManager
{
    protected MBeanServer createMBeanServer()
        throws Exception
    {
        ArrayList serverList = MBeanServerFactory.findMBeanServer( null );

        if( serverList.size() == 0 )
        {
            getLogger().debug( "HostedSystemManager createMBeanServer no MBeanServer could be found" );
            return null;
        }

        MBeanServer ms = (MBeanServer) serverList.get( 0 );
        getLogger().debug( "HostedSystemManager createMBeanServer \"" + ms + "\"" );
        return ms;
    }
}
