/*
 * Copyright (C) The Apache Software Foundation, All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.services.silk;

import org.apache.avalon.excalibur.event.Sink;

/**
 * The SinkMap is an abstraction to allow the container to centrally manage all
 * connections within its scope.  This allows the system to remain using Inversion
 * of Control, while allowing a system to be reassigned at any time.
 *
 * @author <a href="bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/02/04 18:04:30 $
 */

public interface SinkMap {

    /**
     * This gets the main Sink for the SinkMap.  A SinkMap must have at
     * least one Source.
     *
     * @return the main <code>Sink</code> for the SourceMap.
     */
    Sink getMainSink();

    /**
     * Get the named Source.  If the SourceMap does not contain a match for the
     * name, the method throws an exception.
     *
     * @param  name  The name of the desired source
     *
     * @return Sink the source associated with the name
     * @throws NoSuchSinkException if no source is associated with the name
     */
    Sink getSource( String name )
        throws NoSuchSinkException;

    /**
     * The SourceArray allows the Stage to get an array of all Sources attached
     * to the stage.
     *
     * @return an array of <code>Sink</code> objects.  There is at least one
     *         Sink in the array.
     */
    Sink[] getSinkArray();
}