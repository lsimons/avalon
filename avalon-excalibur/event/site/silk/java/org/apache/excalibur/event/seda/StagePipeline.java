/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.seda;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.excalibur.event.EventHandler;
import org.apache.excalibur.event.Queue;
import org.apache.excalibur.event.Source;
import org.apache.excalibur.event.command.EventPipeline;

/**
 * Stage Event Pipeline implementation that is run by the 
 * associated Thread Manager. The pipeline can be created using 
 * either a single handler description in the form of a 
 * {@link org.apache.excalibur.event.seda.impl.StageHandler} 
 * object or a mapping of event type classes to handler objects.
 * Alternatively the event handler exposed by the pipeline can
 * be passed in as well, which allows to have the EventHandler 
 * interface be supported natively.
 * 
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class StagePipeline extends AbstractLogEnabled
    implements EventPipeline, Initializable, Disposable
{
    /** Queues from which the events are pulled and executed in the stage. */
    private final Source[] m_sources;

    /** The pipeline's event handler */
    private final EventHandler m_eventHandler;

    //----------------------- StagePipelineWrapper constructors
    /**
     * Constructor that takes the stage and the handler method
     * to run and the event queues from which to pull the events.
     * @since May 15, 2002
     * 
     * @param handler
     *  The StageHandler describing a handler from the stage 
     *  component
     * @param sources
     *  The sources from which to pull the events.
     */
    public StagePipeline(StageHandler handler, Queue[] sources)
        throws IllegalArgumentException
    {
        super();
        m_sources = sources;

        if(handler.isTypeArray())
        {
            m_eventHandler = new ArrayEventHandler(handler);
        }
        else
        {
            m_eventHandler = new DefaultEventHandler(handler);
        }
    }
    
    /**
     * Constructor that takes the handler to run and the 
     * event queues from which to pull the events.
     * @since May 15, 2002
     * 
     * @param handlers
     *  A map containing type class - StageHandler pairs. This 
     *  allows an event handler to look up the correct handler
     *  to invoke from the map.
     * @param sources
     *  The sources from which to pull the events.
     */
    public StagePipeline(Map handlers, Queue[] sources)
    {
        super();
        m_eventHandler = new MappedEventHandler(handlers);
        m_sources = sources;
    }

    /**
     * Constructor that takes the handler to run and the 
     * event queues from which to pull the events.
     * @since May 15, 2002
     * 
     * @param handler
     *  The event handler to run (Also represents the stage).
     * @param sources
     *  The sources from which to pull the events.
     */
    StagePipeline(EventHandler handler, Queue[] sources)
    {
        super();
        m_eventHandler = handler;
        m_sources = sources;
    }

    //----------------------- EventPipeline implementation
    /**
     * @see EventPipeline#getEventHandler()
     */
    public EventHandler getEventHandler()
    {
        return m_eventHandler;
    }

    /**
     * @see EventPipeline#getSources()
     */
    public Source[] getSources()
    {
        return m_sources;
    }
    
    //----------------------- Initializable implementation
    /**
     * @see Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        ContainerUtil.enableLogging(m_eventHandler, getLogger());
        ContainerUtil.initialize(m_eventHandler);
    }

    //----------------------- Disposable implementation
    /**
     * @see Disposable#dispose()
     */
    public void dispose()
    {
        ContainerUtil.dispose(m_eventHandler);
    }

    //----------------------- StagePipelineWrapper specific implementation
    /**
     * Returns the stage's first event queue
     * @since May 15, 2002
     * 
     * @return {@link Queue}
     *  the stage's event queue.
     */
    Queue getQueue()
    {
        return (Queue) m_sources[0];
    }

    //--------------------------- StagePipeline inner classes
    /**
     * The dynamic mapped event handler implementation takes
     * care of delivering the passed in event or events to 
     * a mapped method described by the passed in handler map.
     * This handler allows to attach a queue to more than one
     * handler method acting as a single stage. Of course this
     * implementation is not as performant as the single handler
     * single queue implementation.
     * @since Sep 12, 2002
     * 
     * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
     */
    private static final class MappedEventHandler extends AbstractLogEnabled 
        implements EventHandler, Disposable, Initializable
    {
        /** The method that acts as a handler on the stage */
        protected final Map m_handlers;
        
        protected final Set m_noneExistant = new HashSet();
        
        //------------------------ MappedEventHandler constructors
        /**
         * Creates a mapped event handler for the stage and
         * service passed in.
         * @since Sep 12, 2002
         * 
         * @param process
         *  The handler method to invoke on the stage
         * @param service
         *  The service object that contains the business methods
         */
        protected MappedEventHandler(Map handlers)
        {
            super();
            
            // the process method must be atomic and take an array
            m_handlers = new HashMap(handlers);
        }
        
        //----------------------- Disposable implementation
        /**
         * @see Disposable#dispose()
         */
        public void dispose()
        {
            final Iterator handlers = m_handlers.values().iterator();
            while(handlers.hasNext())
            {
                ContainerUtil.dispose(handlers.next());
            }
        }
        
        //----------------------- Initializable implementation
        /**
         * @see Initializable#initialize()
         */
        public void initialize() throws Exception
        {
            final Iterator handlers = m_handlers.values().iterator();
            while(handlers.hasNext())
            {
                final Object handler = handlers.next();
                ContainerUtil.enableLogging(handler, getLogger());
                ContainerUtil.initialize(handler);
            }
        }

        //---------------------------- EventHandler implementation
        /**
         * @see EventHandler#handleEvent(Object)
         */
        public void handleEvent(Object element)
        {
            handleEvents(new Object[]{ element });
        }

        /**
         * @see EventHandler#handleEvents(Object[])
         */
        public void handleEvents(Object[] elements)
        {
            if(elements.length == 0)
            {
                return;
            }
            
            // a list to pull events from in a selective fashion
            final List events = new LinkedList(Arrays.asList(elements));
            int length = 0;
            
            final long time = System.currentTimeMillis();
            
            while(!events.isEmpty())
            {
                final int size = events.size();
                final Class eventType = events.get(0).getClass();
                
                if(m_noneExistant.contains(eventType))
                {
                    events.remove(0);      
                    continue;
                }
                
                // a handler info based on the type of the incoming event
                final StageHandler info = 
                    (StageHandler)getHandler(eventType);
                    
                if( null == info )
                {
                    if(getLogger().isWarnEnabled())
                    {
                        final String e = eventType.getName();
                        getLogger().warn("Handler not found. Drop event " + e);
                    }
                    m_noneExistant.add(eventType);
                    events.remove(0);      
                    continue;
                }
                
                // we put the found info in the map and cache it
                if(!m_handlers.containsKey(eventType))
                {
                    m_handlers.put(eventType, info);
                }
                
                // the necessary event type for the handler
                final Class type = info.getEventType();
                if(info.isTypeArray())
                {
                    invoke(events, type, info);
                }
                else
                {
                    invoke(events, info);
                }
                
                // make sure we are not in an endless loop
                if(length == size)
                {
                    // log the remaining events.
                    // propagate to special async signal handler
                    System.out.println(
                        "Events remaining unprocessed: " + events);
                    break;
                }
                length = size;
            }
            
            if(getLogger().isDebugEnabled())
            {
                final long duration = System.currentTimeMillis() - time;
                getLogger().debug("Dispatch took " + duration + " ms.");
            }
        }
        
        //-------------------------- MappedEventHandler specific implementation
        /**
         * Delivers the first event from the list using the 
         * handler method and removes it from the list
         * @since Sep 13, 2002
         * 
         * @param events
         *  A list of events
         * @param handler
         *  The method handler to be used to deliver the first 
         *  event from the list
         */ 
        private void invoke(List events, StageHandler handler)
        {
            // we serve the handler one by one
            final Object event = events.remove(0);
            handler.handle(event);
        }
        
        /**
         * Delivers all the events of the specified type out of 
         * the list using the handler method. The delivered
         * events are removed from the list.
         * @since Sep 13, 2002
         * 
         * @param events
         *  A list of events
         * @param handler
         *  The method handler to be used to deliver the event
         * @param type
         *  The type of event that should be pulled from the list.
         */ 
        private void invoke(List events, Class type, StageHandler handler)
        {
            // we can pack more elements into the array
            final LinkedList elems = new LinkedList();
            final Iterator iter = events.iterator();
            while(iter.hasNext())
            {
                final Object event = iter.next();
                if(event == null)
                {
                    // we do not need null values so remove
                    iter.remove();
                }
                else if(type.isAssignableFrom(event.getClass()))
                {
                    elems.add(event);
                    // delivered so remove
                    iter.remove();
                }
            }
            
            final Object[] objects = 
                elems.toArray((Object[])Array.newInstance(type, 0));
            handler.handle(objects);
        }
        
        /**
         * Returns a handler for the passed in event type.
         * @since Sep 13, 2002
         * 
         * @param clazz
         *  The event class that acts as the key
         * @return Object
         *  The Stage Handler info object.
         */
        private Object getHandler(Class clazz)
        {
            // traverse through the interfaces and hierarchies
            Class type = clazz;
            Object object = null;
            
            while(object == null && type != null)
            {
                object = m_handlers.get(type);
                
                final Class[] interfaces = type.getInterfaces();
                for(int i = 0; i < interfaces.length && object == null; i++)
                {
                    object = getHandler(interfaces[i]);
                }
                
                type = type.getSuperclass();
            }
                
            return object;
        }
    }

    /**
     * The dynamic array event handler implementation takes
     * care of providing the passed in method with an array
     * of objects for handling.
     * @since Sep 12, 2002
     * 
     * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
     */
    private static final class ArrayEventHandler extends AbstractLogEnabled
        implements EventHandler, Disposable, Initializable
    {
        /** The method info that acts as a handler on the stage */
        protected final StageHandler m_handler;
        
        //---------------------------- ArrayEventHandler constructors
        /**
         * Creates an array event handler to handle events
         * to be invoked on the stage.
         * @since Sep 12, 2002
         * 
         * @param handler
         *  The handler method info for stage invocation
         */
        public ArrayEventHandler(StageHandler handler)
        {
            super();
            m_handler = handler;
        }

        //----------------------- Disposable implementation
        /**
         * @see Disposable#dispose()
         */
        public void dispose()
        {
            ContainerUtil.dispose(m_handler);
        }
        
        //----------------------- Initializable implementation
        /**
         * @see Initializable#initialize()
         */
        public void initialize() throws Exception
        {
            ContainerUtil.enableLogging(m_handler, getLogger());
            ContainerUtil.initialize(m_handler);
        }

        //---------------------------- EventHandler implementation
        /**
         * @see EventHandler#handleEvent(Object)
         */
        public void handleEvent(Object element)
        {
            // Todo: here we could speed it up by bypassing reflective 
            // array creation
            handleEvents(new Object[]{ element });
        }

        /**
         * @see EventHandler#handleEvents(Object[])
         */
        public void handleEvents(Object[] elements)
        {
            // copy all elements into a new array
            // Does JDK offer casting arrays by reflection ???
            final Class type = m_handler.getEventType();
            final int len = elements.length;
            final Object[] objects = (Object[])Array.newInstance(type, len);
            System.arraycopy(elements, 0, objects, 0, len);
            m_handler.handle(objects);
        }
    }

    /**
     * The dynamic single event handler implementation takes
     * care of funneling the events to the stage's handler 
     * method
     * @since Sep 12, 2002
     * 
     * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
     */
    private static final class DefaultEventHandler extends AbstractLogEnabled
        implements EventHandler, Disposable, Initializable
    {
        /** The handler method info for stage invocation */
        protected final StageHandler m_handler;
        
        //------------------------ DefaultEventHandler constructors
        /**
         * Creates a default event handler stage to handle events
         * on the stage.
         * @since Sep 12, 2002
         * 
         * @param handler
         *  The handler method info for stage invocation
         */
        protected DefaultEventHandler(StageHandler handler)
        {
            super();
            m_handler = handler;
        }

        //----------------------- Disposable implementation
        /**
         * @see Disposable#dispose()
         */
        public void dispose()
        {
            ContainerUtil.dispose(m_handler);
        }
        
        //----------------------- Initializable implementation
        /**
         * @see Initializable#initialize()
         */
        public void initialize() throws Exception
        {
            ContainerUtil.enableLogging(m_handler, getLogger());
            ContainerUtil.initialize(m_handler);
        }

        //---------------------------- EventHandler implementation
        /**
         * @see EventHandler#handleEvent(Object)
         */
        public void handleEvent(Object element)
        {
            m_handler.handle(element);
        }

        /**
         * @see EventHandler#handleEvents(Object[])
         */
        public void handleEvents(Object[] elements)
        {
            // funnel into simple handler method
            for (int i = 0; i < elements.length; i++)
            {
                m_handler.handle(elements[i]);
            }
        }
    }
}
