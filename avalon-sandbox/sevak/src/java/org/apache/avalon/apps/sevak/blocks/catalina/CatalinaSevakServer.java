/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.apps.sevak.blocks.catalina;

import org.apache.catalina.Deployer;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.deploy.NamingResources;
import org.apache.catalina.util.LifecycleSupport;

/**
 * Catalina Sevak Server
 *
 * @author  Daniel Krieg<dkrieg@kc.rr.com>
 * @version $Revision: 1.1 $ $Date: 2002/09/29 11:38:42 $
 */
public class CatalinaSevakServer implements Server, Lifecycle, Runnable
{
    private static final String C_INFO = CatalinaSevakServer.class.getName();
    private LifecycleSupport m_lifecycleSupport = new LifecycleSupport(this);
    private String m_shutdown = "SHUTDOWN";
    private Service[] m_services = new Service[0];
    private int m_port = 8005;
    private boolean m_initialized = false;
    private boolean m_started = false;
    private NamingResources m_globalNamingResources;
    private boolean m_serving = false;

    /**
     * As per runnable
     */
    public void run()
    {
        while (m_serving)
        {
            try
            {
                Thread.sleep(3 * 1000); // Three seconds
            }
            catch (InterruptedException e)
            {
                System.out.println("CatalinaSevakServer.run: " + e);
            }
        }
    }

    /**
     * Await
     */
    public void await()
    {
        Thread t = new Thread(this);
        t.start();
    }

    /**
     * Start
     * @throws LifecycleException if a problem
     */
    public void start() throws LifecycleException
    {
        if (m_started)
        {
            throw new LifecycleException("This server has already been started");
        }

        m_lifecycleSupport.fireLifecycleEvent(BEFORE_START_EVENT, null);
        m_lifecycleSupport.fireLifecycleEvent(START_EVENT, null);
        m_started = true;

        synchronized (m_services)
        {
            for (int i = 0; i < m_services.length; i++)
            {
                if (m_services[i] instanceof Lifecycle)
                {
                    ((Lifecycle) m_services[i]).start();
                }
            }
        }

        m_lifecycleSupport.fireLifecycleEvent(AFTER_START_EVENT, null);
    }

    /**
     * Stop
     * @throws LifecycleException if a problem
     */
    public void stop() throws LifecycleException
    {
        if (!m_started)
        {
            throw new LifecycleException("This server has not yet been started");
        }

        m_lifecycleSupport.fireLifecycleEvent(BEFORE_STOP_EVENT, null);
        m_lifecycleSupport.fireLifecycleEvent(STOP_EVENT, null);
        m_started = false;

        for (int i = 0; i < m_services.length; i++)
        {
            if (m_services[i] instanceof Lifecycle)
            {
                ((Lifecycle) m_services[i]).stop();
            }
        }

        m_lifecycleSupport.fireLifecycleEvent(AFTER_STOP_EVENT, null);
    }

    /**
     * Initialize
     * @throws LifecycleException if a problem
     */
    public void initialize() throws LifecycleException
    {
        if (m_initialized)
        {
            throw new LifecycleException("This server has already been initialized");
        }
        m_initialized = true;

        for (int i = 0; i < m_services.length; i++)
        {
            m_services[i].initialize();
        }
    }

    /**
     * Get the GlobalNamingReources
     * @return the naming resources
     */
    public NamingResources getGlobalNamingResources()
    {
        return m_globalNamingResources;
    }

    /**
     * Set the global naming resources
     * @param globalNamingResources the global naming resources.
     */
    public void setGlobalNamingResources(NamingResources globalNamingResources)
    {
        m_globalNamingResources = globalNamingResources;
        m_globalNamingResources.setContainer(this);
    }

    /**
     * Add the service
     * @param service the service
     */
    public void addService(final Service service)
    {
        service.setServer(this);

        synchronized (m_services)
        {
            Service[] services = new Service[m_services.length + 1];
            System.arraycopy(m_services, 0, services, 0, m_services.length);
            services[m_services.length] = service;
            m_services = services;

            if (m_initialized)
            {
                try
                {
                    service.initialize();
                }
                catch (LifecycleException e)
                {
                    e.printStackTrace(System.err);
                }
            }

            if (m_started && (service instanceof Lifecycle))
            {
                try
                {
                    ((Lifecycle) service).start();
                }
                catch (LifecycleException e)
                {
                    // nothing?
                }
            }
        }
    }

    /**
     * Find a service
     * @param name the service name
     * @return the service
     */
    public Service findService(String name)
    {

        if (name == null)
        {
            return (null);
        }
        synchronized (m_services)
        {
            for (int i = 0; i < m_services.length; i++)
            {
                if (name.equals(m_services[i].getName()))
                {
                    return (m_services[i]);
                }
            }
        }
        return (null);

    }

    /**
     * Find all services
     * @return the services
     */
    public Service[] findServices()
    {

        return (m_services);

    }

    /**
     * Remove a service
     * @param service the service
     */
    public void removeService(Service service)
    {
        synchronized (m_services)
        {
            int j = -1;
            for (int i = 0; i < m_services.length; i++)
            {
                if (service == m_services[i])
                {
                    j = i;
                    break;
                }
            }
            if (j < 0)
            {
                return;
            }
            if (m_services[j] instanceof Lifecycle)
            {
                try
                {
                    ((Lifecycle) m_services[j]).stop();
                }
                catch (LifecycleException e)
                {
                    // nothing?
                }
            }
            int k = 0;
            Service results[] = new Service[m_services.length - 1];
            for (int i = 0; i < m_services.length; i++)
            {
                if (i != j)
                {
                    results[k++] = m_services[i];
                }
            }
            m_services = results;
        }
    }

    /**
     * Get the port
     * @return the port
     */
    public int getPort()
    {
        return m_port;
    }

    /**
     * Set the port
     * @param port the port
     */
    public void setPort(int port)
    {
        m_port = port;
    }

    /**
     * Get info
     * @return info
     */
    public String getInfo()
    {
        return C_INFO;
    }

    /**
     * Get shutdown property
     * @return the shutdown property
     */
    public String getShutdown()
    {
        return m_shutdown;
    }

    /**
     * Set the shutdown property
     * @param shutdown the shutdown property
     */
    public void setShutdown(String shutdown)
    {
        m_shutdown = shutdown;
    }

    /**
     * Add a lifecycle listener
     * @param listener the listener
     */
    public void addLifecycleListener(LifecycleListener listener)
    {
        m_lifecycleSupport.addLifecycleListener(listener);
    }

    /**
     * Find lifecycle listeners.
     * @return the LifeCycle listeners
     */
    public LifecycleListener[] findLifecycleListeners()
    {
        return m_lifecycleSupport.findLifecycleListeners();
    }

    /**
     * Remove lifecycle listeners.
     * @param listener the listener
     */
    public void removeLifecycleListener(LifecycleListener listener)
    {
        m_lifecycleSupport.removeLifecycleListener(listener);
    }

    /**
     * A string rep of this instance
     * @return the string
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer("CatalinaSevakServer[");
        sb.append(getPort());
        sb.append("]");
        return (sb.toString());
    }

    /**
     * Get the deployer
     * @return the deployer
     */
    public Deployer getDeployer()
    {
        return null;
    }
}
