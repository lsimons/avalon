/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.seda;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.avalon.framework.CascadingException;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.event.Queue;
import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.SinkException;
import org.apache.excalibur.event.command.TPCThreadManager;
import org.apache.excalibur.event.command.TPSPThreadManager;
import org.apache.excalibur.event.command.ThreadManager;
import org.apache.excalibur.event.ext.DefaultQueue;
import org.apache.excalibur.event.ext.EnqueuePredicate;
import org.apache.excalibur.event.ext.FixedSizeQueue;
import org.apache.excalibur.event.ext.RateLimitingPredicate;
import org.apache.excalibur.event.ext.ThresholdPredicate;
import org.apache.excalibur.util.SystemUtil;

/**
 * <p>The default stage manager is a default implementation of the 
 * stage manager interface. The idea behind it is to have central
 * manager component that is seperate from the container the stages
 * are deployed into but can assemble and manage the stages within
 * its scope. The goal is to allow re-assembly of the staged process
 * by changing the configuration and without the stages knowledge.</p>
 * 
 * <p>A stage only deals with its sink and sources that it is provided 
 * by the stage manager. If it must select between sinks to fork 
 * a process the stage must be configured to take the name of the 
 * sinks. These names are configured in the stage manager, too. This 
 * allows the stage manager to direct the stage execution flow and the 
 * stages to focus on the event business logic. It is important that
 * the implementation of the stages lifecycle does not depend on the 
 * sink map passed in. The only guarantee about the availability of a 
 * sinkmap is after the stage's startup phases were executed by the 
 * container!</p>
 * 
 * <p>The stage manager implementation is configured using the following
 * configuration:</p>
 * 
 * <p><m_code>
 * <pre>
 * &lt;stage-manager &gt;
 *   &lt;stages thread-manager="TPC|TPS" processors="1" threads="1"&gt;
 *     &lt;stage name="foo-stage" &gt;
 *       <i>&lt;!-- service and handler identification. 'id' and 'role' 
 *            reference the participating components in the container. --&gt;</i>
 *       &lt;handlers&gt;
 *          &lt;service role="org.apache.excalibur.FooService" 
 *                   id="optional" &gt;
 *             <i>&lt;!-- The handler tag describes the handler methods. 
 *                  Either specific public methods of a service can become 
 *                  handlers or a whole interface is declared as 
 *                  handler. --&gt;</i>
 *             &lt;handler signature="process" 
 *                     type="org.apache.excalibur.Event" 
 *                     exception-sink="bar-stage" 
 *                     return-sink="foobar-stage" &gt;
 *             &lt;/handler&gt;
 *             <i>&lt;!-- or --&gt;</i>
 *             &lt;handler interface="org.apache.excalibur.FooService"
 *                     exception-sink="bar-stage" 
 *                     return-sink="foobar-stage" &gt;
 *             &lt;/handler&gt;
 *          &lt;/service &gt;
 *       &lt;/handlers&gt;
 * 
 *       <i>&lt;!-- Optionally a queue type and predicate for this process
 *            can be defined. The default is VARIABLE without predicate --&gt;</i>
 *       &lt;queue type="FIXED|VARIABLE" size="1" timeout="1000"&gt;
 *         &lt;!-- predicate type="RATE-LIMITING|THRESHOLD|NONE"/ --&gt;
 *       &lt;/queue&gt;
 * 
 *       &lt;sink-map&gt; 
 *         <i>&lt;!-- The sinks to enqueue events to, only visible from
 *              this stage --&gt;</i>
 *         &lt;sink stage-name="abc-stage"/&gt;
 *         &lt;sink stage-name="xyz-stage"/&gt;
 * 
 *         <i>&lt;!-- sink sets allow to group sinks into
 *              a single sink that can be used by a stage.
 *              delivery describes whether enqueue operation
 *              is performed for exactly one, all, one or more 
 *              or zero or more sinks. (default is ALL) --&gt;</i>
 *         &lt;sink-set stage-name="notification" 
 *                    delivery="ALL|ONE|ONE*|ZERO*"&gt;
 *           &lt;sink stage-name="bar-stage"/&gt;
 *           &lt;sink stage-name="foobaz-stage"/&gt;
 *         &lt;/sink-set&gt;
 * 
 *         <i>&lt;!-- Marking a sink as default makes it the main outgoing
 *              sink, otherwise the first sink in the list is the 
 *              default sink --&gt;</i>
 *         &lt;sink stage-name="do-stage" default="true"/&gt;
 * 
 *         <i>&lt;!-- if the stage needs access to its own sink it must be
 *              configured here --&gt;</i>
 *         &lt;sink stage-name="foo-stage"/&gt;
 *       &lt;/sink-map&gt;
 *     &lt;/stage&gt;
 *     &lt;stage name="bar-stage" &gt;
 *       <i>[...]</i>
 *     &lt;/stage&gt;
 *   &lt;/stages&gt;
 *   <i>&lt;!-- more stages with separate thread management --&gt;</i>
 *   &lt;stages thread-manager="TPC|TPS" processors="1" threads="1" &gt;
 *     &lt;stage name="foobaz-stage" &gt;
 *       <i>[...]</i>
 *     &lt;/stage&gt;
 *     &lt;stage name="foobarbaz-stage" &gt;
 *       <i>[...]</i>
 *     &lt;/stage&gt;
 *   &lt;/stages&gt;
 * &lt;/stage-manager&gt;
 * </pre></m_code>
 * <p>
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class DefaultStageManager extends AbstractLogEnabled
    implements StageManager, Initializable, Disposable, Startable, 
        Configurable, Serviceable
{
    /** The original passed in stage manager's service manager. */
    private ServiceManager m_serviceManager = null;
    
    /** The exposed stage manager's service manager. */
    private ServiceManager m_stageServiceManager = null;

    /** Map that contains all executable stages as defined in configuration. */
    private final Map m_stageMap = new HashMap();
    
    /** Map by roles containing Maps by hints containing stage services*/
    private final Map m_servicesMap = new HashMap();
    
    /** Set that contains all stage services participating in stage system */
    private final Set m_services = new HashSet();
    
    /** A constant identifying a default hint if the hint is null */
    final static String DEFAULT_HINT = "default";

    //------------------------ StageManager implementation
    /**
     * @see StageManager#enqueue(Object, String)
     */
    public void enqueue(Object element, String stageName)
        throws SinkException, NoSuchSinkException
    {
        final Sink queue = getSink(stageName);
        queue.enqueue(element);
    }

    /**
     * @see StageManager#getServiceManager()
     */
    public ServiceManager getServiceManager()
    {
        return m_stageServiceManager;
    }

    //------------------------ Startable implementation
    /**
     * @see Startable#start()
     */
    public void start() throws Exception
    {
        // start execution of all stages
        final Iterator stageExecuters = m_stageMap.values().iterator();
        while (stageExecuters.hasNext())
        {
            ContainerUtil.start(stageExecuters.next());
        }
    }

    /**
     * @see Startable#stop()
     */
    public void stop() throws Exception
    {
        // stop execution of all stages
        final Iterator stageExecuters = m_stageMap.values().iterator();
        while (stageExecuters.hasNext())
        {
            ContainerUtil.stop(stageExecuters.next());
        }
    }

    //------------------------ Serviceable implementation
    /**
     * @see Serviceable#service(ServiceManager)
     */
    public void service(ServiceManager serviceManager)
    {
        m_serviceManager = serviceManager;
    }

    //------------------------ Initializable implementation
    /**
     * @see Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        // 1. initialize all services and therefore sink maps
        final Iterator services = m_services.iterator();
        while(services.hasNext())
        {
            final StageService service = (StageService)services.next();
            ContainerUtil.enableLogging(service, getLogger());
            ContainerUtil.initialize(service);
        }
        
        // 2. initialize all stage executables
        final Iterator stageExecuters = m_stageMap.values().iterator();
        while (stageExecuters.hasNext())
        {
            final Object executable = stageExecuters.next();
            ContainerUtil.enableLogging(executable, getLogger());
            ContainerUtil.initialize(executable);
        }
        
        // wrap existing service manager as parent...
        m_stageServiceManager = new SinkServiceManager(m_serviceManager);
        m_stageServiceManager = new StageServiceManager(m_stageServiceManager);
    }

    //------------------------ Disposable implementation
    /**
     * @see Disposable#dispose()
     */
    public void dispose()
    {
        // 1. dispose all stage executables and shutdown the thread manager(s)
        final Iterator stageExecuters = m_stageMap.values().iterator();
        while (stageExecuters.hasNext())
        {
            final StageExecutable exe = (StageExecutable)stageExecuters.next();
            try
            {
                // automatically stops the execution. 
                // We do not have to call stop.
                ContainerUtil.dispose(exe);
                // Also shutdown the associated thread manager if possible.
                try
                {
                    ContainerUtil.shutdown(exe.getThreadManager());
                }
                catch(Exception e)
                {
                    // ignore since the thread manager might 
                    // already be disposed
                }
            }
            catch (Exception e)
            {
                if (getLogger().isErrorEnabled())
                {
                    getLogger().error("Error during termination of stages", e);
                }
            }
        }
        
        m_stageMap.clear();
        
        // 2. release all services
        final Iterator services = m_services.iterator();
        while(services.hasNext())
        {
            ContainerUtil.dispose(services.next());
        }
        m_services.clear();
        m_servicesMap.clear();
    }

    //------------------------ Configurable implementation
    /**
     * @see Configurable#configure(Configuration)
     */
    public void configure(Configuration configuration) 
        throws ConfigurationException
    {
        try
        {
            // create a thread manager object from the configuration
            final ThreadManager manager = createThreadManager(configuration);
            // get the stages configured for this particular stage manager
            final Configuration[] stages = configuration.getChildren("stage");
            
            for (int i = 0; i < stages.length; i++)
            {
                final Configuration stage = stages[i];
                // get the stage name from the config
                final StageExecutable exe =
                    createStageExecutable(manager, stage);
                final String name = stage.getAttribute("name");
                m_stageMap.put(name, exe);
            }
        }
        catch (Exception e)
        {
            throw new ConfigurationException("Stage Manager configuration.", e);
        }
    }

    //--------------------- DefaultStageManager specific implementation
    /**
     * Returns the sink for a stage with the specified 
     * stage name.
     * @since May 8, 2002
     * 
     * @param stageName
     *  The name of the stage for which the sink map 
     *  should be returned.
     * @return {@link Sink}
     *  A Sink for the specified stage name.
     * @throws NoSuchSinkException
     *  if the sink cannot be found.
     */
    protected Sink getSink(String name) throws NoSuchSinkException
    {
        final StageExecutable executer =
            (StageExecutable) m_stageMap.get(name);

        if (null != executer)
        {
            return (Sink)executer.getEventQueue();
        }
        
        throw new NoSuchSinkException("No such sink under the name " + name);
    }
    
    /**
     * Creates a representation of the concept of the stage
     * as a {@link StageExecutable} object. Sets up the handlers
     * and sink maps as well as the queue for the stage from the
     * passed in configuration.
     * @since Sep 17, 2002
     * 
     * @param stage
     *  The configuration for the stage
     * @param threadManager
     *  The thread manager for the stage
     * @return StageExecutable
     *  The representation of the concept of a stage in SEDA
     */
    protected StageExecutable createStageExecutable(
        ThreadManager threadManager, Configuration stage) throws Exception
    {
        final String name = stage.getAttribute("name");
        
        // Set up the queues for the handler
        final Configuration queue = stage.getChild("queue");
        final Queue[] queues = new Queue[]{ createQueue( queue, name ) };
        
        final Configuration handlers = stage.getChild("handlers");
        final Configuration sinkMap = stage.getChild("sink-map");
        final StagePipeline wrapper = createStage(handlers, queues, sinkMap);
        
        // create a stage executer combining pipeline and thread manager
        return new StageExecutable(wrapper, threadManager);
    }

    /**
     * Creates a stage pipeline wrapper from the stage
     * component and stage configuration. The queues are
     * the sources for the stage.
     * @since Sep 13, 2002
     * 
     * @param sinkMap
     *  The configuration for the component's sink map
     * @param mapped
     *  A list with stage service wrappers that already exist
     * @param queues
     *  The sources for the stage method handlers
     * @param handlers
     *  The configuration for the stage method handlers
     */
    protected StagePipeline createStage(
        Configuration handlers, 
        Queue[] queues, 
        Configuration sinkMap) throws Exception
    {
        final Configuration[] services = handlers.getChildren("service");
        final Map map = new HashMap();
        
        for (int i = 0; i < services.length; i++)
        {
            final Configuration service = services[i];
            final StageService stageService = 
                createStageService(service, sinkMap);
        
            // walk through all handler methods and store them as stage 
            final Configuration[] methods = service.getChildren("handler");
        
            // set up the handlers
            // there can be more than one handler
            for(int j = 0; j < methods.length; j++)
            {
                final Configuration handler = methods[j];
        
                final StageHandler[] stageHandlers = 
                    createStageHandlers(stageService, queues, handler);
                
                for(int k = 0; k < stageHandlers.length; k++)
                {
                    final StageHandler stageHandler = stageHandlers[k];
                    // add to the handler map
                    map.put(stageHandler.getEventType(), stageHandler);
                }
            }
        }
        
        if(map.size() == 1)
        {
            final StageHandler stageHandler = 
                (StageHandler)map.values().toArray()[0];
            // return wrapped single info
            return new StagePipeline(stageHandler, queues);
        }
        
        if(map.size() > 1)
        {
            // return wrapped info map 
            return new StagePipeline(map, queues);
        }
        
        throw new CascadingException("No handler methods defined.");
    }

    /**
     * Creates a Stage Service wrapper object that combines
     * the service reference and selector to release it. Also
     * includes the stage sink map.
     * @since Sep 16, 2002
     * 
     * @param mapped
     *  A list of stage services already created.
     * @param service
     *  The service configuration
     * @param sinkMap
     *  The sink map configuration.
     */
    protected StageService createStageService(
        Configuration service, Configuration sinkMap) 
            throws Exception
    {
        // get the hint from the id field
        final Object id = service.getAttribute("id", null);
        // get the role from the role field
        final String role = service.getAttribute("role");
        
        // get a reference to the service component
        final ServiceSelector selector = createServiceSelector(role);
        // and look up the service component using the stage selector
        // this will work for null hints as well if only one component
        // is installed in the container under the role name
        final Object component = selector.select(id);
        
        if (null == component)
        {
            throw new CascadingException(
                "No service installed with role " + role + ", hint " + id);
        }
        
        final SinkMap map = new DefaultSinkMap(this);
        final String hint = id == null ? DEFAULT_HINT : id.toString();
        
        final StageService stageService;
        if(m_servicesMap.containsKey(role))
        {
            final Map select = (Map)m_servicesMap.get(role);
            if(select.containsKey(hint))
            {
                stageService = (StageService)select.get(hint);
            }
            else
            {
                stageService = new StageService(component, selector, map);
                select.put(hint, stageService);
            }
        }
        else
        {
            stageService = new StageService(component, selector, map);
            
            final Map select = new HashMap();
            select.put(hint, stageService);
            m_servicesMap.put(role, select);
        }
        
        // add to the set
        m_services.add(stageService);
        
        // populate the stage's sink map which will be expanded later
        populateSinkMap(sinkMap, stageService);
        return stageService;
    }

    /**
     * Creates a sink map and adds it to the passed in map 
     * with the service instance as the key.
     * @since Sep 16, 2002
     * 
     * @param sinkMap
     *  The configuration portion for the sink map
     * @param service
     *  The service  to which to add the configuration 
     *  information
     */
    protected void populateSinkMap(Configuration sinkMap, StageService service)
    {
        final DefaultSinkMap map = (DefaultSinkMap)service.getSinkMap();
        
        final Configuration[] sinkConfiguration = sinkMap.getChildren();
        for(int i = 0; i < sinkConfiguration.length; i++)
        {
            final String stage = 
                sinkConfiguration[i].getAttribute("stage-name", null);
                
            if(null != stage)
            {
                // add the sink configuration for later use
                map.addSink(stage, sinkConfiguration[i]);
            }
        }
    }

    /**
     * Creates a thread manager from a configuration object.
     * It initializes the manager with default values if none
     * are configured.
     * @since May 14, 2002
     * 
     * @param threadManager
     *  The {@link Configuration} that specifies the thread manager
     * @return {@link ThreadManager}
     *  the created thread manager object.
     */
    protected ThreadManager createThreadManager(Configuration threadManager)
        throws Exception
    {
        final String threadManagerType =
            threadManager.getAttribute("thread-manager");

        final int processors = threadManager.getAttributeAsInteger(
            "processors", SystemUtil.numProcessors());
        final int threads = threadManager.getAttributeAsInteger("threads", 1);
        final int sleep = threadManager.getAttributeAsInteger("sleep-time", 5);
        final long timeout = threadManager.getAttributeAsLong("timeout", 3000000L);

        if ("TPS".equalsIgnoreCase(threadManagerType))
        {
            // ignore until implemented
            final ThreadManager manager = 
                new TPSPThreadManager(processors, threads, sleep, timeout);
            ContainerUtil.enableLogging(manager, getLogger());
            ContainerUtil.initialize(manager);
            ContainerUtil.start(manager);
            return manager;
        }

        final ThreadManager manager = new TPCThreadManager();
        ContainerUtil.enableLogging(manager, getLogger());
        
        final Properties properties = new Properties();
        properties.put("processors", String.valueOf(processors) );
        properties.put("threads-per-processor", String.valueOf(threads) );
        properties.put("sleep-time", String.valueOf(sleep) );
        properties.put("block-timeout", String.valueOf(timeout) );
        final Parameters parameters = Parameters.fromProperties(properties);
        ContainerUtil.parameterize(manager, parameters);
        
        ContainerUtil.initialize(manager);
        ContainerUtil.start(manager);
        return manager;
    }

    /**
     * Creates a queue from a configuration object.
     * It initializes the queue with the configured values.
     * <m_code><pre>
     *  <i>&lt;!-- Optionally a queue type and predicate for 
     *       this process can be defined. The default is VARIABLE 
     *       without predicate --&gt;</i>
     *  &lt;queue type="FIXED|VARIABLE" size="1" timeout="1000"&gt;
     *    &lt;!-- predicate type="RATE-LIMITING|THRESHOLD|NONE"/ --&gt;
     *  &lt;/queue&gt;
     * </pre></m_code>
     * @since May 14, 2002
     * 
     * @param name
     *  The name of the stage for which the queue is setup
     * @param queue
     *  The {@link Configuration} that specifies the queue
     * @return {@link Queue}
     *  the created m_sink object.
     */
    protected Queue createQueue(Configuration queue, String name)
        throws ConfigurationException
    {
        final String queueType = queue.getAttribute("type", "variable");
        final int size = queue.getAttributeAsInteger("size", -1);
        final long timeout = queue.getAttributeAsLong("timeout", 0);
        
        final Configuration pre = queue.getChild("predicate", true);
        final EnqueuePredicate predicate = createPredicate(pre);

        if ("fixed".equalsIgnoreCase(queueType))
        {
            if (size != -1)
            {
                // size is specified and fixed queue is returned
                final Queue fixedQueue = new FixedSizeQueue(name, size);
                //fixedQueue.setEnqueuePredicate(predicate);
                fixedQueue.setTimeout(timeout);
                return fixedQueue;
            }

            throw new ConfigurationException(
                "Size for fixed size queue is not specified");
        }

        // default: return a new default and variable queue    
        final Queue defaultQueue = new DefaultQueue(name, size);
        //defaultQueue.setEnqueuePredicate(predicate);
        defaultQueue.setTimeout(timeout);
        return defaultQueue;
    }

    /**
     * Creates a predicate from a configuration object.
     * It initializes the predicate with the configured values.
     * Returns <m_code>null</m_code> if no predicate was specified.
     * @since May 14, 2002
     * 
     * @param predicate
     *  The {@link Configuration} that specifies the predicate
     * @return {@link EnqueuePredicate}
     *  the created predicate for the m_sink. Returns <m_code>null</m_code> 
     *  if no predicate was specified.
     */
    protected EnqueuePredicate createPredicate(Configuration predicate)
        throws ConfigurationException
    {
        final String predicateType = predicate.getAttribute("type", null);

        if (null == predicateType)
        {
            // no predicate is specified, so just return null;
            return null;
        }

        if ("threshold".equalsIgnoreCase(predicateType))
        {
            final int threshold =
                predicate.getAttributeAsInteger("threshold", -1);

            if (threshold != -1)
            {
                // size is specified and fixed m_sink is returned
                return new ThresholdPredicate(threshold);
            }

            throw new ConfigurationException(
                "Threshold for threshold predicate is not specified");
        }

        if ("rate-limiting".equalsIgnoreCase(predicateType))
        {
            final float targetRate =
                predicate.getAttributeAsFloat("rate-target", -1.0f);
            final int depth = predicate.getAttributeAsInteger("depth", -1);

            if (targetRate == -1.0 && depth < 0)
            {
                // size is specified and fixed m_sink is returned
                return new RateLimitingPredicate(
                    (double) targetRate,
                    depth);
            }

            throw new ConfigurationException(
                "target rate and depth are not properly defined.");
        }

        throw new ConfigurationException(
            "Predicate type " + predicateType + " is not supported.");
    }
    
    /**
     * Returns the handler described in the handler part 
     * of the configuration which is passed into this
     * method together with the service component. 
     * @since Sep 12, 2002
     * 
     * @param service
     *  The service component 
     * @param handler
     *  The information about the method signature(s) and 
     *  parameter type(s)
     * @param queues
     *  The stage's queues
     * @return StageHandler[]
     *  The meta info objects for the handler methods
     * @throws Exception
     *  In the case an exception occurred creating the 
     *  handler object
     */
    protected StageHandler[] createStageHandlers(
        StageService service, Queue[] queues, Configuration handler) 
            throws Exception
    {
        // get the queue's stage name for return value;
        final String returnSink = handler.getAttribute("return-sink", null);
        // get the queue's stage name for exceptions;
        final String errorSink = handler.getAttribute("exception-sink", null);

        final String className = handler.getAttribute("interface", null);
        
        final StageHandler[] handlers;
        if(className == null)
        {
            // get the handler method from the configuration
            final String signature = handler.getAttribute("signature");
            final String type = handler.getAttribute("type", null);
            
            final StageHandler sh = new StageHandler(service, signature, type);
            // add a reference into the service so we can look it up later
            service.setSink(sh.getHandler(), queues);
            
            handlers = new StageHandler[] { sh };
        }
        else
        {
            final ClassLoader loader = 
                Thread.currentThread().getContextClassLoader();
            // get the handlers from the interface
            final Class inter = Class.forName(className, false, loader);
            
            final Method[] methods = inter.getMethods();
            final List list = new ArrayList(methods.length);
            for(int i = 0; i < methods.length; i++)
            {
                final Method method = methods[i];
                if(method.getParameterTypes().length == 1)
                {
                    final StageHandler sh = new StageHandler(service, method);
                    // add a reference into the service so we can look it up later
                    service.setSink(sh.getHandler(), queues);
                    list.add(sh);
                }
            }

            final StageHandler[] array = new StageHandler[list.size()];
            handlers = (StageHandler[])list.toArray(array);
        }

        final DefaultSinkMap sinkMap = new DefaultSinkMap(this);
        sinkMap.addSink(returnSink, StageHandler.RETURN_SINK);
        sinkMap.addSink(errorSink, StageHandler.EXCEPTION_SINK);
        
        for(int i = 0; i < handlers.length; i++)
        {
            // the default sink map will be initialized by the stage handler
            handlers[i].setSinkMap(sinkMap);
        }
        return handlers;
    }

    /**
     * Returns a service selector with which we can select
     * the deployed services within the same container.
     * @since May 15, 2002
     * 
     * @return {@link ServiceSelector}
     *  A component selector for stages.
     * @throws ServiceException
     *  If the service could not be retrieved
     */
    protected ServiceSelector createServiceSelector(final String role) 
        throws ServiceException
    {
        // get the stage selector or if only one stage is installed the 
        // installed stage.
        final Object component = m_serviceManager.lookup(role);
        // if only one stage is installed in the system wrap in
        // an anonymous inner class component selector
        if (component instanceof ServiceSelector)
        {
            return (ServiceSelector) component;
        }
        else
        {
            // the component manager takes care of it being of type Stage
            // Create component selector that exposes only this stage
            return new ServiceSelector()
            {
                /** The hint stored for comparison */
                Object m_hint = null;
                
                //---------------------- ServiceSelector implementation
                /**
                 * @see ServiceSelector#select(java.lang.Object)
                 */
                public Object select(Object hint) throws ServiceException
                {
                    // test if more than one component is ordered 
                    // with different hints.
                    if (m_hint != null && m_hint != hint)
                    {
                        throw new ServiceException(
                            role + "\\hint", "Service not installed.");
                    }

                    m_hint = hint;

                    return component;
                }

                /**
                 * @see ServiceSelector#release(java.lang.Object)
                 */
                public void release(Object component)
                {
                    m_serviceManager.release(component);
                }

                /**
                 * @see ServiceSelector#isSelectable(java.lang.Object)
                 */
                public boolean isSelectable(Object hint)
                {
                    if (m_hint != null && m_hint != hint)
                    {
                        return false;
                    }

                    m_hint = hint;

                    return true;
                }
            }; //-- end anonymous class
        }
    }
    
    /**
     * Returns a map containing Stage executable objects
     * that also contain a queue to enqueue elements for
     * a specific stage to.
     * @since Sep 16, 2002
     * 
     * @return Map
     *  A Map containing Stage executable objects
     */
    final Map getStageMap()
    {
        return m_stageMap;
    }
    
    /**
     * Returns a map containing Stage service objects.
     * @since Sep 16, 2002
     * 
     * @return Map
     *  A Map containing stage services
     */
    final Map getServicesMap()
    {
        return m_servicesMap;
    }
    
    //--------------------------- DefaultStageManager inner classes
    /**
     * An implementation of a ServiceSelector that serves stage
     * component proxies from a map filled with StageServices. 
     * @since Oct 1, 2002
     * 
     * @author <a href="mailto:mschier@bsquare.com">Marc Schier</a>
     */
    final class StageServiceSelector implements ServiceSelector
    {
        /** A map filled with StageServices */
        private Map m_map;
        
        /** The role under which the selector was looked up */
        private String m_role;
        
        //---------------------------- StageServiceSelector constructors
        /**
         * Constructor that creates a StageServiceSelector based
         * on a map filled with StageServices.
         * @since Oct 1, 2002
         * 
         * @param map
         *  The map filled with StageServices
         * @param role
         *  The role under which the selector was looked up
         */
        public StageServiceSelector(String role, Map map)
        {
            super();
            m_role = role;
            m_map = map;
        }
        
        //---------------------------- ServiceSelector implementation
        /**
         * @see ServiceSelector#isSelectable(Object)
         */
        public boolean isSelectable(Object policy)
        {
            return m_map.containsKey(policy);
        }

        /**
         * @see ServiceSelector#release(Object)
         */
        public void release(Object object)
        {
        }

        /**
         * @see ServiceSelector#select(Object)
         */
        public Object select(Object policy) throws ServiceException
        {
            final Object service = m_map.get(policy);
            
            if(null != service)
            {
                return service;
            }
            
            throw new ServiceException(
                m_role, "No such service for hint " + policy);
        }
    }
    
    /**
     * An implementation of a ServiceManager that merges stage
     * component invocation with a parent service manager. The 
     * stage components are served as dynamic proxy objects.
     * @since Oct 1, 2002
     * 
     * @author <a href="mailto:mschier@bsquare.com">Marc Schier</a>
     */
    final class StageServiceManager implements ServiceManager
    {
        /** The parent service manager with the services */
        private ServiceManager m_parent;
        
        //---------------------------- StageServiceManager constructors
        /**
         * Constructor that creates a StageServiceManager based
         * on a parent service manager.
         * @since Oct 1, 2002
         * 
         * @param parent
         *  The parent service manager
         */
        public StageServiceManager(ServiceManager parent)
        {
            super();
            m_parent = parent;
        }

        //---------------------------- ServiceManager implementation
        /**
         * @see ServiceManager#hasService(String)
         */
        public boolean hasService(String role)
        {
            final boolean has = m_servicesMap.containsKey(role);
            return has ? true : m_parent.hasService(role);
        }

        /**
         * @see ServiceManager#lookup(String)
         */
        public Object lookup(String role) throws ServiceException
        {
            final Map map = (Map)m_servicesMap.get(role);
            if(null != map)
            {
                if(map.size() == 1)
                {
                    final StageService service = 
                        (StageService)map.values().toArray()[0];
                    return service.getComponentProxy();
                }
                
                // otherwise return a service selector implementation                
                return new StageServiceSelector(role, map);
            }
            return m_parent.lookup(role);
        }

        /**
         * @see ServiceManager#release(Object)
         */
        public void release(Object object)
        {
            // keep map of checked out objects ?
            m_parent.release(object);
        }
    }
   
    /**
     * An implementation of a ServiceManager that merges stage
     * queues with a parent service manager.
     * @since Oct 1, 2002
     * 
     * @author <a href="mailto:mschier@bsquare.com">Marc Schier</a>
     */
    final class SinkServiceManager implements ServiceManager
    {
        /** The parent service manager with the services */
        private ServiceManager m_parent;
        
        //---------------------------- SinkServiceManager constructors
        /**
         * Constructor that creates a SinkServiceManager based
         * on a parent service manager.
         * @since Oct 1, 2002
         * 
         * @param parent
         *  The parent service manager
         */
        public SinkServiceManager(ServiceManager parent)
        {
            super();
            m_parent = parent;
        }
        
        //---------------------------- ServiceManager implementation
        /**
         * @see ServiceManager#hasService(String)
         */
        public boolean hasService(String role)
        {
            return m_stageMap.containsKey(role) ? true : m_parent.hasService(role);
        }

        /**
         * @see ServiceManager#lookup(String)
         */
        public Object lookup(String role) throws ServiceException
        {
            final StageExecutable executer =
                (StageExecutable) m_stageMap.get(role);
    
            if (null != executer)
            {
                return (Sink)executer.getEventQueue();
            }
            return m_parent.lookup(role);
        }

        /**
         * @see ServiceManager#release(Object)
         */
        public void release(Object object)
        {
            // rather keep map of checked out objects ?
            m_parent.release(object);
        }
    }
}