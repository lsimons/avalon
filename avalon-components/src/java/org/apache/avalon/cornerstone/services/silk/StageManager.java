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
 * The SilkServer provides the mechanism to join seemingly separated blocks
 * into one large Staged Event Driven Architecture server.  The word "Seda"
 * means silk in Spanish.  I added a play on that definition for the work Silk.
 * It is Staged Interblock Linking Kernel.
 *
 * <p>
 *   The concepts listed in this specification are heavily influenced by Matt
 *   Welsh's SEDA architecture.  It has been tested to be insanely scalable,
 *   to the point where a Java based server can service more clients than its
 *   C based counterpart.
 * </p>
 *
 * @author <a href="bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/02/01 20:08:53 $
 */

public interface StageManager {
    String ROLE = "org.apache.avalon.cornerstone.services.silk.SilkServer";

    /**
     * Gets the source for a specified stage name.
     */
    SourceMap getSourceMap( String stageName );
}
