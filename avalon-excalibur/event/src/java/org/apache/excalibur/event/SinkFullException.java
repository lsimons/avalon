/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.event;

/**
 * A SinkException is thrown when an enqueue operation occurs on a queue that is
 * already full.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public class SinkFullException extends SinkException
{
    public SinkFullException( String message )
    {
        super( message );
    }

    public SinkFullException( String message, Throwable e )
    {
        super( message, e );
    }
}
