/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.pipeline;

import java.util.Iterator;

/**
 * This represents a pipeline made up of stages.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class ProcessorPipeline
    extends DefaultPipeline
    implements ProcessorStage
{
    public void process( final Object object )
    {
        final Iterator stages = m_stages.iterator();

        while( stages.hasNext() )
        {
            ((ProcessorStage)stages.next()).process( object );
        }
    }

    public Stage getStage( final int index )
    {
        return (Stage)m_stages.get( index );
    }

    public int getSize()
    {
        return m_stages.size();
    }
}
