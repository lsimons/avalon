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
 * A SourceException is thrown when an enqueue operation fails.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public class SourceException extends CascadingException
{
     public SourceException( String message )
     {
         super( message );
     }

     public SourceException( String message, Throwable e )
     {
         super( message, e );
     }
}
