/*
 * Copyright (C) The Apache Software Foundation, All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.services.silk;

import org.apache.avalon.excalibur.event.Source;

/**
 * The SourceMap is an abstraction to allow the container to centrally manage all
 * connections within its scope.  This allows the system to remain using Inversion
 * of Control, while allowing a system to be reassigned at any time.
 *
 * @author <a href="bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/02/01 20:06:24 $
 */

public interface SourceMap {

    /**
     * This gets the main Source for the SourceMap.  A SourceMap must have at
     * least one Source.
     *
     * @return the main <code>Source</code> for the SourceMap.
     */
    Source getMainSource();

    /**
     * Get the named Source.  If the SourceMap does not contain a match for the
     * name, the method throws an exception.
     *
     * @param  name  The name of the desired source
     *
     * @return Source the source associated with the name
     * @throws NoSuchSourceException if no source is associated with the name
     */
    Source getSource( String name )
        throws NoSuchSourceException;

    /**
     * The SourceArray allows the Stage to get an array of all Sources attached
     * to the stage.
     *
     * @return an array of <code>Source</code> objects.  There is at least one
     *         Source in the array.
     */
    Source[] getSourceArray();
}