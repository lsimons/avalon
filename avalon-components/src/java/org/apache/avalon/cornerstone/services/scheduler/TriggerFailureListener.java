/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.services.scheduler;

/**
 * A callback mechanism for failures on triggering of targets.
 *
 * @author Paul Hammant
 */
public interface TriggerFailureListener
{
    void triggerFailure(Throwable throwable);
}
