/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.seda;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.excalibur.event.Sink;


/**
 * Object that wraps a service instance and the associated
 * ServiceSelector for disposal of the service after usage.
 * This object is also used to attach a sink map. Upon
 * initialization the sink map is set into the service if
 * it supports the <code>Stage</code> interface. This class
 * therefore acts as an accessor for the Stage lifecycle.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
final class StageService extends AbstractLogEnabled
    implements InvocationHandler, Initializable, Disposable
{
    /** The service reference wrapped by this object */
    private final Object m_component;
    
    /** The service reference dynamic proxy */
    private Object m_componentProxy = null;

    /** The service selector that manages the stages. */
    private final ServiceSelector m_releaser;
    
    /** The service's sink map */
    private final SinkMap m_map;
    
    /** A map that keeps method / sink references */
    private final Map m_sinks = new HashMap(); 
    
    
    //--------------------------- StageService constructors
    /**
     * Creates a Service component wrapper based on the passed
     * in object and the associated selector.
     * @since Sep 13, 2002
     * 
     * @param component
     *  The object in which to look for the named method
     * @param releaser
     *  The component selector or manager that can release the 
     *  service later.
     * @param map
     *  The sink map that is set as soon as the wrapper is 
     *  initialized
     */
    public StageService(Object component, ServiceSelector releaser, SinkMap map)
    {
        super();
        
        m_component = component;
        m_releaser = releaser;
        m_map = map;
    }


    //--------------------------- overriden methods in Object
    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object obj)
    {
        if(obj != null && obj instanceof StageService)
        {
            final StageService service = (StageService)obj;
            return m_component.equals(service.m_component);
        }
        return m_component.equals(obj);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return m_component.hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return m_component.toString();
    }

    //--------------------------- Initializable implementation
    /**
     * @see Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        ContainerUtil.enableLogging(m_map, getLogger());
        ContainerUtil.initialize(m_map);
        if(isStage())
        {
            final Stage stage = (Stage)m_component;
            stage.setSinkMap(m_map);
        }
    }

    //--------------------------- Disposable implementation
    /**
     * @see Disposable#dispose()
     */
    public void dispose()
    {
        ContainerUtil.dispose(m_map);
        m_releaser.release(m_component);
    }
    
    //---------------------------- InvocationHandler implementation
    /**
     * @see InvocationHandler#invoke(Object, Method, Object[])
     */
    public Object invoke(Object proxy, Method method, Object[] args)
        throws Throwable
    {
        final Sink[] sinks = getSink(method);
        if(sinks == null || sinks.length == 0)
        {
            return method.invoke(getComponent(), args);
        }
        
        final Object event = args[0];
        if(event.getClass().isArray())
        {
            sinks[0].enqueue((Object[])event);
        }
        else
        {
            sinks[0].enqueue(event);
        }
        return null;
    }

    //--------------------------- StageService specific implementation
    /**
     * Returns a dynamic proxy for the service component
     * @since Sep 16, 2002
     * 
     * @return Object
     *  The dynamic proxy for the service component
     */
    final Object getComponentProxy()
    {
        if(m_componentProxy == null)
        {
            // The class loader used to create the proxy
            final ClassLoader classLoader = 
                Thread.currentThread().getContextClassLoader();
            // The interfaces the proxy will implement
            final Class[] interfaces = m_component.getClass().getInterfaces();
            
            m_componentProxy = 
                Proxy.newProxyInstance(classLoader, interfaces, this);
        }
        return m_componentProxy;
    }
    
    /**
     * Returns the service reference wrapped by this object.
     * @since Sep 16, 2002
     * 
     * @return Object
     *  The service reference wrapped by this object
     */
    final Object getComponent()
    {
        return m_component;
    }
    
    /**
     * Allows to set the sink / queue for the handler 
     * identified by the passed in method object.
     * @since Oct 1, 2002
     * 
     * @param method
     *  The handler method associated with the stage sink
     * @param queues
     *  The sinks for the stage the method takes part in.
     */
    final void setSink(Method method, Sink[] sinks)
    {
        m_sinks.put(method, sinks);
    }
    
    /**
     * Returns the sink for the handler identified by the 
     * passed in method object.
     * @since Oct 1, 2002
     * 
     * @param method
     *  The handler method associated with the stage sink
     * @return Sink[]
     *  The sinks for the stage the method takes part in.
     */
    final Sink[] getSink(Method method)
    {
        return (Sink[])m_sinks.get(method);
    }
    
    /** 
     * Returns whether the service implements the lifecycle
     * interface {@link org.apache.excalibur.event.seda.Stage}
     * @since Sep 16, 2002
     * 
     * @return boolean
     *  whether the service implements the lifecycle
     *  interface <m_code>Stage</m_code>
     */
    final boolean isStage()
    {
        return m_component instanceof Stage;
    }
    
    /**
     * Returns the sink map for this stage participating 
     * component.
     * @since Sep 16, 2002
     * 
     * @return SinkMap
     *  the sink map for this stage participating component.
     */
    final SinkMap getSinkMap()
    {
        return m_map;
    }
}
