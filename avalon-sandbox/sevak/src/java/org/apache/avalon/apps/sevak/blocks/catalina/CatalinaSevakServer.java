package org.apache.avalon.apps.sevak.blocks.catalina;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.deploy.NamingResources;
import org.apache.catalina.core.StandardServer;


public class CatalinaSevakServer implements Server, Lifecycle, Runnable {
    private final StandardServer m_standardServer = new StandardServer();
    private boolean m_serving;

    public String getInfo() {
        return m_standardServer.getInfo();
    }

    public NamingResources getGlobalNamingResources() {
        return m_standardServer.getGlobalNamingResources();
    }

    public void setGlobalNamingResources( NamingResources globalNamingResources ) {
        m_standardServer.setGlobalNamingResources(globalNamingResources);
    }

    public int getPort() {
        return m_standardServer.getPort();
    }

    public void setPort( int port ) {
        m_standardServer.setPort(port);
    }

    public String getShutdown() {
        return m_standardServer.getShutdown();
    }

    public void setShutdown( String shutdown ) {
        m_standardServer.setShutdown(shutdown);
    }

    public void addService( Service service ) {
        m_standardServer.addService(service);
    }

    public void await() {
        Thread t = new Thread(this);
        t.start();
    }

    public Service findService( String name ) {
        return m_standardServer.findService(name);
    }

    public Service[] findServices() {
        return m_standardServer.findServices();
    }

    public void removeService( Service service ) {
        m_standardServer.removeService(service);
    }

    public void initialize() throws LifecycleException {
        m_standardServer.initialize();
    }

    public void addLifecycleListener( LifecycleListener listener ) {
        m_standardServer.addLifecycleListener(listener);
    }

    public LifecycleListener[] findLifecycleListeners() {
        return m_standardServer.findLifecycleListeners();
    }

    public void removeLifecycleListener( LifecycleListener listener ) {
        m_standardServer.removeLifecycleListener(listener);
    }

    public void start() throws LifecycleException {
        m_serving = true;
        m_standardServer.start();
    }

    public void stop() throws LifecycleException {
        m_standardServer.stop();
        m_serving = false;
    }

    public void run() {
        while(m_serving) {
            try {
                Thread.sleep(3 * 1000);
            } catch( InterruptedException e ) {
                //  ignored
            }
        }
    }
}
