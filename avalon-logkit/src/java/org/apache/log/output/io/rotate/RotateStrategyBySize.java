/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output.io.rotate;

/**
 * Rotation stragety based on size written to log file.
 *
 * @author <a href="mailto:bh22351@i-one.at">Bernhard Huber</a>
 */
public class RotateStrategyBySize 
    implements RotateStrategy
{
    private long m_maxSize;
    private long m_currentSize;

    /**
     * Rotate logs by size.
     * By default do log rotation after writing approx. 1MB of messages
     */
    public RotateStrategyBySize()
    {
        this( 1024 * 1024 );
    }

    /**
     *  Rotate logs by size.
     *
     *  @param max_size rotate after writing max_size [byte] of messages
     */
    public RotateStrategyBySize( final long maxSize ) 
    {
        m_currentSize = 0;
        setMaxSize( maxSize );
    }

    /**
     * Get the rotation max size value.
     *
     * @return long current rotation max size value [byte]
     */
    public long getMaxSize() 
    {
        return m_maxSize;
    }

    /**
     *  Set the rotation max size value.
     *
     *  @param maxSize new rotation max size value [byte]
     */
    public void setMaxSize( final long maxSize ) 
    {
        m_maxSize = maxSize;
    }

    /**
     * reset log size written so far.
     */
    public void reset() 
    {
        m_currentSize = 0;
    }

    /**
     *  check if now a log rotation is neccessary.
     *  @param data the last message written to the log system
     *  @return boolean return true if log rotation is neccessary, else false
     */
    public boolean isRotationNeeded( final String data )
    {
        m_currentSize += data.length();
        if( m_currentSize >= m_maxSize )
        {
            m_currentSize = 0;
            return true;
        } 
        else
        {
            return false;
        }
    }
}

