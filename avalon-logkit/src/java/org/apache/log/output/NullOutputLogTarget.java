/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output;

/**
 * A output target that does nothing. Useful for profiling.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class NullOutputLogTarget 
    extends AbstractOutputTarget
{
    public NullOutputLogTarget()
    {
        open();
    }

    /**
     * Do nothing output method.
     *
     * @param data the data to be output
     */
    protected void write( final String data )
    {
    }
}
