/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.event;

import org.apache.avalon.framework.CascadingException;

/**
 * A SinkClosedException is thrown when an enqueue operation occurs on a queue
 * that is already closed.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public class SinkClosedException extends SinkException
{
    public SinkClosedException( String message )
    {
        super( message );
    }

    public SinkClosedException( String message, Throwable e )
    {
        super( message, e );
    }
}
