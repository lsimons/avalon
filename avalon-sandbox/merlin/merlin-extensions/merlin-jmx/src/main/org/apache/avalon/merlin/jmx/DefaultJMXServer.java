/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.avalon.merlin.jmx;

import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.ObjectInstance;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import javax.management.NotificationListener;
import javax.management.NotificationFilter;
import javax.management.ListenerNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.IntrospectionException;
import javax.management.QueryExp;
import javax.management.MBeanInfo;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InvalidAttributeValueException;
import javax.management.AttributeNotFoundException;
import javax.management.OperationsException;
import javax.management.MBeanRegistrationException;
import javax.management.NotCompliantMBeanException;

import mx4j.util.StandardMBeanProxy;
import mx4j.adaptor.rmi.jrmp.JRMPAdaptorMBean;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 * A JMX server with some hard-coded facilites including a JRMP adapter.
 * This needs to be expanded into a componet under which the manageable
 * resources are defined by a configuration.  Secondly, a lot of 
 * method documentation is needed.
 *
 * @avalon.meta.version 1.0
 * @avalon.meta.name jmx
 * @avalon.meta.lifestyle singleton
 * @avalon.meta.service type="javax.management.MBeanServer:1.0"
 */
public class DefaultJMXServer extends AbstractLogEnabled implements Initializable, MBeanServer 
{
    private MBeanServer m_server;

    public void initialize() throws Exception
    {
        getLogger().info( "starting mbean server" );
        // Create and start the naming service 
        MBeanServer server = MBeanServerFactory.createMBeanServer( "localhost" );
        ObjectName naming = new ObjectName("Naming:type=rmiregistry"); 
        server.createMBean("mx4j.tools.naming.NamingService", naming, null); 
        server.invoke(naming, "start", null, null); 

        // Create the JRMP adaptor 
        getLogger().info( "adding JRMP adapter" );
        ObjectName adaptor = new ObjectName("Adaptor:protocol=JRMP"); 
        server.createMBean("mx4j.adaptor.rmi.jrmp.JRMPAdaptor", adaptor, null);
        JRMPAdaptorMBean mbean = (JRMPAdaptorMBean)StandardMBeanProxy.create(
          JRMPAdaptorMBean.class, server, adaptor); 

        // Set the JNDI name with which will be registered 
        getLogger().info( "declaring JRMP adapter to JNDI" );
        String jndiName = "jrmp";
        mbean.setJNDIName(jndiName);
        mbean.start();

        m_server = server;
    }

    public MBeanServer getServer()
    {
        return m_server;
    }

    public void addNotificationListener( 
      ObjectName observed, NotificationListener listener, 
      NotificationFilter filter, Object handback )
      throws InstanceNotFoundException
    {
        m_server.addNotificationListener(observed, listener, filter,handback);
    }

    public void addNotificationListener(
      ObjectName observed, ObjectName listener, NotificationFilter filter, 
      Object handback)
      throws InstanceNotFoundException
    {
        m_server.addNotificationListener(observed, listener, filter,handback);
    }

    public void removeNotificationListener(
      ObjectName observed, NotificationListener listener )
      throws InstanceNotFoundException, ListenerNotFoundException
    {
        m_server.removeNotificationListener(observed, listener);
    }

    public void removeNotificationListener(ObjectName observed, ObjectName listener)
      throws InstanceNotFoundException, ListenerNotFoundException
    {
        m_server.removeNotificationListener(observed, listener);
    }

    public Object instantiate(String className)
      throws ReflectionException, MBeanException
    {
        return m_server.instantiate( className );
    }

    public Object instantiate(String className, ObjectName loaderName)
      throws ReflectionException, MBeanException, InstanceNotFoundException
    {
        return m_server.instantiate(className, loaderName);
    }
                                        
    public Object instantiate( 
      String className, ObjectName loaderName, Object[] args, String[] parameters)
      throws ReflectionException, MBeanException, InstanceNotFoundException
    {
        return m_server.instantiate(className, loaderName, args, parameters);
    }
                                        
    public Object instantiate(String className, Object[] args, String[] parameters)
      throws ReflectionException, MBeanException
    {
        return m_server.instantiate(className, args, parameters);
    }
                                 
    public ObjectInstance createMBean(String className, ObjectName objectName)
      throws ReflectionException, InstanceAlreadyExistsException,       
      MBeanRegistrationException, MBeanException, NotCompliantMBeanException
    {
        return m_server.createMBean(className, objectName);
    }

    public ObjectInstance createMBean( 
      String className, ObjectName objectName, ObjectName loaderName )
      throws ReflectionException, InstanceAlreadyExistsException, 
      MBeanRegistrationException, MBeanException, 
      NotCompliantMBeanException, InstanceNotFoundException
    {
        return m_server.createMBean(className, objectName, loaderName);
    }

    public ObjectInstance createMBean( 
      String className, ObjectName objectName, Object[] args, String[] parameters)
      throws ReflectionException, InstanceAlreadyExistsException, 
      MBeanRegistrationException, MBeanException, NotCompliantMBeanException
    {
        return m_server.createMBean(className, objectName, args, parameters);
    }

    public ObjectInstance createMBean( 
      String className, ObjectName objectName, ObjectName loaderName, 
      Object[] args, String[] parameters)
      throws ReflectionException, InstanceAlreadyExistsException, 
      MBeanRegistrationException, MBeanException, NotCompliantMBeanException, 
      InstanceNotFoundException
    {
        return m_server.createMBean(className, objectName, loaderName, args, parameters);
    }

    public ObjectInstance registerMBean( Object mbean, ObjectName objectName )
      throws InstanceAlreadyExistsException, MBeanRegistrationException, 
      NotCompliantMBeanException
    {
        return m_server.registerMBean(mbean, objectName);
    }
                                        
    public void unregisterMBean(ObjectName objectName)
            throws InstanceNotFoundException, MBeanRegistrationException
    {
        m_server.unregisterMBean(objectName);
    }

    public ObjectInputStream deserialize(
      String className, ObjectName loaderName, byte[] bytes)
      throws InstanceNotFoundException, OperationsException, ReflectionException
    {
        return m_server.deserialize(className, loaderName, bytes);
    }

    public ObjectInputStream deserialize(String className, byte[] bytes )
      throws OperationsException, ReflectionException
    {
        return m_server.deserialize(className, bytes);
    }
                                          
    public ObjectInputStream deserialize(ObjectName objectName, byte[] bytes )
      throws InstanceNotFoundException, OperationsException
    {
        return m_server.deserialize(objectName, bytes);
    }
                                          
    public Object getAttribute(ObjectName objectName, String attribute )
      throws MBeanException, AttributeNotFoundException, 
      InstanceNotFoundException, ReflectionException
    {
        return m_server.getAttribute(objectName, attribute);
    }

    public void setAttribute(ObjectName objectName, Attribute attribute )
      throws InstanceNotFoundException, AttributeNotFoundException, 
      InvalidAttributeValueException, MBeanException, ReflectionException
    {
        m_server.setAttribute(objectName, attribute);
    }

    public AttributeList getAttributes(ObjectName objectName, String[] attributes)
      throws InstanceNotFoundException, ReflectionException
    {
        return m_server.getAttributes(objectName, attributes);
    }

    public AttributeList setAttributes(ObjectName objectName, AttributeList attributes )
      throws InstanceNotFoundException, ReflectionException
    {
        return m_server.setAttributes(objectName, attributes);
    }

    public Object invoke( 
      ObjectName objectName, String methodName, Object[] args, String[] parameters )
      throws InstanceNotFoundException, MBeanException, ReflectionException
    {
        return m_server.invoke(objectName, methodName, args, parameters);
    }

    public String getDefaultDomain()
    {
        return m_server.getDefaultDomain();
    }

    public Integer getMBeanCount()
    {
        return m_server.getMBeanCount();
    }

    public boolean isRegistered( ObjectName objectname )
    {
        return m_server.isRegistered( objectname );
    }

    public MBeanInfo getMBeanInfo( ObjectName objectName )
      throws InstanceNotFoundException, IntrospectionException, 
      ReflectionException
    {
        return m_server.getMBeanInfo(objectName);
    }

    public ObjectInstance getObjectInstance( ObjectName objectName )
      throws InstanceNotFoundException
    {
        return m_server.getObjectInstance(objectName);
    }

    public boolean isInstanceOf( ObjectName objectName, String className )
      throws InstanceNotFoundException
    {
        return m_server.isInstanceOf(objectName, className);
    }

    public Set queryMBeans( ObjectName patternName, QueryExp filter )
    {
        return m_server.queryMBeans(patternName, filter);
    }

    public Set queryNames( ObjectName patternName, QueryExp filter)
    {
        return m_server.queryNames(patternName, filter);
    }
}
