package my.avalon.jmx;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

import java.io.ObjectInputStream;

import java.util.Set;

import javax.management.*;


/**
 * JMXServerProxy is a proxy to a MBeanServer (adpater MBeanServer to Avalon).
 *
 * @avalon.component version="1.0" name="jmx-server" lifestyle="singleton"
 * @avalon.service type="javax.management.MBeanServer"
 */
public class JMXServerProxy extends AbstractLogEnabled implements Initializable, MBeanServer {
    private MBeanServer server_;

    /**
    * Initialization of the component by the container.
    * @exception Exception if an initialization error occurs
    */
    public void initialize() throws Exception {
        getLogger().info("initialization stage");
        server_ = MBeanServerFactory.createMBeanServer();
    }

    //-- MBeanServer impl (can't use Dynamic Proxy if I wan't to use it as a standard component)
    public void addNotificationListener(ObjectName observed, NotificationListener listener, NotificationFilter filter, Object handback)
                                 throws InstanceNotFoundException {
        server_.addNotificationListener(observed, listener, filter, handback);
    }

    public void addNotificationListener(ObjectName observed, ObjectName listener, NotificationFilter filter, Object handback)
                                 throws InstanceNotFoundException {
        server_.addNotificationListener(observed, listener, filter, handback);
    }

    public void removeNotificationListener(ObjectName observed, NotificationListener listener)
                                    throws InstanceNotFoundException, ListenerNotFoundException {
        server_.removeNotificationListener(observed, listener);
    }

    public void removeNotificationListener(ObjectName observed, ObjectName listener)
                                    throws InstanceNotFoundException, ListenerNotFoundException {
        server_.removeNotificationListener(observed, listener);
    }

    public Object instantiate(String className) throws ReflectionException, MBeanException {
        return server_.instantiate(className);
    }

    public Object instantiate(String className, ObjectName loaderName)
                       throws ReflectionException, MBeanException, InstanceNotFoundException {
        return server_.instantiate(className, loaderName);
    }

    public Object instantiate(String className, ObjectName loaderName, Object[] args, String[] parameters)
                       throws ReflectionException, MBeanException, InstanceNotFoundException {
        return server_.instantiate(className, loaderName, args, parameters);
    }

    public Object instantiate(String className, Object[] args, String[] parameters)
                       throws ReflectionException, MBeanException {
        return server_.instantiate(className, args, parameters);
    }

    public ObjectInstance createMBean(String className, ObjectName objectName)
                               throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException,
                                      NotCompliantMBeanException {
        return server_.createMBean(className, objectName);
    }

    public ObjectInstance createMBean(String className, ObjectName objectName, ObjectName loaderName)
                               throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException,
                                      NotCompliantMBeanException, InstanceNotFoundException {
        return server_.createMBean(className, objectName, loaderName);
    }

    public ObjectInstance createMBean(java.lang.String className, ObjectName objectName, Object[] args, String[] parameters)
                               throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException,
                                      NotCompliantMBeanException {
        return server_.createMBean(className, objectName, args, parameters);
    }

    public ObjectInstance createMBean(String className, ObjectName objectName, ObjectName loaderName, Object[] args, String[] parameters)
                               throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException,
                                      NotCompliantMBeanException, InstanceNotFoundException {
        return server_.createMBean(className, objectName, loaderName, args, parameters);
    }

    public ObjectInstance registerMBean(Object mbean, ObjectName objectName)
                                 throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
        return server_.registerMBean(mbean, objectName);
    }

    public void unregisterMBean(ObjectName objectName)
                         throws InstanceNotFoundException, MBeanRegistrationException {
        server_.unregisterMBean(objectName);
    }

    public ObjectInputStream deserialize(String className, ObjectName loaderName, byte[] bytes)
                                  throws InstanceNotFoundException, OperationsException, ReflectionException {
        return server_.deserialize(className, loaderName, bytes);
    }

    public ObjectInputStream deserialize(String className, byte[] bytes)
                                  throws OperationsException, ReflectionException {
        return server_.deserialize(className, bytes);
    }

    public ObjectInputStream deserialize(ObjectName objectName, byte[] bytes)
                                  throws InstanceNotFoundException, OperationsException {
        return server_.deserialize(objectName, bytes);
    }

    public Object getAttribute(ObjectName objectName, String attribute)
                        throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException {
        return server_.getAttribute(objectName, attribute);
    }

    public void setAttribute(ObjectName objectName, Attribute attribute)
                      throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        server_.setAttribute(objectName, attribute);
    }

    public AttributeList getAttributes(ObjectName objectName, String[] attributes)
                                throws InstanceNotFoundException, ReflectionException {
        return server_.getAttributes(objectName, attributes);
    }

    public AttributeList setAttributes(ObjectName objectName, AttributeList attributes)
                                throws InstanceNotFoundException, ReflectionException {
        return server_.setAttributes(objectName, attributes);
    }

    public Object invoke(ObjectName objectName, String methodName, Object[] args, String[] parameters)
                  throws InstanceNotFoundException, MBeanException, ReflectionException {
        return server_.invoke(objectName, methodName, args, parameters);
    }

    public String getDefaultDomain() {
        return server_.getDefaultDomain();
    }

    public Integer getMBeanCount() {
        return server_.getMBeanCount();
    }

    public boolean isRegistered(ObjectName objectName) {
        return server_.isRegistered(objectName);
    }

    public MBeanInfo getMBeanInfo(ObjectName objectName)
                           throws InstanceNotFoundException, IntrospectionException, ReflectionException {
        return server_.getMBeanInfo(objectName);
    }

    public ObjectInstance getObjectInstance(ObjectName objectName)
                                     throws InstanceNotFoundException {
        return server_.getObjectInstance(objectName);
    }

    public boolean isInstanceOf(ObjectName objectName, String className)
                         throws InstanceNotFoundException {
        return server_.isInstanceOf(objectName, className);
    }

    public Set queryMBeans(ObjectName patternName, QueryExp filter) {
        return server_.queryMBeans(patternName, filter);
    }

    public Set queryNames(ObjectName patternName, QueryExp filter) {
        return server_.queryNames(patternName, filter);
    }
}
