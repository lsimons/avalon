/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.apps.sevak.blocks.catalina;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Server;

/**
 * Catalina SevakShutdownHook
 *
 * @author  Daniel Krieg<dkrieg@kc.rr.com>
 * @version $Revision: 1.1 $ $Date: 2002/09/29 11:38:42 $
 */
public class CatalinaSevakShutdownHook extends Thread
{

    private Server m_server;

    /**
     * Construct a shutdown hook.
     * @param server the server
     */
    public CatalinaSevakShutdownHook(Server server)
    {
        super("CatalinaSevakShutdownHook");
        m_server =server;
    }

    /**
     * Run as per Runnable.
     */
    public void run()
    {
        if (m_server != null)
        {
            try
            {
                ((Lifecycle) m_server).stop();
            }
            catch (LifecycleException e)
            {
                System.out.println("Catalina.stop: " + e);
                e.printStackTrace(System.out);
                if (e.getThrowable() != null)
                {
                    System.out.println("----- Root Cause -----");
                    e.getThrowable().printStackTrace(System.out);
                }
            }
        }
    }

}
