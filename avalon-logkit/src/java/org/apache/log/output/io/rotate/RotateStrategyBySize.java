/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output.io.rotate;

/**
 * rotation stragety based on size written to log file.
 *
 * @author <a href="mailto:bh22351@i-one.at">Bernhard Huber</a>
 */
class RotateStrategyBySize implements RotateStrategy {
    long maxSize;
    long currentSize;

    /**
     * rotate logs by size.
     * By default do log rotation after writing approx. 1MB of messages
     */
    RotateStrategyBySize( ) {
        currentSize = 0;
        setMaxSize( 1024 * 1024 );
    }
    /**
     *  rotate logs by size.
     *  @param max_size rotate after writing max_size [byte] of messages
     */
    RotateStrategyBySize( long max_size ) {
        this();
        setMaxSize( max_size );
    }
    /**
     * get the rotation max size value.
     * @return long current rotation max size value [byte]
     */
    public long getMaxSize( ) {
        return maxSize;
    }
    /**
     *  set the rotation max size value.
     *  @param max_size new rotation max size value [byte]
     */
    public void setMaxSize( long max_size ) {
        maxSize = max_size;
    }
    /**
     * reset log size written so far.
     */
    public void reset() {
        currentSize = 0;
    }
    /**
     *  check if now a log rotation is neccessary.
     *  @param data the last message written to the log system
     *  @return boolean return true if log rotation is neccessary, else false
     */
    public boolean isRotationNeeded( String data ) {
        currentSize += data.length();
        if (currentSize >= maxSize) {
            currentSize = 0;
            return true;
        } else {
            return false;
        }
    }
}

