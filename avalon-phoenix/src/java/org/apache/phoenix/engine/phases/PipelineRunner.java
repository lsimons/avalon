/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.phases;

import org.apache.avalon.camelot.pipeline.LifeCycleStage;
import org.apache.phoenix.Block;
import org.apache.phoenix.engine.blocks.BlockEntry;

class PipelineRunner
    implements Runnable
{
    protected final ClassLoader     m_classLoader;
    protected final LifeCycleStage  m_pipeline;
    protected final String          m_name;
    protected final BlockEntry      m_entry;

    protected Exception             m_exception;

    public PipelineRunner( final String name, 
                           final BlockEntry entry, 
                           final LifeCycleStage pipeline,
                           final ClassLoader classLoader )
    {
        m_name = name;
        m_entry = entry;
        m_pipeline = pipeline;
        m_classLoader = classLoader;
    }

    public void run()
    {
        m_exception = null;

        try
        {
            Thread.currentThread().setContextClassLoader( m_classLoader );
            m_pipeline.process( m_name, m_entry );
        }
        catch( final Exception e )
        {
            m_exception = e;
        }
    }

    public Exception getException()
    {
        return m_exception;
    }
}
