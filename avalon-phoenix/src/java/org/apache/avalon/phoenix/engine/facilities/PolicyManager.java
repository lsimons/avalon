/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.engine.facilities;

import java.security.Policy;
import org.apache.avalon.framework.atlantis.Facility;

/**
 * This facility manages the policy for an application instance.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface PolicyManager
    extends Facility
{
    /**
     * Get policy for the current application.
     *
     * @return the Policy
     */
    Policy getPolicy();
}
