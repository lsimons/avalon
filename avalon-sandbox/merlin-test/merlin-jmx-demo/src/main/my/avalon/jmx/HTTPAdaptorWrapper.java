package my.avalon.jmx;

import mx4j.adaptor.http.HttpAdaptor;
import mx4j.adaptor.http.XSLTProcessor;

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


/**
 * Wrapper of the mx4j HttpAdaptor to use it in the Avalon Framework.
 *
 * @avalon.meta.version 1.0
 * @avalon.meta.name jmx-http
 * @avalon.meta.lifestyle singleton
 */
public class HTTPAdaptorWrapper extends AbstractLogEnabled implements Serviceable, Configurable, Startable {
    private HttpAdaptor wrapped_;
    private MBeanServer jmxServer_;
    private ObjectName  jmxName_;

    public HTTPAdaptorWrapper() {
        wrapped_ = new HttpAdaptor();
    }

    /**
     * Servicing of the component by the container during
     * which service dependencies declared under the component
     * can be resolved using the supplied service manager.
     *
     * @param manager the service manager
     * @avalon.meta.dependency key="jmx-server" type="javax.management.MBeanServer"
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
        try {
            wrapped_.setHost(config.getChild("host").getValue(null));
            wrapped_.setPort(config.getChild("port").getValueAsInteger(8082));
            jmxName_ = new ObjectName(config.getAttribute("jmxName", "JMXServer:name=adaptor,protocol=HTTP"));

            if (config.getChild("xslt-processor").getAttributeAsBoolean("enable", false)) {
                XSLTProcessor xsltProcessor = new XSLTProcessor();
                wrapped_.setProcessor(xsltProcessor);
            }
        } catch (Exception exc) {
            throw new ConfigurationException("", exc);
        }
    }

    public void start() throws Exception {
        jmxServer_.registerMBean(wrapped_, jmxName_);
        wrapped_.start();
    }

    public void stop() throws Exception {
        wrapped_.stop();
        jmxServer_.unregisterMBean(jmxName_);
    }
}
