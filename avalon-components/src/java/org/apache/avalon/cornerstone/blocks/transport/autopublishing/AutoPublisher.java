
/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.blocks.transport.autopublishing;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.CascadingRuntimeException;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.phoenix.Block;
import org.apache.avalon.phoenix.BlockEvent;
import org.apache.avalon.phoenix.BlockListener;
import org.apache.commons.altrmi.server.AltrmiPublisher;
import org.apache.commons.altrmi.server.AltrmiPublicationException;


/**
 * Class AutoPublisher
 *
 * This is inprogress.  The order of block being added is not guaranteed.
 *
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.4 $
 */
public class AutoPublisher implements Configurable, BlockListener
{

    private String m_publisherName;
    private AltrmiPublisher m_altrmiPublisher;
    private Map m_publications;
    private List m_queue;

    /**
     * Method configure
     *
     *
     * @param configuration
     *
     * @throws ConfigurationException
     *
     */
    public void configure(final Configuration configuration) throws ConfigurationException
    {

        m_publisherName = configuration.getChild("publisher").getValue("altrmification");
        m_publications = new HashMap();

        final Configuration[] confs = configuration.getChildren("publish");

        for (int i = 0; i < confs.length; i++)
        {
            final Configuration conf = confs[i];
            final String blockName = conf.getAttribute("block");
            final String publishAsName = conf.getAttribute("publishAsName");
            final String interfaceToPublish = conf.getAttribute("interfaceToPublish");

            m_publications.put(blockName, new PublicationInfo(publishAsName, interfaceToPublish));
        }

        m_queue = new ArrayList();
    }

    /**
     * Method blockAdded
     *
     *
     * @param event
     *
     */
    public void blockAdded(final BlockEvent event)
    {

        System.out.println("Block " + event.getName() + " added");

        if (m_publisherName.equals(event.getName()))
        {
            m_altrmiPublisher = (AltrmiPublisher) event.getBlock();
        }

        if (m_publications.containsKey(event.getName()))
        {
            final Block block = event.getBlock();
            final String blockName = event.getName();
            PublicationInfo pi = (PublicationInfo) m_publications.get(event.getName());

            try
            {
                m_altrmiPublisher.publish(block, pi.getPublishAsName(),
                                          Class.forName(pi.getInterfaceToPublish()));
            }
            catch (AltrmiPublicationException e)
            {
                throw new CascadingRuntimeException("Some problem auto-publishing", e);
            }
            catch (ClassNotFoundException e)
            {
                throw new CascadingRuntimeException(
                    "Interface specifies in config.xml ('interfaceToPublish' attribte) not found",
                    e);
            }
        }
    }

    /**
     * Method blockRemoved
     *
     *
     * @param event
     *
     */
    public void blockRemoved(final BlockEvent event)
    {

        System.out.println("Block " + event.getName() + " removed");

        if (m_publications.containsKey(event.getName()))
        {
            final Block block = event.getBlock();
            final String blockName = event.getName();
            PublicationInfo pi = (PublicationInfo) m_publications.get(event.getName());

            try
            {
                m_altrmiPublisher.unPublish(block, pi.getPublishAsName());
            }
            catch (AltrmiPublicationException e)
            {
                throw new CascadingRuntimeException("Some problem un-auto-publishing", e);
            }
        }
    }
}
