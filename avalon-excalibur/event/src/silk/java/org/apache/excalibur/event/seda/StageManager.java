/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.seda;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.event.SinkException;

/**
 * The stage manager provides the mechanism to join seemingly 
 * separated blocks into one large Staged Event Driven Architecture 
 * server. The concepts listed in this specification are heavily  
 * influenced by Matt Welsh's SEDA architecture. It has been tested 
 * to be insanely scalable, to the point where a Java based server 
 * can service more clients than its C based counterpart. The stage 
 * manager component is used to get access to the {@link Sink}s that
 * are associated with a particular stage. Therefore the stage manager
 * is probably exported by a container so a container client can 
 * perform enqueue operations.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public interface StageManager
{
    String ROLE = StageManager.class.getName();

    /**
     * Allows to enqueue an element into the sink of the stage 
     * with the specific name. This is mostly used to notify a 
     * stage to start executing or performing.
     * @since May 24, 2002
     * 
     * @param element
     *  The queue element to enqueue.
     * @param stageName
     *  The stage name which identifies the sink to
     *  enqueue to.
     * @throws SinkException
     *  If there is a problem enqueuing the element.
     *  In the case the stage has more than one sink
     *  the last exception is re-thrown.
     * @throws NoSuchSinkException
     *  If the sink cannot be found.
     */
    void enqueue(Object element, String stageName)
        throws SinkException, NoSuchSinkException;

    /**
     * Returns a service manager reference which allows to
     * retrieve stage queues.
     * @since May 8, 2002
     * 
     * @return {@link ServiceManager}
     *  A ServiceManager which allows to retrieve stage queues.
     */
    ServiceManager getServiceManager();
}
