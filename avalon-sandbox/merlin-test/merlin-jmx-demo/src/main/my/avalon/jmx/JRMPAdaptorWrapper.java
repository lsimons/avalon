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
    private MBeanServer   jmxServer_;
    private JRMPAdaptor   wrapped_;
    private ObjectName    jmxName_;
    private NamingService naming_;
    private ObjectName    jmxNameNaming_;
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
        jmxServer_ = (MBeanServer) manager.lookup("jmx-server");
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
        jmxNameNaming_     = new ObjectName("JMXServer:name=naming,type=rmiregistry");
        naming_            = new NamingService(port_);
        jmxServer_.registerMBean(naming_, jmxNameNaming_);
        naming_.start();

        // Create the JRMP adaptor
        ObjectName jmxName_ = new ObjectName("JMXServer:name=adaptor,protocol=JRMP");
        wrapped_ = new JRMPAdaptor();
        wrapped_.setJNDIName("jrmp");
        wrapped_.setPort(port_);

        // Optionally, you can specify the JNDI properties,
        // instead of having in the classpath a jndi.properties file
        wrapped_.putJNDIProperty(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
        wrapped_.putJNDIProperty(Context.PROVIDER_URL, "rmi://localhost:" + port_);

        jmxServer_.registerMBean(wrapped_, jmxName_);
        wrapped_.start();
    }

    public void stop() throws Exception {
        wrapped_.stop();
        jmxServer_.unregisterMBean(jmxName_);
        naming_.stop();
        jmxServer_.unregisterMBean(jmxNameNaming_);
    }
}
