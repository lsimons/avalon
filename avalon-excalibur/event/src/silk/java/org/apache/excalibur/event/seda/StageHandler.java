/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.seda;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.excalibur.event.Sink;

/**
 * A stage handler method info describes a process method that
 * takes only one argument and acts as a stage handler in the
 * event driven architecture.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
final class StageHandler extends AbstractLogEnabledStage
    implements Disposable, Initializable
{
    /** Constant names for the exception and return value sinks */
    static final String EXCEPTION_SINK = "@___exceptions___$";
    static final String RETURN_SINK = "@___return___$";
    
    /** The method object representing the handler */
    private Method m_method;

    /** The event type that the handler can handle */
    private Class m_eventType;

    /** Whether the event type is an array or not */
    private boolean m_typeArray;

    /** The Stage service that owns the handler */
    private final StageService m_stageService;
    
    /** A sink where return values are enqueued to */
    private Sink m_returnSink = null;
    
    /** A sink where exceptions are enqueued to */
    private Sink m_exceptionSink = null;
    
    /** Wether the handler needs an event at all */
    private boolean m_noEvents;
    
    //--------------------------- StageHandler constructors
    /**
     * Creates a stage handler method based on the passed
     * in object or interface class, method name and parameter
     * name.
     * @since Sep 13, 2002
     * 
     * @param stageService
     *  The service in which to look for the named method
     * @param name
     *  The name of the handler method
     * @param param
     *  The parameter type as a string
     * @throws NoSuchMethodException
     *  If the method cannot be found
     */
    StageHandler(StageService stageService, String name, String param) 
        throws NoSuchMethodException
    {
        super();
        m_stageService = stageService;
        enableLogging(new ConsoleLogger());
        initialize(getMethod(stageService.getComponent(), name, param));
    }

    /**
     * Creates a stage handler method based on the passed
     * in method object representing the handler
     * @since Sep 13, 2002
     * 
     * @param stageService
     *  The service in which to look for the named method
     * @param releaser
     *  The component selector or manager that can release the stages.
     */
    StageHandler(StageService stageService, Method method)
    {
        super();
        m_stageService = stageService;
        enableLogging(new ConsoleLogger());
        initialize(method);
    }

    //--------------------------- Initializable implementation
    /**
     * @see Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        final SinkMap map = getSinkMap();
        if(map != null)
        {
            // first initialize the map so it's filled from the stagemap
            ContainerUtil.enableLogging(map, getLogger());
            ContainerUtil.initialize(map);
            
            // get the necessary sinks
            m_exceptionSink = map.getSink(EXCEPTION_SINK);
            m_returnSink = map.getSink(RETURN_SINK);
        }
    }

    //--------------------------- Disposable implementation
    /**
     * @see Disposable#dispose()
     */
    public void dispose()
    {
        m_stageService.dispose();
    }

    //--------------------------- StageHandler specific implementation
    /**
     * Returns the type of the event the handler method 
     * can handle. This can be used to class object arrays
     * to the right type of array for handling.
     * @since Sep 13, 2002
     * 
     * @return Class
     *  The type of event the handler method can handle.
     */
    Class getEventType()
    {
        return m_eventType;
    }
    
    /**
     * Returns the method that represents the handler.
     * @since Oct 1, 2002
     * 
     * @return Method
     *  The method that represents the handler.
     */
    Method getHandler()
    {
        return m_method;
    }


    /**
     * Returns whether the handler method expects an array
     * of the event type as an argument.
     * @since Sep 13, 2002
     * 
     * @return boolean
     *  Whether the handler method expects an array of the 
     *  event type as an argument.
     */
    boolean isTypeArray()
    {
        return m_typeArray;
    }

    /**
     * Handles the passed in element by invoking the handler 
     * method on it. Can only be invoked if {@link #isTypeArray()} 
     * returns the same boolean value as the invoked method 
     * {@link Class#isArray()} for the specified element object, 
     * otherwise a {@link IllegalArgumentExcption} is routed
     * to the registered exception handler or the default one if
     * none is registered.
     * @since Sep 13, 2002
     * 
     * @param element
     *  The element to be handled.
     */
    void handle(Object element)
    {
        try
        {
            // test for the type of object
            if (isTypeArray() != element.getClass().isArray())
            {
                throw new IllegalArgumentException(
                    "An element array is necessary");
            }
        
            try
            {
                final Object component = m_stageService.getComponent();
                final Object returned;
                
                if(m_noEvents)
                {
                    returned = m_method.invoke(component, new Object[0]);
                }
                else
                {
                    final Object[] params = new Object[] { element };
                    returned = m_method.invoke(component, params);
                }
                
                if(m_returnSink != null)
                {
                    m_returnSink.enqueue(returned);
                }
            }
            catch (InvocationTargetException e)
            {
                final Throwable ex = e.getTargetException();
                // throw the exception ex and propagate it to the 
                // ouside catch block if not caused by incompatible 
                // message types
                throwException(element, ex);
            }
        }
        catch(Throwable e)
        {
            if(m_exceptionSink == null)
            {
                if(getLogger().isErrorEnabled())
                {
                    getLogger().error("Error invoking the specified target.", e);
                }
            
                // TODO: send to default signal handler
                return;
            }
            // try to enqueue the exception into the specified sink
            m_exceptionSink.tryEnqueue(e);
        }
    }

    /**
     * Checks whether the target exception represents a
     * ClassCastException and then checks whether the exception
     * occurred inside the method of was caused by a bad element
     * type. This lazy test here shields us from testing every 
     * time before invoking a method and therefore increases 
     * the performance of the method invocation.
     * @since Sep 17, 2002
     * 
     * @param element
     *  The element whose type is used for compliance testing
     * @param ex
     *  The exception to be tested
     * @throws Throwable
     *  The exception to be tested if not class cast exception 
     *  due to incompatible event type.
     */
    private void throwException(Object element, Throwable ex) 
        throws Throwable
    {
        if(ClassCastException.class.equals(ex.getClass()))
        {
            // test if the exception is from passing in 
            // the wrong type or happened inside the handler 
            // method somewhere. This lazy test here might 
            // shields us from testing every time before
            // we invoke a method and therefore increases 
            // the performance of the method invocation.
            Class elementType = element.getClass();
            if(elementType.isArray())
            {
                elementType = elementType.getComponentType();
            }
            
            if(!m_eventType.isAssignableFrom(elementType))
            {
                // we cannot propagate this type of ClassCastException
                // since this would result in a circularity problem.
                if(getLogger().isErrorEnabled())
                {
                    getLogger().error(
                        "Handler does not accept element "
                        + elementType.getName() + " but will need " 
                        + m_eventType.getName(), ex);
                }
                // TODO: send to default signal handler
                return;
            }
        }
        throw ex;
    }
    
    /**
     * Initializes the handler with the passed in method.
     * This method is called in the constructors.
     * @since Sep 13, 2002
     * 
     * @param method
     *  The method the handler is based on.
     */
    private void initialize(Method method)
    {
        m_method = method;
        
        final Class[] types = method.getParameterTypes();
        // only atomic methods are allowed
        if (types.length == 1)
        {
            m_noEvents = false;
            final Class type = types[0];
            // in case of an array we are only interested in 
            // the component type
            if (type.isArray())
            {
                m_typeArray = true;
                m_eventType = type.getComponentType();
            }
            else
            {
                m_typeArray = false;
                m_eventType = type;
            }
        }
        else if(types.length == 0)
        {
            m_noEvents = true;
            m_typeArray = false;
            m_eventType = Object.class;
        }
        else
        {
            throw new IllegalArgumentException("Method must be atomic.");
        }
    }
    
    /**
     * Returns a method object from the passed in information.
     * @since Sep 13, 2002
     * 
     * @param clazz
     *  The type of the interface or object in which to look 
     *  for the named method
     * @param signature
     *  The name of the method
     * @param type
     *  The parameter type class
     * @return Method
     *  The found method
     * @throws NoSuchMethodException
     *  If the method cannot be found
     */
    private Method getMethod(Class clazz, String signature, Class type)
        throws NoSuchMethodException
    {
        try
        {
            return clazz.getMethod(signature, new Class[] { type });
        }
        catch (NoSuchMethodException e)
        {
            // try array version of type
            try
            {
                final Class arrayType = Array.newInstance(type, 0).getClass();
                return clazz.getMethod(signature, new Class[] { arrayType });
            }
            catch (NoSuchMethodException e2)
            {
                // try interfaces of event type and super interfaces
                final Class[] interfaces = type.getInterfaces();
                for(int i = 0; i < interfaces.length; i++)
                {
                    try
                    {
                        return getMethod(interfaces[i], signature, type);
                    }
                    catch(NoSuchMethodException e3)
                    {
                        // ignore
                    }
                }
                
                // try super classes of the event type
                type = type.getSuperclass();
                if(null != type)
                {
                    return getMethod(clazz, signature, type);
                }
                
                // give up and throw initial exception
                throw e;
            }
        }
    }

    /**
     * Returns a method object from the passed in information.
     * @since Sep 13, 2002
     * 
     * @param obj
     *  The object in which to look for the named method
     * @param signature
     *  The name of the handler method
     * @param type
     *  The parameter type as a string
     * @return Method
     *  The found method
     * @throws NoSuchMethodException
     *  If the method cannot be found
     */
    private Method getMethod(Object obj, String signature, String type) 
        throws NoSuchMethodException
    {
        final Class clazz = obj.getClass();
        final ClassLoader ldr = Thread.currentThread().getContextClassLoader();
        
        if(type != null)
        {
            try
            {
                final Class element = Class.forName(type, false, ldr);
                return getMethod(clazz, signature, element);
            }
            catch(Exception e)
            {
                if(getLogger().isErrorEnabled())
                {
                    getLogger().error(
                        "Specified information invalid. " +
                        "Using no-arg version of specified method", e);
                }
                // ignore
            }
        }
        
        // return no argument version. 
        return clazz.getMethod(signature, new Class[0]);
    }
    
}
