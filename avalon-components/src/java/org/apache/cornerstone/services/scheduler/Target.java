/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.services.scheduler;

import org.apache.avalon.configuration.Configuration;

/**
 * This is the interface to implement to receive notification trigger.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public interface Target
{
    /**
     * Notify target that trigger has occured.
     *
     * @param triggerName the name of trigger
     */
    void targetTriggered( String triggerName );
}
