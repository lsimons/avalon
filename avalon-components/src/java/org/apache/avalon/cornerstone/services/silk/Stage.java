/*
 * Copyright (C) The Apache Software Foundation, All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.services.silk;

import org.apache.avalon.framework.component.Component;

/**
 * The Stage is a specialized type of Component.  It allows the system to be set
 * up in a pipeline, with asynchronously managed pipelines.  Stages are usually
 * managed by name in its container.  In the SILK architecture, the Containers
 * are connected through the SilkServer interface.
 *
 * <p>
 *   The Stage is a central concept in Matt Welsh's SEDA system.  Our Stage is
 *   identical to his in concept, but our interface does not allow for subversion
 *   of control.  A stage is assigned one or more outputs (Sources for the next
 *   stage, or specialized Source types that do not connect to anything.  A
 *   stage also has an EventHandler associated with it.
 * </p>
 * <p>
 *   The EventHandler is identified in the Stage's configuration.  The Stage
 *   Container invokes the necessary EventHandler for the Stage.
 * </p>
 * <p class="note">
 *   TBD: How do we want the EventHandler associated with the stage?  Should the
 *   stage implement EventHandler, should the configuration file manage the class
 *   to be invoked, or should there be a <code>getHandler</code> method?
 * </p>
 *
 * @author <a href="bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/02/01 20:06:24 $
 */

public interface Stage extends Component {
    /**
     * Sets the SourceMap for the Stage.  This allows the stage to specify
     * exactly what Source we are feeding with events.  The SourceMap is set
     * once, and only once during the life of the stage.  Stages are meant to
     * be simple components, and the SourceMap is the last method to be called
     * during initialization.
     */
    void setSourceMap( SourceMap map );
}