/*
 * Copyright (C) The Apache Software Foundation, All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.services.silk;

import org.apache.avalon.framework.CascadingException;

/**
 * The NoSuchSourceException is used by the SourceMap when a Source that is
 * supposed to be associated with a name is not found.
 *
 * @author <a href="bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/02/01 20:06:24 $
 */

public class NoSuchSourceException extends CascadingException {

    public NoSuchSourceException( String message )
    {
        super( message );
    }

    public NoSuchSourceException( String message, Throwable source )
    {
        super( message, source );
    }
}