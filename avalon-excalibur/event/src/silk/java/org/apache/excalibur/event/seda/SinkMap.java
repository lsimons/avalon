/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.seda;

import java.util.Collection;

import org.apache.excalibur.event.Sink;

/**
 * The SinkMap is an abstraction to allow the manager 
 * to centrally manage all connections within its scope.  
 * This allows the system to remain using Inversion of 
 * Control, while allowing a system to be reassigned at 
 * any time.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public interface SinkMap
{
    /**
     * This gets the default Sink for the SinkMap.
     * A SinkMap must have at least one default Sink.
     * The method returns an array of sinks.  In the 
     * case the default sink is replicated, the handler
     * can decide on which sink to enqueue its event 
     * element.
     * @since May 8, 2002
     *
     * @return {@link Sink}
     *  the main {@link Sink} array for the ConfigurableSinkMap.
     */
    Sink getDefaultSink();

    /**
     * Returns the named sink array.  In the case the 
     * requested sink is replicated, the handler can decide 
     * on which sink to enqueue its event element. If the 
     * ConfigurableSinkMap does not contain a match for the
     * name, the method throws an exception.
     * @since May 8, 2002
     *
     * @param stage  
     *  The name of the desired stage's sink
     * @return Sink 
     *  The sink associated with the stage name
     * @throws NoSuchSinkException 
     *  If no sink is associated with the stage name
     */
    Sink getSink(String stage) throws NoSuchSinkException;

    /**
     * Returns a list of sinks. The list contains all
     * sinks inside this sink map.  The stage can get 
     * a list of all sinks attached to the stage.
     * @since May 8, 2002
     *
     * @return Collection
     *  A Collection of {@link Sink} implementing objects.  
     *  There is at least one Sink in the list (The default one).
     */
    Collection getAllSinks();

    /**
     * Returns an array of stage names whose sinks are contained
     * in this map.
     * @since May 14, 2002
     * 
     * @return String[]
     *  an array of stage names whose sinks this map contains.
     */
    String[] getStageNames();

    /**
     * Returns a boolean value indicating whether the sink with
     * the associated stage name is contained in this map.
     * @since May 14, 2002
     * 
     * @param stage
     *  the name of the stage to check the sink with
     * @return boolean
     *  <m_code>true</m_code> if the sink associated with this stage 
     *  name is contained in this map, <m_code>false</m_code> otherwise.
     */
    boolean containsSink(String stage);
}