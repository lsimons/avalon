/*
 * Copyright (C) The Apache Software Foundation, All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.services.silk;

/**
 * The NoSuchSinkException is used by the SinkMap when a Sink that is
 * supposed to be associated with a name is not found.
 *
 * @author <a href="bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/03/16 00:18:35 $
 */

public class NoSuchSinkException extends Exception
{

    public NoSuchSinkException()
    {
        super();
    }

    public NoSuchSinkException( String message )
    {
        super( message );
    }
}