package my.avalon.jmx;

import mx4j.adaptor.rmi.jrmp.JRMPAdaptor;

import mx4j.tools.naming.NamingService;

import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import javax.naming.Context;


/**
 * Wrapper of the mx4j RMIAdaptor to use it in the Avalon Framework.
 *
 * @avalon.component version="1.0" name="jmx-rmi" lifestyle="singleton"
 */
public class JRMPAdaptorWrapper extends AbstractLogEnabled implements Serviceable, Configurable, Startable 
{
    private MBeanServer   m_server;
    private JRMPAdaptor   m_adapter;
    private ObjectName    m_name;
    private NamingService m_naming;
    private ObjectName    m_namingName;
    private int           port_;

    /**
     * Servicing of the component by the container during
     * which service dependencies declared under the component
     * can be resolved using the supplied service manager.
     *
     * @param manager the service manager
     * @avalon.dependency key="jmx-server" type="javax.management.MBeanServer"
     */
    public void service(ServiceManager manager) throws ServiceException {
        m_server = (MBeanServer) manager.lookup("jmx-server");
    }

    /**
     * Configuration of the component by the container.  The
     * implementation get a child element named 'source' and
     * assigns the value of the element to a local variable.
     *
     * @param config the component configuration
     * @exception ConfigurationException if a configuration error occurs
     */
    public void configure(Configuration config) throws ConfigurationException {
        port_ = config.getChild("port").getValueAsInteger(1099);
    }

    public void start() throws Exception {
        // Create and start the naming service
        m_namingName     = new ObjectName("JMXServer:name=naming,type=rmiregistry");
        m_naming            = new NamingService(port_);
        m_server.registerMBean(m_naming, m_namingName);
        m_naming.start();

        // Create the JRMP adaptor
        m_name = new ObjectName("JMXServer:name=adaptor,protocol=JRMP");
        m_adapter = new JRMPAdaptor();
        m_adapter.setJNDIName("jrmp");
        m_adapter.setPort(port_);

        // Optionally, you can specify the JNDI properties,
        // instead of having in the classpath a jndi.properties file
        m_adapter.putJNDIProperty(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
        m_adapter.putJNDIProperty(Context.PROVIDER_URL, "rmi://localhost:" + port_);

        m_server.registerMBean(m_adapter, m_name);
        m_adapter.start();
    }

    public void stop() throws Exception 
    {
        try
        {
            m_adapter.stop();
        }
        catch( Throwable e )
        {
            getLogger().warn( "ignoring error while attempting to stop adapter", e );
        }

        try
        {
            m_server.unregisterMBean( m_name );
        }
        catch( Throwable e )
        {
            getLogger().warn( "ignoring error while unregister jrmp management point: " + m_name, e );
        }

        try
        {
            m_naming.stop();
        }
        catch( Throwable e )
        {
            getLogger().warn( "ignoring error while attempting to stop naming service", e );
        }

        try
        {
            m_server.unregisterMBean( m_namingName );
        }
        catch( Throwable e )
        {
            getLogger().warn( "ignoring error while unregister rmi name seerver management point" , e );
        }

    }
}
