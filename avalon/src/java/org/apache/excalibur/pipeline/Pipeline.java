/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.pipeline;

import java.util.NoSuchElementException;

/**
 * This represents a pipeline made up of stages.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Pipeline 
    extends Stage
{
    /**
     * Retrieve size of pipeline (number of stages).
     *
     * @return the size of pipeline
     */
    int getSize();

    /**
     * Retrieve a particular stage of pipeline
     *
     * @param index the index of stage
     * @return the stage
     * @exception NoSuchElementException if index >= getSize() or index < 0
     */
    Stage getStage( int index )
        throws NoSuchElementException;
}
