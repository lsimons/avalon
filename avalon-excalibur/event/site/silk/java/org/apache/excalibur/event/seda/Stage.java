/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.seda;

/**
 * Lifecycle interface that allows a container to set a sink map 
 * for an object that needs sink information. It allows the system 
 * to be set up in a pipeline, with asynchronously managed pipelines. 
 * Stages are usually managed by name in its container. In the 
 * modified SEDA architecture, the stages and services are connected 
 * through the {@link StageManager} implementing manager service.
 * 
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public interface Stage
{
    /**
     * Sets the SinkMap for the <m_code>SinkMapEnabled</m_code>. 
     * Object. This allows the object to specify exactly what 
     * sink is fed with events. The SinkMap is set once, and 
     * only once during the life of the object. 
     * @since May 8, 2002
     * 
     * @param map
     *  The {@link SinkMap} for the <m_code>Stage</m_code>.
     * @throws NoSuchSinkException
     *  If a sink cannot be found in the map
     */
    void setSinkMap(SinkMap map) throws NoSuchSinkException;
}
