/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.seda;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.CascadingException;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.ext.LossyMultiCastSink;
import org.apache.excalibur.event.ext.MultiCastSink;

/**
 * The default implementation of a sink map. It stores
 * the referenced sinks in a HashMap.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
final class DefaultSinkMap extends AbstractLogEnabled
    implements SinkMap, Initializable, Disposable
{
    /** A map to reference all contained sinks. */
    private final Map m_sinkMap = new HashMap();

    /** The default sink of this sink map. */
    private Sink m_defaultSink = null;
    
    /** The default stage manager that created this object. */
    private DefaultStageManager m_manager;
    
    //------------------------ DefaultSinkMap constructors
    /**
     * Creates a sink map with a reference to the stage manager
     * that created it.
     * @since Sep 16, 2002
     * 
     * @param manager
     *  The default stage manager that created this object.
     */
    DefaultSinkMap(DefaultStageManager manager)
    {
        super();
        m_manager = manager;
    }
    
    //------------------------ SinkMap implementation
    /**
     * @see SinkMap#containsSink(String)
     */
    public boolean containsSink(String stage)
    {
        return m_sinkMap.containsKey(stage);
    }

    /**
     * @see SinkMap#getAllSinks()
     */
    public Collection getAllSinks()
    {
        return m_sinkMap.entrySet();
    }

    /**
     * @see SinkMap#getDefaultSink()
     */
    public Sink getDefaultSink()
    {
        return m_defaultSink;
    }

    /**
     * @see SinkMap#getSink(String)
     */
    public Sink getSink(String stage) throws NoSuchSinkException
    {
        return (Sink) m_sinkMap.get(stage);
    }

    /**
     * @see SinkMap#getStageNames()
     */
    public String[] getStageNames()
    {
        final String[] names = new String[m_sinkMap.size()];
        return (String[]) m_sinkMap.keySet().toArray(names);
    }

    //------------------------ DefaultSinkMap specific implementation
    /**
     * Allows to add a sink to the map under the specified stage 
     * name with the specified sink configuration describing 
     * additional information about the sink.
     * @since Sep 16, 2002
     * 
     * @param sink
     *  The configuration describing additional information about
     *  the sink in the sink map
     * @param stage
     *  The name under which the sink is added.
     */
    void addSink(String stage, Configuration sink)
    {
        if(null != stage && !m_sinkMap.containsKey(stage))
        {
            m_sinkMap.put(stage, sink);
        }
    }
    
    /**
     * Allows to add a sink to the map under the specified
     * stage name. 
     * @since Sep 16, 2002
     * 
     * @param stage
     *  The name under which the sink is added.
     */
    void addSink(String stage)
    {
        if(null != stage && !m_sinkMap.containsKey(stage))
        {
            m_sinkMap.put(stage, stage);
        }
    }
    
    /**
     * Allows to add a sink to the map under the specified
     * stage name and alias name. 
     * @since Sep 16, 2002
     * 
     * @param stage
     *  The name under which the sink is added.
     */
    void addSink(String stage, String alias)
    {
        if(null != stage && !m_sinkMap.containsKey(stage))
        {
            m_sinkMap.put(stage, alias);
        }
    }

    //------------------------ Disposable implementation
    /**
     * @see Disposable#dispose()
     */
    public void dispose()
    {
        m_manager = null;
        m_sinkMap.clear();
        m_defaultSink = null;
    }

    //------------------------ Initializable implementation
    /**
     * @see Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        final Iterator stages = m_sinkMap.keySet().iterator();
        while (stages.hasNext())
        {
            String stage = (String) stages.next();
            final Object initInfo = m_sinkMap.get(stage);

            Sink queue = null;
            boolean main = false;

            if (initInfo instanceof Configuration)
            {
                final Configuration sink = (Configuration)initInfo;
                main = sink.getAttributeAsBoolean("default", false);
                if(sink.getName().equals("sink-set"))
                {
                    queue = createMultiCastSink(sink);
                }
            }
            
            if(null == queue)
            {
                // default. Will work even if no configuration is set with 
                // addSink(String) instead of addSink(String, Configuration)
                final StageExecutable executer =
                    (StageExecutable) m_manager.getStageMap().get(stage);

                if (executer != null)
                {
                    queue = executer.getEventQueue();
                }
                else
                {
                    continue;
                }
                
                if(initInfo instanceof Configuration)
                {
                    final Configuration sink = (Configuration)initInfo;
                    main = sink.getAttributeAsBoolean("default", false);
                }
                else
                {
                    // if it's a string it's an alias for the stage name
                    stage = initInfo.toString();
                }
            }

            m_sinkMap.put(stage, queue);

            if (main || m_defaultSink == null)
            {
                m_defaultSink = (Sink) queue;
            }
        }
    }

    /**
     * Creates sinks based on the sink list and delivery
     * method. Default delivery method is ALL.
     * @since Aug 21, 2002
     * 
     * @param delivery
     *  The method of delivery in the configuration. 
     * @param sinkList 
     *  A list of sinks from which the multicast sink
     *  is created.
     * @return Sink
     *  A multi caster sink
     */
    protected Sink createMultiCastSink(Collection sinkList, String delivery)
    {
        if ("ONE".equalsIgnoreCase(delivery))
        {
            return new MultiCastSink(sinkList, true);
        }
        else if ("ONE*".equalsIgnoreCase(delivery))
        {
            return new LossyMultiCastSink(sinkList, true);
        }
        else if ("ZERO*".equalsIgnoreCase(delivery))
        {
            return new LossyMultiCastSink(sinkList, false);
        }
        return new MultiCastSink(sinkList, false);
    }


    /**
     * Sets up the sink set from the specified configuration
     * @since Sep 12, 2002
     * 
     * @param sinks
     *  The configuration for the sink set.
     * @throws Exception
     *  If the creation failed because of bad configuration
     */
    protected Sink createMultiCastSink(Configuration sinks)
        throws Exception
    {
        final Configuration[] multiSinks = sinks.getChildren("sink");
        
        final Collection sinkList =
            new ArrayList(multiSinks.length);
        
        for (int i = 0; i < multiSinks.length; i++)
        {
            // the real stage name to look up the stage with
            final String multiSinkStage =
                multiSinks[i].getAttribute("stage-name");
        
            final StageExecutable executer =
                (StageExecutable) m_manager.getStageMap().get(multiSinkStage);
        
            if (executer == null)
            {
                throw new CascadingException(
                    "Stage name specified for sink-set sink is unknown.");
            }
            sinkList.add(executer.getEventQueue());
        }
        
        // delivery="ALL|ONE|ONE*|ZERO*" default is ALL
        final String delivery =
            sinks.getAttribute("delivery", "ALL");
        return createMultiCastSink(sinkList, delivery);
    }
} 
