/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.pipeline;

import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * This is basic array based pipeline.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultPipeline 
    implements Pipeline
{
    protected final ArrayList        m_stages     = new ArrayList();

    /**
     * Retrieve size of pipeline (number of stages).
     *
     * @return the size of pipeline
     */
    public int getSize()
    {
        return m_stages.size();
    }

    /**
     * Retrieve a particular stage of pipeline
     *
     * @param index the index of stage
     * @return the stage
     * @exception NoSuchElementException if index >= getSize() or index < 0
     */
    public Stage getStage( final int index )
        throws NoSuchElementException
    {
        return (Stage)m_stages.get( index );
    }

    public void addStage( final Stage stage )
    {
        m_stages.add( stage );
    }
}
