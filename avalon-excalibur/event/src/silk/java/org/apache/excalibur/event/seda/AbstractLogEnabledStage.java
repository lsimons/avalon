/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.seda;

import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 * An abstract base class implementation for a stage.  
 * It provides a base implementation for setting the
 * sink map.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public abstract class AbstractLogEnabledStage extends AbstractLogEnabled 
    implements Stage
{
    /** The sink map associated with this stage. */
    private SinkMap m_sinkMap = null;

    //---------------------- Stage implementation
    /**
     * @see Stage#setSinkMap(SinkMap)
     */
    public final void setSinkMap(SinkMap sinkMap)
    {
        m_sinkMap = sinkMap;
    }

    /**
     * Returns the sink map associated with this stage.
     * The method is protected to give access to the
     * sink map to sub classing component implementations.
     * @since May 14, 2002
     * 
     * @return {@link SinkMap}
     *  the sink map associated with this stage.
     */
    protected final SinkMap getSinkMap()
    {
        return m_sinkMap;
    }
}
